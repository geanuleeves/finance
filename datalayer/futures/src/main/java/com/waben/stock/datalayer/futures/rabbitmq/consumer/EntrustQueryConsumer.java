package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.EntrustQueryMessage;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesGatewayOrder;
import com.waben.stock.interfaces.dto.futures.MarketAveragePrice;
import com.waben.stock.interfaces.enums.FuturesActionType;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
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
	private QuoteContainer quoteContainer;

	// @Autowired
	// private ProfileBusiness profileBusiness;

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("消费期货委托查询消息:{}", message);
		}
		EntrustQueryMessage messgeObj = JacksonUtil.decode(message, EntrustQueryMessage.class);
		try {
			Long orderId = messgeObj.getOrderId();
			FuturesOrder order = orderService.findById(orderId);
			if (order == null) {
				return;
			}
			Integer entrustType = messgeObj.getEntrustType();
			boolean isNeedRetry = true;
			if(orderService.isTradeTime(order)) {
				if (entrustType == 1) {
					// 开仓
					isNeedRetry = openwind(order);
				} else if (entrustType == 2) {
					// 平仓
					isNeedRetry = unwind(order, false);
				} else if (entrustType == 3) {
					// 反手
					isNeedRetry = unwind(order, true);
				} else {
					logger.error("错误的委托类型!");
					isNeedRetry = false;
				}
			}
			if (isNeedRetry) {
				retry(messgeObj);
			}
			// Long gatewayOrderId = messgeObj.getGatewayOrderId();
			// FuturesGatewayOrder gatewayOrder =
			// TradeFuturesOverHttp.retriveByGatewayId(profileBusiness.isProd(),
			// gatewayOrderId);
			// boolean isNeedRetry = true;
			// if (TradeFuturesOverHttp.apiType == 1) {
			// isNeedRetry = checkYingtouOrder(gatewayOrder, entrustType,
			// orderId);
			// } else if (TradeFuturesOverHttp.apiType == 2) {
			// isNeedRetry = checkYishengOrder(gatewayOrder, entrustType,
			// orderId);
			// } else {
			// isNeedRetry = false;
			// }
			// if (isNeedRetry) {
			// retry(messgeObj);
			// }
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
	}

	private boolean unwind(FuturesOrder order, boolean isBackhand) {
		String commodityNo = order.getCommoditySymbol();
		String contractNo = order.getContractNo();
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal totalQuantity = order.getTotalQuantity();
		FuturesCommodity commodity = order.getContract().getCommodity();
		BigDecimal minWave = commodity.getMinWave();
		Integer closeSlipPoint = orderType == FuturesOrderType.BuyUp ? commodity.getBuyUpCloseSlipPoint()
				: commodity.getBuyFallCloseSlipPoint();
		BigDecimal reverse = orderType == FuturesOrderType.BuyUp ? new BigDecimal(-1) : new BigDecimal(1);
		Long orderId = order.getId();
		boolean isNeedRetry = true;
		FuturesActionType actionType = orderType == FuturesOrderType.BuyUp ? FuturesActionType.SELL
				: FuturesActionType.BUY;
		FuturesTradePriceType priceType = order.getSellingPriceType();
		if (order.getState() == FuturesOrderState.SellingEntrust) {
			MarketAveragePrice avgPrice = null;
			FuturesContractMarket market = null;
			if (priceType == FuturesTradePriceType.MKT) {
				market = quoteContainer.getQuote(commodityNo, contractNo); 
				BigDecimal lastPrice = market.getLastPrice();
				// 市价
				if(lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0) {
					avgPrice = new MarketAveragePrice();
					avgPrice.setAvgFillPrice(lastPrice);
					avgPrice.setCommodityNo(commodityNo);
					avgPrice.setContractNo(contractNo);
					avgPrice.setFilled(totalQuantity);
					avgPrice.setRemaining(BigDecimal.ZERO);
					avgPrice.setTotalQuantity(totalQuantity);
					avgPrice.setTotalFillCost(totalQuantity.multiply(avgPrice.getAvgFillPrice()));
				} else {
					avgPrice = new MarketAveragePrice();
					avgPrice.setAvgFillPrice(BigDecimal.ZERO);
					avgPrice.setCommodityNo(commodityNo);
					avgPrice.setContractNo(contractNo);
					avgPrice.setFilled(BigDecimal.ZERO);
					avgPrice.setRemaining(totalQuantity);
					avgPrice.setTotalQuantity(totalQuantity);
					avgPrice.setTotalFillCost(BigDecimal.ZERO);
				}
				// avgPrice = orderService.computeMktAvgPrice(commodityNo, contractNo, actionType, totalQuantity);
			} else {
				// 限价
				avgPrice = orderService.computeLmtAvgPrice(commodityNo, contractNo, actionType, totalQuantity,
						order.getBuyingEntrustPrice());
			}
			if (avgPrice.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				if (avgPrice.getRemaining().compareTo(BigDecimal.ZERO) <= 0) {
					logger.info("订单{}【平仓】成功，买入成功时候的行情为{}", order.getId(), market != null ? JacksonUtil.encode(market) : "");
					BigDecimal sellingPrice = avgPrice.getAvgFillPrice();
					if (closeSlipPoint != null && closeSlipPoint > 0) {
						sellingPrice = sellingPrice
								.add(new BigDecimal(closeSlipPoint).multiply(minWave).multiply(reverse));
					}
					// 已平仓
					orderService.unwindOrder(orderId, avgPrice.getFilled(), avgPrice.getRemaining(),
							avgPrice.getAvgFillPrice(), avgPrice.getTotalFillCost(), sellingPrice);
					if (isBackhand) {
						orderService.backhandPlaceOrder(orderId);
					}
					isNeedRetry = false;
				} else {
					// 部分已平仓
					orderService.partUnwindOrder(orderId, avgPrice.getFilled(), avgPrice.getRemaining(),
							avgPrice.getAvgFillPrice(), avgPrice.getTotalFillCost());
				}
			}
		} else if (order.getState() == FuturesOrderState.PartUnwind) {
			MarketAveragePrice avgPrice = null;
			if (priceType == FuturesTradePriceType.MKT) {
				// 市价
				avgPrice = orderService.computeMktAvgPrice(commodityNo, contractNo, actionType,
						order.getCloseRemaining());
			} else {
				// 限价
				avgPrice = orderService.computeLmtAvgPrice(commodityNo, contractNo, actionType,
						order.getCloseRemaining(), order.getBuyingEntrustPrice());
			}
			if (avgPrice.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal filled = order.getCloseFilled().add(avgPrice.getFilled());
				BigDecimal totalFillCost = order.getCloseTotalFillCost().add(avgPrice.getTotalFillCost());
				BigDecimal avgFillPrice = totalFillCost.divide(filled).setScale(10, RoundingMode.DOWN);
				BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(minWave);
				avgFillPrice = divideArr[0].multiply(minWave);
				if (actionType == FuturesActionType.BUY && divideArr[1].compareTo(BigDecimal.ZERO) > 0) {
					avgFillPrice = divideArr[0].add(new BigDecimal(1)).multiply(minWave);
				}
				if (avgPrice.getRemaining().compareTo(BigDecimal.ZERO) <= 0) {
					BigDecimal sellingPrice = avgPrice.getAvgFillPrice();
					if (closeSlipPoint != null && closeSlipPoint > 0) {
						sellingPrice = sellingPrice
								.add(new BigDecimal(closeSlipPoint).multiply(minWave).multiply(reverse));
					}
					// 已平仓
					orderService.unwindOrder(orderId, filled, avgPrice.getRemaining(), avgFillPrice, totalFillCost,
							sellingPrice);
					if (isBackhand) {
						orderService.backhandPlaceOrder(orderId);
					}
					isNeedRetry = false;
				} else {
					// 部分已平仓
					orderService.partUnwindOrder(orderId, filled, avgPrice.getRemaining(), avgFillPrice, totalFillCost);
				}
			}
		} else {
			isNeedRetry = false;
		}
		return isNeedRetry;
	}

	private boolean openwind(FuturesOrder order) {
		String commodityNo = order.getCommoditySymbol();
		String contractNo = order.getContractNo();
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal totalQuantity = order.getTotalQuantity();
		FuturesCommodity commodity = order.getContract().getCommodity();
		BigDecimal minWave = commodity.getMinWave();
		Integer openSlipPoint = orderType == FuturesOrderType.BuyUp ? commodity.getBuyUpOpenSlipPoint()
				: commodity.getBuyFallOpenSlipPoint();
		BigDecimal reverse = orderType == FuturesOrderType.BuyUp ? new BigDecimal(1) : new BigDecimal(-1);
		Long orderId = order.getId();
		boolean isNeedRetry = true;
		FuturesActionType actionType = orderType == FuturesOrderType.BuyUp ? FuturesActionType.BUY
				: FuturesActionType.SELL;
		FuturesTradePriceType priceType = order.getBuyingPriceType();
		if (order.getState() == FuturesOrderState.BuyingEntrust) {
			MarketAveragePrice avgPrice = null;
			FuturesContractMarket market = null;
			if (priceType == FuturesTradePriceType.MKT) {
				market = quoteContainer.getQuote(commodityNo, contractNo); 
				// 市价
				avgPrice = new MarketAveragePrice();
				avgPrice.setAvgFillPrice(order.getOrderType() == FuturesOrderType.BuyUp ? market.getAskPrice() : market.getBidPrice());
				avgPrice.setCommodityNo(commodityNo);
				avgPrice.setContractNo(contractNo);
				avgPrice.setFilled(totalQuantity);
				avgPrice.setRemaining(BigDecimal.ZERO);
				avgPrice.setTotalQuantity(totalQuantity);
				avgPrice.setTotalFillCost(totalQuantity.multiply(avgPrice.getAvgFillPrice()));
				// avgPrice = orderService.computeMktAvgPrice(commodityNo, contractNo, actionType, totalQuantity);
			} else {
				avgPrice = new MarketAveragePrice();
				avgPrice.setAvgFillPrice(BigDecimal.ZERO);
				avgPrice.setCommodityNo(commodityNo);
				avgPrice.setContractNo(contractNo);
				avgPrice.setFilled(BigDecimal.ZERO);
				avgPrice.setRemaining(totalQuantity);
				avgPrice.setTotalQuantity(totalQuantity);
				avgPrice.setTotalFillCost(BigDecimal.ZERO);
				market = quoteContainer.getQuote(commodityNo, contractNo); 
				BigDecimal askPrice = market.getAskPrice();
				BigDecimal bidPrice = market.getBidPrice();
				BigDecimal buyingEntrustPrice = order.getBuyingEntrustPrice();
				// 限价
				if(order.getOrderType() == FuturesOrderType.BuyUp && askPrice.compareTo(buyingEntrustPrice) <= 0) {
					avgPrice.setAvgFillPrice(askPrice);
					avgPrice.setFilled(totalQuantity);
					avgPrice.setRemaining(BigDecimal.ZERO);
					avgPrice.setTotalQuantity(totalQuantity);
					avgPrice.setTotalFillCost(totalQuantity.multiply(avgPrice.getAvgFillPrice()));
				} else if(order.getOrderType() == FuturesOrderType.BuyFall && bidPrice.compareTo(buyingEntrustPrice) >= 0) {
					avgPrice.setAvgFillPrice(bidPrice);
					avgPrice.setFilled(totalQuantity);
					avgPrice.setRemaining(BigDecimal.ZERO);
					avgPrice.setTotalQuantity(totalQuantity);
					avgPrice.setTotalFillCost(totalQuantity.multiply(avgPrice.getAvgFillPrice()));
				}
				//avgPrice = orderService.computeLmtAvgPrice(commodityNo, contractNo, actionType, totalQuantity,
				//		order.getBuyingEntrustPrice());
			}
			if (avgPrice.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				if (avgPrice.getRemaining().compareTo(BigDecimal.ZERO) <= 0) {
					logger.info("订单{}【开仓】成功，买入成功时候的行情为{}", order.getId(), market != null ? JacksonUtil.encode(market) : "");
					// 持仓中
					BigDecimal buyingPrice = avgPrice.getAvgFillPrice();
					if (openSlipPoint != null && openSlipPoint > 0) {
						buyingPrice = buyingPrice
								.add(new BigDecimal(openSlipPoint).multiply(minWave).multiply(reverse));
					}
					orderService.positionOrder(orderId, avgPrice.getFilled(), avgPrice.getRemaining(),
							avgPrice.getAvgFillPrice(), avgPrice.getTotalFillCost(), buyingPrice);
					isNeedRetry = false;
				} else {
					// 部分买入成功
					orderService.partPositionOrder(orderId, avgPrice.getFilled(), avgPrice.getRemaining(),
							avgPrice.getAvgFillPrice(), avgPrice.getTotalFillCost());
				}
			}
		} else if (order.getState() == FuturesOrderState.PartPosition) {
			MarketAveragePrice avgPrice = null;
			if (priceType == FuturesTradePriceType.MKT) {
				// 市价
				avgPrice = orderService.computeMktAvgPrice(commodityNo, contractNo, actionType,
						order.getOpenRemaining());
			} else {
				// 限价
				avgPrice = orderService.computeLmtAvgPrice(commodityNo, contractNo, actionType,
						order.getOpenRemaining(), order.getBuyingEntrustPrice());
			}
			if (avgPrice.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				BigDecimal filled = order.getOpenFilled().add(avgPrice.getFilled());
				BigDecimal totalFillCost = order.getOpenTotalFillCost().add(avgPrice.getTotalFillCost());
				BigDecimal avgFillPrice = totalFillCost.divide(filled).setScale(10, RoundingMode.DOWN);
				BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(minWave);
				avgFillPrice = divideArr[0].multiply(minWave);
				if (actionType == FuturesActionType.BUY && divideArr[1].compareTo(BigDecimal.ZERO) > 0) {
					avgFillPrice = divideArr[0].add(new BigDecimal(1)).multiply(minWave);
				}
				if (avgPrice.getRemaining().compareTo(BigDecimal.ZERO) <= 0) {
					BigDecimal buyingPrice = avgPrice.getAvgFillPrice();
					if (openSlipPoint != null && openSlipPoint > 0) {
						buyingPrice = buyingPrice
								.add(new BigDecimal(openSlipPoint).multiply(minWave).multiply(reverse));
					}
					// 持仓中
					orderService.positionOrder(orderId, filled, avgPrice.getRemaining(), avgFillPrice, totalFillCost,
							buyingPrice);
					isNeedRetry = false;
				} else {
					// 部分买入成功
					orderService.partPositionOrder(orderId, filled, avgPrice.getRemaining(), avgFillPrice,
							totalFillCost);
				}
			}
		} else {
			isNeedRetry = false;
		}
		return isNeedRetry;
	}

	@SuppressWarnings("unused")
	private boolean checkYingtouOrder(FuturesGatewayOrder gatewayOrder, Integer entrustType, Long orderId) {
		boolean isNeedRetry = true;
		String status = gatewayOrder.getOrderStatus();
		if (entrustType == 1) {
			if ("Cancelled".equals(status)) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if ("Submitted".equals(status) && gatewayOrder.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				// 部分买入成功
				orderService.partPositionOrder(orderId);
			} else if ("Filled".equals(status) && gatewayOrder.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
				// 持仓中
				orderService.positionOrder(orderId, gatewayOrder.getLastFillPrice());
				isNeedRetry = false;
			}
		} else if (entrustType == 2) {
			if ("Cancelled".equals(status)) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if ("Submitted".equals(status) && gatewayOrder.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				// 部分已平仓
				orderService.partUnwindOrder(orderId);
			} else if ("Filled".equals(status) && gatewayOrder.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice(), null);
				isNeedRetry = false;
			}
		} else if (entrustType == 3) {
			if ("Cancelled".equals(status)) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if ("Submitted".equals(status) && gatewayOrder.getFilled().compareTo(BigDecimal.ZERO) > 0) {
				// 部分已平仓
				orderService.partUnwindOrder(orderId);
			} else if ("Filled".equals(status) && gatewayOrder.getRemaining().compareTo(BigDecimal.ZERO) == 0) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice(), null);
				// 反手以市价买入
				orderService.backhandPlaceOrder(orderId);
				isNeedRetry = false;
			}
		} else {
			logger.error("错误的委托类型!");
			isNeedRetry = false;
		}
		return isNeedRetry;
	}

	@SuppressWarnings("unused")
	private boolean checkYishengOrder(FuturesGatewayOrder gatewayOrder, Integer entrustType, Long orderId) {
		if (gatewayOrder == null) {
			logger.info("异常的订单，期货网关未查询到该订单：" + orderId);
			return false;
		}
		boolean isNeedRetry = true;
		Integer state = gatewayOrder.getOrderState();
		if (entrustType == 1 || entrustType == 2 || entrustType == 3) {
			if (state != null && state == 5 && entrustType == 1) {
				// 部分买入成功
				orderService.partPositionOrder(orderId);
			} else if (state != null && state == 5 && (entrustType == 2 || entrustType == 3)) {
				// 部分已平仓
				orderService.partUnwindOrder(orderId);
			} else if (state != null && state == 6 && entrustType == 1) {
				// 持仓中
				orderService.positionOrder(orderId, gatewayOrder.getLastFillPrice());
				isNeedRetry = false;
			} else if (state != null && state == 6 && entrustType == 2) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice(), null);
				isNeedRetry = false;
			} else if (state != null && state == 6 && entrustType == 3) {
				// 已平仓
				orderService.unwindOrder(orderId, gatewayOrder.getLastFillPrice(), null);
				// 反手以市价买入
				orderService.backhandPlaceOrder(orderId);
				isNeedRetry = false;
			} else if (state != null && state == 9) {
				// 已取消
				orderService.canceledOrder(orderId);
				isNeedRetry = false;
			} else if (state != null && state == 11) {
				// 已失败
				orderService.failureOrder(orderId);
				isNeedRetry = false;
			}
		} else {
			logger.error("错误的委托类型!");
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
	
	public void entrustQuery(final Long orderId, final Integer entrustType) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				EntrustQueryMessage msg = new EntrustQueryMessage();
				msg.setOrderId(orderId);
				msg.setGatewayOrderId(-1L);
				msg.setEntrustType(entrustType);
				producer.sendMessage(RabbitmqConfiguration.entrustQueryQueueName, msg);
			}
		}).start();
	}

}
