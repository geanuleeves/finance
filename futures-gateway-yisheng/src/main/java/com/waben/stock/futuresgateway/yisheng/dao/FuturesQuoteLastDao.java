package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;

/**
 * 期货合约行情-最新 Dao
 * 
 * @author lma
 *
 */
public interface FuturesQuoteLastDao {

	public FuturesQuoteLast createFuturesQuoteLast(FuturesQuoteLast futuresQuoteLast);

	public void deleteFuturesQuoteLastById(Integer id);

	public FuturesQuoteLast updateFuturesQuoteLast(FuturesQuoteLast futuresQuoteLast);

	public FuturesQuoteLast retrieveFuturesQuoteLastById(Integer id);

	public Page<FuturesQuoteLast> pageFuturesQuoteLast(int page, int limit);
	
	public List<FuturesQuoteLast> listFuturesQuoteLast();

	public FuturesQuoteLast retriveNewest(String commodityNo, String contractNo);

}
