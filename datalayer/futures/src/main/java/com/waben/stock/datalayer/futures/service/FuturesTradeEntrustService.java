package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.business.OutsideMessageBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeActionDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
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

/**
 * 交易委托
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesTradeEntrustService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesTradeEntrustDao dao;

	@Autowired
	private FuturesTradeActionDao actionDao;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private FuturesOrderDao orderDao;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private OutsideMessageBusiness outsideMessageBusiness;

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
		if (FuturesTradeEntrustState.Queuing != entrust.getState()) {
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// step 1 : 更新委托状态
		entrust.setState(FuturesTradeEntrustState.Canceled);
		entrust.setUpdateTime(new Date());
		// step 2 : 更新开平仓记录和订单状态
		List<FuturesTradeAction> actionList = actionDao.retrieveByTradeEntrust(entrust);
		for(FuturesTradeAction action : actionList) {
			// step 2.1 : 更新开平仓记录状态
			action.setState(FuturesTradeEntrustState.Canceled);
			action.setUpdateTime(new Date());
			// step 2.2 : 更新订单状态
			FuturesOrder order = action.getOrder();
			order.setCloseRemaining(order.getCloseRemaining().subtract(action.getRemaining()));
			if(entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
				order.setState(FuturesOrderState.BuyingCanceled);
			} else {
				if(order.getCloseRemaining().compareTo(BigDecimal.ZERO) > 0 || order.getCloseFilled().compareTo(BigDecimal.ZERO) > 0) {
					order.setState(FuturesOrderState.PartUnwind);
				} else {
					order.setState(FuturesOrderState.Unwind);
				}
			}
			order.setUpdateTime(new Date());
			orderDao.update(order);
			// step 2.3 : 退回交易综合费
			if(entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
				BigDecimal returnServiceFee = action.getOrder().getServiceFee();
				accountBusiness.futuresOrderRevoke(publisherId, order.getId(), returnServiceFee);
			}
		}
		// step 3 : 更新合约订单状态
		FuturesContract contract = entrust.getContract();
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		if(entrust.getTradeActionType() == FuturesTradeActionType.OPEN) {
			BigDecimal expectReverseFund = BigDecimal.ZERO;
			if(entrust.getOrderType() == FuturesOrderType.BuyUp) {
				contractOrder.setBuyUpTotalQuantity(contractOrder.getBuyUpTotalQuantity().subtract(entrust.getRemaining()));
				expectReverseFund = contract.getCommodity().getPerUnitReserveFund().multiply(contractOrder.getBuyUpTotalQuantity());
			} else {
				contractOrder.setBuyFallTotalQuantity(contractOrder.getBuyFallTotalQuantity().subtract(entrust.getRemaining()));
				expectReverseFund = contract.getCommodity().getPerUnitReserveFund().multiply(contractOrder.getBuyFallTotalQuantity());
			}
			// step 4 : 退款保证金，计算需要退款的保证金
			BigDecimal returnReverseFund = BigDecimal.ZERO;
			if(contractOrder.getReserveFund().compareTo(expectReverseFund) > 0) {
				returnReverseFund = contractOrder.getReserveFund().subtract(expectReverseFund);
			}
			accountBusiness.futuresReturnReserveFund(publisherId, contractOrder.getId(), returnReverseFund);
		}
		return entrust;
	}

	@Transactional
	public FuturesTradeEntrust failureOrder(Long id) {
		// TODO 已失败委托
		return null;
	}

	@Transactional
	public FuturesTradeEntrust success(Long id, BigDecimal filled, BigDecimal remaining, BigDecimal avgFillPrice) {
		FuturesTradeEntrust entrust = dao.retrieve(id);
		BigDecimal minWave = entrust.getContract().getCommodity().getMinWave();
		FuturesOrderType orderType = entrust.getOrderType();
		FuturesTradeActionType actionType = entrust.getTradeActionType();
		Date date = new Date();
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
			remaining = remaining.subtract(currentFilled);
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
					// TODO 计算盈亏
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
				}
			} else {
				order.setCloseFilled(order.getCloseFilled().add(currentFilled));
				order.setCloseRemaining(order.getCloseRemaining().subtract(currentFilled));
				order.setCloseTotalFillCost(order.getCloseTotalFillCost().add(avgFillPrice.multiply(currentFilled)));
				BigDecimal orderAvgFillPrice = order.getCloseTotalFillCost().divide(order.getCloseFilled(), 10,
						RoundingMode.DOWN);
				orderAvgFillPrice = avgFillPriceScale(minWave, orderAvgFillPrice, orderType, actionType);
				order.setCloseAvgFillPrice(orderAvgFillPrice);
				if (order.getCloseRemaining().compareTo(BigDecimal.ZERO) > 0) {
					order.setState(FuturesOrderState.PartUnwind);
				} else {
					order.setState(FuturesOrderState.Unwind);
				}
			}
			orderDao.update(order);
			// step 1.3 : 更新合约订单
			FuturesContractOrder contractOrder = order.getContractOrder();
			if (orderType == FuturesOrderType.BuyUp && actionType == FuturesTradeActionType.OPEN) {
				contractOrder.setBuyUpQuantity(contractOrder.getBuyUpQuantity().add(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().add(currentFilled));
			} else if (orderType == FuturesOrderType.BuyUp && actionType == FuturesTradeActionType.CLOSE) {
				contractOrder.setBuyUpTotalQuantity(contractOrder.getBuyUpTotalQuantity().subtract(currentFilled));
				contractOrder.setBuyUpQuantity(contractOrder.getBuyUpQuantity().subtract(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().subtract(currentFilled));
			} else if (orderType == FuturesOrderType.BuyFall && actionType == FuturesTradeActionType.OPEN) {
				contractOrder.setBuyFallQuantity(contractOrder.getBuyFallQuantity().add(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().subtract(currentFilled));
			} else if (orderType == FuturesOrderType.BuyFall && actionType == FuturesTradeActionType.CLOSE) {
				contractOrder.setBuyFallTotalQuantity(contractOrder.getBuyFallTotalQuantity().subtract(currentFilled));
				contractOrder.setBuyFallQuantity(contractOrder.getBuyFallQuantity().subtract(currentFilled));
				contractOrder.setLightQuantity(contractOrder.getLightQuantity().add(currentFilled));
			}
			contractOrderDao.update(contractOrder);
			// step 1.4 : 如果没有的剩余，停止循环
			totalFilled = totalFilled.add(currentFilled);
			if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
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
					// TODO 给用户结算
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

}
