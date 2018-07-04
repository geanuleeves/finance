package com.waben.stock.applayer.admin.business.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.waben.stock.interfaces.dto.admin.futures.FuturesHolidayDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesHolidayQuery;
import com.waben.stock.interfaces.service.futures.FuturesHolidayInterface;

@Service
public class FuturesHolidayBusiness {

	@Autowired
	@Qualifier("futuresHolidayInterface")
	private FuturesHolidayInterface reference;
	
	public PageInfo<FuturesHolidayDto> page(FuturesHolidayQuery query){
		Response<PageInfo<FuturesHolidayDto>> response = reference.page(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesHolidayDto isEnable(FuturesHolidayQuery query){
		Response<FuturesHolidayDto> response = reference.isEnable(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesHolidayDto addHoliday(FuturesHolidayDto dto){
		Response<FuturesHolidayDto> response = reference.addHoliday(dto);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesHolidayDto modifyHoliday(FuturesHolidayDto dto){
		Response<FuturesHolidayDto> response = reference.modityHoliday(dto);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public String delete(FuturesHolidayQuery query){
		Response<String> response = reference.delete(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesHolidayDto findById(FuturesHolidayQuery query){
		Response<FuturesHolidayDto> response = reference.featchById(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
