package com.waben.stock.applayer.tactics.business.futures;

import com.waben.stock.interfaces.dto.futures.FuturesContractOrderDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractOrderInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class FuturesContractOrderBusiness {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("futuresContractOrderInterface")
	private FuturesContractOrderInterface reference;
	
	public FuturesContractOrderDto fetchByContractIdAndPublisherId(Long contractId, Long publisherId) {
		Response<FuturesContractOrderDto> response = reference.fetchByContractIdAndPublisherId(contractId, publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesContractOrderViewDto> pages(FuturesContractOrderQuery query) {
		Response<PageInfo<FuturesContractOrderViewDto>> response = reference.pagesAdmin(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	
}
