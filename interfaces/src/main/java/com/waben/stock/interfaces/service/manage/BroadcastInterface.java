package com.waben.stock.interfaces.service.manage;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.admin.BroadcastDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.BroadcastQuery;

@FeignClient(name = "manage", path = "broadcast", qualifier = "broadcastInterface")
public interface BroadcastInterface {
	
	@RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
	Response<BroadcastDto> save(@RequestBody BroadcastDto t);
	
	@RequestMapping(value = "/modify", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BroadcastDto> modify(@RequestBody BroadcastDto t);
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<Long> delete(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/isCurren/{id}", method = RequestMethod.GET)
	Response<Long> isCurren(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = "application/json")
	Response<PageInfo<BroadcastDto>> page(@RequestBody BroadcastQuery query);
	
	@RequestMapping(value = "/findBytype", method = RequestMethod.GET, consumes = "application/json")
	Response<List<BroadcastDto>> findBytype(@RequestBody BroadcastQuery query);

}
