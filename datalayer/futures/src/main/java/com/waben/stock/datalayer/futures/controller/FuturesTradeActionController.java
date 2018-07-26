package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.service.FuturesTradeActionService;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.futures.FuturesTradeActionInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单交易开平仓记录
 * @author chenk 2018/7/26
 */
@RestController
@RequestMapping("/trade_action")
@Api(description = "订单交易开平仓记录接口列表")
public class FuturesTradeActionController implements FuturesTradeActionInterface {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeActionService futuresTradeActionService;

    @Override
    public Response<FuturesTradeActionDto> fetchById(@PathVariable Long id) {
        return new Response<>(
                CopyBeanUtils.copyBeanProperties(FuturesTradeActionDto.class, futuresTradeActionService.findById(id), false));
    }

}
