package com.waben.stock.datalayer.organization.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.organization.entity.FuturesAgentPrice;
import com.waben.stock.datalayer.organization.repository.FuturesAgentPriceDao;
import com.waben.stock.datalayer.organization.repository.impl.jpa.FuturesAgentPriceRepository;

@Repository
public class FuturesAgentPriceDaoImpl implements FuturesAgentPriceDao {

	@Autowired
	private FuturesAgentPriceRepository repository;

	@Override
	public FuturesAgentPrice create(FuturesAgentPrice t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesAgentPrice update(FuturesAgentPrice t) {
		return repository.save(t);
	}

	@Override
	public FuturesAgentPrice retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesAgentPrice> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesAgentPrice> page(Specification<FuturesAgentPrice> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesAgentPrice> list() {
		return repository.findAll();
	}

	@Override
	public FuturesAgentPrice findByCommodityIdAndOrgId(Long commodityId, Long orgId) {
		return repository.findByCommodityIdAndOrgId(commodityId, orgId);
	}

}
