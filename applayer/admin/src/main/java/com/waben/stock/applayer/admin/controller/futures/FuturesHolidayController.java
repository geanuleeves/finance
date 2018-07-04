package com.waben.stock.applayer.admin.controller.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.futures.FuturesHolidayBusiness;
import com.waben.stock.interfaces.dto.admin.futures.FuturesHolidayDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesHolidayQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/futuresHoliday")
@Api(description = "节假日设置")
public class FuturesHolidayController {

	@Autowired
	private FuturesHolidayBusiness business;
	
	@RequestMapping(value = "/addHoliday", method = RequestMethod.POST)
	@ApiOperation(value = "新增节假日管理")
	public Response<FuturesHolidayDto> addHoliday(FuturesHolidayDto dto){
		return new Response<>(business.addHoliday(dto));
	}
	
	@PutMapping("/modify")
	@ApiOperation(value = "修改节假日管理")
	public Response<FuturesHolidayDto> modifyHoliday(FuturesHolidayDto dto){
		return new Response<>(business.modifyHoliday(dto));
	}
	
	@GetMapping("/isEnable")
	@ApiOperation(value = "启动")
	public Response<FuturesHolidayDto> isEnable(FuturesHolidayQuery query){
		return new Response<>(business.isEnable(query));
	}
	
	@GetMapping("/page")
	@ApiOperation(value = "获取节假日数据")
	public Response<PageInfo<FuturesHolidayDto>> page(FuturesHolidayQuery query){
		return new Response<>(business.page(query));
	}
	
	@DeleteMapping("/delete")
	@ApiOperation(value = "删除节假日管理")
	public Response<String> detele(FuturesHolidayQuery query){
		Response<String> response = new Response<String>();
		response.setCode("200");
		response.setMessage("响应成功");
		response.setResult(business.delete(query));
		return response;
	}
	
	@RequestMapping(value = "/findById", method = RequestMethod.GET)
	@ApiOperation(value = "根据ID获取数据")
	public Response<FuturesHolidayDto> findById(FuturesHolidayQuery query){
		return new Response<>(business.findById(query));
	}
}
