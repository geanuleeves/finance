package com.waben.stock.datalayer.futures.repository.impl;

import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesTradeEntrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 交易委托
 * @author chenk 2018/7/26
 */
@Repository
public class FuturesTradeEntrustDaoImpl implements FuturesTradeEntrustDao {

    @Autowired
    private FuturesTradeEntrustRepository repository;

    @Override
    public FuturesTradeEntrust create(FuturesTradeEntrust t) {
        return repository.save(t);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public FuturesTradeEntrust update(FuturesTradeEntrust t) {
        return repository.save(t);
    }

    @Override
    public FuturesTradeEntrust retrieve(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<FuturesTradeEntrust> page(int page, int limit) {
        return repository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Page<FuturesTradeEntrust> page(Specification<FuturesTradeEntrust> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    public List<FuturesTradeEntrust> list() {
        return repository.findAll();
    }

    @Override
    public List<FuturesTradeEntrust> findByPublisherId(Long contractId) {
        return repository.findByPublisherId(contractId);
    }

	@Override
	public List<FuturesTradeEntrust> retrieveByBackhandEntrustId(Long entrustId) {
		return repository.findByBackhandEntrustId(entrustId);
	}
}
