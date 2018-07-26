package com.waben.stock.datalayer.futures.repository;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;

/**
 * 合约订单
 * @author chenk 2018/7/26.
 */
public interface FuturesContractOrderDao extends BaseDao<FuturesContractOrder, Long> {

	FuturesContractOrder retrieveByContract(FuturesContract contract);
	
}
