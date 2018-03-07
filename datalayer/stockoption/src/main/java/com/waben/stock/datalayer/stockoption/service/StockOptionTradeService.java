package com.waben.stock.datalayer.stockoption.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.waben.stock.datalayer.stockoption.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.stockoption.business.OutsideMessageBusiness;
import com.waben.stock.datalayer.stockoption.entity.StockOptionTrade;
import com.waben.stock.datalayer.stockoption.repository.StockOptionTradeDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.OutsideMessageType;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.enums.StockOptionTradeState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.message.OutsideMessage;
import com.waben.stock.interfaces.pojo.query.StockOptionTradeQuery;
import com.waben.stock.interfaces.pojo.query.StockOptionTradeUserQuery;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

@Service
public class StockOptionTradeService {

	Logger logger = LoggerFactory.getLogger(getClass());

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Autowired
	private StockOptionTradeDao stockOptionTradeDao;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private OutsideMessageBusiness outsideMessageBusiness;

	public Page<StockOptionTrade> pagesByUserQuery(final StockOptionTradeUserQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<StockOptionTrade> pages = stockOptionTradeDao.page(new Specification<StockOptionTrade>() {
			@Override
			public Predicate toPredicate(Root<StockOptionTrade> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (query.getStates() != null && query.getStates().length > 0) {
					predicateList.add(root.get("state").in(query.getStates()));
				}
				if (query.getPublisherId() != null && query.getPublisherId() > 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				if (query.isOnlyProfit()) {
					predicateList.add(criteriaBuilder.gt(root.get("profit").as(BigDecimal.class), new BigDecimal(0)));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sellingTime").as(Date.class)),
						criteriaBuilder.desc(root.get("buyingTime").as(Date.class)),
						criteriaBuilder.desc(root.get("applyTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	@Transactional
	public StockOptionTrade save(StockOptionTrade stockOptionTrade) {
		// 再检查一余额是否充足
		CapitalAccountDto account = accountBusiness.fetchByPublisherId(stockOptionTrade.getPublisherId());
		if (account.getAvailableBalance().compareTo(stockOptionTrade.getRightMoney()) < 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		stockOptionTrade.setTradeNo(UniqueCodeGenerator.generateTradeNo());
		stockOptionTrade.setState(StockOptionTradeState.WAITCONFIRMED);
		Date date = new Date();
		stockOptionTrade.setApplyTime(date);
		stockOptionTrade.setUpdateTime(date);
		stockOptionTradeDao.create(stockOptionTrade);
		// 扣去权利金
		try {
			accountBusiness.rightMoney(stockOptionTrade.getPublisherId(), stockOptionTrade.getId(),
					stockOptionTrade.getRightMoney());
		} catch (ServiceException ex) {
			if (ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION.equals(ex.getType())) {
				throw ex;
			} else {
				try {
					// TODO 再一次确认是否已经扣款
				} catch (ServiceException frozenEx) {
					throw ex;
				}
			}
		}
		// 站外消息推送
		sendOutsideMessage(stockOptionTrade);
		return stockOptionTrade;
	}

	public StockOptionTrade userRight(Long publisherId, Long id) {
		StockOptionTrade trade = findById(id);
		if (trade.getState() != StockOptionTradeState.TURNOVER) {
			throw new ServiceException(ExceptionConstant.STOCKOPTION_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION);
		}
		if (!trade.getPublisherId().equals(publisherId)) {
			throw new ServiceException(ExceptionConstant.STOCKOPTION_PUBLISHERID_NOTMATCH_EXCEPTION);
		}
		trade.setRightTime(new Date());
		trade.setState(StockOptionTradeState.APPLYRIGHT);
		stockOptionTradeDao.update(trade);
		// 站外消息推送
		sendOutsideMessage(trade);
		return trade;
	}

	public Page<StockOptionTrade> pagesByQuery(final StockOptionTradeQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<StockOptionTrade> pages = stockOptionTradeDao.page(new Specification<StockOptionTrade>() {
			@Override
			public Predicate toPredicate(Root<StockOptionTrade> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicatesList = new ArrayList<>();
				if (query.getBeginTime() != null) {
					predicatesList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("buyingTime").as(Date.class),
							query.getBeginTime()));
				}
				if (query.getEndTime() != null) {
					predicatesList
							.add(criteriaBuilder.lessThan(root.get("buyingTime").as(Date.class), query.getEndTime()));
				}
				if (!StringUtils.isEmpty(query.getPublisherPhone())) {
					Predicate publisherPhoneQuery = criteriaBuilder.equal(root.get("publisherPhone").as(Long.class),
							query.getPublisherPhone());
					predicatesList.add(publisherPhoneQuery);
				}
				if (!StringUtils.isEmpty(query.getApplyNo())) {
					Predicate applyNoQuery = criteriaBuilder.equal(root.get("applyNo").as(String.class),
							query.getApplyNo());
					predicatesList.add(applyNoQuery);
				}
				if (!StringUtils.isEmpty(query.getState())) {
					Predicate stateQuery = criteriaBuilder.equal(root.get("state").as(Integer.class), query.getState());
					predicatesList.add(stateQuery);
				}
				criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public StockOptionTrade findById(Long id) {
		return stockOptionTradeDao.retrieve(id);
	}

	@Transactional
	public StockOptionTrade settlement(Long id) {
		StockOptionTrade trade = stockOptionTradeDao.retrieve(id);
		BigDecimal sellingPrice = trade.getOfflineTrade().getSellingPrice();
		trade.setSellingPrice(sellingPrice);
		trade.setSellingTime(new Date());
		trade.setState(StockOptionTradeState.SETTLEMENTED);
		BigDecimal profit = BigDecimal.ZERO;
		if (sellingPrice.compareTo(trade.getBuyingPrice()) > 0) {
			profit = sellingPrice.subtract(trade.getBuyingPrice()).divide(trade.getBuyingPrice())
					.multiply(trade.getNominalAmount()).setScale(2, RoundingMode.DOWN);
		}
		trade.setProfit(profit);
		stockOptionTradeDao.update(trade);
		if (profit.compareTo(BigDecimal.ZERO) > 0) {
			// 用户收益
			accountBusiness.optionProfit(trade.getPublisherId(), trade.getId(), profit);
		}
		// 站外消息推送
		sendOutsideMessage(trade);
		return trade;
	}

	@Transactional
	public StockOptionTrade success(Long id) {
		StockOptionTrade trade = stockOptionTradeDao.retrieve(id);
		BigDecimal buyingPrice = trade.getOfflineTrade().getBuyingPrice();
		if (StockOptionTradeState.WAITCONFIRMED != trade.getState()) {
			throw new ServiceException(ExceptionConstant.STOCKOPTION_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION);
		}
		trade.setState(StockOptionTradeState.TURNOVER);
		trade.setBuyingPrice(buyingPrice);
		Date date = new Date();
		trade.setBuyingTime(date);
		try {
			// 计算到期日期
			Date beginTime = sdf.parse(sdf.format(date));
			Date expireTime = new Date(beginTime.getTime() + (trade.getCycle() - 1) * 24 * 60 * 60 * 1000);
			trade.setExpireTime(expireTime);
		} catch (ParseException e) {
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION);
		}
		return stockOptionTradeDao.update(trade);
	}

	@Transactional
	public StockOptionTrade exercise(Long id) {
		StockOptionTrade stockOptionTrade = stockOptionTradeDao.retrieve(id);
		//申购信息
		stockOptionTrade.setState(StockOptionTradeState.INSETTLEMENT);
		stockOptionTrade.setUpdateTime(new Date());
		StockOptionTrade result = stockOptionTradeDao.update(stockOptionTrade);
		return result;
	}

	@Transactional
	public StockOptionTrade fail(Long id) {
		StockOptionTrade trade = stockOptionTradeDao.retrieve(id);
		if (StockOptionTradeState.WAITCONFIRMED != trade.getState()) {
			throw new ServiceException(ExceptionConstant.STOCKOPTION_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION);
		}
		trade.setState(StockOptionTradeState.FAILURE);
		stockOptionTradeDao.update(trade);
		// 退回权利金
		accountBusiness.returnRightMoney(trade.getPublisherId(), trade.getId(), trade.getRightMoney());
		return trade;
	}

	private void sendOutsideMessage(StockOptionTrade trade) {
		try {
			StockOptionTradeState state = trade.getState();
			Map<String, String> extras = new HashMap<>();
			OutsideMessage message = new OutsideMessage();
			message.setPublisherId(trade.getPublisherId());
			message.setTitle("申购通知");
			extras.put("title", message.getTitle());
			extras.put("publisherId", String.valueOf(trade.getPublisherId()));
			extras.put("resourceType", ResourceType.STOCKOPTIONTRADE.getIndex());
			extras.put("resourceId", String.valueOf(trade.getId()));
			message.setExtras(extras);
			switch (state) {
			case WAITCONFIRMED:
				message.setContent(String.format("您申购的“%s %s”已进入“待确认”状态", trade.getStockName(), trade.getStockCode()));
				extras.put("content", String.format("您申购的“<span id=\"stock\">%s %s</span>”已进入“待确认”状态",
						trade.getStockName(), trade.getStockCode()));
				extras.put("type", OutsideMessageType.OPTION_WAITCONFIRMED.getIndex());
				break;
			case FAILURE:
				message.setContent(String.format("您申购的“%s %s”申购失败", trade.getStockName(), trade.getStockCode()));
				extras.put("content", String.format("您申购的“<span id=\"stock\">%s %s</span>”申购失败", trade.getStockName(),
						trade.getStockCode()));
				extras.put("type", OutsideMessageType.OPTION_FAILURE.getIndex());
				break;
			case TURNOVER:
				message.setContent(String.format("您申购的“%s %s”已进入“持仓中”状态", trade.getStockName(), trade.getStockCode()));
				extras.put("content", String.format("您申购的“<span id=\"stock\">%s %s</span>”已进入“持仓中”状态",
						trade.getStockName(), trade.getStockCode()));
				extras.put("type", OutsideMessageType.OPTION_TURNOVER.getIndex());
				break;
			case APPLYRIGHT:
				message.setContent(String.format("您申购的“%s %s”已进入“结算中”状态", trade.getStockName(), trade.getStockCode()));
				extras.put("content", String.format("您申购的“<span id=\"stock\">%s %s</span>”已进入“结算中”状态",
						trade.getStockName(), trade.getStockCode()));
				extras.put("type", OutsideMessageType.OPTION_INSETTLEMENT.getIndex());
				break;
			case SETTLEMENTED:
				message.setContent(String.format("您申购的“%s %s”已进入“已结算”状态", trade.getStockName(), trade.getStockCode()));
				extras.put("content", String.format("您申购的“<span id=\"stock\">%s %s</span>”已进入“已结算”状态",
						trade.getStockName(), trade.getStockCode()));
				extras.put("type", OutsideMessageType.OPTION_SETTLEMENTED.getIndex());
				break;
			default:
				break;
			}
			if (message.getContent() != null) {
				outsideMessageBusiness.send(message);
			}
		} catch (Exception ex) {
			logger.error("发送期权申购通知失败，{}_{}_{}", trade.getId(), trade.getState().getState(), ex.getMessage());
		}
	}

}
