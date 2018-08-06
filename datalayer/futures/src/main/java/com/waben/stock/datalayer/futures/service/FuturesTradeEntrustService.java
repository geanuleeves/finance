package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.business.OrganizationBusiness;
import com.waben.stock.datalayer.futures.business.OutsideMessageBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.MonitorStopLossOrProfitConsumer;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.MonitorStrongPointConsumer;
import com.waben.stock.datalayer.futures.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeActionDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import com.waben.stock.datalayer.futures.repository.impl.MethodDesc;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.enums.OutsideMessageType;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.message.OutsideMessage;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeEntrustQuery;
import com.waben.stock.interfaces.util.StringUtil;

/**
 * 交易委托
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesTradeEntrustService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DynamicQuerySqlDao sqlDao;

	@Autowired
	private FuturesTradeEntrustDao dao;

	@Autowired
	private FuturesTradeActionDao actionDao;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private FuturesOrderDao orderDao;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private OutsideMessageBusiness outsideMessageBusiness;

	@Autowired
	private FuturesTradeEntrustDao futuresTradeEntrustDao;

	@Autowired
	private OrganizationBusiness orgBusiness;

	@Autowired
	private MonitorStopLossOrProfitConsumer monitorStopLossOrProfit;

	@Autowired
	private MonitorStrongPointConsumer monitorStrongPoint;

	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public FuturesTradeEntrust findById(Long id) {
		return dao.retrieve(id);
	}

	public FuturesTradeEntrust save(FuturesTradeEntrust futuresTradeEntrust) {
		return dao.create(futuresTradeEntrust);
	}

	public FuturesTradeEntrust modify(FuturesTradeEntrust futuresTradeEntrust) {
		return dao.update(futuresTradeEntrust);
	}

	public void delete(Long id) {
		dao.delete(id);
	}

	@Transactional
	public FuturesTradeEntrust cancelEntrust(Long id, Long publisherId) {
		FuturesTradeEntrust entrust = dao.retrieve(id);
		if (entrust == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		if (FuturesTradeEntrustState.Queuing != entrust.getState()) {
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// step 1 : 更新开平仓记录和订单状态
		FuturesContract contract = entrust.getContract();
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		List<FuturesTradeAction> actionList = actionDao.retrieveByTradeEntrust(entrust);
		for (FuturesTradeAction action : actionList) {
			// step 1.1 : 更新开平仓记录状态
			action.setRemaining(BigDecimal.ZERO);
			action.setState(FuturesTradeEntrustState.Canceled);
			action.setUpdateTime(new Date());
			// step 1.2 : 更新订单状态
			FuturesOrder order = action.getOrder();
			order.setCloseRemaining(order.getCloseRemaining().subtract(action.getRemaining()));
			if (entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
				order.setState(FuturesOrderState.BuyingCanceled);
			} else {
				if (order.getOrderType() == FuturesOrderType.BuyUp) {
					contractOrder.setBuyUpCanUnwindQuantity(
							contractOrder.getBuyUpCanUnwindQuantity().add(action.getQuantity()));
					contractOrderDao.doUpdate(contractOrder);
				} else {
					contractOrder.setBuyFallCanUnwindQuantity(
							contractOrder.getBuyFallCanUnwindQuantity().add(action.getQuantity()));
					contractOrderDao.doUpdate(contractOrder);
				}
				if (order.getCloseRemaining().compareTo(BigDecimal.ZERO) > 0
						|| order.getCloseFilled().compareTo(BigDecimal.ZERO) > 0) {
					order.setState(FuturesOrderState.PartUnwind);
				} else {
					order.setState(FuturesOrderState.Position);
				}
			}
			order.setUpdateTime(new Date());
			orderDao.update(order);
			// step 1.3 : 退回交易综合费
			if (entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
				BigDecimal returnServiceFee = action.getOrder().getServiceFee();
				accountBusiness.futuresOrderRevoke(publisherId, order.getId(), returnServiceFee);
			}
		}
		// step 2 : 更新合约订单状态
		if (entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
			BigDecimal buyUpNum = contractOrder.getBuyUpTotalQuantity();
			BigDecimal buyFallNum = contractOrder.getBuyFallTotalQuantity();
			BigDecimal singleEdgeMax = BigDecimal.ZERO;
			if (entrust.getOrderType() == FuturesOrderType.BuyUp) {
				BigDecimal preBuyUpNum = buyUpNum.subtract(entrust.getQuantity());
				contractOrder.setBuyUpTotalQuantity(preBuyUpNum);
				singleEdgeMax = preBuyUpNum.compareTo(buyFallNum) >= 0 ? preBuyUpNum : buyFallNum;
			} else {
				BigDecimal prebuyFallNum = buyFallNum.subtract(entrust.getQuantity());
				contractOrder.setBuyFallTotalQuantity(prebuyFallNum);
				singleEdgeMax = prebuyFallNum.compareTo(buyUpNum) >= 0 ? prebuyFallNum : buyUpNum;
			}
			contractOrderDao.doUpdate(contractOrder);
			this.monitorContractOrder(contractOrder);
			BigDecimal expectReserveFund = contract.getCommodity().getPerUnitReserveFund().multiply(singleEdgeMax);
			// step 3 : 退款保证金，计算需要退款的保证金
			BigDecimal returnReserveFund = BigDecimal.ZERO;
			if (contractOrder.getReserveFund().compareTo(expectReserveFund) > 0) {
				returnReserveFund = contractOrder.getReserveFund().subtract(expectReserveFund);
				contractOrder.setReserveFund(contractOrder.getReserveFund().subtract(returnReserveFund));
				accountBusiness.futuresReturnReserveFund(publisherId, contractOrder.getId(), returnReserveFund);
				contractOrderDao.doUpdate(contractOrder);
			}
			entrust.setReturnReserveFund(returnReserveFund);
			// step 4 : 发送站外消息
			sendOutsideMessage(entrust);
		}
		// step 5 : 更新委托状态
		entrust.setState(FuturesTradeEntrustState.Canceled);
		entrust.setUpdateTime(new Date());
		dao.update(entrust);
		return entrust;
	}

	private void monitorContractOrder(FuturesContractOrder contractOrder) {
		BigDecimal buyUpCanUnwind = contractOrder.getBuyUpCanUnwindQuantity();
		BigDecimal buyFallCanUnwind = contractOrder.getBuyFallCanUnwindQuantity();
		boolean needMonitor = false;
		if (buyUpCanUnwind != null && buyUpCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
			needMonitor = true;
		}
		if (buyFallCanUnwind != null && buyFallCanUnwind.compareTo(BigDecimal.ZERO) > 0) {
			needMonitor = true;
		}
		if (needMonitor) {
			monitorStopLossOrProfit.monitorContractOrder(contractOrder.getId());
			monitorStrongPoint.monitorPublisher(contractOrder.getPublisherId());
		}
	}

	@Transactional
	public FuturesTradeEntrust failureOrder(Long id) {
		// TODO 已失败委托
		return null;
	}

	/**
	 * 计算盈利或者亏损（交易所货币）
	 *
	 * @param orderType
	 *            订单类型
	 * @param buyingPrice
	 *            买入价格
	 * @param sellingPrice
	 *            卖出价格
	 * @param minWave
	 *            最小波动
	 * @param perWaveMoney
	 *            波动一次盈亏金额，单位为该合约的货币单位
	 * @param rate
	 *            汇率
	 * @return 盈利或者亏损值
	 */
	private BigDecimal computeProfitOrLoss(FuturesOrderType orderType, BigDecimal totalQuantity, BigDecimal buyingPrice,
			BigDecimal sellingPrice, BigDecimal minWave, BigDecimal perWaveMoney) {
		BigDecimal waveMoney = sellingPrice.subtract(buyingPrice).divide(minWave, 4, RoundingMode.DOWN)
				.multiply(perWaveMoney).multiply(totalQuantity);
		if (orderType == FuturesOrderType.BuyUp) {
			return waveMoney;
		} else {
			return waveMoney.multiply(new BigDecimal(-1));
		}
	}

	@Transactional
	public FuturesTradeEntrust success(Long id, BigDecimal filled, BigDecimal remaining, BigDecimal avgFillPrice,
			Date date) {
		FuturesTradeEntrust entrust = dao.retrieve(id);
		BigDecimal minWave = entrust.getContract().getCommodity().getMinWave();
		BigDecimal perWaveMoney = entrust.getContract().getCommodity().getPerWaveMoney();
		String currency = entrust.getContract().getCommodity().getCurrency();
		FuturesOrderType orderType = entrust.getOrderType();
		FuturesTradeActionType actionType = entrust.getTradeActionType();
		date = date != null ? date : new Date();
		BigDecimal totalFilled = BigDecimal.ZERO;
		// step 1 : 更新订单信息
		List<FuturesTradeAction> actionList = actionDao.retrieveByTradeEntrustAndTradeActionType(entrust, actionType);
		for (FuturesTradeAction action : actionList) {
			if (action.getRemaining().compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}
			// step 1.1 : 计算当前成交的数量
			BigDecimal currentFilled = action.getRemaining();
			if (currentFilled.compareTo(filled) > 0) {
				currentFilled = filled;
			}
			filled = filled.subtract(currentFilled);
			// step 1.2 : 更新开平仓记录
			BigDecimal actionRemaining = action.getRemaining().subtract(currentFilled);
			BigDecimal actionFilled = action.getFilled().add(currentFilled);
			BigDecimal actionTotalFillCost = action.getTotalFillCost().add(avgFillPrice.multiply(currentFilled));
			BigDecimal actionAvgFillPrice = actionTotalFillCost.divide(actionFilled, 10, RoundingMode.DOWN);
			actionAvgFillPrice = avgFillPriceScale(minWave, actionAvgFillPrice, entrust.getOrderType(),
					entrust.getTradeActionType());
			if (actionRemaining.compareTo(BigDecimal.ZERO) > 0) {
				action.setState(FuturesTradeEntrustState.PartSuccess);
			} else {
				action.setState(FuturesTradeEntrustState.Success);
				action.setTradePrice(actionAvgFillPrice);
				action.setTradeTime(date);
				if (entrust.getTradeActionType() == FuturesTradeActionType.CLOSE) {
					// 盈亏（交易所货币）
					BigDecimal currencyProfitOrLoss = computeProfitOrLoss(orderType, actionFilled,
							action.getOrder().getOpenAvgFillPrice(), actionAvgFillPrice, minWave, perWaveMoney);
					// 盈亏（人民币）
					BigDecimal rate = rateService.findByCurrency(currency).getRate();
					BigDecimal profitOrLoss = currencyProfitOrLoss.multiply(rate).setScale(2, RoundingMode.DOWN);
					action.setCurrencyProfitOrLoss(currencyProfitOrLoss);
					action.setProfitOrLoss(profitOrLoss);
					action.setSettlementRate(rate);
					action.setSettlementTime(date);
				}
			}
			action.setAvgFillPrice(actionAvgFillPrice);
			action.setFilled(actionFilled);
			action.setRemaining(actionRemaining);
			action.setTotalFillCost(actionTotalFillCost);
			action.setUpdateTime(date);
			actionDao.update(action);
			// step 1.2 : 更新订单信息
			FuturesOrder order = action.getOrder();
			if (entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
				order.setOpenFilled(order.getOpenFilled().add(currentFilled));
				order.setOpenRemaining(order.getOpenRemaining().subtract(currentFilled));
				order.setOpenTotalFillCost(order.getOpenTotalFillCost().add(avgFillPrice.multiply(currentFilled)));
				BigDecimal orderAvgFillPrice = order.getOpenTotalFillCost().divide(order.getOpenFilled(), 10,
						RoundingMode.DOWN);
				orderAvgFillPrice = avgFillPriceScale(minWave, orderAvgFillPrice, orderType, actionType);
				order.setOpenAvgFillPrice(orderAvgFillPrice);
				if (order.getOpenRemaining().compareTo(BigDecimal.ZERO) > 0) {
					order.setState(FuturesOrderState.PartPosition);
				} else {
					order.setState(FuturesOrderState.Position);
					order.setOpenTradeTime(date);
				}
			} else {
				order.setCloseFilled(order.getCloseFilled().add(currentFilled));
				order.setCloseRemaining(order.getCloseRemaining().subtract(currentFilled));
				order.setCloseTotalFillCost(order.getCloseTotalFillCost().add(avgFillPrice.multiply(currentFilled)));
				BigDecimal orderAvgFillPrice = order.getCloseTotalFillCost().divide(order.getCloseFilled(), 10,
						RoundingMode.DOWN);
				orderAvgFillPrice = avgFillPriceScale(minWave, orderAvgFillPrice, orderType, actionType);
				order.setCloseAvgFillPrice(orderAvgFillPrice);
				if (order.getCloseRemaining().compareTo(BigDecimal.ZERO) <= 0
						&& order.getTotalQuantity().compareTo(order.getCloseFilled()) == 0) {
					order.setState(FuturesOrderState.Unwind);
				} else {
					order.setState(FuturesOrderState.PartUnwind);
				}
			}
			orderDao.update(order);
			// step 1.3 : 更新合约订单
			FuturesContractOrder contractOrder = order.getContractOrder();
			if (orderType == FuturesOrderType.BuyUp && actionType == FuturesTradeActionType.OPEN) {
				contractOrder.setBuyUpQuantity(contractOrder.getBuyUpQuantity().add(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().add(currentFilled));
				contractOrder.setBuyUpCanUnwindQuantity(contractOrder.getBuyUpCanUnwindQuantity().add(currentFilled));
			} else if (orderType == FuturesOrderType.BuyUp && actionType == FuturesTradeActionType.CLOSE) {
				contractOrder.setBuyUpTotalQuantity(contractOrder.getBuyUpTotalQuantity().subtract(currentFilled));
				contractOrder.setBuyUpQuantity(contractOrder.getBuyUpQuantity().subtract(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().subtract(currentFilled));
			} else if (orderType == FuturesOrderType.BuyFall && actionType == FuturesTradeActionType.OPEN) {
				contractOrder.setBuyFallQuantity(contractOrder.getBuyFallQuantity().add(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().subtract(currentFilled));
				contractOrder
						.setBuyFallCanUnwindQuantity(contractOrder.getBuyFallCanUnwindQuantity().add(currentFilled));
			} else if (orderType == FuturesOrderType.BuyFall && actionType == FuturesTradeActionType.CLOSE) {
				contractOrder.setBuyFallTotalQuantity(contractOrder.getBuyFallTotalQuantity().subtract(currentFilled));
				contractOrder.setBuyFallQuantity(contractOrder.getBuyFallQuantity().subtract(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().add(currentFilled));
			}
			contractOrderDao.doUpdate(contractOrder);
			this.monitorContractOrder(contractOrder);
			// step 1.4 : 如果没有的剩余，停止循环
			totalFilled = totalFilled.add(currentFilled);
			if (filled.compareTo(BigDecimal.ZERO) <= 0) {
				break;
			}
		}
		// step 2 : 更新委托记录
		if (totalFilled.compareTo(BigDecimal.ZERO) > 0) {
			entrust.setFilled(entrust.getFilled().add(totalFilled));
			entrust.setRemaining(entrust.getRemaining().subtract(totalFilled));
			entrust.setTotalFillCost(entrust.getTotalFillCost().add(avgFillPrice.multiply(totalFilled)));
			BigDecimal entrustAvgFillPrice = entrust.getTotalFillCost().divide(entrust.getFilled(), 10,
					RoundingMode.DOWN);
			entrustAvgFillPrice = avgFillPriceScale(minWave, entrustAvgFillPrice, orderType, actionType);
			entrust.setAvgFillPrice(entrustAvgFillPrice);
			entrust.setUpdateTime(date);
			if (entrust.getRemaining().compareTo(BigDecimal.ZERO) > 0) {
				entrust.setState(FuturesTradeEntrustState.PartSuccess);
			} else {
				entrust.setState(FuturesTradeEntrustState.Success);
				entrust.setTradePrice(entrustAvgFillPrice);
				entrust.setTradeTime(date);
				if (entrust.getTradeActionType() == FuturesTradeActionType.CLOSE) {
					FuturesContract contract = entrust.getContract();
					FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract,
							entrust.getPublisherId());
					BigDecimal singleEdgeMax = contractOrder.getBuyUpTotalQuantity()
							.compareTo(contractOrder.getBuyFallTotalQuantity()) > 0
									? contractOrder.getBuyUpTotalQuantity() : contractOrder.getBuyFallTotalQuantity();
					contractOrderDao.doUpdate(contractOrder);
					BigDecimal expectReserveFund = contract.getCommodity().getPerUnitReserveFund()
							.multiply(singleEdgeMax);
					// step 2.1 : 退款保证金，计算需要退款的保证金
					BigDecimal returnReserveFund = BigDecimal.ZERO;
					if (contractOrder.getReserveFund().compareTo(expectReserveFund) > 0) {
						returnReserveFund = contractOrder.getReserveFund().subtract(expectReserveFund);
						contractOrder.setReserveFund(contractOrder.getReserveFund().subtract(returnReserveFund));
						accountBusiness.futuresReturnReserveFund(entrust.getPublisherId(), contractOrder.getId(),
								returnReserveFund);
						contractOrderDao.doUpdate(contractOrder);
					}
					entrust.setReturnReserveFund(returnReserveFund);
					futuresTradeEntrustDao.update(entrust);
					// step 2.2 : 给用户结算盈亏
					BigDecimal rate = rateService.findByCurrency(currency).getRate();
					entrust.setSettlementTime(date);
					entrust.setSettlementRate(rate);
					BigDecimal totalOpenCost = BigDecimal.ZERO;
					BigDecimal totalUnwindQuantity = BigDecimal.ZERO;
					BigDecimal totalPublisherProfitOrLoss = BigDecimal.ZERO;
					for (FuturesTradeAction action : actionList) {
						totalUnwindQuantity = totalUnwindQuantity.add(action.getQuantity());
						totalOpenCost = totalUnwindQuantity.multiply(action.getOpenAvgFillPrice());
						CapitalAccountDto account = accountBusiness.futuresOrderSettlement(action.getPublisherId(),
								action.getOrder().getId(), action.getProfitOrLoss());
						// 发布人盈亏（人民币）、平台盈亏（人民币）
						BigDecimal publisherProfitOrLoss = BigDecimal.ZERO;
						BigDecimal platformProfitOrLoss = BigDecimal.ZERO;
						if (action.getProfitOrLoss().compareTo(BigDecimal.ZERO) > 0) {
							publisherProfitOrLoss = action.getProfitOrLoss();
						} else if (action.getProfitOrLoss().compareTo(BigDecimal.ZERO) < 0) {
							publisherProfitOrLoss = account.getRealProfitOrLoss();
							if (action.getProfitOrLoss().abs().compareTo(publisherProfitOrLoss.abs()) > 0) {
								platformProfitOrLoss = action.getProfitOrLoss().abs()
										.subtract(publisherProfitOrLoss.abs()).multiply(new BigDecimal(-1));
							}
						}
						action.setPublisherProfitOrLoss(publisherProfitOrLoss);
						totalPublisherProfitOrLoss = totalPublisherProfitOrLoss.add(publisherProfitOrLoss);
						action.setPlatformProfitOrLoss(platformProfitOrLoss);
						actionDao.update(action);

						logger.info("代理分成自动平仓外, actionNo:{}, state:{}", action.getActionNo(),
								action.getState().getType());
						// 给代理商分成结算
						if (action.getOrder().getIsTest() == null || action.getOrder().getIsTest() == false) {
							logger.info("代理分成自动平仓内, actionNo:{}, state:{}", action.getActionNo(),
									action.getState().getType());
							// 递延费
							BigDecimal deferredFee = BigDecimal.ZERO;
							if (deferredFee == null) {
								deferredFee = BigDecimal.ZERO;
							}
							// 当前拆分的服务费
							BigDecimal currentServiceFee = action.getOrder().getServiceFee()
									.divide(action.getOrder().getTotalQuantity()).multiply(action.getQuantity());
							// 结算
							orgBusiness.futuresRatioSettlement(action.getPublisherId(), action.getContractId(),
									action.getId(), action.getActionNo(), action.getOrder().getTotalQuantity(),
									currentServiceFee, publisherProfitOrLoss, deferredFee);
						}

					}
					// 计算开仓的均价
					BigDecimal openAvgFillPrice = totalOpenCost.multiply(totalUnwindQuantity);
					BigDecimal[] divideArr = openAvgFillPrice.divideAndRemainder(minWave);
					openAvgFillPrice = divideArr[0].multiply(minWave);
					entrust.setOpenAvgFillPrice(openAvgFillPrice);
					entrust.setPublisherProfitOrLoss(totalPublisherProfitOrLoss);
				}
				dao.update(entrust);
				sendOutsideMessage(entrust);
			}
		}
		return entrust;
	}

	private void sendOutsideMessage(FuturesTradeEntrust entrust) {
		try {
			FuturesTradeEntrustState state = entrust.getState();
			FuturesCommodity commodity = entrust.getContract().getCommodity();
			Map<String, String> extras = new HashMap<>();
			OutsideMessage message = new OutsideMessage();
			message.setPublisherId(entrust.getPublisherId());
			message.setTitle("期货订单通知");
			extras.put("title", message.getTitle());
			extras.put("publisherId", String.valueOf(entrust.getPublisherId()));
			extras.put("resourceType", ResourceType.FUTURESTRADEENTRUST.getIndex());
			extras.put("resourceId", String.valueOf(entrust.getId()));
			message.setExtras(extras);
			switch (state) {
			case Failure:
				message.setContent(String.format("您购买的“%s”委托买入失败，已退款到您的账户", commodity.getName()));
				extras.put("content",
						String.format("您购买的“<span id=\"futures\">%s</span>”委托买入失败，已退款到您的账户", commodity.getName()));
				extras.put("type", OutsideMessageType.Futures_BuyingFailure.getIndex());
				break;
			case Canceled:
				message.setContent(String.format("您所购买的“%s”已取消委托，已退款到您的账户", commodity.getName()));
				extras.put("content",
						String.format("您所购买的“<span id=\"futures\">%s</span>”已取消委托，已退款到您的账户", commodity.getName()));
				extras.put("type", OutsideMessageType.Futures_BuyingCanceled.getIndex());
				break;
			case Success:
				if (entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
					// 开仓
					if (entrust.getPriceType() == FuturesTradePriceType.MKT) {
						message.setContent(String.format("您购买的“%s”已开仓成功，进入“持仓中”状态", commodity.getName()));
						extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”已开仓成功，进入“持仓中”状态",
								commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_Position.getIndex());
						break;
					} else {
						message.setContent(String.format("您委托指定价购买“%s”已开仓成功，进入“持仓中”状态", commodity.getName()));
						extras.put("content", String.format("您委托指定价购买“<span id=\"futures\">%s</span>”已开仓成功，进入“持仓中”状态",
								commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_EntrustPosition.getIndex());
						break;
					}
				} else {
					// 平仓
					FuturesWindControlType windControlType = entrust.getWindControlType();
					if (windControlType != null && windControlType == FuturesWindControlType.DayUnwind) {
						// 日内平仓
						message.setContent(String.format("您购买的“%s”因余额不足无法持仓过夜系统已强制平仓，已进入结算状态", commodity.getName()));
						extras.put("content", String.format(
								"您购买的“<span id=\"futures\">%s</span>”因余额不足无法持仓过夜系统已强制平仓，已进入结算状态", commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_DayUnwind.getIndex());
						break;
					} else if (windControlType != null && windControlType == FuturesWindControlType.UserApplyUnwind) {
						// 用户申请平仓
						message.setContent(String.format("您购买的“%s”手动平仓，已进入结算状态", commodity.getName()));
						extras.put("content",
								String.format("您购买的“<span id=\"futures\">%s</span>”手动平仓，已进入结算状态", commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_ApplyUnwind.getIndex());
						break;
					} else if (windControlType != null && windControlType == FuturesWindControlType.ReachProfitPoint) {
						// 达到止盈点
						message.setContent(String.format("您购买的“%s”达到止盈平仓，已进入结算状态", commodity.getName()));
						extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”达到止盈平仓，已进入结算状态",
								commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_ReachProfitPoint.getIndex());
						break;
					} else if (windControlType != null && windControlType == FuturesWindControlType.ReachLossPoint) {
						// 达到止损点
						message.setContent(String.format("您购买的“%s”达到止损平仓，已进入结算状态", commodity.getName()));
						extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”达到止损平仓，已进入结算状态",
								commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_ReachLossPoint.getIndex());
						break;
					} else if (windControlType != null
							&& windControlType == FuturesWindControlType.ReachContractExpiration) {
						// 合约到期平仓
						message.setContent(String.format("您购买的“%s”因合约到期系统强制平仓，已进入结算状态", commodity.getName()));
						extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”因合约到期系统强制平仓，已进入结算状态",
								commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_ReachContractExpiration.getIndex());
						break;
					} else if (windControlType != null && windControlType == FuturesWindControlType.ReachStrongPoint) {
						// 达到强平点
						message.setContent(String.format("您购买的“%s”因达到系统强平风控金额，已强制平仓，已进入结算状态", commodity.getName()));
						extras.put("content", String.format(
								"您购买的“<span id=\"futures\">%s</span>”因达到系统强平风控金额，已强制平仓，已进入结算状态", commodity.getName()));
						extras.put("type", OutsideMessageType.Futures_ReachStrongPoint.getIndex());
						break;
					}
				}
				break;
			default:
				break;
			}
			if (message.getContent() != null) {
				outsideMessageBusiness.send(message);
			}
		} catch (Exception ex) {
			logger.error("发送期货订单通知失败，{}_{}_{}", entrust.getId(), entrust.getState(), ex.getMessage());
		}
	}

	private BigDecimal avgFillPriceScale(BigDecimal minWave, BigDecimal avgFillPrice, FuturesOrderType orderType,
			FuturesTradeActionType actionType) {
		if ((orderType == FuturesOrderType.BuyUp && actionType == FuturesTradeActionType.OPEN)
				|| (orderType == FuturesOrderType.BuyFall && actionType == FuturesTradeActionType.CLOSE)) {
			BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(minWave);
			if (divideArr[1].compareTo(BigDecimal.ZERO) > 0) {
				avgFillPrice = divideArr[0].add(new BigDecimal(1)).multiply(minWave);
			}
			return avgFillPrice;
		}
		if ((orderType == FuturesOrderType.BuyFall && actionType == FuturesTradeActionType.OPEN)
				|| (orderType == FuturesOrderType.BuyUp && actionType == FuturesTradeActionType.CLOSE)) {
			BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(minWave);
			avgFillPrice = divideArr[0].multiply(minWave);
		}
		return avgFillPrice;
	}

	public Page<FuturesTradeEntrust> pagesTradeAdmin(final FuturesTradeEntrustQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesTradeEntrust> page = futuresTradeEntrustDao.page(new Specification<FuturesTradeEntrust>() {
			@Override
			public Predicate toPredicate(Root<FuturesTradeEntrust> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();

				// 委托编号
				if (!StringUtils.isEmpty(query.getEntrustNo())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("entrustNo").as(String.class), query.getEntrustNo()));
				}

				// 用户ID
				if (query.getPublisherId() != null && query.getPublisherId() != 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}

				// 品种编号
				if (!StringUtils.isEmpty(query.getCommodityNo())) {
					predicateList.add(
							criteriaBuilder.equal(root.get("commodityNo").as(String.class), query.getCommodityNo()));
				}
				// 合约编号
				if (!StringUtils.isEmpty(query.getContractNo())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("contractNo").as(String.class), query.getContractNo()));
				}
				// 订单类型
				if (!StringUtils.isEmpty(query.getOrderType())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("orderType").as(String.class), query.getOrderType()));
				}

				// 价格类型
				if (!StringUtils.isEmpty(query.getPriceType())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("priceType").as(String.class), query.getPriceType()));
				}
				// 交易开平仓 类型
				if (!StringUtils.isEmpty(query.getTradeActionType())) {
					predicateList.add(criteriaBuilder.equal(root.get("tradeActionType").as(String.class),
							query.getTradeActionType()));
				}
				// 委托状态
				if (!StringUtils.isEmpty(query.getState())) {
					predicateList.add(criteriaBuilder.equal(root.get("state").as(String.class), query.getState()));
				}

				if (query.getStartTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("tradeTime").as(Date.class),
							query.getStartTime()));
				}
				if (query.getEndTime() != null) {
					predicateList
							.add(criteriaBuilder.lessThan(root.get("tradeTime").as(Date.class), query.getEndTime()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				// 以委托时间排序
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("entrustTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

	public Page<FuturesTradeEntrust> pages(final FuturesTradeEntrustQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesTradeEntrust> page = futuresTradeEntrustDao.page(new Specification<FuturesTradeEntrust>() {
			@Override
			public Predicate toPredicate(Root<FuturesTradeEntrust> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();

				// 委托编号
				if (!StringUtils.isEmpty(query.getEntrustNo())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("entrustNo").as(String.class), query.getEntrustNo()));
				}

				// 用户ID
				if (query.getPublisherId() != null && query.getPublisherId() != 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				// 品种编号
				if (!StringUtils.isEmpty(query.getCommodityNo())) {
					predicateList.add(
							criteriaBuilder.equal(root.get("commodityNo").as(String.class), query.getCommodityNo()));
				}
				// 合约编号
				if (!StringUtils.isEmpty(query.getContractNo())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("contractNo").as(String.class), query.getContractNo()));
				}
				// 订单类型
				if (!StringUtils.isEmpty(query.getOrderType())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("orderType").as(String.class), query.getOrderType()));
				}
				// 价格类型
				if (!StringUtils.isEmpty(query.getPriceType())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("priceType").as(String.class), query.getPriceType()));
				}
				// 交易开平仓 类型
				if (!StringUtils.isEmpty(query.getTradeActionType())) {
					predicateList.add(criteriaBuilder.equal(root.get("tradeActionType").as(String.class),
							query.getTradeActionType()));
				}
				// 委托状态
				if (!StringUtils.isEmpty(query.getState())) {
					predicateList.add(criteriaBuilder.equal(root.get("state").as(String.class), query.getState()));
				}
				if (query.getStartTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("tradeTime").as(Date.class),
							query.getStartTime()));
				}
				if (query.getEndTime() != null) {
					predicateList
							.add(criteriaBuilder.lessThan(root.get("tradeTime").as(Date.class), query.getEndTime()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				// 以委托时间排序
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("entrustTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

	public Page<FuturesTradeEntrust> pagesAction(final FuturesTradeEntrustQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesTradeEntrust> page = futuresTradeEntrustDao.page(new Specification<FuturesTradeEntrust>() {
			@Override
			public Predicate toPredicate(Root<FuturesTradeEntrust> root, CriteriaQuery<?> criteriaQuery,
										 CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();

				// 委托编号
				if (!StringUtils.isEmpty(query.getEntrustNo())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("entrustNo").as(String.class), query.getEntrustNo()));
				}

				// 用户ID
				if (query.getPublisherId() != null && query.getPublisherId() != 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				// 品种编号
				if (!StringUtils.isEmpty(query.getCommodityNo())) {
					predicateList.add(
							criteriaBuilder.equal(root.get("commodityNo").as(String.class), query.getCommodityNo()));
				}
				// 合约编号
				if (!StringUtils.isEmpty(query.getContractNo())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("contractNo").as(String.class), query.getContractNo()));
				}
				// 订单类型
				if (!StringUtils.isEmpty(query.getOrderType())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("orderType").as(String.class), query.getOrderType()));
				}
				// 价格类型
				if (!StringUtils.isEmpty(query.getPriceType())) {
					predicateList
							.add(criteriaBuilder.equal(root.get("priceType").as(String.class), query.getPriceType()));
				}
				// 交易开平仓 类型
				if (!StringUtils.isEmpty(query.getTradeActionType())) {
					predicateList.add(criteriaBuilder.equal(root.get("tradeActionType").as(String.class),
							query.getTradeActionType()));
				}
				// 委托状态
				if (!StringUtils.isEmpty(query.getState())) {
					predicateList.add(criteriaBuilder.equal(root.get("state").as(String.class), query.getState()));
				}
				if (query.getStartTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("tradeTime").as(Date.class),
							query.getStartTime()));
				}
				if (query.getEndTime() != null) {
					predicateList
							.add(criteriaBuilder.lessThan(root.get("tradeTime").as(Date.class), query.getEndTime()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				// 以委托时间排序
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("entrustTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

	public Page<FuturesTradeEntrust> pagesPhoneEntrust(final FuturesTradeEntrustQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesTradeEntrust> page = futuresTradeEntrustDao.page(new Specification<FuturesTradeEntrust>() {
			@Override
			public Predicate toPredicate(Root<FuturesTradeEntrust> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				if (query.getId() != null && query.getId() != 0) {
					predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), query.getId()));
				}
				// 用户ID
				if (query.getPublisherId() != null && query.getPublisherId() != 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				if (query.getStartTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("tradeTime").as(Date.class),
							query.getStartTime()));
				}
				if (query.getEndTime() != null) {
					predicateList
							.add(criteriaBuilder.lessThan(root.get("tradeTime").as(Date.class), query.getEndTime()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("entrustTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

	public Page<FuturesTradeEntrust> pagesPhoneAction(final FuturesTradeEntrustQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesTradeEntrust> page = futuresTradeEntrustDao.page(new Specification<FuturesTradeEntrust>() {
			@Override
			public Predicate toPredicate(Root<FuturesTradeEntrust> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				if (query.getId() != null && query.getId() != 0) {
					predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), query.getId()));
				}
				// 用户ID
				if (query.getPublisherId() != null && query.getPublisherId() != 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				Predicate predicate1 = criteriaBuilder.and(
						criteriaBuilder.equal(root.get("tradeActionType").as(FuturesTradeActionType.class),
								FuturesTradeActionType.OPEN),
						criteriaBuilder.and(root.get("state").in(new FuturesTradeEntrustState[] {
								FuturesTradeEntrustState.Canceled, FuturesTradeEntrustState.Failure })));
				Predicate predicate2 = criteriaBuilder.and(
						criteriaBuilder.equal(root.get("tradeActionType").as(FuturesTradeActionType.class),
								FuturesTradeActionType.CLOSE),
						criteriaBuilder.and(root.get("state").in(new FuturesTradeEntrustState[] {
								FuturesTradeEntrustState.PartSuccess, FuturesTradeEntrustState.Success })));
				predicateList.add(criteriaBuilder.or(predicate1, predicate2));

				if (query.getStartTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("tradeTime").as(Date.class),
							query.getStartTime()));
				}
				if (query.getEndTime() != null) {
					predicateList
							.add(criteriaBuilder.lessThan(root.get("tradeTime").as(Date.class), query.getEndTime()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("tradeTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

	public Page<FuturesTradeDto> pageTradeAdmin(FuturesTradeAdminQuery query) {

		String publisherNameCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			publisherNameCondition = " and f4.phone like '%" + query.getPublisherPhone() + "%' ";
		}
		String publisherPhoneCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			publisherPhoneCondition = " and f5.name like '%" + query.getPublisherName() + "%' ";
		}
		String contractNameCondition = "";
		if (!StringUtil.isEmpty(query.getName())) {
			contractNameCondition = " and f3.commodity_name like '%" + query.getName() + "%'";
		}
		String contractNoCondition = "";
		if (!StringUtil.isEmpty(query.getSymbol())) {
			contractNoCondition = " and f1.commodity_no like '%" + query.getSymbol() + "%'";
		}
		String orderTypeCondition = "";
		if (!StringUtil.isEmpty(query.getOrderType())) {
			orderTypeCondition = " and f1.orderType = " + query.getOrderType().trim();
		}
		String tradeActionCondition = "";
		if (query.getTradeActionType() != null) {
			tradeActionCondition = " and f1.trade_action_type =" + query.getTradeActionType();
		}
		String tradeNoCondition = "";
		if (!StringUtil.isEmpty(query.getTradeNo())) {
			tradeNoCondition = " and f1.entrust_no = " + query.getTradeNo().trim();
		}

		String orderStateCondition = "";
		if (!StringUtil.isEmpty(query.getOrderState())) {
			orderStateCondition = " and f1.state in in(" + query.getOrderState().trim() + ") ";
		}

		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			startTimeCondition = " and t1.entrust_time>='" + fullSdf.format(query.getStartTime()) + "' ";
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			endTimeCondition = " and t1.entrust_time<'" + fullSdf.format(query.getEndTime()) + "' ";
		}

		String treeCode = "";
		if (query.getTreeCode() != null) {
			treeCode = " AND t8.tree_code LIKE '%" + query.getTreeCode() + "%'";
		}

		String sql = String.format(
				" select f1.id, f5.name, f4.phone, f1.commodity_no, f3.commodity_name, f1.contract_no,f1.order_type,"
						+ " f1.trade_action_type, f1.filled, f1.quantity, f1.entrust_price, f1.avg_fill_price,"
						+ " f1.entrust_no, f1.entrust_time, f1.state,f1.total_fill_cost, f1.trade_price,"
						+ " f1.price_type, f3.is_test, f1.return_reserve_fund, t8.code, t8.name as org_name, f2.trade_time "
						+ " from f_futures_trade_entrust f1"
						+ " LEFT JOIN f_futures_trade_action f2 on f1.id=f2.trade_entrust_id"
						+ " LEFT JOIN f_futures_order f3 ON f2.order_id=f3.id"
						+ " LEFT JOIN publisher f4 ON f1.publisher_id=f4.id"
						+ " LEFT JOIN real_name f5 ON f5.resource_id = f1.publisher_id"
						+ " LEFT JOIN f_futures_contract t6 ON t6.id = f1.contract_id "
						+ " LEFT JOIN p_organization_publisher t7 ON t7.publisher_id = f1.publisher_id "
						+ " LEFT JOIN p_organization t8 ON t8.id = t7.org_id"
						+ " where 1=1 %s %s %s %s %s %s %s %s %s %s %s  ORDER BY f1.entrust_time DESC LIMIT "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				publisherNameCondition, publisherPhoneCondition, contractNameCondition, contractNoCondition,
				orderTypeCondition, tradeActionCondition, tradeNoCondition, orderStateCondition, startTimeCondition,
				endTimeCondition, treeCode);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("LIMIT"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setSymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setContractNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setOrderType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setTradeActionType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setFilled", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setQuantity", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setEntrustPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setAvgFillPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setEntrustNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setEntrustTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setState", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setTotalFillCost", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setTradePrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setPriceType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setIsTest", new Class<?>[] { Boolean.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setReturnReserveFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(20), new MethodDesc("setCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(21), new MethodDesc("setOrgName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(22), new MethodDesc("setTradeTime", new Class<?>[] { Date.class }));

		List<FuturesTradeDto> content = sqlDao.execute(FuturesTradeDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

}
