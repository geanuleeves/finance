package com.waben.stock.applayer.operation.business;

import com.waben.stock.applayer.operation.service.stock.StrategyTypeService;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.stockcontent.StrategyTypeDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.StrategyTypeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * @author Created by yuyidi on 2017/12/6.
 * @desc
 */
@Service
public class StrategyTypeBusiness {

    @Autowired
    @Qualifier("strategyTypeFeignService")
    private StrategyTypeService strategyTypeService;

    public PageInfo<StrategyTypeDto> pages(StrategyTypeQuery query) {
        Response<PageInfo<StrategyTypeDto>> response = strategyTypeService.pages(query);
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }


}
