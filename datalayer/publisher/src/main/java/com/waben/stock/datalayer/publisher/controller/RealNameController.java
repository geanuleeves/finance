package com.waben.stock.datalayer.publisher.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.publisher.entity.RealName;
import com.waben.stock.datalayer.publisher.service.RealNameService;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

/**
 * 实名认证 Controller
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/realname")
public class RealNameController implements RealNameInterface {

	@Autowired
	private RealNameService service;

	@Override
	public Response<RealNameDto> add(@RequestBody RealNameDto realName) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(RealNameDto.class,
				service.save(CopyBeanUtils.copyBeanProperties(RealName.class, realName, false)), false));
	}

	@Override
	public Response<RealNameDto> fetch(@PathVariable String resourceTypeIndex, @PathVariable Long resourceId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(RealNameDto.class,
				service.findByResourceTypeAndResourceId(ResourceType.getByIndex(resourceTypeIndex), resourceId),
				false));
	}
	
	@Override
	public Response<List<RealNameDto>> findByName(@PathVariable String name){
		List<RealName> list = service.findByName(name);
		List<RealNameDto> result = new ArrayList<RealNameDto>();
		for (RealName realName : list) {
			result.add(CopyBeanUtils.copyBeanProperties(RealNameDto.class, realName, false));
		}
		return new Response<>(result);
	}

	@Override
	public Response<RealNameDto> fetchByResourceId(@PathVariable Long resourceId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(RealNameDto.class, service.findByResourceId(resourceId), false));
	}

}
