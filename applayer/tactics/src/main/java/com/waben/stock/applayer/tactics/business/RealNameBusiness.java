package com.waben.stock.applayer.tactics.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.tactics.reference.RealNameReference;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;

/**
 * 实名认证 Business
 * 
 * @author luomengan
 *
 */
@Service
public class RealNameBusiness {

	@Autowired
	@Qualifier("realNameReference")
	private RealNameReference reference;

	public RealNameDto add(RealNameDto realName) {
		Response<RealNameDto> response = reference.add(realName);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public RealNameDto fetch(ResourceType resourceType, Long resourceId) {
		Response<RealNameDto> response = reference.fetch(resourceType.getIndex(), resourceId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
