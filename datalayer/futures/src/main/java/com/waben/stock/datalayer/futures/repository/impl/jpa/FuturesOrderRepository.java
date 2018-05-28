package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;


import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.interfaces.enums.FuturesOrderType;

/**
 * 期货订单 Repository
 * 
 * @author sunl
 *
 */
public interface FuturesOrderRepository extends CustomJpaRepository<FuturesOrder, Long> {

	@Query(value = "SELECT count(*) FROM f_futures_order o LEFT JOIN f_futures_contract c ON o.contract_id = c.id  where c.id=?1 and o.order_type = ?2", nativeQuery = true)
	Integer countOrderByType(Long contractId, FuturesOrderType orderType);
}
