package com.waben.stock.applayer.admin.business.manage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.admin.BroadcastDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.BroadcastQuery;
import com.waben.stock.interfaces.service.manage.BroadcastInterface;

@Service
public class BroadcastBusiness {
	
	@Autowired
	@Qualifier("broadcastInterface")
	private BroadcastInterface reference;
	
	public BroadcastDto save(BroadcastDto t){
		Response<BroadcastDto> response = reference.save(t);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public BroadcastDto modify(BroadcastDto t){
		Response<BroadcastDto> response = reference.modify(t);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public PageInfo<BroadcastDto> page(BroadcastQuery query){
		Response<PageInfo<BroadcastDto>> response = reference.page(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public Long delete(Long id){
		Response<Long> response = reference.delete(id);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public Long isCurrency(Long id){
		Response<Long> response = reference.isCurren(id);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
}
