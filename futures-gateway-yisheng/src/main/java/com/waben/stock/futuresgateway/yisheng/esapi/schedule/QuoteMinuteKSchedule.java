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

import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqProducer;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.DeleteQuoteMessage;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteLastService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteMinuteKService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteService;
import com.waben.stock.futuresgateway.yisheng.util.CopyBeanUtils;

/**
 * 行情分钟作业
 * 
 * @author luomengan
 *
 */
@Component
@EnableScheduling
public class QuoteMinuteKSchedule {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private FuturesQuoteService quoteService;

	@Autowired
	private FuturesQuoteLastService quoteLastService;

	@Autowired
	private FuturesQuoteMinuteKService minuteKServcie;

	@Autowired
	private RabbitmqProducer producer;

	/**
	 * 每分钟计算上一分钟的分钟K
	 */
	@Scheduled(cron = "20 0/1 * * * ?")
	public void computeMinuteK() {
		SimpleDateFormat minSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// step 1 : 获取可用的合约
		List<FuturesContract> contractList = contractService.getByEnable(true);
		// step 2 : 获取上一分钟
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, -1);
		Date before = cal.getTime();
		// step 3 : 遍历所有合约，计算分钟K
		for (FuturesContract contract : contractList) {
			String commodityNo = contract.getCommodityNo();
			String contractNo = contract.getContractNo();
			// step 3.1 : 判断之前是否有计算过
			FuturesQuoteMinuteK beforeMinuteK = minuteKServcie.getByCommodityNoAndContractNoAndTime(commodityNo,
					contractNo, before);
			if (beforeMinuteK != null) {
				continue;
			}
			// step 3.2 : 根据时间获取所有的行情
			List<FuturesQuote> quoteList = quoteService.getByCommodityNoAndContractNoAndDateTimeStampLike(commodityNo,
					contractNo, minSdf.format(before) + "%");
			if (quoteList != null && quoteList.size() > 0) {
				// step 3.3 : 初始化部分数据
				beforeMinuteK = new FuturesQuoteMinuteK();
				beforeMinuteK.setCommodityNo(commodityNo);
				beforeMinuteK.setContractNo(contractNo);
				beforeMinuteK.setTime(before);
				beforeMinuteK.setTimeStr(fullSdf.format(before));
				beforeMinuteK.setTotalVolume(quoteList.get(quoteList.size() - 1).getTotalQty());
				beforeMinuteK
						.setVolume(quoteList.get(quoteList.size() - 1).getTotalQty() - quoteList.get(0).getTotalQty());
				beforeMinuteK.setOpenPrice(quoteList.get(0).getOpeningPrice());
				beforeMinuteK.setClosePrice(quoteList.get(quoteList.size() - 1).getClosingPrice());
				// step 3.4 : 计算最高价、最低价
				BigDecimal highPrice = quoteList.get(0).getHighPrice();
				BigDecimal lowPrice = quoteList.get(0).getLowPrice();
				for (FuturesQuote quote : quoteList) {
					if (quote.getHighPrice().compareTo(highPrice) > 0) {
						highPrice = quote.getHighPrice();
					}
					if (quote.getLowPrice().compareTo(lowPrice) < 0) {
						lowPrice = quote.getLowPrice();
					}
				}
				beforeMinuteK.setHighPrice(highPrice);
				beforeMinuteK.setLowPrice(lowPrice);
				// step 3.5 : 保存计算出来的分K数据
				minuteKServcie.addFuturesQuoteMinuteK(beforeMinuteK);
				// step 3.6 : 删除该分钟的行情数据
				for (int i = 0; i < quoteList.size(); i++) {
					FuturesQuote quote = quoteList.get(i);
					DeleteQuoteMessage delQuote = new DeleteQuoteMessage();
					delQuote.setQuoteId(quote.getId());
					delQuote.setType(1);
					producer.sendMessage(RabbitmqConfiguration.deleteQuoteQueueName, delQuote);
					if (i == quoteList.size() - 1) {
						// 判断当前分钟有没有行情数据，如果没有的话，将这条数据保存到FuturesQuoteLast中
						cal.add(Calendar.MINUTE, 1);
						Date currentMin = cal.getTime();
						Long count = quoteService.countByTimeGreaterThanEqual(currentMin);
						if (count <= 0) {
							FuturesQuoteLast quoteLast = CopyBeanUtils.copyBeanProperties(FuturesQuoteLast.class, quote,
									false);
							quoteLast.setId(null);
							quoteLastService.addFuturesQuoteLast(quoteLast);
						}
					}
				}
			}
		}
		logger.info("计算分K数据结束:" + fullSdf.format(new Date()));
	}

	@SuppressWarnings("unused")
	private boolean isExchangeSameDay(Date d1, Date d2, int timeZoneGap) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(d1);
		c1.add(Calendar.HOUR_OF_DAY, timeZoneGap * -1);
		Date exchangeD1 = c1.getTime();

		Calendar c2 = Calendar.getInstance();
		c2.setTime(d2);
		c2.add(Calendar.HOUR_OF_DAY, timeZoneGap * -1);
		Date exchangeD2 = c2.getTime();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(exchangeD1).equals(sdf.format(exchangeD2));
	}

}
