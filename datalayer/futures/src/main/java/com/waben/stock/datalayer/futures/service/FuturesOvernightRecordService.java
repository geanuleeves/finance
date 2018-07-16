package com.waben.stock.datalayer.futures.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.repository.FuturesOvernightRecordDao;

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

	public FuturesOvernightRecord findNewestOvernightRecord(FuturesOrder order) {
		List<FuturesOvernightRecord> list = recordDao.retrieveByOrder(order);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public List<FuturesOvernightRecord> findAll(FuturesOrder order){
		return recordDao.retrieveByOrder(order);
	}
	
	public FuturesOvernightRecord retrieve(Long id){
		return recordDao.retrieve(id);
	}
}
