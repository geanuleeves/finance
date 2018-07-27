package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;

/**
 * 期货合约行情 Dao
 * 
 * @author lma
 *
 */
public interface FuturesQuoteDao {

	public FuturesQuote createFuturesQuote(FuturesQuote futuresQuote);

	public void deleteFuturesQuoteById(String commodityNo, String contractNo, String id);

	public FuturesQuote retrieveFuturesQuoteById(String commodityNo, String contractNo, String id);

	public Page<FuturesQuote> pageFuturesQuote(String commodityNo, String contractNo, int page, int limit);

	public List<FuturesQuote> listFuturesQuote(String commodityNo, String contractNo);

	public List<FuturesQuote> retrieveByCommodityNoAndContractNoAndDateTimeStampLike(String commodityNo,
			String contractNo, String dateTimeStamp);

	public Long countByTimeGreaterThanEqual(String commodityNo, String contractNo, Date time);

	public FuturesQuote retriveNewest(String commodityNo, String contractNo);

}
