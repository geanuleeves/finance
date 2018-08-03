package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.business.CapitalFlowBusiness;
import com.waben.stock.datalayer.futures.business.OrganizationBusiness;
import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.datalayer.futures.business.PublisherBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesOrderStateConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesWindControlTypeConverter;
import com.waben.stock.datalayer.futures.quote.QuoteContainer;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.EntrustQueryConsumer;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.MonitorStopLossOrProfitConsumer;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.MonitorStrongPointConsumer;
import com.waben.stock.datalayer.futures.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeActionDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeEntrustDao;
import com.waben.stock.datalayer.futures.repository.impl.MethodDesc;
import com.waben.stock.interfaces.commonapi.retrivefutures.TradeFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.AgentOrderRecordDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeActionAgentDto;
import com.waben.stock.interfaces.dto.futures.MarketAveragePrice;
import com.waben.stock.interfaces.dto.futures.TurnoverStatistyRecordDto;
import com.waben.stock.interfaces.dto.organization.FuturesAgentPriceDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.dto.publisher.FrozenCapitalDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.CapitalFlowExtendType;
import com.waben.stock.interfaces.enums.CapitalFlowType;
import com.waben.stock.interfaces.enums.FuturesActionType;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradeLimitType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.param.futures.PlaceOrderParam;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.StringUtil;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

/**
 * 期货订单 service
 *
 * @author sunl
 */
