package com.waben.stock.datalayer.organization.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.organization.business.ProfileBusiness;
import com.waben.stock.datalayer.organization.service.OrganizationSettlementService;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.organization.OrganizationSettlementInterface;

import io.swagger.annotations.Api;

/**
 * 机构结算 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/organizationSettlement")
@Api(description = "结算接口列表")
public class OrganizationSettlementController implements OrganizationSettlementInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public OrganizationSettlementService service;

	@Autowired
	private ProfileBusiness profileBusiness;

	@Override
	public Response<String> strategySettlement(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			@PathVariable String tradeNo, @PathVariable Long strategyTypeId, @PathVariable BigDecimal serviceFee,
			@PathVariable BigDecimal deferredFee) {
		logger.info("机构结算配资交易{}，策略类型{}，服务费{}，递延费{}!", buyRecordId, strategyTypeId, serviceFee, deferredFee);
		service.strategySettlement(publisherId, buyRecordId, tradeNo, strategyTypeId, serviceFee, deferredFee);
		Response<String> response = new Response<String>();
		response.setResult("success");
		return response;
	}

	@Override
	public Response<String> stockoptionSettlement(@PathVariable Long publisherId, @PathVariable Long stockOptionTradeId,
			@PathVariable String tradeNo, @PathVariable Long cycleId, BigDecimal rightMoneyProfit,
			BigDecimal rightMoney) {
		logger.info("机构结算期权交易{}，周期{}，平台权利金收益{}，权利金{}!", stockOptionTradeId, cycleId, rightMoneyProfit, rightMoney);
		service.stockoptionSettlement(publisherId, stockOptionTradeId, tradeNo, cycleId, rightMoneyProfit, rightMoney);
		Response<String> response = new Response<String>();
		response.setResult("success");
		return response;
	}

	@Override
	public Response<String> futuresSettlement(@PathVariable Long publisherId, @PathVariable Long commodityId,
			@PathVariable Long futuresOrderId, @PathVariable String tradeNo, @PathVariable BigDecimal totalQuantity,
			@PathVariable BigDecimal openingFee, @PathVariable BigDecimal closeFee) {
		logger.info("期货代理商结算订单{}，开仓手续费{}，平仓手续费{}!", futuresOrderId, openingFee, closeFee);
		service.futuresSettlement(publisherId, commodityId, futuresOrderId, tradeNo, totalQuantity, openingFee,
				closeFee);
		Response<String> response = new Response<String>();
		response.setResult("success");
		return response;
	}

	@Override
	public Response<String> futuresDeferredSettlement(@PathVariable Long publisherId, @PathVariable Long commodityId,
			@PathVariable Long overnightRecordId, @PathVariable String tradeNo, @PathVariable BigDecimal totalQuantity,
			@PathVariable BigDecimal deferredFee) {
		logger.info("期货代理商结算订单隔夜递延{}，递延费{}!", overnightRecordId, deferredFee);
		service.futuresDeferredSettlement(publisherId, commodityId, overnightRecordId, tradeNo, totalQuantity,
				deferredFee);
		Response<String> response = new Response<String>();
		response.setResult("success");
		return response;
	}

	@Override
	public Response<String> futuresRatioSettlement(@PathVariable Long publisherId, @PathVariable Long benefitResourceId,
			@PathVariable Long futuresOrderId, @PathVariable String tradeNo, @PathVariable BigDecimal totalQuantity,
			@PathVariable BigDecimal serviceFee, @PathVariable BigDecimal orderCloseFee,
			@PathVariable BigDecimal deferredFee) {
		logger.info("期货代理商结算订单{}，交易综合手续费{}，订单盈亏金额{}，递延费{}!", futuresOrderId, serviceFee, orderCloseFee, deferredFee);
		service.futuresRatioSettlement(publisherId, benefitResourceId, futuresOrderId, tradeNo, totalQuantity,
				serviceFee, orderCloseFee, deferredFee);
		Response<String> response = new Response<String>();
		response.setResult("success");
		return response;
	}

}
