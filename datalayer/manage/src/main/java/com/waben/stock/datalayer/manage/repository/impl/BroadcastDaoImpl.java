package com.waben.stock.datalayer.manage.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.manage.entity.Broadcast;
import com.waben.stock.datalayer.manage.repository.BroadcastDao;
import com.waben.stock.datalayer.manage.repository.impl.jpa.BroadcastRepository;

@Repository
public class BroadcastDaoImpl implements BroadcastDao {
	
	@Autowired
	private BroadcastRepository repository;

	@Override
	public Broadcast create(Broadcast t) {
		return repository.save(t);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public Broadcast update(Broadcast t) {
		return repository.save(t);
	}

	@Override
	public Broadcast retrieve(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<Broadcast> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public Page<Broadcast> page(Specification<Broadcast> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public List<Broadcast> list() {
		return repository.findAll();
	}

	@Override
	public List<Broadcast> findBytype(String type, boolean enable) {
		return repository.findBytype(type, enable);
	}

}