@Service
public class FuturesOrderService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private DynamicQuerySqlDao sqlDao;

	@Autowired
	private FuturesOrderDao orderDao;

	@Autowired
	private FuturesContractDao contractDao;

	@Autowired
	private FuturesContractOrderDao contractOrderDao;

	@Autowired
	private FuturesTradeEntrustDao tradeEntrustDao;

	@Autowired
	private FuturesTradeActionDao tradeActionDao;

	@Autowired
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private CapitalFlowBusiness flowBusiness;

	@Autowired
	private PublisherBusiness publisherBusiness;

	@Autowired
	private OrganizationBusiness orgBusiness;

	@Autowired
	private QuoteContainer allQuote;

	private MonitorStrongPointConsumer monitorPublisher;

	private MonitorStopLossOrProfitConsumer monitorOrder;

	@Autowired
	private EntrustQueryConsumer entrueQuery;

	@Autowired
	private ProfileBusiness profileBusiness;

	@Autowired
	private FuturesHolidayService futuresHolidayService;

	@Autowired
	private FuturesTradeLimitService futuresTradeLimitService;

	@Autowired
	private FuturesContractOrderDao futuresContractOrderDao;

	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Value("${order.domain:youguwang.com.cn}")
	private String domain;

	public List<Object> queryByState(List<Integer> state) {
		return orderDao.queryByState(state);
	}

	public FuturesOrder findById(Long id) {
		return orderDao.retrieve(id);
	}

	public List<FuturesOrder> findByContractId(List<Long> contractId) {
		return orderDao.findByContractId(contractId);
	}

	public List<FuturesOrder> findByContractTermId(List<Long> contractTermId) {
		return orderDao.findByContractTermId(contractTermId);
	}

	public Page<FuturesOrder> pagesOrderAdmin(final FuturesTradeAdminQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesOrder> pages = orderDao.page(new Specification<FuturesOrder>() {

			@Override
			public Predicate toPredicate(Root<FuturesOrder> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();

				if (query.getPublisherIds().size() > 0) {
					predicateList.add(criteriaBuilder.in(root.get("publisherId")).value(query.getPublisherIds()));
				}
				if (query.getOrderType() != null) {
					if (query.getOrderType().equals("1") || query.getOrderType().equals("2")) {
						predicateList.add(
								criteriaBuilder.equal(root.get("orderType").as(String.class), query.getOrderType()));

					}
				}
				if (query.getTradeNo() != null && !"".equals(query.getTradeNo())) {
					predicateList.add(criteriaBuilder.equal(root.get("tradeNo").as(String.class), query.getTradeNo()));
				}

				if (query.getSymbol() != null && !"".equals(query.getSymbol())) {
					predicateList.add(criteriaBuilder.like(root.get("commoditySymbol").as(String.class),
							"%" + query.getSymbol() + "%"));
				}
				if (query.getName() != null && !"".equals(query.getName())) {
					predicateList.add(criteriaBuilder.like(root.get("commodityName").as(String.class),
							"%" + query.getName() + "%"));
				}
				if (!StringUtil.isEmpty(query.getPriceType())) {
					FuturesTradePriceType type = FuturesTradePriceType.getByIndex(query.getPriceType());
					predicateList.add(
							criteriaBuilder.equal(root.get("buyingPriceType").as(FuturesTradePriceType.class), type));
				}

				if (query.getStartTime() != null && query.getEndTime() != null) {
					if (query.getQueryType() != null) {
						if (query.getQueryType() == 1 || query.getQueryType() == 0) {
							predicateList.add(criteriaBuilder.between(root.get("buyingTime").as(Date.class),
									query.getStartTime(), query.getEndTime()));
						} else if (query.getQueryType() == 2) {
							predicateList.add(criteriaBuilder.between(root.get("sellingTime").as(Date.class),
									query.getStartTime(), query.getEndTime()));
						} else if (query.getQueryType() == 3 || query.getQueryType() == 4) {
							predicateList.add(criteriaBuilder.between(root.get("postTime").as(Date.class),
									query.getStartTime(), query.getEndTime()));
						}
					}
				}

				if (query.getOrderState() != null) {
					FuturesOrderStateConverter convert = new FuturesOrderStateConverter();
					if (query.getOrderState().length() > 1) {
						String[] array = query.getOrderState().split(",");
						ArrayList<FuturesOrderState> list = new ArrayList<FuturesOrderState>();
						for (String temp : array) {
							list.add(convert.convertToEntityAttribute(Integer.valueOf(temp)));
						}
						predicateList.add(criteriaBuilder.in(root.get("state")).value(list));
					} else {
						predicateList
								.add(criteriaBuilder.equal(root.get("state").as(String.class), query.getOrderState()));
						if (query.getOrderState().equals("5")) {

						}
					}
				}

				if (query.getWindControlType() != null && !"".equals(query.getWindControlType())) {
					FuturesWindControlTypeConverter converter = new FuturesWindControlTypeConverter();
					if (query.getWindControlType().length() > 0) {
						String[] array = query.getWindControlType().split(",");
						ArrayList<FuturesWindControlType> list = new ArrayList<FuturesWindControlType>();
						for (String temp : array) {
							list.add(converter.convertToEntityAttribute(Integer.valueOf(temp)));
						}
						predicateList.add(criteriaBuilder.in(root.get("windControlType")).value(list));
					} else {
						FuturesWindControlType windType = converter
								.convertToEntityAttribute(Integer.valueOf(query.getWindControlType()));
						predicateList
								.add(criteriaBuilder.equal(root.get("windControlType").as(String.class), windType));
					}
				}

				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				if (!StringUtil.isEmpty(query.getOrderState())) {
					if (query.getOrderState().equals("6,9") || query.getOrderState().equals("6")
							|| query.getOrderState().equals("9")) {
						Order o = criteriaBuilder.desc(root.get("buyingTime").as(Date.class));
						criteriaQuery.orderBy(o);
					} else {
						Order o = criteriaBuilder.desc(root.get("postTime").as(Date.class));
						criteriaQuery.orderBy(o);
					}
				}
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public Page<FuturesOrderAdminDto> adminPagesByQuery(FuturesTradeAdminQuery query) {
		String publisherNameCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			publisherNameCondition = " and f2.phone like '%" + query.getPublisherPhone() + "%' ";
		}
		String publisherPhoneCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			publisherPhoneCondition = " and f3.name like '%" + query.getPublisherName() + "%' ";
		}
		String contractNameCondition = "";
		if (!StringUtil.isEmpty(query.getName())) {
			contractNameCondition = " and f4.name like '%" + query.getName() + "%'";
		}
		String orderTypeCondition = "";
		if (!StringUtil.isEmpty(query.getOrderType())) {
			orderTypeCondition = " and f1.orderType like '%" + query.getOrderType() + "%'";
		}
		String orderStateCondition = "";
		if (!StringUtil.isEmpty(query.getOrderState())) {
			orderStateCondition = " and f1.state like '%" + query.getOrderState() + "%'";
		}
		String sql = String.format(
				"select f1.id, f3.name,f2.phone,f4.name as cname,f1.trade_no, f1.open_gateway_order_id, f1.close_gateway_order_id, f1.order_type, f1.state, f1.total_quantity, f1.buying_time,"
						+ " f1.buying_price, f1.profit_or_loss, f1.openwind_service_fee, f1.reserve_fund, f1.per_unit_limit_profit_amount, f1.per_unit_limit_loss_amount,"
						+ " f1.selling_time, f1.selling_price, f1.unwind_service_fee, f1.wind_control_type"
						+ " from f_futures_order f1 " + " LEFT JOIN publisher f2 on f1.publisher_id = f2.id"
						+ " LEFT JOIN real_name f3 on f1.publisher_id = f3.resource_id"
						+ " LEFT JOIN f_futures_contract f4 ON f1.contract_id = f4.id"
						+ " where 1=1 %s %s %s %s %s ORDER BY f1.post_time LIMIT " + query.getPage() * query.getSize()
						+ "," + query.getSize(),
				publisherNameCondition, publisherPhoneCondition, contractNameCondition, orderStateCondition,
				orderTypeCondition);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("LIMIT"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setTradeNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setOpenGatewayOrderId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setCloseGatewayOrderId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setOrderType", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setState", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setTotalQuantity", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setBuyingTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setBuyingPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setProfit", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setOpenwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setReserveFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(15),
				new MethodDesc("setPerUnitLimitProfitAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(16),
				new MethodDesc("setPerUnitLimitLossAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setSellingTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setSellingPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setUnwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(20), new MethodDesc("setWindControlType", new Class<?>[] { String.class }));
		List<FuturesOrderAdminDto> content = sqlDao.execute(FuturesOrderAdminDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<FuturesOrder> pagesOrder(final FuturesOrderQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesOrder> pages = orderDao.page(new Specification<FuturesOrder>() {
			@Override
			public Predicate toPredicate(Root<FuturesOrder> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				Join<FuturesOrder, FuturesContract> join = root.join("contract", JoinType.LEFT).join("commodity",
						JoinType.LEFT);
				// 用户ID
				if (query.getPublisherId() != null && query.getPublisherId() != 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				// 订单ID
				if (query.getOrderId() != null && query.getOrderId() != 0) {
					predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), query.getOrderId()));
				}
				// 订单状态
				if (query.getStates() != null && query.getStates().length > 0) {
					predicateList.add(root.get("state").in(query.getStates()));
				}
				// 盈利了的交易
				// if (query.isOnlyProfit()) {
				// predicateList.add(criteriaBuilder.gt(root.get("publisherProfitOrLoss").as(BigDecimal.class),
				// new BigDecimal(0)));
				// }
				// 交易动态过滤已到期数据
				if (query.isExpire()) {
					Join<FuturesOrder, FuturesContract> contractJoin = root.join("contract", JoinType.LEFT);
					Predicate expirationDate = criteriaBuilder
							.greaterThan(contractJoin.get("expirationDate").as(Date.class), new Date());
					predicateList.add(criteriaBuilder.and(expirationDate));
				}
				// 合约名称
				if (!StringUtil.isEmpty(query.getContractName())) {
					Predicate contractName = criteriaBuilder.like(join.get("name").as(String.class),
							"%" + query.getContractName() + "%");
					predicateList.add(criteriaBuilder.and(contractName));
				}
				// 起始日期
				if (query.getStartBuyingTime() != null) {
					Predicate stateTime = criteriaBuilder.greaterThanOrEqualTo(root.get("openTradeTime").as(Date.class),
							query.getStartBuyingTime());
					predicateList.add(criteriaBuilder.and(stateTime));
				}
				// 结束日期
				if (query.getEndBuyingTime() != null) {
					Predicate endTime = criteriaBuilder.lessThan(root.get("openTradeTime").as(Date.class),
							query.getEndBuyingTime());
					predicateList.add(criteriaBuilder.and(endTime));
				}
				// 是否测试单
				if (query.getIsTest() != null) {
					Predicate isTestPredicate = criteriaBuilder.equal(root.get("isTest").as(Boolean.class),
							query.getIsTest());
					Predicate isTestNullPredicate = criteriaBuilder.isNull(root.get("isTest").as(Boolean.class));
					if (query.getIsTest()) {
						predicateList.add(isTestPredicate);
					} else {
						predicateList.add(criteriaBuilder.or(isTestPredicate, isTestNullPredicate));
					}
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				FuturesOrderState[] unwindStates = { FuturesOrderState.Unwind, FuturesOrderState.BuyingCanceled,
						FuturesOrderState.BuyingFailure };
				FuturesOrderState[] wtStates = { FuturesOrderState.BuyingEntrust, FuturesOrderState.BuyingCanceled,
						FuturesOrderState.BuyingFailure, FuturesOrderState.PartPosition, FuturesOrderState.Position,
						FuturesOrderState.SellingEntrust, FuturesOrderState.PartUnwind, FuturesOrderState.Unwind };
				FuturesOrderState[] positionStates = { FuturesOrderState.Position };
				FuturesOrderState[] monitorPositionStates = { FuturesOrderState.Position,
						FuturesOrderState.SellingEntrust };
				if (query.getStates() != null) {
					if (orderStateArrToString(query.getStates()).equals(orderStateArrToString(unwindStates))) {
						List<Order> orderList = new ArrayList<Order>();
						orderList.add(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
						criteriaQuery.orderBy(orderList);
					} else if (orderStateArrToString(query.getStates()).equals(orderStateArrToString(wtStates))) {
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("postTime").as(Date.class)));
					} else if (orderStateArrToString(query.getStates()).equals(orderStateArrToString(positionStates))) {
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("openTradeTime").as(Date.class)));
					} else if (orderStateArrToString(query.getStates())
							.equals(orderStateArrToString(monitorPositionStates))) {
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("openTradeTime").as(Date.class)));
					}
				}
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	private String orderStateArrToString(FuturesOrderState[] states) {
		StringBuilder strBuilder = new StringBuilder();
		if (states != null) {
			for (FuturesOrderState state : states) {
				strBuilder.append(state.name());
			}
		}
		return strBuilder.toString();
	}

	@Transactional
	public FuturesTradeEntrust placeOrder(PlaceOrderParam orderParam) {
		// step 1 : 检查网关是否正常
		boolean isConnected = TradeFuturesOverHttp.checkConnection(profileBusiness.isProd());
		if (!isConnected) {
			throw new ServiceException(ExceptionConstant.FUTURESAPI_NOTCONNECTED_EXCEPTION);
		}
		// step 2 : 再一次确认余额是否充足
		CapitalAccountDto capitalAccount = accountBusiness.fetchByPublisherId(orderParam.getPublisherId());
		BigDecimal totalFee = orderParam.getServiceFee().add(orderParam.getReserveFund());
		if (totalFee.compareTo(capitalAccount.getAvailableBalance()) > 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		// step 3 : 更新合约订单信息
		Long contractId = orderParam.getContractId();
		FuturesOrderType orderType = orderParam.getOrderType();
		BigDecimal totalQuantity = orderParam.getTotalQuantity();
		FuturesContract contract = contractDao.retrieve(contractId);
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract,
				orderParam.getPublisherId());
		if (contractOrder != null) {
			if (orderType == FuturesOrderType.BuyUp) {
				contractOrder.setBuyUpTotalQuantity(contractOrder.getBuyUpTotalQuantity().add(totalQuantity));
			} else {
				contractOrder.setBuyFallTotalQuantity(contractOrder.getBuyFallTotalQuantity().add(totalQuantity));
			}
			contractOrder.setReserveFund(contractOrder.getReserveFund().add(orderParam.getReserveFund()));
			if (orderParam.getLimitLossType() != null && orderParam.getLimitLossType() > 0
					&& orderParam.getPerUnitLimitLossAmount() != null
					&& orderParam.getPerUnitLimitLossAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (orderParam.getOrderType() == FuturesOrderType.BuyUp) {
					contractOrder.setBuyUpLimitLossType(orderParam.getLimitLossType());
					contractOrder.setBuyUpPerUnitLimitLossAmount(orderParam.getPerUnitLimitLossAmount());
				} else {
					contractOrder.setBuyFallLimitLossType(orderParam.getLimitLossType());
					contractOrder.setBuyFallPerUnitLimitLossAmount(orderParam.getPerUnitLimitLossAmount());
				}
			}
			if (orderParam.getLimitProfitType() != null && orderParam.getLimitProfitType() > 0
					&& orderParam.getPerUnitLimitProfitAmount() != null
					&& orderParam.getPerUnitLimitProfitAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (orderParam.getOrderType() == FuturesOrderType.BuyUp) {
					contractOrder.setBuyUpLimitProfitType(orderParam.getLimitProfitType());
					contractOrder.setBuyUpPerUnitLimitProfitAmount(orderParam.getPerUnitLimitProfitAmount());
				} else {
					contractOrder.setBuyFallLimitProfitType(orderParam.getLimitProfitType());
					contractOrder.setBuyFallPerUnitLimitProfitAmount(orderParam.getPerUnitLimitProfitAmount());
				}
			}
			contractOrder.setUpdateTime(new Date());
			contractOrderDao.doUpdate(contractOrder);
		} else {
			contractOrder = new FuturesContractOrder();
			contractOrder.setBuyFallQuantity(BigDecimal.ZERO);
			contractOrder.setBuyUpQuantity(BigDecimal.ZERO);
			contractOrder.setBuyUpCanUnwindQuantity(BigDecimal.ZERO);
			contractOrder.setBuyFallCanUnwindQuantity(BigDecimal.ZERO);
			if (orderType == FuturesOrderType.BuyUp) {
				contractOrder.setBuyUpTotalQuantity(totalQuantity);
				contractOrder.setBuyFallTotalQuantity(BigDecimal.ZERO);
			} else {
				contractOrder.setBuyFallTotalQuantity(totalQuantity);
				contractOrder.setBuyUpTotalQuantity(BigDecimal.ZERO);
			}
			contractOrder.setContract(contract);
			contractOrder.setCommodityNo(contract.getCommodity().getSymbol());
			contractOrder.setCommodityName(contract.getCommodity().getName());
			contractOrder.setContractNo(contract.getContractNo());
			contractOrder.setLightQuantity(BigDecimal.ZERO);
			contractOrder.setPublisherId(orderParam.getPublisherId());
			contractOrder.setReserveFund(orderParam.getReserveFund());
			if (orderParam.getLimitLossType() != null && orderParam.getLimitLossType() > 0
					&& orderParam.getPerUnitLimitLossAmount() != null
					&& orderParam.getPerUnitLimitLossAmount().compareTo(BigDecimal.ZERO) > 0) {
				orderParam.setLimitLossType(orderParam.getLimitLossType());
				orderParam.setPerUnitLimitLossAmount(orderParam.getPerUnitLimitLossAmount());
			}
			if (orderParam.getLimitProfitType() != null && orderParam.getLimitProfitType() > 0
					&& orderParam.getPerUnitLimitProfitAmount() != null
					&& orderParam.getPerUnitLimitProfitAmount().compareTo(BigDecimal.ZERO) > 0) {
				orderParam.setLimitProfitType(orderParam.getLimitProfitType());
				orderParam.setPerUnitLimitProfitAmount(orderParam.getPerUnitLimitProfitAmount());
			}
			contractOrder.setUpdateTime(new Date());
			contractOrderDao.create(contractOrder);
		}
		// step 4 : 创建订单信息
		Date date = new Date();
		FuturesOrder order = new FuturesOrder();
		order.setCloseAvgFillPrice(BigDecimal.ZERO);
		order.setCloseFilled(BigDecimal.ZERO);
		order.setCloseRemaining(BigDecimal.ZERO);
		order.setCloseTotalFillCost(BigDecimal.ZERO);
		order.setCommodityCurrency(orderParam.getCommodityCurrency());
		order.setCommodityName(orderParam.getCommodityName());
		order.setCommoditySymbol(orderParam.getCommoditySymbol());
		order.setContract(contract);
		order.setContractNo(orderParam.getContractNo());
		order.setContractOrder(contractOrder);
		order.setIsTest(orderParam.getIsTest());
		order.setOpenAvgFillPrice(BigDecimal.ZERO);
		order.setOpenFilled(BigDecimal.ZERO);
		order.setOpenRemaining(orderParam.getTotalQuantity());
		order.setOpenTotalFillCost(BigDecimal.ZERO);
		order.setOpenwindServiceFee(orderParam.getOpenwindServiceFee());
		order.setOrderType(orderParam.getOrderType());
		order.setPostTime(new Date());
		order.setPublisherId(orderParam.getPublisherId());
		order.setReserveFund(orderParam.getReserveFund());
		order.setServiceFee(orderParam.getServiceFee());
		order.setState(FuturesOrderState.BuyingEntrust);
		order.setTotalQuantity(orderParam.getTotalQuantity());
		order.setTradeNo(UniqueCodeGenerator.generateTradeNo());
		order.setUnwindServiceFee(orderParam.getUnwindServiceFee());
		order.setUpdateTime(date);
		orderDao.create(order);
		// step 5 : 创建委托记录
		FuturesTradeEntrust tradeEntrust = new FuturesTradeEntrust();
		tradeEntrust.setEntrustNo(UniqueCodeGenerator.generateTradeNo());
		tradeEntrust.setCommodityNo(orderParam.getCommoditySymbol());
		tradeEntrust.setContract(contract);
		tradeEntrust.setContractNo(orderParam.getContractNo());
		if (orderParam.getTradePriceType() == FuturesTradePriceType.MKT) {
			orderParam.setEntrustPrice(
					allQuote.getLastPrice(orderParam.getCommoditySymbol(), orderParam.getContractNo()));
		}
		tradeEntrust.setEntrustPrice(orderParam.getEntrustPrice());
		tradeEntrust.setEntrustTime(date);
		tradeEntrust.setOrderType(orderParam.getOrderType());
		tradeEntrust.setPriceType(orderParam.getTradePriceType());
		tradeEntrust.setPublisherId(orderParam.getPublisherId());
		tradeEntrust.setQuantity(orderParam.getTotalQuantity());
		tradeEntrust.setRemaining(orderParam.getTotalQuantity());
		tradeEntrust.setReserveFund(orderParam.getReserveFund());
		tradeEntrust.setFilled(BigDecimal.ZERO);
		tradeEntrust.setAvgFillPrice(BigDecimal.ZERO);
		tradeEntrust.setTotalFillCost(BigDecimal.ZERO);
		tradeEntrust.setState(FuturesTradeEntrustState.Queuing);
		tradeEntrust.setTradeActionType(FuturesTradeActionType.OPEN);
		tradeEntrust.setTradePrice(BigDecimal.ZERO);
		tradeEntrust.setBackhandEntrustId(orderParam.getBackhandEntrustId());
		tradeEntrust.setUpdateTime(date);
		tradeEntrustDao.create(tradeEntrust);
		// step 6 : 创建开仓记录
		FuturesTradeAction tradeAction = new FuturesTradeAction();
		tradeAction.setAvgFillPrice(BigDecimal.ZERO);
		tradeAction.setCurrencyProfitOrLoss(BigDecimal.ZERO);
		tradeAction.setEntrustTime(date);
		tradeAction.setFilled(BigDecimal.ZERO);
		tradeAction.setOrder(order);
		tradeAction.setPlatformProfitOrLoss(BigDecimal.ZERO);
		tradeAction.setProfitOrLoss(BigDecimal.ZERO);
		tradeAction.setPublisherId(orderParam.getPublisherId());
		tradeAction.setPublisherProfitOrLoss(BigDecimal.ZERO);
		tradeAction.setQuantity(orderParam.getTotalQuantity());
		tradeAction.setRemaining(orderParam.getTotalQuantity());
		tradeAction.setSettlementRate(BigDecimal.ZERO);
		tradeAction.setSort(1);
		tradeAction.setActionNo(UniqueCodeGenerator.generateTradeNo());
		tradeAction.setState(FuturesTradeEntrustState.Queuing);
		tradeAction.setTotalFillCost(BigDecimal.ZERO);
		tradeAction.setTradeActionType(FuturesTradeActionType.OPEN);
		tradeAction.setTradeEntrust(tradeEntrust);
		tradeAction.setTradePrice(BigDecimal.ZERO);
		tradeAction.setUpdateTime(date);
		tradeActionDao.create(tradeAction);
		// step 7 : 扣去金额、冻结保证金
		try {
			accountBusiness.futuresOrderServiceFeeAndReserveFund(order.getPublisherId(), order.getId(),
					order.getServiceFee(), order.getReserveFund());
		} catch (ServiceException ex) {
			if (ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION.equals(ex.getType())) {
				throw ex;
			} else {
				// 再一次确认是否已经扣款
				try {
					FrozenCapitalDto frozen = accountBusiness.futuresOrderFetchFrozenCapital(order.getPublisherId(),
							order.getId());
					if (frozen == null) {
						throw ex;
					}
				} catch (ServiceException frozenEx) {
					throw ex;
				}
			}
		}
		// step 8 : 放入委托查询队列（开仓）
		entrueQuery.entrustQuery(tradeEntrust.getId(), 1, false, null);
		return tradeEntrust;
	}

	@Transactional
	public FuturesTradeEntrust backhandPlaceOrder(Long entrustId) {
		logger.info("反手下单，源委托ID：{}", entrustId);
		FuturesTradeEntrust originEntrust = tradeEntrustDao.retrieve(entrustId);
		if (originEntrust.getState() != FuturesTradeEntrustState.Success) {
			throw new ServiceException(ExceptionConstant.BACKHANDSOURCEORDER_NOTUNWIND_EXCEPTION);
		}
		List<FuturesTradeEntrust> checkEntrust = tradeEntrustDao.retrieveByBackhandEntrustId(entrustId);
		if (checkEntrust != null && checkEntrust.size() > 0) {
			throw new ServiceException(ExceptionConstant.FUTURESORDER_ALREADYBACKHAND_EXCEPTION);
		}
		FuturesOrderType backhandOrderType = (originEntrust.getOrderType() == FuturesOrderType.BuyUp)
				? FuturesOrderType.BuyFall : FuturesOrderType.BuyUp;
		// step 1 : 检查合约信息
		FuturesContract contract = originEntrust.getContract();
		Integer timeZoneGap = contract.getCommodity().getExchange().getTimeZoneGap();
		if (!isTradeTime(timeZoneGap, contract, FuturesTradeActionType.OPEN)) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		// step 2 : 根据最后交易日和首次通知日判断是否可以下单，可以下单计算公式：MIN（最后交易日，首次通知日）> 当前日期
		Long checkLastTrade = 0L;
		if (contract.getFirstNoticeDate() != null) {
			checkLastTrade = contract.getFirstNoticeDate().getTime();
		}
		if (contract.getLastTradingDate() != null) {
			checkLastTrade = checkLastTrade == 0 ? contract.getLastTradingDate().getTime()
					: Math.min(checkLastTrade, contract.getLastTradingDate().getTime());
		}
		if (checkLastTrade > 0) {
			Date exchangeTime = timeZoneGap == null ? new Date() : retriveExchangeTime(new Date(), timeZoneGap);
			if (checkLastTrade.compareTo(exchangeTime.getTime()) < 0) {
				throw new ServiceException(ExceptionConstant.MIN_PLACE_ORDER_EXCEPTION);
			}
		}
		// step 3 : 检查下单数量
		BigDecimal perNum = contract.getPerOrderLimit();
		BigDecimal userMaxNum = contract.getUserTotalLimit();
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract,
				originEntrust.getPublisherId());
		BigDecimal buyUpNum = BigDecimal.ZERO;
		BigDecimal buyFallNum = BigDecimal.ZERO;
		BigDecimal alreadyReserveFund = BigDecimal.ZERO;
		if (contractOrder != null) {
			buyUpNum = contractOrder.getBuyUpTotalQuantity();
			buyFallNum = contractOrder.getBuyFallTotalQuantity();
			alreadyReserveFund = contractOrder.getReserveFund();
		}
		BigDecimal backhandQuantity = originEntrust.getQuantity();
		checkBuyUpAndFullSum(buyUpNum, buyFallNum, perNum, userMaxNum, backhandOrderType, backhandQuantity, contract);
		// step 5 : 计算总费用，总保证金=单边最大手数*一手保证金
		BigDecimal totalFee = new BigDecimal(0);
		BigDecimal singleEdgeMax = BigDecimal.ZERO;
		if (backhandOrderType == FuturesOrderType.BuyUp) {
			BigDecimal preBuyUpNum = backhandQuantity.add(buyUpNum);
			singleEdgeMax = preBuyUpNum.compareTo(buyFallNum) >= 0 ? preBuyUpNum : buyFallNum;
		} else {
			BigDecimal prebuyFallNum = backhandQuantity.add(buyFallNum);
			singleEdgeMax = prebuyFallNum.compareTo(buyUpNum) >= 0 ? prebuyFallNum : buyUpNum;
		}
		BigDecimal totalReserveFund = contract.getCommodity().getPerUnitReserveFund().multiply(singleEdgeMax);
		BigDecimal reserveFund = totalReserveFund.compareTo(alreadyReserveFund) > 0
				? totalReserveFund.subtract(alreadyReserveFund) : BigDecimal.ZERO;
		BigDecimal serviceFee = contract.getCommodity().getOpenwindServiceFee()
				.add(contract.getCommodity().getUnwindServiceFee()).multiply(backhandQuantity);
		totalFee = reserveFund.add(serviceFee);
		// step 6 : 检查余额
		CapitalAccountDto capitalAccount = accountBusiness.fetchByPublisherId(originEntrust.getPublisherId());
		if (totalFee.compareTo(capitalAccount.getAvailableBalance()) > 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		BigDecimal unsettledProfitOrLoss = this.getUnsettledProfitOrLoss(originEntrust.getPublisherId());
		if (unsettledProfitOrLoss != null && unsettledProfitOrLoss.compareTo(BigDecimal.ZERO) < 0) {
			if (totalFee.add(unsettledProfitOrLoss.abs()).compareTo(capitalAccount.getAvailableBalance()) > 0) {
				throw new ServiceException(ExceptionConstant.HOLDINGLOSS_LEADTO_NOTENOUGH_EXCEPTION);
			}
		}
		// step 7 : 组装请求参数，请求下单
		PlaceOrderParam orderParam = new PlaceOrderParam();
		orderParam.setPublisherId(originEntrust.getPublisherId());
		orderParam.setOrderType(backhandOrderType);
		orderParam.setContractId(contract.getId());
		orderParam.setTotalQuantity(backhandQuantity);
		orderParam.setReserveFund(reserveFund);
		orderParam.setServiceFee(serviceFee);
		orderParam.setCommoditySymbol(contract.getCommodity().getSymbol());
		orderParam.setCommodityName((contract.getCommodity().getName()));
		orderParam.setCommodityCurrency(contract.getCommodity().getCurrency());
		orderParam.setContractNo(contract.getContractNo());
		orderParam.setOpenwindServiceFee(contract.getCommodity().getOpenwindServiceFee());
		orderParam.setUnwindServiceFee(contract.getCommodity().getUnwindServiceFee());
		orderParam.setTradePriceType(FuturesTradePriceType.MKT);
		PublisherDto publisher = publisherBusiness.findById(originEntrust.getPublisherId());
		orderParam.setIsTest(publisher.getIsTest());
		orderParam.setBackhandEntrustId(entrustId);
		return this.placeOrder(orderParam);
	}

	/**
	 * 判断当前下单手数是否满足条件
	 * 
	 * @param buyUpNum
	 *            买涨数量
	 * @param buyFullNum
	 *            买跌数量
	 * @param perNum
	 *            用户单笔最大可交易数量
	 * @param userMaxNum
	 *            用户最大可持仓量
	 * @param buysellDto
	 *            当前订单详情
	 * @param buysellDto
	 *            当前合约详情
	 */
	public void checkBuyUpAndFullSum(BigDecimal buyUpNum, BigDecimal buyFallNum, BigDecimal perNum,
			BigDecimal userMaxNum, FuturesOrderType orderType, BigDecimal quantity, FuturesContract contract) {
		BigDecimal buyUpTotal = BigDecimal.ZERO;
		BigDecimal buyFallTotal = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			buyUpTotal = buyUpNum.add(quantity);
			if (contract.getBuyUpTotalLimit() != null && buyUpTotal.compareTo(contract.getBuyUpTotalLimit()) > 0) {
				// 买涨持仓总额度已达上限
				throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYUP_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
		} else {
			buyFallTotal = buyFallNum.add(quantity);
			if (contract.getBuyFullTotalLimit() != null
					&& buyFallTotal.compareTo(contract.getBuyFullTotalLimit()) > 0) {
				// 买跌持仓总额度已达上限
				throw new ServiceException(ExceptionConstant.TOTAL_AMOUNT_BUYFULL_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
		}
		if (perNum != null && quantity.compareTo(perNum) > 0) {
			// 单笔交易数量过大
			throw new ServiceException(ExceptionConstant.SINGLE_TRANSACTION_QUANTITY_EXCEPTION);
		}
		if (userMaxNum != null) {
			if (perNum.compareTo(userMaxNum) > 0) {
				// 用户单笔交易数量大于用户持仓总量
				throw new ServiceException(ExceptionConstant.CONTRACT_HOLDING_CAPACITY_INSUFFICIENT_EXCEPTION);
			}
			if (buyUpTotal.abs().compareTo(userMaxNum) > 0 || buyFallTotal.compareTo(userMaxNum) > 0) {
				// 该用户持仓量已达上限
				throw new ServiceException(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION);
			}
			if (buyUpTotal.add(buyFallTotal).compareTo(userMaxNum) > 0) {
				// 该用户持仓量已达上限
				throw new ServiceException(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION);
			}
		}
	}

	@Transactional(propagation = Propagation.REQUIRED)
	public void doUnwind(FuturesContract contract, FuturesContractOrder contractOrder, FuturesOrderType orderType,
			BigDecimal quantity, FuturesTradePriceType priceType, BigDecimal entrustPrice, Long publisherId,
			FuturesWindControlType windControlType, boolean isBackhand, boolean isStopLossOrProfit,
			BigDecimal stopLossOrProfitPrice) {
		Date date = new Date();
		// step 1 : 创建平仓委托
		FuturesTradeEntrust tradeEntrust = new FuturesTradeEntrust();
		tradeEntrust.setEntrustNo(UniqueCodeGenerator.generateTradeNo());
		tradeEntrust.setCommodityNo(contract.getCommodity().getSymbol());
		tradeEntrust.setContract(contract);
		tradeEntrust.setContractNo(contract.getContractNo());
		tradeEntrust.setEntrustPrice(entrustPrice);
		if (priceType == FuturesTradePriceType.MKT) {
			tradeEntrust.setEntrustPrice(
					allQuote.getLastPrice(contract.getCommodity().getSymbol(), contract.getContractNo()));
		}
		tradeEntrust.setEntrustTime(date);
		tradeEntrust.setOrderType(orderType);
		tradeEntrust.setPriceType(priceType);
		tradeEntrust.setPublisherId(publisherId);
		tradeEntrust.setQuantity(quantity);
		tradeEntrust.setWindControlType(windControlType);
		tradeEntrust.setRemaining(quantity);
		tradeEntrust.setFilled(BigDecimal.ZERO);
		tradeEntrust.setAvgFillPrice(BigDecimal.ZERO);
		tradeEntrust.setTotalFillCost(BigDecimal.ZERO);
		tradeEntrust.setState(FuturesTradeEntrustState.Queuing);
		tradeEntrust.setTradeActionType(FuturesTradeActionType.CLOSE);
		tradeEntrust.setTradePrice(BigDecimal.ZERO);
		tradeEntrust.setUpdateTime(date);
		tradeEntrustDao.create(tradeEntrust);
		// step 2 : 创建平仓记录，更新订单信息
		List<FuturesOrder> orderList = orderDao.retrieveByContractOrder(contractOrder);
		BigDecimal entrustQuantity = quantity;
		if (orderList != null && orderList.size() > 0) {
			int sort = 1;
			for (FuturesOrder order : orderList) {
				if (order.getOrderType() != orderType || !(order.getState() == FuturesOrderState.Position
						|| order.getState() == FuturesOrderState.PartUnwind
						|| order.getState() == FuturesOrderState.SellingEntrust)) {
					continue;
				}
				// step 2.1 : 获取可平仓数量
				BigDecimal availableQuantity = order.getTotalQuantity();
				List<FuturesTradeAction> actionList = tradeActionDao.retrieveByOrder(order);
				for (FuturesTradeAction action : actionList) {
					if ((action.getState() == FuturesTradeEntrustState.Queuing
							|| action.getState() == FuturesTradeEntrustState.PartSuccess
							|| action.getState() == FuturesTradeEntrustState.Success)
							&& action.getTradeActionType() == FuturesTradeActionType.CLOSE) {
						availableQuantity = availableQuantity.subtract(action.getQuantity());
					}
				}
				if (availableQuantity.compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}
				if (availableQuantity.compareTo(entrustQuantity) >= 0) {
					availableQuantity = entrustQuantity;
					entrustQuantity = BigDecimal.ZERO;
				} else {
					entrustQuantity = entrustQuantity.subtract(availableQuantity);
				}
				// step 2.2 : 创建平仓记录
				FuturesTradeAction tradeAction = new FuturesTradeAction();
				tradeAction.setAvgFillPrice(BigDecimal.ZERO);
				tradeAction.setCurrencyProfitOrLoss(BigDecimal.ZERO);
				tradeAction.setEntrustTime(date);
				tradeAction.setFilled(BigDecimal.ZERO);
				tradeAction.setOrder(order);
				tradeAction.setPlatformProfitOrLoss(BigDecimal.ZERO);
				tradeAction.setProfitOrLoss(BigDecimal.ZERO);
				tradeAction.setPublisherId(publisherId);
				tradeAction.setPublisherProfitOrLoss(BigDecimal.ZERO);
				tradeAction.setQuantity(availableQuantity);
				tradeAction.setRemaining(availableQuantity);
				tradeAction.setSettlementRate(BigDecimal.ZERO);
				tradeAction.setActionNo(UniqueCodeGenerator.generateTradeNo());
				tradeAction.setWindControlType(windControlType);
				tradeAction.setSort(sort++);
				tradeAction.setState(FuturesTradeEntrustState.Queuing);
				tradeAction.setTotalFillCost(BigDecimal.ZERO);
				tradeAction.setTradeActionType(FuturesTradeActionType.CLOSE);
				tradeAction.setTradeEntrust(tradeEntrust);
				tradeAction.setTradePrice(BigDecimal.ZERO);
				tradeAction.setUpdateTime(date);
				tradeActionDao.create(tradeAction);
				// step 2.3 : 更新订单信息
				order.setCloseRemaining(order.getCloseRemaining().add(availableQuantity));
				order.setState(FuturesOrderState.SellingEntrust);
				order.setUpdateTime(date);
				orderDao.update(order);
			}
		}
		// step 3 : 更新合约订单
		if (entrustQuantity.compareTo(BigDecimal.ZERO) > 0) {
			throw new ServiceException(ExceptionConstant.UNWINDQUANTITY_NOTENOUGH_EXCEPTION);
		}
		if (orderType == FuturesOrderType.BuyUp) {
			contractOrder.setBuyUpCanUnwindQuantity(contractOrder.getBuyUpCanUnwindQuantity().subtract(quantity));
		} else {
			contractOrder.setBuyFallCanUnwindQuantity(contractOrder.getBuyFallCanUnwindQuantity().subtract(quantity));
		}
		contractOrder.setUpdateTime(date);
		contractOrderDao.doUpdate(contractOrder);
		// step 4 : 放入委托查询队列（平仓）
		if (isBackhand) {
			entrueQuery.entrustQuery(tradeEntrust.getId(), 3, isStopLossOrProfit, stopLossOrProfitPrice);
		} else {
			entrueQuery.entrustQuery(tradeEntrust.getId(), 2, isStopLossOrProfit, stopLossOrProfitPrice);
		}
	}

	/**
	 * 用户申请平仓
	 * 
	 * @param contractId
	 *            合约ID
	 * @param orderType
	 *            订单类型
	 * @param priceType
	 *            价格类型
	 * @param entrustPrice
	 *            委托价格
	 * @param publisherId
	 *            用户ID
	 */
	@Transactional
	public void applyUnwind(Long contractId, FuturesOrderType orderType, FuturesTradePriceType priceType,
			BigDecimal entrustPrice, Long publisherId) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		if (contractOrder == null) {
			return;
		}
		BigDecimal quantity = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			quantity = contractOrder.getBuyUpCanUnwindQuantity();
		} else {
			quantity = contractOrder.getBuyFallCanUnwindQuantity();
		}
		if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		doUnwind(contract, contractOrder, orderType, quantity, priceType, entrustPrice, publisherId,
				FuturesWindControlType.UserApplyUnwind, false, false, null);
	}

	@Transactional
	public void applyUnwindAll(Long publisherId) {
		List<FuturesContractOrder> contractOrderList = contractOrderDao.retrieveByPublisherId(publisherId);
		if (contractOrderList != null && contractOrderList.size() > 0) {
			for (FuturesContractOrder contractOrder : contractOrderList) {
				FuturesContract contract = contractOrder.getContract();
				BigDecimal buyUpQuantity = contractOrder.getBuyUpCanUnwindQuantity();
				BigDecimal buyFallQuantity = contractOrder.getBuyFallCanUnwindQuantity();
				if (buyUpQuantity.compareTo(BigDecimal.ZERO) > 0) {
					doUnwind(contract, contractOrder, FuturesOrderType.BuyUp, buyUpQuantity, FuturesTradePriceType.MKT,
							null, publisherId, FuturesWindControlType.UserApplyUnwind, false, false, null);
				}
				if (buyFallQuantity.compareTo(BigDecimal.ZERO) > 0) {
					doUnwind(contract, contractOrder, FuturesOrderType.BuyFall, buyFallQuantity,
							FuturesTradePriceType.MKT, null, publisherId, FuturesWindControlType.UserApplyUnwind, false,
							false, null);
				}
			}
		}
	}

	@Transactional
	public void backhandUnwind(Long contractId, FuturesOrderType orderType, FuturesTradePriceType priceType,
			BigDecimal entrustPrice, Long publisherId) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		if (contractOrder == null) {
			return;
		}
		BigDecimal quantity = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			quantity = contractOrder.getBuyUpCanUnwindQuantity();
		} else {
			quantity = contractOrder.getBuyFallCanUnwindQuantity();
		}
		if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		doUnwind(contract, contractOrder, orderType, quantity, priceType, entrustPrice, publisherId,
				FuturesWindControlType.BackhandUnwind, true, false, null);
	}

	@Transactional
	public void balanceUnwind(Long contractId, FuturesOrderType orderType, FuturesTradePriceType sellingPriceType,
			BigDecimal sellingEntrustPrice, Long publisherId, BigDecimal quantity) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		if (contractOrder == null) {
			return;
		}
		BigDecimal canQuantity = BigDecimal.ZERO;
		if (orderType == FuturesOrderType.BuyUp) {
			canQuantity = contractOrder.getBuyUpCanUnwindQuantity();
		} else {
			canQuantity = contractOrder.getBuyFallCanUnwindQuantity();
		}
		if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}
		if (canQuantity.compareTo(quantity) < 0) {
			throw new ServiceException(ExceptionConstant.UNWINDQUANTITY_NOTENOUGH_EXCEPTION);
		}
		doUnwind(contract, contractOrder, orderType, quantity, FuturesTradePriceType.MKT, null, publisherId,
				FuturesWindControlType.UserApplyUnwind, false, false, null);
	}

	public void settingProfitAndLossLimit(Long publisherId, Long contractId, FuturesOrderType orderType,
			Integer limitProfitType, BigDecimal perUnitLimitProfitAmount, Integer limitLossType,
			BigDecimal perUnitLimitLossAmount) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if (contract == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		FuturesContractOrder contractOrder = contractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		if (contractOrder == null) {
			return;
		}
		if (orderType == FuturesOrderType.BuyUp) {
			contractOrder.setBuyUpLimitLossType(limitLossType);
			contractOrder.setBuyUpLimitProfitType(limitProfitType);
			contractOrder.setBuyUpPerUnitLimitLossAmount(perUnitLimitLossAmount);
			contractOrder.setBuyUpPerUnitLimitProfitAmount(perUnitLimitProfitAmount);
		} else {
			contractOrder.setBuyFallLimitLossType(limitLossType);
			contractOrder.setBuyFallLimitProfitType(limitProfitType);
			contractOrder.setBuyFallPerUnitLimitLossAmount(perUnitLimitLossAmount);
			contractOrder.setBuyFallPerUnitLimitProfitAmount(perUnitLimitProfitAmount);
		}
		contractOrder.setUpdateTime(new Date());
		contractOrderDao.doUpdate(contractOrder);
	}

	/**************************************** 分割线 ************************************************/

	/**
	 * 禁止开仓
	 *
	 * @param limitList
	 *            期货交易限制列表
	 * @param exchangeTime
	 *            当前时间
	 */
	public void checkedLimitOpenwind(List<FuturesTradeLimit> limitList, Date exchangeTime) {
		String fullStr = fullSdf.format(exchangeTime);
		for (FuturesTradeLimit limit : limitList) {
			if (limit.getEnable()) {
				if (limit.getLimitType() == FuturesTradeLimitType.LimitOpenwind) {
					if (fullStr.compareTo(limit.getStartLimitTime()) >= 0
							&& fullStr.compareTo(limit.getEndLimitTime()) < 0) {
						throw new ServiceException(ExceptionConstant.NOT_OPEN_GRANARY_PROVIDE_RELIEF_EXCEPTION);
					}
				}
			}
		}

		/*
		 * String dayStr = sdf.format(exchangeTime); String fullStr =
		 * fullSdf.format(exchangeTime); Calendar cal = Calendar.getInstance();
		 * cal.setTime(exchangeTime); Integer week =
		 * cal.get(Calendar.DAY_OF_WEEK); for (FuturesTradeLimit limit :
		 * limitList) { if (limit.getEnable()) { if (limit.getLimitType() ==
		 * FuturesTradeLimitType.LimitOpenwind) { if (limit.getWeekDay() ==
		 * week) { if (fullStr.compareTo(dayStr + " " +
		 * limit.getStartLimitTime()) >= 0 && fullStr.compareTo(dayStr + " " +
		 * limit.getEndLimitTime()) < 0) { throw new
		 * ServiceException(ExceptionConstant.
		 * NOT_OPEN_GRANARY_PROVIDE_RELIEF_EXCEPTION); } } } } }
		 */
	}

	/**
	 * 禁止平仓（当前时间是否禁止平仓）
	 *
	 * @param limitList
	 *            期货交易限制列表
	 * @param exchangeTime
	 *            当前时间
	 */
	public boolean isLimitUnwind(List<FuturesTradeLimit> limitList, Date exchangeTime) {
		boolean result = false;
		String fullStr = fullSdf.format(exchangeTime);
		for (FuturesTradeLimit limit : limitList) {
			if (limit.getEnable()) {
				if (limit.getLimitType() == FuturesTradeLimitType.LimitUnwind) {
					if (fullStr.compareTo(limit.getStartLimitTime()) >= 0
							&& fullStr.compareTo(limit.getEndLimitTime()) < 0) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	/**
	 * 禁止平仓
	 *
	 * @param limitList
	 *            期货交易限制列表
	 * @param exchangeTime
	 *            当前时间
	 */
	public void checkedLimitUnwind(List<FuturesTradeLimit> limitList, Date exchangeTime) {
		String fullStr = fullSdf.format(exchangeTime);
		for (FuturesTradeLimit limit : limitList) {
			if (limit.getEnable()) {
				if (limit.getLimitType() == FuturesTradeLimitType.LimitUnwind) {
					if (fullStr.compareTo(limit.getStartLimitTime()) >= 0
							&& fullStr.compareTo(limit.getEndLimitTime()) < 0) {
						throw new ServiceException(ExceptionConstant.CLOSE_POSITION_EXCEPTION);
					}
				}
			}
		}

		/*
		 * String dayStr = sdf.format(exchangeTime); String fullStr =
		 * fullSdf.format(exchangeTime); Calendar cal = Calendar.getInstance();
		 * cal.setTime(exchangeTime); Integer week =
		 * cal.get(Calendar.DAY_OF_WEEK); for (FuturesTradeLimit limit :
		 * limitList) { if (limit.getEnable()) { if (limit.getLimitType() ==
		 * FuturesTradeLimitType.LimitUnwind) { if (limit.getWeekDay() == week)
		 * { if (fullStr.compareTo(dayStr + " " + limit.getStartLimitTime()) >=
		 * 0 && fullStr.compareTo(dayStr + " " + limit.getEndLimitTime()) < 0) {
		 * throw new
		 * ServiceException(ExceptionConstant.CLOSE_POSITION_EXCEPTION); } } } }
		 * }
		 */
	}

	public Integer countOrderType(Long contractId, FuturesOrderType orderType) {
		return orderDao.countOrderByType(contractId, orderType);
	}

	public Integer sumByListOrderContractIdAndPublisherId(Long contractId, Long publisherId, Integer type) {
		return orderDao.sumByListOrderContractIdAndPublisherId(contractId, publisherId, type);
	}

	/**
	 * 部分买入成功
	 *
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder partPositionOrder(Long id) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Posted || order.getState() == FuturesOrderState.BuyingEntrust
				|| order.getState() == FuturesOrderState.PartPosition)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		order.setState(FuturesOrderState.PartPosition);
		order.setUpdateTime(new Date());
		return orderDao.update(order);
	}

	/**
	 * 部分买入成功
	 *
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder partPositionOrder(Long id, BigDecimal filled, BigDecimal remaining, BigDecimal avgFillPrice,
			BigDecimal totalFillCost) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Posted || order.getState() == FuturesOrderState.BuyingEntrust
				|| order.getState() == FuturesOrderState.PartPosition)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		order.setState(FuturesOrderState.PartPosition);
		order.setOpenFilled(filled);
		order.setOpenRemaining(remaining);
		order.setOpenAvgFillPrice(avgFillPrice);
		order.setOpenTotalFillCost(totalFillCost);
		order.setUpdateTime(new Date());
		return orderDao.update(order);
	}

	private void unwindReturnOvernightReserveFund(FuturesOrder order) {
		try {
			// 平仓时退还隔夜保证金
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
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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
	public BigDecimal computeProfitOrLoss(FuturesOrderType orderType, BigDecimal totalQuantity, BigDecimal buyingPrice,
			BigDecimal sellingPrice, BigDecimal minWave, BigDecimal perWaveMoney) {
		BigDecimal waveMoney = sellingPrice.subtract(buyingPrice).divide(minWave, 4, RoundingMode.DOWN)
				.multiply(perWaveMoney).multiply(totalQuantity);
		if (orderType == FuturesOrderType.BuyUp) {
			return waveMoney;
		} else {
			return waveMoney.multiply(new BigDecimal(-1));
		}
	}

	/**
	 * 包装代理商销售价格到合约信息
	 *
	 * @param contract
	 *            合约信息
	 */
	public FuturesCommodity wrapperAgentPrice(Long publisherId, Long commodityId, FuturesCommodity commodity) {
		if (commodity != null) {
			// 获取代理商设置的销售价格
			FuturesAgentPriceDto agentPrice = orgBusiness.getCurrentAgentPrice(publisherId, commodityId);
			if (agentPrice != null) {
				// 保证金
				if (agentPrice.getCostReserveFund() != null
						&& agentPrice.getCostReserveFund().compareTo(BigDecimal.ZERO) > 0) {
					commodity.setPerUnitReserveFund(agentPrice.getCostReserveFund());
				}
				// 开仓手续费
				if (!(agentPrice.getCostOpenwindServiceFee() == null
						&& agentPrice.getSaleOpenwindServiceFee() == null)) {
					commodity.setOpenwindServiceFee(agentPrice.getSaleOpenwindServiceFee() != null
							? agentPrice.getSaleOpenwindServiceFee() : agentPrice.getCostOpenwindServiceFee());
				}
				// 平仓手续费
				if (!(agentPrice.getCostUnwindServiceFee() == null && agentPrice.getSaleUnwindServiceFee() == null)) {
					commodity.setUnwindServiceFee(agentPrice.getSaleUnwindServiceFee() != null
							? agentPrice.getSaleUnwindServiceFee() : agentPrice.getCostUnwindServiceFee());
				}
				// 隔夜递延费
				if (!(agentPrice.getCostDeferredFee() == null && agentPrice.getSaleDeferredFee() == null)) {
					commodity.setOvernightPerUnitDeferredFee(agentPrice.getSaleDeferredFee() != null
							? agentPrice.getSaleDeferredFee() : agentPrice.getCostDeferredFee());
				}
			}
		}
		return commodity;
	}

	// public FuturesOrder backhandPlaceOrder(Long orderId) {
	// FuturesOrder order = orderDao.retrieve(orderId);
	// if (order.getState() != FuturesOrderState.Unwind) {
	// throw new
	// ServiceException(ExceptionConstant.BACKHANDSOURCEORDER_NOTUNWIND_EXCEPTION);
	// }
	// List<FuturesOrder> checkOrder =
	// orderDao.retrieveByBackhandSourceOrderId(orderId);
	// if (checkOrder != null && checkOrder.size() > 0) {
	// throw new
	// ServiceException(ExceptionConstant.FUTURESORDER_ALREADYBACKHAND_EXCEPTION);
	// }
	// // 反手下单
	// FuturesOrder backhandOrder = new FuturesOrder();
	// backhandOrder.setBackhandSourceOrderId(orderId);
	// FuturesContract contract = order.getContract();
	// FuturesCommodity commodity = contract.getCommodity();
	// Long commodityId = commodity.getId();
	// commodity.setId(null);
	// wrapperAgentPrice(order.getPublisherId(), commodityId, commodity);
	// // 计算服务费和保证金
	// BigDecimal serviceFee = order.getTotalQuantity().multiply(
	// contract.getCommodity().getOpenwindServiceFee().add(contract.getCommodity().getUnwindServiceFee()));
	// // 获取运营后台设置的止损止盈
	// backhandOrder.setStopLossOrProfitId(order.getStopLossOrProfitId());
	// FuturesStopLossOrProfit lossOrProfit =
	// stopLossOrProfitDao.retrieve(order.getStopLossOrProfitId());
	// BigDecimal reserveFund = order.getReserveFund();
	// if (lossOrProfit != null) {
	// FuturesCurrencyRate rate =
	// rateService.findByCurrency(order.getCommodityCurrency());
	// reserveFund =
	// order.getTotalQuantity().multiply(lossOrProfit.getReserveFund().multiply(rate.getRate()));
	// backhandOrder.setPerUnitUnwindPoint(lossOrProfit.getStrongLevelingAmount());
	// backhandOrder.setUnwindPointType(2);
	// } else {
	// backhandOrder.setPerUnitUnwindPoint(order.getPerUnitUnwindPoint());
	// backhandOrder.setUnwindPointType(2);
	// }
	// backhandOrder.setLimitLossType(order.getLimitLossType());
	// backhandOrder.setPerUnitLimitLossAmount(order.getPerUnitLimitLossAmount());
	// backhandOrder.setLimitProfitType(order.getLimitProfitType());
	// backhandOrder.setPerUnitLimitProfitAmount(order.getPerUnitLimitProfitAmount());
	// backhandOrder.setStopLossOrProfitId(order.getStopLossOrProfitId());
	// // 初始化部分订单信息
	// backhandOrder.setPublisherId(order.getPublisherId());
	// backhandOrder.setOrderType(
	// order.getOrderType() == FuturesOrderType.BuyUp ? FuturesOrderType.BuyFall
	// : FuturesOrderType.BuyUp);
	// backhandOrder.setTotalQuantity(order.getTotalQuantity());
	// backhandOrder.setReserveFund(reserveFund);
	// backhandOrder.setServiceFee(serviceFee);
	// backhandOrder.setCommoditySymbol(commodity.getSymbol());
	// backhandOrder.setCommodityName(commodity.getName());
	// backhandOrder.setCommodityCurrency(commodity.getCurrency());
	// backhandOrder.setContractNo(contract.getContractNo());
	// backhandOrder.setOpenwindServiceFee(commodity.getOpenwindServiceFee());
	// backhandOrder.setUnwindServiceFee(commodity.getUnwindServiceFee());
	// backhandOrder.setUnwindPointType(commodity.getUnwindPointType());
	// backhandOrder.setOvernightPerUnitReserveFund(commodity.getOvernightPerUnitReserveFund());
	// backhandOrder.setOvernightPerUnitDeferredFee(commodity.getOvernightPerUnitDeferredFee());
	// backhandOrder.setBuyingPriceType(FuturesTradePriceType.MKT);
	// // 获取是否为测试单
	// PublisherDto publisher =
	// publisherBusiness.findById(order.getPublisherId());
	// backhandOrder.setIsTest(publisher.getIsTest());
	// // 请求下单
	// try {
	// return save(backhandOrder, contract.getId());
	// } catch (ServiceException ex) {
	// if
	// (!ex.getType().equals(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION))
	// {
	// throw ex;
	// } else {
	// logger.error("余额不足，反手失败，orderId:{}", orderId);
	// return null;
	// }
	// }
	// }

	// public FuturesOrder backhandUnwind(Long orderId, Long publisherId) {
	// // 检查是否在交易时间段
	// FuturesOrder order = orderDao.retrieveByOrderIdAndPublisherId(orderId,
	// publisherId);
	// if (order == null) {
	// throw new
	// ServiceException(ExceptionConstant.USER_ORDER_DOESNOT_EXIST_EXCEPTION);
	// }
	// Integer timeZoneGap = this.retriveTimeZoneGap(order);
	// boolean isTradeTime = isTradeTime(timeZoneGap, order.getContract(), new
	// Date());
	// if (!isTradeTime) {
	// throw new
	// ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
	// }
	// List<FuturesTradeLimit> limitList =
	// futuresTradeLimitService.findByContractId(order.getContractId());
	// if (limitList != null && limitList.size() > 0) {
	// // 判断该交易平仓时是否在后台设置的期货交易限制内
	// checkedLimitUnwind(limitList, retriveExchangeTime(new Date(),
	// this.retriveTimeZoneGap(order)));
	// }
	// // 判断账户余额是否足够支付反手买入的保证金和服务费
	// FuturesContract contract = order.getContract();
	// FuturesCommodity commodity = contract.getCommodity();
	// FuturesCurrencyRate rate =
	// rateService.findByCurrency(order.getCommodityCurrency());
	// // 获取运营后台设置的止损止盈
	// FuturesStopLossOrProfit lossOrProfit =
	// stopLossOrProfitDao.retrieve(order.getStopLossOrProfitId());
	// BigDecimal totalFee = BigDecimal.ZERO;
	// if (lossOrProfit != null) {
	// totalFee =
	// order.getTotalQuantity().multiply(lossOrProfit.getReserveFund().multiply(rate.getRate())
	// .add(commodity.getOpenwindServiceFee()).add(commodity.getUnwindServiceFee()));
	// } else {
	// totalFee = order.getTotalQuantity()
	// .multiply(commodity.getOpenwindServiceFee().add(commodity.getUnwindServiceFee()))
	// .add(order.getReserveFund());
	// }
	// CapitalAccountDto account =
	// accountBusiness.fetchByPublisherId(order.getPublisherId());
	// if (account.getAvailableBalance().compareTo(totalFee) < 0) {
	// throw new
	// ServiceException(ExceptionConstant.FUTURESORDER_BACKHAND_BALANCENOTENOUGH_EXCEPTION);
	// }
	// return sellingEntrust(order, FuturesWindControlType.BackhandUnwind,
	// FuturesTradePriceType.MKT, null);
	// }

	public FuturesOrder revisionOrder(FuturesOrder order) {
		return orderDao.update(order);
	}

	/************************************* START获取交易所时间、判断是否在交易时间段 ******************************************/

	/**
	 * 获取北京时间和交易所的时差
	 *
	 * @param order
	 *            订单
	 * @return 北京时间和交易所的时差
	 */
	public Integer retriveTimeZoneGap(FuturesOrder order) {
		return order.getContract().getCommodity().getExchange().getTimeZoneGap();
	}

	/**
	 * 获取交易所的对应时间
	 *
	 * @param timeZoneGap
	 *            和交易所的时差
	 * @return 交易所的对应时间
	 */
	public Date retriveExchangeTime(Integer timeZoneGap) {
		return retriveExchangeTime(new Date(), timeZoneGap);
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

	/**
	 * 是否在交易时间
	 *
	 * @param timeZoneGap
	 *            时区
	 * @param term
	 *            合约期限
	 * @return 是否在交易时间
	 */
	public boolean isTradeTime(FuturesOrder order) {
		Integer timeZoneGap = this.retriveTimeZoneGap(order);
		FuturesContract contract = order.getContract();
		return isTradeTime(timeZoneGap, contract, new Date());
	}

	/**
	 * 是否在交易时间
	 *
	 * @param timeZoneGap
	 *            时差
	 * @param contract
	 *            合约
	 * @param tradeActionType
	 *            开仓或者平仓
	 * @return 是否在交易时间
	 */
	public boolean isTradeTime(Integer timeZoneGap, FuturesContract contract, FuturesTradeActionType tradeActionType) {
		return isHoliday(contract) && isTradeLimitDay(contract) && isTradeTime(timeZoneGap, contract, new Date());
	}

	/**
	 * 判断是否假期
	 *
	 * @param contract
	 *            合约
	 * @return
	 */
	private boolean isHoliday(FuturesContract contract) {
		// 获得品种所有的节假日
		List<FuturesHoliday> futuresHolidayList = futuresHolidayService.findByCommodityId(contract.getCommodityId());
		Date contractDate = retriveExchangeTime(new Date(), contract.getCommodity().getTimeZoneGap());
		for (FuturesHoliday futuresHoliday : futuresHolidayList) {
			if (futuresHoliday.getEnable()) {
				if (futuresHoliday.getStartTime().before(contractDate)
						&& futuresHoliday.getEndTime().after(contractDate)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 判断是否在风控的禁止开仓和平仓时间内
	 *
	 * @param contract
	 * @return
	 */
	private boolean isTradeLimitDay(FuturesContract contract) {
		// 获得期货交易限制
		List<FuturesTradeLimit> futuresTradeLimitList = futuresTradeLimitService
				.findByContractId(contract.getCommodityId());
		Date contractDate = retriveExchangeTime(new Date(), contract.getCommodity().getTimeZoneGap());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (FuturesTradeLimit futuresTradeLimit : futuresTradeLimitList) {
			if (futuresTradeLimit.getEnable()) {
				try {
					if (formatter.parse(futuresTradeLimit.getStartLimitTime()).before(contractDate)
							&& formatter.parse(futuresTradeLimit.getEndLimitTime()).after(contractDate)) {
						return false;
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}

	/**
	 * 是否在交易时间
	 *
	 * @param timeZoneGap
	 *            时区
	 * @param term
	 *            合约期限
	 * @return 是否在交易时间
	 */
	public boolean isTradeTime(Integer timeZoneGap, FuturesContract contract) {
		return isTradeTime(timeZoneGap, contract, new Date());
	}

	/**
	 * 是否在交易时间
	 *
	 * @param timeZoneGap
	 *            时区
	 * @param term
	 *            合约期限
	 * @param date
	 *            日期
	 * @return 是否在交易时间
	 */
	public boolean isTradeTime(Integer timeZoneGap, FuturesContract contract, Date date) {
		if (contract != null) {
			SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date exchangeTime = retriveExchangeTime(date, timeZoneGap);
			Calendar cal = Calendar.getInstance();
			cal.setTime(exchangeTime);
			int week = cal.get(Calendar.DAY_OF_WEEK);
			String tradeTime = null;
			if (week == 1) {
				tradeTime = contract.getCommodity().getSunTradeTime();
			} else if (week == 2) {
				tradeTime = contract.getCommodity().getMonTradeTime();
			} else if (week == 3) {
				tradeTime = contract.getCommodity().getTueTradeTime();
			} else if (week == 4) {
				tradeTime = contract.getCommodity().getWedTradeTime();
			} else if (week == 5) {
				tradeTime = contract.getCommodity().getThuTradeTime();
			} else if (week == 6) {
				tradeTime = contract.getCommodity().getFriTradeTime();
			} else if (week == 7) {
				tradeTime = contract.getCommodity().getSatTradeTime();
			}
			if (!StringUtil.isEmpty(tradeTime)) {
				String[] tradeTimeArr = tradeTime.split(",");
				boolean isTradeTime = false;
				for (String tradeTimeDuration : tradeTimeArr) {
					String[] tradeTimePointArr = tradeTimeDuration.trim().split("-");
					String dayStr = daySdf.format(exchangeTime);
					String fullStr = fullSdf.format(exchangeTime);
					if (fullStr.compareTo(dayStr + " " + tradeTimePointArr[0].trim()) >= 0
							&& fullStr.compareTo(dayStr + " " + tradeTimePointArr[1].trim()) < 0) {
						isTradeTime = true;
					}
				}
				return isTradeTime;
			}
		}
		return true;
	}

	/************************************* END获取交易所时间、判断是否在交易时间段 ******************************************/

	public TurnoverStatistyRecordDto getTurnoverStatisty(Long publisherId) {
		String sql = String
				.format("SELECT COUNT(o.id) AS number, SUM(o.total_quantity) AS total_quantity, SUM((o.openwind_service_fee + o.unwind_service_fee) * o.total_quantity) AS service_fee,(SELECT SUM(f.publisher_profit_or_loss) AS user_profit_or_loss FROM f_futures_order f where f.state = 9 AND f.publisher_id="
						+ publisherId
						+ ") AS user_profit_or_loss FROM f_futures_order o where o.state in(6,9) AND o.publisher_id="
						+ publisherId);
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setTurnoverNum", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setTurnoverHandsNum", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setProfitAndLoss", new Class<?>[] { BigDecimal.class }));
		List<TurnoverStatistyRecordDto> content = sqlDao.execute(TurnoverStatistyRecordDto.class, sql, setMethodMap);
		if (content != null && content.size() > 0) {
			return content.get(0);
		}
		return null;
	}

	public BigDecimal getProfitOrLossCurrency(FuturesContract contract, FuturesOrderType orderType,
			BigDecimal totalQuantity, BigDecimal buyingPrice, BigDecimal lastPrice) {
		// 计算浮动盈亏
		if (lastPrice != null) {
			if (orderType == FuturesOrderType.BuyUp) {
				return lastPrice.subtract(buyingPrice).divide(contract.getCommodity().getMinWave())
						.multiply(contract.getCommodity().getPerWaveMoney()).multiply(totalQuantity);
			} else {
				return buyingPrice.subtract(lastPrice).divide(contract.getCommodity().getMinWave())
						.multiply(contract.getCommodity().getPerWaveMoney()).multiply(totalQuantity);
			}
		} else {
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal getStrongMoney(FuturesOrder order) {
		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
		// 合约设置
		// Integer unwindPointType = order.getUnwindPointType();
		// BigDecimal perUnitUnwindPoint = order.getPerUnitUnwindPoint();
		// if (unwindPointType != null && perUnitUnwindPoint != null &&
		// unwindPointType == 1) {
		// if (perUnitUnwindPoint != null && perUnitUnwindPoint.compareTo(new
		// BigDecimal(100)) < 0
		// && perUnitUnwindPoint.compareTo(new BigDecimal(0)) > 0) {
		// return order.getReserveFund()
		// .multiply(new BigDecimal(100).subtract(perUnitUnwindPoint).divide(new
		// BigDecimal(100)));
		// }
		// } else if (unwindPointType != null && perUnitUnwindPoint != null &&
		// unwindPointType == 2) {
		// if (perUnitUnwindPoint != null &&
		// perUnitUnwindPoint.compareTo(BigDecimal.ZERO) >= 0
		// && perUnitUnwindPoint.compareTo(new BigDecimal(0)) > 0) {
		// BigDecimal strongMoney =
		// order.getReserveFund().subtract(perUnitUnwindPoint
		// .multiply(order.getTotalQuantity()).multiply(rate.getRate()).setScale(2,
		// RoundingMode.DOWN));
		// if (strongMoney.compareTo(BigDecimal.ZERO) <= 0) {
		// return order.getReserveFund();
		// } else {
		// return strongMoney;
		// }
		// }
		// }
		return order.getReserveFund();
	}

	/**
	 * 用户未结算订单的盈亏
	 *
	 * @param publisherId
	 *            发布人ID
	 * @return 未结算订单的盈亏
	 */
	public BigDecimal getUnsettledProfitOrLoss(Long publisherId) {
		// 获取订单
		FuturesOrderState[] states = { FuturesOrderState.Position, FuturesOrderState.SellingEntrust,
				FuturesOrderState.PartUnwind };
		FuturesOrderQuery query = new FuturesOrderQuery();
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		query.setStates(states);
		query.setPublisherId(publisherId);
		Page<FuturesOrder> pages = this.pagesOrder(query);
		List<FuturesOrder> orderList = pages.getContent();
		BigDecimal totalProfitOrLoss = BigDecimal.ZERO;
		for (FuturesOrder order : orderList) {
			// 计算浮动盈亏
			BigDecimal lastPrice = allQuote.getLastPrice(order.getCommoditySymbol(), order.getContractNo());
			totalProfitOrLoss = totalProfitOrLoss.add(this.getProfitOrLossCurrency(order.getContract(),
					order.getOrderType(), order.getTotalQuantity(), order.getOpenAvgFillPrice(), lastPrice));
		}
		return totalProfitOrLoss;
	}

	/**
	 * 计算市场均价
	 *
	 * @param commodityNo
	 *            品种编号
	 * @param contractNo
	 *            合约编号
	 * @param actionType
	 *            交易动作类型
	 * @param totalQuantity
	 *            交易总量
	 * @return 市场均价
	 */
	public MarketAveragePrice computeMktAvgPrice(String commodityNo, String contractNo, FuturesActionType actionType,
			BigDecimal totalQuantity) {
		FuturesCommodity commodity = commodityDao.retrieveByCommodityNo(commodityNo);
		FuturesContractMarket mkt = allQuote.getQuote(commodityNo, contractNo);
		if (mkt == null) {
			MarketAveragePrice result = new MarketAveragePrice();
			result.setAvgFillPrice(BigDecimal.ZERO);
			result.setCommodityNo(commodityNo);
			result.setContractNo(contractNo);
			result.setFilled(BigDecimal.ZERO);
			result.setRemaining(totalQuantity);
			result.setTotalFillCost(BigDecimal.ZERO);
			return result;
		}
		if (actionType == FuturesActionType.BUY) {
			MarketAveragePrice result = new MarketAveragePrice();
			// 买方向，取卖档数据
			List<Long> askSizeList = askSizeList(mkt);
			List<BigDecimal> askPriceList = askPriceList(mkt);
			BigDecimal filled = BigDecimal.ZERO;
			BigDecimal remaining = totalQuantity;
			BigDecimal totalFillCost = BigDecimal.ZERO;
			for (int i = 0; i < askSizeList.size(); i++) {
				BigDecimal askSize = new BigDecimal(askSizeList.get(i));
				if (askSize.compareTo(remaining) >= 0) {
					filled = filled.add(remaining);
					totalFillCost = totalFillCost.add(askPriceList.get(i).multiply(remaining));
					remaining = BigDecimal.ZERO;
					break;
				} else {
					filled = filled.add(askSize);
					remaining = remaining.subtract(askSize);
					totalFillCost = totalFillCost.add(askPriceList.get(i).multiply(askSize));
				}
			}
			BigDecimal avgFillPrice = BigDecimal.ZERO;
			if (filled.compareTo(BigDecimal.ZERO) > 0) {
				avgFillPrice = totalFillCost.divide(filled, 10, RoundingMode.DOWN);
				BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(commodity.getMinWave());
				if (divideArr[1].compareTo(BigDecimal.ZERO) > 0) {
					avgFillPrice = divideArr[0].add(new BigDecimal(1)).multiply(commodity.getMinWave());
				}
			}
			// 返回结果
			result.setAvgFillPrice(avgFillPrice);
			result.setCommodityNo(commodityNo);
			result.setContractNo(contractNo);
			result.setFilled(filled);
			result.setRemaining(remaining);
			result.setTotalFillCost(totalFillCost);
			result.setTotalQuantity(totalQuantity);
			result.setMarket(mkt);
			return result;
		} else {
			MarketAveragePrice result = new MarketAveragePrice();
			// 卖方向，取买档数据
			List<Long> bidSizeList = bidSizeList(mkt);
			List<BigDecimal> bidPriceList = bidPriceList(mkt);
			BigDecimal filled = BigDecimal.ZERO;
			BigDecimal remaining = totalQuantity;
			BigDecimal totalFillCost = BigDecimal.ZERO;
			for (int i = 0; i < bidSizeList.size(); i++) {
				BigDecimal askSize = new BigDecimal(bidSizeList.get(i));
				if (askSize.compareTo(remaining) >= 0) {
					filled = filled.add(remaining);
					totalFillCost = totalFillCost.add(bidPriceList.get(i).multiply(remaining));
					remaining = BigDecimal.ZERO;
					break;
				} else {
					filled = filled.add(askSize);
					remaining = remaining.subtract(askSize);
					totalFillCost = totalFillCost.add(bidPriceList.get(i).multiply(askSize));
				}
			}
			BigDecimal avgFillPrice = BigDecimal.ZERO;
			if (filled.compareTo(BigDecimal.ZERO) > 0) {
				avgFillPrice = totalFillCost.divide(filled, 10, RoundingMode.DOWN);
				BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(commodity.getMinWave());
				avgFillPrice = divideArr[0].multiply(commodity.getMinWave());
			}
			// 返回结果
			result.setAvgFillPrice(avgFillPrice);
			result.setCommodityNo(commodityNo);
			result.setContractNo(contractNo);
			result.setFilled(filled);
			result.setRemaining(remaining);
			result.setTotalFillCost(totalFillCost);
			result.setTotalQuantity(totalQuantity);
			result.setMarket(mkt);
			return result;
		}
	}

	/**
	 * 计算限定价均价
	 *
	 * @param commodityNo
	 *            品种编号
	 * @param contractNo
	 *            合约编号
	 * @param actionType
	 *            交易动作类型
	 * @param totalQuantity
	 *            交易总量
	 * @param entrustPrice
	 *            委托价格
	 * @return 市场均价
	 */
	public MarketAveragePrice computeLmtAvgPrice(String commodityNo, String contractNo, FuturesActionType actionType,
			BigDecimal totalQuantity, BigDecimal entrustPrice) {
		FuturesCommodity commodity = commodityDao.retrieveByCommodityNo(commodityNo);
		FuturesContractMarket mkt = allQuote.getQuote(commodityNo, contractNo);
		if (mkt == null) {
			MarketAveragePrice result = new MarketAveragePrice();
			result.setAvgFillPrice(BigDecimal.ZERO);
			result.setCommodityNo(commodityNo);
			result.setContractNo(contractNo);
			result.setFilled(BigDecimal.ZERO);
			result.setRemaining(totalQuantity);
			result.setTotalFillCost(BigDecimal.ZERO);
			return result;
		}
		if (actionType == FuturesActionType.BUY) {
			MarketAveragePrice result = new MarketAveragePrice();
			// 买方向，取卖档数据
			List<Long> askSizeList = askSizeList(mkt);
			List<BigDecimal> askPriceList = askPriceList(mkt);
			BigDecimal filled = BigDecimal.ZERO;
			BigDecimal remaining = totalQuantity;
			BigDecimal totalFillCost = BigDecimal.ZERO;
			for (int i = 0; i < askPriceList.size(); i++) {
				if (entrustPrice.compareTo(askPriceList.get(i)) >= 0) {
					BigDecimal askSize = new BigDecimal(askSizeList.get(i));
					if (askSize.compareTo(remaining) >= 0) {
						filled = filled.add(remaining);
						totalFillCost = totalFillCost.add(askPriceList.get(i).multiply(remaining));
						remaining = BigDecimal.ZERO;
						break;
					} else {
						filled = filled.add(askSize);
						remaining = remaining.subtract(askSize);
						totalFillCost = totalFillCost.add(askPriceList.get(i).multiply(askSize));
					}
				}
			}
			BigDecimal avgFillPrice = BigDecimal.ZERO;
			if (filled.compareTo(BigDecimal.ZERO) > 0) {
				avgFillPrice = totalFillCost.divide(filled, 10, RoundingMode.DOWN);
				BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(commodity.getMinWave());
				if (divideArr[1].compareTo(BigDecimal.ZERO) > 0) {
					avgFillPrice = divideArr[0].add(new BigDecimal(1)).multiply(commodity.getMinWave());
				}
			}
			// 返回结果
			result.setAvgFillPrice(avgFillPrice);
			result.setCommodityNo(commodityNo);
			result.setContractNo(contractNo);
			result.setFilled(filled);
			result.setRemaining(remaining);
			result.setTotalFillCost(totalFillCost);
			result.setTotalQuantity(totalQuantity);
			return result;
		} else {
			MarketAveragePrice result = new MarketAveragePrice();
			// 卖方向，取买档数据
			List<Long> bidSizeList = bidSizeList(mkt);
			List<BigDecimal> bidPriceList = bidPriceList(mkt);
			BigDecimal filled = BigDecimal.ZERO;
			BigDecimal remaining = totalQuantity;
			BigDecimal totalFillCost = BigDecimal.ZERO;
			for (int i = 0; i < bidPriceList.size(); i++) {
				if (entrustPrice.compareTo(bidPriceList.get(i)) <= 0) {
					BigDecimal bidSize = new BigDecimal(bidSizeList.get(i));
					if (bidSize.compareTo(remaining) >= 0) {
						filled = filled.add(remaining);
						totalFillCost = totalFillCost.add(bidPriceList.get(i).multiply(remaining));
						remaining = BigDecimal.ZERO;
						break;
					} else {
						filled = filled.add(bidSize);
						remaining = remaining.subtract(bidSize);
						totalFillCost = totalFillCost.add(bidPriceList.get(i).multiply(bidSize));
					}
				}
			}
			BigDecimal avgFillPrice = BigDecimal.ZERO;
			if (filled.compareTo(BigDecimal.ZERO) > 0) {
				avgFillPrice = totalFillCost.divide(filled, 10, RoundingMode.DOWN);
				BigDecimal[] divideArr = avgFillPrice.divideAndRemainder(commodity.getMinWave());
				avgFillPrice = divideArr[0].multiply(commodity.getMinWave());
			}
			// 返回结果
			result.setAvgFillPrice(avgFillPrice);
			result.setCommodityNo(commodityNo);
			result.setContractNo(contractNo);
			result.setFilled(filled);
			result.setRemaining(remaining);
			result.setTotalFillCost(totalFillCost);
			result.setTotalQuantity(totalQuantity);
			return result;
		}
	}

	private List<Long> askSizeList(FuturesContractMarket mkt) {
		List<Long> result = new ArrayList<>();
		if (mkt.getAskSize() != null && mkt.getAskSize() > 0) {
			result.add(mkt.getAskSize());
			if (mkt.getAskSize2() != null && mkt.getAskSize2() > 0) {
				result.add(mkt.getAskSize2());
				if (mkt.getAskSize3() != null && mkt.getAskSize3() > 0) {
					result.add(mkt.getAskSize3());
					if (mkt.getAskSize4() != null && mkt.getAskSize4() > 0) {
						result.add(mkt.getAskSize4());
						if (mkt.getAskSize5() != null && mkt.getAskSize5() > 0) {
							result.add(mkt.getAskSize5());
							if (mkt.getAskSize6() != null && mkt.getAskSize6() > 0) {
								result.add(mkt.getAskSize6());
								if (mkt.getAskSize7() != null && mkt.getAskSize7() > 0) {
									result.add(mkt.getAskSize7());
									if (mkt.getAskSize8() != null && mkt.getAskSize8() > 0) {
										result.add(mkt.getAskSize8());
										if (mkt.getAskSize9() != null && mkt.getAskSize9() > 0) {
											result.add(mkt.getAskSize9());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private List<BigDecimal> askPriceList(FuturesContractMarket mkt) {
		List<BigDecimal> result = new ArrayList<>();
		if (mkt.getAskPrice() != null && mkt.getAskPrice().compareTo(BigDecimal.ZERO) > 0) {
			result.add(mkt.getAskPrice());
			if (mkt.getAskPrice2() != null && mkt.getAskPrice2().compareTo(BigDecimal.ZERO) > 0) {
				result.add(mkt.getAskPrice2());
				if (mkt.getAskPrice3() != null && mkt.getAskPrice3().compareTo(BigDecimal.ZERO) > 0) {
					result.add(mkt.getAskPrice3());
					if (mkt.getAskPrice4() != null && mkt.getAskPrice4().compareTo(BigDecimal.ZERO) > 0) {
						result.add(mkt.getAskPrice4());
						if (mkt.getAskPrice5() != null && mkt.getAskPrice5().compareTo(BigDecimal.ZERO) > 0) {
							result.add(mkt.getAskPrice5());
							if (mkt.getAskPrice6() != null && mkt.getAskPrice6().compareTo(BigDecimal.ZERO) > 0) {
								result.add(mkt.getAskPrice6());
								if (mkt.getAskPrice7() != null && mkt.getAskPrice7().compareTo(BigDecimal.ZERO) > 0) {
									result.add(mkt.getAskPrice7());
									if (mkt.getAskPrice8() != null
											&& mkt.getAskPrice8().compareTo(BigDecimal.ZERO) > 0) {
										result.add(mkt.getAskPrice8());
										if (mkt.getAskPrice9() != null
												&& mkt.getAskPrice9().compareTo(BigDecimal.ZERO) > 0) {
											result.add(mkt.getAskPrice9());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private List<Long> bidSizeList(FuturesContractMarket mkt) {
		List<Long> result = new ArrayList<>();
		if (mkt.getBidSize() != null && mkt.getBidSize() > 0) {
			result.add(mkt.getBidSize());
			if (mkt.getBidSize2() != null && mkt.getBidSize2() > 0) {
				result.add(mkt.getBidSize2());
				if (mkt.getBidSize3() != null && mkt.getBidSize3() > 0) {
					result.add(mkt.getBidSize3());
					if (mkt.getBidSize4() != null && mkt.getBidSize4() > 0) {
						result.add(mkt.getBidSize4());
						if (mkt.getBidSize5() != null && mkt.getBidSize5() > 0) {
							result.add(mkt.getBidSize5());
							if (mkt.getBidSize6() != null && mkt.getBidSize6() > 0) {
								result.add(mkt.getBidSize6());
								if (mkt.getBidSize7() != null && mkt.getBidSize7() > 0) {
									result.add(mkt.getBidSize7());
									if (mkt.getBidSize8() != null && mkt.getBidSize8() > 0) {
										result.add(mkt.getBidSize8());
										if (mkt.getBidSize9() != null && mkt.getBidSize9() > 0) {
											result.add(mkt.getBidSize9());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private List<BigDecimal> bidPriceList(FuturesContractMarket mkt) {
		List<BigDecimal> result = new ArrayList<>();
		if (mkt.getBidPrice() != null && mkt.getBidPrice().compareTo(BigDecimal.ZERO) > 0) {
			result.add(mkt.getBidPrice());
			if (mkt.getBidPrice2() != null && mkt.getBidPrice2().compareTo(BigDecimal.ZERO) > 0) {
				result.add(mkt.getBidPrice2());
				if (mkt.getBidPrice3() != null && mkt.getBidPrice3().compareTo(BigDecimal.ZERO) > 0) {
					result.add(mkt.getBidPrice3());
					if (mkt.getBidPrice4() != null && mkt.getBidPrice4().compareTo(BigDecimal.ZERO) > 0) {
						result.add(mkt.getBidPrice4());
						if (mkt.getBidPrice5() != null && mkt.getBidPrice5().compareTo(BigDecimal.ZERO) > 0) {
							result.add(mkt.getBidPrice5());
							if (mkt.getBidPrice6() != null && mkt.getBidPrice6().compareTo(BigDecimal.ZERO) > 0) {
								result.add(mkt.getBidPrice6());
								if (mkt.getBidPrice7() != null && mkt.getBidPrice7().compareTo(BigDecimal.ZERO) > 0) {
									result.add(mkt.getBidPrice7());
									if (mkt.getBidPrice8() != null
											&& mkt.getBidPrice8().compareTo(BigDecimal.ZERO) > 0) {
										result.add(mkt.getBidPrice8());
										if (mkt.getBidPrice9() != null
												&& mkt.getBidPrice9().compareTo(BigDecimal.ZERO) > 0) {
											result.add(mkt.getBidPrice9());
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	public Page<AgentOrderRecordDto> pagesOrderRecord(FuturesTradeAdminQuery query) {

		String publisherNameCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			publisherNameCondition = " AND t2.name like '%" + query.getPublisherName().trim() + "%' ";
		}
		String publisherPhoneCondition = "";
		if (query.getPublisherPhone() != null && !"".equals(query.getPublisherPhone())) {
			publisherPhoneCondition = " AND t3.phone like '%" + query.getPublisherPhone().trim() + "%' ";
		}
		String symbol = "";
		if (!StringUtil.isEmpty(query.getSymbol())) {
			symbol = " AND t1.commodity_symbol like '%" + query.getSymbol().trim() + "%'";
		}
		String commodityName = "";
		if (!StringUtil.isEmpty(query.getName())) {
			commodityName = " AND t1.commodity_name like '%" + query.getName().trim() + "%'";
		}
		String orderType = "";
		if (!StringUtil.isEmpty(query.getOrderType())) {
			orderType = " AND t1.order_type =" + query.getOrderType().trim();
		}
		String orderByName = "";
		String orderState = "";
		if (!StringUtil.isEmpty(query.getOrderState())) {
			orderState = " AND t1.state in(" + query.getOrderState().trim() + ") ";
			if (query.getOrderState().equals("6,9") || query.getOrderState().equals("6")
					|| query.getOrderState().equals("9")) {
				orderByName = " ORDER BY t1.buying_time DESC";
			} else {
				orderByName = " ORDER BY t1.post_time DESC";
			}
		}
		String priceType = "";
		if (!StringUtil.isEmpty(query.getPriceType())) {
			orderType = " AND t1.buying_price_type =" + query.getPriceType().trim();
		}
		String treeCode = "";
		if (query.getTreeCode() != null) {
			treeCode = " AND t5.tree_code LIKE '%" + query.getTreeCode() + "%'";
		}
		String tradeNo = "";
		if (!StringUtil.isEmpty(query.getTradeNo())) {
			tradeNo = " AND t1.trade_no LIKE '%" + query.getTradeNo() + "%'";
		}
		String windControlType = "";
		if (!StringUtil.isEmpty(query.getWindControlType())) {
			windControlType = " AND t1.wind_control_type in(" + query.getWindControlType() + ")";
		}
		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			startTimeCondition = " and t1.create_time>='" + fullSdf.format(query.getStartTime()) + "' ";
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			endTimeCondition = " and t1.create_time<'" + fullSdf.format(query.getEndTime()) + "' ";
		}

		String sql = String.format(
				"SELECT t1.id, t2.name AS publisher_name, t3.phone as publisher_phone, t1.commodity_symbol, "
						+ "t1.commodity_name, t1.contract_no, t1.trade_no, t1.order_type, t1.state, t1.total_quantity, t1.buying_time, "
						+ "t1.buying_price, t1.publisher_profit_or_loss, t1.buying_price_type, t1.openwind_service_fee, "
						+ "t1.unwind_service_fee, t1.reserve_fund, t4.overnight_deferred_fee, t4.overnight_reserve_fund, "
						+ "t1.per_unit_limit_loss_amount, t1.per_unit_limit_profit_amount, t1.selling_time, t1.selling_price, "
						+ "t1.profit_or_loss, t1.wind_control_type, t6.name AS org_name, t1.contract_id, t1.commodity_currency, "
						+ "t6.code, t1.buying_entrust_price, t1.post_time, t1.service_fee "
						+ "FROM  f_futures_order t1 LEFT JOIN real_name t2 ON t2.resource_id = t1.publisher_id "
						+ " LEFT JOIN publisher t3 ON t3.id = t1.publisher_id "
						+ " LEFT JOIN f_futures_overnight_record t4 ON t4.order_id = t1.id "
						+ " LEFT JOIN p_organization_publisher t5 ON t5.publisher_id = t1.publisher_id "
						+ " LEFT JOIN p_organization t6 ON t6.id = t5.org_id  WHERE 1=1 %s %s %s %s %s %s %s %s %s %s %s LIMIT "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				treeCode, publisherNameCondition, publisherPhoneCondition, symbol, commodityName, orderType, orderState,
				priceType, tradeNo, windControlType, orderByName);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("FROM"), sql.indexOf("LIMIT"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setSymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setContractNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setTradeNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setOrderType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setState", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setTotalQuantity", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setBuyingTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setBuyingPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(12),
				new MethodDesc("setPublisherProfitOrLoss", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(13),
				new MethodDesc("setBuyingPriceType", new Class<?>[] { FuturesTradePriceType.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setOpenwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setUnwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setReserveFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(17),
				new MethodDesc("setOvernightServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(18),
				new MethodDesc("setOvernightReserveFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(19),
				new MethodDesc("setPerUnitLimitLossAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(20),
				new MethodDesc("setPerUnitLimitProfitAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(21), new MethodDesc("setSellingTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(22), new MethodDesc("setSellingPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(23), new MethodDesc("setProfitOrLoss", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(24),
				new MethodDesc("setWindControlType", new Class<?>[] { FuturesWindControlType.class }));
		setMethodMap.put(new Integer(25), new MethodDesc("setOrgName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(26), new MethodDesc("setContractId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(27), new MethodDesc("setCommodityCurrency", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(28), new MethodDesc("setCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(29),
				new MethodDesc("setEntrustAppointPrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(30), new MethodDesc("setPostTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(31), new MethodDesc("setServiceFee", new Class<?>[] { BigDecimal.class }));

		List<AgentOrderRecordDto> content = sqlDao.execute(AgentOrderRecordDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Integer countByPublisherId(Long publisherId) {
		return orderDao.countByPublisherId(publisherId);
	}

	/**
	 * 已成交部分均价
	 * 
	 * @param publisherId
	 *            用户ID
	 * @param contractNo
	 *            合约编号
	 * @param commodityNo
	 *            产品编号
	 * @param orderType
	 *            订单类型
	 * @return
	 */
	public BigDecimal getOpenAvgFillPrice(Long publisherId, String contractNo, String commodityNo, String orderType) {
		return orderDao.getOpenAvgFillPrice(publisherId, contractNo, commodityNo, orderType);
	}

	public BigDecimal getCloseAvgFillPrice(Long publisherId, String contractNo, String commodityNo, String orderType) {
		return orderDao.getCloseAvgFillPrice(publisherId, contractNo, commodityNo, orderType);
	}

	public Page<FuturesTradeActionAgentDto> pagesOrderAgentDealRecord(FuturesTradeAdminQuery query) {
		String publisherNameCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			publisherNameCondition = " AND t4.name like '%" + query.getPublisherName().trim() + "%' ";
		}
		String publisherPhoneCondition = "";
		if (query.getPublisherPhone() != null && !"".equals(query.getPublisherPhone())) {
			publisherPhoneCondition = " AND t5.phone like '%" + query.getPublisherPhone().trim() + "%' ";
		}
		String symbol = "";
		if (!StringUtil.isEmpty(query.getSymbol())) {
			symbol = " AND t2.commodity_symbol like '%" + query.getSymbol().trim() + "%'";
		}
		String commodityName = "";
		if (!StringUtil.isEmpty(query.getName())) {
			commodityName = " AND t2.commodity_name like '%" + query.getName().trim() + "%'";
		}
		// /交易方向 ,1 买涨，2 买跌
		String orderType = "";
		if (!StringUtil.isEmpty(query.getOrderType())) {
			orderType = " AND t2.order_type =" + query.getOrderType().trim();
		}
		String orderState = "";
		if (!StringUtil.isEmpty(query.getOrderState())) {
			orderState = " AND t2.state in(" + query.getOrderState().trim() + ")";
		}
		// 定单类型，1 市价 ,2 限价
		String priceType = "";
		if (!StringUtil.isEmpty(query.getPriceType())) {
			priceType = " AND t3.price_type =" + query.getPriceType().trim();
		}
		String tradeNo = "";
		if (!StringUtil.isEmpty(query.getTradeNo())) {
			tradeNo = " AND t2.trade_no LIKE '%" + query.getTradeNo() + "%'";
		}
		String windControlType = "";
		if (!StringUtil.isEmpty(query.getWindControlType())) {
			windControlType = " AND t1.wind_control_type in(" + query.getWindControlType() + ")";
		}
		String treeCode = "";
		if (query.getTreeCode() != null) {
			treeCode = " AND t8.tree_code LIKE '%" + query.getTreeCode() + "%'";
		}
		String tradeActionType = "";
		if (query.getTradeActionType() != null) {
			tradeActionType = " AND t1.trade_action_type in(" + query.getTradeActionType() + ")";
		}

		String sql = String.format(
				"SELECT t1.id as action_id, t4.name AS publisher_name, t5.phone AS publisher_phone, t2.commodity_symbol, t2.commodity_name, t2.contract_no, t2.order_type, "
						+ "t1.trade_action_type, t1.filled, t1.trade_price, t1.publisher_profit_or_loss, t1.action_no, t1.trade_time,t1.state,t3.price_type,t1.wind_control_type, "
						+ "t2.commodity_currency, t6.id, t8.code, t8.name "
						+ "FROM f_futures_trade_action t1 LEFT JOIN f_futures_order t2 ON t2.id = t1.order_id "
						+ "LEFT JOIN f_futures_trade_entrust t3 ON t3.id = t1.trade_entrust_id "
						+ "LEFT JOIN real_name t4 ON t4.resource_id = t1.publisher_id "
						+ "LEFT JOIN publisher t5 ON t5.id = t1.publisher_id "
						+ "LEFT JOIN f_futures_contract t6 ON t6.id = t2.contract_id "
						+ "LEFT JOIN p_organization_publisher t7 ON t7.publisher_id = t1.publisher_id "
						+ "LEFT JOIN p_organization t8 ON t8.id = t7.org_id"
						+ " WHERE 1=1 %s %s %s %s %s %s %s %s %s %s %s ORDER BY t1.trade_time DESC LIMIT "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				publisherNameCondition, publisherPhoneCondition, symbol, commodityName, orderType, priceType, tradeNo,
				windControlType, treeCode, tradeActionType, orderState);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("FROM"), sql.indexOf("LIMIT"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setCommoditySymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setCommodityName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setContractNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setOrderType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setTradeActionType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setFilled", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setTradePrice", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(10),
				new MethodDesc("setPublisherProfitOrLoss", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setActionNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setTradeTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setState", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setPriceType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setWindControlType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setCommodityCurrency", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setContractId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setOrgName", new Class<?>[] { String.class }));

		List<FuturesTradeActionAgentDto> content = sqlDao.execute(FuturesTradeActionAgentDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<FuturesContractOrder> pages(final FuturesTradeAdminQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesContractOrder> page = futuresContractOrderDao.page(new Specification<FuturesContractOrder>() {
			@Override
			public Predicate toPredicate(Root<FuturesContractOrder> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				if (query.getPublisherIds().size() > 0) {
					predicateList.add(criteriaBuilder.in(root.get("publisherId")).value(query.getPublisherIds()));
				}
				if (!StringUtil.isEmpty(query.getSymbol())) {
					predicateList.add(criteriaBuilder.or(
							criteriaBuilder.like(root.get("commodityNo").as(String.class),
									"%" + query.getSymbol() + "%"),
							criteriaBuilder.like(root.get("commodityName").as(String.class),
									"%" + query.getName() + "%")));
				}
				// 以更新时间排序
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}

}
