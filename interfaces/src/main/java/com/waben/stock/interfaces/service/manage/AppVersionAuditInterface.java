package com.waben.stock.interfaces.service.manage;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.manage.AppVersionAuditDto;
import com.waben.stock.interfaces.pojo.Response;

/**
 * app版本审核 公共接口
 * 
 * @author luomengan
 *
 */
@FeignClient(name = "manage", path = "appversionaudit", qualifier = "appVersionAuditInterface")
public interface AppVersionAuditInterface {

	@RequestMapping(value = "/getAppVersionAudit", method = RequestMethod.GET)
	Response<AppVersionAuditDto> getAppVersionAudit(@RequestParam("deviceType") Integer deviceType,
			@RequestParam("shellIndex") Integer shellIndex);

}
