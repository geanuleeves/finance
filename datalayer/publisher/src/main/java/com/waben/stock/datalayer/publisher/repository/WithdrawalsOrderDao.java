package com.waben.stock.datalayer.publisher.repository;

import com.waben.stock.datalayer.publisher.entity.WithdrawalsOrder;

/**
 * 提现订单 Dao
 * 
 * @author lma
 *
 */
public interface WithdrawalsOrderDao extends BaseDao<WithdrawalsOrder, Long> {

	WithdrawalsOrder retrieveByWithdrawalsNo(String withdrawalsNo);
	
}
