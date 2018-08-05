package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;

/**
 * 合约订单
 * 
 * @author chenk 2018/7/26.
 */
public interface FuturesContractOrderDao extends BaseDao<FuturesContractOrder, Long> {

	FuturesContractOrder retrieveByContractAndPublisherId(FuturesContract contract, Long publisherId);

	List<FuturesContractOrder> retrieveByPublisherId(Long publisherId);

	List<FuturesContractOrder> retrivePositionContractOrders();

	FuturesContractOrder doUpdate(FuturesContractOrder contractOrder);

	List<FuturesContractOrder> retrivePublisherPositionContractOrders(Long publisherId);

}
