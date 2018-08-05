package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorStrongPointMessage;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

@Component
@RabbitListener(queues = { RabbitmqConfiguration.monitorStrongPointQueueName })
public class MonitorStrongPointConsumer {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private RabbitmqProducer producer;

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	@Autowired
	private QuoteContainer quoteContainer;

	private List<Long> monitorPublisherList = Collections.synchronizedList(new ArrayList<Long>());

	@PostConstruct
	public void init() {
		List<FuturesContractOrder> orderList = retrivePositionContractOrders();
		for (FuturesContractOrder order : orderList) {
			Long publisherId = order.getPublisherId();
			if (!monitorPublisherList.contains(publisherId)) {
				monitorPublisherList.add(publisherId);
			}
		}
		for (Long publisherId : monitorPublisherList) {
			MonitorStrongPointMessage messgeObj = new MonitorStrongPointMessage();
			messgeObj.setPublisherId(publisherId);
			producer.sendMessage(RabbitmqConfiguration.monitorStrongPointQueueName, messgeObj);
		}
	}

	@RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("监控强平点:{}", message);
		}
		MonitorStrongPointMessage messgeObj = JacksonUtil.decode(message, MonitorStrongPointMessage.class);
		try {
			Long publisherId = messgeObj.getPublisherId();
			// step 1 : 获取资金账号
			CapitalAccountDto account = accountBusiness.fetchByPublisherId(publisherId);
			// step 2 : 获取持仓订单
			List<FuturesContractOrder> orderList = retrivePublisherPositionContractOrders(publisherId);
			boolean isNeedRetry = true;
			if (orderList != null && orderList.size() > 0) {
				boolean hasQuatity = false;
				for (FuturesContractOrder order : orderList) {
					BigDecimal buyUpCanUnwind = order.getBuyUpCanUnwindQuantity();
					BigDecimal buyFallCanUnwind = order.getBuyFallCanUnwindQuantity();
					if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
						hasQuatity = true;
					}
					if (buyFallCanUnwind != null && buyFallCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
						hasQuatity = true;
					}
				}
				if (!hasQuatity) {
					isNeedRetry = false;
				} else {
					// 执行强平逻辑
					doStongPoint(orderList, account);
					// TODO 隔夜
				}
			} else {
				isNeedRetry = false;
			}
			if (isNeedRetry) {
				retry(messgeObj);
			} else {
				monitorPublisherList.remove(messgeObj.getPublisherId());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
	}

	private BigDecimal computeFloatProfitOrLoss(FuturesContractOrder contractOrder) {
		Long publisherId = contractOrder.getPublisherId();
		Long contractId = contractOrder.getContract().getId();
		BigDecimal floatProfitOrLoss = BigDecimal.ZERO;
		FuturesCommodity commotidy = contractOrder.getContract().getCommodity();
		FuturesContractMarket market = quoteContainer.getQuote(commotidy.getSymbol(),
				contractOrder.getContract().getContractNo());

		BigDecimal buyUpCanUnwind = contractOrder.getBuyUpCanUnwindQuantity();
		BigDecimal buyFallCanUnwind = contractOrder.getBuyFallCanUnwindQuantity();
		if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0
				&& market.getBidPrice().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal buyUpOpenAvgFillPrice = orderService.getOpenAvgFillPrice(publisherId, contractId,
					FuturesOrderType.BuyUp.getIndex());
			floatProfitOrLoss = floatProfitOrLoss
					.add(orderService.computeProfitOrLoss(FuturesOrderType.BuyUp, buyUpCanUnwind, buyUpOpenAvgFillPrice,
							market.getBidPrice(), commotidy.getMinWave(), commotidy.getPerWaveMoney()));
		}
		if (buyFallCanUnwind != null && buyFallCanUnwind.compareTo(BigDecimal.ZERO) > 0
				&& market.getAskPrice().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal buyFallOpenAvgFillPrice = orderService.getOpenAvgFillPrice(publisherId, contractId,
					FuturesOrderType.BuyFall.getIndex());
			floatProfitOrLoss = floatProfitOrLoss.add(orderService.computeProfitOrLoss(FuturesOrderType.BuyFall,
					buyFallCanUnwind, buyFallOpenAvgFillPrice, market.getAskPrice(), commotidy.getMinWave(),
					commotidy.getPerWaveMoney()));
		}
		return floatProfitOrLoss;
	}

	/**
	 * 判断是否触发强平
	 * 
	 * @param orderList
	 *            订单列表
	 * @param account
	 *            资金账户
	 * @return 是否触发强平
	 */
	private void doStongPoint(List<FuturesContractOrder> orderList, CapitalAccountDto account) {
		BigDecimal totalStrong = BigDecimal.ZERO;
		BigDecimal totalProfitOrLoss = BigDecimal.ZERO;
		for (FuturesContractOrder order : orderList) {
			BigDecimal strongMoney = orderService.getStrongMoney(order);
			BigDecimal floatProfitOrLoss = computeFloatProfitOrLoss(order);
			order.setStrongMoney(strongMoney);
			order.setFloatProfitOrLoss(floatProfitOrLoss);
			// 计算强平金额
			totalStrong = totalStrong.add(strongMoney);
			// 计算浮动盈亏
			totalProfitOrLoss = totalProfitOrLoss.add(floatProfitOrLoss);
		}
		if (totalProfitOrLoss.compareTo(BigDecimal.ZERO) < 0
				&& totalProfitOrLoss.abs().compareTo(account.getAvailableBalance()) < 0) {
			// 根据盈亏值进行排序
			Collections.sort(orderList, new Comparator<FuturesContractOrder>() {
				@Override
				public int compare(FuturesContractOrder o1, FuturesContractOrder o2) {
					return o1.getFloatProfitOrLoss().compareTo(o2.getFloatProfitOrLoss());
				}
			});
			// 账户余额已经亏损完，计算超出的部分
			BigDecimal loss = totalProfitOrLoss.add(account.getAvailableBalance());
			for (FuturesContractOrder order : orderList) {
				FuturesContract contract = order.getContract();
				if (orderService.isTradeTime(contract.getCommodity().getExchange().getTimeZoneGap(), contract,
						FuturesTradeActionType.CLOSE)) {
					BigDecimal strongMoney = order.getStrongMoney();
					if (loss.abs().compareTo(strongMoney) >= 0) {
						// 强平
						BigDecimal buyUpQuantity = order.getBuyUpCanUnwindQuantity();
						BigDecimal buyFallQuantity = order.getBuyFallCanUnwindQuantity();
						if (buyUpQuantity.compareTo(BigDecimal.ZERO) > 0) {
							orderService.doUnwind(contract, order, FuturesOrderType.BuyUp, buyUpQuantity,
									FuturesTradePriceType.MKT, null, order.getPublisherId(),
									FuturesWindControlType.ReachStrongPoint, false, false, null);
						}
						if (buyFallQuantity.compareTo(BigDecimal.ZERO) > 0) {
							orderService.doUnwind(contract, order, FuturesOrderType.BuyFall, buyFallQuantity,
									FuturesTradePriceType.MKT, null, order.getPublisherId(),
									FuturesWindControlType.ReachStrongPoint, false, false, null);
						}
						loss = loss.add(strongMoney);
					}
				}
			}
		}
	}

	/**
	 * 获取所有持仓中的合约订单
	 * 
	 * @return 持仓中的订单
	 */
	private List<FuturesContractOrder> retrivePositionContractOrders() {
		return contractOrderDao.retrivePositionContractOrders();
	}

	/**
	 * 获取所有持仓中的合约订单
	 * 
	 * @return 持仓中的订单
	 */
	private List<FuturesContractOrder> retrivePublisherPositionContractOrders(Long publisherId) {
		return contractOrderDao.retrivePublisherPositionContractOrders(publisherId);
	}

	private void retry(MonitorStrongPointMessage messgeObj) {
		try {
			int consumeCount = messgeObj.getConsumeCount();
			messgeObj.setConsumeCount(consumeCount + 1);
			Thread.sleep(50);
			if (messgeObj.getMaxConsumeCount() > 0 && consumeCount < messgeObj.getMaxConsumeCount()) {
				producer.sendMessage(RabbitmqConfiguration.monitorStrongPointQueueName, messgeObj);
			} else if (messgeObj.getMaxConsumeCount() <= 0) {
				producer.sendMessage(RabbitmqConfiguration.monitorStrongPointQueueName, messgeObj);
			}
		} catch (Exception ex) {
			throw new RuntimeException(RabbitmqConfiguration.monitorStrongPointQueueName + " message retry exception!",
					ex);
		}
	}

	public void monitorPublisher(final Long publisherId) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (!monitorPublisherList.contains(publisherId)) {
					MonitorStrongPointMessage messgeObj = new MonitorStrongPointMessage();
					messgeObj.setPublisherId(publisherId);
					producer.sendMessage(RabbitmqConfiguration.monitorStrongPointQueueName, messgeObj);
					monitorPublisherList.add(publisherId);
				}
			}
		}).start();
	}

}
