package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;

/**
 * 期货订单隔夜记录 Reopsitory
 * 
 * @author sunl
 *
 */
public interface FuturesOvernightRecordRepository extends CustomJpaRepository<FuturesOvernightRecord, Long> {

	List<FuturesOvernightRecord> findByOrder(FuturesOrder order, Sort sort);

	@Query("select SUM(f.overnightDeferredFee) from FuturesOvernightRecord f where f.order.id = ?1")
	BigDecimal getSUMOvernightRecord(Long orderId);

}
