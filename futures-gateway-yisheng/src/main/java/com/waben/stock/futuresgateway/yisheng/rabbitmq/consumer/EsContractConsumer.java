package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.quote.bean.TapAPIQuoteContractInfo;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.esapi.EsEngine;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.contractQueueName })
public class EsContractConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private EsEngine engine;

	@RabbitHandler
	public void handlerMessage(String message) {
		TapAPIQuoteContractInfo msgObj = JacksonUtil.decode(message, TapAPIQuoteContractInfo.class);
		try {
			String commodityNo = msgObj.getContract().getCommodity().getCommodityNo();
			String contractNo = msgObj.getContract().getContractNo1();
			char commodityType = msgObj.getContract().getCommodity().getCommodityType();
			if (commodityType == 'F') {
				// 保存合约信息
				FuturesContract contract = contractService.getByCommodityNoAndContractNo(commodityNo, contractNo);
				if (contract != null) {
					contract.setCommodityNo(commodityNo);
					contract.setContractName(msgObj.getContractName());
					contract.setContractNo(contractNo);
					contractService.modifyFuturesContract(contract);
				} else {
					contract = new FuturesContract();
					contract.setCommodityNo(commodityNo);
					contract.setContractName(msgObj.getContractName());
					contract.setContractNo(contractNo);
					contractService.addFuturesContract(contract);
				}
				// 订阅行情
				if (contract.getEnable() != null && contract.getEnable()) {
					engine.subscribeQuote(msgObj.getContract());
				}
			}
		} catch (Exception ex) {
			logger.error("消费易盛Contract消息异常!", ex);
		}
	}

}
