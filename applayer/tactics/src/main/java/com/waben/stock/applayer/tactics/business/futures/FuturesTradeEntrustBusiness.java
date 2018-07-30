package com.waben.stock.applayer.tactics.business.futures;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.futures.FuturesTradeEntrustInterface;

/**
 * @author chenk 2018/7/27
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

    public PageInfo<FuturesTradeEntrustDto> pages(FuturesTradeEntrustQuery query) {
        Response<PageInfo<FuturesTradeEntrustDto>> response = reference.pagesAdmin(query);
        if ("200".equals(response.getCode())) {
            return response.getResult();
        }
        throw new ServiceException(response.getCode());
    }

}
