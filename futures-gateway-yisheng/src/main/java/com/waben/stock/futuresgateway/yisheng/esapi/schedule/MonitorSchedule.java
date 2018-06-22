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
	 * 5点55的时候重新连接行情api和交易api
	 */
	@Scheduled(cron = "0 55 5 * * ?")
	public void monitor() {
		try {
			quoteWrapper.reconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			tradeWrapper.reconnect();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
