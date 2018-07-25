package com.waben.stock.datalayer.publisher.repository;

import com.waben.stock.datalayer.publisher.entity.FrozenCapital;

/**
 * 冻结资金 Dao
 * 
 * @author lma
 *
 */
public interface FrozenCapitalDao extends BaseDao<FrozenCapital, Long> {

	FrozenCapital retriveByPublisherIdAndBuyRecordId(Long publisherId, Long buyRecordId);

	FrozenCapital retriveByPublisherIdAndWithdrawalsNo(Long publisherId, String withdrawalsNo);

	FrozenCapital retriveByPublisherIdAndFuturesOrderId(Long publisherId, Long futuresOrderId);

	FrozenCapital retriveByPublisherIdAndFuturesOvernightId(Long publisherId, Long futuresOvernightId);

}
