package com.waben.stock.futuresgateway.yisheng.rabbitmq.consumer;

import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.trade.bean.TapAPIOrderInfoNotice;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesOrderDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.orderStateQueueName })
public class EsOrderStateConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderDao orderDao;

	@RabbitHandler
	public void handlerMessage(String message) {
		logger.info("消费易盛OrderState通知消息:" + message);
		TapAPIOrderInfoNotice msgObj = JacksonUtil.decode(message, TapAPIOrderInfoNotice.class);
		try {
			String orderNo = msgObj.getOrderInfo().getOrderNo();
			FuturesOrder order = orderDao.retrieveByOrderNo(orderNo);
			if (order == null) {
				int orderSessionId = msgObj.getSessionID();
				order = orderDao.retrieveByOrderSessionId(orderSessionId);
				if (order != null && order.getOrderNo() != null
						&& !order.getOrderNo().trim().equals(msgObj.getOrderInfo().getOrderNo())) {
					order = null;
				}
			}
			if (order != null && order.getOrderState() != 6) {
				order.setFilled(new BigDecimal(msgObj.getOrderInfo().getOrderMatchQty()));
				order.setLastFillPrice(new BigDecimal(msgObj.getOrderInfo().getOrderMatchPrice()));
				order.setOrderState(convertState(msgObj.getOrderInfo().getOrderState()));
				order.setOrderNo(msgObj.getOrderInfo().getOrderNo());
				order.setUpdateTime(new Date());
				orderDao.updateFuturesOrder(order);
			}
		} catch (Exception ex) {
			logger.error("消费易盛OrderState通知消息异常!", ex);
		}
	}

	private int convertState(char orderState) {
		if (orderState == '0') {
			// 终端提交
			return 0;
		} else if (orderState == '1') {
			// 已受理
			return 1;
		} else if (orderState == '2') {
			// 策略待触发
			return 2;
		} else if (orderState == '3') {
			// 交易所待触发
			return 3;
		} else if (orderState == '4') {
			// 已排队
			return 4;
		} else if (orderState == '5') {
			// 部分成交
			return 5;
		} else if (orderState == '6') {
			// 完全成交
			return 6;
		} else if (orderState == '7') {
			// 待撤消(排队临时状态)
			return 7;
		} else if (orderState == '8') {
			// 待修改(排队临时状态)
			return 8;
		} else if (orderState == '9') {
			// 完全撤单
			return 9;
		} else if (orderState == 'A') {
			// 已撤余单
			return 10;
		} else if (orderState == 'B') {
			// 指令失败
			return 11;
		} else if (orderState == 'C') {
			// 策略删除
			return 12;
		} else if (orderState == 'D') {
			// 已挂起
			return 13;
		} else if (orderState == 'E') {
			// 到期删除
			return 14;
		} else if (orderState == 'F') {
			// 已生效——询价成功
			return 15;
		} else if (orderState == 'G') {
			// 已申请——行权、弃权、套利等申请成功
			return 16;
		} else {
			return Integer.parseInt(String.valueOf(orderState));
		}
	}

}
