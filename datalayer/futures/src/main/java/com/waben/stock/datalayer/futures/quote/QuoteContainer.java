package com.waben.stock.datalayer.futures.quote;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.datalayer.futures.quote.protobuf.FuturesQuoteFullData.FuturesQuoteFullDataBase;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;

/**
 * 行情
 * 
 * @author luomengan
 *
 */
@Component
public class QuoteContainer {

	/** 行情缓存 */
	private Map<String, FuturesContractMarket> quoteCache = new ConcurrentHashMap<>();

	@Autowired
	private ProfileBusiness profileBusiness;

	@PostConstruct
	public void initTask() {
		Map<String, FuturesContractMarket> map = RetriveFuturesOverHttp.marketAll(profileBusiness.isProd());
		quoteCache.putAll(map);
	}

	public BigDecimal getLastPrice(String commodityNo, String contractNo) {
		FuturesContractMarket market = quoteCache.get(getQuoteCacheKey(commodityNo, contractNo));
		if (market != null) {
			return market.getLastPrice();
		}
		return null;
	}

	public FuturesContractMarket getQuote(String commodityNo, String contractNo) {
		return quoteCache.get(getQuoteCacheKey(commodityNo, contractNo));
	}

	public String getQuoteCacheKey(String commodityNo, String contractNo) {
		return commodityNo + "-" + contractNo;
	}

	public Map<String, FuturesContractMarket> getQuoteCache() {
		return quoteCache;
	}

	public void pushQuote(FuturesQuoteFullDataBase quote) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String commodityNo = quote.getCommodityNo();
		String contractNo = quote.getContractNo();
		FuturesContractMarket market = new FuturesContractMarket();
		market.setCommodityNo(commodityNo);
		market.setContractNo(contractNo);
		market.setClosePrice(new BigDecimal(quote.getClosePrice()));
		market.setHighPrice(new BigDecimal(quote.getHighPrice()));
		market.setLastPrice(new BigDecimal(quote.getLastPrice()));
		market.setLastSize(quote.getLastSize());
		market.setLowPrice(new BigDecimal(quote.getLowPrice()));
		market.setOpenPrice(new BigDecimal(quote.getOpenPrice()));
		market.setNowClosePrice(new BigDecimal(quote.getNowClosePrice()));
		market.setVolume(quote.getVolume());
		market.setTotalVolume(quote.getTotalVolume());
		try {
			market.setTime(sdf.parse(quote.getTime()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		market.setAskPrice(new BigDecimal(quote.getAskPrice()));
		market.setAskPrice2(new BigDecimal(quote.getAskPrice2()));
		market.setAskPrice3(new BigDecimal(quote.getAskPrice3()));
		market.setAskPrice4(new BigDecimal(quote.getAskPrice4()));
		market.setAskPrice5(new BigDecimal(quote.getAskPrice5()));
		market.setAskPrice6(new BigDecimal(quote.getAskPrice6()));
		market.setAskPrice7(new BigDecimal(quote.getAskPrice7()));
		market.setAskPrice8(new BigDecimal(quote.getAskPrice8()));
		market.setAskPrice9(new BigDecimal(quote.getAskPrice9()));
		market.setAskPrice10(new BigDecimal(quote.getAskPrice10()));
		market.setAskSize(quote.getAskSize());
		market.setAskSize2(quote.getAskSize());
		market.setAskSize3(quote.getAskSize());
		market.setAskSize4(quote.getAskSize());
		market.setAskSize5(quote.getAskSize());
		market.setAskSize6(quote.getAskSize());
		market.setAskSize7(quote.getAskSize());
		market.setAskSize8(quote.getAskSize());
		market.setAskSize9(quote.getAskSize());
		market.setAskSize10(quote.getAskSize());
		market.setBidPrice(new BigDecimal(quote.getBidPrice()));
		market.setBidPrice2(new BigDecimal(quote.getBidPrice2()));
		market.setBidPrice3(new BigDecimal(quote.getBidPrice3()));
		market.setBidPrice4(new BigDecimal(quote.getBidPrice4()));
		market.setBidPrice5(new BigDecimal(quote.getBidPrice5()));
		market.setBidPrice6(new BigDecimal(quote.getBidPrice6()));
		market.setBidPrice7(new BigDecimal(quote.getBidPrice7()));
		market.setBidPrice8(new BigDecimal(quote.getBidPrice8()));
		market.setBidPrice9(new BigDecimal(quote.getBidPrice9()));
		market.setBidPrice10(new BigDecimal(quote.getBidPrice10()));
		market.setBidSize(quote.getBidSize());
		market.setBidSize2(quote.getBidSize2());
		market.setBidSize3(quote.getBidSize3());
		market.setBidSize4(quote.getBidSize4());
		market.setBidSize5(quote.getBidSize5());
		market.setBidSize6(quote.getBidSize6());
		market.setBidSize7(quote.getBidSize7());
		market.setBidSize8(quote.getBidSize8());
		market.setBidSize9(quote.getBidSize9());
		market.setBidSize10(quote.getBidSize10());
		String cacheKey = getQuoteCacheKey(commodityNo, contractNo);
		this.quoteCache.put(cacheKey, market);
	}

	public void setQuoteCache(Map<String, FuturesContractMarket> quoteCache) {
		this.quoteCache = quoteCache;
	}

}
