package com.waben.stock.datalayer.futures.schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import com.waben.stock.datalayer.futures.entity.*;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import com.waben.stock.datalayer.futures.service.FuturesTradeEntrustService;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.RandomUtil;

/**
 * 买入委托中订单监控作业
 * 
 * <p>
 * 达到合约强平日或者隔夜时间余额不足，买入委托订单要自动取消，金额退还用户
 * </p>
 * 
 * @author lma
 *
 */
@Component
public class BuyingEntrustOrderMonitorSchedule {

	/**
	 * 监控间隔
	 * <p>
	 * 如果是工作日，每间隔1秒中获取持仓中的股票，判断持仓中的股票
	 * </p>
	 */
	public static final long Execute_Interval = 10;

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private FuturesTradeEntrustDao entrustDao;

	@Autowired
	private FuturesTradeEntrustService entrustService;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	@PostConstruct
	public void initTask() {
		Timer timer = new Timer();
		timer.schedule(new WindControlTask(), Execute_Interval);
	}

	private class WindControlTask extends TimerTask {
		@Override
		public void run() {
			try {
				// step 1 : 获取所有买入委托中的订单
				List<FuturesTradeEntrust> content = retriveQueueingEntrustOrders();
				if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
					logger.info("监控买入委托中的订单数量：" + content.size());
				}
				// step 2 : 遍历所有订单，判断是否达到风控平仓条件
				if (content != null && content.size() > 0) {
					for (FuturesTradeEntrust order : content) {
						Integer timeZoneGap = order.getContract().getCommodity().getTimeZoneGap();
						FuturesContract contract = order.getContract();
						// step 3 : 是否合约到期
						if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.OPEN)
								&& isReachContractExpiration(timeZoneGap, contract)) {
							entrustService.cancelEntrust(order.getId(), order.getPublisherId());
							continue;
						}
						// step 4 : 是否触发隔夜时间
						if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.OPEN) && isTriggerOvernight(order, timeZoneGap)) {
							entrustService.cancelEntrust(order.getId(), order.getPublisherId());
							continue;
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("监控买入委托中的订单发生异常!", ex);
			} finally {
				initTask();
			}
		}

	}

	/**
	 * 判断是否合约到期
	 * 
	 * @param order
	 *            订单
	 * @return 是否合约到期
	 */
	private boolean isReachContractExpiration(Integer timeZoneGap, FuturesContract contract) {
		if (contract != null) {
			Date exchangeTime = orderService.retriveExchangeTime(timeZoneGap);
			Date forceUnwindDate = contract.getForceUnwindDate();
			if (forceUnwindDate != null && exchangeTime.getTime() >= forceUnwindDate.getTime()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否触发隔夜
	 * 
	 * @param order
	 *            订单
	 * @return 是否触发隔夜
	 */
	private boolean isTriggerOvernight(FuturesTradeEntrust order, Integer timeZoneGap) {
		SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		Date nowExchangeTime = orderService.retriveExchangeTime(now, timeZoneGap);
		String nowStr = daySdf.format(nowExchangeTime);
		FuturesContract contract = order.getContract();
		String overnightTime = contract.getCommodity().getOvernightTime();
		try {
			// 判断是否达到隔夜时间，隔夜时间~隔夜时间+1分钟
			Date beginTime = fullSdf.parse(nowStr + " " + overnightTime);
			Date endTime = new Date(beginTime.getTime() + 1 * 60 * 1000);
			if (nowExchangeTime.getTime() >= beginTime.getTime() && nowExchangeTime.getTime() < endTime.getTime()) {
				return true;
			}
		} catch (ParseException e) {
			// logger.error("期货品种" + contract.getCommodity().getSymbol() + "隔夜时间格式错误?" + overnightTime);
			return false;
		}
		return false;
	}

	/**
	 * 获取所有买入委托中的订单
	 * 
	 * @return 买入委托中的订单
	 */
	private List<FuturesTradeEntrust> retriveQueueingEntrustOrders() {
		return entrustDao.retrieveByTradeActionTypeAndState(FuturesTradeActionType.OPEN, FuturesTradeEntrustState.Queuing);
	}

}