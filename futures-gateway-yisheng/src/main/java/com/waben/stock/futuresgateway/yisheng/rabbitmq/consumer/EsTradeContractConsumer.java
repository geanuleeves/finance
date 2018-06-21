package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.trade.bean.TapAPITradeContractInfo;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.tradeContractQueueName })
public class EsTradeContractConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractService contractService;

	@RabbitHandler
	public void handlerMessage(String message) {
		TapAPITradeContractInfo msgObj = JacksonUtil.decode(message, TapAPITradeContractInfo.class);
		try {
			String commodityNo = msgObj.getCommodityNo();
			String contractNo = msgObj.getContractNo1();
			char commodityType = msgObj.getCommodityType();
			if (commodityType == 'F') {
				// 保存合约信息
				FuturesContract contract = contractService.getByCommodityNoAndContractNo(commodityNo, contractNo);
				if (contract != null) {
					contract.setCommodityNo(commodityNo);
					contract.setContractExpDate(msgObj.getContractExpDate());
					contract.setContractName(msgObj.getContractName());
					contract.setContractNo(contractNo);
					contract.setFirstNoticeDate(msgObj.getFirstNoticeDate());
					contract.setLastTradeDate(msgObj.getLastTradeDate());
					contractService.modifyFuturesContract(contract);
				} else {
					contract = new FuturesContract();
					contract.setCommodityNo(commodityNo);
					contract.setContractExpDate(msgObj.getContractExpDate());
					contract.setContractName(msgObj.getContractName());
					contract.setContractNo(contractNo);
					contract.setFirstNoticeDate(msgObj.getFirstNoticeDate());
					contract.setLastTradeDate(msgObj.getLastTradeDate());
					contractService.addFuturesContract(contract);
				}
			}
		} catch (Exception ex) {
			logger.error("消费易盛Contract消息异常!", ex);
		}
	}

}
