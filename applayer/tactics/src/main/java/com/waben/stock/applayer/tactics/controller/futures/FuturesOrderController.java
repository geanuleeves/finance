package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.CapitalAccountBusiness;
import com.waben.stock.applayer.tactics.business.PublisherBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesContractBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesContractOrderBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesOrderBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesTradeActionBusiness;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderBuysellDto;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderMarketDto;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderProfitDto;
import com.waben.stock.applayer.tactics.dto.futures.TransactionDynamicsDto;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.applayer.tactics.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.*;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.param.futures.PlaceOrderParam;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import com.waben.stock.interfaces.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
	private FuturesContractOrderBusiness contractOrderBusiness;

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

	@Autowired
	private FuturesTradeActionBusiness futuresTradeActionBusiness;

	private SimpleDateFormat exprotSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@PostMapping("/buy")
	@ApiOperation(value = "买涨买跌下单")
	public Response<FuturesTradeEntrustDto> placeOrder(FuturesOrderBuysellDto param) {
		logger.info("调用接口发布人{}期货下单{}，手数{}!", SecurityUtil.getUserId(), param.getContractId(), param.getTotalQuantity());
		// step 1 : 检查合约信息
		FuturesContractDto contract = futuresContractBusiness.findByContractId(param.getContractId());
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION, "contractId", param.getContractId());
		}
		if (contract.getExchangeEnable() != null && !contract.getExchangeEnable()) {
			throw new ServiceException(ExceptionConstant.EXCHANGE_ISNOT_AVAILABLE_EXCEPTION);
		}
		if (contract.getEnable() != null && !contract.getEnable()) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ABNORMALITY_EXCEPTION);
		}
		if (!contract.getIsTradeTime()) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		// step 2 : 根据最后交易日和首次通知日判断是否可以下单，可以下单计算公式：MIN（最后交易日，首次通知日）> 当前日期
		Long checkLastTrade = 0L;
		if (contract.getFirstNoticeDate() != null) {
			checkLastTrade = contract.getFirstNoticeDate().getTime();
		}
		if (contract.getLastTradingDate() != null) {
			checkLastTrade = checkLastTrade == 0 ? contract.getLastTradingDate().getTime()
					: Math.min(checkLastTrade, contract.getLastTradingDate().getTime());
		}
		if (checkLastTrade > 0) {
			Date exchangeTime = contract.getTimeZoneGap() == null ? new Date()
					: retriveExchangeTime(new Date(), contract.getTimeZoneGap());
			if (checkLastTrade.compareTo(exchangeTime.getTime()) < 0) {
				throw new ServiceException(ExceptionConstant.MIN_PLACE_ORDER_EXCEPTION);
			}
		}
		// step 3 : 包装代理商销售价格到合约信息
		futuresContractBusiness.wrapperAgentPrice(contract);
		// step 4 : 检查下单数量
		BigDecimal perNum = contract.getPerOrderLimit();
		BigDecimal userMaxNum = contract.getUserTotalLimit();
		FuturesContractOrderDto contractOrder = contractOrderBusiness
				.fetchByContractIdAndPublisherId(param.getContractId(), SecurityUtil.getUserId());
		BigDecimal buyUpNum = BigDecimal.ZERO;
		BigDecimal buyFallNum = BigDecimal.ZERO;
		BigDecimal alreadyReserveFund = BigDecimal.ZERO;
		if (contractOrder != null) {
			buyUpNum = contractOrder.getBuyUpTotalQuantity();
			buyFallNum = contractOrder.getBuyFallTotalQuantity();
			alreadyReserveFund = contractOrder.getReserveFund();
		}
		checkBuyUpAndFullSum(buyUpNum, buyFallNum, perNum, userMaxNum, param.getOrderType(), param.getTotalQuantity(),
				contract);
		// step 5 : 计算总费用，总保证金=单边最大手数*一手保证金
		BigDecimal totalFee = new BigDecimal(0);
		BigDecimal singleEdgeMax = BigDecimal.ZERO;
		if (param.getOrderType() == FuturesOrderType.BuyUp) {
			BigDecimal preBuyUpNum = param.getTotalQuantity().add(buyUpNum);
			singleEdgeMax = preBuyUpNum.compareTo(buyFallNum) >= 0 ? preBuyUpNum : buyFallNum;
		} else {
			BigDecimal prebuyFallNum = param.getTotalQuantity().add(buyFallNum);
			singleEdgeMax = prebuyFallNum.compareTo(buyUpNum) >= 0 ? prebuyFallNum : buyUpNum;
		}
		BigDecimal totalReserveFund = contract.getPerUnitReserveFund().multiply(singleEdgeMax);
		BigDecimal reserveFund = totalReserveFund.compareTo(alreadyReserveFund) > 0
				? totalReserveFund.subtract(alreadyReserveFund) : BigDecimal.ZERO;
		BigDecimal serviceFee = contract.getOpenwindServiceFee().add(contract.getUnwindServiceFee())
				.multiply(param.getTotalQuantity());
		totalFee = reserveFund.add(serviceFee);
		// step 6 : 检查余额
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
		// step 7 : 组装请求参数，请求下单
		PlaceOrderParam orderParam = new PlaceOrderParam();
		orderParam.setPublisherId(SecurityUtil.getUserId());
		orderParam.setOrderType(param.getOrderType());
		orderParam.setContractId(param.getContractId());
		orderParam.setTotalQuantity(param.getTotalQuantity());
		orderParam.setReserveFund(reserveFund);
		orderParam.setServiceFee(serviceFee);
		orderParam.setCommoditySymbol(contract.getSymbol());
		orderParam.setCommodityName((contract.getName()));
		orderParam.setCommodityCurrency(contract.getCurrency());
		orderParam.setContractNo(contract.getContractNo());
		orderParam.setOpenwindServiceFee(contract.getOpenwindServiceFee());
		orderParam.setUnwindServiceFee(contract.getUnwindServiceFee());
		orderParam.setTradePriceType(param.getBuyingPriceType());
		orderParam.setEntrustPrice(param.getBuyingEntrustPrice());
		if (param.getLimitLossType() != null && param.getLimitLossType() > 0
				&& param.getPerUnitLimitLossAmount() != null
				&& param.getPerUnitLimitLossAmount().compareTo(BigDecimal.ZERO) > 0) {
			orderParam.setLimitLossType(param.getLimitLossType());
			orderParam.setPerUnitLimitLossAmount(param.getPerUnitLimitLossAmount());
		}
		if (param.getLimitProfitType() != null && param.getLimitProfitType() > 0
				&& param.getPerUnitLimitProfitAmount() != null
				&& param.getPerUnitLimitProfitAmount().compareTo(BigDecimal.ZERO) > 0) {
			orderParam.setLimitProfitType(param.getLimitProfitType());
			orderParam.setPerUnitLimitProfitAmount(param.getPerUnitLimitProfitAmount());
		}
		PublisherDto publisher = publisherBusiness.findById(SecurityUtil.getUserId());
		orderParam.setIsTest(publisher.getIsTest());
		return new Response<>(futuresOrderBusiness.placeOrder(orderParam));
	}

	@PostMapping("/applyUnwind/{contractId}")
	@ApiOperation(value = "市价平仓")
	public Response<String> applyUnwind(@PathVariable Long contractId,
			@RequestParam(required = true) FuturesOrderType orderType) {
		futuresOrderBusiness.applyUnwind(contractId, orderType, FuturesTradePriceType.MKT, null,
				SecurityUtil.getUserId());
		return new Response<>();
	}

	@PostMapping("/applyUnwindAll")
	@ApiOperation(value = "用户申请一键平仓所有订单")
	public Response<String> applyUnwindAll() {
		futuresOrderBusiness.applyUnwindAll(SecurityUtil.getUserId());
		Response<String> result = new Response<>();
		result.setResult("success");
		return result;
	}

	@PostMapping("/backhandUnwind/{contractId}")
	@ApiOperation(value = "市价反手")
	public Response<String> backhandUnwind(@PathVariable Long contractId,
			@RequestParam(required = true) FuturesOrderType orderType) {
		FuturesOrderType backhandOrderType = (orderType == FuturesOrderType.BuyUp) ? FuturesOrderType.BuyFall
				: FuturesOrderType.BuyUp;
		// step 1 : 检查合约信息
		FuturesContractDto contract = futuresContractBusiness.findByContractId(contractId);
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION, "contractId", contractId);
		}
		if (contract.getExchangeEnable() != null && !contract.getExchangeEnable()) {
			throw new ServiceException(ExceptionConstant.EXCHANGE_ISNOT_AVAILABLE_EXCEPTION);
		}
		if (contract.getEnable() != null && !contract.getEnable()) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ABNORMALITY_EXCEPTION);
		}
		if (!contract.getIsTradeTime()) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		// step 2 : 根据最后交易日和首次通知日判断是否可以下单，可以下单计算公式：MIN（最后交易日，首次通知日）> 当前日期
		Long checkLastTrade = 0L;
		if (contract.getFirstNoticeDate() != null) {
			checkLastTrade = contract.getFirstNoticeDate().getTime();
		}
		if (contract.getLastTradingDate() != null) {
			checkLastTrade = checkLastTrade == 0 ? contract.getLastTradingDate().getTime()
					: Math.min(checkLastTrade, contract.getLastTradingDate().getTime());
		}
		if (checkLastTrade > 0) {
			Date exchangeTime = contract.getTimeZoneGap() == null ? new Date()
					: retriveExchangeTime(new Date(), contract.getTimeZoneGap());
			if (checkLastTrade.compareTo(exchangeTime.getTime()) < 0) {
				throw new ServiceException(ExceptionConstant.MIN_PLACE_ORDER_EXCEPTION);
			}
		}
		// step 3 : 包装代理商销售价格到合约信息
		futuresContractBusiness.wrapperAgentPrice(contract);
		// step 4 : 检查下单数量
		BigDecimal perNum = contract.getPerOrderLimit();
		BigDecimal userMaxNum = contract.getUserTotalLimit();
		FuturesContractOrderDto contractOrder = contractOrderBusiness.fetchByContractIdAndPublisherId(contractId,
				SecurityUtil.getUserId());
		BigDecimal backhandQuantity = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			backhandQuantity = contractOrder.getBuyUpCanUnwindQuantity();
		} else {
			backhandQuantity = contractOrder.getBuyFallCanUnwindQuantity();
		}
		if (backhandQuantity.compareTo(BigDecimal.ZERO) <= 0) {
			throw new ServiceException(ExceptionConstant.UNWINDQUANTITY_NOTENOUGH_EXCEPTION);
		}
		BigDecimal buyUpNum = BigDecimal.ZERO;
		BigDecimal buyFallNum = BigDecimal.ZERO;
		BigDecimal alreadyReserveFund = BigDecimal.ZERO;
		if (contractOrder != null) {
			buyUpNum = contractOrder.getBuyUpTotalQuantity();
			buyFallNum = contractOrder.getBuyFallTotalQuantity();
			alreadyReserveFund = contractOrder.getReserveFund();
		}
		checkBuyUpAndFullSum(buyUpNum, buyFallNum, perNum, userMaxNum, backhandOrderType, backhandQuantity, contract);
		// step 5 : 计算总费用，总保证金=单边最大手数*一手保证金
		BigDecimal totalFee = new BigDecimal(0);
		BigDecimal singleEdgeMax = BigDecimal.ZERO;
		if (backhandOrderType == FuturesOrderType.BuyUp) {
			BigDecimal preBuyUpNum = backhandQuantity.add(buyUpNum);
			singleEdgeMax = preBuyUpNum.compareTo(buyFallNum) >= 0 ? preBuyUpNum : buyFallNum;
		} else {
			BigDecimal prebuyFallNum = backhandQuantity.add(buyFallNum);
			singleEdgeMax = prebuyFallNum.compareTo(buyUpNum) >= 0 ? prebuyFallNum : buyUpNum;
		}
		BigDecimal totalReserveFund = contract.getPerUnitReserveFund().multiply(singleEdgeMax);
		BigDecimal reserveFund = totalReserveFund.compareTo(alreadyReserveFund) > 0
				? totalReserveFund.subtract(alreadyReserveFund) : BigDecimal.ZERO;
		BigDecimal serviceFee = contract.getOpenwindServiceFee().add(contract.getUnwindServiceFee())
				.multiply(backhandQuantity);
		totalFee = reserveFund.add(serviceFee);
		// step 6 : 检查余额
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
		// step 7 : 检查条件成立
		futuresOrderBusiness.backhandUnwind(contractId, orderType, FuturesTradePriceType.MKT, null,
				SecurityUtil.getUserId());
		return new Response<>();
	}

	@PostMapping("/lockUnwind/{contractId}")
	@ApiOperation(value = "快捷锁仓")
	public Response<FuturesTradeEntrustDto> lockUnwind(@PathVariable Long contractId,
			@RequestParam(required = true) FuturesOrderType orderType) {
		orderType = orderType == FuturesOrderType.BuyUp ? FuturesOrderType.BuyFall : FuturesOrderType.BuyUp;
		FuturesContractOrderDto contractOrder = contractOrderBusiness.fetchByContractIdAndPublisherId(contractId,
				SecurityUtil.getUserId());
		BigDecimal quantity = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			quantity = contractOrder.getBuyFallCanUnwindQuantity();
		} else {
			quantity = contractOrder.getBuyUpCanUnwindQuantity();
		}
		logger.info("调用接口发布人{}期货快捷锁仓{}，手数{}!", SecurityUtil.getUserId(), contractId, quantity);
		// step 1 : 检查合约信息
		FuturesContractDto contract = futuresContractBusiness.findByContractId(contractId);
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION, "contractId", contractId);
		}
		if (contract.getExchangeEnable() != null && !contract.getExchangeEnable()) {
			throw new ServiceException(ExceptionConstant.EXCHANGE_ISNOT_AVAILABLE_EXCEPTION);
		}
		if (contract.getEnable() != null && !contract.getEnable()) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ABNORMALITY_EXCEPTION);
		}
		if (!contract.getIsTradeTime()) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		// step 2 : 根据最后交易日和首次通知日判断是否可以下单，可以下单计算公式：MIN（最后交易日，首次通知日）> 当前日期
		Long checkLastTrade = 0L;
		if (contract.getFirstNoticeDate() != null) {
			checkLastTrade = contract.getFirstNoticeDate().getTime();
		}
		if (contract.getLastTradingDate() != null) {
			checkLastTrade = checkLastTrade == 0 ? contract.getLastTradingDate().getTime()
					: Math.min(checkLastTrade, contract.getLastTradingDate().getTime());
		}
		if (checkLastTrade > 0) {
			Date exchangeTime = contract.getTimeZoneGap() == null ? new Date()
					: retriveExchangeTime(new Date(), contract.getTimeZoneGap());
			if (checkLastTrade.compareTo(exchangeTime.getTime()) < 0) {
				throw new ServiceException(ExceptionConstant.MIN_PLACE_ORDER_EXCEPTION);
			}
		}
		// step 3 : 包装代理商销售价格到合约信息
		futuresContractBusiness.wrapperAgentPrice(contract);
		// step 4 : 检查下单数量
		BigDecimal perNum = contract.getPerOrderLimit();
		BigDecimal userMaxNum = contract.getUserTotalLimit();
		BigDecimal buyUpNum = BigDecimal.ZERO;
		BigDecimal buyFallNum = BigDecimal.ZERO;
		BigDecimal alreadyReserveFund = BigDecimal.ZERO;
		if (contractOrder != null) {
			buyUpNum = contractOrder.getBuyUpTotalQuantity();
			buyFallNum = contractOrder.getBuyFallTotalQuantity();
			alreadyReserveFund = contractOrder.getReserveFund();
		}
		checkBuyUpAndFullSum(buyUpNum, buyFallNum, perNum, userMaxNum, orderType, quantity, contract);
		// step 5 : 计算总费用，总保证金=单边最大手数*一手保证金
		BigDecimal totalFee = new BigDecimal(0);
		BigDecimal singleEdgeMax = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			BigDecimal preBuyUpNum = quantity.add(buyUpNum);
			singleEdgeMax = preBuyUpNum.compareTo(buyFallNum) >= 0 ? preBuyUpNum : buyFallNum;
		} else {
			BigDecimal prebuyFallNum = quantity.add(buyFallNum);
			singleEdgeMax = prebuyFallNum.compareTo(buyUpNum) >= 0 ? prebuyFallNum : buyUpNum;
		}
		BigDecimal totalReserveFund = contract.getPerUnitReserveFund().multiply(singleEdgeMax);
		BigDecimal reserveFund = totalReserveFund.compareTo(alreadyReserveFund) > 0
				? totalReserveFund.subtract(alreadyReserveFund) : BigDecimal.ZERO;
		BigDecimal serviceFee = contract.getOpenwindServiceFee().add(contract.getUnwindServiceFee()).multiply(quantity);
		totalFee = reserveFund.add(serviceFee);
		// step 6 : 检查余额
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
		// step 7 : 组装请求参数，请求下单
		PlaceOrderParam orderParam = new PlaceOrderParam();
		orderParam.setPublisherId(SecurityUtil.getUserId());
		orderParam.setOrderType(orderType);
		orderParam.setContractId(contractId);
		orderParam.setTotalQuantity(quantity);
		orderParam.setReserveFund(reserveFund);
		orderParam.setServiceFee(serviceFee);
		orderParam.setCommoditySymbol(contract.getSymbol());
		orderParam.setCommodityName((contract.getName()));
		orderParam.setCommodityCurrency(contract.getCurrency());
		orderParam.setContractNo(contract.getContractNo());
		orderParam.setOpenwindServiceFee(contract.getOpenwindServiceFee());
		orderParam.setUnwindServiceFee(contract.getUnwindServiceFee());
		orderParam.setTradePriceType(FuturesTradePriceType.MKT);
		PublisherDto publisher = publisherBusiness.findById(SecurityUtil.getUserId());
		orderParam.setIsTest(publisher.getIsTest());
		return new Response<>(futuresOrderBusiness.placeOrder(orderParam));
	}

	@PostMapping("/balanceUnwind/{contractId}")
	@ApiOperation(value = "买平或者卖平")
	public Response<String> balanceUnwind(@PathVariable Long contractId,
			@RequestParam(required = true) FuturesOrderType orderType,
			@RequestParam(required = true) BigDecimal quantity) {
		futuresOrderBusiness.balanceUnwind(contractId, orderType, FuturesTradePriceType.MKT, null,
				SecurityUtil.getUserId(), quantity);
		Response<String> result = new Response<>();
		result.setResult("success");
		return result;
	}

	@PostMapping("/settingProfitAndLossLimit/{contractId}")
	@ApiOperation(value = "设置止损止盈")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "contractId", value = "订单ID", dataType = "Long", paramType = "path", required = true),
			@ApiImplicitParam(name = "limitProfitType", value = "止盈类型", dataType = "int", paramType = "query", required = false),
			@ApiImplicitParam(name = "perUnitLimitProfitAmount", value = "止盈金额", dataType = "BigDecimal", paramType = "query", required = false),
			@ApiImplicitParam(name = "limitLossType", value = "止损类型", dataType = "int", paramType = "query", required = false),
			@ApiImplicitParam(name = "perUnitLimitLossAmount", value = "止损金额", dataType = "BigDecimal", paramType = "query", required = false),
			@ApiImplicitParam(name = "orderType", value = "订单类型", dataType = "String", paramType = "query", required = true) })
	public Response<String> settingProfitAndLossLimit(@PathVariable Long contractId, Integer limitProfitType,
			BigDecimal perUnitLimitProfitAmount, Integer limitLossType, BigDecimal perUnitLimitLossAmount,
			FuturesOrderType orderType) {
		futuresOrderBusiness.settingProfitAndLossLimit(SecurityUtil.getUserId(), contractId, orderType, limitProfitType,
				perUnitLimitProfitAmount, limitLossType, perUnitLimitLossAmount, SecurityUtil.getUserId());
		Response<String> result = new Response<>();
		return result;
	}

	/******************************************** 分割线 ************************************************/

	@GetMapping("/holding")
	@ApiOperation(value = "获取持仓中列表", hidden = true)
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
	@ApiOperation(value = "获取委托排队中列表", hidden = true)
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
	@ApiOperation(value = "获取委托中列表", hidden = true)
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
	@ApiOperation(value = "获取成交记录列表（包括持仓中、已结算订单）", hidden = true)
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
	@ApiOperation(value = "获取已结算列表", hidden = true)
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
	public Response<FuturesOrderProfitDto> holdingProfit() {
		FuturesOrderProfitDto result = new FuturesOrderProfitDto();
		// 获得合计浮动盈亏
		BigDecimal totalFloatingProfitAndLoss = futuresOrderBusiness
				.getTotalFloatingProfitAndLoss(SecurityUtil.getUserId());
		result.setTotalIncome(totalFloatingProfitAndLoss);
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
			// totalIncome =
			// totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss()
			// == null ? new BigDecimal(0)
			// : futuresOrderMarketDto.getPublisherProfitOrLoss());
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
	@ApiOperation(value = "获取已平仓列表", hidden = true)
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
		FuturesOrderProfitDto result = new FuturesOrderProfitDto();
		//结算总收益
		FuturesTradeActionQuery futuresTradeActionQuery = new FuturesTradeActionQuery();
		futuresTradeActionQuery.setPublisherId(SecurityUtil.getUserId());
		PageInfo<FuturesTradeActionViewDto> tradeActionpageInfo = futuresTradeActionBusiness.pagesPhone(futuresTradeActionQuery);
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesTradeActionViewDto futuresTradeActionViewDto : tradeActionpageInfo.getContent()) {
			totalIncome = totalIncome.add(futuresTradeActionViewDto.getPublisherProfitOrLoss());
		}
		result.setTotalIncome(totalIncome);
		//冻结保证金
		CapitalAccountDto capitalAccountDto = capitalAccountBusiness.findByPublisherId(SecurityUtil.getUserId());
		result.setReserveFund(capitalAccountDto.getFrozenCapital());
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
			// totalIncome =
			// totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
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
			// totalIncome =
			// totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		return new Response<>(totalIncome.setScale(2, RoundingMode.DOWN));
	}

	/**
	 * 包装当前用户的账户资金及当天持仓、平仓盈亏
	 * 
	 * @return 当前用户的账户资金及当天持仓、平仓盈亏
	 */
	@GetMapping("/current/capital/profit/settled")
	@ApiOperation(value = "获取当前用户的账户资金及当天持仓、平仓盈亏")
	public Response<FuturesOrderDayGainLossDto> capitalAccountAndDayGainLoss() {
		return new Response<>(futuresOrderBusiness.capitalAccountAndDayGainLoss(SecurityUtil.getUserId()));
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
			// if (trade.getBuyingPriceType() == FuturesTradePriceType.MKT) {
			// buyPrice = String.valueOf(trade.getBuyingPrice());
			// } else {
			// buyPrice = String.valueOf(trade.getBuyingEntrustPrice());
			// }
			// String sellPrice = "";
			// if (trade.getBuyingPriceType() == FuturesTradePriceType.MKT) {
			// sellPrice = String.valueOf(trade.getSellingPrice());
			// } else {
			// sellPrice = String.valueOf(trade.getSellingEntrustPrice());
			// }
			data.add(trade.getTradeNo() == null ? "" : trade.getTradeNo());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName());
			data.add(trade.getExchangeName() == null ? "" : trade.getExchangeName());
			data.add(business);
			data.add(trade.getState() == null ? "" : trade.getState().getType());
			data.add(String.valueOf(trade.getTotalQuantity() == null ? "" : trade.getTotalQuantity().intValue() + "手"));
			// data.add(trade.getBuyingTime() == null ? "" :
			// exprotSdf.format(trade.getBuyingTime()));
			// data.add(buyPrice);
			// data.add(String.valueOf(trade.getPublisherProfitOrLoss() == null
			// ? "" : trade.getPublisherProfitOrLoss()));
			// data.add(String.valueOf(trade.getOpenwindServiceFee() == null ?
			// "" : trade.getOpenwindServiceFee()));
			// data.add(String.valueOf(trade.getReserveFund() == null ? "" :
			// trade.getReserveFund()));
			// data.add(trade.getSellingTime() == null ? "" :
			// exprotSdf.format(trade.getSellingTime()));
			// data.add(sellPrice);
			// data.add(String.valueOf(trade.getPublisherProfitOrLoss() == null
			// ? "" : trade.getPublisherProfitOrLoss()));
			// data.add(String.valueOf(trade.getUnwindServiceFee() == null ? ""
			// : trade.getUnwindServiceFee()));
			// data.add(trade.getWindControlType() == null ? "" :
			// trade.getWindControlType().getType());
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
	public void checkBuyUpAndFullSum(BigDecimal buyUpNum, BigDecimal buyFallNum, BigDecimal perNum,
			BigDecimal userMaxNum, FuturesOrderType orderType, BigDecimal quantity, FuturesContractDto contractDto) {
		BigDecimal buyUpTotal = BigDecimal.ZERO;
		BigDecimal buyFallTotal = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			buyUpTotal = buyUpNum.add(quantity);
			if (contractDto.getBuyUpTotalLimit() != null
					&& buyUpTotal.compareTo(contractDto.getBuyUpTotalLimit()) > 0) {
				// 买涨持仓总额度已达上限
				throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYUP_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
		} else {
			buyFallTotal = buyFallNum.add(quantity);
			if (contractDto.getBuyFullTotalLimit() != null
					&& buyFallTotal.compareTo(contractDto.getBuyFullTotalLimit()) > 0) {
				// 买跌持仓总额度已达上限
				throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYFULL_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
		}
		if (perNum != null && quantity.compareTo(perNum) > 0) {
			// 单笔交易数量过大
			throw new ServiceException(ExceptionConstant.SINGLE_TRANSACTION_QUANTITY_EXCEPTION);
		}
		if (userMaxNum != null) {
			if (perNum.compareTo(userMaxNum) > 0) {
				// 用户单笔交易数量大于用户持仓总量
				throw new ServiceException(ExceptionConstant.CONTRACT_HOLDING_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
			if (buyUpTotal.abs().compareTo(userMaxNum) > 0 || buyFallTotal.compareTo(userMaxNum) > 0) {
				// 该用户持仓量已达上限
				throw new ServiceException(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION);
			}
			if (buyUpTotal.add(buyFallTotal).compareTo(userMaxNum) > 0) {
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
