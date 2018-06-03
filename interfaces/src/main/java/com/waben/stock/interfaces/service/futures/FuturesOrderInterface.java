package com.waben.stock.interfaces.service.futures;

import java.math.BigDecimal;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;

@FeignClient(name = "futures", path = "order", qualifier = "futuresOrderInterface")
public interface FuturesOrderInterface {

	/**
	 * 查询期货订单数据
	 * 
	 * @param orderQuery
	 *            查询条件
	 * @return 期货订单
	 */
	@RequestMapping(value = "/pages", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesOrderDto>> pagesOrder(@RequestBody FuturesOrderQuery orderQuery);

	/**
	 * 添加期货订单
	 * 
	 * @param futuresOrderDto
	 *            订单数据
	 * @return 期货订单
	 */
	@RequestMapping(value = "/addOrder", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesOrderDto> addOrder(@RequestBody FuturesOrderDto futuresOrderDto);

	/**
	 * 获取每个合约的买量 卖量数
	 * 
	 * @param state
	 *            1 买涨， 2 买跌
	 * @param contractId
	 *            合约ID
	 * @return 买量 卖量数
	 */
	@RequestMapping(value = "/count/order/type", method = RequestMethod.GET)
	Response<Integer> countOrderType(@RequestParam(name = "contractId") Long contractId,
			@RequestParam(name = "orderType") FuturesOrderType orderType);

	/**
	 * 根据合约ID和用户ID获取用户购买该合约总数
	 * 
	 * @param contractId
	 *            合约ID
	 * @param publisherId
	 *            用户ID
	 * @return 合约总数
	 */
	@RequestMapping(value = "/sum/{contractId}/{publisherId}", method = RequestMethod.GET)
	Response<Integer> sumByListOrderContractIdAndPublisherId(@PathVariable(name = "contractId") Long contractId,
			@PathVariable(name = "publisherId") Long publisherId);

	/**
	 * 取消订单
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/cancelOrder/{id}", method = RequestMethod.GET)
	Response<FuturesOrderDto> cancelOrder(@PathVariable(name = "id") Long id);

	/**
	 * 设置止盈止损
	 * 
	 * @param orderId
	 *            订单ID
	 * @param limitProfitType
	 *            触发止盈类型
	 * @param perUnitLimitProfitAmount
	 *            止盈金额
	 * @param limitLossType
	 *            触发止损类型
	 * @param perUnitLimitLossAmount
	 *            止损金额
	 * @return 订单
	 */
	@RequestMapping(value = "/edit/order/{orderId}", method = RequestMethod.POST)
	Response<FuturesOrderDto> editOrder(@PathVariable("orderId") Long orderId,
			@RequestParam("limitProfitType") Integer limitProfitType,
			@RequestParam("perUnitLimitProfitAmount") BigDecimal perUnitLimitProfitAmount,
			@RequestParam("limitLossType") Integer limitLossType,
			@RequestParam("perUnitLimitLossAmount") BigDecimal perUnitLimitLossAmount);

}
