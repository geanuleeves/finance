package com.waben.stock.datalayer.futures.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesCommodityRepository;

@Repository
public class FuturesCommodityDaoImpl implements FuturesCommodityDao {

	@Autowired
	private FuturesCommodityRepository repository;

	@Override
	public FuturesCommodity create(FuturesCommodity t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesCommodity update(FuturesCommodity t) {
		return repository.save(t);
	}

	@Override
	public FuturesCommodity retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesCommodity> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesCommodity> page(Specification<FuturesCommodity> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesCommodity> list() {
		return repository.findAll();
	}

	@Override
	public List<FuturesCommodity> retrieveByExchange(FuturesExchange exchange) {
		return repository.findByExchange(exchange);
	}

	@Override
	public FuturesCommodity retrieveByCommodityNo(String commodityNo) {
		return repository.findBySymbol(commodityNo);
	}

}
