package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.futuresgateway.yisheng.cache.CommonDataCache;
import com.waben.stock.futuresgateway.yisheng.cache.RedisCache;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.pojo.FuturesQuoteData;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.YtTickPriceMessage;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.twsapi.TwsConstant;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.tickPriceQueueName })
public class YtTickPriceConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractService futuresContractService;

	@Autowired
	private RedisCache redisCache;

	@RabbitHandler
	public void handlerMessage(String message) {
		logger.info("TickPrice消息:{}", message);
		YtTickPriceMessage msgObj = JacksonUtil.decode(message, YtTickPriceMessage.class);
		try {
			int tickerId = msgObj.getTickerId();
			int field = msgObj.getField();
			double price = msgObj.getPrice();
			// step 1 : 获取期货合约的ID
			Long contractId = Long.valueOf(tickerId);
			String tickerIdStr = String.valueOf(tickerId);
			if (tickerIdStr.length() > 3) {
				contractId = Long.parseLong(tickerIdStr.substring(3));
			}
			// step 2 : 获取期货合约
			FuturesContract contract = CommonDataCache.contractMap.get(contractId);
			if (contract == null) {
				contract = futuresContractService.getFuturesContractInfo(contractId);
			}
			// step 3 : 更新期货合约的相关价格
			String redisKey = String.format(TwsConstant.Market_RedisKey,
					contract.getId() + "_" + contract.getCommodityNo() + "_" + contract.getContractNo());
			String quoteStr = redisCache.get(redisKey);
			FuturesQuoteData quote = null;
			if (StringUtil.isEmpty(quoteStr)) {
				quote = JacksonUtil.decode(quoteStr, FuturesQuoteData.class);
			} else {
				quote = new FuturesQuoteData();
			}
			boolean isNeedUpdate = true;
			if (field == 1) {
				quote.setBidPrice(new BigDecimal(price));
			} else if (field == 2) {
				quote.setAskPrice(new BigDecimal(price));
			} else if (field == 4) {
				quote.setLastPrice(new BigDecimal(price));
			} else if (field == 6) {
				quote.setHighPrice(new BigDecimal(price));
			} else if (field == 7) {
				quote.setLowPrice(new BigDecimal(price));
			} else if (field == 9) {
				quote.setClosePrice(new BigDecimal(price));
			} else if (field == 14) {
				quote.setOpenPrice(new BigDecimal(price));
			} else {
				isNeedUpdate = false;
			}
			// step 4 : 更新quote
			if (isNeedUpdate) {
				redisCache.set(redisKey, JacksonUtil.encode(quote));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
