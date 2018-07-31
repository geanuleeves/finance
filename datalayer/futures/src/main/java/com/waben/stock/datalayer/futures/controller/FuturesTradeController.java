package com.waben.stock.datalayer.futures.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.datalayer.futures.service.FuturesTradeActionService;
import com.waben.stock.datalayer.futures.service.FuturesTradeLimitService;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.admin.futures.AgentOrderRecordDto;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesHoldPositionAgentDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeActionAgentDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.util.PageToPageInfo;

@RestController
@RequestMapping("/futuresTrade")
public class FuturesTradeController implements FuturesTradeInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderService futuresOrderService;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	@Autowired
	private FuturesTradeLimitService limitService;

	@Autowired
	private ProfileBusiness profileBusiness;

	@Autowired
	private FuturesOvernightRecordService overnightRecordService;

	@Autowired
	private FuturesTradeActionService futuresTradeActionService;

	@Autowired
	private QuoteContainer quoteContainer;

	@Autowired
	private FuturesCommodityService futuresCommodityService;

	@Autowired
	@Qualifier("realNameInterface")
	private RealNameInterface realnameInterface;

	@Autowired
	@Qualifier("publisherInterface")
	private PublisherInterface publisherInterface;

	SimpleDateFormat dateFm = new SimpleDateFormat("HH:mm:ss");

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public Response<PageInfo<FutresOrderEntrustDto>> pagesOrderEntrust(@RequestBody FuturesTradeAdminQuery query) {
		Page<FuturesOrder> page = futuresOrderService.pagesOrderAdmin(query);
		PageInfo<FutresOrderEntrustDto> result = PageToPageInfo.pageToPageInfo(page, FutresOrderEntrustDto.class);
		List<FuturesOrder> orderList = page.getContent();
		for (int i = 0; i < orderList.size(); i++) {
			FuturesOrder order = orderList.get(i);

			result.getContent().get(i).setSymbol(order.getCommoditySymbol());
			result.getContent().get(i).setName(order.getCommodityName());
			result.getContent().get(i).setPublisherId(order.getPublisherId());
			if (order.getOrderType() != null) {
				result.getContent().get(i).setOrderType(order.getOrderType().getType());
			}
			if (order.getState() != null) {
				result.getContent().get(i).setState(order.getState().getType());
			}
			if (order.getCommoditySymbol() != null && !"".equals(order.getCommoditySymbol())) {
				String symbol = order.getCommoditySymbol();
				FuturesContractMarket market = RetriveFuturesOverHttp.market(profileBusiness.isProd(), symbol,
						order.getContractNo());
				if (market != null) {
					result.getContent().get(i).setLastPrice(market.getLastPrice());
				}
			}
			// result.getContent().get(i).setEntrustAppointPrice(order.getBuyingEntrustPrice());
			// if (order.getState().getIndex().equals("8")) {
			// result.getContent().get(i).setDealTime(order.getSellingTime());
			// } else {
			// result.getContent().get(i).setDealTime(order.getBuyingTime());
			// }
		}
		return new Response<>(result);
	}

	@Override
	public Response<PageInfo<FuturesOrderAdminDto>> adminPagesByQuery(@RequestBody FuturesTradeAdminQuery query) {
		Page<FuturesOrder> page = futuresOrderService.pagesOrderAdmin(query);
		PageInfo<FuturesOrderAdminDto> result = PageToPageInfo.pageToPageInfo(page, FuturesOrderAdminDto.class);
		for (int i = 0; i < page.getContent().size(); i++) {
			FuturesOrder order = page.getContent().get(i);
			result.getContent().get(i).setSymbol(order.getCommoditySymbol());
			result.getContent().get(i).setName(order.getCommodityName());
			List<FuturesOvernightRecord> recordList = overnightService.findAll(order);
			double count = 0.00;
			double overnightReserveFund = 0.00;
			for (FuturesOvernightRecord futuresOvernightRecord : recordList) {
				count += futuresOvernightRecord.getOvernightDeferredFee().doubleValue();
				overnightReserveFund += futuresOvernightRecord.getOvernightReserveFund() == null ? 0.00
						: futuresOvernightRecord.getOvernightReserveFund().doubleValue();
			}
			result.getContent().get(i).setPublisherId(order.getPublisherId());
			result.getContent().get(i).setOvernightServiceFee(new BigDecimal(count));
			result.getContent().get(i).setOvernightReserveFund(new BigDecimal(overnightReserveFund));
			// if (order.getWindControlType() != null) {
			// result.getContent().get(i).setWindControlType(order.getWindControlType().getType());
			// }
			// if (order.getSellingTime() != null) {
			// result.getContent().get(i).setPositionEndTime(order.getSellingTime());
			// }
			if (order.getOrderType() != null) {
				result.getContent().get(i).setOrderType(order.getOrderType().getType());
			}
			if (order.getState() != null) {
				result.getContent().get(i).setState(order.getState().getType());
			}
			// if (order.getBuyingTime() != null) {
			// Long date = order.getBuyingTime().getTime();
			// Long current = new Date().getTime();
			// Long hours = ((current - date) % (1000 * 60 * 60 * 24) / (1000 *
			// 60));
			// if (Math.abs(hours.intValue()) > 60) {
			// Long stime = hours / 60;
			// result.getContent().get(i).setPositionDays(stime.toString() +
			// "小时");
			// } else {
			// result.getContent().get(i).setPositionDays(hours.toString() +
			// "分钟");
			// }
			// }
			if (order.getState().getIndex().equals("9")) {
				// result.getContent().get(i).setProfit(order.getProfitOrLoss());
				// result.getContent().get(i).setSellingProfit(order.getProfitOrLoss());
				// if (order.getSellingTime() != null) {
				// Long laseDate = order.getSellingTime().getTime();
				// Long date = order.getBuyingTime().getTime();
				// Long hours = ((laseDate - date) % (1000 * 60 * 60 * 24)) /
				// (1000 * 60);
				// if (Math.abs(hours.intValue()) > 60) {
				// Long stime = hours / 60;
				// result.getContent().get(i).setPositionDays(stime.toString() +
				// "小时");
				// } else {
				// result.getContent().get(i).setPositionDays(hours.toString() +
				// "分钟");
				// }
				// }
			} else {
				List<FuturesTradeLimit> limit = limitService.findByContractId(order.getContract().getId());
				if (limit != null) {
					for (int j = 0; j < limit.size(); j++) {
						// TODO 需求修改，风控限制关联到合约，开始和结束时间精确到日、时、分、秒
						if (result.getContent().get(i).getWindControlState() == null
								&& "".equals(result.getContent().get(i).getWindControlState())) {
							result.getContent().get(i).setWindControlState("正常");
						}
					}
				}

			}
		}
		return new Response<>(result);
	}

	/*
	 * private int getWeekOfDate(Date date) { // String[] weekDays = { "星期日",
	 * "星期一", "星期二", "星期三", "星期四", "星期五", "星期六" // }; Calendar cal =
	 * Calendar.getInstance(); cal.setTime(date); int w =
	 * cal.get(Calendar.DAY_OF_WEEK); if (w < 0) w = 0; return w; }
	 */

	@Override
	public Response<Object[]> countOrderState(String state) {
		ArrayList<Integer> list = new ArrayList<Integer>();
		if (state.indexOf(",") > 0) {
			String[] array = state.split(",");
			for (String temp : array) {
				list.add(Integer.valueOf(temp));
			}
		} else {
			list.add(Integer.valueOf(state));
		}
		List<Object> map = futuresOrderService.queryByState(list);
		// FuturesOrderCountDto dto = new FuturesOrderCountDto();
		Object[] bitge = (Object[]) map.get(0);
		for (int i = 0; i < map.size(); i++) {
			// Object[] bitge = (Object[])map.get(i);
			// String quantity =bitge[0] == null ? "" : bitge[0].toString();
			// String fund = bitge[1] == null ? "" : bitge[1].toString();
			// String fee = bitge[2] == null ? "" : bitge[2].toString();
			// String deferred = bitge[3] == null ? "" : bitge[3].toString();
			// dto.setQuantity(quantity.toString());
			// dto.setFund(fund.toString());
			// dto.setFee(fee.toString());
			// dto.setDeferred(deferred.toString());
		}
		Response<Object[]> res = new Response<Object[]>();
		res.setCode("200");
		res.setResult(bitge);
		res.setMessage("响应成功");
		return res;
	}

	@Override
	public Response<FuturesOrderCountDto> getSUMOrder(@RequestBody FuturesTradeAdminQuery query) {
		return new Response<>(limitService.getSUMOrder(query));
	}

	@Override
	public Response<PageInfo<AgentOrderRecordDto>> pagesOrderRecord(@RequestBody FuturesTradeAdminQuery query) {
		return new Response<>(
				PageToPageInfo.pageToPageInfo(futuresOrderService.pagesOrderRecord(query), AgentOrderRecordDto.class));
	}

	@Override
	public Response<BigDecimal> getSUMOvernightRecord(@PathVariable Long orderId) {
		return new Response<>(overnightRecordService.getSUMOvernightRecord(orderId));
	}

	@Override
	public Response<PageInfo<FuturesTradeActionAgentDto>> pagesOrderAgentDealRecord(
			@RequestBody FuturesTradeAdminQuery query) {
		return new Response<>(PageToPageInfo.pageToPageInfo(futuresOrderService.pagesOrderAgentDealRecord(query),
				FuturesTradeActionAgentDto.class));
	}

	@Override
	public Response<PageInfo<FuturesHoldPositionAgentDto>> pagesAgentAdmin(@RequestBody FuturesTradeAdminQuery query) {
		Page<FuturesContractOrder> page = futuresOrderService.pages(query);
		PageInfo<FuturesHoldPositionAgentDto> result = PageToPageInfo.pageToPageInfo(page,
				FuturesHoldPositionAgentDto.class);
		List<FuturesHoldPositionAgentDto> futuresContractOrderViewDtos = new ArrayList<>();
		if (result != null && result.getContent() != null) {
			for (int i = 0; i < result.getContent().size(); i++) {
				FuturesHoldPositionAgentDto futuresContractOrderViewDto = result.getContent().get(i);
				FuturesContractOrder futuresContractOrder = page.getContent().get(i);
				// 合约名称
				futuresContractOrderViewDto.setContractName(futuresContractOrder.getContract().getContractName());
				// 拷贝两份出来
				try {
					FuturesCommodity futuresCommodity = futuresCommodityService
							.retrieveByCommodityNo(futuresContractOrder.getCommodityNo());
					RealNameDto realName = realnameInterface
							.fetchByResourceId(futuresContractOrderViewDto.getPublisherId()).getResult();
					PublisherDto publisher = publisherInterface.fetchById(futuresContractOrderViewDto.getPublisherId())
							.getResult();
					// 已成交部分最新均价
					BigDecimal lastPrice = quoteContainer.getLastPrice(futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo());
					// 买涨
					FuturesHoldPositionAgentDto buyDto = futuresContractOrderViewDto.deepClone();
					if (realName != null) {
						buyDto.setPublisherName(realName.getName());
					}
					if (publisher != null) {
						buyDto.setPublisherPhone(publisher.getPhone());
					}
					buyDto.setCommodityName(futuresCommodity.getName());
					buyDto.setCommodityCurrency(futuresCommodity.getCurrency());
					buyDto.setCommoditySymbol(futuresCommodity.getSymbol());
					buyDto.setContractId(futuresContractOrder.getContract().getId());

					buyDto.setOrderType(FuturesOrderType.BuyUp);
					// 已持仓
					buyDto.setBuyUpQuantity(futuresContractOrder.getBuyUpQuantity());
					// 今持仓
					Integer findUpFilledNow = futuresTradeActionService.findFilledNow(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo(), FuturesOrderType.BuyUp.getIndex());
					buyDto.setQuantityNow(new BigDecimal(findUpFilledNow));
					// 成交价格
					BigDecimal avgUpFillPrice = futuresOrderService.getAvgFillPrice(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getContractNo(),
							futuresContractOrder.getCommodityNo(), FuturesOrderType.BuyUp.getIndex());
					buyDto.setAvgFillPrice(avgUpFillPrice);
					buyDto.setAvgFillPriceNow(lastPrice);
					// 浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格
					if (futuresCommodity != null) {
						buyDto.setFloatingProfitAndLoss(lastPrice.subtract(avgUpFillPrice)
								.divide(futuresCommodity.getMinWave()).multiply(futuresCommodity.getPerWaveMoney()));
						buyDto.setServiceFee(
								futuresCommodity.getOpenwindServiceFee().add(futuresCommodity.getUnwindServiceFee()));
						if (futuresContractOrder.getBuyUpQuantity()
								.compareTo(futuresContractOrder.getBuyFallQuantity()) > 0) {
							buyDto.setReserveFund(futuresCommodity.getPerUnitReserveFund()
									.multiply(futuresContractOrder.getBuyUpQuantity()));
						}
					}
					// 买跌
					FuturesHoldPositionAgentDto sellDto = futuresContractOrderViewDto.deepClone();
					if (realName != null) {
						sellDto.setPublisherName(realName.getName());
					}
					if (publisher != null) {
						sellDto.setPublisherPhone(publisher.getPhone());
					}
					sellDto.setCommodityName(futuresCommodity.getName());
					sellDto.setCommodityCurrency(futuresCommodity.getCurrency());
					sellDto.setCommoditySymbol(futuresCommodity.getSymbol());
					sellDto.setContractId(futuresContractOrder.getContract().getId());

					sellDto.setOrderType(FuturesOrderType.BuyFall);
					sellDto.setBuyFallQuantity(futuresContractOrder.getBuyFallQuantity());
					// 今持仓
					Integer findFallFilledNow = futuresTradeActionService.findFilledNow(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo(), FuturesOrderType.BuyFall.getIndex());
					sellDto.setQuantityNow(new BigDecimal(findFallFilledNow));
					// 成交价格
					BigDecimal avgFallFillPrice = futuresOrderService.getAvgFillPrice(
							futuresContractOrder.getPublisherId(), futuresContractOrder.getContractNo(),
							futuresContractOrder.getCommodityNo(), FuturesOrderType.BuyFall.getIndex());
					sellDto.setAvgFillPrice(avgFallFillPrice);
					sellDto.setAvgFillPriceNow(lastPrice);
					// 浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格
					if (futuresCommodity != null) {
						sellDto.setFloatingProfitAndLoss(lastPrice.subtract(avgFallFillPrice)
								.divide(futuresCommodity.getMinWave()).multiply(futuresCommodity.getPerWaveMoney()));
						sellDto.setServiceFee(
								futuresCommodity.getOpenwindServiceFee().add(futuresCommodity.getUnwindServiceFee()));
						if (futuresContractOrder.getBuyFallQuantity()
								.compareTo(futuresContractOrder.getBuyUpQuantity()) > 0) {
							sellDto.setReserveFund(futuresCommodity.getPerUnitReserveFund()
									.multiply(futuresContractOrder.getBuyUpQuantity()));
						}
					}
					futuresContractOrderViewDtos.add(buyDto);
					futuresContractOrderViewDtos.add(sellDto);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		result.setContent(futuresContractOrderViewDtos);
		return new Response<>(result);
	}

}
