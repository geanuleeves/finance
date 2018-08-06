package com.waben.stock.datalayer.futures.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.service.FuturesCommodityService;
import com.waben.stock.datalayer.futures.service.FuturesContractOrderService;
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
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.futures.FuturesTradeInterface;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;
import com.waben.stock.interfaces.service.organization.OrganizationPublisherInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.util.PageToPageInfo;
import com.waben.stock.interfaces.util.StringUtil;

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

	@Autowired
	@Qualifier("futuresCurrencyRateInterface")
	private FuturesCurrencyRateInterface futuresCurrencyRateInterface;

	@Autowired
	@Qualifier("organizationPublisherInterface")
	private OrganizationPublisherInterface organizationPublisherInterface;

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface organizationInterface;

	@Autowired
	private FuturesContractOrderService futuresContractOrderService;

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
		Page<FuturesContractOrder> page = futuresContractOrderService.pages(query);
		PageInfo<FuturesOrderAdminDto> result = PageToPageInfo.pageToPageInfo(page, FuturesOrderAdminDto.class);
		for (int i = 0; i < page.getContent().size(); i++) {
			FuturesContractOrder order = page.getContent().get(i);
			result.getContent().get(i).setSymbol(order.getCommodityNo());
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
			/*
			 * if (order.getOrderType() != null) {
			 * result.getContent().get(i).setOrderType(order.getOrderType().
			 * getType()); } if (order.getState() != null) {
			 * result.getContent().get(i).setState(order.getState().getType());
			 * }
			 */
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
			/*
			 * if (order.getState().getIndex().equals("9")) { //
			 * result.getContent().get(i).setProfit(order.getProfitOrLoss()); //
			 * result.getContent().get(i).setSellingProfit(order.getProfitOrLoss
			 * ()); // if (order.getSellingTime() != null) { // Long laseDate =
			 * order.getSellingTime().getTime(); // Long date =
			 * order.getBuyingTime().getTime(); // Long hours = ((laseDate -
			 * date) % (1000 * 60 * 60 * 24)) / // (1000 * 60); // if
			 * (Math.abs(hours.intValue()) > 60) { // Long stime = hours / 60;
			 * // result.getContent().get(i).setPositionDays(stime.toString() +
			 * // "小时"); // } else { //
			 * result.getContent().get(i).setPositionDays(hours.toString() + //
			 * "分钟"); // } // } } else { List<FuturesTradeLimit> limit =
			 * limitService.findByContractId(order.getContract().getId()); if
			 * (limit != null) { for (int j = 0; j < limit.size(); j++) { //
			 * TODO 需求修改，风控限制关联到合约，开始和结束时间精确到日、时、分、秒 if
			 * (result.getContent().get(i).getWindControlState() == null &&
			 * "".equals(result.getContent().get(i).getWindControlState())) {
			 * result.getContent().get(i).setWindControlState("正常"); } } }
			 * 
			 * }
			 */
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
		List<FuturesHoldPositionAgentDto> orderList = result.getContent();
		List<FuturesHoldPositionAgentDto> positionAgentList = new ArrayList<FuturesHoldPositionAgentDto>();
		for (FuturesHoldPositionAgentDto positionAgent : orderList) {
			if (!StringUtil.isEmpty(query.getPublisherName())) {
				if (positionAgent.getPublisherName().equals(query.getPublisherName())) {
					positionAgentList.add(positionAgent);
				}
			}
			if (!StringUtil.isEmpty(query.getPublisherPhone())) {
				if (positionAgent.getPublisherPhone().equals(query.getPublisherPhone())) {
					positionAgentList.add(positionAgent);
				}

			}
			if (query.getPublisherPhone() == null && query.getPublisherName() == null) {
				positionAgentList.add(positionAgent);
			}
		}
		result.setContent(positionAgentList);
		List<FuturesHoldPositionAgentDto> futuresContractOrderViewDtos = new ArrayList<FuturesHoldPositionAgentDto>();
		if (result != null && result.getContent() != null) {
			// 封装汇率
			Map<String, FuturesCurrencyRateDto> rateMap = new HashMap<String, FuturesCurrencyRateDto>();
			List<FuturesCurrencyRateDto> rateList = getListCurrencyRate();
			for (FuturesCurrencyRateDto futuresCurrencyRateDto : rateList) {
				rateMap.put(futuresCurrencyRateDto.getCurrency(), futuresCurrencyRateDto);
			}
			for (int i = 0; i < result.getContent().size(); i++) {
				FuturesHoldPositionAgentDto futuresContractOrderViewDto = result.getContent().get(i);
				FuturesContractOrder futuresContractOrder = page.getContent().get(i);
				// 合约id
				futuresContractOrderViewDto.setContractId(futuresContractOrder.getContract().getId());
				// 拷贝两份出来
				try {
					FuturesCommodity futuresCommodity = futuresCommodityService
							.retrieveByCommodityNo(futuresContractOrder.getCommodityNo());
					RealNameDto realName = realnameInterface
							.fetchByResourceId(futuresContractOrderViewDto.getPublisherId()).getResult();
					PublisherDto publisher = publisherInterface.fetchById(futuresContractOrderViewDto.getPublisherId())
							.getResult();
					OrganizationPublisherDto orgPublisher = organizationPublisherInterface
							.fetchOrgPublisher(futuresContractOrderViewDto.getPublisherId()).getResult();
					// 合约名称
					futuresContractOrderViewDto
							.setCommodityName(futuresCommodity != null ? futuresCommodity.getName() : "");
					futuresContractOrderViewDto
							.setUnwindPointType(futuresCommodity != null ? futuresCommodity.getUnwindPointType() : 0);
					futuresContractOrderViewDto.setPerUnitUnwindPoint(
							futuresCommodity != null ? futuresCommodity.getPerUnitUnwindPoint() : new BigDecimal(0));
					// 已成交部分最新均价
					BigDecimal lastPrice = quoteContainer.getLastPrice(futuresContractOrder.getCommodityNo(),
							futuresContractOrder.getContractNo());
					lastPrice = lastPrice == null ? new BigDecimal(0) : lastPrice;
					if (query.getOrderType() == null
							|| (query.getOrderType() != null && query.getOrderType().equals("1"))) {
						// 买涨
						FuturesHoldPositionAgentDto buyDto = futuresContractOrderViewDto.deepClone();
						if (realName != null) {
							buyDto.setPublisherName(realName.getName());
						}
						if (publisher != null) {
							buyDto.setPublisherPhone(publisher.getPhone());
						}
						if (orgPublisher != null) {
							OrganizationDto org = organizationInterface.fetchByOrgId(orgPublisher.getOrgId())
									.getResult();
							if (org != null) {
								buyDto.setCode(org.getCode());
								buyDto.setOrgName(org.getName());
							}
						}

						buyDto.setCommodityName(futuresCommodity.getName());
						buyDto.setCommodityCurrency(futuresCommodity.getCurrency());
						buyDto.setCommoditySymbol(futuresCommodity.getSymbol());
						buyDto.setContractId(futuresContractOrder.getContract().getId());

						buyDto.setPerUnitLimitLossAmount(futuresContractOrder.getBuyUpPerUnitLimitLossAmount());
						buyDto.setPerUnitLimitProfitAmount(futuresContractOrder.getBuyUpPerUnitLimitProfitAmount());

						// 订单类型
						buyDto.setOrderType(FuturesOrderType.BuyUp);
						// 买涨手数
						buyDto.setBuyUpQuantity(futuresContractOrder.getBuyUpQuantity());
						// 今持仓
						Integer findUpFilledNow = futuresTradeActionService.findFilledNow(
								futuresContractOrder.getPublisherId(), futuresContractOrder.getCommodityNo(),
								futuresContractOrder.getContractNo(), FuturesTradeActionType.OPEN.getIndex(),
								FuturesOrderType.BuyUp.getIndex());
						// 浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格*手数
						if (futuresCommodity != null) {
							buyDto.setQuantityNow(new BigDecimal(findUpFilledNow == null ? 0 : findUpFilledNow));
							// 成交价格
							BigDecimal avgUpFillPrice = futuresOrderService.getOpenAvgFillPrice(
									futuresContractOrder.getPublisherId(), futuresContractOrder.getId(), FuturesOrderType.BuyUp.getIndex());
							avgUpFillPrice = avgUpFillPrice == null ? new BigDecimal(0) : avgUpFillPrice;

							buyDto.setAvgFillPrice(avgUpFillPrice);
							// 最新价
							buyDto.setLastPrice(lastPrice);
							// 最小波动
							buyDto.setMinWave(futuresCommodity.getMinWave());
							// 最小波动价格
							buyDto.setPerWaveMoney(futuresCommodity.getPerWaveMoney());
							// 查询汇率
							FuturesCurrencyRateDto rate = rateMap.get(futuresCommodity.getCurrency());
							buyDto.setRate(rate.getRate());
							buyDto.setCurrencySign(rate.getCurrencySign());
							buyDto.setFloatingProfitAndLoss(lastPrice.subtract(avgUpFillPrice)
									.divide(futuresCommodity.getMinWave()).multiply(futuresCommodity.getPerWaveMoney())
									.multiply(futuresContractOrder.getBuyUpQuantity()));
							buyDto.setServiceFee(futuresCommodity.getOpenwindServiceFee()
									.add(futuresCommodity.getUnwindServiceFee()));
							if (futuresContractOrder.getBuyUpQuantity()
									.compareTo(futuresContractOrder.getBuyFallQuantity()) > 0) {
								buyDto.setReserveFund(futuresCommodity.getPerUnitReserveFund()
										.multiply(futuresContractOrder.getBuyUpQuantity()));
							}
							futuresContractOrderViewDtos.add(buyDto);

						}
					}

					if (query.getOrderType() == null
							|| (query.getOrderType() != null && query.getOrderType().equals("2"))) {
						// 买跌
						FuturesHoldPositionAgentDto sellDto = futuresContractOrderViewDto.deepClone();
						if (realName != null) {
							sellDto.setPublisherName(realName.getName());
						}
						if (publisher != null) {
							sellDto.setPublisherPhone(publisher.getPhone());
						}
						if (orgPublisher != null) {
							OrganizationDto org = organizationInterface.fetchByOrgId(orgPublisher.getOrgId())
									.getResult();
							if (org != null) {
								sellDto.setCode(org.getCode());
								sellDto.setOrgName(org.getName());
							}
						}

						sellDto.setCommodityName(futuresCommodity.getName());
						sellDto.setCommodityCurrency(futuresCommodity.getCurrency());
						sellDto.setCommoditySymbol(futuresCommodity.getSymbol());
						sellDto.setContractId(futuresContractOrder.getContract().getId());

						sellDto.setPerUnitLimitLossAmount(futuresContractOrder.getBuyFallPerUnitLimitLossAmount());
						sellDto.setPerUnitLimitProfitAmount(futuresContractOrder.getBuyFallPerUnitLimitProfitAmount());

						sellDto.setOrderType(FuturesOrderType.BuyFall);
						sellDto.setBuyFallQuantity(futuresContractOrder.getBuyFallQuantity());
						// 今持仓
						Integer findFallFilledNow = futuresTradeActionService.findFilledNow(
								futuresContractOrder.getPublisherId(), futuresContractOrder.getCommodityNo(),
								futuresContractOrder.getContractNo(), FuturesTradeActionType.CLOSE.getIndex(),
								FuturesOrderType.BuyFall.getIndex());
						// 浮动盈亏 (最新价格-成交价格)/波动*每笔波动价格*手数
						if (futuresCommodity != null) {
							sellDto.setQuantityNow(new BigDecimal(findFallFilledNow == null ? 0 : findFallFilledNow));
							// 成交价格
							BigDecimal avgFallFillPrice = futuresOrderService.getOpenAvgFillPrice(
									futuresContractOrder.getPublisherId(), futuresContractOrder.getId(), FuturesOrderType.BuyFall.getIndex());
							avgFallFillPrice = avgFallFillPrice == null ? new BigDecimal(0) : avgFallFillPrice;
							sellDto.setAvgFillPrice(avgFallFillPrice);
							sellDto.setLastPrice(lastPrice);
							// 最小波动
							sellDto.setMinWave(futuresCommodity.getMinWave());
							// 最小波动价格
							sellDto.setPerWaveMoney(futuresCommodity.getPerWaveMoney());
							// 查询汇率
							FuturesCurrencyRateDto rate = rateMap.get(futuresCommodity.getCurrency());
							sellDto.setRate(rate.getRate());
							sellDto.setCurrencySign(rate.getCurrencySign());
							sellDto.setFloatingProfitAndLoss(lastPrice.subtract(avgFallFillPrice)
									.divide(futuresCommodity.getMinWave()).multiply(futuresCommodity.getPerWaveMoney()
											.multiply(futuresContractOrder.getBuyFallQuantity())));
							sellDto.setServiceFee(futuresCommodity.getOpenwindServiceFee()
									.add(futuresCommodity.getUnwindServiceFee()));
							if (futuresContractOrder.getBuyFallQuantity()
									.compareTo(futuresContractOrder.getBuyUpQuantity()) > 0) {
								sellDto.setReserveFund(futuresCommodity.getPerUnitReserveFund()
										.multiply(futuresContractOrder.getBuyUpQuantity()));
							}

							futuresContractOrderViewDtos.add(sellDto);
						}

					}
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

	public List<FuturesCurrencyRateDto> getListCurrencyRate() {
		Response<PageInfo<FuturesCurrencyRateDto>> response = futuresCurrencyRateInterface.list();
		if ("200".equals(response.getCode())) {
			return response.getResult().getContent();
		}
		throw new ServiceException(response.getCode());
	}

}
