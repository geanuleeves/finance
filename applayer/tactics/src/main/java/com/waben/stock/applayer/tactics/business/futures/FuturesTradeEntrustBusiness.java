package com.waben.stock.applayer.tactics.business.futures;

import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeEntrustInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author chenk 2018/7/27
 */
@Service
public class FuturesTradeEntrustBusiness {

    Logger logger = LoggerFactory.getLogger(getClass());

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
        Response<PageInfo<FuturesTradeEntrustDto>> response = reference.pages(query);
        if ("200".equals(response.getCode())) {
            return response.getResult();
        }
        throw new ServiceException(response.getCode());
    }

    public PageInfo<FuturesTradeEntrustDto> pagesPhone(FuturesTradeEntrustQuery query) {
        Response<PageInfo<FuturesTradeEntrustDto>> response = reference.pagesPhone(query);
        if ("200".equals(response.getCode())) {
            return response.getResult();
        }
        throw new ServiceException(response.getCode());
    }


    public FuturesTradeEntrustDto detail(Long id) {
        Response<FuturesTradeEntrustDto> response = reference.detail(id);
        if ("200".equals(response.getCode())) {
            return response.getResult();
        }
        throw new ServiceException(response.getCode());
    }

}
