package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesTradeActionBusiness;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chenk 2018/7/27
 */
@RestController
@RequestMapping("/futures_trade_action")
@Api(description = "订单交易开平仓记录")
public class FuturesTradeActionController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeActionBusiness futuresTradeActionBusiness;

    @GetMapping("/pages")
    @ApiOperation(value = "订单交易开平仓记录列表")
    public Response<PageInfo<FuturesTradeActionViewDto>> pages(int page, int size) {
        FuturesTradeActionQuery query = new FuturesTradeActionQuery(page, size);
        return new Response<>(futuresTradeActionBusiness.pages(query));
    }

}
