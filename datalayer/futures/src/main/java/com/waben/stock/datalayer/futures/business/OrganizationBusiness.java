package com.waben.stock.datalayer.futures.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.organization.FuturesAgentPriceDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;

@Service
public class OrganizationBusiness {

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface organizationInterface;

	/**
	 * 获取用户的期货代理商价格数据
	 * 
	 * @param publisherId
	 *            发布人ID
	 * @param commodityId
	 *            品种ID
	 * @return 期货代理价格
	 */
	public FuturesAgentPriceDto getCurrentAgentPrice(Long publisherId, Long commodityId) {
		Response<FuturesAgentPriceDto> response = organizationInterface.getCurrentAgentPrice(publisherId, commodityId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
