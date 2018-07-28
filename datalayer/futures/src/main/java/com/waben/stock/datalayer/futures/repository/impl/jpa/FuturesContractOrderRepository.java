package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;

/**
 * 合约订单
 * 
 * @author chenk 2018/7/26.
 */
public interface FuturesContractOrderRepository extends CustomJpaRepository<FuturesContractOrder, Long> {

	FuturesContractOrder findByContractAndPublisherId(FuturesContract contract, Long publisherId);

}
