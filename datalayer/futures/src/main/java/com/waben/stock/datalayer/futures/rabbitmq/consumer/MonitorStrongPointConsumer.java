package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.business.CapitalFlowBusiness;
import com.waben.stock.datalayer.futures.entity.*;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorStrongPointMessage;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesOvernightRecordDao;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.enums.*;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;
import com.waben.stock.interfaces.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@RabbitListener(queues = {
		RabbitmqConfiguration.monitorStrongPointQueueName }, containerFactory = "monitorStrongPointContainerFactory")
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
	private FuturesCurrencyRateService rateService;

	@Autowired
	private QuoteContainer quoteContainer;

	@Autowired
	private FuturesOvernightRecordDao recordDao;

	@Autowired
	private CapitalFlowBusiness flowBusiness;

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
		if (messgeObj.getConsumeCount() == 0) {
			// 第一次消费消息，输出日志
			logger.info("第一次消费监控强平点消息:{}", message);
		}
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
					List<FuturesContractOrder> overnightOrderList = triggerOvernightOrderList(orderList);
					if (isEnoughOvernight(overnightOrderList, account)) {
						// 隔夜
						overnight(overnightOrderList);
					} else {
						doUnwind(overnightOrderList);
					}
				}
			} else {
				isNeedRetry = false;
			}
			if (isNeedRetry) {
				retry(messgeObj);
			} else if (messgeObj.getConsumeCount() < 10) {
				retry(messgeObj);
			} else {
				monitorPublisherList.remove(messgeObj.getPublisherId());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			retry(messgeObj);
		}
	}

	private void doUnwind(List<FuturesContractOrder> orderList) {
		for (FuturesContractOrder order : orderList) {
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				logger.info("隔夜强平订单日志{}", order.getId());
			}
			if (orderService.isTradeTime(order.getContract().getCommodity().getTimeZoneGap(), order.getContract(),
					FuturesTradeActionType.CLOSE)) {
				if (order.getBuyUpQuantity().compareTo(BigDecimal.ZERO) > 0) {
					orderService.doUnwind(order.getContract(), order, FuturesOrderType.BuyUp, order.getBuyUpQuantity(),
							FuturesTradePriceType.MKT, null, order.getPublisherId(), FuturesWindControlType.DayUnwind,
							false, false, null);
				}
				if (order.getBuyFallQuantity().compareTo(BigDecimal.ZERO) > 0) {
					orderService.doUnwind(order.getContract(), order, FuturesOrderType.BuyFall,
							order.getBuyFallQuantity(), FuturesTradePriceType.MKT, null, order.getPublisherId(),
							FuturesWindControlType.DayUnwind, false, false, null);
				}
			}
		}
	}

	/**
	 * 判断是否足够过夜
	 *
	 * <p>
	 * 计算公式：账户余额 +浮动盈亏+交易保证金+隔夜手续费>=隔夜保证金
	 * </p>
	 *
	 * @param orderList
	 *            订单列表
	 * @param account
	 *            资金账户
	 * @return 是否足够过夜
	 */
	private boolean isEnoughOvernight(List<FuturesContractOrder> orderList, CapitalAccountDto account) {
		BigDecimal totalTradeReserveFund = BigDecimal.ZERO;
		BigDecimal totalOvernightDeferredFee = BigDecimal.ZERO;
		BigDecimal totalOvernightReserveFund = BigDecimal.ZERO;
		for (FuturesContractOrder order : orderList) {
			if (order.getContract().getCommodity().getOvernightPerUnitReserveFund() == null) {
				continue;
			}
			// 计算交易保证金
			totalTradeReserveFund = totalTradeReserveFund.add(order.getReserveFund());
			// 隔夜保证金=单边最大*每笔隔夜保证金
			BigDecimal quantity = order.getBuyUpQuantity().compareTo(order.getBuyFallQuantity()) > 0
					? order.getBuyUpQuantity() : order.getBuyFallQuantity();
			totalOvernightReserveFund = totalOvernightReserveFund
					.add(quantity.multiply(order.getContract().getCommodity().getOvernightPerUnitReserveFund()));
			// 隔夜递延费
			totalOvernightDeferredFee = totalOvernightDeferredFee
					.add(order.getBuyUpCanUnwindQuantity().add(order.getBuyFallCanUnwindQuantity())
							.multiply(order.getContract().getCommodity().getOvernightPerUnitDeferredFee()));
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				logger.info("隔夜订单日志{}，totalTradeReserveFund:{}，totalOvernightDeferredFee{}", order.getId(), totalTradeReserveFund,
						totalOvernightDeferredFee);
			}
		}
		// 隔夜保证金小于交易保证金
		if (totalOvernightReserveFund.compareTo(totalTradeReserveFund) < 0) {
			return true;
		}
		// 账号余额+交易保证金>=隔夜保证金+隔夜手续费
		if (account.getAvailableBalance().add(totalTradeReserveFund)
				.compareTo(totalOvernightReserveFund.add(totalOvernightDeferredFee)) >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * 隔夜
	 *
	 * @param orderList
	 *            订单
	 * @return 订单
	 */
	@Transactional
	public boolean overnight(List<FuturesContractOrder> orderList) {
		for (FuturesContractOrder order : orderList) {
			if (order.getContract().getCommodity().getOvernightPerUnitReserveFund() == null) {
				continue;
			}
			// 保存隔夜记录
			FuturesOvernightRecord overnightRecord = new FuturesOvernightRecord();
			BigDecimal overnightDeferredFee = order.getBuyUpCanUnwindQuantity().add(order.getBuyFallCanUnwindQuantity())
					.multiply(order.getContract().getCommodity().getOvernightPerUnitDeferredFee());
			overnightRecord.setOrder(order);
			overnightRecord.setOvernightDeferredFee(overnightDeferredFee);
			BigDecimal quantity = order.getBuyUpQuantity().compareTo(order.getBuyFallQuantity()) > 0
					? order.getBuyUpQuantity() : order.getBuyFallQuantity();
			// 隔夜保证金
			BigDecimal overnightReserveFund = quantity
					.multiply(order.getContract().getCommodity().getOvernightPerUnitReserveFund());
			// 冻结金额=隔夜保证金-保证金
			BigDecimal frozenDeposit = overnightReserveFund.compareTo(order.getReserveFund()) < 0 ? BigDecimal.ZERO
					: overnightReserveFund.subtract(order.getReserveFund());
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				logger.info("隔夜订单日志{}，overnightReserveFund:{}，frozenDeposit{}", order.getId(), overnightReserveFund,
						frozenDeposit);
			}
			overnightRecord.setOvernightReserveFund(overnightReserveFund);
			overnightRecord.setPublisherId(order.getPublisherId());
			overnightRecord.setReduceTime(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(
					retriveExchangeTime(new Date(), order.getContract().getCommodity().getExchange().getTimeZoneGap()));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			overnightRecord.setDeferredTime(cal.getTime());
			overnightRecord = recordDao.create(overnightRecord);
			// 修改订单状态
			order.setUpdateTime(new Date());
			contractOrderDao.update(order);
			if (frozenDeposit.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}
			// 扣除递延费，冻结保证金
			try {
				accountBusiness.futuresOrderOvernight(order.getPublisherId(), overnightRecord.getId(),
						overnightDeferredFee, frozenDeposit);
			} catch (ServiceException e) {
				if (ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION.equals(e.getType())) {
					// step 1.1 : 余额不足，强制平仓
					recordDao.delete(overnightRecord.getId());
					doUnwind(orderList);
					return false;
				} else {
					List<CapitalFlowDto> list = flowBusiness.fetchByExtendTypeAndExtendId(
							CapitalFlowExtendType.FUTURESOVERNIGHTRECORD, overnightRecord.getId());
				}
			}
		}
		return true;
	}

	/**
	 * 获取触发隔夜的订单
	 *
	 * @param orderList
	 *            订单
	 * @return 是否触发隔夜
	 */
	private List<FuturesContractOrder> triggerOvernightOrderList(List<FuturesContractOrder> orderList) {
		List<FuturesContractOrder> result = new ArrayList<>();
		SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		for (FuturesContractOrder order : orderList) {
			String overnightTimeGroup = overnightTimeMap().get(order.getContract().getCommodityId());
			if (overnightTimeGroup != null) {
				// 获取时差、隔夜时间、交易所时间
				String[] group = overnightTimeGroup.split("-");
				Integer timeZoneGap = Integer.parseInt(group[0]);
				String overnightTime = group[1];
				FuturesOvernightRecord record = overnightService.findNewestOvernightRecord(order);
				Date nowExchangeTime = orderService.retriveExchangeTime(now, timeZoneGap);
				String nowStr = daySdf.format(nowExchangeTime);
				// 判断是否有今天的隔夜记录
				if (!(record != null && nowStr.equals(daySdf.format(record.getDeferredTime())))) {
					FuturesContract contract = order.getContract();
					try {
						// 判断是否达到隔夜时间，隔夜时间~隔夜时间+1分钟
						Date beginTime = fullSdf.parse(nowStr + " " + overnightTime);
						Date endTime = new Date(beginTime.getTime() + 1 * 60 * 1000);
						if (nowExchangeTime.getTime() >= beginTime.getTime()
								&& nowExchangeTime.getTime() < endTime.getTime()) {
							result.add(order);
						}
					} catch (ParseException e) {
						logger.error("期货品种" + contract.getCommodity().getSymbol() + "隔夜时间格式错误?" + overnightTime);
					}
				}
			}
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				logger.info("隔夜队列{}，result:{}，frozenDeposit{}", order.getId(), order);
			}
		}
		return result;
	}

	/**
	 * 获取隔夜时间Map
	 *
	 * <p>
	 * key为品种ID;value格式为“时差-隔夜时间”，如“12-16:55:00”。
	 * </p>
	 *
	 * @return 隔夜时间Map
	 */
	private Map<Long, String> overnightTimeMap() {
		Map<Long, String> result = new HashMap<Long, String>();
		List<FuturesCommodity> commodityList = commodityDao.list();
		for (FuturesCommodity commodity : commodityList) {
			Integer timeZoneGap = commodity.getExchange().getTimeZoneGap();
			String overnightTime = commodity.getOvernightTime();
			if (!StringUtil.isEmpty(overnightTime)) {
				result.put(commodity.getId(), getOvernightTimeMapValue(timeZoneGap, overnightTime.trim()));
			}
		}
		return result;
	}

	private String getOvernightTimeMapValue(Integer timeZoneGap, String overnightTime) {
		return timeZoneGap + "-" + overnightTime;
	}

	/**
	 * 获取交易所的对应时间
	 *
	 * @param localTime
	 *            日期
	 * @param timeZoneGap
	 *            和交易所的时差
	 * @return 交易所的对应时间
	 */
	public Date retriveExchangeTime(Date localTime, Integer timeZoneGap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.HOUR_OF_DAY, timeZoneGap * -1);
		return cal.getTime();
	}

	private BigDecimal computeFloatProfitOrLoss(FuturesContractOrder contractOrder) {
		Long publisherId = contractOrder.getPublisherId();
		Long contractId = contractOrder.getContract().getId();
		BigDecimal floatProfitOrLoss = BigDecimal.ZERO;
		FuturesCommodity commotidy = contractOrder.getContract().getCommodity();
		FuturesContractMarket market = quoteContainer.getQuote(commotidy.getSymbol(),
				contractOrder.getContract().getContractNo());
		FuturesCurrencyRate rate = rateService.findByCurrency(commotidy.getCurrency());
		BigDecimal buyUpCanUnwind = contractOrder.getBuyUpCanUnwindQuantity();
		BigDecimal buyFallCanUnwind = contractOrder.getBuyFallCanUnwindQuantity();
		if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0
				&& market.getLastPrice().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal buyUpOpenAvgFillPrice = orderService.getOpenAvgFillPrice(publisherId, contractId,
					FuturesOrderType.BuyUp.getIndex());
			floatProfitOrLoss = floatProfitOrLoss
					.add(orderService.computeProfitOrLoss(FuturesOrderType.BuyUp, buyUpCanUnwind, buyUpOpenAvgFillPrice,
							market.getLastPrice(), commotidy.getMinWave(), commotidy.getPerWaveMoney()).multiply(rate.getRate()));
		}
		if (buyFallCanUnwind != null && buyFallCanUnwind.compareTo(BigDecimal.ZERO) > 0
				&& market.getLastPrice().compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal buyFallOpenAvgFillPrice = orderService.getOpenAvgFillPrice(publisherId, contractId,
					FuturesOrderType.BuyFall.getIndex());
			floatProfitOrLoss = floatProfitOrLoss.add(orderService.computeProfitOrLoss(FuturesOrderType.BuyFall,
					buyFallCanUnwind, buyFallOpenAvgFillPrice, market.getLastPrice(), commotidy.getMinWave(),
					commotidy.getPerWaveMoney()).multiply(rate.getRate()));
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

		List<FuturesContractOrderTemp> tempOrderList = new ArrayList<>();
		for (FuturesContractOrder order : orderList) {
			BigDecimal strongMoney = orderService.getStrongMoney(order);
			BigDecimal floatProfitOrLoss = computeFloatProfitOrLoss(order);
			order.setStrongMoney(strongMoney);
			order.setFloatProfitOrLoss(floatProfitOrLoss);
			if (order.getIsNeedLog() != null && order.getIsNeedLog()) {
				logger.info("监控强平订单日志{}，strongMoney:{}，floatProfitOrLoss{}", order.getId(), strongMoney,
						floatProfitOrLoss);
			}
			// 计算强平金额
			totalStrong = totalStrong.add(strongMoney);
			// 计算浮动盈亏
			totalProfitOrLoss = totalProfitOrLoss.add(floatProfitOrLoss);

			FuturesContractOrderTemp tempOrder = CopyBeanUtils.copyBeanProperties(FuturesContractOrderTemp.class, order,
					false);
			tempOrder.setContract(order.getContract());
			tempOrder.setOriginOrder(order);
			tempOrderList.add(tempOrder);
		}

		if (totalProfitOrLoss.compareTo(BigDecimal.ZERO) < 0
				&& totalProfitOrLoss.abs().compareTo(account.getAvailableBalance()) > 0) {
			// 根据盈亏值进行排序
			Collections.sort(tempOrderList, new FuturesContractOrderComparator());
			// 账户余额已经亏损完，计算超出的部分
			BigDecimal loss = totalProfitOrLoss.add(account.getAvailableBalance());
			for (FuturesContractOrderTemp order : tempOrderList) {
				FuturesContract contract = order.getContract();
				if (orderService.isTradeTime(contract.getCommodity().getExchange().getTimeZoneGap(), contract,
						FuturesTradeActionType.CLOSE)) {
					BigDecimal strongMoney = order.getStrongMoney();
					if (loss.abs().compareTo(strongMoney) >= 0) {
						// 强平
						BigDecimal buyUpQuantity = order.getBuyUpCanUnwindQuantity();
						BigDecimal buyFallQuantity = order.getBuyFallCanUnwindQuantity();
						if (buyUpQuantity.compareTo(BigDecimal.ZERO) > 0) {
							orderService.doUnwind(contract, order.getOriginOrder(), FuturesOrderType.BuyUp,
									buyUpQuantity, FuturesTradePriceType.MKT, null, order.getPublisherId(),
									FuturesWindControlType.ReachStrongPoint, false, false, null);
						}
						if (buyFallQuantity.compareTo(BigDecimal.ZERO) > 0) {
							orderService.doUnwind(contract, order.getOriginOrder(), FuturesOrderType.BuyFall,
									buyFallQuantity, FuturesTradePriceType.MKT, null, order.getPublisherId(),
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

	private class FuturesContractOrderComparator implements Comparator<FuturesContractOrderTemp> {
		@Override
		public int compare(FuturesContractOrderTemp o1, FuturesContractOrderTemp o2) {
			return o1.getFloatProfitOrLoss().compareTo(o2.getFloatProfitOrLoss());
		}
	}

}
