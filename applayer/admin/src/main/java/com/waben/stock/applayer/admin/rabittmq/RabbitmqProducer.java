package com.waben.stock.applayer.admin.rabittmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.interfaces.util.JacksonUtil;

@Component
public class RabbitmqProducer {
	
	@Autowired
	private RabbitTemplate template;
	
	public void sendAll(String mesage) {
		template.convertAndSend(RabbitmqConfiguration.pcPushExchangeName, "", mesage);
	}
	
	public void sendMessage(String queueName, Object message) {
		template.convertAndSend(queueName, JacksonUtil.encode(message));
	}

}
