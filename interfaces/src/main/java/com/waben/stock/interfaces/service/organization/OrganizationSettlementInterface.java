package com.waben.stock.interfaces.service.organization;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.pojo.Response;

/**
 * 机构结算 reference服务接口
 *
 * @author luomengan
 */
@FeignClient(name = "organization", path = "organizationSettlement", qualifier = "organizationSettlementInterface")
public interface OrganizationSettlementInterface {

	@RequestMapping(value = "/strategysettlement/{publisherId}/{buyRecordId}/{tradeNo}/{strategyTypeId}/{serviceFee}/{deferredFee}", method = RequestMethod.POST)
	public Response<String> strategySettlement(@PathVariable("publisherId") Long publisherId,
			@PathVariable("buyRecordId") Long buyRecordId, @PathVariable("tradeNo") String tradeNo,
			@PathVariable("strategyTypeId") Long strategyTypeId, @PathVariable("serviceFee") BigDecimal serviceFee,
			@PathVariable("deferredFee") BigDecimal deferredFee);

	@RequestMapping(value = "stockoptionsettlement/{publisherId}/{stockOptionTradeId}/{tradeNo}/{cycleId}", method = RequestMethod.POST)
	public Response<String> stockoptionSettlement(@PathVariable("publisherId") Long publisherId,
			@PathVariable("stockOptionTradeId") Long stockOptionTradeId, @PathVariable("tradeNo") String tradeNo,
			@PathVariable("cycleId") Long cycleId, @RequestParam("rightMoneyProfit") BigDecimal rightMoneyProfit,
			@RequestParam("rightMoney") BigDecimal rightMoney);

	@RequestMapping(value = "/futuressettlement/{publisherId}/{futuresOrderId}/{tradeNo}/{futuresId}/{openingFee}/{closeFee}/{deferredFee}", method = RequestMethod.POST)
	public Response<String> futuresSettlement(@PathVariable("publisherId") Long publisherId,
			@PathVariable("futuresOrderId") Long futuresOrderId, @PathVariable("tradeNo") String tradeNo,
			@PathVariable("futuresId") Long futuresId, @PathVariable("openingFee") BigDecimal openingFee,
			@PathVariable("closeFee") BigDecimal closeFee, @PathVariable("deferredFee") BigDecimal deferredFee);

}
