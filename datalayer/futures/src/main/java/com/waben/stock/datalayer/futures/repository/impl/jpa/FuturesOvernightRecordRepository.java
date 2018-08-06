package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

/**
 * 期货订单隔夜记录 Reopsitory
 * 
 * @author sunl
 *
 */
public interface FuturesOvernightRecordRepository extends CustomJpaRepository<FuturesOvernightRecord, Long> {

	List<FuturesOvernightRecord> findByOrder(FuturesContractOrder order, Sort sort);

	@Query("select SUM(f.overnightDeferredFee) from FuturesOvernightRecord f where f.order.id = ?1")
	BigDecimal getSUMOvernightRecord(Long orderId);

}
