package com.waben.stock.interfaces.service.manage;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.manage.AppVersionUpgradeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.AppVersionupgradeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

/**
 * app版本升级 公共接口
 * 
 * @author lma
 *
 */
@FeignClient(name = "manage", path = "upgrade", qualifier = "appVersionUpgradeInterface")
public interface AppVersionUpgradeInterface {

	@RequestMapping(value = "/{versionCode}/checkUpgrade/{deviceType}", method = RequestMethod.GET)
	Response<AppVersionUpgradeDto> checkUpgrade(@PathVariable("versionCode") Integer versionCode,
			@PathVariable("deviceType") Integer deviceType, @RequestParam("shellIndex") Integer shellIndex);

	@RequestMapping(value = "/addUpgrade", method = RequestMethod.POST, consumes = "application/json")
	Response<AppVersionUpgradeDto> addUpgrade(@RequestBody AppVersionUpgradeDto dto);
	
	@RequestMapping(value = "/modifyUpgrade", method = RequestMethod.POST, consumes = "application/json")
	Response<AppVersionUpgradeDto> modifyUpgrade(@RequestBody AppVersionUpgradeDto dto);
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<Long> delete(@RequestParam("id") Long id);
	
	@RequestMapping(value = "/dowload/{deviceType}", method = RequestMethod.GET)
	Response<String> dowload(@PathVariable("deviceType") Integer deviceType);
	
	@RequestMapping(value = "/{deviceType}/{id}", method = RequestMethod.GET)
	Response<Long> isCurrentVersion(@PathVariable("deviceType") Integer deviceType, @RequestParam("id") Long id);
	
	@RequestMapping(value = "/page", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<AppVersionUpgradeDto>> page(@RequestBody AppVersionupgradeQuery query);
}
