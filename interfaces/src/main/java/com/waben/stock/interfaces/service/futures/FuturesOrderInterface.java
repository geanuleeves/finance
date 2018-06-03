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
	 * 根据ID获取订单
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Response<FuturesOrderDto> fetchById(@PathVariable("id") Long id);

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
	 * 取消订单
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/cancelOrder/{id}", method = RequestMethod.GET)
	Response<FuturesOrderDto> cancelOrder(@PathVariable(name = "id") Long id);

	/**
	 * 用户申请平仓
	 * 
	 * @param orderId
	 *            订单ID
	 * @param sellingPriceTypeIndex
	 *            期货交易价格 类型
	 * @param sellingEntrustPrice
	 *            委托价格
	 * @return 订单
	 */
	@RequestMapping(value = "/applyUnwind/{id}", method = RequestMethod.PUT)
	Response<FuturesOrderDto> applyUnwind(@PathVariable("id") Long id,
			@RequestParam("sellingPriceTypeIndex") String sellingPriceTypeIndex,
			@RequestParam("sellingEntrustPrice") BigDecimal sellingEntrustPrice);

	/**
	 * 用户申请一键平仓所有订单
	 * 
	 * @param publisherId
	 *            用户ID
	 */
	@RequestMapping(value = "/applyUnwindAll/{publisherId}", method = RequestMethod.PUT)
	Response<Void> applyUnwindAll(@PathVariable("publisherId") Long publisherId);

	/**
	 * 用户市价反手
	 * 
	 * @param orderId
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/backhandUnwind/{id}", method = RequestMethod.PUT)
	Response<FuturesOrderDto> backhandUnwind(@PathVariable("id") Long id);

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

}
