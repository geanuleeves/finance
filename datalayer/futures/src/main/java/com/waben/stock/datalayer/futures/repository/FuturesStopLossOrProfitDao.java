package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesStopLossOrProfit;

/**
 * 设置止损止盈
 * 
 * @author sl
 *
 */
public interface FuturesStopLossOrProfitDao extends BaseDao<FuturesStopLossOrProfit, Long> {

	List<FuturesStopLossOrProfit> findByCommodityId(Long commodityId);
}
