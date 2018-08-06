package com.waben.stock.datalayer.futures.repository;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;

import java.math.BigDecimal;
import java.util.List;

/**
 * 期货订单隔夜记录 Dao
 * 
 * @author sunl
 *
 */
public interface FuturesOvernightRecordDao extends BaseDao<FuturesOvernightRecord, Long> {

	List<FuturesOvernightRecord> retrieveByOrder(FuturesContractOrder order);

	BigDecimal getSUMOvernightRecord(Long orderId);

}
