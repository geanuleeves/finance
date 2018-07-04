package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.quote.bean.TapAPIQuoteCommodityInfo;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.esapi.EsEngine;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.service.FuturesCommodityService;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.commodityQueueName })
public class EsCommodityConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesCommodityService commodityService;

	@Autowired
	private EsEngine engine;

	@RabbitHandler
	public void handlerMessage(String message) {
		TapAPIQuoteCommodityInfo msgObj = JacksonUtil.decode(message, TapAPIQuoteCommodityInfo.class);
		try {
			String commodityNo = msgObj.getCommodity().getCommodityNo();
			char commodityType = msgObj.getCommodity().getCommodityType();
			if (commodityType == 'F') {
				// 保存品种信息
				FuturesCommodity commodity = commodityService.getByCommodityNo(commodityNo);
				if (commodity != null) {
					commodity.setCommodityContractLen(msgObj.getCommodityContractLen());
					commodity.setCommodityEngName(msgObj.getCommodityEngName());
					commodity.setCommodityName(msgObj.getCommodityName());
					commodity.setCommodityNo(msgObj.getCommodity().getCommodityNo());
					if ("CD".equals(commodityNo) || "EC".equals(commodityNo)) {
						commodity.setCommodityTickSize(new BigDecimal("0.00005"));
					} else {
						commodity.setCommodityTickSize(new BigDecimal(msgObj.getCommodityTickSize()));
					}
					commodity.setCommodityType(String.valueOf(msgObj.getCommodity().getCommodityType()));
					commodity.setContractSize(new BigDecimal(msgObj.getContractSize()));
					commodity.setExchangeNo(msgObj.getCommodity().getExchangeNo());
					commodityService.modifyFuturesCommodity(commodity);
				} else {
					commodity = new FuturesCommodity();
					commodity.setCommodityContractLen(msgObj.getCommodityContractLen());
					commodity.setCommodityEngName(msgObj.getCommodityEngName());
					commodity.setCommodityName(msgObj.getCommodityName());
					commodity.setCommodityNo(msgObj.getCommodity().getCommodityNo());
					if ("CD".equals(commodityNo) || "EC".equals(commodityNo)) {
						commodity.setCommodityTickSize(new BigDecimal("0.00005"));
					} else {
						commodity.setCommodityTickSize(new BigDecimal(msgObj.getCommodityTickSize()));
					}
					commodity.setCommodityType(String.valueOf(msgObj.getCommodity().getCommodityType()));
					commodity.setContractSize(new BigDecimal(msgObj.getContractSize()));
					commodity.setExchangeNo(msgObj.getCommodity().getExchangeNo());
					commodityService.addFuturesCommodity(commodity);
				}
				// 查询品种合约信息
				if (commodity.getEnable() != null && commodity.getEnable()) {
					engine.qryContract(msgObj.getCommodity());
					Thread.sleep(1000);
				}
			}
		} catch (Exception ex) {
			logger.error("消费易盛Commodity消息异常!", ex);
		}
	}

}
