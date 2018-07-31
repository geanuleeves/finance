package com.waben.stock.applayer.tactics.business.futures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeActionInterface;

/**
 * @author chenk 2018/7/27
 */
@Service
public class FuturesTradeActionBusiness {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    @Qualifier("futuresTradeActionInterface")
    private FuturesTradeActionInterface reference;

    public PageInfo<FuturesTradeActionViewDto> pages(FuturesTradeActionQuery query) {
        Response<PageInfo<FuturesTradeActionViewDto>> response = reference.pages(query);
        if ("200".equals(response.getCode())) {
            return response.getResult();
        }
        throw new ServiceException(response.getCode());
    }

}
