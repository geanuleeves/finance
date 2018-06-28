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

	/**
	 * 每小时组合一天的小时K，计算天K
	 */
	@Scheduled(cron = "0 20 0/1 * * ?")
	public void computeDayK() {
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
			cal.add(Calendar.DAY_OF_MONTH, -1);
			Date yesterday = cal.getTime();
			// step 3 : 计算上一天的日K
			innerComputeDayK(commodityNo, contractNo, today);
			// step 4 : 计算今天的日K
			innerComputeDayK(commodityNo, contractNo, yesterday);
		}
		logger.info("计算日K数据结束:" + fullSdf.format(new Date()));
	}

	private void innerComputeDayK(String commodityNo, String contractNo, Date date) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date[] arr = retriveBeijingTimeInterval(date);
		List<FuturesQuoteMinuteKGroup> groupList = minuteKGroupServcie
				.getByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo, arr[0],
						arr[1]);
		if (groupList != null && groupList.size() > 0) {
			FuturesQuoteDayK dayK = dayKServcie.getByCommodityNoAndContractNoAndTime(commodityNo, contractNo, date);
			if (dayK == null) {
				dayK = new FuturesQuoteDayK();
			}
			// 初始化部分数据
			dayK.setCommodityNo(commodityNo);
			dayK.setContractNo(contractNo);
			dayK.setTime(date);
			dayK.setTimeStr(fullSdf.format(date));
			dayK.setTotalVolume(groupList.get(groupList.size() - 1).getTotalVolume());
			Long startTotalQty = groupList.get(0).getStartTotalQty();
			Long endTotalQty = groupList.get(groupList.size() - 1).getEndTotalQty();
			dayK.setEndTotalQty(endTotalQty);
			dayK.setStartTotalQty(startTotalQty);
			if(endTotalQty != null && startTotalQty != null) {
				dayK.setVolume(endTotalQty - startTotalQty);
			} else {
				dayK.setVolume(0L);
			}
			dayK.setOpenPrice(groupList.get(0).getOpenPrice());
			dayK.setClosePrice(groupList.get(groupList.size() - 1).getClosePrice());
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
			dayKServcie.addFuturesQuoteDayK(dayK);

		}
	}

	private Date[] retriveBeijingTimeInterval(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, 5);
		cal.add(Calendar.MINUTE, 1);
		Date endTime = cal.getTime();

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(date);
		startCal.add(Calendar.HOUR_OF_DAY, -18);
		startCal.add(Calendar.MINUTE, 1);
		Date startTime = startCal.getTime();

		return new Date[] { startTime, endTime };
	}

}
