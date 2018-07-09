package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.futures.entity.FuturesStopLossOrProfit;

/**
 * 止损止盈 Repository
 * 
 * @author Administrator
 *
 */
public interface FuturesStopLossOrProfitRepostiory extends CustomJpaRepository<FuturesStopLossOrProfit, Long> {

	@Query("select f from FuturesStopLossOrProfit f where f.commodity.id = ?1 ")
	List<FuturesStopLossOrProfit> findByCommodityId(Long commodityId);

}
