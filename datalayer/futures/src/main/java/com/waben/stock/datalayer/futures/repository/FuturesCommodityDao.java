package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;

/**
 * 期货品种
 * 
 * @author pzl
 *
 */
public interface FuturesCommodityDao extends BaseDao<FuturesCommodity, Long> {

	List<FuturesCommodity> retrieveByExchange(FuturesExchange exchange);

	FuturesCommodity retrieveByCommodityNo(String commodityNo);

}
