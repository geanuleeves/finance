package com.waben.stock.datalayer.publisher.repository.impl.jpa;

import com.waben.stock.datalayer.publisher.entity.FrozenCapital;

/**
 * 冻结资金 Jpa
 * 
 * @author lma
 *
 */
public interface FrozenCapitalRepository extends CustomJpaRepository<FrozenCapital, Long> {

	FrozenCapital findByPublisherIdAndBuyRecordId(Long publisherId, Long buyRecordId);

	FrozenCapital findByPublisherIdAndWithdrawalsNo(Long publisherId, String withdrawalsNo);
	
	FrozenCapital findByPublisherIdAndFuturesOrderId(Long publisherId, Long futuresOrderId);

	FrozenCapital findByPublisherIdAndFuturesOvernightId(Long publisherId, Long futuresOvernightId);

}
