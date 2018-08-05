package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.repository.FuturesOvernightRecordDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * 期货订单过夜记录 service
 * 
 * @author sunl
 *
 */
@Service
public class FuturesOvernightRecordService {

	@Autowired
	private FuturesOvernightRecordDao recordDao;

	public FuturesOvernightRecord findNewestOvernightRecord(FuturesContractOrder order) {
		List<FuturesOvernightRecord> list = recordDao.retrieveByOrder(order);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<FuturesOvernightRecord> findAll(FuturesContractOrder order) {
		return recordDao.retrieveByOrder(order);
	}

	public BigDecimal getSUMOvernightRecord(Long orderId) {
		return recordDao.getSUMOvernightRecord(orderId);
	}

	public FuturesOvernightRecord retrieve(Long id) {
		return recordDao.retrieve(id);
	}
}
