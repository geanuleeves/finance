package com.waben.stock.datalayer.futures.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
import com.waben.stock.interfaces.dto.futures.FuturesOvernightRecordDto;
import com.waben.stock.interfaces.dto.futures.TurnoverStatistyRecordDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesOrderInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/order")
@Api(description = "期货订单接口列表")
public class FuturesOrderController implements FuturesOrderInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderService futuresOrderService;
	
	@Autowired
	private FuturesOvernightRecordService recordService;

	@Override
	public Response<PageInfo<FuturesOrderDto>> pagesOrder(@RequestBody FuturesOrderQuery orderQuery) {
		Page<FuturesOrder> page = futuresOrderService.pagesOrder(orderQuery);
		PageInfo<FuturesOrderDto> result = PageToPageInfo.pageToPageInfo(page, FuturesOrderDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<FuturesOrderDto> addOrder(@RequestBody FuturesOrderDto futuresOrderDto) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesOrderDto.class,
				futuresOrderService.save(CopyBeanUtils.copyBeanProperties(FuturesOrder.class, futuresOrderDto, false),
						futuresOrderDto.getContractId()),
				false));
	}

	@Override
	public Response<Integer> countOrderType(Long contractId, FuturesOrderType orderType) {
		return new Response<>(futuresOrderService.countOrderType(contractId, orderType));
	}

	@Override
	public Response<Integer> sumByListOrderContractIdAndPublisherId(@PathVariable Long contractId,
			@PathVariable Long publisherId, Integer type) {
		return new Response<>(
				futuresOrderService.sumByListOrderContractIdAndPublisherId(contractId, publisherId, type));
	}

	@Override
	public Response<FuturesOrderDto> cancelOrder(@PathVariable Long id, Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesOrderDto.class,
				futuresOrderService.cancelOrder(id, publisherId), false));
	}

	@Override
	public Response<FuturesOrderDto> applyUnwind(@PathVariable Long id, String sellingPriceTypeIndex,
			BigDecimal sellingEntrustPrice, Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(
				FuturesOrderDto.class, futuresOrderService.applyUnwind(id,
						FuturesTradePriceType.getByIndex(sellingPriceTypeIndex), sellingEntrustPrice, publisherId),
				false));
	}

	@Override
	public Response<Void> applyUnwindAll(@PathVariable Long publisherId) {
		futuresOrderService.applyUnwindAll(publisherId);
		return new Response<>();
	}

	@Override
	public Response<FuturesOrderDto> backhandUnwind(@PathVariable Long id, Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesOrderDto.class,
				futuresOrderService.backhandUnwind(id, publisherId), false));
	}

	@Override
	public Response<FuturesOrderDto> fetchById(@PathVariable Long id) {
		return new Response<>(
				CopyBeanUtils.copyBeanProperties(FuturesOrderDto.class, futuresOrderService.findById(id), false));
	}

	@Override
	public Response<FuturesOrderDto> settingStopLoss(@PathVariable Long orderId, Integer limitProfitType,
			BigDecimal perUnitLimitProfitAmount, Integer limitLossType, BigDecimal perUnitLimitLossAmount,
			Long publisherId, Long stopLossOrProfitId) {
		return new Response<>(
				CopyBeanUtils
						.copyBeanProperties(FuturesOrderDto.class,
								futuresOrderService.settingStopLoss(orderId, limitProfitType, perUnitLimitProfitAmount,
										limitLossType, perUnitLimitLossAmount, publisherId, stopLossOrProfitId),
								false));
	}

	@Override
	public Response<TurnoverStatistyRecordDto> getTurnoverStatisty(Long publisherId) {
		return new Response<>(futuresOrderService.getTurnoverStatisty(publisherId));
	}

	@Override
	public Response<BigDecimal> getUnsettledProfitOrLoss(@PathVariable Long publisherId) {
		return new Response<>(futuresOrderService.getUnsettledProfitOrLoss(publisherId));
	}

	@Override
	public Response<FuturesOvernightRecordDto> fetchByOvernightId(Long id) {
		FuturesOvernightRecord result = recordService.retrieve(id);
		FuturesOvernightRecordDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesOvernightRecordDto(), false);
		return new Response<>(response);
	}

}
