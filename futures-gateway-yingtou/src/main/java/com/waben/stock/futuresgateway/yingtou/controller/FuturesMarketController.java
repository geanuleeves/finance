package com.waben.stock.futuresgateway.yingtou.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;
import com.waben.stock.futuresgateway.yingtou.pojo.FuturesContractLineData;
import com.waben.stock.futuresgateway.yingtou.pojo.Response;
import com.waben.stock.futuresgateway.yingtou.service.FuturesMarketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货合约行情
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/market")
@Api(description = "期货合约行情接口列表")
public class FuturesMarketController {

	@Autowired
	public FuturesMarketService service;

	@GetMapping("/{symbol}")
	@ApiOperation(value = "期货合约行情")
	public Response<FuturesContract> market(@PathVariable("symbol") String symbol) {
		return new Response<>(service.market(symbol));
	}

	@GetMapping("/{symbol}/timeline")
	@ApiOperation(value = "期货合约分时图", notes = "dayCount表示统计的天数，不设置值默认为1天")
	public Response<List<FuturesContractLineData>> timeLine(@PathVariable("symbol") String symbol, Integer dayCount) {
		return new Response<>(service.timeLine(symbol, dayCount));
	}

	@GetMapping("/{symbol}/dayline")
	@ApiOperation(value = "期货合约日K线", notes = "startTime和endTime格式为:yyyy-MM-DD HH:mm:ss")
	public Response<List<FuturesContractLineData>> dayLine(@PathVariable("symbol") String symbol, String startTime,
			String endTime) {
		return new Response<>(service.dayLine(symbol, startTime, endTime));
	}

	@GetMapping("/{symbol}/minsline")
	@ApiOperation(value = "期货合约分K线", notes = "mins表示分钟， dayCount表示统计的天数，不设置值默认为1天")
	public Response<List<FuturesContractLineData>> minsLine(@PathVariable("symbol") String symbol, Integer mins,
			Integer dayCount) {
		return new Response<>(service.minsLine(symbol, mins, dayCount));
	}

}
