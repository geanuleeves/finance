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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.PublisherBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesContractBusiness;
import com.waben.stock.applayer.tactics.business.futures.FuturesOrderBusiness;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderBuysellDto;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderMarketDto;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.applayer.tactics.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
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
import com.waben.stock.interfaces.util.PasswordCrypt;
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

	@Autowired
	private FuturesOrderBusiness futuresOrderBusiness;

	@Autowired
	private FuturesContractBusiness futuresContractBusiness;

	@Autowired
	private PublisherBusiness publisherBusiness;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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

		// TODO 检查期货网关是否支持该合约

		// 用户单笔最大可交易数量
		BigDecimal perNum = contractDto.getPerOrderLimit();
		// 用户最大可持仓量
		BigDecimal userMaxNum = contractDto.getUserTotalLimit();
		// 用户持仓总数量
		// TODO 查询方法有误
		Integer sumUser = futuresOrderBusiness.sumUserNum(buysellDto.getContractId(), SecurityUtil.getUserId());
		BigDecimal sumUserNum = sumUser == null ? new BigDecimal(0) : new BigDecimal(sumUser).abs();
		// 当前用户单笔持仓数量
		BigDecimal userNum = buysellDto.getTotalQuantity();
		// 用户已持仓量 + 当前买入持仓量
		BigDecimal sumTotal = sumUserNum.add(buysellDto.getTotalQuantity());

		if (userNum.compareTo(perNum) > 0) {
			// 单笔交易数量过大
			throw new ServiceException(ExceptionConstant.SINGLE_TRANSACTION_QUANTITY_EXCEPTION);
		}
		if (perNum.compareTo(userMaxNum) > 0) {
			// 交易数量大于用户持仓总量
			throw new ServiceException(ExceptionConstant.CONTRACT_HOLDING_CAPACITY_INSUFFICIENT_EXCEPTION);
		}
		if (sumUserNum.abs().compareTo(userMaxNum) > 0 || sumTotal.compareTo(userMaxNum) > 0) {
			// 该用户持仓量已达上限
			throw new ServiceException(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION);
		}

		// 验证支付密码
		CapitalAccountDto capitalAccount = futuresContractBusiness.findByPublisherId(SecurityUtil.getUserId());
		String storePaymentPassword = capitalAccount.getPaymentPassword();
		if (storePaymentPassword == null || "".equals(storePaymentPassword)) {
			throw new ServiceException(ExceptionConstant.PAYMENTPASSWORD_NOTSET_EXCEPTION);
		}
		if (!PasswordCrypt.match(buysellDto.getPaymentPassword(), storePaymentPassword)) {
			throw new ServiceException(ExceptionConstant.PAYMENTPASSWORD_WRONG_EXCEPTION);
		}
		// 总金额
		BigDecimal totalFee = new BigDecimal(0);
		// 保证金金额
		BigDecimal reserveAmount = contractDto.getPerUnitReserveFund().multiply(buysellDto.getTotalQuantity());
		// 开仓手续费 + 平仓手续费
		BigDecimal openUnwin = contractDto.getOpenwindServiceFee().add(contractDto.getUnwindServiceFee());

		// 交易综合费 = (开仓手续费 + 平仓手续费)* 交易持仓数
		BigDecimal comprehensiveAmount = openUnwin.multiply(buysellDto.getTotalQuantity());

		// 总金额 = 保证金金额 + 交易综合费
		totalFee = reserveAmount.add(comprehensiveAmount);

		// 检查余额
		if (totalFee.compareTo(capitalAccount.getAvailableBalance()) > 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		FuturesOrderDto orderDto = new FuturesOrderDto();
		orderDto.setPublisherId(SecurityUtil.getUserId());
		orderDto.setOrderType(buysellDto.getOrderType());
		orderDto.setContractId(buysellDto.getContractId());
		orderDto.setTotalQuantity(buysellDto.getTotalQuantity());
		// 保证金
		orderDto.setReserveFund(reserveAmount);
		// 服务费
		orderDto.setServiceFee(comprehensiveAmount);
		orderDto.setContractSymbol(contractDto.getSymbol());
		orderDto.setContractName(contractDto.getName());
		orderDto.setContractCurrency(contractDto.getCurrency());
		orderDto.setOpenwindServiceFee(contractDto.getOpenwindServiceFee());
		orderDto.setUnwindServiceFee(contractDto.getUnwindServiceFee());
		orderDto.setPerUnitUnwindPoint(contractDto.getPerUnitUnwindPoint());
		orderDto.setUnwindPointType(contractDto.getUnwindPointType());
		orderDto.setOvernightPerUnitReserveFund(contractDto.getOvernightPerUnitReserveFund());
		orderDto.setOvernightPerUnitDeferredFee(contractDto.getOvernightPerUnitDeferredFee());
		// 买入价格类型
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
		if (buysellDto.getBuyingPriceType() == FuturesTradePriceType.LMT) {
			orderDto.setBuyingEntrustPrice(buysellDto.getBuyingEntrustPrice());
		}
		// 获取是否为测试单
		PublisherDto publisher = publisherBusiness.findById(SecurityUtil.getUserId());
		orderDto.setIsTest(publisher.getIsTest());
		return new Response<>(futuresOrderBusiness.buy(orderDto));
	}

	@PostMapping("/cancelOrder/{orderId}")
	@ApiOperation(value = "用户取消订单委托")
	public Response<FuturesOrderDto> cancelOrder(@PathVariable Long orderId) {
		return new Response<>(futuresOrderBusiness.cancelOrder(orderId));
	}

	@PostMapping("/applyUnwind/{orderId}")
	@ApiOperation(value = "用户申请平仓")
	public Response<FuturesOrderDto> applyUnwind(@PathVariable Long orderId,
			@RequestParam(required = true) FuturesTradePriceType sellingPriceType, BigDecimal sellingEntrustPrice) {
		return new Response<>(futuresOrderBusiness.applyUnwind(orderId, sellingPriceType, sellingEntrustPrice));
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
		return new Response<>(futuresOrderBusiness.backhandUnwind(orderId));
	}

	@GetMapping("/holding")
	@ApiOperation(value = "获取持仓中列表")
	public Response<PageInfo<FuturesOrderMarketDto>> holdingList(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position };
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
		FuturesOrderState[] states = { FuturesOrderState.BuyingEntrust, FuturesOrderState.PartPosition,
				FuturesOrderState.SellingEntrust, FuturesOrderState.PartUnwind };
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
				orderQuery.setStartBuyingTime(sdf.parse(startTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(endTime)) {
			try {
				orderQuery.setEndBuyingTime(sdf.parse(endTime));
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
		FuturesOrderState[] states = { FuturesOrderState.BuyingCanceled, FuturesOrderState.BuyingFailure,
				FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@GetMapping("/holding/profit")
	@ApiOperation(value = "获取持仓中总收益")
	public Response<BigDecimal> holdingProfit(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Position };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getFloatingProfitOrLoss() == null ? new BigDecimal(0)
					: futuresOrderMarketDto.getFloatingProfitOrLoss());
		}
		return new Response<>(totalIncome.setScale(2, RoundingMode.DOWN));
	}

	@GetMapping("/entrustment/profit")
	@ApiOperation(value = "获取委托中总收益")
	public Response<BigDecimal> entrustmentProfit(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.BuyingEntrust, FuturesOrderState.PartPosition,
				FuturesOrderState.SellingEntrust, FuturesOrderState.PartUnwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getFloatingProfitOrLoss() == null ? new BigDecimal(0)
					: futuresOrderMarketDto.getFloatingProfitOrLoss());
		}
		return new Response<>(totalIncome.setScale(2, RoundingMode.DOWN));
	}

	@GetMapping("/settled/profit")
	@ApiOperation(value = "获取已结算总收益")
	public Response<BigDecimal> settledProfit(int page, int size) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		FuturesOrderState[] states = { FuturesOrderState.Unwind };
		orderQuery.setStates(states);
		orderQuery.setPage(page);
		orderQuery.setSize(size);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		List<FuturesOrderMarketDto> list = futuresOrderBusiness.pageOrderMarket(orderQuery).getContent();
		BigDecimal totalIncome = new BigDecimal(0);
		for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
			totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss() == null ? new BigDecimal(0)
					: futuresOrderMarketDto.getPublisherProfitOrLoss());
		}
		return new Response<>(totalIncome.setScale(2, RoundingMode.DOWN));
	}

	@GetMapping("/detail/{orderId}")
	@ApiOperation(value = "获取期货订单已结算详细信息")
	@ApiImplicitParam(paramType = "path", dataType = "Long", name = "orderId", value = "期货订单ID", required = true)
	public Response<PageInfo<FuturesOrderMarketDto>> getOrderSettldDetails(@PathVariable("orderId") Long orderId) {
		FuturesOrderQuery orderQuery = new FuturesOrderQuery();
		orderQuery.setPage(0);
		orderQuery.setSize(1);
		orderQuery.setOrderId(orderId);
		orderQuery.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresOrderBusiness.pageOrderMarket(orderQuery));
	}

	@PostMapping("/settingStopLoss/{orderId}")
	@ApiOperation(value = "设置止损止盈")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "orderId", value = "订单ID", dataType = "Long", paramType = "path", required = true),
			@ApiImplicitParam(name = "limitProfitType", value = "止盈类型", dataType = "int", paramType = "query", required = false),
			@ApiImplicitParam(name = "perUnitLimitProfitAmount", value = "止盈金额", dataType = "BigDecimal", paramType = "query", required = false),
			@ApiImplicitParam(name = "limitLossType", value = "止损类型", dataType = "int", paramType = "query", required = false),
			@ApiImplicitParam(name = "perUnitLimitLossAmount", value = "止损金额", dataType = "BigDecimal", paramType = "query", required = false) })
	public Response<Integer> editOrder(@PathVariable Long orderId, Integer limitProfitType,
			BigDecimal perUnitLimitProfitAmount, Integer limitLossType, BigDecimal perUnitLimitLossAmount) {
		return new Response<>(futuresOrderBusiness.settingStopLoss(orderId, limitProfitType, perUnitLimitProfitAmount,
				limitLossType, perUnitLimitLossAmount));
	}

	@GetMapping("/turnover/statisty/record")
	@ApiOperation(value = "获取成交统计记录")
	public Response<TurnoverStatistyRecordDto> getTurnoverStatistyRecord() {
		return new Response<>(futuresOrderBusiness.getTurnoverStatistyRecord());
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
			totalIncome = totalIncome.add(futuresOrderMarketDto.getFloatingProfitOrLoss());
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
				orderQuery.setStartBuyingTime(sdf.parse(startTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(endTime)) {
			try {
				orderQuery.setEndBuyingTime(sdf.parse(endTime));
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
			data.add(trade.getContractName() == null ? "" : trade.getContractName());
			data.add(trade.getExchangeName() == null ? "" : trade.getExchangeName());
			data.add(business);
			data.add(trade.getState() == null ? "" : trade.getState().getType());
			data.add(String.valueOf(trade.getTotalQuantity() == null ? "" : trade.getTotalQuantity().intValue() + "手"));
			data.add(trade.getBuyingTime() == null ? "" : exprotSdf.format(trade.getBuyingTime()));
			data.add(buyPrice);
			data.add(String.valueOf(trade.getFloatingProfitOrLoss() == null ? "" : trade.getFloatingProfitOrLoss()));
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

}
