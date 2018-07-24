package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.EsDeleteQuoteMessage;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteMinuteKService;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;

@Component
@RabbitListener(queues = {
		RabbitmqConfiguration.deleteQuoteQueueName }, containerFactory = "deleteQuoteListenerContainerFactory")
public class EsDeleteQuoteConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesQuoteService quoteService;

	@Autowired
	private FuturesQuoteMinuteKService minuteKService;

	@RabbitHandler
	public void handlerMessage(String message) {
		EsDeleteQuoteMessage msgObj = JacksonUtil.decode(message, EsDeleteQuoteMessage.class);
		try {
			if (msgObj.getType() != null && msgObj.getType() == 1) {
				quoteService.deleteFuturesQuote(msgObj.getCommodityNo(), msgObj.getContractNo(), msgObj.getQuoteId());
			} else if (msgObj.getType() != null && msgObj.getType() == 2) {
				minuteKService.deleteFuturesQuoteMinuteK(msgObj.getCommodityNo(), msgObj.getContractNo(),
						msgObj.getQuoteId());
			}
		} catch (Exception ex) {
			logger.error("消费删除易盛Quote消息异常!", ex);
		}
	}

}
