package com.waben.stock.futuresgateway.yisheng.esapi.schedule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.waben.stock.futuresgateway.yisheng.esapi.EsQuoteWrapper;
import com.waben.stock.futuresgateway.yisheng.esapi.EsTradeWrapper;

/**
 * 监控API
 * 
 * @author luomengan
 *
 */
@Component
@EnableScheduling
public class MonitorSchedule {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EsQuoteWrapper quoteWrapper;

	@Autowired
	private EsTradeWrapper tradeWrapper;

	/**
	 * 每分钟计算上一分钟的分钟K
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void monitor() {
		quoteWrapper.connect();
		tradeWrapper.connect();
	}

}
