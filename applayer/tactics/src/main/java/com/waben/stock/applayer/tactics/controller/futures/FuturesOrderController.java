package com.waben.stock.applayer.tactics.controller.futures;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.CapitalAccountBusiness;
import com.waben.stock.applayer.tactics.business.PublisherBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesContractBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesOrderBusiness;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderBuysellDto;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderDayGainLossDto;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderMarketDto;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderProfitDto;
import com.waben.stock.applayer.tactics.dto.futures.TransactionDynamicsDto;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.applayer.tactics.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
import com.waben.stock.interfaces.dto.futures.FuturesStopLossOrProfitDto;
import com.waben.stock.interfaces.dto.futures.TurnoverStatistyRecordDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 期货订单
 * 
 * @author sl
 *
 */
@RestController
@RequestMapping("/futuresOrder")
@Api(description = "期货订单")
public class FuturesOrderController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderBusiness futuresOrderBusiness;

	@Autowired
	private FuturesContractBusiness futuresContractBusiness;

	@Autowired
	private PublisherBusiness publisherBusiness;

	@Autowired
	private CapitalAccountBusiness capitalAccountBusiness;

	@Autowired
	private FuturesOrderBusiness orderBusiness;

	// private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat exprotSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@PostMapping("/buy")
	@ApiOperation(value = "买涨买跌下单")
	public Response<FuturesOrderDto> placeOrder(FuturesOrderBuysellDto buysellDto) {
		FuturesContractQuery query = new FuturesContractQuery();
		query.setPage(0);
		query.setSize(1);
		query.setContractId(buysellDto.getContractId());
		// 判断该合约是否可用是否在交易中、网关是否支持该合约、合约交易所是否可用、以及是否在交易时间内
		FuturesContractDto contractDto = futuresContractBusiness.getContractByOne(query);
		futuresContractBusiness.wrapperAgentPrice(contractDto);
		// 根据最后交易日和首次通知日判断是否可以下单，可以下单计算公式：MIN（最后交易日，首次通知日）> 当前日期
		checkedMinPlaceOrder(contractDto);
		// 用户单笔最大可交易数量
		BigDecimal perNum = contractDto.getPerOrderLimit();
		// 用户最大可持仓量
		BigDecimal userMaxNum = contractDto.getUserTotalLimit();
		// 用户买涨持仓总额度
		Integer buyUp = futuresOrderBusiness.sumUserNum(buysellDto.getContractId(), SecurityUtil.getUserId(), 1);
		BigDecimal buyUpNum = buyUp == null ? new BigDecimal(0) : new BigDecimal(buyUp).abs();
		// 用户买跌持仓总额度
		Integer buyFull = futuresOrderBusiness.sumUserNum(buysellDto.getContractId(), SecurityUtil.getUserId(), 2);
		BigDecimal buyFullNum = buyFull == null ? new BigDecimal(0) : new BigDecimal(buyFull).abs();

		// 判断当前下单手数是否满足条件
		checkBuyUpAndFullSUM(buyUpNum, buyFullNum, perNum, userMaxNum, buysellDto, contractDto);

		// 总金额
		BigDecimal totalFee = new BigDecimal(0);

		// 获取运营后台设置的止损止盈
		if (buysellDto.getStopLossOrProfitId() == null) {
			throw new ServiceException(ExceptionConstant.SETTING_STOP_LOSS_EXCEPTION);
		}
		FuturesStopLossOrProfitDto lossOrProfitDto = futuresOrderBusiness
				.getLossOrProfitsById(buysellDto.getStopLossOrProfitId());
		if (lossOrProfitDto == null) {
			throw new ServiceException(ExceptionConstant.SETTING_STOP_LOSS_EXCEPTION);
		}
		// 保证金金额
		BigDecimal reserveAmount = lossOrProfitDto.getReserveFund().multiply(buysellDto.getTotalQuantity())
				.multiply(contractDto.getRate());
		// 开仓手续费 + 平仓手续费
		BigDecimal openUnwin = contractDto.getOpenwindServiceFee().add(contractDto.getUnwindServiceFee());
		// 交易综合费 = (开仓手续费 + 平仓手续费)* 交易持仓数
		BigDecimal comprehensiveAmount = openUnwin.multiply(buysellDto.getTotalQuantity());
		// 总金额 = 保证金金额 + 交易综合费
		totalFee = reserveAmount.add(comprehensiveAmount);

		// 检查余额
		CapitalAccountDto capitalAccount = futuresContractBusiness.findByPublisherId(SecurityUtil.getUserId());
		if (totalFee.compareTo(capitalAccount.getAvailableBalance()) > 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		BigDecimal unsettledProfitOrLoss = futuresOrderBusiness.getUnsettledProfitOrLoss(SecurityUtil.getUserId());
		if (unsettledProfitOrLoss != null && unsettledProfitOrLoss.compareTo(BigDecimal.ZERO) < 0) {
			if (totalFee.add(unsettledProfitOrLoss.abs()).compareTo(capitalAccount.getAvailableBalance()) > 0) {
				throw new ServiceException(ExceptionConstant.HOLDINGLOSS_LEADTO_NOTENOUGH_EXCEPTION);
			}
		}

		FuturesOrderDto orderDto = new FuturesOrderDto();
		orderDto.setPublisherId(SecurityUtil.getUserId());
		orderDto.setOrderType(buysellDto.getOrderType());
		orderDto.setContractId(buysellDto.getContractId());
		orderDto.setTotalQuantity(buysellDto.getTotalQuantity());
		orderDto.setReserveFund(reserveAmount);
		orderDto.setServiceFee(comprehensiveAmount);
		orderDto.setCommoditySymbol(contractDto.getSymbol());
		orderDto.setCommodityName((contractDto.getName()));
		orderDto.setCommodityCurrency(contractDto.getCurrency());
		orderDto.setContractNo(contractDto.getContractNo());
		orderDto.setOpenwindServiceFee(contractDto.getOpenwindServiceFee());
		orderDto.setUnwindServiceFee(contractDto.getUnwindServiceFee());
		orderDto.setPerUnitUnwindPoint(lossOrProfitDto.getStrongLevelingAmount());
		orderDto.setDefaultStopLossFee(lossOrProfitDto.getStopLossFee());
		orderDto.setUnwindPointType(2);
		orderDto.setOvernightPerUnitReserveFund(contractDto.getOvernightPerUnitReserveFund());
		orderDto.setOvernightPerUnitDeferredFee(contractDto.getOvernightPerUnitDeferredFee());
		orderDto.setBuyingPriceType(buysellDto.getBuyingPriceType());
		// 止损类型及金额点位
		if (buysellDto.getLimitLossType() != null && buysellDto.getLimitLossType() > 0) {
			orderDto.setLimitLossType(buysellDto.getLimitLossType());
			orderDto.setPerUnitLimitLossAmount(buysellDto.getPerUnitLimitLossAmount());
		}
		// 止盈类型及金额点位
		if (buysellDto.getLimitProfitType() != null && buysellDto.getLimitProfitType() > 0) {
			orderDto.setLimitProfitType(buysellDto.getLimitProfitType());
			orderDto.setPerUnitLimitProfitAmount(buysellDto.getPerUnitLimitProfitAmount());
		}
		// 委托买入价格
		orderDto.setBuyingEntrustPrice(buysellDto.getBuyingEntrustPrice());
		// 获取是否为测试单
		PublisherDto publisher = publisherBusiness.findById(SecurityUtil.getUserId());
		orderDto.setIsTest(publisher.getIsTest());
		orderDto.setStopLossOrProfitId(buysellDto.getStopLossOrProfitId());
		return new Response<>(futuresOrderBusiness.buy(orderDto));
	}

	@PostMapping("/cancelOrder/{orderId}")
	@ApiOperation(value = "用户取消订单委托")
	public Response<FuturesOrderDto> cancelOrder(@PathVariable Long orderId) {
		return new Response<>(futuresOrderBusiness.cancelOrder(orderId, SecurityUtil.getUserId()));
	}

	@PostMapping("/applyUnwind/{orderId}")
	@ApiOperation(value = "用户申请平仓")
	public Response<FuturesOrderDto> applyUnwind(@PathVariable Long orderId,
			@RequestParam(required = true) FuturesTradePriceType sellingPriceType, BigDecimal sellingEntrustPrice) {
		return new Response<>(futuresOrderBusiness.applyUnwind(orderId, sellingPriceType, sellingEntrustPrice,
				SecurityUtil.getUserId()));
	}

	@PostMapping("/applyUnwindAll")
	@ApiOperation(value = "用户申请一键平仓所有订单")
	public Response<String> applyUnwindAll() {
		futuresOrderBusiness.applyUnwindAll(SecurityUtil.getUserId());
		Response<String> result = new Response<>();
		result.setResult("success");
		return result;
	}

	@PostMapping("/backhandUnwind/{orderId}")
	@ApiOperation(value = "用户市价反手")
	public Response<FuturesOrderDto> backhandUnwind(@PathVariable Long orderId) {
		FuturesOrderDto orderDto = futuresOrderBusiness.fetchByOrderId(orderId);
		FuturesContractQuery query = new FuturesContractQuery();
		query.setPage(0);
		query.setSize(1);
		query.setContractId(orderDto.getContractId());
		// 判断该合约是否可用是否在交易中、网关是否支持该合约、合约交易所是否可用、以及是否在交易时间内
		FuturesContractDto contractDto = futuresContractBusiness.getContractByOne(query);
		checkedMinPlaceOrder(contractDto);
		// 用户买涨持仓总额度
		Integer buyUp = futuresOrderBusiness.sumUserNum(orderDto.getContractId(), SecurityUtil.getUserId(), 1);
		BigDecimal buyUpNum = buyUp == null ? new BigDecimal(0) : new BigDecimal(buyUp).abs();
		// 用户买跌持仓总额度
		Integer buyFull = futuresOrderBusiness.sumUserNum(orderDto.getContractId(), SecurityUtil.getUserId(), 2);
		BigDecimal buyFullNum = buyFull == null ? new BigDecimal(0) : new BigDecimal(buyFull).abs();
		if (orderDto.getOrderType() == FuturesOrderType.BuyUp) {
			if (contractDto.getBuyFullTotalLimit() != null
					&& buyFullNum.add(orderDto.getTotalQuantity()).compareTo(contractDto.getBuyFullTotalLimit()) > 0) {
				// 买跌持仓总额度已达上限
				throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYFULL_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
		} else {
			if (contractDto.getBuyUpTotalLimit() != null
					&& buyUpNum.add(orderDto.getTotalQuantity()).compareTo(contractDto.getBuyUpTotalLimit()) > 0) {
				// 买涨持仓总额度已达上限
				throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYUP_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
		}
		FuturesOrderBuysellDto buysellDto = new FuturesOrderBuysellDto();
		buysellDto.setTotalQuantity(orderDto.getTotalQuantity());
		buysellDto.setOrderType(orderDto.getOrderType());
		// 判断当前下单手数是否满足条件
		checkBuyUpAndFullSUM(buyUpNum, buyFullNum, contractDto.getPerOrderLimit(), contractDto.getUserTotalLimit(),
				buysellDto, contractDto);

		return new Response<>(futuresOrderBusiness.backhandUnwind(orderId, SecurityUtil.getUserId()));
	}

	@GetMapping("/holding")
	@ApiOperation(value = "获取持仓中列表")
	public Response<PageInfo<FuturesOrderMarketDto>> holdingList(int page, int size) {
		// long startTime = System.currentTimeMillis();
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		Response<PageInfo<FuturesOrderMarketDto>> result = new Response<>(
				futuresOrderBusiness.pageOrderMarket(orderQuery));
		// logger.info("持仓列表耗时：" + (System.currentTimeMillis() - startTime));
		return result;
	}

	@GetMapping("/entrustQueuing")
	@ApiOperation(value = "获取委托排队中列表")
	public Response<PageInfo<FuturesOrderMarketDto>> entrustQueuing(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.BuyingEntrust, FuturesOrderState.SellingEntrust };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@GetMapping("/entrustment")
	@ApiOperation(value = "获取委托中列表")
	public Response<PageInfo<FuturesOrderMarketDto>> entrustmentList(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.BuyingEntrust, FuturesOrderState.BuyingCanceled,
				FuturesOrderState.BuyingFailure, FuturesOrderState.PartPosition, FuturesOrderState.Position,
				FuturesOrderState.SellingEntrust, FuturesOrderState.PartUnwind, FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@GetMapping("/turnover")
	@ApiOperation(value = "获取成交记录列表（包括持仓中、已结算订单）")
	public Response<PageInfo<FuturesOrderMarketDto>> turnoverList(int page, int size, String contractName,
			String startTime, String endTime) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position, FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setContractName(contractName);
		if (!StringUtil.isEmpty(startTime)) {
			try {
				orderQuery.setStartBuyingTime(exprotSdf.parse(startTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(endTime)) {
			try {
				orderQuery.setEndBuyingTime(exprotSdf.parse(endTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@GetMapping("/settled")
	@ApiOperation(value = "获取已结算列表")
	public Response<PageInfo<FuturesOrderMarketDto>> settledList(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Unwind, FuturesOrderState.BuyingCanceled,
				FuturesOrderState.BuyingFailure };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@GetMapping("/holding/profit")
	@ApiOperation(value = "获取持仓中总收益")
	public Response<FuturesOrderProfitDto> holdingProfit(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		BigDecimal rate = BigDecimal.ZERO;
		String sign = "";
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss() == null ? new BigDecimal(0)
					: futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		if (list != null && list.size() > 0) {
			sign = list.get(0).getCurrencySign();
			rate = list.get(0).getRate();
		}
		// 获取用户账户资金
		CapitalAccountDto capital = capitalAccountBusiness.findByPublisherId(SecurityUtil.getUserId());
		FuturesOrderProfitDto result = new FuturesOrderProfitDto();
		result.setTotalIncome(totalIncome.setScale(2, RoundingMode.DOWN));
		result.setRate(rate.setScale(2, RoundingMode.DOWN));
		result.setCurrencySign(sign);
		result.setTotalBalance(capital.getAvailableBalance()
				.add(futuresOrderBusiness.totalBalance(0, Integer.MAX_VALUE)).setScale(2, RoundingMode.DOWN));
		return new Response<>(result);
	}

	@GetMapping("/entrustment/profit")
	@ApiOperation(value = "获取委托中总收益")
	public Response<FuturesOrderProfitDto> entrustmentProfit(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.BuyingEntrust, FuturesOrderState.BuyingCanceled,
				FuturesOrderState.BuyingFailure, FuturesOrderState.PartPosition, FuturesOrderState.Position,
				FuturesOrderState.SellingEntrust, FuturesOrderState.PartUnwind, FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		BigDecimal rate = BigDecimal.ZERO;
		String sign = "";
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss() == null ? new BigDecimal(0)
					: futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		if (list != null && list.size() > 0) {
			sign = list.get(0).getCurrencySign();
			rate = list.get(0).getRate();
		}

		FuturesOrderProfitDto result = new FuturesOrderProfitDto();
		result.setTotalIncome(totalIncome.setScale(2, RoundingMode.DOWN));
		result.setRate(rate.setScale(2, RoundingMode.DOWN));
		result.setCurrencySign(sign);
		return new Response<>(result);
	}

	@GetMapping("/unwinded")
	@ApiOperation(value = "获取已平仓列表")
	public Response<PageInfo<FuturesOrderMarketDto>> unwindedList(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@GetMapping("/settled/profit")
	@ApiOperation(value = "获取已结算总收益")
	public Response<FuturesOrderProfitDto> settledProfit(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		String sign = "";
		BigDecimal rate = BigDecimal.ZERO;
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss() == null ? new BigDecimal(0)
					: futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		if (list != null && list.size() > 0) {
			sign = list.get(0).getCurrencySign();
			rate = list.get(0).getRate();
		}

		FuturesOrderProfitDto result = new FuturesOrderProfitDto();
		result.setTotalIncome(totalIncome.setScale(2, RoundingMode.DOWN));
		result.setRate(rate.setScale(2, RoundingMode.DOWN));
		result.setCurrencySign(sign);
		return new Response<>(result);
	}

	@GetMapping("/detail/{orderId}")
	@ApiOperation(value = "获取期货订单已结算详细信息")
	@ApiImplicitParam(paramType = "path", dataType = "Long", name = "orderId", value = "期货订单ID", required = true)
	public Response<FuturesOrderMarketDto> getOrderSettldDetails(@PathVariable("orderId") Long orderId) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		orderQuery.setPage(0);
		orderQuery.setSize(1);
		orderQuery.setOrderId(orderId);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		PageInfo<FuturesOrderMarketDto> withMarketPage = futuresOrderBusiness.pageOrderMarket(orderQuery);
		if (withMarketPage.getContent().size() > 0) {
			return new Response<>(withMarketPage.getContent().get(0));
		} else {
			return new Response<>();
		}
	}

	@PostMapping("/settingStopLoss/{orderId}")
	@ApiOperation(value = "设置止损止盈")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "Long", paramType = "path", required = true),
			@ApiImplicitParam(name = "limitProfitType", value = "止盈类型", dataType = "int", paramType = "query", required = false),
			@ApiImplicitParam(name = "perUnitLimitProfitAmount", value = "止盈金额", dataType = "BigDecimal", paramType = "query", required = false),
			@ApiImplicitParam(name = "limitLossType", value = "止损类型", dataType = "int", paramType = "query", required = false),
			@ApiImplicitParam(name = "perUnitLimitLossAmount", value = "止损金额", dataType = "BigDecimal", paramType = "query", required = false),
			@ApiImplicitParam(name = "stopLossOrProfitId", value = "档位ID", dataType = "Long", paramType = "query", required = true) })
	public Response<Integer> editOrder(@PathVariable Long orderId, Integer limitProfitType,
			BigDecimal perUnitLimitProfitAmount, Integer limitLossType, BigDecimal perUnitLimitLossAmount,
			Long stopLossOrProfitId) {
		return new Response<>(futuresOrderBusiness.settingStopLoss(orderId, limitProfitType, perUnitLimitProfitAmount,
				limitLossType, perUnitLimitLossAmount, SecurityUtil.getUserId(), stopLossOrProfitId));
	}

	@GetMapping("/turnover/statisty/record")
	@ApiOperation(value = "获取成交统计记录")
	public Response<TurnoverStatistyRecordDto> getTurnoverStatistyRecord() {
		return new Response<>(futuresOrderBusiness.getTurnoverStatistyRecord(SecurityUtil.getUserId()));
	}

	@GetMapping("/day/holding/profit")
	@ApiOperation(value = "获取当天持仓浮动盈亏")
	public Response<BigDecimal> dayHoldingProfit() {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position };
		orderQuery.setStates(states);
		orderQuery.setPage(0);
		orderQuery.setSize(Integer.MAX_VALUE);
		orderQuery.setStartBuyingTime(getCurrentDay());
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		return new Response<>(totalIncome.setScale(2, RoundingMode.DOWN));
	}

	@GetMapping("/day/settled/profit")
	@ApiOperation(value = "获取当天平仓盈亏")
	public Response<BigDecimal> daySettledProfit() {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(0);
		orderQuery.setSize(Integer.MAX_VALUE);
		orderQuery.setStartBuyingTime(getCurrentDay());
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		return new Response<>(totalIncome.setScale(2, RoundingMode.DOWN));
	}

	/**
	 * 包装当前用户的账户资金及当天持仓、平仓盈亏
	 * 
	 * @return 当前用户的账户资金及当天持仓、平仓盈亏
	 */
	@GetMapping("/current/capital/profit/settled/")
	@ApiOperation(value = "获取当前用户的账户资金及当天持仓、平仓盈亏")
	public Response<FuturesOrderDayGainLossDto> capitalAccountAndDayGainLoss() {
		FuturesOrderDayGainLossDto gainLoss = new FuturesOrderDayGainLossDto();
		// 获取当天平仓金额
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(0);
		orderQuery.setSize(Integer.MAX_VALUE);
		orderQuery.setStartBuyingTime(getCurrentDay());
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		gainLoss.setUnwindProFee(totalIncome.setScale(2, RoundingMode.DOWN));

		// 获取持仓浮动盈亏
		FuturesOrderQuery positionOrderQuery = new FuturesOrderQuery();
		FuturesOrderState[] positionState = { FuturesOrderState.Position };
		positionOrderQuery.setStates(positionState);
		positionOrderQuery.setPage(0);
		positionOrderQuery.setSize(Integer.MAX_VALUE);
		positionOrderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> positionList = futuresOrderBusiness.pageOrderMarket(positionOrderQuery)
				.getContent();
		BigDecimal positionTotalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : positionList) {
			positionTotalIncome = positionTotalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss() == null
					? new BigDecimal(0) : futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		gainLoss.setPositionFee(positionTotalIncome.setScale(2, RoundingMode.DOWN));

		// 获取用户账户资金
		CapitalAccountDto result = capitalAccountBusiness.findByPublisherId(SecurityUtil.getUserId());
		BigDecimal unsettledProfitOrLoss = orderBusiness.getUnsettledProfitOrLoss(SecurityUtil.getUserId());
		if (unsettledProfitOrLoss != null && unsettledProfitOrLoss.compareTo(BigDecimal.ZERO) < 0) {
			if (unsettledProfitOrLoss.abs().compareTo(result.getAvailableBalance()) > 0) {
				gainLoss.setFloatAvailableBalance(BigDecimal.ZERO);
			} else {
				gainLoss.setFloatAvailableBalance(result.getAvailableBalance().stripTrailingZeros()
						.subtract(unsettledProfitOrLoss.abs().stripTrailingZeros()));
			}
		} else {
			gainLoss.setFloatAvailableBalance(result.getAvailableBalance().stripTrailingZeros());
		}
		result.setPaymentPassword(null);
		gainLoss.setTotalBalance(result.getAvailableBalance()
				.add(futuresOrderBusiness.totalBalance(0, Integer.MAX_VALUE)).setScale(2, RoundingMode.DOWN));
		gainLoss.setAvailableBalance(result.getAvailableBalance());
		gainLoss.setFrozenCapital(result.getFrozenCapital());
		return new Response<>(gainLoss);
	}

	@GetMapping("/transaction/dynamics")
	@ApiOperation(value = "获取交易动态")
	public Response<PageInfo<TransactionDynamicsDto>> transactionDynamics(int page, int size) {
		return new Response<>(futuresOrderBusiness.transactionDynamics(page, size));

	}

	@GetMapping(value = "/export")
	@ApiOperation(value = "导出期货订单成交记录")
	public void export(String contractName, String startTime, String endTime, HttpServletResponse svrResponse) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position, FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(0);
		orderQuery.setSize(Integer.MAX_VALUE);
		orderQuery.setContractName(contractName);
		if (!StringUtil.isEmpty(startTime)) {
			try {
				orderQuery.setStartBuyingTime(exprotSdf.parse(startTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(endTime)) {
			try {
				orderQuery.setEndBuyingTime(exprotSdf.parse(endTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		PageInfo<FuturesOrderMarketDto> result = futuresOrderBusiness.pageOrderMarket(orderQuery);
		File file = null;
		FileInputStream is = null;
		try {
			String fileName = "turnover_" + String.valueOf(System.currentTimeMillis());
			file = File.createTempFile(fileName, ".xls");
			List<String> columnDescList = columnDescList();
			List<List<String>> dataList = dataList(result.getContent());
			PoiUtil.writeDataToExcel("成交记录", file, columnDescList, dataList);

			is = new FileInputStream(file);
			svrResponse.setContentType("application/vnd.ms-excel");
			svrResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
			IOUtils.copy(is, svrResponse.getOutputStream());
			svrResponse.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "导出期货订单成交记录到excel异常：" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (file != null) {
				file.delete();
			}
		}
	}

	private List<List<String>> dataList(List<FuturesOrderMarketDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (FuturesOrderMarketDto trade : content) {
			List<String> data = new ArrayList<>();
			String business = "";
			if (trade.getOrderType() == FuturesOrderType.BuyUp) {
				business = "买";
			} else {
				business = "卖";
			}
			String buyPrice = "";
			if (trade.getBuyingPriceType() == FuturesTradePriceType.MKT) {
				buyPrice = String.valueOf(trade.getBuyingPrice());
			} else {
				buyPrice = String.valueOf(trade.getBuyingEntrustPrice());
			}
			String sellPrice = "";
			if (trade.getBuyingPriceType() == FuturesTradePriceType.MKT) {
				sellPrice = String.valueOf(trade.getSellingPrice());
			} else {
				sellPrice = String.valueOf(trade.getSellingEntrustPrice());
			}
			data.add(trade.getTradeNo() == null ? "" : trade.getTradeNo());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName());
			data.add(trade.getExchangeName() == null ? "" : trade.getExchangeName());
			data.add(business);
			data.add(trade.getState() == null ? "" : trade.getState().getType());
			data.add(String.valueOf(trade.getTotalQuantity() == null ? "" : trade.getTotalQuantity().intValue() + "手"));
			data.add(trade.getBuyingTime() == null ? "" : exprotSdf.format(trade.getBuyingTime()));
			data.add(buyPrice);
			data.add(String.valueOf(trade.getPublisherProfitOrLoss() == null ? "" : trade.getPublisherProfitOrLoss()));
			data.add(String.valueOf(trade.getOpenwindServiceFee() == null ? "" : trade.getOpenwindServiceFee()));
			data.add(String.valueOf(trade.getReserveFund() == null ? "" : trade.getReserveFund()));
			data.add(trade.getSellingTime() == null ? "" : exprotSdf.format(trade.getSellingTime()));
			data.add(sellPrice);
			data.add(String.valueOf(trade.getPublisherProfitOrLoss() == null ? "" : trade.getPublisherProfitOrLoss()));
			data.add(String.valueOf(trade.getUnwindServiceFee() == null ? "" : trade.getUnwindServiceFee()));
			data.add(trade.getWindControlType() == null ? "" : trade.getWindControlType().getType());
			result.add(data);
		}
		return result;
	}

	private List<String> columnDescList() {
		List<String> result = new ArrayList<>();
		result.add("成交编号");
		result.add("合约名称");
		result.add("市场");
		result.add("买卖");
		result.add("状态");
		result.add("手数");
		result.add("开仓时间");
		result.add("开仓价格");
		result.add("浮动盈亏");
		result.add("开仓手续费");
		result.add("保证金");
		result.add("平仓时间");
		result.add("平仓价格");
		result.add("平仓盈亏");
		result.add("平仓手续费");
		result.add("平仓类型");
		return result;
	}

	private Date getCurrentDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}

	/**
	 * 判断当前下单手数是否满足条件
	 * 
	 * @param buyUpNum
	 *            买涨数量
	 * @param buyFullNum
	 *            买跌数量
	 * @param perNum
	 *            用户单笔最大可交易数量
	 * @param userMaxNum
	 *            用户最大可持仓量
	 * @param buysellDto
	 *            当前订单详情
	 * @param buysellDto
	 *            当前合约详情
	 */
	public void checkBuyUpAndFullSUM(BigDecimal buyUpNum, BigDecimal buyFullNum, BigDecimal perNum,
			BigDecimal userMaxNum, FuturesOrderBuysellDto buysellDto, FuturesContractDto contractDto) {
		BigDecimal userNum = buysellDto.getTotalQuantity();
		BigDecimal buyUpTotal = BigDecimal.ZERO;
		BigDecimal buyFullTotal = BigDecimal.ZERO;
		if (buysellDto.getOrderType() == FuturesOrderType.BuyUp) {
			buyUpTotal = buyUpNum.add(buysellDto.getTotalQuantity());
		} else {
			buyFullTotal = buyFullNum.add(buysellDto.getTotalQuantity());
		}

		if (contractDto.getBuyUpTotalLimit() != null && buyUpTotal.compareTo(contractDto.getBuyUpTotalLimit()) > 0) {
			// 买涨持仓总额度已达上限
			throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYUP_CAPACITY_INSUFFICIENT_EXCEPTION);
		}
		if (contractDto.getBuyFullTotalLimit() != null
				&& buyFullTotal.compareTo(contractDto.getBuyFullTotalLimit()) > 0) {
			// 买跌持仓总额度已达上限
			throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYFULL_CAPACITY_INSUFFICIENT_EXCEPTION);
		}
		if (perNum != null && userNum.compareTo(perNum) > 0) {
			// 单笔交易数量过大
			throw new ServiceException(ExceptionConstant.SINGLE_TRANSACTION_QUANTITY_EXCEPTION);
		}
		if (userMaxNum != null) {
			if (perNum.compareTo(userMaxNum) > 0) {
				// 用户单笔交易数量大于用户持仓总量
				throw new ServiceException(ExceptionConstant.CONTRACT_HOLDING_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
			if (buyUpTotal.abs().compareTo(userMaxNum) > 0 || buyFullTotal.compareTo(userMaxNum) > 0) {
				// 该用户持仓量已达上限
				throw new ServiceException(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION);
			}
			if (buyUpTotal.add(buyFullTotal).compareTo(userMaxNum) > 0) {
				// 该用户持仓量已达上限
				throw new ServiceException(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION);
			}
		}
	}

	/**
	 * 根据最后交易日和首次通知日判断是否可以下单，可以下单计算公式：MIN（最后交易日，首次通知日）> 当前日期
	 * 
	 * @param contractDto
	 *            合约信息
	 */
	public void checkedMinPlaceOrder(FuturesContractDto contractDto) {
		if (contractDto == null) {
			throw new ServiceException(ExceptionConstant.CONTRACT_DOESNOT_EXIST_EXCEPTION);
		}
		Long minTime = null;
		if (contractDto.getFirstNoticeDate() != null && contractDto.getLastTradingDate() != null) {
			minTime = Math.min(contractDto.getFirstNoticeDate().getTime(), contractDto.getLastTradingDate().getTime());
		} else if (contractDto.getFirstNoticeDate() != null) {
			minTime = contractDto.getFirstNoticeDate().getTime();
		} else if (contractDto.getLastTradingDate() != null) {
			minTime = contractDto.getLastTradingDate().getTime();
		}
		if (minTime != null) {
			Date exchangeTime = contractDto.getTimeZoneGap() == null ? new Date()
					: retriveExchangeTime(new Date(), contractDto.getTimeZoneGap());
			if (minTime.compareTo(exchangeTime.getTime()) < 0) {
				// 当前时间大于（等于）最后交易日和首次通知日中的最小时间，不能下单
				throw new ServiceException(ExceptionConstant.MIN_PLACE_ORDER_EXCEPTION);
			}
		}
	}

	/**
	 * 获取交易所的对应时间
	 * 
	 * @param localTime
	 *            日期
	 * @param timeZoneGap
	 *            和交易所的时差
	 * @return 交易所的对应时间
	 */
	private Date retriveExchangeTime(Date localTime, Integer timeZoneGap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.HOUR_OF_DAY, timeZoneGap * -1);
		return cal.getTime();
	}

}
