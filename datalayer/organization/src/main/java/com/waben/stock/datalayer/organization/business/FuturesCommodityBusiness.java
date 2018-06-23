package com.waben.stock.datalayer.organization.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;

@Service
public class FuturesCommodityBusiness {

	@Autowired
	@Qualifier("futuresCommodityInterface")
	private FuturesCommodityInterface commodityInterface;

	public FuturesCommodityDto getFuturesByCommodityId(Long commodityId) {
		Response<FuturesCommodityDto> response = commodityInterface.getFuturesByCommodityId(commodityId);
		if (response.getCode().equals("200")) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
