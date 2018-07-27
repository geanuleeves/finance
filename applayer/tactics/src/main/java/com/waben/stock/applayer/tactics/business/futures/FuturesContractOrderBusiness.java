package com.waben.stock.applayer.tactics.business.futures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesContractOrderDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesContractOrderInterface;

@Service
public class FuturesContractOrderBusiness {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("futuresContractOrderInterface")
	private FuturesContractOrderInterface reference;
	
	public FuturesContractOrderDto fetchByContractId(Long contractId) {
		Response<FuturesContractOrderDto> response = reference.fetchByContractId(contractId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
}
