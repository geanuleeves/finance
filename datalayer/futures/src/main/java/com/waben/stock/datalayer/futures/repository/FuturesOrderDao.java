package com.waben.stock.datalayer.futures.repository;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.interfaces.enums.FuturesOrderState;

/**
 * 期货订单 Dao
 * 
 * @author sunl
 *
 */
public interface FuturesOrderDao extends BaseDao<FuturesOrder, Long> {

	FuturesOrder editOrder(Long id, FuturesOrderState state);
}
