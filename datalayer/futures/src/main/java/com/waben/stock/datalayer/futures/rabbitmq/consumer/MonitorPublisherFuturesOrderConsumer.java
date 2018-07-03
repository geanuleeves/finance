package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorPublisherFuturesOrderMessage;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

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
			logger.info("消费期货委托查询消息:{}", message);
		}
		MonitorPublisherFuturesOrderMessage messgeObj = JacksonUtil.decode(message,
				MonitorPublisherFuturesOrderMessage.class);
		try {
			Long publisherId = messgeObj.getPublisherId();
			List<FuturesOrder> orderList = retrivePublisherPositionOrders(publisherId);
			if (orderList != null && orderList.size() > 0) {
				BigDecimal totalStrong = BigDecimal.ZERO;
				BigDecimal totalProfitOrLoss = BigDecimal.ZERO;
				for (FuturesOrder order : orderList) {
					// 计算强平金额
					totalStrong.add(orderService.getStrongMoney(order));
					// 计算浮动盈亏
					totalProfitOrLoss.add(orderService.getProfitOrLoss(order));
				}

				// 获取资金账号
				CapitalAccountDto account = accountBusiness.fetchByPublisherId(publisherId);
				if (totalProfitOrLoss.compareTo(BigDecimal.ZERO) < 0
						&& account.getAvailableBalance().add(totalStrong).compareTo(totalProfitOrLoss.abs()) <= 0) {
					// 强平
					for (FuturesOrder order : orderList) {
						FuturesContract contract = order.getContract();
						if(orderService.isTradeTime(contract.getCommodity().getExchange().getTimeZoneGap(), contract)) {
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
				}
			} else {
				// 从监控队列中移除
				monitorPublisherList.remove(publisherId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
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

	public void monitorPublisher(Long publisherId) {
		if (!monitorPublisherList.contains(publisherId)) {
			MonitorPublisherFuturesOrderMessage messgeObj = new MonitorPublisherFuturesOrderMessage();
			messgeObj.setPublisherId(publisherId);
			producer.sendMessage(RabbitmqConfiguration.monitorPublisherFuturesOrderQueueName, messgeObj);
			monitorPublisherList.add(publisherId);
		}
	}

}
