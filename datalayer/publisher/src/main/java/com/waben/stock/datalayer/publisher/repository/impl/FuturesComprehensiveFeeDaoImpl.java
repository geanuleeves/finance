package com.waben.stock.datalayer.publisher.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.publisher.entity.FuturesComprehensiveFee;
import com.waben.stock.datalayer.publisher.repository.FuturesComprehensiveFeeDao;
import com.waben.stock.datalayer.publisher.repository.impl.jpa.FuturesComprehensiveFeeRepository;

@Repository
public class FuturesComprehensiveFeeDaoImpl implements FuturesComprehensiveFeeDao {
	
	@Autowired
	private FuturesComprehensiveFeeRepository repository;

	@Override
	public FuturesComprehensiveFee create(FuturesComprehensiveFee t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesComprehensiveFee update(FuturesComprehensiveFee t) {
		return repository.save(t);
	}

	@Override
	public FuturesComprehensiveFee retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesComprehensiveFee> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<FuturesComprehensiveFee> page(Specification<FuturesComprehensiveFee> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<FuturesComprehensiveFee> list() {
		return repository.findAll();
	}

}
