package com.waben.stock.applayer.tactics.controller.futures;

import com.waben.stock.applayer.tactics.business.futures.FuturesTradeEntrustBusiness;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.FuturesTradeEntrustDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
import com.waben.stock.interfaces.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @author chenk 2018/7/27
 */
@RestController
@RequestMapping("/futures_trade_entrust")
@Api(description = "交易委托")
public class FuturesTradeEntrustController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesTradeEntrustBusiness futuresTradeEntrustBusiness;

	@PostMapping("/cancelEntrust/{entrustId}")
	@ApiOperation(value = "用户取消委托", notes = "entrustId为委托Id")
	public Response<FuturesTradeEntrustDto> cancelEntrust(@PathVariable Long entrustId) {
		return new Response<>(futuresTradeEntrustBusiness.cancelEntrust(entrustId, SecurityUtil.getUserId()));
	}

	@GetMapping("/pages")
	@ApiOperation(value = "交易委托列表")
	public Response<PageInfo<FuturesTradeEntrustDto>> pages(int page, int size) {
		FuturesTradeEntrustQuery query = new FuturesTradeEntrustQuery();
		query.setPage(page);
		query.setSize(size);
		query.setPublisherId(SecurityUtil.getUserId());
		return new Response<>(futuresTradeEntrustBusiness.pages(query));
	}

	@GetMapping("/pages_phone/entrust")
	@ApiOperation(value = "移动端交易委托列表", notes = "startTime和endTime格式(yyyy-MM-dd HH:mm:ss)")
	public Response<PageInfo<FuturesTradeEntrustDto>> pagesPhoneEntrust(int page, int size, String name, String startTime,
																	String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FuturesTradeEntrustQuery query = new FuturesTradeEntrustQuery();
		query.setPage(page);
		query.setSize(size);
		query.setPublisherId(SecurityUtil.getUserId());
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
		return new Response<>(futuresTradeEntrustBusiness.pagesPhoneEntrust(query));
	}

	@GetMapping("/pages_phone/action")
	@ApiOperation(value = "移动端结算列表", notes = "startTime和endTime格式(yyyy-MM-dd HH:mm:ss)")
	public Response<PageInfo<FuturesTradeEntrustDto>> pagesPhoneAction(int page, int size, String name, String startTime,
																 String endTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FuturesTradeEntrustQuery query = new FuturesTradeEntrustQuery();
		query.setPage(page);
		query.setSize(size);
		query.setPublisherId(SecurityUtil.getUserId());
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
		return new Response<>(futuresTradeEntrustBusiness.pagesPhoneAction(query));
	}

	@GetMapping("/detail/{id}")
	@ApiOperation(value = "移动端交易委托明细", notes = "startTime和endTime格式(yyyy-MM-dd HH:mm:ss)")
	public Response<FuturesTradeEntrustDto> detail(@PathVariable(value = "id") Long id) {
		return new Response<>(futuresTradeEntrustBusiness.detail(id));
	}

}
