package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 订单交易开平仓记录
 *
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

    @Override
    public Response<FuturesTradeActionDto> save(@RequestBody FuturesTradeActionDto dto) {
        FuturesTradeAction futuresTradeAction = CopyBeanUtils.copyBeanProperties(
                FuturesTradeAction.class, dto, false);
        FuturesTradeAction result = futuresTradeActionService.save(futuresTradeAction);
        FuturesTradeActionDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeActionDto(),
                false);
        return new Response<>(response);
    }

    @Override
    public Response<FuturesTradeActionDto> modify(FuturesTradeActionDto dto) {
        FuturesTradeAction futuresTradeAction = CopyBeanUtils.copyBeanProperties(FuturesTradeAction.class, dto, false);
        FuturesTradeAction result = futuresTradeActionService.modify(futuresTradeAction);
        FuturesTradeActionDto resultDto = CopyBeanUtils.copyBeanProperties(result, new FuturesTradeActionDto(),
                false);
        return new Response<>(resultDto);
    }

    @Override
    public Response<String> delete(Long id) {
        futuresTradeActionService.delete(id);
        Response<String> res = new Response<String>();
        res.setCode("200");
        res.setMessage("响应成功");
        res.setResult("1");
        return res;
    }

}
