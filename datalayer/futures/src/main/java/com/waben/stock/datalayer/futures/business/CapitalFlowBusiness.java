package com.waben.stock.datalayer.futures.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.enums.CapitalFlowExtendType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.publisher.CapitalFlowInterface;

/**
 * 资金流水 Business
 * 
 * @author lma
 *
 */
@Service
public class CapitalFlowBusiness {

	@Autowired
	@Qualifier("capitalFlowInterface")
	private CapitalFlowInterface service;

	public List<CapitalFlowDto> fetchByExtendTypeAndExtendId(CapitalFlowExtendType extendType, Long extendId) {
		Response<List<CapitalFlowDto>> response = service.fetchByExtendTypeAndExtendId(extendType.getIndex(), extendId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
