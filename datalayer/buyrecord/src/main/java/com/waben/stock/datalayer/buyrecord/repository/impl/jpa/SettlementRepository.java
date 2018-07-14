package com.waben.stock.datalayer.buyrecord.repository.impl.jpa;

import java.util.List;

import com.waben.stock.datalayer.buyrecord.entity.BuyRecord;
import com.waben.stock.datalayer.buyrecord.entity.Settlement;

/**
 * 结算 Jpa
 * 
 * @author luomengan
 *
 */
public interface SettlementRepository extends CustomJpaRepository<Settlement, Long> {
	
    Settlement findByBuyRecordId(Long id);

	List<Settlement> findByBuyRecord(BuyRecord buyRecord);
	
}
