package com.waben.stock.applayer.operation.business;

import com.waben.stock.applayer.operation.service.publisher.CapitalFlowService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalFlowQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CapitalFlowBusiness {
    @Autowired
    @Qualifier("capitalFlowFeignService")
    private CapitalFlowService capitalFlowService;

    public PageInfo<CapitalFlowDto> pages(CapitalFlowQuery query) {
        Response<PageInfo<CapitalFlowDto>> response = capitalFlowService.pagesByQuery(query);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }
}
