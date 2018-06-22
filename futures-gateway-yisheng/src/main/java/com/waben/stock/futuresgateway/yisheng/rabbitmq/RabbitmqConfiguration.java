package com.waben.stock.futuresgateway.yisheng.rabbitmq;

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

	public static final String commodityQueueName = "futures-gateway-yisheng-commodity";

	public static final String contractQueueName = "futures-gateway-yisheng-contract";

	public static final String tradeContractQueueName = "futures-gateway-yisheng-tradecontract";

	public static final String quoteQueueName = "futures-gateway-yisheng-quote";

	public static final String deleteQuoteQueueName = "futures-gateway-yisheng-deletequote";

	public static final String orderStateQueueName = "futures-gateway-yisheng-orderstate";

	public static final String orderActionQueueName = "futures-gateway-yisheng-orderaction";

	public static final String tickPriceQueueName = "futures-gateway-yingtou-tickPrice";

	public static final String tickSizeQueueName = "futures-gateway-yingtou-tickSize";

	public static final String historicalDataQueueName = "futures-gateway-yingtou-historicalData";

	@Autowired
	private ConnectionFactory connectionFactory;

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public RabbitTemplate rabbitTemplate() {
		logger.info("host,username:{}{}", connectionFactory.getHost(), connectionFactory.getUsername());
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		return rabbitTemplate;
	}

	@Bean(name = { "quoteListenerContainerFactory" })
	public SimpleRabbitListenerContainerFactory quoteListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setConcurrentConsumers(10);
		factory.setMaxConcurrentConsumers(50);
		return factory;
	}

	@Bean(name = { "deleteQuoteListenerContainerFactory" })
	public SimpleRabbitListenerContainerFactory deleteQuoteListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setConcurrentConsumers(10);
		factory.setMaxConcurrentConsumers(25);
		return factory;
	}

	@Bean(name = { "historicalDataListenerContainerFactory" })
	public SimpleRabbitListenerContainerFactory historicalDataListenerContainerFactory() {
		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setConcurrentConsumers(2);
		factory.setMaxConcurrentConsumers(5);
		return factory;
	}

	/**
	 * 创建 品种 队列
	 */
	@Bean
	public Queue commodityQueue() {
		return new Queue(commodityQueueName);
	}

	/**
	 * 创建 合约 队列
	 */
	@Bean
	public Queue contractQueue() {
		return new Queue(contractQueueName);
	}

	/**
	 * 创建 交易合约 队列
	 */
	@Bean
	public Queue tradeContractQueue() {
		return new Queue(tradeContractQueueName);
	}

	/**
	 * 创建 行情 队列
	 */
	@Bean
	public Queue quoteQueue() {
		return new Queue(quoteQueueName);
	}

	/**
	 * 创建 删除行情 队列
	 */
	@Bean
	public Queue deleteQuoteQueue() {
		return new Queue(deleteQuoteQueueName);
	}

	/**
	 * 创建 订单状态回调 队列
	 */
	@Bean
	public Queue orderStateQuoteQueue() {
		return new Queue(orderStateQueueName);
	}

	/**
	 * 创建 订单动作回调 队列
	 */
	@Bean
	public Queue orderActionQuoteQueue() {
		return new Queue(orderActionQueueName);
	}

	/************************* 盈透 **************************/

	/**
	 * 创建 处理行情价格 队列
	 */
	@Bean
	public Queue tickSizeQueue() {
		return new Queue(tickSizeQueueName);
	}

	/**
	 * 创建 处理行情价格 队列
	 */
	@Bean
	public Queue tickPriceQueue() {
		return new Queue(tickPriceQueueName);
	}

	/**
	 * 创建 处理历史数据 队列
	 */
	@Bean
	public Queue historicalDataQueue() {
		return new Queue(historicalDataQueueName);
	}

}
