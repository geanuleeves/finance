package com.waben.stock.datalayer.futures.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.futures.entity.FuturesStopLossOrProfit;
import com.waben.stock.datalayer.futures.repository.FuturesStopLossOrProfitDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesStopLossOrProfitRepostiory;

@Repository
public class FuturesStopLossOrProfitDaoImpl implements FuturesStopLossOrProfitDao {

	@Autowired
	private FuturesStopLossOrProfitRepostiory repostiory;

	@Override
	public FuturesStopLossOrProfit create(FuturesStopLossOrProfit t) {
		return repostiory.save(t);
	}

	@Override
	public void delete(Long id) {
		repostiory.delete(id);
	}

	@Override
	public FuturesStopLossOrProfit update(FuturesStopLossOrProfit t) {
		return repostiory.save(t);
	}

	@Override
	public FuturesStopLossOrProfit retrieve(Long id) {
		return repostiory.findById(id);
	}

	@Override
	public Page<FuturesStopLossOrProfit> page(int page, int limit) {
		return repostiory.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesStopLossOrProfit> page(Specification<FuturesStopLossOrProfit> specification, Pageable pageable) {
		return repostiory.findAll(specification, pageable);
	}

	@Override
	public List<FuturesStopLossOrProfit> list() {
		return repostiory.findAll();
	}

	@Override
	public List<FuturesStopLossOrProfit> findByCommodityId(Long commodityId) {
		return repostiory.findByCommodityId(commodityId);
	}

}
