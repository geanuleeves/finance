package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 合约订单
 * @author chenk 2018/7/26
 */
@Service
public class FuturesContractOrderService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesContractOrderDao futuresContractOrderDao;

    public FuturesContractOrder findById(Long id) {
        return futuresContractOrderDao.retrieve(id);
    }

}
