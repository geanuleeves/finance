package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;

/**
 * 期货交易所 Dao
 * 
 * @author sunl
 *
 */
public interface FuturesExchangeDao extends BaseDao<FuturesExchange, Long> {

	List<FuturesContract> findByExchangId(Long exchangeId);
}
