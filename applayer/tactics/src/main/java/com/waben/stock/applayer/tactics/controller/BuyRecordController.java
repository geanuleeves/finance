package com.waben.stock.applayer.tactics.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.BuyRecordBusiness;
import com.waben.stock.applayer.tactics.business.CapitalAccountBusiness;
import com.waben.stock.applayer.tactics.business.HolidayBusiness;
import com.waben.stock.applayer.tactics.business.StockBusiness;
import com.waben.stock.applayer.tactics.dto.buyrecord.BuyRecordWithMarketDto;
import com.waben.stock.applayer.tactics.dto.buyrecord.TradeDynamicDto;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.buyrecord.BuyRecordDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.BuyRecordState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.BuyRecordQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.SettlementQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 点买记录 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/buyRecord")
@Api(description = "点买交易")
public class BuyRecordController {

	@Autowired
	private BuyRecordBusiness buyRecordBusiness;

	@Autowired
	private CapitalAccountBusiness capitalAccountBusiness;

	@Autowired
	private HolidayBusiness holidayBusiness;

	@Autowired
	private StockBusiness stockBusiness;

	@GetMapping("/isTradeTime")
	@ApiOperation(value = "是否为交易时间段")
	public Response<Boolean> isTradeTime() {
		return new Response<>(holidayBusiness.isTradeTime());
	}
	
	@GetMapping("/strategyqualify/{strategyTypeId}")
	@ApiOperation(value = "是否有资格参与某个策略")
	public Response<Boolean> hasStrategyQualify(@PathVariable("strategyTypeId") Long strategyTypeId) {
		return new Response<>(buyRecordBusiness.hasStrategyQualify(SecurityUtil.getUserId(), strategyTypeId));
	}

