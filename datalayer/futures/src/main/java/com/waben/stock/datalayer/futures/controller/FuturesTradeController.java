package com.waben.stock.datalayer.futures.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.datalayer.futures.service.FuturesTradeLimitService;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesTradeInterface;
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
	private FuturesCurrencyRateService rateService;

	@Autowired
	private FuturesTradeLimitService limitService;

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
				FuturesContractMarket market = RetriveFuturesOverHttp.market(symbol, order.getContractNo());
				if (market != null) {
					result.getContent().get(i).setLastPrice(market.getLastPrice());
				}
			}
			result.getContent().get(i).setEntrustAppointPrice(order.getBuyingEntrustPrice());
			if (order.getState().getIndex().equals("8")) {
				result.getContent().get(i).setDealTime(order.getSellingTime());
			} else {
				result.getContent().get(i).setDealTime(order.getBuyingTime());
			}
		}
		return new Response<>(result);
	}

	@Override
	public Response<PageInfo<FuturesOrderAdminDto>> adminPagesByQuery(@RequestBody FuturesTradeAdminQuery query) {
		// Page<FuturesOrderAdminDto> page =
		// futuresOrderService.adminPagesByQuery(query);
		Page<FuturesOrder> page = futuresOrderService.pagesOrderAdmin(query);
		PageInfo<FuturesOrderAdminDto> result = PageToPageInfo.pageToPageInfo(page, FuturesOrderAdminDto.class);
		for (int i = 0; i < page.getContent().size(); i++) {
			FuturesOrder order = page.getContent().get(i);
			result.getContent().get(i).setSymbol(order.getCommoditySymbol());
			result.getContent().get(i).setName(order.getCommodityName());
			List<FuturesOvernightRecord> recordList = overnightService.findAll(order);
			double count = 0.00;
			for (FuturesOvernightRecord futuresOvernightRecord : recordList) {
				count += futuresOvernightRecord.getOvernightDeferredFee().doubleValue();
			}
			result.getContent().get(i).setPublisherId(order.getPublisherId());
			result.getContent().get(i).setOvernightServiceFee(new BigDecimal(count));
			if (order.getWindControlType() != null) {
				result.getContent().get(i).setWindControlType(order.getWindControlType().getType());
			}
			if (order.getSellingTime() != null) {
				result.getContent().get(i).setPositionEndTime(order.getSellingTime());
			}
			if (order.getOrderType() != null) {
				result.getContent().get(i).setOrderType(order.getOrderType().getType());
			}
			if (order.getState() != null) {
				result.getContent().get(i).setState(order.getState().getType());
			}
			if (order.getBuyingTime() != null) {
				Long date = order.getBuyingTime().getTime();
				Long current = new Date().getTime();

				Long hours = (current - date) / (60 * 60 * 1000);
				result.getContent().get(i).setPositionDays(Math.abs(hours.intValue()));
			}
//			FuturesCurrencyRate rate = rateService.queryByName(order.getContract().getCommodity().getCurrency());
			if (order.getState().getIndex().equals("9")) {
				result.getContent().get(i).setProfit(order.getProfitOrLoss());
				result.getContent().get(i).setSellingProfit(order.getProfitOrLoss());
				if (order.getSellingTime() != null) {
					Long laseDate = order.getSellingTime().getTime();
					Long date = order.getBuyingTime().getTime();
					Long hours = (laseDate - date) / (60 * 60 * 1000);
					result.getContent().get(i).setPositionDays(Math.abs(hours.intValue()));
				}
			} else {
				/*FuturesContractMarket market = RetriveFuturesOverHttp.market(order.getCommoditySymbol(),
						order.getContractNo());
				if (market != null) {
					BigDecimal lastPrice = market.getLastPrice();
					if (lastPrice != null) {
						BigDecimal profit = futuresOrderService.computeProfitOrLoss(order.getOrderType(),
								order.getTotalQuantity(), order.getBuyingPrice(), lastPrice,
								order.getContract().getCommodity().getMinWave(),
								order.getContract().getCommodity().getPerWaveMoney());
						if (rate != null && rate.getRate() != null) {
							result.getContent().get(i).setProfit(profit.multiply(rate.getRate()));
						}
					}
				}*/
				List<FuturesTradeLimit> limit = limitService.findByContractId(order.getContract().getId());
				if (limit != null) {
//					Date da = new Date();
//					String curreyDay = sdf.format(da);
//					int weekDays = getWeekOfDate(da);
					for (int j = 0; j < limit.size(); j++) {
						// TODO 需求修改，风控限制关联到合约，开始和结束时间精确到日、时、分、秒
						// if (limit.get(j).getWeekDay() == weekDays) {
						// try {
						// Date start = null;
						// Date end = null;
						// if (limit.get(j).getStartLimitTime() != null
						// && !"".equals(limit.get(j).getStartLimitTime())) {
						// String startStr = curreyDay + " " +
						// limit.get(j).getStartLimitTime();
						// start = sdf1.parse(startStr);
						// }
						// if (limit.get(j).getEndLimitTime() != null
						// && !"".equals(limit.get(j).getEndLimitTime())) {
						// String endStr = curreyDay + " " +
						// limit.get(j).getEndLimitTime();
						// end = sdf1.parse(endStr);
						// }
						// if (start != null && end != null) {
						// if (da.getTime() > start.getTime() && da.getTime() <
						// end.getTime()) {
						// result.getContent().get(i)
						// .setWindControlState(limit.get(j).getLimitType().getType());
						// } else {
						// result.getContent().get(i).setWindControlState("正常");
						// }
						// }
						// } catch (ParseException e) {
						// e.printStackTrace();
						// }
						// }
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

	/*private int getWeekOfDate(Date date) {
		// String[] weekDays = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
		// };
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int w = cal.get(Calendar.DAY_OF_WEEK);
		if (w < 0)
			w = 0;
		return w;
	}*/

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
//		FuturesOrderCountDto dto = new FuturesOrderCountDto();
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

}
