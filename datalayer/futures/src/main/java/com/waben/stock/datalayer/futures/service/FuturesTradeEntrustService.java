package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 交易委托
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesTradeEntrustService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeEntrustDao futuresTradeEntrustDao;

    public FuturesTradeEntrust findById(Long id) {
        return futuresTradeEntrustDao.retrieve(id);
    }

    public FuturesTradeEntrust save(FuturesTradeEntrust futuresTradeEntrust) {
        return futuresTradeEntrustDao.create(futuresTradeEntrust);
    }

    public FuturesTradeEntrust modify(FuturesTradeEntrust futuresTradeEntrust) {
        return futuresTradeEntrustDao.update(futuresTradeEntrust);
    }

    public void delete(Long id) {
        futuresTradeEntrustDao.delete(id);
    }

}