	@PostMapping("/buy")
	@ApiOperation(value = "点买")
	public Response<BuyRecordWithMarketDto> buy(@RequestParam(required = true) Long strategyTypeId,
			@RequestParam(required = true) BigDecimal applyAmount, @RequestParam(required = true) BigDecimal serviceFee,
			@RequestParam(required = true) BigDecimal reserveFund,
			@RequestParam(required = true) BigDecimal delegatePrice,
			@RequestParam(required = true) BigDecimal profitPoint, @RequestParam(required = true) BigDecimal lossPoint,
			@RequestParam(required = true) String stockCode, @RequestParam(required = true) Boolean deferred,
			@RequestParam(required = true) String paymentPassword) {
		// 检查交易时间段
		boolean isTradeTime = holidayBusiness.isTradeTime();
		if (!isTradeTime) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_NONTRADINGPERIOD_EXCEPTION);
		}
		// 判断该股票是否已经停牌
		boolean isSuspension = stockBusiness.isSuspension(stockCode);
		if (isSuspension) {
			throw new ServiceException(ExceptionConstant.STOCK_SUSPENSION_EXCEPTION);
		}
		// 判断是否有资格参与该策略
		boolean qualify = buyRecordBusiness.hasStrategyQualify(SecurityUtil.getUserId(), strategyTypeId);
		if(!qualify) {
			throw new ServiceException(ExceptionConstant.STRATEGYQUALIFY_NOTENOUGH_EXCEPTION);
		}
		// 判断该市值是否足够购买一手股票
		BigDecimal temp = applyAmount.divide(delegatePrice, 2, RoundingMode.HALF_DOWN);
		Integer numberOfStrand = temp.divideAndRemainder(BigDecimal.valueOf(100))[0].multiply(BigDecimal.valueOf(100))
				.intValue();
		if(numberOfStrand < 100) {
			throw new ServiceException(ExceptionConstant.APPLYAMOUNT_NOTENOUGH_BUYSTOCK_EXCEPTION);
		}
		// 检查参数是否合理
		if (delegatePrice.compareTo(new BigDecimal(0)) <= 0) {
			throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
		}
		if (!(profitPoint.abs().compareTo(new BigDecimal(0)) > 0
				&& profitPoint.abs().compareTo(new BigDecimal(1)) < 0)) {
			throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
		}
		if (!(lossPoint.abs().compareTo(new BigDecimal(0)) > 0 && lossPoint.abs().compareTo(new BigDecimal(1)) < 0)) {
			throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
		}
		// 验证支付密码
		CapitalAccountDto capitalAccount = capitalAccountBusiness.findByPublisherId(SecurityUtil.getUserId());
		String storePaymentPassword = capitalAccount.getPaymentPassword();
		if (storePaymentPassword == null || "".equals(storePaymentPassword)) {
			throw new ServiceException(ExceptionConstant.PAYMENTPASSWORD_NOTSET_EXCEPTION);
		}
		if (!storePaymentPassword.equals(paymentPassword)) {
			throw new ServiceException(ExceptionConstant.PAYMENTPASSWORD_WRONG_EXCEPTION);
		}
		// 检查余额
		if (serviceFee.add(reserveFund).compareTo(capitalAccount.getAvailableBalance()) > 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		// 初始化点买数据
		BuyRecordDto dto = new BuyRecordDto();
		dto.setStrategyTypeId(strategyTypeId);
		dto.setApplyAmount(applyAmount);
		dto.setServiceFee(serviceFee);
		dto.setReserveFund(reserveFund);
		dto.setProfitPoint(profitPoint);
		dto.setLossPoint(lossPoint.abs().multiply(new BigDecimal(-1)));
		dto.setStockCode(stockCode);
		dto.setDeferred(deferred);
		dto.setDelegatePrice(delegatePrice);
		// 设置对应的publisher
		dto.setPublisherId(SecurityUtil.getUserId());
		dto.setPublisherSerialCode(SecurityUtil.getSerialCode());
		BuyRecordDto buyRecordDto = buyRecordBusiness.buy(dto);
		return new Response<>(buyRecordBusiness.wrapMarketInfo(buyRecordDto));
	}

	@GetMapping("/pagesHoldPosition")
	@ApiOperation(value = "持仓中的点买记录列表")
	public Response<PageInfo<BuyRecordWithMarketDto>> pagesHoldPosition(int page, int size) {
		BuyRecordQuery query = new BuyRecordQuery(page, size, SecurityUtil.getUserId(),
				new BuyRecordState[] { BuyRecordState.POSTED, BuyRecordState.BUYLOCK, BuyRecordState.HOLDPOSITION,
						BuyRecordState.SELLAPPLY, BuyRecordState.SELLLOCK });
		PageInfo<BuyRecordDto> pageInfo = buyRecordBusiness.pages(query);
		List<BuyRecordWithMarketDto> content = buyRecordBusiness.wrapMarketInfo(pageInfo.getContent());
		return new Response<>(new PageInfo<>(content, pageInfo.getTotalPages(), pageInfo.getLast(),
				pageInfo.getTotalElements(), pageInfo.getSize(), pageInfo.getNumber(), pageInfo.getFrist()));
	}

	@GetMapping("/pagesUnwind")
	@ApiOperation(value = "结算的点买记录列表")
	public Response<PageInfo<BuyRecordWithMarketDto>> pagesUnwind(int page, int size) {
		SettlementQuery query = new SettlementQuery(page, size);
		query.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(buyRecordBusiness.pagesSettlement(query));
	}

	@GetMapping("/tradeDynamic")
	@ApiOperation(value = "交易动态列表")
	public Response<PageInfo<TradeDynamicDto>> tradeDynamic(int page, int size) {
		return new Response<>(buyRecordBusiness.tradeDynamic(page, size));
	}

	@RequestMapping(value = "/sellapply/{id}", method = RequestMethod.POST)
	@ApiOperation(value = "用户申请卖出")
	Response<BuyRecordDto> sellapply(@PathVariable("id") Long id) {
		return new Response<>(buyRecordBusiness.sellApply(SecurityUtil.getUserId(), id));
	}

}
