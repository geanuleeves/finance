package com.waben.stock.datalayer.buyrecord.schedule;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.waben.stock.datalayer.buyrecord.business.HolidayBusiness;
import com.waben.stock.datalayer.buyrecord.business.OrganizationSettlementBusiness;
import com.waben.stock.datalayer.buyrecord.entity.BuyRecord;
import com.waben.stock.datalayer.buyrecord.entity.DeferredRecord;
import com.waben.stock.datalayer.buyrecord.service.BuyRecordService;
import com.waben.stock.interfaces.commonapi.retrivestock.RetriveStockOverHttp;
import com.waben.stock.interfaces.commonapi.retrivestock.bean.StockMarket;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.enums.BuyRecordState;
import com.waben.stock.interfaces.enums.WindControlType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.query.BuyRecordQuery;

/**
 * 风控作业
 * 
 * @author luomengan
 *
 */
@Component
public class WindControlSchedule {

	/**
	 * 监控间隔
	 * <p>
	 * 如果是工作日，每间隔5秒中获取持仓中的股票，判断持仓中的股票
	 * </p>
	 */
	public static final long Execute_Interval = 3 * 1000;

	private Logger logger = LoggerFactory.getLogger(getClass());

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	private SimpleDateFormat timeSdf = new SimpleDateFormat("HH:mm:ss");

	@Autowired
	private HolidayBusiness holidayBusiness;

	@Autowired
	private BuyRecordService service;

	@Autowired
	private OrganizationSettlementBusiness orgSettlementBusiness;

	@Autowired
	private RestTemplate restTemplate;

	/**
	 * 递延费扣除时间
	 */
	public static final String deferredDeductionTime = "14:00:00";
	/**
	 * 递延费不足强平时间
	 */
	public static final String deferredForceTime = "14:50:00";

	@PostConstruct
	public void initTask() {
		// 计算下一次执行的时间，判断是否在交易时间日，如果是交易时间日则每5秒钟执行作业，否则下一个工作日9点开始执行作业
		Date now = new Date();
		boolean isTradeDay = holidayBusiness.isTradeDay(now);
		if (isTradeDay) {
			// 当前是工作日
			String nowStr = sdf.format(now);
			String nowFullStr = fullSdf.format(now);
			if (nowFullStr.compareTo(nowStr + " 09:30:00") < 0) {
				Timer timer = new Timer();
				timer.schedule(new WindControlTask(), getAmExecuteTime(now, false));
			} else if (nowFullStr.compareTo(nowStr + " 15:00:00") > 0) {
				Timer timer = new Timer();
				timer.schedule(new WindControlTask(), getAmExecuteTime(now, true));
			} else if (nowFullStr.compareTo(nowStr + " 11:30:00") > 0
					&& nowFullStr.compareTo(nowStr + " 13:00:00") < 0) {
				Timer timer = new Timer();
				timer.schedule(new WindControlTask(), getPmExecuteTime(now));
			} else {
				Timer timer = new Timer();
				timer.schedule(new WindControlTask(), Execute_Interval);
			}
		} else {
			// 当前是非工作日
			Timer timer = new Timer();
			timer.schedule(new WindControlTask(), getAmExecuteTime(now, true));
		}
	}

	private Date getAmExecuteTime(Date now, boolean isNextDay) {
		Date executeDay = now;
		if (isNextDay) {
			executeDay = holidayBusiness.getAfterTradeDate(new Date(), 1);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(executeDay);
		cal.set(Calendar.HOUR_OF_DAY, 9);
		cal.set(Calendar.MINUTE, 30);
		cal.set(Calendar.SECOND, 30);
		cal.set(Calendar.MILLISECOND, 0);
		Date nextExecuteTime = cal.getTime();
		logger.info("下一次执行风控作业的时间：" + fullSdf.format(nextExecuteTime));
		return nextExecuteTime;
	}

	private Date getPmExecuteTime(Date now) {
		Date executeDay = now;
		Calendar cal = Calendar.getInstance();
		cal.setTime(executeDay);
		cal.set(Calendar.HOUR_OF_DAY, 13);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 30);
		cal.set(Calendar.MILLISECOND, 0);
		Date nextExecuteTime = cal.getTime();
		logger.info("下一次执行风控作业的时间：" + fullSdf.format(nextExecuteTime));
		return nextExecuteTime;
	}

