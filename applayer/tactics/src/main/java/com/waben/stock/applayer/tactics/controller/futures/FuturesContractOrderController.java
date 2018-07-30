package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesContractOrderBusiness;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
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
@RequestMapping("/futures_contract_order")
@Api(description = "合约订单")
public class FuturesContractOrderController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesContractOrderBusiness futuresContractOrderBusiness;

    @GetMapping("/pages")
    @ApiOperation(value = "期货合约列表")
    public Response<PageInfo<FuturesContractOrderViewDto>> pages(int page, int size) {
        FuturesContractOrderQuery query = new FuturesContractOrderQuery(page, size);
        return new Response<>(futuresContractOrderBusiness.pages(query));
    }

}
