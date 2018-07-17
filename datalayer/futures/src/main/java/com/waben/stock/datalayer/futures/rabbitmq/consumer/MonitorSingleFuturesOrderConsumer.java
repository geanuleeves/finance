package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorSingleFuturesOrderMessage;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

@Component
@RabbitListener(queues = {
		RabbitmqConfiguration.monitorSingleFuturesOrderQueueName }, containerFactory = "monitorSingleOrderContainerFactory")
public class MonitorSingleFuturesOrderConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private QuoteContainer quoteContainer;

	private List<Long> monitorOrderIdList = Collections.synchronizedList(new ArrayList<Long>());

	@PostConstruct
	public void init() {
		List<FuturesOrder> orderList = retrivePositionOrders();
		for (FuturesOrder order : orderList) {
			if (!monitorOrderIdList.contains(order.getId())) {
				monitorOrderIdList.add(order.getId());
			}
		}

		for (Long orderId : monitorOrderIdList) {
			MonitorSingleFuturesOrderMessage messgeObj = new MonitorSingleFuturesOrderMessage();
			messgeObj.setOrderId(orderId);
			producer.sendMessage(RabbitmqConfiguration.monitorSingleFuturesOrderQueueName, messgeObj);
		}
	}

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("监控当个订单:{}", message);
		}
		MonitorSingleFuturesOrderMessage messgeObj = JacksonUtil.decode(message,
				MonitorSingleFuturesOrderMessage.class);
		try {
			FuturesOrder order = orderService.findById(messgeObj.getOrderId());
			if (order != null) {
				Integer timeZoneGap = orderService.retriveTimeZoneGap(order);
				FuturesContract contract = order.getContract();
				boolean isNeedRetry = true;
				// step 1 : 是否达到止盈点
				if (orderService.isTradeTime(timeZoneGap, contract) && isReachProfitPoint(order)) {
					isNeedRetry = false;
				}
				// step 2 : 是否达到止损点
				if (orderService.isTradeTime(timeZoneGap, contract) && isReachLossPoint(order)) {
					isNeedRetry = false;
				}
				if (isNeedRetry) {
					retry(messgeObj);
				}
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
		BigDecimal lastPrice = quoteContainer.getLastPrice(order.getCommoditySymbol(), order.getContractNo());
		if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal profitOrLoss = orderService.getProfitOrLossCurrency(order, lastPrice);
			BigDecimal limitProfit = order.getPerUnitLimitProfitAmount().multiply(order.getTotalQuantity());
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				System.out.println(
						"监控订单：" + order.getId() + "，最新价：" + lastPrice + "，浮动盈亏：" + profitOrLoss + "，止盈：" + limitProfit);
			}
			if (profitOrLoss.compareTo(BigDecimal.ZERO) > 0 && profitOrLoss.compareTo(limitProfit) >= 0) {
				BigDecimal minWave = order.getContract().getCommodity().getMinWave();
				BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
				BigDecimal waveCount = order.getPerUnitLimitProfitAmount().divide(perWaveMoney, 0, RoundingMode.DOWN);
				BigDecimal wavePrice = minWave.multiply(waveCount);
				BigDecimal sellingPrice = order.getBuyingPrice();
				if (order.getOrderType() == FuturesOrderType.BuyUp) {
					sellingPrice = sellingPrice.add(wavePrice);
				} else {
					sellingPrice = sellingPrice.subtract(wavePrice);
				}
				orderService.unwindOrder(order.getId(), sellingPrice, FuturesWindControlType.ReachProfitPoint);
				return true;
			}
		}
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
		BigDecimal lastPrice = quoteContainer.getLastPrice(order.getCommoditySymbol(), order.getContractNo());
		if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal profitOrLoss = orderService.getProfitOrLossCurrency(order, lastPrice);
			BigDecimal limitLoss = order.getPerUnitLimitLossAmount().multiply(order.getTotalQuantity());
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				System.out.println(
						"监控订单：" + order.getId() + "，最新价：" + lastPrice + "，浮动盈亏：" + profitOrLoss + "，止损：" + limitLoss);
			}
			if (profitOrLoss.compareTo(BigDecimal.ZERO) < 0 && profitOrLoss.abs().compareTo(limitLoss.abs()) >= 0) {
				BigDecimal minWave = order.getContract().getCommodity().getMinWave();
				BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
				BigDecimal waveCount = order.getPerUnitLimitLossAmount().divide(perWaveMoney, 0, RoundingMode.DOWN);
				BigDecimal wavePrice = minWave.multiply(waveCount);
				BigDecimal sellingPrice = order.getBuyingPrice();
				if (order.getOrderType() == FuturesOrderType.BuyUp) {
					sellingPrice = sellingPrice.subtract(wavePrice);
				} else {
					sellingPrice = sellingPrice.add(wavePrice);
				}
				orderService.unwindOrder(order.getId(), sellingPrice, FuturesWindControlType.ReachLossPoint);
				return true;
			}
		}
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
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal buyingPrice = order.getBuyingPrice();
		// 用户设置
		Integer limitLossType = order.getLimitLossType();
		BigDecimal perUnitLimitLossAmount = order.getPerUnitLimitLossAmount();
		// 波动设置
		BigDecimal minWave = order.getContract().getCommodity().getMinWave();
		BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
		// 货币汇率
		// FuturesCurrencyRate rate =
		// rateService.findByCurrency(order.getCommodityCurrency());
		if (buyingPrice != null) {
			// 获取用户设置的止损价格
			BigDecimal userSetNeedWavePrice = null;
			if (limitLossType != null && limitLossType == 1 && perUnitLimitLossAmount != null) {
				// type为行情价格
				if (orderType == FuturesOrderType.BuyUp && perUnitLimitLossAmount.compareTo(buyingPrice) < 0) {
					userSetNeedWavePrice = buyingPrice.subtract(perUnitLimitLossAmount);
				} else if (orderType == FuturesOrderType.BuyFall && perUnitLimitLossAmount.compareTo(buyingPrice) > 0) {
					userSetNeedWavePrice = perUnitLimitLossAmount.subtract(buyingPrice);
				}
			} else if (limitLossType != null && limitLossType == 2 && perUnitLimitLossAmount != null) {
				// type为每手亏损金额
				userSetNeedWavePrice = perUnitLimitLossAmount.divide(perWaveMoney, 2, RoundingMode.DOWN)
						.multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN);
			}
			// 获取最终需要波动的价格
			BigDecimal lastNeedWavePrice = userSetNeedWavePrice;
			if (lastNeedWavePrice != null) {
				if (orderType == FuturesOrderType.BuyUp) {
					return buyingPrice.subtract(lastNeedWavePrice.abs());
				} else if (orderType == FuturesOrderType.BuyFall) {
					return buyingPrice.add(lastNeedWavePrice.abs());
				}
			}
		}
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
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal buyingPrice = order.getBuyingPrice();
		// 用户设置
		Integer limitProfitType = order.getLimitProfitType();
		BigDecimal perUnitLimitProfitAmount = order.getPerUnitLimitProfitAmount();
		// 波动设置
		BigDecimal minWave = order.getContract().getCommodity().getMinWave();
		BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
		// 货币汇率
		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
		if (buyingPrice != null && perUnitLimitProfitAmount != null) {
			if (limitProfitType != null && limitProfitType == 1 && perUnitLimitProfitAmount != null) {
				// type为行情价格
				if (orderType == FuturesOrderType.BuyUp && perUnitLimitProfitAmount.compareTo(buyingPrice) > 0) {
					return perUnitLimitProfitAmount;
				} else if (orderType == FuturesOrderType.BuyFall
						&& perUnitLimitProfitAmount.compareTo(buyingPrice) < 0) {
					return perUnitLimitProfitAmount;
				}
			} else if (limitProfitType != null && limitProfitType == 2 && rate != null && rate.getRate() != null
					&& perUnitLimitProfitAmount != null) {
				// type为每手盈利金额
				BigDecimal needWavePrice = perUnitLimitProfitAmount.divide(perWaveMoney, 2, RoundingMode.DOWN)
						.multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN);
				if (orderType == FuturesOrderType.BuyUp) {
					return buyingPrice.add(needWavePrice);
				} else if (orderType == FuturesOrderType.BuyFall) {
					return buyingPrice.subtract(needWavePrice);
				}
			}
		}
		return null;
	}

	private void retry(MonitorSingleFuturesOrderMessage messgeObj) {
		try {
			int consumeCount = messgeObj.getConsumeCount();
			messgeObj.setConsumeCount(consumeCount + 1);
			Thread.sleep(50);
			if (messgeObj.getMaxConsumeCount() > 0 && consumeCount < messgeObj.getMaxConsumeCount()) {
				producer.sendMessage(RabbitmqConfiguration.monitorSingleFuturesOrderQueueName, messgeObj);
			} else if (messgeObj.getMaxConsumeCount() <= 0) {
				producer.sendMessage(RabbitmqConfiguration.monitorSingleFuturesOrderQueueName, messgeObj);
			}
		} catch (Exception ex) {
			throw new RuntimeException(
					RabbitmqConfiguration.monitorSingleFuturesOrderQueueName + " message retry exception!", ex);
		}
	}

	public void monitorOrder(final Long orderId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!monitorOrderIdList.contains(orderId)) {
					MonitorSingleFuturesOrderMessage messgeObj = new MonitorSingleFuturesOrderMessage();
					messgeObj.setOrderId(orderId);
					producer.sendMessage(RabbitmqConfiguration.monitorSingleFuturesOrderQueueName, messgeObj);
					monitorOrderIdList.add(orderId);
				}
			}
		}).start();
	}

	/**
	 * 获取所有持仓中的订单
	 * 
	 * @return 持仓中的订单
	 */
	private List<FuturesOrder> retrivePositionOrders() {
		FuturesOrderState[] states = { FuturesOrderState.Position, FuturesOrderState.SellingEntrust };
		FuturesOrderQuery query = new FuturesOrderQuery();
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		query.setStates(states);
		Page<FuturesOrder> pages = orderService.pagesOrder(query);
		return pages.getContent();
	}

}
