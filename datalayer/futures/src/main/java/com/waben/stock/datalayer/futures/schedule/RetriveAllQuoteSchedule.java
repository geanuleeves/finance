package com.waben.stock.datalayer.futures.schedule;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;

/**
 * 获取所有合约行情作业
 * 
 * @author luomengan
 *
 */
@Component
public class RetriveAllQuoteSchedule {

	Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 监控间隔
	 */
	public static final long Execute_Interval = 100;

	@Autowired
	private ProfileBusiness profileBusiness;

	/** 行情缓存 */
	private Map<String, FuturesContractMarket> quoteCache = new ConcurrentHashMap<>();

	@PostConstruct
	public void initTask() {
		Timer timer = new Timer();
		timer.schedule(new WindControlTask(), Execute_Interval);
	}

	// TODO 先使用轮询请求，后续修改为推送接收行情的方式
	private class WindControlTask extends TimerTask {
		@Override
		public void run() {
			try {
				Map<String, FuturesContractMarket> map = RetriveFuturesOverHttp.marketAll(profileBusiness.isProd());
				quoteCache.putAll(map);
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("获取所有合约行情发生异常!", ex);
			} finally {
				initTask();
			}
		}
	}
	
	public BigDecimal getLastPrice(String commodityNo, String contractNo) {
		FuturesContractMarket market = quoteCache.get(getQuoteCacheKey(commodityNo, contractNo));
		if(market != null) {
			return market.getLastPrice();
		}
		return null;
	}
	
	public String getQuoteCacheKey(String commodityNo, String contractNo) {
		return commodityNo + "-" + contractNo;
	}
	
	public Map<String, FuturesContractMarket> getQuoteCache() {
		return quoteCache;
	}

	public void setQuoteCache(Map<String, FuturesContractMarket> quoteCache) {
		this.quoteCache = quoteCache;
	}

}