package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesTradeEntrustBusiness;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
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
@RequestMapping("/futures_trade_entrust")
@Api(description = "交易委托")
public class FuturesTradeEntrustController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeEntrustBusiness futuresTradeEntrustBusiness;

    @GetMapping("/pages")
    @ApiOperation(value = "交易委托列表")
    public Response<PageInfo<FuturesTradeEntrustDto>> pages(int page, int size) {
        FuturesTradeEntrustQuery query = new FuturesTradeEntrustQuery(page, size);
        return new Response<>(futuresTradeEntrustBusiness.pages(query));
    }

}
