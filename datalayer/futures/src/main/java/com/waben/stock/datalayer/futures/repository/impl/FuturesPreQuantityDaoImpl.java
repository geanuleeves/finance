package com.waben.stock.datalayer.futures.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;
import com.waben.stock.datalayer.futures.repository.FuturesPreQuantityDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesPreQuantityRepository;

@Repository
public class FuturesPreQuantityDaoImpl implements FuturesPreQuantityDao {

	@Autowired
	private FuturesPreQuantityRepository repository;
	
	@Override
	public FuturesPreQuantity create(FuturesPreQuantity t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesPreQuantity update(FuturesPreQuantity t) {
		return repository.save(t);
	}

	@Override
	public FuturesPreQuantity retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesPreQuantity> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesPreQuantity> page(Specification<FuturesPreQuantity> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesPreQuantity> list() {
		return repository.findAll();
	}

	@Override
	public List<FuturesPreQuantity> findByCommodityId(Long commodityId) {
		return repository.findByCommodityId(commodityId);
	}

	@Override
	public int deleteByCommodityId(Long commodityId) {
		return repository.deleteByCommodityId(commodityId);
	}

}
