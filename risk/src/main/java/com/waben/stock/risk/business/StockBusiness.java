package com.waben.stock.risk.business;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.stockcontent.StockDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.risk.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author Created by yuyidi on 2017/12/14.
 * @desc
 */
@Service
public class StockBusiness {
    @Autowired
    @Qualifier("stockFeignService")
    private StockService stockService;


    public StockDto fetchByCode(String stockCode) {
        Response<StockDto> response = stockService.fetchWithExponentByCode(stockCode);
        System.out.println("股票内容请求结果:"+ JacksonUtil.encode(response));
        String code = response.getCode();
        if ("200".equals(code)) {
            return response.getResult();
        }else if(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)){
            throw new NetflixCircuitException(code);
        }
        throw new ServiceException(response.getCode());
    }
}
