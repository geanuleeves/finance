package com.waben.stock.datalayer.futures.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class RabbitmqConfiguration {

	Logger logger = LoggerFactory.getLogger(getClass());

	public static final String entrustQueryQueueName = "futures-entrustQuery";

	public static final String monitorPublisherFuturesOrderQueueName = "futures-monitorPublisherFuturesOrder";

	public static final String monitorStopLossOrProfitQueueName = "futures-monitorStopLossOrProfit";

	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RabbitTemplate rabbitTemplate() {
		logger.info("host,username:{}{}", connectionFactory.getHost(), connectionFactory.getUsername());
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		return rabbitTemplate;
	}

	@Bean(name = { "monitorStopLossOrProfitContainerFactory" })
	public SimpleRabbitListenerContainerFactory monitorStopLossOrProfitContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setConcurrentConsumers(5);
		factory.setMaxConcurrentConsumers(10);
		return factory;
	}

	/**
	 * 创建 查询委托 队列
	 */
	@Bean
	public Queue entrustQueryQueue() {
		return new Queue(entrustQueryQueueName);
	}

	/**
	 * 创建 监控用户期货订单 队列
	 */
	@Bean
	public Queue monitorPublisherFuturesOrderQueue() {
		return new Queue(monitorPublisherFuturesOrderQueueName);
	}

	/**
	 * 创建 监控止损止盈 队列
	 */
	@Bean
	public Queue monitorStopLossOrProfitQueue() {
		return new Queue(monitorStopLossOrProfitQueueName);
	}

}
