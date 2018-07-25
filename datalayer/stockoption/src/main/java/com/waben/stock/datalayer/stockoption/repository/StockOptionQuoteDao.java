package com.waben.stock.datalayer.stockoption.repository;

import com.waben.stock.datalayer.stockoption.entity.StockOptionQuote;

/**
 * 期权报价 Dao
 * 
 * @author lma
 *
 */
public interface StockOptionQuoteDao extends BaseDao<StockOptionQuote, Long> {

	StockOptionQuote retrieveByStockCodeAndCycle(String stockCode, Integer cycle);

}
