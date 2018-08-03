package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.datalayer.futures.rabbitmq.RabbitmqProducer;
import com.waben.stock.datalayer.futures.rabbitmq.message.MonitorStrongPointMessage;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
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
//		List<FuturesOrder> orderList = retrivePositionOrders();
//		for (FuturesOrder order : orderList) {
//			Long publisherId = order.getPublisherId();
//			if (!monitorPublisherList.contains(publisherId)) {
//				monitorPublisherList.add(publisherId);
//			}
//		}
//
//		for (Long publisherId : monitorPublisherList) {
//			MonitorStrongPointMessage messgeObj = new MonitorStrongPointMessage();
//			messgeObj.setPublisherId(publisherId);
//			producer.sendMessage(RabbitmqConfiguration.monitorStrongPointQueueName, messgeObj);
//		}
	}

	// @RabbitHandler
	public void handlerMessage(String message) {
		if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
			logger.info("监控用户订单:{}", message);
		}
		MonitorStrongPointMessage messgeObj = JacksonUtil.decode(message, MonitorStrongPointMessage.class);
//		try {
//			Long publisherId = messgeObj.getPublisherId();
//			// step 1 : 获取资金账号
//			CapitalAccountDto account = accountBusiness.fetchByPublisherId(publisherId);
//			// step 2 : 获取持仓订单
//			List<FuturesOrder> orderList = retrivePublisherPositionOrders(publisherId);
//			if (orderList != null && orderList.size() > 0) {
//				// step 3 : 判断是否达到强平
//				if (isReachStongPoint(orderList, account)) {
//					for (FuturesOrder order : orderList) {
//						strongUnwind(order, FuturesWindControlType.ReachStrongPoint);
//					}
//				} else {
//					// step 4 : 判断是否触发隔夜，是否足够过夜
//					List<FuturesOrder> overnightOrderList = triggerOvernightOrderList(orderList);
//					if (overnightOrderList != null && overnightOrderList.size() > 0) {
//						if (isEnoughOvernight(orderList, account)) {
//							// step 4.1 : 扣除递延费
//							for (FuturesOrder order : overnightOrderList) {
//								// orderService.overnight(order,
//								// order.getContract().getCommodity().getExchange().getTimeZoneGap());
//								// TODO
//							}
//						} else {
//							// step 4.2 : 不满足隔夜条件，强平
//							for (FuturesOrder order : overnightOrderList) {
//								strongUnwind(order, FuturesWindControlType.DayUnwind);
//							}
//						}
//					}
//				}
//				retry(messgeObj);
//			} else {
//				// 从监控队列中移除
//				monitorPublisherList.remove(publisherId);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			retry(messgeObj);
//		}
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
	private boolean isReachStongPoint(List<FuturesOrder> orderList, CapitalAccountDto account) {
		BigDecimal totalStrong = BigDecimal.ZERO;
		BigDecimal totalProfitOrLoss = BigDecimal.ZERO;
		for (FuturesOrder order : orderList) {
			// 计算强平金额
			totalStrong = totalStrong.add(orderService.getStrongMoney(order));
			// 计算浮动盈亏
			// totalProfitOrLoss =
			// totalProfitOrLoss.add(orderService.getProfitOrLoss(order,
			// quoteContainer.getLastPrice(order.getCommoditySymbol(),
			// order.getContractNo())));
		}
		if (totalProfitOrLoss.compareTo(BigDecimal.ZERO) < 0
				&& account.getAvailableBalance().add(totalStrong).compareTo(totalProfitOrLoss.abs()) <= 0) {
			return true;
		} else {
			return false;
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
