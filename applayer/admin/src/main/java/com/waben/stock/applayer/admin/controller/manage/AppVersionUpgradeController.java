package com.waben.stock.applayer.admin.controller.manage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.manage.AppVersionUpgradeBusiness;
import com.waben.stock.interfaces.dto.manage.AppVersionUpgradeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.AppVersionupgradeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/appVersion")
@Api(description="版本更新")
public class AppVersionUpgradeController {

	@Autowired
	private AppVersionUpgradeBusiness business;
	
	@RequestMapping(value = "/addUpgrade", method = RequestMethod.GET)
	public Response<AppVersionUpgradeDto> addUpgrade(AppVersionUpgradeDto dto){
		return new Response<>(business.addUpgrade(dto));
	}
	
	@RequestMapping(value = "/modifyUpgrade", method = RequestMethod.GET)
	public Response<AppVersionUpgradeDto> modifyUpgrade(AppVersionUpgradeDto dto){
		return new Response<>(business.modifyUpgrade(dto));
	}
	
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public Response<Long> delete(@PathVariable("id") Long id){
		return new Response<>(business.delete(id));
	}
	
	@RequestMapping(value = "/page", method = RequestMethod.GET)
	public Response<PageInfo<AppVersionUpgradeDto>> page(AppVersionupgradeQuery query){
		return new Response<>(business.page(query));
	}
	
	@RequestMapping(value = "/{deviceType}/{id}", method = RequestMethod.GET)
	public Response<Long> isCurrentVersion(@PathVariable("deviceType") Integer deviceType, @PathVariable("id") Long id){
		return new Response<>(business.isCurrentVersion(deviceType, id));
	}
}
