package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesContractOrderBusiness;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    @ApiOperation(value = "合约订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commodityNo", value = "品种编号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "contractNo", value = "对应的合约ID", dataType = "string", paramType = "query", required = false)
    })
    public Response<PageInfo<FuturesContractOrderViewDto>> pages(int page, int size, @RequestParam(required = false)String commodityNo,
                                                                 @RequestParam(required = false)String contractNo) {
        FuturesContractOrderQuery query = new FuturesContractOrderQuery();
        query.setPage(page);
        query.setSize(size);
        query.setPublisherId(SecurityUtil.getUserId());
        query.setCommodityNo(commodityNo);
        query.setContractNo(contractNo);
        return new Response<>(futuresContractOrderBusiness.pages(query));
    }

}
