package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.service.FuturesTradeEntrustService;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesTradeEntrustInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 交易委托
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/trade_entrust")
@Api(description = "交易委托接口列表")
public class FuturesTradeEntrustController implements FuturesTradeEntrustInterface {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeEntrustService futuresTradeEntrustService;

    @Override
    public Response<FuturesTradeEntrustDto> fetchById(@PathVariable Long id) {
        return new Response<>(
                CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class, futuresTradeEntrustService.findById(id), false));
    }

}
