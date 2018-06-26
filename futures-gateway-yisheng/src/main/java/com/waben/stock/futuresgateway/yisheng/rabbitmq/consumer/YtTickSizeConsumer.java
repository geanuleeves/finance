package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

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
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.YtTickSizeMessage;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.twsapi.TwsConstant;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.tickSizeQueueName })
public class YtTickSizeConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private RedisCache redisCache;

	@RabbitHandler
	public void handlerMessage(String message) {
//		logger.info("TickSize消息:{}", message);
		YtTickSizeMessage msgObj = JacksonUtil.decode(message, YtTickSizeMessage.class);
		try {
			int tickerId = msgObj.getTickerId();
			int field = msgObj.getField();
			int size = msgObj.getSize();
			// step 1 : 获取期货合约的ID
			Long contractId = Long.valueOf(tickerId);
			String tickerIdStr = String.valueOf(tickerId);
			if (tickerIdStr.length() > 3) {
				contractId = Long.parseLong(tickerIdStr.substring(3));
			}
			// step 2 : 获取期货合约
			FuturesContract contract = CommonDataCache.contractMap.get(contractId);
			if (contract == null) {
				contract = contractService.getFuturesContractInfo(contractId);
			}
			// step 3 : 更新期货合约的相关size
			String redisKey = String.format(TwsConstant.Market_RedisKey,
					contract.getId() + "_" + contract.getCommodityNo() + "_" + contract.getContractNo());
			String quoteStr = redisCache.get(redisKey);
			FuturesQuoteData quote = null;
			if (!StringUtil.isEmpty(quoteStr)) {
				quote = JacksonUtil.decode(quoteStr, FuturesQuoteData.class);
			} else {
				quote = new FuturesQuoteData();
			}
			boolean isNeedUpdate = true;
			if (field == 0) {
				quote.setBidSize(new Integer(size).longValue());
			} else if (field == 3) {
				quote.setAskSize(new Integer(size).longValue());
			} else if (field == 5) {
				quote.setLastSize(new Integer(size).longValue());
			} else if (field == 8) {
				quote.setVolume(new Integer(size).longValue());
				quote.setTotalVolume(new Integer(size).longValue());
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
