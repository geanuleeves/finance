package com.waben.stock.interfaces.service.futures;

import com.waben.stock.interfaces.dto.futures.FuturesTradeActionDto;
import com.waben.stock.interfaces.pojo.Response;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 订单交易开平仓记录
 * 
 * @author chenk 2018/7/26
 */
@FeignClient(name = "futures", path = "trade_action", qualifier = "futuresTradeActionInterface")
public interface FuturesTradeActionInterface {

	/**
	 * 根据ID获取合约订单
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Response<FuturesTradeActionDto> fetchById(@PathVariable("id") Long id);

	/**
	 * 新增订单交易开平仓记录
	 *
	 * @param dto
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesTradeActionDto> save(@RequestBody FuturesTradeActionDto dto);

	/**
	 * 修改订单交易开平仓记录
	 *
	 * @param dto
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/modify", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesTradeActionDto> modify(@RequestBody FuturesTradeActionDto dto);

	/**
	 * 删除订单交易开平仓记录
	 *
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<String> delete(@PathVariable("id") Long id);

}
