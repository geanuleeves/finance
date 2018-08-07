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
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorStopLossOrProfitMessage;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

@Component
@RabbitListener(queues = {
		RabbitmqConfiguration.monitorStopLossOrProfitQueueName }, containerFactory = "monitorStopLossOrProfitContainerFactory")
public class MonitorStopLossOrProfitConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private FuturesOrderService orderService;

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
			logger.info("监控止损止盈:{}", message);
		}
		MonitorStopLossOrProfitMessage messgeObj = JacksonUtil.decode(message, MonitorStopLossOrProfitMessage.class);
		if(messgeObj.getConsumeCount() == 0) {
			// 第一次消费消息，输出日志
			logger.info("第一次消费监控止损止盈消息:{}", message);
		}
		try {
			FuturesContractOrder order = contractOrderDao.retrieve(messgeObj.getContractOrderId());
			if (order == null) {
				return;
			}
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
					boolean needMonitorBuyUp = true;
					boolean needMonitorBuyFall = true;
					// step 1 : 买涨订单
					if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal buyUpLimitProfit = order.getBuyUpPerUnitLimitProfitAmount();
						BigDecimal buyUpLimitLoss = order.getBuyUpPerUnitLimitLossAmount();
						BigDecimal buyUpAvgFillPrice = orderService.getOpenAvgFillPrice(order.getPublisherId(),
								contract.getId(), FuturesOrderType.BuyUp.getIndex());
						// step 1.1 : 买涨是否止盈
						if (buyUpLimitProfit != null) {
							if (buyUpLimitProfit.compareTo(buyUpAvgFillPrice) > 0
									&& market.getLastPrice().compareTo(buyUpLimitProfit) >= 0) {
								logger.info("{}买涨订单{}手达到止盈，止盈价格{}，此时的行情{}", order.getId(),
										buyUpCanUnwind, buyUpLimitProfit,
										JacksonUtil.encode(market));
								BigDecimal stopLossOrProfitPrice = BigDecimal.ZERO;
								BigDecimal[] divideArr = buyUpLimitProfit.divideAndRemainder(minWave);
								stopLossOrProfitPrice = divideArr[0].multiply(minWave);
								orderService.doUnwind(contract, order, FuturesOrderType.BuyUp,
										buyUpCanUnwind, FuturesTradePriceType.MKT, null,
										order.getPublisherId(), FuturesWindControlType.ReachProfitPoint, false, true,
										stopLossOrProfitPrice);
								needMonitorBuyUp = false;
							}
						}
						// step 1.2 : 买涨是否止损
						if (buyUpLimitLoss != null) {
							if (buyUpLimitLoss.compareTo(buyUpAvgFillPrice) < 0
									&& market.getLastPrice().compareTo(buyUpLimitLoss) <= 0) {
								logger.info("{}买涨订单{}手达到止损，止损价格{}，此时的行情{}", order.getId(),
										buyUpCanUnwind, buyUpLimitLoss, JacksonUtil.encode(market));
								BigDecimal stopLossOrProfitPrice = BigDecimal.ZERO;
								BigDecimal[] divideArr = buyUpLimitLoss.divideAndRemainder(minWave);
								stopLossOrProfitPrice = divideArr[0].multiply(minWave);
								orderService.doUnwind(contract, order, FuturesOrderType.BuyUp,
										buyUpCanUnwind, FuturesTradePriceType.MKT, null,
										order.getPublisherId(), FuturesWindControlType.ReachLossPoint, false, true,
										stopLossOrProfitPrice);
								needMonitorBuyUp = false;
							}
						}
						if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
							logger.info(
									"订单日志{}，buyUpCanUnwind:{}，buyUpAvgFillPrice{}，buyUpLimitProfit：{}，buyUpLimitLoss：{}",
									order.getId(), buyUpCanUnwind, buyUpAvgFillPrice, buyUpLimitProfit, buyUpLimitLoss);
						}
					} else {
						needMonitorBuyUp = false;
					}
					// step 2 : 买跌订单
					BigDecimal buyFallLimitProfit = order.getBuyFallPerUnitLimitProfitAmount();
					BigDecimal buyFallLimitLoss = order.getBuyFallPerUnitLimitLossAmount();
					if (buyFallCanUnwind != null && buyFallCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
						BigDecimal buyFallAvgFillPrice = orderService.getOpenAvgFillPrice(order.getPublisherId(),
								contract.getId(), FuturesOrderType.BuyFall.getIndex());
						// step 2.1 : 买跌是否止盈
						if (buyFallLimitProfit != null) {
							if (buyFallLimitProfit.compareTo(buyFallAvgFillPrice) < 0
									&& market.getLastPrice().compareTo(buyFallLimitProfit) <= 0) {
								logger.info("{}买跌订单{}手达到止盈，止盈价格{}，此时的行情{}", order.getId(),
										buyFallCanUnwind, buyFallLimitProfit,
										JacksonUtil.encode(market));
								BigDecimal stopLossOrProfitPrice = BigDecimal.ZERO;
								BigDecimal[] divideArr = buyFallLimitProfit.divideAndRemainder(minWave);
								stopLossOrProfitPrice = divideArr[0].multiply(minWave);
								orderService.doUnwind(contract, order, FuturesOrderType.BuyFall,
										buyFallCanUnwind, FuturesTradePriceType.MKT, null,
										order.getPublisherId(), FuturesWindControlType.ReachProfitPoint, false, true,
										stopLossOrProfitPrice);
								needMonitorBuyFall = false;
							}
						}
						// step 2.2 : 买跌是否止损
						if (buyFallLimitLoss != null) {
							if (buyFallLimitLoss.compareTo(buyFallAvgFillPrice) > 0
									&& market.getLastPrice().compareTo(buyFallLimitLoss) >= 0) {
								logger.info("{}买跌订单{}手达到止损，止损价格{}，此时的行情{}", order.getId(),
										buyFallCanUnwind, buyFallLimitLoss,
										JacksonUtil.encode(market));
								BigDecimal stopLossOrProfitPrice = BigDecimal.ZERO;
								BigDecimal[] divideArr = buyFallLimitLoss.divideAndRemainder(minWave);
								stopLossOrProfitPrice = divideArr[0].multiply(minWave);
								orderService.doUnwind(contract, order, FuturesOrderType.BuyFall,
										buyFallCanUnwind, FuturesTradePriceType.MKT, null,
										order.getPublisherId(), FuturesWindControlType.ReachLossPoint, false, true,
										stopLossOrProfitPrice);
								needMonitorBuyFall = false;
							}
						}
						if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
							logger.info(
									"订单日志{}，buyFallCanUnwind:{}，buyFallAvgFillPrice{}，buyFallLimitProfit：{}，buyFallLimitLoss：{}",
									order.getId(), buyFallCanUnwind, buyFallAvgFillPrice, buyFallLimitProfit,
									buyFallLimitLoss);
						}
					} else {
						needMonitorBuyFall = false;
					}
					if (!needMonitorBuyUp && !needMonitorBuyFall) {
						isNeedRetry = false;
					}
				}
			} else {
				isNeedRetry = false;
			}
			if (isNeedRetry) {
				retry(messgeObj);
			} else if(messgeObj.getConsumeCount() < 5) {
				retry(messgeObj);
			} else {
				monitorContractOrderIdList.remove(messgeObj.getContractOrderId());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
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
