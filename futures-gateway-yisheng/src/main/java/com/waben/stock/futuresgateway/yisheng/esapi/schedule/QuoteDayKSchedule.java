package com.waben.stock.futuresgateway.yisheng.esapi.schedule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;
import com.waben.stock.futuresgateway.yisheng.service.FuturesCommodityService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteDayKService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteMinuteKGroupService;
import com.waben.stock.futuresgateway.yisheng.util.TimeZoneUtil;

/**
 * 行情-日K组合作业
 * 
 * @author luomengan
 *
 */
@Component
@EnableScheduling
public class QuoteDayKSchedule {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesCommodityService commodityService;

	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private FuturesQuoteDayKService dayKServcie;

	@Autowired
	private FuturesQuoteMinuteKGroupService minuteKGroupServcie;

	/** 在明天的收盘~开盘的中间时间重置为1 */
	private long quoteIndex = 1;

	/**
	 * 每小时组合一天的小时K，计算天K
	 */
	@Scheduled(cron = "0 20 0/1 * * ?")
	public void computeDayK() {
		// 此处为额外计算的内容
		computeQuoteIndex();

		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// step 1 : 获取可用的合约
		List<FuturesContract> contractList = contractService.getByEnable(true);
		for (FuturesContract contract : contractList) {
			String commodityNo = contract.getCommodityNo();
			String contractNo = contract.getContractNo();
			// step 2 : 获取北京时间对应的交易所时间，当天和前一天
			FuturesCommodity commodity = commodityService.getByCommodityNo(commodityNo);
			if (commodity == null || commodity.getTimeZoneGap() == null) {
				continue;
			}
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Date today = cal.getTime();
			// step 3 : 计算上一天的日K
			try {
				Calendar now = Calendar.getInstance();
				if (now.get(Calendar.HOUR_OF_DAY) < TimeZoneUtil.getCloseTimeHour()) {
					continue;
				}
				innerComputeDayK(commodityNo, contractNo, today);
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("计算日K数据异常:{}_{}_{}", commodityNo, contractNo, fullSdf.format(today));
			}
		}
		logger.info("计算日K数据结束:" + fullSdf.format(new Date()));
	}

	private void computeQuoteIndex() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, TimeZoneUtil.getCloseTimeHour());
		cal.set(Calendar.MINUTE, 10);
		Date startTime = cal.getTime();
		cal.set(Calendar.MINUTE, 50);
		Date endTime = cal.getTime();

		Date now = new Date();
		if (now.getTime() > startTime.getTime() && now.getTime() < endTime.getTime() && quoteIndex > 10000) {
			quoteIndex = 1;
		}
	}

	private void innerComputeDayK(String commodityNo, String contractNo, Date date) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date[] arr = TimeZoneUtil.retriveBeijingTimeInterval(date);
		List<FuturesQuoteMinuteKGroup> groupList = minuteKGroupServcie
				.getByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo, arr[0],
						arr[1]);
		if (groupList != null && groupList.size() > 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date yesterday = cal.getTime();
			FuturesQuoteDayK dayK = dayKServcie.getByCommodityNoAndContractNoAndTime(commodityNo, contractNo,
					yesterday);
			if (dayK == null) {
				dayK = new FuturesQuoteDayK();
			}
			// 初始化部分数据
			dayK.setCommodityNo(commodityNo);
			dayK.setContractNo(contractNo);
			dayK.setTime(yesterday);
			dayK.setTimeStr(fullSdf.format(yesterday));
			dayK.setTotalVolume(groupList.get(groupList.size() - 1).getTotalVolume());
			Long startTotalQty = groupList.get(0).getStartTotalQty();
			Long endTotalQty = groupList.get(groupList.size() - 1).getEndTotalQty();
			dayK.setEndTotalQty(endTotalQty);
			dayK.setStartTotalQty(startTotalQty);
			if (endTotalQty != null) {
				dayK.setVolume(endTotalQty);
			} else {
				dayK.setVolume(0L);
			}
			BigDecimal openPrice = groupList.get(0).getOpenPrice();
			dayK.setOpenPrice(openPrice);
			BigDecimal closePrice = groupList.get(groupList.size() - 1).getClosePrice();
			dayK.setClosePrice(closePrice);
			// 计算最高价、最低价
			BigDecimal highPrice = groupList.get(0).getHighPrice();
			BigDecimal lowPrice = groupList.get(0).getLowPrice();
			for (FuturesQuoteMinuteKGroup group : groupList) {
				if (group.getHighPrice().compareTo(highPrice) > 0) {
					highPrice = group.getHighPrice();
				}
				if (group.getLowPrice().compareTo(lowPrice) < 0) {
					lowPrice = group.getLowPrice();
				}
			}
			dayK.setHighPrice(highPrice);
			dayK.setLowPrice(lowPrice);
			// 保存计算出来的日K数据
			if (openPrice != null && openPrice.compareTo(BigDecimal.ZERO) > 0 && closePrice != null
					&& closePrice.compareTo(BigDecimal.ZERO) > 0 && highPrice != null
					&& highPrice.compareTo(BigDecimal.ZERO) > 0 && lowPrice != null
					&& lowPrice.compareTo(BigDecimal.ZERO) > 0) {
				dayKServcie.addFuturesQuoteDayK(dayK);
			}
		}
	}

	public synchronized long getQuoteIndex() {
		return quoteIndex;
	}

	public void setQuoteIndex(long quoteIndex) {
		this.quoteIndex = quoteIndex;
	}

	public synchronized void increaseQuoteIndex() {
		this.quoteIndex += 1;
	}

}
