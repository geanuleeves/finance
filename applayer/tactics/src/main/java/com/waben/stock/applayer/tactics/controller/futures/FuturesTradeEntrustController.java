package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesTradeEntrustBusiness;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

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
    
    @PostMapping("/cancelEntrust/{entrustId}")
	@ApiOperation(value = "用户取消委托", notes = "entrustId为委托Id")
	public Response<FuturesTradeEntrustDto> cancelEntrust(@PathVariable Long entrustId) {
		return new Response<>(futuresTradeEntrustBusiness.cancelEntrust(entrustId, SecurityUtil.getUserId()));
	}

    @GetMapping("/pages")
    @ApiOperation(value = "交易委托列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "entrustNo", value = "委托编号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "contractId", value = "对应的合约ID", dataType = "long", paramType = "query", required = false),
            @ApiImplicitParam(name = "commodityNo", value = "品种编号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "contractNo", value = "合约编号", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "orderType", value = "订单交易类型", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "entrustTime", value = "委托时间", dataType = "date", paramType = "query", required = false),
            @ApiImplicitParam(name = "priceType", value = "价格类型", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "tradeActionType", value = "交易开平仓类型", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "state", value = "委托状态", dataType = "string", paramType = "query", required = false),
            @ApiImplicitParam(name = "tradeTime", value = "交易成功时间", dataType = "date", paramType = "query", required = false)
    })
    public Response<PageInfo<FuturesTradeEntrustDto>> pages(@RequestParam(required = false) String entrustNo,
                                                            @RequestParam(required = false) Long contractId,
                                                            @RequestParam(required = false) String commodityNo,
                                                            @RequestParam(required = false) String contractNo,
                                                            @RequestParam(required = false) String orderType,
                                                            @RequestParam(required = false) Date entrustTime,
                                                            @RequestParam(required = false) String priceType,
                                                            @RequestParam(required = false) String tradeActionType,
                                                            @RequestParam(required = false) String state,
                                                            @RequestParam(required = false) Date tradeTime) {
        FuturesTradeEntrustQuery query = new FuturesTradeEntrustQuery();
        query.setPublisherId(SecurityUtil.getUserId());
        query.setEntrustNo(entrustNo);
        query.setContractId(contractId);
        query.setCommodityNo(commodityNo);
        query.setContractNo(contractNo);
        query.setOrderType(orderType);
        query.setEntrustTime(entrustTime);
        query.setPriceType(priceType);
        query.setTradeActionType(tradeActionType);
        query.setState(state);
        query.setTradeTime(tradeTime);
        return new Response<>(futuresTradeEntrustBusiness.pages(query));
    }

}
