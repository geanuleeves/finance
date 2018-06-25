package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.EntrustQueryMessage;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.interfaces.commonapi.retrivefutures.TradeFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesGatewayOrder;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.entrustQueryQueueName })
public class EntrustQueryConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesOrderService orderService;
	
	@Autowired
	private ProfileBusiness profileBusiness;

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("消费期货委托查询消息:{}", message);
		}
		EntrustQueryMessage messgeObj = JacksonUtil.decode(message, EntrustQueryMessage.class);
		try {
			Long orderId = messgeObj.getOrderId();
			Integer entrustType = messgeObj.getEntrustType();
			Long gatewayOrderId = messgeObj.getGatewayOrderId();
			FuturesGatewayOrder gatewayOrder = TradeFuturesOverHttp.retriveByGatewayId(profileBusiness.isProd(), gatewayOrderId);
			boolean isNeedRetry = true;
			if (TradeFuturesOverHttp.apiType == 1) {
				isNeedRetry = checkYingtouOrder(gatewayOrder, entrustType, orderId);
			} else if (TradeFuturesOverHttp.apiType == 2) {
				isNeedRetry = checkYishengOrder(gatewayOrder, entrustType, orderId);
			} else {
				isNeedRetry = false;
			}
			if (isNeedRetry) {
				retry(messgeObj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
	}

	private boolean checkYingtouOrder(FuturesGatewayOrder gatewayOrder, Integer entrustType, Long orderId) {
		boolean isNeedRetry = true;
		String status = gatewayOrder.getOrderStatus();
		if (entrustType == 1) {
			if ("Cancelled".equals(status)) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if ("Submitted".equals(status) && gatewayOrder.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				// 部分买入成功
				orderService.partPositionOrder(orderId);
			} else if ("Filled".equals(status) && gatewayOrder.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
				// 持仓中
				orderService.positionOrder(orderId, gatewayOrder.getLastFillPrice());
				isNeedRetry = false;
			}
		} else if (entrustType == 2) {
			if ("Cancelled".equals(status)) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if ("Submitted".equals(status) && gatewayOrder.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				// 部分已平仓
				orderService.partUnwindOrder(orderId);
			} else if ("Filled".equals(status) && gatewayOrder.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice());
				isNeedRetry = false;
			}
		} else if (entrustType == 3) {
			if ("Cancelled".equals(status)) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if ("Submitted".equals(status) && gatewayOrder.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				// 部分已平仓
				orderService.partUnwindOrder(orderId);
			} else if ("Filled".equals(status) && gatewayOrder.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice());
				// 反手以市价买入
				orderService.backhandPlaceOrder(orderId);
				isNeedRetry = false;
			}
		} else {
			logger.error("错误的委托类型!");
			isNeedRetry = false;
		}
		return isNeedRetry;
	}

	private boolean checkYishengOrder(FuturesGatewayOrder gatewayOrder, Integer entrustType, Long orderId) {
		boolean isNeedRetry = true;
		Integer state = gatewayOrder.getOrderState();
		if (entrustType == 1 || entrustType == 2 || entrustType == 3) {
			if (state != null && state == 5 && entrustType == 1) {
				// 部分买入成功
				orderService.partPositionOrder(orderId);
			} else if (state != null && state == 5 && (entrustType == 2 || entrustType == 3)) {
				// 部分已平仓
				orderService.partUnwindOrder(orderId);
			} else if (state != null && state == 6 && entrustType == 1) {
				// 持仓中
				orderService.positionOrder(orderId, gatewayOrder.getLastFillPrice());
				isNeedRetry = false;
			} else if (state != null && state == 6 && entrustType == 2) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice());
				isNeedRetry = false;
			} else if (state != null && state == 6 && entrustType == 3) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice());
				// 反手以市价买入
				orderService.backhandPlaceOrder(orderId);
				isNeedRetry = false;
			} else if (state != null && state == 9) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if (state != null && state == 11) {
				// 已失败
				orderService.failureOrder(orderId);
				isNeedRetry = false;
			}
		} else {
			logger.error("错误的委托类型!");
			isNeedRetry = false;
		}
		return isNeedRetry;
	}

	private void retry(EntrustQueryMessage messgeObj) {
		try {
			int consumeCount = messgeObj.getConsumeCount();
			messgeObj.setConsumeCount(consumeCount + 1);
			Thread.sleep(50);
			if (messgeObj.getMaxConsumeCount() > 0 && consumeCount < messgeObj.getMaxConsumeCount()) {
				producer.sendMessage(RabbitmqConfiguration.entrustQueryQueueName, messgeObj);
			} else if (messgeObj.getMaxConsumeCount() <= 0) {
				producer.sendMessage(RabbitmqConfiguration.entrustQueryQueueName, messgeObj);
			}
		} catch (Exception ex) {
			throw new RuntimeException(RabbitmqConfiguration.entrustQueryQueueName + " message retry exception!", ex);
		}
	}

}