	private class WindControlTask extends TimerTask {
		@Override
		public void run() {
			try {
				logger.info("进入风控作业：" + fullSdf.format(new Date()));
				BuyRecordQuery query = new BuyRecordQuery();
				query.setStates(new BuyRecordState[] { BuyRecordState.HOLDPOSITION });
				query.setPage(0);
				query.setSize(Integer.MAX_VALUE);
				Page<BuyRecord> pages = service.pagesByQuery(query);
				List<BuyRecord> content = pages.getContent();
				if (content != null && content.size() > 0) {
					for (BuyRecord record : content) {
						try {
							Date now = new Date();
							String stockCode = record.getStockCode();
							// step 1 : 获取股票行情
							StockMarket market = RetriveStockOverHttp.singleStockMarket(restTemplate, stockCode);
							if (market == null || market.getLastPrice() == null
									|| market.getLastPrice().compareTo(new BigDecimal(0)) <= 0) {
								throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION,
										String.format("获取股票{}的最新行情失败!", stockCode));
							}
							if (!sdf.format(now).equals(market.getTradeDay())) {
								continue;
							}
							BigDecimal lastPrice = market.getLastPrice();
							// step 2 : 处理到期
							if (now.getTime() > record.getExpireTime().getTime()
									&& timeSdf.format(now).compareTo(deferredDeductionTime) >= 0) {
								boolean isExpire = true;
								// 扣除递延费
								try {
									DeferredRecord deferredRecord = service.deferred(record.getId());
									orgSettlementBusiness.strategySettlement(record.getPublisherId(), record.getId(),
											record.getTradeNo(), record.getStrategyTypeId(), BigDecimal.ZERO,
											deferredRecord.getFee());
									isExpire = false;
								} catch (Exception ex) {
									if (ex instanceof ServiceException) {
										ServiceException sex = (ServiceException) ex;
										if (sex.getType() == ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION) {
											isExpire = true;
										} else if (sex
												.getType() == ExceptionConstant.BUYRECORD_USERNOTDEFERRED_EXCEPTION) {
											isExpire = true;
										} else if (sex
												.getType() == ExceptionConstant.BUYRECORD_ALREADY_DEFERRED_EXCEPTION) {
											isExpire = false;
										}
									}
								}
								// 过期强制平仓
								if (isExpire && timeSdf.format(now).compareTo(deferredForceTime) > 0) {
									service.sellWithMarket(record.getId(), WindControlType.TRADINGEND, lastPrice);
									continue;
								}
							}
							// step 3 : 判断是否达到涨停价或者跌停价
							BigDecimal profitPosition = record.getProfitPosition();
							BigDecimal lossPosition = record.getLossPosition();
							String nowStr = sdf.format(now);
							String buyingTimeStr = sdf.format(record.getBuyingTime());
							if (nowStr.compareTo(buyingTimeStr) > 0 && lastPrice.compareTo(profitPosition) >= 0) {
								// 达到止盈点位
								service.sellWithMarket(record.getId(), WindControlType.REACHPROFITPOINT, lastPrice);
								continue;
							}
							if (nowStr.compareTo(buyingTimeStr) > 0 && lastPrice.compareTo(lossPosition) <= 0) {
								// 达到止损点位
								service.sellWithMarket(record.getId(), WindControlType.REACHLOSSPOINT, lastPrice);
								continue;
							}
						} catch (Exception ex) {
							logger.error("监控点买记录" + record.getId() + "发生异常", ex);
						}
					}
				}
			} finally {
				initTask();
			}
		}
	}

}
