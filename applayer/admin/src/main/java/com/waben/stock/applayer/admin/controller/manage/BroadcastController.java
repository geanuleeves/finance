package com.waben.stock.applayer.admin.controller.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.manage.BroadcastBusiness;
import com.waben.stock.interfaces.dto.admin.BroadcastDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.BroadcastQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/broadcast")
@Api(description="直播地址")
public class BroadcastController {
	
	@Autowired
	private BroadcastBusiness business;
	
	@PostMapping("/save")
	@ApiOperation(value = "新增直播地址")
	public Response<BroadcastDto> save(BroadcastDto t){
		return new Response<>(business.save(t));
	}
	
	@PutMapping("/modify")
	@ApiOperation(value = "修改直播地址")
	public Response<BroadcastDto> modify(BroadcastDto t){
		return new Response<>(business.modify(t));
	}
	
	@GetMapping("/pages")
	@ApiOperation(value = "查询直播地址")
	public Response<PageInfo<BroadcastDto>> page(BroadcastQuery query){
		return new Response<>(business.page(query));
	}
	
	@DeleteMapping("/delete/{id}")
	@ApiOperation(value = "删除直播地址")
	public Response<Long> delete(@PathVariable Long id){
		return new Response<>(business.delete(id));
	}
	
	@RequestMapping(value = "/isCurren/{id}", method = RequestMethod.GET)
	@ApiOperation(value = "启用")
	public Response<Long> iscurrency(@PathVariable Long id){
		return new Response<>(business.isCurrency(id));
	}
	
}
