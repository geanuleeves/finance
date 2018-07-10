package com.waben.stock.datalayer.manage.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.manage.entity.Broadcast;
import com.waben.stock.datalayer.manage.service.BroadcastService;
import com.waben.stock.interfaces.dto.admin.BroadcastDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.BroadcastQuery;
import com.waben.stock.interfaces.service.manage.BroadcastInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

@RestController
@RequestMapping("/broadcast")
public class BroadcastController implements BroadcastInterface {
	
	@Autowired
	private BroadcastService broadService;

	@Override
	public Response<BroadcastDto> save(@RequestBody BroadcastDto t) {
		Broadcast broad = CopyBeanUtils.copyBeanProperties(Broadcast.class, t, false);
		broad.setEnable(false);
		broad.setCreateTime(new Date());
		Broadcast result = broadService.save(broad);
		BroadcastDto response = CopyBeanUtils.copyBeanProperties(result, new BroadcastDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<BroadcastDto> modify(@RequestBody BroadcastDto t) {
		Broadcast broad = CopyBeanUtils.copyBeanProperties(Broadcast.class, t, false);
		broad.setEnable(false);
		Broadcast result = broadService.save(broad);
		BroadcastDto response = CopyBeanUtils.copyBeanProperties(result, new BroadcastDto(), false);
		return new Response<>(response);
	}

	@Override
	public Response<Long> delete(@PathVariable Long id) {
		broadService.delete(id);
		return new Response<>(id);
	}

	@Override
	public Response<Long> isCurren(@PathVariable Long id) {
		Broadcast broad = broadService.findById(id);
		if(broad.isEnable()){
			broad.setEnable(false);
		}else{
			broad.setEnable(true);
		}
		
		broadService.modify(broad);
		return new Response<>(id);
	}

	@Override
	public Response<PageInfo<BroadcastDto>> page(@RequestBody BroadcastQuery query) {
		Page<Broadcast> page = broadService.page(query);
		PageInfo<BroadcastDto> result = PageToPageInfo.pageToPageInfo(page, BroadcastDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<List<BroadcastDto>> findBytype(@RequestBody BroadcastQuery query) {
		List<Broadcast> list = broadService.findByType(query.getType(), query.isEnable());
		List<BroadcastDto> result = new ArrayList<BroadcastDto>();
		for(Broadcast cast : list){
			BroadcastDto dto = CopyBeanUtils.copyBeanProperties(cast, new BroadcastDto(), false);
			result.add(dto);
		}
		return new Response<>(result);
	}

}
