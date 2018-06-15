package com.waben.stock.datalayer.organization.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;

/**
 * 期货代理价格 Business
 * 
 * @author sl
 *
 */
@Service
public class FuturesAgentPriceBusiness {

	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface futuresContractInterface;

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface organizationInterface;

	@Autowired
	@Qualifier("futuresCommodityInterface")
	private FuturesCommodityInterface futuresCommodityInterface;

	public FuturesCommodityDto getFuturesByCommodityId(Long commodityId) {
		Response<FuturesCommodityDto> response = futuresCommodityInterface.getFuturesByCommodityId(commodityId);
		if (response.getCode().equals("200")) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public OrganizationDto fetchByOrgId(Long id) {
		Response<OrganizationDto> response = organizationInterface.fetchByOrgId(id);
		if (response.getCode().equals("200")) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
