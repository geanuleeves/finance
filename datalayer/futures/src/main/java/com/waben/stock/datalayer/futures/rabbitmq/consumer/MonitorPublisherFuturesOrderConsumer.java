package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorPublisherFuturesOrderMessage;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;
import com.waben.stock.interfaces.util.StringUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName })
public class MonitorPublisherFuturesOrderConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	private List<Long> monitorPublisherList = Collections.synchronizedList(new ArrayList<Long>());

	@PostConstruct
	public void init() {
		List<FuturesOrder> orderList = retrivePositionOrders();
		for (FuturesOrder order : orderList) {
			Long publisherId = order.getPublisherId();
			if (!monitorPublisherList.contains(publisherId)) {
				monitorPublisherList.add(publisherId);
			}
		}

		for (Long publisherId : monitorPublisherList) {
			MonitorPublisherFuturesOrderMessage messgeObj = new MonitorPublisherFuturesOrderMessage();
			messgeObj.setPublisherId(publisherId);
			producer.sendMessage(RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName, messgeObj);
		}
	}

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("监控用户订单:{}", message);
		}
		MonitorPublisherFuturesOrderMessage messgeObj = JacksonUtil.decode(message,
				MonitorPublisherFuturesOrderMessage.class);
		try {
			Long publisherId = messgeObj.getPublisherId();
			// step 1 : 获取资金账号
			CapitalAccountDto account = accountBusiness.fetchByPublisherId(publisherId);
			// step 2 : 获取持仓订单
			List<FuturesOrder> orderList = retrivePublisherPositionOrders(publisherId);
			if (orderList != null && orderList.size() > 0) {
				// step 3 : 判断是否达到强平
				if (isReachStongPoint(orderList, account)) {
					for (FuturesOrder order : orderList) {
						FuturesContract contract = order.getContract();
						if (orderService.isTradeTime(contract.getCommodity().getExchange().getTimeZoneGap(),
								contract)) {
							if (order.getState() == FuturesOrderState.Position) {
								orderService.sellingEntrust(order, FuturesWindControlType.ReachStrongPoint,
										FuturesTradePriceType.MKT, null);
							} else if (order.getState() == FuturesOrderState.SellingEntrust
									&& order.getSellingPriceType() == FuturesTradePriceType.LMT
									&& order.getWindControlType() != FuturesWindControlType.ReachStrongPoint) {
								order.setWindControlType(FuturesWindControlType.ReachStrongPoint);
								orderService.revisionOrder(order);
								orderService.cancelOrder(order.getId(), publisherId);
							}
						}
					}
				} else {
					// step 4 : 判断是否触发隔夜，是否足够过夜
					List<FuturesOrder> overnightOrderList = triggerOvernightOrderList(orderList);
					if (overnightOrderList != null && overnightOrderList.size() > 0) {
						if (isEnoughOvernight(orderList, account)) {
							// step 4.1 : 扣除递延费
							for (FuturesOrder order : overnightOrderList) {
								orderService.overnight(order,
										order.getContract().getCommodity().getExchange().getTimeZoneGap());
							}
						} else {
							// step 4.2 : 不满足隔夜条件，强平
							for (FuturesOrder order : overnightOrderList) {
								strongUnwind(order);
							}
						}
					}
				}
				retry(messgeObj);
			} else {
				// 从监控队列中移除
				monitorPublisherList.remove(publisherId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
	}

	private void strongUnwind(FuturesOrder order) {
		FuturesContract contract = order.getContract();
		if (orderService.isTradeTime(contract.getCommodity().getExchange().getTimeZoneGap(), contract)) {
			if (order.getState() == FuturesOrderState.Position) {
				orderService.sellingEntrust(order, FuturesWindControlType.ReachStrongPoint, FuturesTradePriceType.MKT,
						null);
			} else if (order.getState() == FuturesOrderState.SellingEntrust
					&& order.getSellingPriceType() == FuturesTradePriceType.LMT
					&& order.getWindControlType() != FuturesWindControlType.ReachStrongPoint) {
				order.setWindControlType(FuturesWindControlType.ReachStrongPoint);
				orderService.revisionOrder(order);
				orderService.cancelOrder(order.getId(), order.getPublisherId());
			}
		}
	}

	/**
	 * 判断是否触发强平
	 * 
	 * @param orderList
	 *            订单列表
	 * @param account
	 *            资金账户
	 * @return 是否触发强平
	 */
	private boolean isReachStongPoint(List<FuturesOrder> orderList, CapitalAccountDto account) {
		BigDecimal totalStrong = BigDecimal.ZERO;
		BigDecimal totalProfitOrLoss = BigDecimal.ZERO;
		for (FuturesOrder order : orderList) {
			// 计算强平金额
			totalStrong = totalStrong.add(orderService.getStrongMoney(order));
			// 计算浮动盈亏
			totalProfitOrLoss = totalProfitOrLoss.add(orderService.getProfitOrLoss(order));
		}
		if (totalProfitOrLoss.compareTo(BigDecimal.ZERO) < 0
				&& account.getAvailableBalance().add(totalStrong).compareTo(totalProfitOrLoss.abs()) <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否足够过夜
	 * 
	 * <p>
	 * 计算公式：账户余额 +浮动盈亏+交易保证金+隔夜手续费>=隔夜保证金
	 * </p>
	 * 
	 * @param orderList
	 *            订单列表
	 * @param account
	 *            资金账户
	 * @return 是否足够过夜
	 */
	private boolean isEnoughOvernight(List<FuturesOrder> orderList, CapitalAccountDto account) {
		BigDecimal totalProfitOrLoss = BigDecimal.ZERO;
		BigDecimal totalTradeReserveFund = BigDecimal.ZERO;
		BigDecimal totalOvernightDeferredFee = BigDecimal.ZERO;
		BigDecimal totalOvernightReserveFund = BigDecimal.ZERO;
		for (FuturesOrder order : orderList) {
			// 计算浮动盈亏
			totalProfitOrLoss = totalProfitOrLoss.add(orderService.getProfitOrLoss(order));
			// 计算交易保证金
			totalTradeReserveFund = totalTradeReserveFund.add(order.getReserveFund());
			// 计算隔夜手续费
			totalOvernightDeferredFee = totalOvernightDeferredFee
					.add(order.getTotalQuantity().multiply(order.getOvernightPerUnitDeferredFee()));
			// 计算隔夜保证金
			totalOvernightReserveFund = totalOvernightReserveFund
					.add(order.getTotalQuantity().multiply(order.getOvernightPerUnitReserveFund()));
		}

		if (account.getAvailableBalance().add(totalProfitOrLoss).add(totalTradeReserveFund)
				.add(totalOvernightDeferredFee).compareTo(totalOvernightReserveFund) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取触发隔夜的订单
	 * 
	 * @param order
	 *            订单
	 * @return 是否触发隔夜
	 */
	private List<FuturesOrder> triggerOvernightOrderList(List<FuturesOrder> orderList) {
		List<FuturesOrder> result = new ArrayList<>();
		SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (FuturesOrder order : orderList) {
			String overnightTimeGroup = overnightTimeMap().get(order.getContract().getCommodityId());
			if (overnightTimeGroup != null) {
				// 获取时差、隔夜时间、交易所时间
				String[] group = overnightTimeGroup.split("-");
				Integer timeZoneGap = Integer.parseInt(group[0]);
				String overnightTime = group[1];
				FuturesOvernightRecord record = overnightService.findNewestOvernightRecord(order);
				Date now = new Date();
				Date nowExchangeTime = orderService.retriveExchangeTime(now, timeZoneGap);
				String nowStr = daySdf.format(nowExchangeTime);
				// 判断是否有今天的隔夜记录
				if (!(record != null && nowStr.equals(daySdf.format(record.getDeferredTime())))) {
					FuturesContract contract = order.getContract();
					try {
						// 判断是否达到隔夜时间，隔夜时间~隔夜时间+1分钟
						Date beginTime = fullSdf.parse(nowStr + " " + overnightTime);
						Date endTime = new Date(beginTime.getTime() + 1 * 60 * 1000);
						if (nowExchangeTime.getTime() >= beginTime.getTime()
								&& nowExchangeTime.getTime() < endTime.getTime()) {
							result.add(order);
						}
					} catch (ParseException e) {
						logger.error("期货品种" + contract.getCommodity().getSymbol() + "隔夜时间格式错误?" + overnightTime);
					}
				}
			}
		}
		return result;
	}

	/**
	 * 获取隔夜时间Map
	 * 
	 * <p>
	 * key为品种ID;value格式为“时差-隔夜时间”，如“12-16:55:00”。
	 * </p>
	 * 
	 * @return 隔夜时间Map
	 */
	private Map<Long, String> overnightTimeMap() {
		Map<Long, String> result = new HashMap<Long, String>();
		List<FuturesCommodity> commodityList = commodityDao.list();
		for (FuturesCommodity commodity : commodityList) {
			Integer timeZoneGap = commodity.getExchange().getTimeZoneGap();
			String overnightTime = commodity.getOvernightTime();
			if (!StringUtil.isEmpty(overnightTime)) {
				result.put(commodity.getId(), getOvernightTimeMapValue(timeZoneGap, overnightTime.trim()));
			}
		}
		return result;
	}

	private String getOvernightTimeMapValue(Integer timeZoneGap, String overnightTime) {
		return timeZoneGap + "-" + overnightTime;
	}

	/**
	 * 获取用户所有持仓中的订单
	 * 
	 * @return 持仓中的订单
	 */
	private List<FuturesOrder> retrivePublisherPositionOrders(Long publisherId) {
		FuturesOrderState[] states = { FuturesOrderState.Position, FuturesOrderState.SellingEntrust,
				FuturesOrderState.PartUnwind };
		FuturesOrderQuery query = new FuturesOrderQuery();
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		query.setStates(states);
		query.setPublisherId(publisherId);
		Page<FuturesOrder> pages = orderService.pagesOrder(query);
		return pages.getContent();
	}

	/**
	 * 获取全部用户所有持仓中的订单
	 * 
	 * @return 持仓中的订单
	 */
	private List<FuturesOrder> retrivePositionOrders() {
		FuturesOrderState[] states = { FuturesOrderState.Position, FuturesOrderState.SellingEntrust,
				FuturesOrderState.PartUnwind };
		FuturesOrderQuery query = new FuturesOrderQuery();
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		query.setStates(states);
		Page<FuturesOrder> pages = orderService.pagesOrder(query);
		return pages.getContent();
	}

	private void retry(MonitorPublisherFuturesOrderMessage messgeObj) {
		try {
			int consumeCount = messgeObj.getConsumeCount();
			messgeObj.setConsumeCount(consumeCount + 1);
			Thread.sleep(50);
			if (messgeObj.getMaxConsumeCount() > 0 && consumeCount < messgeObj.getMaxConsumeCount()) {
				producer.sendMessage(RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName, messgeObj);
			} else if (messgeObj.getMaxConsumeCount() <= 0) {
				producer.sendMessage(RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName, messgeObj);
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName + " message retry exception!", ex);
		}
	}

	public void monitorPublisher(final Long publisherId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!monitorPublisherList.contains(publisherId)) {
					MonitorPublisherFuturesOrderMessage messgeObj = new MonitorPublisherFuturesOrderMessage();
					messgeObj.setPublisherId(publisherId);
					producer.sendMessage(RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName, messgeObj);
					monitorPublisherList.add(publisherId);
				}
			}
		}).start();
	}

}
