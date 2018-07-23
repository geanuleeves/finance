package com.waben.stock.applayer.tactics.controller.futures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.ProfileBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesContractBusiness;
import com.waben.stock.applayer.tactics.dto.futures.FuturesContractMarketDto;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractLineData;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 期货合约行情
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/futuresMarket")
@Api(description = "期货合约行情接口列表")
public class FuturesMarketController {

	@Autowired
	private FuturesContractBusiness futuresContractBusiness;

	@Autowired
	private ProfileBusiness profileBusiness;

	@GetMapping("/{symbol}/{contractNo}")
	@ApiOperation(value = "期货合约行情")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "symbol", value = "品种代码", dataType = "string", paramType = "path", required = true),
			@ApiImplicitParam(name = "contractNo", value = "合约编号", dataType = "string", paramType = "path", required = true) })
	public Response<FuturesContractMarketDto> market(@PathVariable("symbol") String symbol,
			@PathVariable("contractNo") String contractNo) {
		FuturesContractQuery query = new FuturesContractQuery();
		query.setPage(0);
		query.setSize(1);
		query.setSymbol(symbol);
		List<FuturesContractDto> contractList = futuresContractBusiness.pagesContract(query).getContent();
		FuturesContractMarketDto marketDto = CopyBeanUtils.copyBeanProperties(FuturesContractMarketDto.class,
				RetriveFuturesOverHttp.market(profileBusiness.isProd(), symbol, contractNo), false);
		FuturesContractDto contractPage = null;
		if (contractList != null && contractList.size() > 0) {
			contractPage = contractList.get(0);
			marketDto.setContractName(contractPage.getName());
			marketDto.setCurrencySign(contractPage.getCurrencySign());
			marketDto.setContractState(contractPage.getState());
			if (contractPage.getCurrentHoldingTime() != null) {
				contractPage.setCurrentHoldingTime(
						timeZoneConversion(contractPage.getTimeZoneGap(), contractPage.getCurrentHoldingTime()));
			}
			if (contractPage.getNextTradingTime() != null) {
				contractPage.setNextTradingTime(
						timeZoneConversion(contractPage.getTimeZoneGap(), contractPage.getNextTradingTime()));
			}
		}

		return new Response<>(marketDto);
	}

	@GetMapping("/{symbol}/{contractNo}/timeline")
	@ApiOperation(value = "期货合约分时图")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "symbol", value = "品种代码", dataType = "string", paramType = "path", required = true),
			@ApiImplicitParam(name = "contractNo", value = "合约编号", dataType = "string", paramType = "path", required = true) })
	public Response<List<FuturesContractLineData>> timeLine(@PathVariable("symbol") String symbol,
			@PathVariable("contractNo") String contractNo) {
		return new Response<>(RetriveFuturesOverHttp.timeLine(profileBusiness.isProd(), symbol, contractNo));
	}

	@GetMapping("/{symbol}/{contractNo}/dayline")
	@ApiOperation(value = "期货合约日K线", notes = "startTime和endTime格式为:yyyy-MM-DD HH:mm:ss")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "symbol", value = "品种代码", dataType = "string", paramType = "path", required = true),
			@ApiImplicitParam(name = "contractNo", value = "合约编号", dataType = "string", paramType = "path", required = true),
			@ApiImplicitParam(name = "startTime", value = "开始时间", dataType = "string", paramType = "query", required = false),
			@ApiImplicitParam(name = "endTime", value = "结束时间", dataType = "string", paramType = "query", required = false) })
	public Response<List<FuturesContractLineData>> dayLine(@PathVariable("symbol") String symbol,
			@PathVariable("contractNo") String contractNo, String startTime, String endTime) {
		return new Response<>(
				RetriveFuturesOverHttp.dayLine(profileBusiness.isProd(), symbol, contractNo, startTime, endTime));
	}

	@GetMapping("/{symbol}/{contractNo}/minsline")
	@ApiOperation(value = "期货合约分K线")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "symbol", value = "品种代码", dataType = "string", paramType = "path", required = true),
			@ApiImplicitParam(name = "contractNo", value = "合约编号", dataType = "string", paramType = "path", required = true),
			@ApiImplicitParam(name = "mins", value = "分钟", dataType = "integer", paramType = "query", required = true) })
	public Response<List<FuturesContractLineData>> minsLine(@PathVariable("symbol") String symbol,
			@PathVariable("contractNo") String contractNo, Integer mins, String startTime, String endTime) {
		return new Response<>(RetriveFuturesOverHttp.minsLine(profileBusiness.isProd(), symbol, contractNo, mins, startTime, endTime));
	}

	/**
	 * 将时间转成国内时间
	 * 
	 * @param timeZoneGap
	 *            时差
	 * @param time
	 *            国外时间
	 * @return 国内时间
	 */
	private String timeZoneConversion(Integer timeZoneGap, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String timeStr = "";
		try {
			if (StringUtil.isEmpty(time)) {
				return "";
			}
			timeStr = sdf.format(retriveExchangeTime(sdf.parse(time), timeZoneGap));
		} catch (ParseException e) {
			return "";
		}

		return timeStr;
	}

	private Date retriveExchangeTime(Date localTime, Integer timeZoneGap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.HOUR_OF_DAY, timeZoneGap);
		return cal.getTime();
	}

}
