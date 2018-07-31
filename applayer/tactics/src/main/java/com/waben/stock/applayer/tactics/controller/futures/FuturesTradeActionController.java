package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesTradeActionBusiness;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import com.waben.stock.interfaces.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author chenk 2018/7/27
 */
@RestController
@RequestMapping("/futures_trade_action")
@Api(description = "订单交易开平仓记录")
public class FuturesTradeActionController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesTradeActionBusiness futuresTradeActionBusiness;

	@GetMapping("/pages")
	@ApiOperation(value = "订单交易开平仓记录列表", notes = "startTime和endTime格式(yyyy-MM-dd HH:mm:ss)")
	public Response<PageInfo<FuturesTradeActionViewDto>> pages(int page, int size, String name, String startTime,
			String endTime, FuturesTradeActionType tradeActionType) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FuturesTradeActionQuery query = new FuturesTradeActionQuery();
		query.setPage(page);
		query.setSize(size);
		query.setPublisherId(SecurityUtil.getUserId());
		query.setName(name);
		query.setTradeActionType(tradeActionType);
		if (!StringUtil.isEmpty(startTime)) {
			try {
				query.setStartTime(sdf.parse(startTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(endTime)) {
			try {
				query.setEndTime(sdf.parse(endTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		query.setStates(new FuturesTradeEntrustState[] { FuturesTradeEntrustState.Success, FuturesTradeEntrustState.PartSuccess});
		return new Response<>(futuresTradeActionBusiness.pages(query));
	}

	@GetMapping("/pages_phone")
	@ApiOperation(value = "移动端订单交易开平仓记录列表", notes = "startTime和endTime格式(yyyy-MM-dd HH:mm:ss)")
	public Response<PageInfo<FuturesTradeActionViewDto>> pagesPhone(int page, int size, String name, String startTime,
															   String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FuturesTradeActionQuery query = new FuturesTradeActionQuery();
		query.setPage(page);
		query.setSize(size);
		query.setPublisherId(SecurityUtil.getUserId());
		query.setName(name);
		if (!StringUtil.isEmpty(startTime)) {
			try {
				query.setStartTime(sdf.parse(startTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		if (!StringUtil.isEmpty(endTime)) {
			try {
				query.setEndTime(sdf.parse(endTime));
			} catch (ParseException e) {
				throw new ServiceException(ExceptionConstant.ARGUMENT_EXCEPTION);
			}
		}
		query.setStates(new FuturesTradeEntrustState[] { FuturesTradeEntrustState.Success, FuturesTradeEntrustState.PartSuccess});
		return new Response<>(futuresTradeActionBusiness.pagesPhone(query));
	}


}
