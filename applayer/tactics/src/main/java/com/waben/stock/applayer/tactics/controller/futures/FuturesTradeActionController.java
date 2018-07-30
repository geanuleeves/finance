package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesTradeActionBusiness;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

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

    @PostMapping("/pages")
    @ApiOperation(value = "订单交易开平仓记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "对应的订单", dataType = "long", paramType = "query", required = false),
            @ApiImplicitParam(name = "tradeEntrustId", value = "对应的委托", dataType = "long", paramType = "query", required = false),
            @ApiImplicitParam(name = "entrustTime", value = "委托时间", dataType = "date", paramType = "query", required = false),
            @ApiImplicitParam(name = "tradeActionType", value = "交易开平仓类型", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "windControlType", value = "风控类型", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "state", value = "委托状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "tradeTime", value = "交易成功时间", dataType = "date", paramType = "query", required = false),
            @ApiImplicitParam(name = "settlementTime", value = "结算时间", dataType = "string", paramType = "query", required = false)
    })
    public Response<PageInfo<FuturesTradeActionViewDto>> pages(@RequestParam(required = false) Long orderId,
                                                               @RequestParam(required = false) Long tradeEntrustId,
                                                               @RequestParam(required = false) Date entrustTime,
                                                               @RequestParam(required = false) String tradeActionType,
                                                               @RequestParam(required = false) String windControlType,
                                                               @RequestParam(required = false) Date tradeTime,
                                                               @RequestParam(required = false) Date settlementTime) {
        FuturesTradeActionQuery query = new FuturesTradeActionQuery();
        query.setPublisherId(SecurityUtil.getUserId());
        query.setOrderId(orderId);
        query.setTradeEntrustId(tradeEntrustId);
        query.setEntrustTime(entrustTime);
        query.setTradeActionType(tradeActionType);
        query.setWindControlType(windControlType);
        query.setTradeTime(tradeTime);
        query.setSettlementTime(settlementTime);
        return new Response<>(futuresTradeActionBusiness.pages(query));
    }

}
