package com.waben.stock.datalayer.publisher.repository.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import com.waben.stock.datalayer.publisher.entity.Publisher;
import com.waben.stock.datalayer.publisher.repository.PublisherDao;
import com.waben.stock.datalayer.publisher.repository.impl.jpa.PublisherRepository;

/**
 * @author Created by yuyidi on 2017/11/12.
 * @desc
 */
@Repository
public class PublisherDaoImpl implements PublisherDao {

	@Autowired
	private PublisherRepository repository;

	public Publisher create(Publisher publisher) {
		return repository.save(publisher);
	}

	public void delete(Long id) {
		repository.delete(id);
	}

	public Publisher update(Publisher publisher) {
		return repository.save(publisher);
	}

	public Publisher retrieve(Long id) {
		return repository.findById(id);
	}

	public Page<Publisher> page(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	public List<Publisher> list() {
		return repository.findAll();
	}

	@Override
	public Page<Publisher> page(Specification<Publisher> specification, Pageable pageable) {
		return repository.findAll(specification, pageable);
	}

	@Override
	public Publisher retriveByPhone(String phone) {
		return repository.findByPhone(phone);
	}

	@Override
	public Publisher retriveBySerialCode(String serialCode) {
		return repository.findBySerialCode(serialCode);
	}
}
