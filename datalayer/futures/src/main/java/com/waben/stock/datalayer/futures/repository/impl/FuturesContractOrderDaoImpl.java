package com.waben.stock.datalayer.futures.repository.impl;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesContractOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 合约订单
 * 
 * @author chenk 2018/7/26.
 */
@Repository
public class FuturesContractOrderDaoImpl implements FuturesContractOrderDao {

	@Autowired
	private FuturesContractOrderRepository repository;

	@Override
	public FuturesContractOrder create(FuturesContractOrder t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesContractOrder update(FuturesContractOrder t) {
		return repository.save(t);
	}

	@Override
	public FuturesContractOrder retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesContractOrder> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesContractOrder> page(Specification<FuturesContractOrder> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesContractOrder> list() {
		return repository.findAll();
	}

	@Override
	public FuturesContractOrder retrieveByContractAndPublisherId(FuturesContract contract, Long publisherId) {
		return repository.findByContractAndPublisherId(contract, publisherId);
	}

	@Override
	public List<FuturesContractOrder> retrieveByPublisherId(Long publisherId) {
		return repository.findByPublisherId(publisherId);
	}

}
