package com.waben.stock.applayer.tactics.business.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.admin.futures.FuturesGlobalConfigDto;
import com.waben.stock.interfaces.dto.admin.futures.PutForwardDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesGlobalConfigQuery;
import com.waben.stock.interfaces.service.futures.FuturesGlobalConfigInterface;
import com.waben.stock.interfaces.service.manage.PutForwardInterface;

@Service
public class FuturesTradeLimitBusiness {

	@Autowired
	private FuturesGlobalConfigInterface configReference;
	
	@Autowired
	@Qualifier("putForwardInterface")
	private PutForwardInterface putReference;
	
	public PageInfo<PutForwardDto> pagesPutForward(){
		Response<PageInfo<PutForwardDto>> response = putReference.pages();
		if("200".equals(response.getCode())){
			return response.getResult();
			
		}
		throw new ServiceException(response.getCode());
	}
	
	public PageInfo<FuturesGlobalConfigDto> pageConfig(){
		FuturesGlobalConfigQuery query = new FuturesGlobalConfigQuery();
		Response<PageInfo<FuturesGlobalConfigDto>> response = configReference.pagesConfig(query);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	
}
