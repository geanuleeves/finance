package com.waben.stock.applayer.admin.business.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesGlobalConfigDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeLimitDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesGlobalConfigQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeLimitQuery;
import com.waben.stock.interfaces.service.futures.FuturesGlobalConfigInterface;
import com.waben.stock.interfaces.service.futures.FuturesTradeLimitInterface;

@Service
public class FuturesTradeLimitBusiness {

	@Autowired
	@Qualifier("futuresTradeLimitInterface")
	private FuturesTradeLimitInterface reference;
	
	@Autowired
	@Qualifier("futuresGlobalConfigInterface")
	private FuturesGlobalConfigInterface configReference;
	
	public PageInfo<FuturesGlobalConfigDto> pageConfig(FuturesGlobalConfigQuery query){
		Response<PageInfo<FuturesGlobalConfigDto>> response = configReference.pagesConfig(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesGlobalConfigDto saveAndModify(FuturesGlobalConfigDto global){
		Response<FuturesGlobalConfigDto> response = configReference.saveAndModify(global);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public PageInfo<FuturesTradeLimitDto> pagesLimiet(FuturesTradeLimitQuery query){
		Response<PageInfo<FuturesTradeLimitDto>> response = reference.pagesLimit(query);
		String code = response.getCode();
		if("200".equals(code)){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesTradeLimitDto save(FuturesTradeLimitDto limit){
		Response<FuturesTradeLimitDto> response = reference.addLimit(limit);
		String code = response.getCode();
		if("200".equals(code)){
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}
	
	public FuturesTradeLimitDto modify(FuturesTradeLimitDto limit){
		Response<FuturesTradeLimitDto> response = reference.modifyLimit(limit);
		String code = response.getCode();
		if("200".equals(code)){
			return response.getResult();
		}else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
			throw new NetflixCircuitException(code);
		}
		throw new ServiceException(response.getCode());
	}
	
	public void delete(Long id){
		reference.deleteLimit(id);
	}
}
