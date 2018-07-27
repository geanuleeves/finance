package com.waben.stock.interfaces.service.futures;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.admin.futures.FuturesCommodityAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeTimeDto;
import com.waben.stock.interfaces.dto.admin.futures.SetSlipPointDto;
import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.dto.futures.FuturesStopLossOrProfitDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesCommodityAdminQuery;

@FeignClient(name = "futures", path = "commodity", qualifier = "futuresCommodityInterface")
public interface FuturesCommodityInterface {

	@RequestMapping(value = "/pages", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesCommodityAdminDto>> pagesAdmin(@RequestBody FuturesCommodityAdminQuery query);

	@RequestMapping(value = "/saveCommodity", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesCommodityAdminDto> save(@RequestBody FuturesCommodityAdminDto dto);

	@RequestMapping(value = "/modifCommodity", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesCommodityAdminDto> modify(@RequestBody FuturesCommodityAdminDto dto);

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<String> deleteCommodity(@PathVariable("id") Long id);

	@RequestMapping(value = "/tradeTime/{id}", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesCommodityAdminDto> queryTradeTime(@PathVariable("id") Long id);

	@RequestMapping(value = "/tradeTime/saveAndModify", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesCommodityAdminDto> saveAndModify(@RequestBody FuturesTradeTimeDto dto);

	@RequestMapping(value = "/isCurrency/{id}", method = RequestMethod.POST, consumes = "application/json")
	Response<String> isCurrency(@PathVariable("id") Long id);

	/**
	 * 根据品种ID获取品种数据
	 * 
	 * @param commodityId
	 *            品种ID
	 * @return 品种数据
	 */
	@RequestMapping(value = "/commodity/{commodityId}", method = RequestMethod.GET)
	Response<FuturesCommodityDto> getFuturesByCommodityId(@PathVariable("commodityId") Long commodityId);

	@RequestMapping(value = "/lists/{exchangeId}", method = RequestMethod.GET)
	Response<List<FuturesCommodityDto>> listByExchangeId(@PathVariable("exchangeId") Long exchangeId);

	/**
	 * 设置品种交易点差
	 * 
	 * @param dto
	 *            品种交易点差
	 * @return 品种
	 */
	@RequestMapping(value = "/setSlipPoint", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesCommodityAdminDto> setSlipPoint(@RequestBody SetSlipPointDto dto);

	/**
	 * 设置止损止盈
	 * 
	 * @param stopLossOrProfitDto
	 *            止损止盈实体
	 */
	@RequestMapping(value = "/save/lossOrProfit", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<Integer> saveLossOrProfit(@RequestBody List<FuturesStopLossOrProfitDto> lossOrProfitDto);

	/**
	 * 根据品种ID获取止损止盈列表
	 * 
	 * @param commodityId
	 *            品种ID
	 * @return 止损止盈列表
	 */
	@RequestMapping(value = "/loss/profit/{commodityId}", method = RequestMethod.GET)
	Response<List<FuturesStopLossOrProfitDto>> getLossOrProfits(@PathVariable("commodityId") Long commodityId);

	@RequestMapping(value = "/lossOrprofit/{id}", method = RequestMethod.GET)
	Response<FuturesStopLossOrProfitDto> getLossOrProfitsById(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/getTradingState/{id}", method = RequestMethod.GET)
	Response<Integer> getTradingState(@PathVariable("id") Long id);

}
