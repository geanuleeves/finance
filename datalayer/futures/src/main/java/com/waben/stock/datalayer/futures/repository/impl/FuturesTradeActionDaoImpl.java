package com.waben.stock.datalayer.futures.repository.impl;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.repository.FuturesTradeActionDao;
import com.waben.stock.datalayer.futures.repository.impl.jpa.FuturesTradeActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单交易开平仓记录
 * @author chenk 2018/7/26
 */
@Repository
public class FuturesTradeActionDaoImpl implements FuturesTradeActionDao {

    @Autowired
    private FuturesTradeActionRepository repository;

    @Override
    public FuturesTradeAction create(FuturesTradeAction t) {
        return repository.save(t);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public FuturesTradeAction update(FuturesTradeAction t) {
        return repository.save(t);
    }

    @Override
    public FuturesTradeAction retrieve(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<FuturesTradeAction> page(int page, int limit) {
        return repository.findAll(new PageRequest(page, limit));
    }

    @Override
    public Page<FuturesTradeAction> page(Specification<FuturesTradeAction> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    public List<FuturesTradeAction> list() {
        return repository.findAll();
    }

}
