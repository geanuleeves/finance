package com.waben.stock.datalayer.organization.repository.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;
import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;
import com.waben.stock.datalayer.organization.repository.FuturesCommissionAuditDao;
import com.waben.stock.datalayer.organization.repository.impl.jpa.FuturesCommissionAuditRepository;

@Repository
public class FuturesCommissionAuditDaoImpl implements FuturesCommissionAuditDao {

	@Autowired
	private FuturesCommissionAuditRepository repository;

	@Override
	public FuturesCommissionAudit create(FuturesCommissionAudit t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesCommissionAudit update(FuturesCommissionAudit t) {
		return repository.save(t);
	}

	@Override
	public FuturesCommissionAudit retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesCommissionAudit> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesCommissionAudit> page(Specification<FuturesCommissionAudit> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesCommissionAudit> list() {
		return repository.findAll();
	}

	@Override
	public Integer countCommissionAudit() {
		return repository.countCommissionAudit();
	}

	@Override
	public BigDecimal realMaidFee() {
		return repository.realMaidFee();
	}

	@Override
	public FuturesCommissionAudit findByflowId(Long flowId) {
		return repository.findByflowId(flowId);
	}

}
