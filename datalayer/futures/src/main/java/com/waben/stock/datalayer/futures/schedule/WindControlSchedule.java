package com.waben.stock.datalayer.futures.schedule;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.business.CapitalFlowBusiness;
import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.service.FuturesContractOrderService;
import com.waben.stock.datalayer.futures.service.FuturesCurrencyRateService;
import com.waben.stock.datalayer.futures.service.FuturesOrderService;
import com.waben.stock.datalayer.futures.service.FuturesOvernightRecordService;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.enums.*;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import com.waben.stock.interfaces.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 风控作业
 * 
 * @author lma
 *
 */
@Component
public class WindControlSchedule {

	/**
	 * 监控间隔
	 * <p>
	 * 如果是工作日，每间隔10毫秒中获取持仓中的股票，判断持仓中的股票
	 * </p>
	 */
	public static final long Execute_Interval = 10;

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private CapitalFlowBusiness flowBusiness;

	@Autowired
	private ProfileBusiness profileBusiness;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private FuturesContractOrderService futuresContractOrderService;

	@PostConstruct
	public void initTask() {
		Timer timer = new Timer();
		timer.schedule(new WindControlTask(), Execute_Interval);
	}

	private class WindControlTask extends TimerTask {
		@Override
		public void run() {
			try {
				// step 1 : 获取所有持仓中的正式单
				List<FuturesContractOrder> content = retrivePositionContractOrders();
				if (RandomUtil.getRandomInt(100) % 51 == 0 && RandomUtil.getRandomInt(100) % 51 == 0) {
					logger.info("监控持仓中的正式单数量：" + content.size());
				}
				// step 2 : 遍历所有订单，判断是否达到风控平仓条件
				if (content != null && content.size() > 0) {
					for (FuturesContractOrder order : content) {
						FuturesContract contract = order.getContract();
						Integer timeZoneGap = contract.getCommodity().getTimeZoneGap();
						// step 3 : 是否触发退还隔夜保证金时间
						checkAndDoReturnOvernightReserveFund(order, timeZoneGap);
						// step 4 : 是否合约到期
						if (orderService.isTradeTime(timeZoneGap, contract, FuturesTradeActionType.CLOSE)
								&& isReachContractExpiration(timeZoneGap, contract)) {
							synchronized (getLockKey(contract.getId(), order.getPublisherId()).intern()) {
								BigDecimal buyUpQuantity = order.getBuyUpCanUnwindQuantity();
								BigDecimal buyFallQuantity = order.getBuyFallCanUnwindQuantity();
								// 合约到期，强平买涨订单
								if (buyUpQuantity.compareTo(BigDecimal.ZERO) > 0) {
									orderService.doUnwind(contract, order.getPublisherId(), FuturesOrderType.BuyUp, buyUpQuantity, FuturesTradePriceType.MKT,
											null, FuturesWindControlType.ReachContractExpiration, false, false, null);
								}
								// 合约到期，强平买跌订单
								if (buyFallQuantity.compareTo(BigDecimal.ZERO) > 0) {
									orderService.doUnwind(contract, order.getPublisherId(), FuturesOrderType.BuyFall, buyFallQuantity,
											FuturesTradePriceType.MKT, null, FuturesWindControlType.ReachContractExpiration, false,
											false, null);
								}
							}
							continue;
						}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				logger.error("监控持仓订单发生异常!", ex);
			} finally {
				initTask();
			}
		}

	}

	private String getLockKey(Long contractId, Long publisherId) {
		return contractId + "-" + publisherId;
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
	 * 判断是否达到止盈点
	 * 
	 * @param order
	 *            订单
	 * @param market
	 *            行情
	 * @return 否达到止盈点
	 */
	private boolean isReachProfitPoint(FuturesOrder order, FuturesContractMarket market) {
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal limitProfitPrice = computeLimitProfitPrice(order);
		if (orderType == FuturesOrderType.BuyUp) {
			BigDecimal lastPrice = orderService.computeMktAvgPrice(order.getCommoditySymbol(), order.getContractNo(),
					FuturesActionType.SELL, order.getTotalQuantity()).getAvgFillPrice();
			if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0 && limitProfitPrice != null
					&& lastPrice.compareTo(limitProfitPrice) >= 0) {
				if (order.getState() == FuturesOrderState.Position) {
					// orderService.unwindOrder(order.getId(), lastPrice, FuturesWindControlType.ReachProfitPoint);
					// TODO
				}
				return true;
			}
		} else {
			BigDecimal lastPrice = orderService.computeMktAvgPrice(order.getCommoditySymbol(), order.getContractNo(),
					FuturesActionType.BUY, order.getTotalQuantity()).getAvgFillPrice();
			if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0 && limitProfitPrice != null
					&& lastPrice.compareTo(limitProfitPrice) <= 0) {
				if (order.getState() == FuturesOrderState.Position) {
					// orderService.unwindOrder(order.getId(), lastPrice, FuturesWindControlType.ReachProfitPoint);
					// TODO
				}
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
//		FuturesOrderType orderType = order.getOrderType();
//		BigDecimal buyingPrice = order.getBuyingPrice();
//		// 用户设置
//		Integer limitLossType = order.getLimitLossType();
//		BigDecimal perUnitLimitLossAmount = order.getPerUnitLimitLossAmount();
//		// 波动设置
//		BigDecimal minWave = order.getContract().getCommodity().getMinWave();
//		BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
//		// 货币汇率
//		// FuturesCurrencyRate rate =
//		// rateService.findByCurrency(order.getCommodityCurrency());
//		if (buyingPrice != null) {
//			// 获取用户设置的止损价格
//			BigDecimal userSetNeedWavePrice = null;
//			if (limitLossType != null && limitLossType == 1 && perUnitLimitLossAmount != null) {
//				// type为行情价格
//				if (orderType == FuturesOrderType.BuyUp && perUnitLimitLossAmount.compareTo(buyingPrice) < 0) {
//					userSetNeedWavePrice = buyingPrice.subtract(perUnitLimitLossAmount);
//				} else if (orderType == FuturesOrderType.BuyFall && perUnitLimitLossAmount.compareTo(buyingPrice) > 0) {
//					userSetNeedWavePrice = perUnitLimitLossAmount.subtract(buyingPrice);
//				}
//			} else if (limitLossType != null && limitLossType == 2 && perUnitLimitLossAmount != null) {
//				// type为每手亏损剩余到金额
//				userSetNeedWavePrice = perUnitLimitLossAmount.divide(perWaveMoney, 2, RoundingMode.DOWN)
//						.multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN);
//			}
//			// 获取最终需要波动的价格
//			BigDecimal lastNeedWavePrice = userSetNeedWavePrice;
//			if (lastNeedWavePrice != null) {
//				if (orderType == FuturesOrderType.BuyUp) {
//					return buyingPrice.subtract(lastNeedWavePrice.abs());
//				} else if (orderType == FuturesOrderType.BuyFall) {
//					return buyingPrice.add(lastNeedWavePrice.abs());
//				}
//			}
//		}
		return null;
	}

	/**
	 * 计算订单强平价格
	 * 
	 * @param order
	 *            订单
	 * @return 强平价格
	 */
	private BigDecimal computeStrongPrice(FuturesOrder order) {
//		FuturesOrderType orderType = order.getOrderType();
//		BigDecimal buyingPrice = order.getBuyingPrice();
//		// 合约设置
//		Integer unwindPointType = order.getUnwindPointType();
//		BigDecimal perUnitUnwindPoint = order.getPerUnitUnwindPoint();
//		// 波动设置
//		BigDecimal minWave = order.getContract().getCommodity().getMinWave();
//		BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
//		// 货币汇率
//		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
//		if (buyingPrice != null) {
//			// 获取合约设置的止损价格
//			BigDecimal contractSetNeedWavePrice = null;
//			if (unwindPointType != null && perUnitUnwindPoint != null && unwindPointType == 1) {
//				if (perUnitUnwindPoint != null && perUnitUnwindPoint.compareTo(new BigDecimal(100)) < 0
//						&& perUnitUnwindPoint.compareTo(new BigDecimal(0)) > 0) {
//					contractSetNeedWavePrice = (order.getReserveFund().divide(order.getTotalQuantity())
//							.multiply(new BigDecimal(100).subtract(perUnitUnwindPoint).divide(new BigDecimal(100)))
//							.divide(rate.getRate(), 2, RoundingMode.DOWN).divide(perWaveMoney, 2, RoundingMode.DOWN)
//							.multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN));
//				}
//
//			} else if (unwindPointType != null && perUnitUnwindPoint != null && unwindPointType == 2) {
//				if (perUnitUnwindPoint != null && perUnitUnwindPoint.compareTo(BigDecimal.ZERO) >= 0
//						&& perUnitUnwindPoint.compareTo(new BigDecimal(0)) > 0) {
//					contractSetNeedWavePrice = (order.getReserveFund().divide(order.getTotalQuantity())
//							.subtract(perUnitUnwindPoint).divide(rate.getRate(), 2, RoundingMode.DOWN)
//							.divide(perWaveMoney, 2, RoundingMode.DOWN).multiply(minWave)
//							.setScale(minWave.scale(), RoundingMode.DOWN));
//				}
//			}
//			// 获取最终需要波动的价格
//			BigDecimal lastNeedWavePrice = contractSetNeedWavePrice;
//			if (lastNeedWavePrice != null) {
//				if (orderType == FuturesOrderType.BuyUp) {
//					return buyingPrice.subtract(lastNeedWavePrice.abs());
//				} else if (orderType == FuturesOrderType.BuyFall) {
//					return buyingPrice.add(lastNeedWavePrice.abs());
//				}
//			}
//		}
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
//		FuturesOrderType orderType = order.getOrderType();
//		BigDecimal buyingPrice = order.getBuyingPrice();
//		// 用户设置
//		Integer limitProfitType = order.getLimitProfitType();
//		BigDecimal perUnitLimitProfitAmount = order.getPerUnitLimitProfitAmount();
//		// 波动设置
//		BigDecimal minWave = order.getContract().getCommodity().getMinWave();
//		BigDecimal perWaveMoney = order.getContract().getCommodity().getPerWaveMoney();
//		// 货币汇率
//		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
//		if (buyingPrice != null && perUnitLimitProfitAmount != null) {
//			if (limitProfitType != null && limitProfitType == 1 && perUnitLimitProfitAmount != null) {
//				// type为行情价格
//				if (orderType == FuturesOrderType.BuyUp && perUnitLimitProfitAmount.compareTo(buyingPrice) > 0) {
//					return perUnitLimitProfitAmount;
//				} else if (orderType == FuturesOrderType.BuyFall
//						&& perUnitLimitProfitAmount.compareTo(buyingPrice) < 0) {
//					return perUnitLimitProfitAmount;
//				}
//			} else if (limitProfitType != null && limitProfitType == 2 && rate != null && rate.getRate() != null
//					&& perUnitLimitProfitAmount != null) {
//				// type为每手盈利金额
//				BigDecimal needWavePrice = perUnitLimitProfitAmount.divide(perWaveMoney, 2, RoundingMode.DOWN)
//						.multiply(minWave).setScale(minWave.scale(), RoundingMode.DOWN);
//				if (orderType == FuturesOrderType.BuyUp) {
//					return buyingPrice.add(needWavePrice);
//				} else if (orderType == FuturesOrderType.BuyFall) {
//					return buyingPrice.subtract(needWavePrice);
//				}
//			}
//		}
		return null;
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
	private boolean isReachLossPoint(FuturesOrder order, FuturesContractMarket market) {
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal limitLossPrice = computeLimitLossPrice(order);
		if (orderType == FuturesOrderType.BuyUp) {
			BigDecimal lastPrice = orderService.computeMktAvgPrice(order.getCommoditySymbol(), order.getContractNo(),
					FuturesActionType.SELL, order.getTotalQuantity()).getAvgFillPrice();
			if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0 && limitLossPrice != null
					&& lastPrice.compareTo(limitLossPrice) <= 0) {
				if (order.getState() == FuturesOrderState.Position) {
					// orderService.unwindOrder(order.getId(), lastPrice, FuturesWindControlType.ReachLossPoint);
					// TODO
				}
				return true;
			}
		} else {
			BigDecimal lastPrice = orderService.computeMktAvgPrice(order.getCommoditySymbol(), order.getContractNo(),
					FuturesActionType.BUY, order.getTotalQuantity()).getAvgFillPrice();
			if (lastPrice != null && lastPrice.compareTo(BigDecimal.ZERO) > 0 && limitLossPrice != null
					&& lastPrice.compareTo(limitLossPrice) >= 0) {
				if (order.getState() == FuturesOrderState.Position) {
					// orderService.unwindOrder(order.getId(), lastPrice, FuturesWindControlType.ReachLossPoint);
					// TODO
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否达到强平点
	 * 
	 * @param order
	 *            订单
	 * @param market
	 *            行情
	 * @return 是否达到强平点
	 */
	@SuppressWarnings("unused")
	private boolean isReachStongPoint(FuturesOrder order, FuturesContractMarket market) {
		BigDecimal lastPrice = market.getLastPrice();
		FuturesOrderType orderType = order.getOrderType();
		BigDecimal limitLossPrice = computeStrongPrice(order);
		if (orderType == FuturesOrderType.BuyUp) {
			if (lastPrice != null && limitLossPrice != null && lastPrice.compareTo(limitLossPrice) <= 0) {
				return true;
			}
		} else {
			if (lastPrice != null && limitLossPrice != null && lastPrice.compareTo(limitLossPrice) >= 0) {
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
	private boolean isTriggerOvernight(FuturesContractOrder order, Integer timeZoneGap) {
		SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		FuturesOvernightRecord record = overnightService.findNewestOvernightRecord(order);
		Date now = new Date();
		Date nowExchangeTime = orderService.retriveExchangeTime(now, timeZoneGap);
		String nowStr = daySdf.format(nowExchangeTime);
		// 判断是否有今天的隔夜记录
		if (!(record != null && nowStr.equals(daySdf.format(record.getDeferredTime())))) {
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
				logger.error("期货品种" + contract.getCommodity().getSymbol() + "隔夜时间格式错误?" + overnightTime);
			}
		}
		return false;
	}

	/**
	 * 判断是否触发退还隔夜保证金
	 * 
	 * @param order
	 *            订单
	 * @return 是否触发隔夜
	 */
	private void checkAndDoReturnOvernightReserveFund(FuturesContractOrder order, Integer timeZoneGap) {
		SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 判断当前时候+30分钟是否为交易时间段
		Date now = new Date();
		// 获取退还隔夜保证金的时间
		String returnOvernightReserveFundTime = order.getContract().getCommodity().getReturnOvernightReserveFundTime();
		if (returnOvernightReserveFundTime == null) {
			return;
		}
		Date nowExchangeTime = orderService.retriveExchangeTime(now, timeZoneGap);
		String nowStr = daySdf.format(nowExchangeTime);
		try {
			// 判断是否到达退还隔夜保证金时间，退还隔夜保证金时间~退还隔夜保证金时间+1分钟
			Date beginTime = fullSdf.parse(nowStr + " " + returnOvernightReserveFundTime);
			Date endTime = new Date(beginTime.getTime() + 1 * 60 * 1000);
			if (nowExchangeTime.getTime() >= beginTime.getTime() && nowExchangeTime.getTime() < endTime.getTime()) {
				// 如果到达退还隔夜保证金时间
				FuturesOvernightRecord record = overnightService.findNewestOvernightRecord(order);
				if (record != null) {
					List<CapitalFlowDto> flowList = flowBusiness
							.fetchByExtendTypeAndExtendId(CapitalFlowExtendType.FUTURESOVERNIGHTRECORD, record.getId());
					if (flowList != null && flowList.size() > 0) {
						boolean hasOvernightReserveFund = false;
						boolean hasReturnOvernightReserveFund = false;
						BigDecimal reserveFund = BigDecimal.ZERO;
						for (CapitalFlowDto flow : flowList) {
							if (flow.getType() == CapitalFlowType.FuturesOvernightReserveFund) {
								hasOvernightReserveFund = true;
								reserveFund = flow.getAmount();
							}
							if (flow.getType() == CapitalFlowType.FuturesReturnOvernightReserveFund) {
								hasReturnOvernightReserveFund = true;
							}
						}
						if (hasOvernightReserveFund && !hasReturnOvernightReserveFund) {
							// 退还隔夜保证金
							accountBusiness.futuresReturnOvernightReserveFund(order.getPublisherId(), record.getId(),
									reserveFund);
						}
					}
				}
			}
		} catch (ParseException e) {
			logger.error("期货品种" + order.getContract().getCommodity().getSymbol() + "隔夜时间格式错误?" + returnOvernightReserveFundTime);
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

}