package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.futures.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.futures.business.CapitalFlowBusiness;
import com.waben.stock.datalayer.futures.business.OrganizationBusiness;
import com.waben.stock.datalayer.futures.business.OutsideMessageBusiness;
import com.waben.stock.datalayer.futures.business.ProfileBusiness;
import com.waben.stock.datalayer.futures.business.PublisherBusiness;
import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOvernightRecord;
import com.waben.stock.datalayer.futures.entity.FuturesStopLossOrProfit;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesOrderStateConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesWindControlTypeConverter;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.EntrustQueryConsumer;
import com.waben.stock.datalayer.futures.rabbitmq.consumer.MonitorPublisherFuturesOrderConsumer;
import com.waben.stock.datalayer.futures.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractDao;
import com.waben.stock.datalayer.futures.repository.FuturesOrderDao;
import com.waben.stock.datalayer.futures.repository.FuturesOvernightRecordDao;
import com.waben.stock.datalayer.futures.repository.FuturesStopLossOrProfitDao;
import com.waben.stock.datalayer.futures.repository.impl.MethodDesc;
import com.waben.stock.datalayer.futures.schedule.RetriveAllQuoteSchedule;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.TradeFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
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
import com.waben.stock.interfaces.enums.FuturesTradeLimitType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;
import com.waben.stock.interfaces.enums.OutsideMessageType;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.message.OutsideMessage;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.util.StringUtil;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

