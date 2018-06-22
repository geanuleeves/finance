package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesOrderDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.EsOrderActionMessage;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.orderActionQueueName })
public class EsOrderActionConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderDao futuresOrderDao;

	@RabbitHandler
	public void handlerMessage(String message) {
		logger.info("消费易盛OrderAction通知消息:" + message);
		EsOrderActionMessage msgObj = JacksonUtil.decode(message, EsOrderActionMessage.class);
		try {
			if (msgObj.getErrorCode() == 60011
					|| (msgObj.getInfo() != null && msgObj.getInfo().getOrderInfo().getOrderState() == 'B')) {
				// 下单无效的合约
				int sessionId = msgObj.getSessionID();
				FuturesOrder order = futuresOrderDao.retrieveByOrderSessionId(sessionId);
				if (order.getOrderState() == 0) {
					order.setOrderState(11);
					futuresOrderDao.updateFuturesOrder(order);
				}
			}
		} catch (Exception ex) {
			logger.error("消费易盛OrderAction通知消息异常!", ex);
		}
	}

}
