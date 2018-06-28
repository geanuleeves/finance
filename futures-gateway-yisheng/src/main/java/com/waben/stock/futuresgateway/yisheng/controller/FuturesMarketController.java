package com.waben.stock.futuresgateway.yisheng.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.futuresgateway.yisheng.pojo.FuturesContractLineData;
import com.waben.stock.futuresgateway.yisheng.pojo.FuturesQuoteData;
import com.waben.stock.futuresgateway.yisheng.pojo.Response;
import com.waben.stock.futuresgateway.yisheng.service.FuturesMarketService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货合约行情
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/market")
@Api(description = "期货合约行情接口列表")
public class FuturesMarketController {

	@Autowired
	public FuturesMarketService service;

	@GetMapping("/{commodityNo}/{contractNo}")
	@ApiOperation(value = "期货合约行情")
	public Response<FuturesQuoteData> market(@PathVariable("commodityNo") String commodityNo,
			@PathVariable("contractNo") String contractNo) {
		return new Response<>(service.quote(commodityNo, contractNo));
	}

	@GetMapping("/{commodityNo}/{contractNo}/dayline")
	@ApiOperation(value = "期货合约日K线", notes = "startTime和endTime格式为:yyyy-MM-DD HH:mm:ss")
	public Response<List<FuturesContractLineData>> dayLine(@PathVariable("commodityNo") String commodityNo,
			@PathVariable("contractNo") String contractNo, String startTime, String endTime) {
		return new Response<>(service.dayLine(commodityNo, contractNo, startTime, endTime));
	}

	@GetMapping("/{commodityNo}/{contractNo}/minsline")
	@ApiOperation(value = "期货合约分K线", notes = "startTime和endTime格式为:yyyy-MM-DD HH:mm:ss，不设置值默认为1天")
	public Response<List<FuturesContractLineData>> minsLine(@PathVariable("commodityNo") String commodityNo,
			@PathVariable("contractNo") String contractNo, String startTime, String endTime, Integer mins) {
		if(mins == null || mins < 1) {
			mins = 1;
		}
		return new Response<>(service.minsLine(commodityNo, contractNo, startTime, endTime, mins));
	}
	
	@GetMapping("/{commodityNo}/{contractNo}/computedayline")
	@ApiOperation(value = "计算日K数据")
	public Response<String> computeDayline(@PathVariable("commodityNo") String commodityNo,
			@PathVariable("contractNo") String contractNo, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			service.computeDayline(commodityNo, contractNo, sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Response<String> result = new Response<>();
		result.setResult("success");
		return result;
	}

}
