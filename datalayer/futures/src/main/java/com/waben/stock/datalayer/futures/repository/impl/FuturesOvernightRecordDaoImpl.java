package com.waben.stock.datalayer.futures.repository.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.repository.FuturesOvernightRecordDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesOvernightRecordRepository;

/**
 * 期货订单过夜记录 Impl
 * 
 * @author sunl
 *
 */
@Repository
public class FuturesOvernightRecordDaoImpl implements FuturesOvernightRecordDao {

	@Autowired
	private FuturesOvernightRecordRepository repository;

	@Override
	public FuturesOvernightRecord create(FuturesOvernightRecord t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesOvernightRecord update(FuturesOvernightRecord t) {
		return repository.save(t);
	}

	@Override
	public FuturesOvernightRecord retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesOvernightRecord> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesOvernightRecord> page(Specification<FuturesOvernightRecord> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesOvernightRecord> list() {
		return repository.findAll();
	}

	@Override
	public List<FuturesOvernightRecord> retrieveByOrder(FuturesOrder order) {
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "deferredTime"));
		return repository.findByOrder(order, sort);
	}

	@Override
	public BigDecimal getSUMOvernightRecord(Long orderId) {
		return repository.getSUMOvernightRecord(orderId);
	}

}
