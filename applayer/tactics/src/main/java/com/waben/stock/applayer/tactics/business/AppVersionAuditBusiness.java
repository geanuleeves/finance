package com.waben.stock.applayer.tactics.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.manage.AppVersionAuditDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.manage.AppVersionAuditInterface;

/**
 * app版本 Business
 * 
 * @author lma
 *
 */
@Service
public class AppVersionAuditBusiness {

	@Autowired
	@Qualifier("appVersionAuditInterface")
	private AppVersionAuditInterface reference;

	public AppVersionAuditDto getAppVersionAudit(Integer deviceType, Integer shellIndex) {
		Response<AppVersionAuditDto> response = reference.getAppVersionAudit(deviceType, shellIndex);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
