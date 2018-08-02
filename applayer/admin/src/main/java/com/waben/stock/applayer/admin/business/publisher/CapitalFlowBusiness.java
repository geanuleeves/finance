package com.waben.stock.applayer.admin.business.publisher;

import java.math.BigDecimal;

import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowAdminDto;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowFuturesAdminDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalFlowAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalFlowFuturesAdminQuery;
import com.waben.stock.interfaces.service.publisher.CapitalFlowInterface;

/**
 * 资金流水 Business
 * 
 * @author lma
 */
@Service
public class CapitalFlowBusiness {

	@Autowired
	@Qualifier("capitalFlowInterface")
	private CapitalFlowInterface reference;

	public PageInfo<CapitalFlowAdminDto> adminPagesByQuery(CapitalFlowAdminQuery query) {
		Response<PageInfo<CapitalFlowAdminDto>> response = reference.adminPagesByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BigDecimal adminAccumulateAmountByQuery(CapitalFlowAdminQuery query) {
		Response<BigDecimal> response = reference.adminAccumulateAmountByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public BigDecimal adminAccumulateFuturesAmountByQuery(CapitalFlowFuturesAdminQuery query) {
		Response<BigDecimal> response = reference.adminAccumulateFuturesAmountByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public PageInfo<CapitalFlowFuturesAdminDto> adminFuturesPagesByQuery(CapitalFlowFuturesAdminQuery query){
		Response<PageInfo<CapitalFlowFuturesAdminDto>> response = reference.adminFuturesPagesByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalFlowDto findById(Long id) {
		Response<CapitalFlowDto> response = reference.fetchById(id);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
