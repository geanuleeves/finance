package com.waben.stock.applayer.tactics.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.tactics.business.CapitalFlowBusiness;
import com.waben.stock.applayer.tactics.business.PaymentOrderBusiness;
import com.waben.stock.applayer.tactics.business.WithdrawalsOrderBusiness;
import com.waben.stock.applayer.tactics.dto.publisher.CapitalFlowWithExtendDto;
import com.waben.stock.applayer.tactics.dto.publisher.PaymentRecordDto;
import com.waben.stock.applayer.tactics.dto.publisher.WithdrawalsRecordDto;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.enums.CapitalFlowType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalFlowQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.PaymentOrderQuery;
import com.waben.stock.interfaces.pojo.query.WithdrawalsOrderQuery;
import com.waben.stock.interfaces.util.StringUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 资金流水 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/capitalFlow")
@Api(description = "资金流水")
public class CapitalFlowController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CapitalFlowBusiness capitalFlowBusiness;

	@Autowired
	private PaymentOrderBusiness paymentOrderBusiness;

	@Autowired
	private WithdrawalsOrderBusiness withdrawalsBusiness;

	@GetMapping("/pages")
	@ApiOperation(value = "用户资金流水", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<CapitalFlowWithExtendDto>> publisherCapitalFlow(int page, int size,
			@RequestParam(defaultValue = "0") int range) {
		CapitalFlowQuery query = new CapitalFlowQuery(page, size);
		Date startTime = null;
		if (range == 1) {
			startTime = new DateTime(new Date()).minusHours(7 * 24).toDate();
		} else if (range == 2) {
			startTime = new DateTime(new Date()).minusHours(30 * 24).toDate();
		} else if (range == 3) {
			startTime = new DateTime(new Date()).minusHours(180 * 24).toDate();
		}
		query.setStartTime(startTime);
		query.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(capitalFlowBusiness.pages(query));
	}

	@GetMapping("/pagesTrade")
	@ApiOperation(value = "用户交易流水", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<CapitalFlowWithExtendDto>> tradeCapitalFlow(int page, int size,
			@RequestParam(defaultValue = "0") int range) {
		CapitalFlowQuery query = new CapitalFlowQuery(page, size);
		Date startTime = null;
		if (range == 1) {
			startTime = new DateTime(new Date()).minusHours(7 * 24).toDate();
		} else if (range == 2) {
			startTime = new DateTime(new Date()).minusHours(30 * 24).toDate();
		} else if (range == 3) {
			startTime = new DateTime(new Date()).minusHours(180 * 24).toDate();
		}
		query.setTypes(new CapitalFlowType[] { CapitalFlowType.FuturesServiceFee, CapitalFlowType.FuturesReserveFund,
				CapitalFlowType.FuturesReturnReserveFund, CapitalFlowType.FuturesRevoke,
				CapitalFlowType.FuturesOvernightDeferredFee, CapitalFlowType.FuturesOvernightReserveFund,
				CapitalFlowType.FuturesReturnOvernightReserveFund, CapitalFlowType.FuturesLoss,
				CapitalFlowType.FuturesProfit });
		query.setStartTime(startTime);
		query.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(capitalFlowBusiness.pages(query));
	}

	@GetMapping("/pagesPayment")
	@ApiOperation(value = "用户充值记录", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<PaymentRecordDto>> pagesPayment(int page, int size,
			@RequestParam(defaultValue = "0") int range) {
		PaymentOrderQuery query = new PaymentOrderQuery(page, size);
		Date startTime = null;
		if (range == 1) {
			startTime = new DateTime(new Date()).minusHours(7 * 24).toDate();
		} else if (range == 2) {
			startTime = new DateTime(new Date()).minusHours(30 * 24).toDate();
		} else if (range == 3) {
			startTime = new DateTime(new Date()).minusHours(180 * 24).toDate();
		}
		query.setStartTime(startTime);
		query.setPublisherId(SecurityUtil.getUserId());
		PageInfo<PaymentRecordDto> result = new PageInfo<>(paymentOrderBusiness.pages(query), PaymentRecordDto.class);
		return new Response<>(result);
	}

	@GetMapping("/pagesWithdraw")
	@ApiOperation(value = "用户提现记录", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<WithdrawalsRecordDto>> pagesWithdraw(int page, int size,
			@RequestParam(defaultValue = "0") int range) {
		WithdrawalsOrderQuery query = new WithdrawalsOrderQuery(page, size);
		Date startTime = null;
		if (range == 1) {
			startTime = new DateTime(new Date()).minusHours(7 * 24).toDate();
		} else if (range == 2) {
			startTime = new DateTime(new Date()).minusHours(30 * 24).toDate();
		} else if (range == 3) {
			startTime = new DateTime(new Date()).minusHours(180 * 24).toDate();
		}
		query.setStartTime(startTime);
		query.setPublisherId(SecurityUtil.getUserId());
		PageInfo<WithdrawalsRecordDto> result = new PageInfo<>(withdrawalsBusiness.pagesByQuery(query),
				WithdrawalsRecordDto.class);
		return new Response<>(result);
	}

	/****************************** 以下是PC接口 *****************************/

	@GetMapping("/pagesByTimeScope")
	@ApiOperation(value = "用户资金流水（时间范围统计，PC桌面端）", notes = "startTime和endTime格式(yyyy-MM-dd HH:mm:ss)")
	public Response<PageInfo<CapitalFlowWithExtendDto>> publisherCapitalFlow(int page, int size, String startTime,
			String endTime, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CapitalFlowQuery query = new CapitalFlowQuery(page, size);
		Date startTimeObj = null;
		if (!StringUtil.isEmpty(startTime)) {
			try {
				startTimeObj = sdf.parse(startTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		Date endTimeObj = null;
		if (!StringUtil.isEmpty(endTime)) {
			try {
				endTimeObj = sdf.parse(endTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(type)) {
			CapitalFlowType[] types = { CapitalFlowType.getByIndex(type) };
			query.setTypes(types);
		}
		query.setStartTime(startTimeObj);
		query.setEndTime(endTimeObj);
		query.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(capitalFlowBusiness.pages(query));
	}

	@GetMapping("/pcPagesTrade")
	@ApiOperation(value = "用户交易流水（时间范围统计，PC桌面端）", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<CapitalFlowWithExtendDto>> pcTradeCapitalFlow(int page, int size, String startTime,
			String endTime, String type) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CapitalFlowQuery query = new CapitalFlowQuery(page, size);
		Date startTimeObj = null;
		if (!StringUtil.isEmpty(startTime)) {
			try {
				startTimeObj = sdf.parse(startTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		Date endTimeObj = null;
		if (!StringUtil.isEmpty(endTime)) {
			try {
				endTimeObj = sdf.parse(endTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(type)) {
			CapitalFlowType[] types = { CapitalFlowType.getByIndex(type) };
			query.setTypes(types);
		} else {
			CapitalFlowType[] types = { CapitalFlowType.FuturesServiceFee, CapitalFlowType.FuturesReserveFund,
					CapitalFlowType.FuturesReturnReserveFund, CapitalFlowType.FuturesRevoke,
					CapitalFlowType.FuturesOvernightDeferredFee, CapitalFlowType.FuturesOvernightReserveFund,
					CapitalFlowType.FuturesReturnOvernightReserveFund, CapitalFlowType.FuturesLoss,
					CapitalFlowType.FuturesProfit };
			query.setTypes(types);
		}
		query.setStartTime(startTimeObj);
		query.setEndTime(endTimeObj);
		query.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(capitalFlowBusiness.pages(query));
	}

	@GetMapping("/pcPagesPayment")
	@ApiOperation(value = "用户充值记录（时间范围统计，PC桌面端）", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<PaymentRecordDto>> pcPagesPayment(int page, int size, String startTime, String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		PaymentOrderQuery query = new PaymentOrderQuery(page, size);
		Date startTimeObj = null;
		if (!StringUtil.isEmpty(startTime)) {
			try {
				startTimeObj = sdf.parse(startTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		Date endTimeObj = null;
		if (!StringUtil.isEmpty(endTime)) {
			try {
				endTimeObj = sdf.parse(endTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		query.setStartTime(startTimeObj);
		query.setEndTime(endTimeObj);
		query.setPublisherId(SecurityUtil.getUserId());
		PageInfo<PaymentRecordDto> result = new PageInfo<>(paymentOrderBusiness.pages(query), PaymentRecordDto.class);
		return new Response<>(result);
	}

	@GetMapping("/pcPagesWithdraw")
	@ApiOperation(value = "用户提现记录（时间范围统计，PC桌面端）", notes = "range表示统计范围，0全部，1最近一周，2最近一个月，3最近半年")
	public Response<PageInfo<WithdrawalsRecordDto>> pcPagesWithdraw(int page, int size, String startTime,
			String endTime) {
		WithdrawalsOrderQuery query = new WithdrawalsOrderQuery(page, size);
		Date startTimeObj = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (!StringUtil.isEmpty(startTime)) {
			try {
				startTimeObj = sdf.parse(startTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		Date endTimeObj = null;
		if (!StringUtil.isEmpty(endTime)) {
			try {
				endTimeObj = sdf.parse(endTime);
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		query.setStartTime(startTimeObj);
		query.setEndTime(endTimeObj);
		query.setPublisherId(SecurityUtil.getUserId());
		PageInfo<WithdrawalsRecordDto> result = new PageInfo<>(withdrawalsBusiness.pagesByQuery(query),
				WithdrawalsRecordDto.class);
		return new Response<>(result);
	}

}