/**
 * 期货订单 service
 * 
 * @author sunl
 *
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
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesStopLossOrProfitDao stopLossOrProfitDao;

	@Autowired
	private FuturesOvernightRecordService overnightService;

	@Autowired
	private FuturesCurrencyRateService rateService;

	@Autowired
	private FuturesOvernightRecordDao recordDao;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private CapitalFlowBusiness flowBusiness;

	@Autowired
	private PublisherBusiness publisherBusiness;

	@Autowired
	private OrganizationBusiness orgBusiness;

	@Autowired
	private OutsideMessageBusiness outsideMessageBusiness;

	@Autowired
	private FuturesTradeLimitService futuresTradeLimitService;

	@Autowired
	private RetriveAllQuoteSchedule allQuote;

	@Autowired
	private MonitorPublisherFuturesOrderConsumer monitorPublisher;

	@Autowired
	private EntrustQueryConsumer entrueQuery;

	@Autowired
	private ProfileBusiness profileBusiness;

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
				if (query.isOnlyProfit()) {
					predicateList.add(criteriaBuilder.gt(root.get("publisherProfitOrLoss").as(BigDecimal.class),
							new BigDecimal(0)));
				}
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
					Predicate stateTime = criteriaBuilder.greaterThanOrEqualTo(root.get("buyingTime").as(Date.class),
							query.getStartBuyingTime());
					predicateList.add(criteriaBuilder.and(stateTime));
				}
				// 结束日期
				if (query.getEndBuyingTime() != null) {
					Predicate endTime = criteriaBuilder.lessThan(root.get("buyingTime").as(Date.class),
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
				if (query.getStates() != null) {
					if (orderStateArrToString(query.getStates()).equals(orderStateArrToString(unwindStates))) {
						List<Order> orderList = new ArrayList<Order>();
						orderList.add(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
						criteriaQuery.orderBy(orderList);
					} else if (orderStateArrToString(query.getStates()).equals(orderStateArrToString(wtStates))) {
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("buyingEntrustTime").as(Date.class)));
					} else if (orderStateArrToString(query.getStates()).equals(orderStateArrToString(positionStates))) {
						criteriaQuery.orderBy(criteriaBuilder.desc(root.get("buyingTime").as(Date.class)));
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
	public FuturesOrder save(FuturesOrder order, Long contractId) {
		// step 1 : 检查网关是否正常
		boolean isConnected = TradeFuturesOverHttp.checkConnection(profileBusiness.isProd());
		if (!isConnected) {
			throw new ServiceException(ExceptionConstant.FUTURESAPI_NOTCONNECTED_EXCEPTION);
		}
		// step 2 : 再一次确认余额是否充足
		CapitalAccountDto capitalAccount = accountBusiness.fetchByPublisherId(order.getPublisherId());
		BigDecimal totalFee = order.getServiceFee().add(order.getReserveFund());
		if (totalFee.compareTo(capitalAccount.getAvailableBalance()) > 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		// step 3 : 获取期货合约和期货合约期限
		FuturesContract contract = contractDao.retrieve(contractId);
		List<FuturesTradeLimit> limitList = futuresTradeLimitService.findByContractId(contract.getId());
		if (limitList != null && limitList.size() > 0) {
			// 判断该交易在开仓时是否在后台设置的期货交易限制内
			checkedLimitOpenwind(limitList,
					retriveExchangeTime(new Date(), contract.getCommodity().getExchange().getTimeZoneGap()));
		}
		// step 4 : 初始化订单
		order.setTradeNo(UniqueCodeGenerator.generateTradeNo());
		Date date = new Date();
		order.setPostTime(date);
		order.setUpdateTime(date);
		order.setState(FuturesOrderState.Posted);
		order.setContract(contract);
		order = orderDao.create(order);
		// step 5 : 扣去金额、冻结保证金
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
		// step 6 : 调用期货网关委托下单
		// FuturesActionType action = order.getOrderType() ==
		// FuturesOrderType.BuyUp ? FuturesActionType.BUY
		// : FuturesActionType.SELL;
		// Integer orderType = order.getBuyingPriceType() ==
		// FuturesTradePriceType.MKT ? 1 : 2;
		// //
		// 如果恒生指数或者小恒生，需做特殊处理，这两个只能以先定价下单，恒指和小恒指买涨在最新市价基础上增加3个点（按最波动点位来）。买跌减3个点
		// BigDecimal gatewayBuyingEntrustPrice = order.getBuyingEntrustPrice();
		// if (("CN".equals(order.getCommoditySymbol()) ||
		// "HSI".equals(order.getCommoditySymbol())
		// || "MHI".equals(order.getCommoditySymbol())) && orderType == 1) {
		// orderType = 2;
		// if (action == FuturesActionType.BUY) {
		// gatewayBuyingEntrustPrice = gatewayBuyingEntrustPrice
		// .add(new
		// BigDecimal("3").multiply(contract.getCommodity().getMinWave()));
		// } else {
		// gatewayBuyingEntrustPrice = gatewayBuyingEntrustPrice
		// .subtract(new
		// BigDecimal("3").multiply(contract.getCommodity().getMinWave()));
		// }
		// }
		// 修改成模拟下单，以下代码注释
		// FuturesGatewayOrder gatewayOrder =
		// TradeFuturesOverHttp.placeOrder(profileBusiness.isProd(), domain,
		// order.getCommoditySymbol(), order.getContractNo(), order.getId(),
		// action, order.getTotalQuantity(),
		// orderType, gatewayBuyingEntrustPrice);
		// TODO 委托下单异常情况处理，此处默认为所有的委托都能成功
		// step 7 : 更新订单状态
		order.setState(FuturesOrderState.BuyingEntrust);
		order.setOpenGatewayOrderId(-1L);
		order.setBuyingEntrustTime(date);
		order = orderDao.update(order);
		// step 8 : 站外消息推送
		sendOutsideMessage(order);
		// step 9 : 放入委托查询队列（开仓）
		entrueQuery.entrustQuery(order.getId(), 1);
		return order;
	}

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

	private void sendOutsideMessage(FuturesOrder order) {
		try {
			FuturesOrderState state = order.getState();
			Map<String, String> extras = new HashMap<>();
			OutsideMessage message = new OutsideMessage();
			message.setPublisherId(order.getPublisherId());
			message.setTitle("期货订单通知");
			extras.put("title", message.getTitle());
			extras.put("publisherId", String.valueOf(order.getPublisherId()));
			extras.put("resourceType", ResourceType.FUTURESORDER.getIndex());
			extras.put("resourceId", String.valueOf(order.getId()));
			message.setExtras(extras);
			switch (state) {
			case BuyingFailure:
				message.setContent(String.format("您购买的“%s”委托买入失败，已退款到您的账户", order.getCommodityName()));
				extras.put("content",
						String.format("您购买的“<span id=\"futures\">%s</span>”委托买入失败，已退款到您的账户", order.getCommodityName()));
				extras.put("type", OutsideMessageType.Futures_BuyingFailure.getIndex());
				break;
			case BuyingCanceled:
				message.setContent(String.format("您所购买的“%s”已取消委托，已退款到您的账户", order.getCommodityName()));
				extras.put("content",
						String.format("您所购买的“<span id=\"futures\">%s</span>”已取消委托，已退款到您的账户", order.getCommodityName()));
				extras.put("type", OutsideMessageType.Futures_BuyingCanceled.getIndex());
				break;
			case Position:
				if (order.getBuyingPriceType() == FuturesTradePriceType.MKT) {
					message.setContent(String.format("您购买的“%s”已开仓成功，进入“持仓中”状态", order.getCommodityName()));
					extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”已开仓成功，进入“持仓中”状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_Position.getIndex());
					break;
				} else {
					message.setContent(String.format("您委托指定价购买“%s”已开仓成功，进入“持仓中”状态", order.getCommodityName()));
					extras.put("content", String.format("您委托指定价购买“<span id=\"futures\">%s</span>”已开仓成功，进入“持仓中”状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_EntrustPosition.getIndex());
					break;
				}
			case Unwind:
				FuturesWindControlType windControlType = order.getWindControlType();
				if (windControlType != null && windControlType == FuturesWindControlType.DayUnwind) {
					// 日内平仓
					message.setContent(String.format("您购买的“%s”因余额不足无法持仓过夜系统已强制平仓，已进入结算状态", order.getCommodityName()));
					extras.put("content",
							String.format("您购买的“<span id=\"futures\">%s</span>”因余额不足无法持仓过夜系统已强制平仓，已进入结算状态",
									order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_DayUnwind.getIndex());
					break;
				} else if (windControlType != null && windControlType == FuturesWindControlType.UserApplyUnwind) {
					// 用户申请平仓
					message.setContent(String.format("您购买的“%s”手动平仓，已进入结算状态", order.getCommodityName()));
					extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”手动平仓，已进入结算状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_ApplyUnwind.getIndex());
					break;
				} else if (windControlType != null && windControlType == FuturesWindControlType.ReachProfitPoint) {
					// 达到止盈点
					message.setContent(String.format("您购买的“%s”达到止盈平仓，已进入结算状态", order.getCommodityName()));
					extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”达到止盈平仓，已进入结算状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_ReachProfitPoint.getIndex());
					break;
				} else if (windControlType != null && windControlType == FuturesWindControlType.ReachLossPoint) {
					// 达到止损点
					message.setContent(String.format("您购买的“%s”达到止损平仓，已进入结算状态", order.getCommodityName()));
					extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”达到止损平仓，已进入结算状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_ReachLossPoint.getIndex());
					break;
				} else if (windControlType != null
						&& windControlType == FuturesWindControlType.ReachContractExpiration) {
					// 合约到期平仓
					message.setContent(String.format("您购买的“%s”因合约到期系统强制平仓，已进入结算状态", order.getCommodityName()));
					extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”因合约到期系统强制平仓，已进入结算状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_ReachContractExpiration.getIndex());
					break;
				} else if (windControlType != null && windControlType == FuturesWindControlType.ReachStrongPoint) {
					// 达到强平点
					message.setContent(String.format("您购买的“%s”因达到系统强平风控金额，已强制平仓，已进入结算状态", order.getCommodityName()));
					extras.put("content", String.format("您购买的“<span id=\"futures\">%s</span>”因达到系统强平风控金额，已强制平仓，已进入结算状态",
							order.getCommodityName()));
					extras.put("type", OutsideMessageType.Futures_ReachStrongPoint.getIndex());
					break;
				}
				break;
			default:
				break;
			}
			if (message.getContent() != null) {
				outsideMessageBusiness.send(message);
			}
		} catch (Exception ex) {
			logger.error("发送期货订单通知失败，{}_{}_{}", order.getId(), order.getState(), ex.getMessage());
		}
	}

	public Integer countOrderType(Long contractId, FuturesOrderType orderType) {
		return orderDao.countOrderByType(contractId, orderType);
	}

	public Integer sumByListOrderContractIdAndPublisherId(Long contractId, Long publisherId, Integer type) {
		return orderDao.sumByListOrderContractIdAndPublisherId(contractId, publisherId, type);
	}

	/**
	 * 订单已取消
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder canceledOrder(Long id) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Posted || order.getState() == FuturesOrderState.BuyingEntrust
				|| order.getState() == FuturesOrderState.SellingEntrust)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		if (order.getState() == FuturesOrderState.SellingEntrust) {
			order.setState(FuturesOrderState.Position);
			order.setUpdateTime(new Date());
			orderDao.update(order);
			if (order.getWindControlType() != null
					&& order.getWindControlType() == FuturesWindControlType.ReachStrongPoint) {
				// 市价卖出
				this.sellingEntrust(order, FuturesWindControlType.ReachStrongPoint, FuturesTradePriceType.MKT, null);
			}
		} else {
			// 撤单退款
			accountBusiness.futuresOrderRevoke(order.getPublisherId(), order.getId(), order.getServiceFee());
			// 修改订单状态
			order.setState(FuturesOrderState.BuyingCanceled);
			order.setUpdateTime(new Date());
			orderDao.update(order);
			// 站外消息推送
			sendOutsideMessage(order);
		}
		return order;
	}

	/**
	 * 订单失败
	 * 
	 * <p>
	 * 通知是指令失败的情况出现，如委托价格不是最小波动的整数倍
	 * </p>
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder failureOrder(Long id) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Posted || order.getState() == FuturesOrderState.BuyingEntrust
				|| order.getState() == FuturesOrderState.SellingEntrust)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		if (order.getState() == FuturesOrderState.SellingEntrust) {
			order.setState(FuturesOrderState.Position);
			order.setUpdateTime(new Date());
			orderDao.update(order);
		} else {
			// 撤单退款
			accountBusiness.futuresOrderRevoke(order.getPublisherId(), order.getId(), order.getServiceFee());
			// 修改订单状态
			order.setState(FuturesOrderState.BuyingFailure);
			order.setUpdateTime(new Date());
			orderDao.update(order);
			// 站外消息推送
			sendOutsideMessage(order);
		}
		return order;
	}

	/**
	 * 持仓中
	 * 
	 * @param id
	 *            订单ID
	 * @param buyingPrice
	 *            买入价格
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder positionOrder(Long id, BigDecimal buyingPrice) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Posted || order.getState() == FuturesOrderState.BuyingEntrust
				|| order.getState() == FuturesOrderState.PartPosition)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		Date date = new Date();
		order.setBuyingPrice(buyingPrice);
		order.setBuyingTime(date);
		order.setState(FuturesOrderState.Position);
		order.setUpdateTime(date);
		orderDao.update(order);
		// 给渠道推广机构结算
		if (order.getIsTest() == null || order.getIsTest() == false) {
			orgBusiness.futuresSettlement(order.getPublisherId(), order.getContract().getCommodity().getId(),
					order.getId(), order.getTradeNo(), order.getTotalQuantity(), order.getOpenwindServiceFee(),
					order.getUnwindServiceFee());
		}
		// 站外消息推送
		sendOutsideMessage(order);
		// 放入监控队列
		monitorPublisher.monitorPublisher(order.getPublisherId());
		return order;
	}

	/**
	 * 持仓中
	 * 
	 * @param id
	 *            订单ID
	 * @param buyingPrice
	 *            买入价格
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder positionOrder(Long id, BigDecimal filled, BigDecimal remaining, BigDecimal avgFillPrice,
			BigDecimal totalFillCost, BigDecimal buyingPrice) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Posted || order.getState() == FuturesOrderState.BuyingEntrust
				|| order.getState() == FuturesOrderState.PartPosition)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		Date date = new Date();
		order.setBuyingPrice(buyingPrice);
		order.setBuyingTime(date);
		order.setOpenFilled(filled);
		order.setOpenRemaining(remaining);
		order.setOpenAvgFillPrice(avgFillPrice);
		order.setOpenTotalFillCost(totalFillCost);
		order.setState(FuturesOrderState.Position);
		order.setUpdateTime(date);
		orderDao.update(order);
		// 给渠道推广机构结算
		if (order.getIsTest() == null || order.getIsTest() == false) {
			orgBusiness.futuresSettlement(order.getPublisherId(), order.getContract().getCommodity().getId(),
					order.getId(), order.getTradeNo(), order.getTotalQuantity(), order.getOpenwindServiceFee(),
					order.getUnwindServiceFee());
		}
		// 站外消息推送
		sendOutsideMessage(order);
		// 放入监控队列
		monitorPublisher.monitorPublisher(order.getPublisherId());
		return order;
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

	/**
	 * 已平仓
	 * 
	 * @param id
	 *            订单ID
	 * @param sellingPrice
	 *            卖出价格
	 * @return 订单
	 */
	public FuturesOrder unwindOrder(Long id, BigDecimal sellingPrice) {
		FuturesOrder order = orderDao.retrieve(id);
		if (order.getState() == FuturesOrderState.Unwind) {
			return order;
		}
		if (!(order.getState() == FuturesOrderState.Position || order.getState() == FuturesOrderState.SellingEntrust
				|| order.getState() == FuturesOrderState.PartUnwind)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 盈亏（交易所货币）
		BigDecimal currencyProfitOrLoss = computeProfitOrLoss(order.getOrderType(), order.getTotalQuantity(),
				order.getBuyingPrice(), sellingPrice, order.getContract().getCommodity().getMinWave(),
				order.getContract().getCommodity().getPerWaveMoney());
		// 盈亏（人民币）
		BigDecimal rate = rateService.findByCurrency(order.getCommodityCurrency()).getRate();
		BigDecimal profitOrLoss = currencyProfitOrLoss.multiply(rate).setScale(2, RoundingMode.DOWN);
		// 给用户结算
		CapitalAccountDto account = accountBusiness.futuresOrderSettlement(order.getPublisherId(), order.getId(),
				profitOrLoss);
		// 发布人盈亏（人民币）、平台盈亏（人民币）
		BigDecimal publisherProfitOrLoss = BigDecimal.ZERO;
		BigDecimal platformProfitOrLoss = BigDecimal.ZERO;
		if (profitOrLoss.compareTo(BigDecimal.ZERO) > 0) {
			publisherProfitOrLoss = profitOrLoss;
		} else if (profitOrLoss.compareTo(BigDecimal.ZERO) < 0) {
			publisherProfitOrLoss = account.getRealProfitOrLoss();
			if (profitOrLoss.abs().compareTo(publisherProfitOrLoss.abs()) > 0) {
				platformProfitOrLoss = profitOrLoss.abs().subtract(publisherProfitOrLoss.abs())
						.multiply(new BigDecimal(-1));
			}
		}
		// 修改订单状态
		Date date = new Date();
		order.setCurrencyProfitOrLoss(currencyProfitOrLoss);
		order.setProfitOrLoss(profitOrLoss);
		order.setPublisherProfitOrLoss(publisherProfitOrLoss);
		order.setPlatformProfitOrLoss(platformProfitOrLoss);
		order.setSettlementRate(rate);
		order.setSellingPrice(sellingPrice);
		order.setSellingTime(date);
		order.setState(FuturesOrderState.Unwind);
		order.setUpdateTime(date);
		orderDao.update(order);
		// unwindReturnOvernightReserveFund(order);

		// 给代理商分成结算
		if (order.getIsTest() == null || order.getIsTest() == false) {
			// 递延费
			BigDecimal deferredFee = overnightService.getSUMOvernightRecord(order.getId());
			if (deferredFee == null) {
				deferredFee = BigDecimal.ZERO;
			}
			orgBusiness.futuresRatioSettlement(order.getPublisherId(), null, order.getId(), order.getTradeNo(),
					order.getTotalQuantity(), order.getServiceFee(), publisherProfitOrLoss, deferredFee);
		}
		// 站外消息推送
		sendOutsideMessage(order);
		return order;
	}

	/**
	 * 已平仓
	 * 
	 * @param id
	 *            订单ID
	 * @param sellingPrice
	 *            卖出价格
	 * @return 订单
	 */
	public FuturesOrder unwindOrder(Long id, BigDecimal filled, BigDecimal remaining, BigDecimal avgFillPrice,
			BigDecimal totalFillCost, BigDecimal sellingPrice) {
		FuturesOrder order = orderDao.retrieve(id);
		if (order.getState() == FuturesOrderState.Unwind) {
			return order;
		}
		if (!(order.getState() == FuturesOrderState.Position || order.getState() == FuturesOrderState.SellingEntrust
				|| order.getState() == FuturesOrderState.PartUnwind)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 盈亏（交易所货币）
		BigDecimal currencyProfitOrLoss = computeProfitOrLoss(order.getOrderType(), order.getTotalQuantity(),
				order.getBuyingPrice(), sellingPrice, order.getContract().getCommodity().getMinWave(),
				order.getContract().getCommodity().getPerWaveMoney());
		// 盈亏（人民币）
		BigDecimal rate = rateService.findByCurrency(order.getCommodityCurrency()).getRate();
		BigDecimal profitOrLoss = currencyProfitOrLoss.multiply(rate).setScale(2, RoundingMode.DOWN);
		// 给用户结算
		CapitalAccountDto account = accountBusiness.futuresOrderSettlement(order.getPublisherId(), order.getId(),
				profitOrLoss);
		// 发布人盈亏（人民币）、平台盈亏（人民币）
		BigDecimal publisherProfitOrLoss = BigDecimal.ZERO;
		BigDecimal platformProfitOrLoss = BigDecimal.ZERO;
		if (profitOrLoss.compareTo(BigDecimal.ZERO) > 0) {
			publisherProfitOrLoss = profitOrLoss;
		} else if (profitOrLoss.compareTo(BigDecimal.ZERO) < 0) {
			publisherProfitOrLoss = account.getRealProfitOrLoss();
			if (publisherProfitOrLoss == null) {
				publisherProfitOrLoss = profitOrLoss;
			}
			if (profitOrLoss.abs().compareTo(publisherProfitOrLoss.abs()) > 0) {
				platformProfitOrLoss = profitOrLoss.abs().subtract(publisherProfitOrLoss.abs())
						.multiply(new BigDecimal(-1));
			}
		}
		// 修改订单状态
		Date date = new Date();
		order.setCloseFilled(filled);
		order.setCloseRemaining(remaining);
		order.setCloseAvgFillPrice(avgFillPrice);
		order.setCloseTotalFillCost(totalFillCost);
		order.setCurrencyProfitOrLoss(currencyProfitOrLoss);
		order.setProfitOrLoss(profitOrLoss);
		order.setPublisherProfitOrLoss(publisherProfitOrLoss);
		order.setPlatformProfitOrLoss(platformProfitOrLoss);
		order.setSettlementRate(rate);
		order.setSellingPrice(sellingPrice);
		order.setSellingTime(date);
		order.setState(FuturesOrderState.Unwind);
		order.setUpdateTime(date);
		orderDao.update(order);
		// unwindReturnOvernightReserveFund(order);

		// 给代理商分成结算
		// if (order.getIsTest() == null || order.getIsTest() == false) {
		// 递延费
		BigDecimal deferredFee = overnightService.getSUMOvernightRecord(order.getId());
		if (deferredFee == null) {
			deferredFee = BigDecimal.ZERO;
		}
		orgBusiness.futuresRatioSettlement(order.getPublisherId(), order.getContract().getCommodityId(), order.getId(),
				order.getTradeNo(), order.getTotalQuantity(), order.getServiceFee(), publisherProfitOrLoss,
				deferredFee);
		// }

		// 站外消息推送
		sendOutsideMessage(order);
		return order;
	}

	/**
	 * 部分已平仓
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder partUnwindOrder(Long id) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Position || order.getState() == FuturesOrderState.SellingEntrust
				|| order.getState() == FuturesOrderState.PartUnwind)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		order.setState(FuturesOrderState.PartUnwind);
		order.setUpdateTime(new Date());
		return orderDao.update(order);
	}

	/**
	 * 部分已平仓
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder partUnwindOrder(Long id, BigDecimal filled, BigDecimal remaining, BigDecimal avgFillPrice,
			BigDecimal totalFillCost) {
		FuturesOrder order = orderDao.retrieve(id);
		if (!(order.getState() == FuturesOrderState.Position || order.getState() == FuturesOrderState.SellingEntrust
				|| order.getState() == FuturesOrderState.PartUnwind)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		order.setCloseFilled(filled);
		order.setCloseRemaining(remaining);
		order.setCloseAvgFillPrice(avgFillPrice);
		order.setCloseTotalFillCost(totalFillCost);
		order.setState(FuturesOrderState.PartUnwind);
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
	 * 卖出委托
	 * 
	 * @param order
	 *            订单
	 * @param windControlType
	 *            风控类型
	 * @param priceType
	 *            价格类型
	 * @param entrustPrice
	 *            委托价格
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder sellingEntrust(FuturesOrder order, FuturesWindControlType windControlType,
			FuturesTradePriceType priceType, BigDecimal entrustPrice) {
		// step 1 : 检查网关是否正常
		boolean isConnected = TradeFuturesOverHttp.checkConnection(profileBusiness.isProd());
		if (!isConnected) {
			throw new ServiceException(ExceptionConstant.FUTURESAPI_NOTCONNECTED_EXCEPTION);
		}
		// step 2 : 检查订单状态是否正确
		if (order.getState() != FuturesOrderState.Position) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// 修改订单状态
		order.setWindControlType(windControlType);
		order.setState(FuturesOrderState.SellingEntrust);
		Date date = new Date();
		order.setUpdateTime(date);
		order.setSellingEntrustTime(date);
		order.setSellingPriceType(priceType);
		if (entrustPrice == null && priceType == FuturesTradePriceType.MKT) {
			FuturesContractMarket market = RetriveFuturesOverHttp.market(profileBusiness.isProd(),
					order.getCommoditySymbol(), order.getContractNo());
			if (market != null && market.getLastPrice() != null
					&& market.getLastPrice().compareTo(BigDecimal.ZERO) > 0) {
				entrustPrice = market.getLastPrice();
			}
		}
		order.setSellingEntrustPrice(entrustPrice);
		order = orderDao.update(order);
		// 委托卖出
		// FuturesActionType action = order.getOrderType() ==
		// FuturesOrderType.BuyUp ? FuturesActionType.SELL
		// : FuturesActionType.BUY;
		// Integer orderType = priceType == FuturesTradePriceType.MKT ? 1 : 2;
		// //
		// 如果恒生指数或者小恒生，需做特殊处理，这两个只能以先定价下单，恒指和小恒指买涨在最新市价基础上增加3个点（按最波动点位来）。买跌减3个点
		// BigDecimal gatewayBuyingEntrustPrice = order.getBuyingEntrustPrice();
		// if (("CN".equals(order.getCommoditySymbol()) ||
		// "HSI".equals(order.getCommoditySymbol())
		// || "MHI".equals(order.getCommoditySymbol())) && orderType == 1) {
		// orderType = 2;
		// }
		// FuturesGatewayOrder gatewayOrder =
		// TradeFuturesOverHttp.placeOrder(profileBusiness.isProd(), domain,
		// order.getCommoditySymbol(), order.getContractNo(), order.getId(),
		// action, order.getTotalQuantity(),
		// orderType, gatewayBuyingEntrustPrice);
		// order.setCloseGatewayOrderId(gatewayOrder.getId());
		// TODO 委托下单异常情况处理，此处默认为所有的委托都能成功
		// 消息推送
		sendOutsideMessage(order);
		// 放入委托查询队列（平仓）
		if (windControlType == FuturesWindControlType.BackhandUnwind) {
			entrueQuery.entrustQuery(order.getId(), 3);
		} else {
			entrueQuery.entrustQuery(order.getId(), 2);
		}
		return order;
	}

	/**
	 * 隔夜
	 * 
	 * @param order
	 *            订单
	 * @return 订单
	 */
	@Transactional
	public FuturesOrder overnight(FuturesOrder order, Integer timeZoneGap) {
		if (order.getState() != FuturesOrderState.Position) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// step 1 : 检查余额是否充足
		CapitalAccountDto account = accountBusiness.fetchByPublisherId(order.getPublisherId());
		BigDecimal deferredFee = order.getOvernightPerUnitDeferredFee().multiply(order.getTotalQuantity());
		// BigDecimal reserveFund =
		// order.getOvernightPerUnitReserveFund().multiply(order.getTotalQuantity());
		BigDecimal reserveFund = BigDecimal.ZERO;
		// BigDecimal totalFee = deferredFee.add(reserveFund);
		BigDecimal totalFee = deferredFee;
		if (account.getAvailableBalance().compareTo(totalFee) < 0) {
			// step 1.1 : 余额不足，强制平仓
			return sellingEntrust(order, FuturesWindControlType.DayUnwind, FuturesTradePriceType.MKT, null);
		} else {
			// step 2 : 保存隔夜记录
			FuturesOvernightRecord overnightRecord = new FuturesOvernightRecord();
			overnightRecord.setOrder(order);
			overnightRecord.setOvernightDeferredFee(deferredFee);
			overnightRecord.setOvernightReserveFund(reserveFund);
			overnightRecord.setPublisherId(order.getPublisherId());
			overnightRecord.setReduceTime(new Date());
			Calendar cal = Calendar.getInstance();
			cal.setTime(retriveExchangeTime(new Date(), timeZoneGap));
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			overnightRecord.setDeferredTime(cal.getTime());
			overnightRecord = recordDao.create(overnightRecord);
			// step 4 : 修改订单状态
			order.setWindControlType(FuturesWindControlType.OvernightPosition);
			order.setUpdateTime(new Date());
			orderDao.update(order);
			// step 5 : 扣除隔夜递延费、冻结隔夜保证金
			if (deferredFee.compareTo(BigDecimal.ZERO) > 0 || reserveFund.compareTo(BigDecimal.ZERO) > 0) {
				try {
					accountBusiness.futuresOrderOvernight(order.getPublisherId(), overnightRecord.getId(), deferredFee,
							reserveFund);
					// 给渠道推广机构结算
					if (order.getIsTest() == null || order.getIsTest() == false) {
						orgBusiness.futuresDeferredSettlement(order.getPublisherId(),
								order.getContract().getCommodity().getId(), order.getId(), order.getTradeNo(),
								order.getTotalQuantity(), order.getOvernightPerUnitDeferredFee());
					}
				} catch (ServiceException ex) {
					if (ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION.equals(ex.getType())) {
						// step 1.1 : 余额不足，强制平仓
						order = sellingEntrust(order, FuturesWindControlType.DayUnwind, FuturesTradePriceType.MKT,
								null);
						orderDao.delete(overnightRecord.getId());
					} else {
						// 再一次确认是否已经扣款
						try {
							List<CapitalFlowDto> list = flowBusiness.fetchByExtendTypeAndExtendId(
									CapitalFlowExtendType.FUTURESOVERNIGHTRECORD, overnightRecord.getId());
							if (list == null || list.size() == 0) {
								throw ex;
							} else {
								// 给渠道推广机构结算
								if (order.getIsTest() == null || order.getIsTest() == false) {
									orgBusiness.futuresDeferredSettlement(order.getPublisherId(),
											order.getContract().getCommodity().getId(), overnightRecord.getId(),
											order.getTradeNo(), order.getTotalQuantity(),
											order.getOvernightPerUnitDeferredFee());
								}
							}
						} catch (ServiceException frozenEx) {
							throw ex;
						}
					}
				}
			}
		}
		return order;
	}

	public FuturesOrder cancelOrder(Long id, Long publisherId) {
		// step 1 : 检查网关是否正常
		boolean isConnected = TradeFuturesOverHttp.checkConnection(profileBusiness.isProd());
		if (!isConnected) {
			throw new ServiceException(ExceptionConstant.FUTURESAPI_NOTCONNECTED_EXCEPTION);
		}
		// step 2 : 检查订单状态
		FuturesOrder order = orderDao.retrieveByOrderIdAndPublisherId(id, publisherId);
		if (order == null) {
			throw new ServiceException(ExceptionConstant.USER_ORDER_DOESNOT_EXIST_EXCEPTION);
		}
		Integer timeZoneGap = this.retriveTimeZoneGap(order);
		boolean isTradeTime = isTradeTime(timeZoneGap, order.getContract(), new Date());
		if (!isTradeTime) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		// if (order.getState() == FuturesOrderState.PartPosition ||
		// order.getState() == FuturesOrderState.PartUnwind) {
		// throw new
		// ServiceException(ExceptionConstant.FUTURESORDER_PARTSUCCESS_CANNOTCANCEL_EXCEPTION);
		// }
		if (!(order.getState() == FuturesOrderState.BuyingEntrust || order.getState() == FuturesOrderState.PartPosition
				|| order.getState() == FuturesOrderState.SellingEntrust
				|| order.getState() == FuturesOrderState.PartUnwind)) {
			logger.error("state not match, orderId:{}, state:{}", order.getId(), order.getState());
			throw new ServiceException(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION);
		}
		// step 3 : 请求网关取消订单
		canceledOrder(id);
		// if (order.getState() == FuturesOrderState.BuyingEntrust) {
		// TradeFuturesOverHttp.cancelOrder(profileBusiness.isProd(), domain,
		// order.getOpenGatewayOrderId());
		// }
		// if (order.getState() == FuturesOrderState.SellingEntrust) {
		// throw new
		// ServiceException(ExceptionConstant.UNWINDORDER_CANNOTCANCEL_EXCEPTION);
		// // TradeFuturesOverHttp.cancelOrder(profileBusiness.isProd(),
		// // domain, order.getCloseGatewayOrderId());
		// }
		return order;
	}

	public FuturesOrder applyUnwind(Long orderId, FuturesTradePriceType priceType, BigDecimal sellingEntrustPrice,
			Long publisherId) {
		// 检查是否在交易时间段
		FuturesOrder order = orderDao.retrieveByOrderIdAndPublisherId(orderId, publisherId);
		if (order == null) {
			throw new ServiceException(ExceptionConstant.USER_ORDER_DOESNOT_EXIST_EXCEPTION);
		}
		Integer timeZoneGap = this.retriveTimeZoneGap(order);
		boolean isTradeTime = isTradeTime(timeZoneGap, order.getContract(), new Date());
		if (!isTradeTime) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		List<FuturesTradeLimit> limitList = futuresTradeLimitService.findByContractId(order.getContractId());
		if (limitList != null && limitList.size() > 0) {
			// 判断该交易平仓时是否在后台设置的期货交易限制内
			checkedLimitUnwind(limitList, retriveExchangeTime(new Date(), this.retriveTimeZoneGap(order)));
		}
		// 委托卖出
		return sellingEntrust(order, FuturesWindControlType.UserApplyUnwind, priceType, sellingEntrustPrice);
	}

	public void applyUnwindAll(Long publisherId) {
		// 获取持仓中的订单
		List<FuturesOrder> orderList = orderDao.retrieveByPublisherIdAndState(publisherId, FuturesOrderState.Position);
		// 检查是否在交易时间段
		for (FuturesOrder order : orderList) {
			Integer timeZoneGap = this.retriveTimeZoneGap(order);
			boolean isTradeTime = isTradeTime(timeZoneGap, order.getContract(), new Date());
			if (!isTradeTime) {
				throw new ServiceException(ExceptionConstant.PARTCONTRACT_NOTINTRADETIME_EXCEPTION);
			}
			List<FuturesTradeLimit> limitList = futuresTradeLimitService.findByContractId(order.getContractId());
			if (limitList != null && limitList.size() > 0) {
				// 判断该交易平仓时是否在后台设置的期货交易限制内
				checkedLimitUnwind(limitList, retriveExchangeTime(new Date(), this.retriveTimeZoneGap(order)));
			}
		}
		// 委托卖出
		for (FuturesOrder order : orderList) {
			sellingEntrust(order, FuturesWindControlType.UserApplyUnwind, FuturesTradePriceType.MKT, null);
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

	public FuturesOrder backhandPlaceOrder(Long orderId) {
		FuturesOrder order = orderDao.retrieve(orderId);
		if (order.getState() != FuturesOrderState.Unwind) {
			throw new ServiceException(ExceptionConstant.BACKHANDSOURCEORDER_NOTUNWIND_EXCEPTION);
		}
		List<FuturesOrder> checkOrder = orderDao.retrieveByBackhandSourceOrderId(orderId);
		if (checkOrder != null && checkOrder.size() > 0) {
			throw new ServiceException(ExceptionConstant.FUTURESORDER_ALREADYBACKHAND_EXCEPTION);
		}
		// 反手下单
		FuturesOrder backhandOrder = new FuturesOrder();
		backhandOrder.setBackhandSourceOrderId(orderId);
		FuturesContract contract = order.getContract();
		FuturesCommodity commodity = contract.getCommodity();
		Long commodityId = commodity.getId();
		commodity.setId(null);
		wrapperAgentPrice(order.getPublisherId(), commodityId, commodity);
		// 计算服务费和保证金
		BigDecimal serviceFee = order.getTotalQuantity().multiply(
				contract.getCommodity().getOpenwindServiceFee().add(contract.getCommodity().getUnwindServiceFee()));
		// 获取运营后台设置的止损止盈
		backhandOrder.setStopLossOrProfitId(order.getStopLossOrProfitId());
		FuturesStopLossOrProfit lossOrProfit = stopLossOrProfitDao.retrieve(order.getStopLossOrProfitId());
		BigDecimal reserveFund = order.getReserveFund();
		if (lossOrProfit != null) {
			FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
			reserveFund = order.getTotalQuantity().multiply(lossOrProfit.getReserveFund().multiply(rate.getRate()));
			backhandOrder.setPerUnitUnwindPoint(lossOrProfit.getStrongLevelingAmount());
			backhandOrder.setUnwindPointType(2);
		} else {
			backhandOrder.setPerUnitUnwindPoint(order.getPerUnitUnwindPoint());
			backhandOrder.setUnwindPointType(2);
		}
		backhandOrder.setLimitLossType(order.getLimitLossType());
		backhandOrder.setPerUnitLimitLossAmount(order.getPerUnitLimitLossAmount());
		backhandOrder.setLimitProfitType(order.getLimitProfitType());
		backhandOrder.setPerUnitLimitProfitAmount(order.getPerUnitLimitProfitAmount());
		backhandOrder.setStopLossOrProfitId(order.getStopLossOrProfitId());
		// 初始化部分订单信息
		backhandOrder.setPublisherId(order.getPublisherId());
		backhandOrder.setOrderType(
				order.getOrderType() == FuturesOrderType.BuyUp ? FuturesOrderType.BuyFall : FuturesOrderType.BuyUp);
		backhandOrder.setTotalQuantity(order.getTotalQuantity());
		backhandOrder.setReserveFund(reserveFund);
		backhandOrder.setServiceFee(serviceFee);
		backhandOrder.setCommoditySymbol(commodity.getSymbol());
		backhandOrder.setCommodityName(commodity.getName());
		backhandOrder.setCommodityCurrency(commodity.getCurrency());
		backhandOrder.setContractNo(contract.getContractNo());
		backhandOrder.setOpenwindServiceFee(commodity.getOpenwindServiceFee());
		backhandOrder.setUnwindServiceFee(commodity.getUnwindServiceFee());
		backhandOrder.setUnwindPointType(commodity.getUnwindPointType());
		backhandOrder.setOvernightPerUnitReserveFund(commodity.getOvernightPerUnitReserveFund());
		backhandOrder.setOvernightPerUnitDeferredFee(commodity.getOvernightPerUnitDeferredFee());
		backhandOrder.setBuyingPriceType(FuturesTradePriceType.MKT);
		// 获取是否为测试单
		PublisherDto publisher = publisherBusiness.findById(order.getPublisherId());
		backhandOrder.setIsTest(publisher.getIsTest());
		// 请求下单
		try {
			return save(backhandOrder, contract.getId());
		} catch (ServiceException ex) {
			if (!ex.getType().equals(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION)) {
				throw ex;
			} else {
				logger.error("余额不足，反手失败，orderId:{}", orderId);
				return null;
			}
		}
	}

	public FuturesOrder backhandUnwind(Long orderId, Long publisherId) {
		// 检查是否在交易时间段
		FuturesOrder order = orderDao.retrieveByOrderIdAndPublisherId(orderId, publisherId);
		if (order == null) {
			throw new ServiceException(ExceptionConstant.USER_ORDER_DOESNOT_EXIST_EXCEPTION);
		}
		Integer timeZoneGap = this.retriveTimeZoneGap(order);
		boolean isTradeTime = isTradeTime(timeZoneGap, order.getContract(), new Date());
		if (!isTradeTime) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		List<FuturesTradeLimit> limitList = futuresTradeLimitService.findByContractId(order.getContractId());
		if (limitList != null && limitList.size() > 0) {
			// 判断该交易平仓时是否在后台设置的期货交易限制内
			checkedLimitUnwind(limitList, retriveExchangeTime(new Date(), this.retriveTimeZoneGap(order)));
		}
		// 判断账户余额是否足够支付反手买入的保证金和服务费
		FuturesContract contract = order.getContract();
		FuturesCommodity commodity = contract.getCommodity();
		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
		// 获取运营后台设置的止损止盈
		FuturesStopLossOrProfit lossOrProfit = stopLossOrProfitDao.retrieve(order.getStopLossOrProfitId());
		BigDecimal totalFee = BigDecimal.ZERO;
		if (lossOrProfit != null) {
			totalFee = order.getTotalQuantity().multiply(lossOrProfit.getReserveFund().multiply(rate.getRate())
					.add(commodity.getOpenwindServiceFee()).add(commodity.getUnwindServiceFee()));
		} else {
			totalFee = order.getTotalQuantity()
					.multiply(commodity.getOpenwindServiceFee().add(commodity.getUnwindServiceFee()))
					.add(order.getReserveFund());
		}
		CapitalAccountDto account = accountBusiness.fetchByPublisherId(order.getPublisherId());
		if (account.getAvailableBalance().compareTo(totalFee) < 0) {
			throw new ServiceException(ExceptionConstant.FUTURESORDER_BACKHAND_BALANCENOTENOUGH_EXCEPTION);
		}
		return sellingEntrust(order, FuturesWindControlType.BackhandUnwind, FuturesTradePriceType.MKT, null);
	}

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

	public FuturesOrder settingStopLoss(Long orderId, Integer limitProfitType, BigDecimal perUnitLimitProfitAmount,
			Integer limitLossType, BigDecimal perUnitLimitLossAmount, Long publisherId, Long stopLossOrProfitId) {
		FuturesOrder order = orderDao.retrieveByOrderIdAndPublisherId(orderId, publisherId);
		if (order == null) {
			throw new ServiceException(ExceptionConstant.USER_ORDER_DOESNOT_EXIST_EXCEPTION);
		}
		if (order.getState() == FuturesOrderState.Unwind) {
			throw new ServiceException(ExceptionConstant.ORDER_HAS_BEEN_CLOSED_EXCEPTION);
		}
		Integer timeZoneGap = this.retriveTimeZoneGap(order);
		boolean isTradeTime = isTradeTime(timeZoneGap, order.getContract(), new Date());
		if (!isTradeTime) {
			throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
		}
		// 获取运营后台设置的档位信息
		FuturesStopLossOrProfit lossOrProfit = stopLossOrProfitDao.retrieve(stopLossOrProfitId);
		if (lossOrProfit == null) {
			throw new ServiceException(ExceptionConstant.SETTING_STOP_LOSS_EXCEPTION);
		}
		order.setPerUnitUnwindPoint(lossOrProfit.getStrongLevelingAmount());

		// if (limitProfitType != null && perUnitLimitProfitAmount != null) {
		order.setLimitProfitType(limitProfitType);
		order.setPerUnitLimitProfitAmount(perUnitLimitProfitAmount);
		// }
		// if (limitLossType != null && perUnitLimitLossAmount != null) {
		order.setLimitLossType(limitLossType);
		order.setPerUnitLimitLossAmount(perUnitLimitLossAmount);
		order.setStopLossOrProfitId(stopLossOrProfitId);
		// }
		orderDao.update(order);
		return order;
	}

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

	public BigDecimal getProfitOrLoss(FuturesOrder order) {
		String commodityNo = order.getCommoditySymbol();
		String contractNo = order.getContractNo();
		BigDecimal lastPrice = allQuote.getLastPrice(commodityNo, contractNo);
		BigDecimal buyingPrice = order.getBuyingPrice();
		// 货币汇率
		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
		// 计算浮动盈亏
		if (lastPrice != null) {
			if (order.getOrderType() == FuturesOrderType.BuyUp) {
				return lastPrice.subtract(buyingPrice).divide(order.getContract().getCommodity().getMinWave())
						.multiply(order.getContract().getCommodity().getPerWaveMoney()).multiply(rate.getRate())
						.multiply(order.getTotalQuantity());
			} else {
				return buyingPrice.subtract(lastPrice).divide(order.getContract().getCommodity().getMinWave())
						.multiply(order.getContract().getCommodity().getPerWaveMoney()).multiply(rate.getRate())
						.multiply(order.getTotalQuantity());
			}
		} else {
			return BigDecimal.ZERO;
		}
	}

	public BigDecimal getStrongMoney(FuturesOrder order) {
		FuturesCurrencyRate rate = rateService.findByCurrency(order.getCommodityCurrency());
		// 合约设置
		Integer unwindPointType = order.getUnwindPointType();
		BigDecimal perUnitUnwindPoint = order.getPerUnitUnwindPoint();
		if (unwindPointType != null && perUnitUnwindPoint != null && unwindPointType == 1) {
			if (perUnitUnwindPoint != null && perUnitUnwindPoint.compareTo(new BigDecimal(100)) < 0
					&& perUnitUnwindPoint.compareTo(new BigDecimal(0)) > 0) {
				return order.getReserveFund()
						.multiply(new BigDecimal(100).subtract(perUnitUnwindPoint).divide(new BigDecimal(100)));
			}
		} else if (unwindPointType != null && perUnitUnwindPoint != null && unwindPointType == 2) {
			if (perUnitUnwindPoint != null && perUnitUnwindPoint.compareTo(BigDecimal.ZERO) >= 0
					&& perUnitUnwindPoint.compareTo(new BigDecimal(0)) > 0) {
				BigDecimal strongMoney = order.getReserveFund().subtract(perUnitUnwindPoint
						.multiply(order.getTotalQuantity()).multiply(rate.getRate()).setScale(2, RoundingMode.DOWN));
				if (strongMoney.compareTo(BigDecimal.ZERO) <= 0) {
					return order.getReserveFund();
				} else {
					return strongMoney;
				}
			}
		}
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
			totalProfitOrLoss = totalProfitOrLoss.add(this.getProfitOrLoss(order));
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

}
