package com.waben.stock.datalayer.futures.repository;

import java.math.BigDecimal;
import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;

/**
 * 期货订单隔夜记录 Dao
 * 
 * @author sunl
 *
 */
public interface FuturesOvernightRecordDao extends BaseDao<FuturesOvernightRecord, Long> {

	List<FuturesOvernightRecord> retrieveByOrder(FuturesOrder order);

	BigDecimal getSUMOvernightRecord(Long orderId);

}
