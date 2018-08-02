package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorStopLossOrProfitMessage;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

// @Component
// @RabbitListener(queues = {
//		RabbitmqConfiguration.monitorStopLossOrProfitQueueName }, containerFactory = "monitorStopLossOrProfitContainerFactory")
public class MonitorStopLossOrProfitConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private QuoteContainer quoteContainer;

	private List<Long> monitorContractOrderIdList = Collections.synchronizedList(new ArrayList<Long>());

	@PostConstruct
	public void init() {
		List<FuturesContractOrder> contractOrderList = retrivePositionContractOrders();
		for (FuturesContractOrder contractOrder : contractOrderList) {
			if (!monitorContractOrderIdList.contains(contractOrder.getId())) {
				monitorContractOrderIdList.add(contractOrder.getId());
			}
		}
		for (Long contractOrderId : monitorContractOrderIdList) {
			MonitorStopLossOrProfitMessage messgeObj = new MonitorStopLossOrProfitMessage();
			messgeObj.setContractOrderId(contractOrderId);
			producer.sendMessage(RabbitmqConfiguration.monitorStopLossOrProfitQueueName, messgeObj);
		}
	}

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("监控当个订单:{}", message);
		}
		MonitorStopLossOrProfitMessage messgeObj = JacksonUtil.decode(message, MonitorStopLossOrProfitMessage.class);
		try {
			FuturesContractOrder order = contractOrderDao.retrieve(messgeObj.getContractOrderId());
			BigDecimal buyUpCanUnwind = order.getBuyUpCanUnwindQuantity();
			BigDecimal buyFallCanUnwind = order.getBuyFallCanUnwindQuantity();
			boolean isNeedRetry = true;
			if (order != null && ((buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0)
					|| (buyFallCanUnwind != null && buyFallCanUnwind.compareTo(BigDecimal.ZERO) > 0))) {
				Integer timeZoneGap = order.getContract().getCommodity().getExchange().getTimeZoneGap();
				FuturesContract contract = order.getContract();
				String commodityNo = contract.getCommodity().getSymbol();
				BigDecimal minWave = contract.getCommodity().getMinWave();
				String contractNo = contract.getContractNo();
				FuturesContractMarket market = quoteContainer.getQuote(commodityNo, contractNo);
				if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.CLOSE)) {
					// step 1 : 买涨订单
					if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal limitProfit = order.getBuyUpPerUnitLimitProfitAmount();
						BigDecimal limitLoss = order.getBuyUpPerUnitLimitLossAmount();
						// step 1.1 : 买涨是否止盈
						if (limitProfit != null) {
							BigDecimal avgFillPrice = orderService.getAvgFillPrice(order.getPublisherId(), contractNo,
									commodityNo, FuturesOrderType.BuyUp.getIndex());
							if (limitProfit.compareTo(avgFillPrice) > 0
									&& market.getLastPrice().compareTo(limitProfit) > 0) {
								logger.info("{}买涨订单达到止盈，");
								// TODO
							}
						}
						// step 1.2 : 买涨是否止损
						if (limitLoss != null) {
							BigDecimal avgFillPrice = orderService.getAvgFillPrice(order.getPublisherId(), contractNo,
									commodityNo, FuturesOrderType.BuyUp.getIndex());
							if (limitProfit.compareTo(avgFillPrice) > 0
									&& market.getLastPrice().compareTo(limitProfit) > 0) {

							}
						}

					}
					// step 2 : 买跌订单
					if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal avgFillPrice = orderService.getAvgFillPrice(order.getPublisherId(), contractNo,
								commodityNo, FuturesOrderType.BuyFall.getIndex());
						// step 2.1 : 买跌是否止盈

						// step 2.2 : 买跌是否止损

					}
				}
			} else {
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
	 * 判断是否达到止盈点
	 * 
	 * @param order
	 *            订单
	 * @param market
	 *            行情
	 * @return 否达到止盈点
	 */
	private boolean isReachProfitPoint(FuturesOrder order) {
		// BigDecimal lastPrice =
		// quoteContainer.getLastPrice(order.getCommoditySymbol(),
		// order.getContractNo());
		// if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
		// BigDecimal profitOrLoss = orderService.getProfitOrLossCurrency(order,
		// lastPrice);
		// BigDecimal limitProfit =
		// order.getPerUnitLimitProfitAmount().multiply(order.getTotalQuantity());
		// if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
		// System.out.println(
		// "监控订单：" + order.getId() + "，最新价：" + lastPrice + "，浮动盈亏：" +
		// profitOrLoss + "，止盈：" + limitProfit);
		// }
		// if (profitOrLoss.compareTo(BigDecimal.ZERO) > 0 &&
		// profitOrLoss.compareTo(limitProfit) >= 0) {
		// BigDecimal minWave = order.getContract().getCommodity().getMinWave();
		// BigDecimal perWaveMoney =
		// order.getContract().getCommodity().getPerWaveMoney();
		// BigDecimal waveCount =
		// order.getPerUnitLimitProfitAmount().divide(perWaveMoney, 0,
		// RoundingMode.DOWN);
		// BigDecimal wavePrice = minWave.multiply(waveCount);
		// BigDecimal sellingPrice = order.getBuyingPrice();
		// if (order.getOrderType() == FuturesOrderType.BuyUp) {
		// sellingPrice = sellingPrice.add(wavePrice);
		// } else {
		// sellingPrice = sellingPrice.subtract(wavePrice);
		// }
		// orderService.unwindOrder(order.getId(), sellingPrice,
		// FuturesWindControlType.ReachProfitPoint);
		// return true;
		// }
		// }
		return false;
	}

	/**
	 * 判断是否达到止损点
	 * 
	 * @param order
	 *            订单
	 * @param market
	 *            行情
	 * @return 是否达到止损点
	 */
	private boolean isReachLossPoint(FuturesOrder order) {
		// BigDecimal lastPrice =
		// quoteContainer.getLastPrice(order.getCommoditySymbol(),
		// order.getContractNo());
		// if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
		// BigDecimal profitOrLoss = orderService.getProfitOrLossCurrency(order,
		// lastPrice);
		// BigDecimal limitLoss =
		// order.getPerUnitLimitLossAmount().multiply(order.getTotalQuantity());
		// if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
		// System.out.println(
		// "监控订单：" + order.getId() + "，最新价：" + lastPrice + "，浮动盈亏：" +
		// profitOrLoss + "，止损：" + limitLoss);
		// }
		// if (profitOrLoss.compareTo(BigDecimal.ZERO) < 0 &&
		// profitOrLoss.abs().compareTo(limitLoss.abs()) >= 0) {
		// BigDecimal minWave = order.getContract().getCommodity().getMinWave();
		// BigDecimal perWaveMoney =
		// order.getContract().getCommodity().getPerWaveMoney();
		// BigDecimal waveCount =
		// order.getPerUnitLimitLossAmount().divide(perWaveMoney, 0,
		// RoundingMode.DOWN);
		// BigDecimal wavePrice = minWave.multiply(waveCount);
		// BigDecimal sellingPrice = order.getBuyingPrice();
		// if (order.getOrderType() == FuturesOrderType.BuyUp) {
		// sellingPrice = sellingPrice.subtract(wavePrice);
		// } else {
		// sellingPrice = sellingPrice.add(wavePrice);
		// }
		// orderService.unwindOrder(order.getId(), sellingPrice,
		// FuturesWindControlType.ReachLossPoint);
		// return true;
		// }
		// }
		return false;
	}

	/**
	 * 计算订单止损价格
	 * 
	 * @param order
	 *            订单
	 * @return 止损价格
	 */
	private BigDecimal computeLimitLossPrice(FuturesOrder order) {
		// FuturesOrderType orderType = order.getOrderType();
		// BigDecimal buyingPrice = order.getBuyingPrice();
		// // 用户设置
		// Integer limitLossType = order.getLimitLossType();
		// BigDecimal perUnitLimitLossAmount =
		// order.getPerUnitLimitLossAmount();
		// // 波动设置
		// BigDecimal minWave = order.getContract().getCommodity().getMinWave();
		// BigDecimal perWaveMoney =
		// order.getContract().getCommodity().getPerWaveMoney();
		// // 货币汇率
		// // FuturesCurrencyRate rate =
		// // rateService.findByCurrency(order.getCommodityCurrency());
		// if (buyingPrice != null) {
		// // 获取用户设置的止损价格
		// BigDecimal userSetNeedWavePrice = null;
		// if (limitLossType != null && limitLossType == 1 &&
		// perUnitLimitLossAmount != null) {
		// // type为行情价格
		// if (orderType == FuturesOrderType.BuyUp &&
		// perUnitLimitLossAmount.compareTo(buyingPrice) < 0) {
		// userSetNeedWavePrice = buyingPrice.subtract(perUnitLimitLossAmount);
		// } else if (orderType == FuturesOrderType.BuyFall &&
		// perUnitLimitLossAmount.compareTo(buyingPrice) > 0) {
		// userSetNeedWavePrice = perUnitLimitLossAmount.subtract(buyingPrice);
		// }
		// } else if (limitLossType != null && limitLossType == 2 &&
		// perUnitLimitLossAmount != null) {
		// // type为每手亏损金额
		// userSetNeedWavePrice = perUnitLimitLossAmount.divide(perWaveMoney, 2,
		// RoundingMode.DOWN)
		// .multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN);
		// }
		// // 获取最终需要波动的价格
		// BigDecimal lastNeedWavePrice = userSetNeedWavePrice;
		// if (lastNeedWavePrice != null) {
		// if (orderType == FuturesOrderType.BuyUp) {
		// return buyingPrice.subtract(lastNeedWavePrice.abs());
		// } else if (orderType == FuturesOrderType.BuyFall) {
		// return buyingPrice.add(lastNeedWavePrice.abs());
		// }
		// }
		// }
		return null;
	}

	/**
	 * 计算订单止盈价格
	 * 
	 * @param order
	 *            订单
	 * @return 止盈价格
	 */
	private BigDecimal computeLimitProfitPrice(FuturesOrder order) {
		// FuturesOrderType orderType = order.getOrderType();
		// BigDecimal buyingPrice = order.getBuyingPrice();
		// // 用户设置
		// Integer limitProfitType = order.getLimitProfitType();
		// BigDecimal perUnitLimitProfitAmount =
		// order.getPerUnitLimitProfitAmount();
		// // 波动设置
		// BigDecimal minWave = order.getContract().getCommodity().getMinWave();
		// BigDecimal perWaveMoney =
		// order.getContract().getCommodity().getPerWaveMoney();
		// // 货币汇率
		// FuturesCurrencyRate rate =
		// rateService.findByCurrency(order.getCommodityCurrency());
		// if (buyingPrice != null && perUnitLimitProfitAmount != null) {
		// if (limitProfitType != null && limitProfitType == 1 &&
		// perUnitLimitProfitAmount != null) {
		// // type为行情价格
		// if (orderType == FuturesOrderType.BuyUp &&
		// perUnitLimitProfitAmount.compareTo(buyingPrice) > 0) {
		// return perUnitLimitProfitAmount;
		// } else if (orderType == FuturesOrderType.BuyFall
		// && perUnitLimitProfitAmount.compareTo(buyingPrice) < 0) {
		// return perUnitLimitProfitAmount;
		// }
		// } else if (limitProfitType != null && limitProfitType == 2 && rate !=
		// null && rate.getRate() != null
		// && perUnitLimitProfitAmount != null) {
		// // type为每手盈利金额
		// BigDecimal needWavePrice =
		// perUnitLimitProfitAmount.divide(perWaveMoney, 2, RoundingMode.DOWN)
		// .multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN);
		// if (orderType == FuturesOrderType.BuyUp) {
		// return buyingPrice.add(needWavePrice);
		// } else if (orderType == FuturesOrderType.BuyFall) {
		// return buyingPrice.subtract(needWavePrice);
		// }
		// }
		// }
		return null;
	}

	private void retry(MonitorStopLossOrProfitMessage messgeObj) {
		try {
			int consumeCount = messgeObj.getConsumeCount();
			messgeObj.setConsumeCount(consumeCount + 1);
			Thread.sleep(50);
			if (messgeObj.getMaxConsumeCount() > 0 && consumeCount < messgeObj.getMaxConsumeCount()) {
				producer.sendMessage(RabbitmqConfiguration.monitorStopLossOrProfitQueueName, messgeObj);
			} else if (messgeObj.getMaxConsumeCount() <= 0) {
				producer.sendMessage(RabbitmqConfiguration.monitorStopLossOrProfitQueueName, messgeObj);
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					RabbitmqConfiguration.monitorStopLossOrProfitQueueName + " message retry exception!", ex);
		}
	}

	public void monitorContractOrder(final Long contractOrderId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!monitorContractOrderIdList.contains(contractOrderId)) {
					MonitorStopLossOrProfitMessage messgeObj = new MonitorStopLossOrProfitMessage();
					messgeObj.setContractOrderId(contractOrderId);
					producer.sendMessage(RabbitmqConfiguration.monitorStopLossOrProfitQueueName, messgeObj);
					monitorContractOrderIdList.add(contractOrderId);
				}
			}
		}).start();
	}

	/**
	 * 获取所有持仓中的合约订单
	 * 
	 * @return 持仓中的订单
	 */
	private List<FuturesContractOrder> retrivePositionContractOrders() {
		return contractOrderDao.retrivePositionContractOrders();
	}

}
