package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.EntrustQueryMessage;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesTradeEntrustService;
import com.waben.stock.interfaces.dto.futures.MarketAveragePrice;
import com.waben.stock.interfaces.enums.FuturesActionType;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.entrustQueryQueueName })
public class EntrustQueryConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private FuturesTradeEntrustDao entrustDao;

	@Autowired
	private FuturesTradeEntrustService entrustService;

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("消费期货委托查询消息:{}", message);
		}
		EntrustQueryMessage messgeObj = JacksonUtil.decode(message, EntrustQueryMessage.class);
		try {
			Long entrustId = messgeObj.getEntrustId();
			FuturesTradeEntrust tradeEntrust = entrustDao.retrieve(entrustId);
			if (tradeEntrust == null || tradeEntrust.getState() == FuturesTradeEntrustState.Canceled
					|| tradeEntrust.getState() == FuturesTradeEntrustState.Failure
					|| tradeEntrust.getState() == FuturesTradeEntrustState.Success) {
				return;
			}
			Integer entrustType = messgeObj.getEntrustType();
			boolean isNeedRetry = true;
			FuturesContract contract = tradeEntrust.getContract();
			Integer timeZoneGap = contract.getCommodity().getTimeZoneGap();
			if (entrustType == 1) {
				if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.OPEN)) {
					// 开仓
					isNeedRetry = handleEntrust(tradeEntrust, entrustType, messgeObj.isStopLossOrProfit(),
							messgeObj.getStopLossOrProfitPrice());
				}
			} else if (entrustType == 2) {
				if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.CLOSE)) {
					// 平仓
					isNeedRetry = handleEntrust(tradeEntrust, entrustType, messgeObj.isStopLossOrProfit(),
							messgeObj.getStopLossOrProfitPrice());
				}
			} else if (entrustType == 3) {
				if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.CLOSE)) {
					// 反手
					isNeedRetry = handleEntrust(tradeEntrust, entrustType, messgeObj.isStopLossOrProfit(),
							messgeObj.getStopLossOrProfitPrice());
				}
			} else {
				logger.error("错误的委托类型!");
				isNeedRetry = false;
			}
			if (isNeedRetry) {
				retry(messgeObj);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
	}

	/**
	 * 处理交易委托
	 * 
	 * @param tradeEntrust
	 *            交易委托
	 * @return 是否成功
	 */
	private boolean handleEntrust(FuturesTradeEntrust tradeEntrust, Integer entrustType, boolean isStopLossOrProfit,
			BigDecimal stopLossOrProfitPrice) {
		// step 1 : 获取基本参数
		FuturesContract contract = tradeEntrust.getContract();
		FuturesCommodity commodity = contract.getCommodity();
		String commodityNo = commodity.getSymbol();
		String contractNo = contract.getContractNo();
		BigDecimal minWave = commodity.getMinWave();
		// step 2 : 获取部分委托信息
		FuturesOrderType orderType = tradeEntrust.getOrderType();
		BigDecimal totalQuantity = tradeEntrust.getRemaining();
		if (totalQuantity.compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		// step 3 : 计算点差、交易动作是买或者卖
		FuturesActionType actionType = null;
		Integer slipPoint = null;
		BigDecimal reverse = new BigDecimal(1);
		if (orderType == FuturesOrderType.BuyUp) {
			if (entrustType == 1) {
				actionType = FuturesActionType.BUY;
				slipPoint = commodity.getBuyUpOpenSlipPoint();
				reverse = new BigDecimal(1);
			} else {
				actionType = FuturesActionType.SELL;
				slipPoint = commodity.getBuyUpCloseSlipPoint();
				reverse = new BigDecimal(-1);
			}
		} else {
			if (entrustType == 1) {
				actionType = FuturesActionType.SELL;
				slipPoint = commodity.getBuyFallOpenSlipPoint();
				reverse = new BigDecimal(-1);
			} else {
				actionType = FuturesActionType.BUY;
				slipPoint = commodity.getBuyFallCloseSlipPoint();
				reverse = new BigDecimal(1);
			}
		}
		FuturesTradePriceType priceType = tradeEntrust.getPriceType();
		boolean isNeedRetry = true;
		// step 3 : 处理委托
		if (tradeEntrust.getState() == FuturesTradeEntrustState.Queuing) {
			if (isStopLossOrProfit) {
				logger.info("交易委托{}【{}】成功，平仓类型是止损止盈类型", tradeEntrust.getId(), entrustType);
				BigDecimal price = stopLossOrProfitPrice;
				if (slipPoint != null && slipPoint > 0) {
					price = price.add(new BigDecimal(slipPoint).multiply(minWave).multiply(reverse));
				}
				entrustService.success(tradeEntrust.getId(), tradeEntrust.getQuantity(), BigDecimal.ZERO, price, null);
			} else {
				MarketAveragePrice avgPrice = null;
				if (priceType == FuturesTradePriceType.MKT) {
					// 市价
					avgPrice = orderService.computeMktAvgPrice(commodityNo, contractNo, actionType, totalQuantity);
				} else {
					// 限价
					avgPrice = orderService.computeLmtAvgPrice(commodityNo, contractNo, actionType, totalQuantity,
							tradeEntrust.getEntrustPrice());
				}
				if (avgPrice.getFilled().compareTo(totalQuantity) == 0
						&& avgPrice.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
					logger.info("交易委托{}【{}】成功，买入成功时候的行情为{}", tradeEntrust.getId(), entrustType,
							avgPrice.getMarket() != null ? JacksonUtil.encode(avgPrice.getMarket()) : "");
					BigDecimal price = avgPrice.getAvgFillPrice();
					if (slipPoint != null && slipPoint > 0) {
						price = price.add(new BigDecimal(slipPoint).multiply(minWave).multiply(reverse));
					}
					entrustService.success(tradeEntrust.getId(), avgPrice.getFilled(), avgPrice.getRemaining(), price,
							null);
					if (entrustType == 3) {
						// 反手开仓
						orderService.backhandPlaceOrder(tradeEntrust.getId());
					}
					isNeedRetry = false;
				}
			}
		} else {
			isNeedRetry = false;
		}
		return isNeedRetry;
	}

	private void retry(EntrustQueryMessage messgeObj) {
		try {
			int consumeCount = messgeObj.getConsumeCount();
			messgeObj.setConsumeCount(consumeCount + 1);
			Thread.sleep(50);
			if (messgeObj.getMaxConsumeCount() > 0 && consumeCount < messgeObj.getMaxConsumeCount()) {
				producer.sendMessage(RabbitmqConfiguration.entrustQueryQueueName, messgeObj);
			} else if (messgeObj.getMaxConsumeCount() <= 0) {
				producer.sendMessage(RabbitmqConfiguration.entrustQueryQueueName, messgeObj);
			}
		} catch (Exception ex) {
			throw new RuntimeException(RabbitmqConfiguration.entrustQueryQueueName + " message retry exception!", ex);
		}
	}

	public void entrustQuery(final Long entrustId, final Integer entrustType, boolean isStopLossOrProfit,
			BigDecimal stopLossOrProfitPrice) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				EntrustQueryMessage msg = new EntrustQueryMessage();
				msg.setEntrustId(entrustId);
				msg.setEntrustType(entrustType);
				msg.setStopLossOrProfit(isStopLossOrProfit);
				msg.setStopLossOrProfitPrice(stopLossOrProfitPrice);
				producer.sendMessage(RabbitmqConfiguration.entrustQueryQueueName, msg);
			}
		}).start();
	}

}
