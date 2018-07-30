package com.waben.stock.datalayer.futures.controller;

import com.waben.stock.datalayer.futures.entity.*;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesContractOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.dto.futures.*;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.param.futures.PlaceOrderParam;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesOrderInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.PageToPageInfo;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/order")
@Api(description = "期货订单接口列表")
public class FuturesOrderController implements FuturesOrderInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderService futuresOrderService;

	@Autowired
	private FuturesOvernightRecordService recordService;

	@Autowired
	private QuoteContainer quoteContainer;

	@Autowired
	private FuturesCommodityService futuresCommodityService;

	@Autowired
	private FuturesContractOrderService futuresContractOrderService;


	@Override
	public Response<PageInfo<FuturesOrderDto>> pagesOrder(@RequestBody FuturesOrderQuery orderQuery) {
		Page<FuturesOrder> page = futuresOrderService.pagesOrder(orderQuery);
		PageInfo<FuturesOrderDto> result = PageToPageInfo.pageToPageInfo(page, FuturesOrderDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<FuturesTradeEntrustDto> placeOrder(@RequestBody PlaceOrderParam orderParam) {
		logger.info("发布人{}期货下单{}，手数{}!", orderParam.getPublisherId(), orderParam.getContractId(),
				orderParam.getTotalQuantity());
		return new Response<>(CopyBeanUtils.copyBeanProperties(FuturesTradeEntrustDto.class,
				futuresOrderService.placeOrder(orderParam), false));
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
		return new Response<>();
	}

	@Override
	public Response<Void> applyUnwind(@PathVariable Long contractId, String orderTypeIndex,
			String sellingPriceTypeIndex, BigDecimal sellingEntrustPrice, Long publisherId) {
		futuresOrderService.applyUnwind(contractId, FuturesOrderType.getByIndex(orderTypeIndex),
				FuturesTradePriceType.getByIndex(sellingPriceTypeIndex), sellingEntrustPrice, publisherId);
		return new Response<>();
	}

	@Override
	public Response<Void> applyUnwindAll(@PathVariable Long publisherId) {
		return new Response<>();
	}

	@Override
	public Response<FuturesOrderDto> backhandUnwind(@PathVariable Long id, Long publisherId) {
		return new Response<>();
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
		return new Response<>();
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
	public Response<FuturesOvernightRecordDto> fetchByOvernightId(@PathVariable Long id) {
		FuturesOvernightRecord result = recordService.retrieve(id);
		FuturesOvernightRecordDto response = CopyBeanUtils.copyBeanProperties(result, new FuturesOvernightRecordDto(),
				false);
		return new Response<>(response);
	}

	@Override
	public Response<Integer> countByPublisherId(@PathVariable Long publisherId) {
		return new Response<>(futuresOrderService.countByPublisherId(publisherId));
	}

	@Override
	public Response<Void> settingProfitAndLossLimit(@PathVariable Long publisherId, @PathVariable Long contractId,
			String orderTypeIndex, Integer limitProfitType, BigDecimal perUnitLimitProfitAmount, Integer limitLossType,
			BigDecimal perUnitLimitLossAmount) {
		futuresOrderService.settingProfitAndLossLimit(publisherId, contractId,
				FuturesOrderType.getByIndex(orderTypeIndex), limitProfitType, perUnitLimitProfitAmount, limitLossType,
				perUnitLimitLossAmount);
		return new Response<>();
	}

	public Response<BigDecimal> getTotalFloatingProfitAndLoss(@PathVariable Long publisherId) {
		FuturesContractOrderQuery futuresContractOrderQuery = new FuturesContractOrderQuery();
		futuresContractOrderQuery.setPublisherId(publisherId);
		Page<FuturesContractOrder> page = futuresContractOrderService.pages(futuresContractOrderQuery);
		PageInfo<FuturesContractOrderViewDto> result = PageToPageInfo.pageToPageInfo(page, FuturesContractOrderViewDto.class);
		BigDecimal totalFloatingProfitAndLoss = new BigDecimal(0);
		if (result != null && result.getContent() != null) {
			for (int i = 0; i < result.getContent().size(); i++) {
				FuturesContractOrder futuresContractOrder = page.getContent().get(i);
				FuturesCommodity futuresCommodity = futuresCommodityService.retrieveByCommodityNo(
						futuresContractOrder.getCommodityNo());
				//已成交部分最新均价
				BigDecimal lastPrice = quoteContainer.getLastPrice(futuresContractOrder.getCommodityNo(),
						futuresContractOrder.getContractNo());
				if (futuresCommodity != null) {
					//成交价格-买涨
					BigDecimal avgUpFillPrice = futuresOrderService.getAvgFillPrice(futuresContractOrder.getPublisherId(),
							futuresContractOrder.getContractNo(), futuresContractOrder.getCommodityNo(),
							FuturesOrderType.BuyUp.getIndex());
					//成交价格-买跌
					BigDecimal avgFallFillPrice = futuresOrderService.getAvgFillPrice(futuresContractOrder.getPublisherId(),
							futuresContractOrder.getContractNo(), futuresContractOrder.getCommodityNo(),
							FuturesOrderType.BuyFall.getIndex());
					//买涨浮动盈亏
					BigDecimal buyUpFloatingProfitAndLoss = lastPrice.subtract(avgUpFillPrice).divide(futuresCommodity.getMinWave())
							.multiply(futuresCommodity.getPerWaveMoney()).multiply(futuresContractOrder.getBuyUpQuantity());
					//买跌浮动盈亏
					BigDecimal buyFallFloatingProfitAndLoss = lastPrice.subtract(avgFallFillPrice).divide(futuresCommodity.getMinWave())
							.multiply(futuresCommodity.getPerWaveMoney()).multiply(futuresContractOrder.getBuyFallQuantity());
					totalFloatingProfitAndLoss.add(buyUpFloatingProfitAndLoss).add(buyFallFloatingProfitAndLoss);
				}
			}
		}
		return new Response<>(totalFloatingProfitAndLoss);
	}

}
