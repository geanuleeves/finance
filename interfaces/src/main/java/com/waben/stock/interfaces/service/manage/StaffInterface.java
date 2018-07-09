package com.waben.stock.interfaces.service.manage;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StaffQuery;

/**
 * @author Created by yuyidi on 2017/11/15.
 * @desc
 */
@FeignClient(name = "manage", path = "staff", qualifier = "staffInterface")
public interface StaffInterface {

	@RequestMapping(value = "/{username}", method = RequestMethod.GET)
	Response<StaffDto> fetchByUserName(@PathVariable("username") String username);

	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = "application/json")
	Response<PageInfo<StaffDto>> pagesByQuery(@RequestBody StaffQuery staffQuery);

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = "application/json")
	Response<StaffDto> saveStaff(@RequestBody StaffDto staffDto);

	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
	Response<StaffDto> fetchById(@PathVariable("id") Long id);

	@RequestMapping(value = "/modify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<StaffDto> modify(@RequestBody StaffDto staffDto);

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	void delete(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/findAll", method = RequestMethod.GET, consumes = "application/json")
	Response<List<StaffDto>> findAll();

}
