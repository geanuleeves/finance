package com.waben.stock.applayer.tactics.business.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesTradeEntrustInterface;

/**
 * 交易委托 Business
 * 
 * @author lma
 *
 */
@Service
public class FuturesTradeEntrustBusiness {

	@Autowired
	@Qualifier("futuresTradeEntrustInterface")
	private FuturesTradeEntrustInterface reference;

	public FuturesTradeEntrustDto cancelEntrust(Long id, Long publisherId) {
		Response<FuturesTradeEntrustDto> response = reference.cancelEntrust(id, publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
