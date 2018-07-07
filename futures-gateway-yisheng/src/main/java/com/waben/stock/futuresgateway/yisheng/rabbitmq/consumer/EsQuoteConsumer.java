package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.quote.bean.TapAPIQuoteWhole;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.EsQuoteInfo;
import com.waben.stock.futuresgateway.yisheng.service.FuturesQuoteService;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.quoteQueueName }, containerFactory = "quoteListenerContainerFactory")
public class EsQuoteConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesQuoteService quoteService;

	@RabbitHandler
	public void handlerMessage(String message) {
		EsQuoteInfo msgBaseObj = JacksonUtil.decode(message, EsQuoteInfo.class);
		try {
			TapAPIQuoteWhole msgObj = msgBaseObj.getInfo();
			String commodityNo = msgObj.getContract().getCommodity().getCommodityNo();
			String contractNo = msgObj.getContract().getContractNo1();
			char commodityType = msgObj.getContract().getCommodity().getCommodityType();
			if (commodityType == 'F') {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				// 保存行情信息
				FuturesQuote quote = new FuturesQuote();
				quote.setQuoteIndex(msgBaseObj.getQuoteIndex());
				quote.setAskPrice(JacksonUtil.encode(msgObj.getQAskPrice()));
				quote.setAskQty(JacksonUtil.encode(msgObj.getQAskQty()));
				quote.setAveragePrice(String.valueOf(msgObj.getQAveragePrice()));
				quote.setBidPrice(JacksonUtil.encode(msgObj.getQBidPrice()));
				quote.setBidQty(JacksonUtil.encode(msgObj.getQBidQty()));
				quote.setChangeRate(String.valueOf(msgObj.getQChangeRate()));
				quote.setChangeSpeed(String.valueOf(msgObj.getQChangeSpeed()));
				quote.setChangeValue(String.valueOf(msgObj.getQChangeValue()));
				quote.setClosingPrice(String.valueOf(msgObj.getQClosingPrice()));
				quote.setCommodityNo(commodityNo);
				quote.setContractNo(contractNo);
				quote.setCurrDelta(String.valueOf(msgObj.getQCurrDelta()));
				quote.setD5AvgQty(msgObj.getQ5DAvgQty());
				quote.setDateTimeStamp(msgObj.getDateTimeStamp());
				quote.setHighPrice(String.valueOf(msgObj.getQHighPrice()));
				quote.setHisHighPrice(String.valueOf(msgObj.getQHisHighPrice()));
				quote.setHisLowPrice(String.valueOf(msgObj.getQHisLowPrice()));
				quote.setImpliedAskPrice(String.valueOf(msgObj.getQImpliedAskPrice()));
				quote.setImpliedAskQty(msgObj.getQImpliedAskQty());
				quote.setImpliedBidPrice(String.valueOf(msgObj.getQImpliedBidPrice()));
				quote.setImpliedBidQty(msgObj.getQImpliedBidQty());
				quote.setInsideQty(msgObj.getQInsideQty());
				quote.setLastPrice(String.valueOf(msgObj.getQLastPrice()));
				quote.setLastQty(msgObj.getQLastQty());
				quote.setLimitDownPrice(String.valueOf(msgObj.getQLimitDownPrice()));
				quote.setLimitUpPrice(String.valueOf(msgObj.getQLimitUpPrice()));
				quote.setLowPrice(String.valueOf(msgObj.getQLowPrice()));
				quote.setNegotiableValue(String.valueOf(msgObj.getQNegotiableValue()));
				quote.setOpeningPrice(String.valueOf(msgObj.getQOpeningPrice()));
				quote.setOutsideQty(msgObj.getQOutsideQty());
				quote.setPeRatio(String.valueOf(msgObj.getQPERatio()));
				quote.setPositionQty(msgObj.getQPositionQty());
				quote.setPositionTrend(msgObj.getQPositionTrend());
				quote.setPreClosingPrice(String.valueOf(msgObj.getQPreClosingPrice()));
				quote.setPreDelta(String.valueOf(msgObj.getQPreDelta()));
				quote.setPrePositionQty(msgObj.getQPrePositionQty());
				quote.setPreSettlePrice(String.valueOf(msgObj.getQPreSettlePrice()));
				quote.setSettlePrice(String.valueOf(msgObj.getQSettlePrice()));
				quote.setSwing(String.valueOf(msgObj.getQSwing()));
				Date nowTime = sdf.parse(msgObj.getDateTimeStamp());
				quote.setTime(nowTime);
				quote.setTotalAskQty(msgObj.getQTotalAskQty());
				quote.setTotalBidQty(msgObj.getQTotalBidQty());
				quote.setTotalQty(msgObj.getQTotalQty());
				quote.setTotalTurnover(String.valueOf(msgObj.getQTotalTurnover()));
				quote.setTotalValue(String.valueOf(msgObj.getQTotalValue()));
				quote.setTurnoverRate(String.valueOf(msgObj.getQTurnoverRate()));
				quoteService.addFuturesQuote(quote);
			}
		} catch (Exception ex) {
			logger.error("消费易盛Quote消息异常!", ex);
		}
	}

}
