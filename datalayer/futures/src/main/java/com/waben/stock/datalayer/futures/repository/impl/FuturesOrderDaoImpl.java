package com.waben.stock.datalayer.futures.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.repository.FuturesOrderDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesOrderRepository;

import com.waben.stock.interfaces.enums.FuturesOrderState;

/**
 * 期货订单 Impl
 * 
 * @author sunl
 *
 */
@Repository
public class FuturesOrderDaoImpl implements FuturesOrderDao {

	@Autowired
	private FuturesOrderRepository futuresOrderRepository;
	
	private FuturesOrderRepository repository;

	@Override
	public FuturesOrder create(FuturesOrder t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		futuresOrderRepository.delete(id);
	}


	@Override
	public FuturesOrder update(FuturesOrder t) {
		return repository.save(t);
	}

	@Override
	public FuturesOrder retrieve(Long id) {
		return futuresOrderRepository.findById(id);
	}

	@Override
	public Page<FuturesOrder> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesOrder> page(Specification<FuturesOrder> specification, Pageable pageable) {
		return futuresOrderRepository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesOrder> list() {
		return futuresOrderRepository.findAll();
	}

//	@Override
//	public Integer countStockOptionTradeState(Long publisherId) {
//		return repository.findAll();
//	}

	@Override
	public FuturesOrder editOrder(Long id, FuturesOrderState state) {
		// TODO Auto-generated method stub
		return null;
	}

}
