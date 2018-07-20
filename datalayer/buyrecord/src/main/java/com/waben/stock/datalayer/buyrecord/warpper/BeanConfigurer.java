package com.waben.stock.datalayer.buyrecord.warpper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import feign.Retryer;

/**
 * @author Created by yuyidi on 2017/9/19.
 * @desc
 */
@Configuration
public class BeanConfigurer {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ConnectionFactory connectionFactory;

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        jackson2HttpMessageConverter.setObjectMapper(objectMapper);
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        jackson2HttpMessageConverter.setSupportedMediaTypes(mediaTypes);
        return jackson2HttpMessageConverter;
    }
    
	@Bean
	Retryer feignRetryer() {
		return Retryer.NEVER_RETRY;
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate rabbitTemplate() {
        logger.info("host,username:{}{}", connectionFactory.getHost(), connectionFactory.getUsername());
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        return rabbitTemplate;
    }

    /**
     * 创建 自动申请买入队列
     */
    @Bean(name = "voluntarilyApplyBuyIn")
    public Queue voluntarilyBuyInQueue() {
        return new Queue("voluntarilyApplyBuyIn");
    }

    /**
     * 创建 自动申请买出队列
     */
    @Bean(name = "voluntarilyApplySellOut")
    public Queue voluntarilySellOutQueue() {
        return new Queue("voluntarilyApplySellOut");
    }

//    /**
//     * 创建 委托申请买入队列
//     */
//    @Bean(name = "entrustApplyBuyIn")
//    public Queue entrustBuyInQueue() {
//        return new Queue("entrustApplyBuyIn");
//    }

    /**
     * 创建 委托申请撤单队列
     */
    @Bean(name = "entrustApplyWithdraw")
    public Queue entrustWithdrawQueue() {
        return new Queue("entrustApplyWithdraw");
    }

    /**
     * 创建 委托申请查询撤单队列
     */
    @Bean(name = "entrustQueryWithdraw")
    public Queue queryWithdrawQueue() {
        return new Queue("entrustQueryWithdraw");
    }

    /**
     * 风控队列
     * @return
     */
    @Bean(name = "risk")
    public Queue risk() {
        return new Queue("risk");
    }

    @Bean("buyRecord")
    public TopicExchange buyRecordExchange() {
        return new TopicExchange("buyRecord");
    }

    /**
     * 点买交易风控交换机
     */
    @Bean("buyRecordRisk")
    public TopicExchange riskExchange() {
        return new TopicExchange("buyRecordRisk");
    }

    @Bean
    public Binding bindingExchangVoluntarilyBuyIn(@Qualifier("voluntarilyApplyBuyIn") Queue queue,
                                              @Qualifier("buyRecord") TopicExchange buyRecordExchange) {
        return BindingBuilder.bind(queue).to(buyRecordExchange).with("voluntarilyBuyIn");
    }

    @Bean
    public Binding bindingExchangVoluntarilySellOut(@Qualifier("voluntarilyApplySellOut") Queue queue,
                                                  @Qualifier("buyRecord") TopicExchange buyRecordExchange) {
        return BindingBuilder.bind(queue).to(buyRecordExchange).with("voluntarilySellOut");
    }

//    @Bean
//    public Binding bindingExchangEntrustBuyIn(@Qualifier("entrustApplyBuyIn") Queue queue,
//                                              @Qualifier("buyRecord") TopicExchange buyRecordExchange) {
//        return BindingBuilder.bind(queue).to(buyRecordExchange).with("applyBuyIn");
//    }

    @Bean
    public Binding bindingExchangRisk(@Qualifier("risk") Queue queue,
                                              @Qualifier("buyRecordRisk") TopicExchange riskExchange) {
        return BindingBuilder.bind(queue).to(riskExchange).with("stock");
    }

    @Bean
    public Binding bindingExchangEntrustWithdraw(@Qualifier("entrustApplyWithdraw") Queue queue,
                                      @Qualifier("buyRecord") TopicExchange riskExchange) {
        return BindingBuilder.bind(queue).to(riskExchange).with("withdraw");
    }

    @Bean
    public Binding bindingExchangQueryWithdraw(@Qualifier("entrustQueryWithdraw") Queue queue,
                                                 @Qualifier("buyRecord") TopicExchange riskExchange) {
        return BindingBuilder.bind(queue).to(riskExchange).with("queryWithdraw");
    }

    /**
     * 风控持仓卖出队列
     * @return
     */
    @Bean(name = "riskPositionSellOut")
    public Queue riskPositionSellOut() {
        return new Queue("riskPositionSellOut");
    }

    @Bean
    public Binding bindingExchangPosition(@Qualifier("riskPositionSellOut") Queue queue,
                                          @Qualifier("buyRecordRisk") TopicExchange riskExchange) {
        return BindingBuilder.bind(queue).to(riskExchange).with("position");
    }
    
    /**************************************************/
    
    @Bean(name = "againEntrust")
    public Queue againEntrust() {
        return new Queue("againEntrust");
    }
    
    @Bean(name = "entrustApplyBuyIn")
    public Queue entrustApplyBuyIn() {
        return new Queue("entrustApplyBuyIn");
    }
    
    @Bean(name = "entrustApplySellOut")
    public Queue entrustApplySellOut() {
        return new Queue("entrustApplySellOut");
    }
    
    @Bean(name = "entrustBuyIn")
    public Queue entrustBuyIn() {
        return new Queue("entrustBuyIn");
    }

    @Bean(name = "entrustSellOut")
    public Queue entrustSellOut() {
        return new Queue("entrustSellOut");
    }
    
    @Bean(name = "entrustWaste")
    public Queue entrustWaste() {
        return new Queue("entrustWaste");
    }
    
}
