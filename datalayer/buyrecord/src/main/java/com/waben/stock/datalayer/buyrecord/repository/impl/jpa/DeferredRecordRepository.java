package com.waben.stock.datalayer.buyrecord.repository.impl.jpa;

import java.util.Date;
import java.util.List;

import com.waben.stock.datalayer.buyrecord.entity.DeferredRecord;

/**
 * 递延记录 Jpa
 * 
 * @author luomengan
 *
 */
public interface DeferredRecordRepository extends CustomJpaRepository<DeferredRecord, Long> {

	List<DeferredRecord> findByPublisherIdAndBuyRecordId(Long publisherId, Long buyRecordId);

	List<DeferredRecord> findByPublisherIdAndBuyRecordIdAndDeferredTime(Long publisherId, Long buyRecordId,
			Date deferredTime);

}
