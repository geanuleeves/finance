package com.waben.stock.datalayer.futures.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.repository.FuturesContractDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;

/**
 * 合约订单
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesContractOrderService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesContractOrderDao futuresContractOrderDao;
    
    @Autowired
    private FuturesContractDao contractDao;

    public FuturesContractOrder findById(Long id) {
        return futuresContractOrderDao.retrieve(id);
    }

    public FuturesContractOrder save(FuturesContractOrder futuresContractOrder) {
        return futuresContractOrderDao.create(futuresContractOrder);
    }

    public FuturesContractOrder modify(FuturesContractOrder futuresContractOrder) {
        return futuresContractOrderDao.update(futuresContractOrder);
    }

    public void delete(Long id) {
        futuresContractOrderDao.delete(id);
    }

	public FuturesContractOrder findByContractId(Long contractId) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if(contract != null) {
			return futuresContractOrderDao.retrieveByContract(contract);
		}
		return null;
	}


}
