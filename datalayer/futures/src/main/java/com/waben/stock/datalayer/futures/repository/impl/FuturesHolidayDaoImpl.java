package com.waben.stock.datalayer.futures.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;
import com.waben.stock.datalayer.futures.repository.FuturesHolidayDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesHolidayRepository;

/**
 * 节假日管理
 * 
 * @author pengzhenliang
 *
 */
@Repository
public class FuturesHolidayDaoImpl implements FuturesHolidayDao {

	@Autowired
	private FuturesHolidayRepository repository;

	@Override
	public FuturesHoliday create(FuturesHoliday t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesHoliday update(FuturesHoliday t) {
		return repository.save(t);
	}

	@Override
	public FuturesHoliday retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesHoliday> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesHoliday> page(Specification<FuturesHoliday> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesHoliday> list() {
		return repository.findAll();
	}

	@Override
	public List<FuturesHoliday> findByCommodity(FuturesCommodity commodity) {
		return repository.findByCommodity(commodity);
	}

	@Override
	public List<FuturesHoliday> findByCommodityId(Long commodityId) {
		return repository.findByCommodityId(commodityId);
	}

}
