package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.repository.FuturesTradeActionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 订单交易开平仓记录
 * @author chenk 2018/7/26
 */
@Service
public class FuturesTradeActionService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeActionDao futuresTradeActionDao;

    public FuturesTradeAction findById(Long id) {
        return futuresTradeActionDao.retrieve(id);
    }

}
