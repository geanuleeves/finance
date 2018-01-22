package com.waben.stock.datalayer.buyrecord.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.waben.stock.datalayer.buyrecord.business.CapitalAccountBusiness;
import com.waben.stock.datalayer.buyrecord.business.HolidayBusiness;
import com.waben.stock.datalayer.buyrecord.business.StrategyTypeBusiness;
import com.waben.stock.datalayer.buyrecord.entity.BuyRecord;
import com.waben.stock.datalayer.buyrecord.entity.DeferredRecord;
import com.waben.stock.datalayer.buyrecord.entity.Settlement;
import com.waben.stock.datalayer.buyrecord.repository.BuyRecordDao;
import com.waben.stock.datalayer.buyrecord.repository.DeferredRecordDao;
import com.waben.stock.datalayer.buyrecord.repository.SettlementDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.FrozenCapitalDto;
import com.waben.stock.interfaces.dto.stockcontent.StrategyTypeDto;
import com.waben.stock.interfaces.enums.BuyRecordState;
import com.waben.stock.interfaces.enums.FrozenCapitalStatus;
import com.waben.stock.interfaces.enums.WindControlType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.query.BuyRecordQuery;
import com.waben.stock.interfaces.pojo.query.StrategyHoldingQuery;
import com.waben.stock.interfaces.pojo.query.StrategyPostedQuery;
import com.waben.stock.interfaces.pojo.query.StrategyUnwindQuery;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

/**
 * 点买记录 Service
 *
 * @author luomengan
 */
@Service
public class BuyRecordService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private BuyRecordDao buyRecordDao;

	@Autowired
	private SettlementDao settlementDao;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private StrategyTypeBusiness strategyTypeBusiness;

	@Autowired
	private DeferredRecordDao deferredRecordDao;

	@Autowired
	private HolidayBusiness holidayBusiness;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public BuyRecord findBuyRecord(Long buyrecord) {
		BuyRecord buyRecord = buyRecordDao.retrieve(buyrecord);
		if (buyRecord == null) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_NOT_FOUND_EXCEPTION);
		}
		return buyRecord;
	}

	@Transactional
	public BuyRecord save(BuyRecord buyRecord) {
		buyRecord.setSerialCode(UniqueCodeGenerator.generateSerialCode());
		buyRecord.setTradeNo(UniqueCodeGenerator.generateTradeNo());
		buyRecord.setState(BuyRecordState.POSTED);
		Date date = new Date();
		buyRecord.setCreateTime(date);
		buyRecord.setUpdateTime(date);
		// 根据委托价格计算持股数
		BigDecimal temp = buyRecord.getApplyAmount().divide(buyRecord.getDelegatePrice(), 2, RoundingMode.HALF_DOWN);
		Integer numberOfStrand = temp.divideAndRemainder(BigDecimal.valueOf(100))[0].multiply(BigDecimal.valueOf(100))
				.intValue();
		buyRecord.setNumberOfStrand(numberOfStrand);
		buyRecordDao.create(buyRecord);
		// 扣去金额、冻结保证金
		try {
			accountBusiness.serviceFeeAndReserveFund(buyRecord.getPublisherId(), buyRecord.getId(),
					buyRecord.getServiceFee(), buyRecord.getReserveFund(), buyRecord.getDeferredFee());
		} catch (ServiceException ex) {
			if (ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION.equals(ex.getType())) {
				throw ex;
			} else {
				// 再一次确认是否已经扣款
				try {
					FrozenCapitalDto frozen = accountBusiness.fetchFrozenCapital(buyRecord.getPublisherId(),
							buyRecord.getId());
					if (frozen == null) {
						throw ex;
					}
				} catch (ServiceException frozenEx) {
					throw ex;
				}
			}
		}
		return buyRecord;
	}

	public Page<BuyRecord> pagesByQuery(final BuyRecordQuery buyRecordQuery) {
		Pageable pageable = new PageRequest(buyRecordQuery.getPage(), buyRecordQuery.getSize());
		Page<BuyRecord> pages = buyRecordDao.page(new Specification<BuyRecord>() {
			@Override
			public Predicate toPredicate(Root<BuyRecord> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (buyRecordQuery.getStates() != null && buyRecordQuery.getStates().length > 0) {
					predicateList.add(root.get("state").in(buyRecordQuery.getStates()));
				}
				if (buyRecordQuery.getPublisherId() != null && buyRecordQuery.getPublisherId() > 0) {
					predicateList.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class),
							buyRecordQuery.getPublisherId()));
				}
				if (buyRecordQuery.getStartCreateTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
							buyRecordQuery.getStartCreateTime()));
				}
				if (buyRecordQuery.getEndCreateTime() != null) {
					predicateList.add(criteriaBuilder.lessThan(root.get("createTime").as(Date.class),
							buyRecordQuery.getEndCreateTime()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("sellingTime").as(Long.class)),
						criteriaBuilder.desc(root.get("buyingTime").as(Long.class)),
						criteriaBuilder.desc(root.get("createTime").as(Long.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public List<BuyRecord> fetchByStateAndOrderByCreateTime(BuyRecordState buyRecordState) {
		List<BuyRecord> result = buyRecordDao.retieveByStateAndOrderByCreateTime(buyRecordState);
		return result;
	}

	@Transactional
	public void remove(Long buyRecordId) {
		buyRecordDao.delete(buyRecordId);
	}

	@Transactional
	public BuyRecord changeState(BuyRecord record, boolean isSellLock) {
		BuyRecordState current = record.getState();
		BuyRecordState next = BuyRecordState.UNKONWN;
		if (BuyRecordState.POSTED.equals(current)) {
			next = BuyRecordState.BUYLOCK;
		} else if (BuyRecordState.BUYLOCK.equals(current)) {
			next = BuyRecordState.HOLDPOSITION;
		} else if (BuyRecordState.HOLDPOSITION.equals(current) && isSellLock == false) {
			next = BuyRecordState.SELLAPPLY;
		} else if ((BuyRecordState.HOLDPOSITION.equals(current) || BuyRecordState.SELLAPPLY.equals(current))
				&& isSellLock == true) {
			next = BuyRecordState.SELLLOCK;
		} else if (BuyRecordState.SELLLOCK.equals(current)) {
			next = BuyRecordState.UNWIND;
		}
		record.setState(next);
		record.setUpdateTime(new Date());
		BuyRecord result = buyRecordDao.update(record);
		return result;
	}

	@Transactional
	public BuyRecord buyLock(Long investorId, Long id, String delegateNumber) {
		BuyRecord buyRecord = findBuyRecord(id);
		if (buyRecord.getState() != BuyRecordState.POSTED) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION);
		}
		buyRecord.setInvestorId(investorId);
		buyRecord.setDelegateNumber(delegateNumber);
		// 修改点买记录状态
		return changeState(buyRecord, false);
	}

	@Transactional
	public BuyRecord buyInto(Long investorId, Long id, BigDecimal buyingPrice) {
		BuyRecord buyRecord = findBuyRecord(id);
		if (buyRecord.getState() != BuyRecordState.BUYLOCK) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_ISNOTLOCK_EXCEPTION);
		}
		logger.info("订单是否相等:{},{}", investorId, buyRecord.getInvestorId());
		if (!investorId.equals(buyRecord.getInvestorId())) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_INVESTORID_NOTMATCH_EXCEPTION);
		}
		buyRecord.setBuyingPrice(buyingPrice);
		Date date = new Date();
		buyRecord.setBuyingTime(date);
		// 止盈点位价格 = 买入价格 + ((市值 * 止盈点)/股数)
		buyRecord.setProfitPosition(buyingPrice.add(buyRecord.getApplyAmount().multiply(buyRecord.getProfitPoint())
				.divide(new BigDecimal(buyRecord.getNumberOfStrand()), 2, RoundingMode.HALF_UP)));
		// 止损点位价格 = 买入价格 - ((市值 * 止损点)/股数)
		buyRecord.setLossPosition(
				buyingPrice.subtract(buyRecord.getApplyAmount().multiply(buyRecord.getLossPoint().abs())
						.divide(new BigDecimal(buyRecord.getNumberOfStrand()), 2, RoundingMode.HALF_UP)));
		// 止盈预警点位价格 = (止盈点位 - 买入点位) * 90% + 买入点位
		buyRecord.setProfitWarnPosition(buyRecord.getProfitPosition().subtract(buyRecord.getBuyingPrice())
				.multiply(new BigDecimal(0.9)).setScale(2, RoundingMode.HALF_UP).add(buyRecord.getBuyingPrice()));
		// 止损预警点位价格 = 买入点位 - (买入点位 - 止损点位) * 90%
		buyRecord.setLossWarnPosition(
				buyRecord.getBuyingPrice().subtract(buyRecord.getBuyingPrice().subtract(buyRecord.getLossPosition())
						.multiply(new BigDecimal(0.9)).setScale(2, RoundingMode.HALF_UP)));
		// 修改点买记录状态
		StrategyTypeDto strategyType = strategyTypeBusiness.fetchById(buyRecord.getStrategyTypeId());
		buyRecord.setExpireTime(holidayBusiness.getAfterTradeDate(date, strategyType.getCycle() + 1));
		return changeState(buyRecord, false);
	}

	@Transactional
	public BuyRecord sellApply(Long publisherId, Long id) {
		BuyRecord buyRecord = findBuyRecord(id);
		if (buyRecord.getState() != BuyRecordState.HOLDPOSITION) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION);
		}
		if (!buyRecord.getPublisherId().equals(publisherId)) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_PUBLISHERID_NOTMATCH_EXCEPTION);
		}
		buyRecord.setWindControlType(WindControlType.PUBLISHERAPPLY);
		// 修改点买记录状态
		return changeState(buyRecord, false);
	}

	@Transactional
	public BuyRecord sellLock(Long investorId, Long id, String delegateNumber, WindControlType windControlType) {
		BuyRecord buyRecord = findBuyRecord(id);
		if (buyRecord.getState() != BuyRecordState.HOLDPOSITION && buyRecord.getState() != BuyRecordState.SELLAPPLY) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION);
		}

		if (buyRecord.getWindControlType() == null) {
			buyRecord.setWindControlType(windControlType);
		}
		buyRecord.setDelegateNumber(delegateNumber);
		// 修改点买记录状态
		return changeState(buyRecord, true);
	}

	@Transactional
	public BuyRecord sellOut(Long investorId, Long id, BigDecimal sellingPrice) {
		BuyRecord buyRecord = buyRecordDao.retrieve(id);
		if (buyRecord.getState() != BuyRecordState.SELLLOCK) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_ISNOTLOCK_EXCEPTION);
		}
		buyRecord.setSellingPrice(sellingPrice);
		buyRecord.setSellingTime(new Date());
		BigDecimal profitOrLoss = sellingPrice.subtract(buyRecord.getBuyingPrice())
				.multiply(new BigDecimal(buyRecord.getNumberOfStrand()));
		// 结算
		Settlement settlement = new Settlement();
		settlement.setBuyRecord(buyRecord);
		settlement.setProfitOrLoss(profitOrLoss);
		settlement.setInvestorProfitOrLoss(new BigDecimal(0));
		settlement.setSettlementTime(new Date());
		if (profitOrLoss.compareTo(BigDecimal.ZERO) >= 0) {
			settlement.setPublisherProfitOrLoss(profitOrLoss.multiply(new BigDecimal(0.9)));
			settlement.setInvestorProfitOrLoss(profitOrLoss.multiply(new BigDecimal(1).subtract(new BigDecimal(0.9))));
		} else {
			if (profitOrLoss.abs().compareTo(buyRecord.getReserveFund()) > 0) {
				settlement.setPublisherProfitOrLoss(buyRecord.getReserveFund().multiply(new BigDecimal(-1)));
				settlement.setInvestorProfitOrLoss(profitOrLoss.subtract(settlement.getPublisherProfitOrLoss()));
			} else {
				settlement.setPublisherProfitOrLoss(profitOrLoss);
			}
		}
		settlementDao.create(settlement);
		// 退回保证金
		try {
			accountBusiness.returnReserveFund(buyRecord.getPublisherId(), buyRecord.getId(), buyRecord.getSerialCode(),
					settlement.getPublisherProfitOrLoss());
		} catch (ServiceException ex) {
			// 再一次确认是否已经退回保证金
			try {
				FrozenCapitalDto frozen = accountBusiness.fetchFrozenCapital(buyRecord.getPublisherId(),
						buyRecord.getId());
				if (frozen == null || frozen.getStatus() != FrozenCapitalStatus.Thaw) {
					// 退回保证金异常
					throw new ServiceException(ExceptionConstant.BUYRECORD_RETURNRESERVEFUND_EXCEPTION);
				}
			} catch (ServiceException frozenEx) {
				throw ex;
			}
		}
		// 如果点买记录勾选了递延，判断是否递延了，如果没递延，退回递延费，递延了则产生递延记录
		Date expireTime = buyRecord.getExpireTime();
		if (buyRecord.getDeferred() && buyRecord.getDeferredFee() != null
				&& buyRecord.getDeferredFee().compareTo(new BigDecimal(0)) > 0 && expireTime != null) {
			String nowStr = sdf.format(new Date());
			String expireStr = sdf.format(expireTime);
			if (nowStr.compareTo(expireStr) < 0) {
				// 退回递延费
				accountBusiness.returnDeferredFee(buyRecord.getPublisherId(), buyRecord.getId(),
						buyRecord.getDeferredFee());
			} else {
				// 生成递延记录
				StrategyTypeDto strategyType = strategyTypeBusiness.fetchById(buyRecord.getStrategyTypeId());
				DeferredRecord deferredRecord = new DeferredRecord();
				deferredRecord.setBuyRecordId(id);
				deferredRecord.setCycle(1);
				deferredRecord.setDeferredTime(new Date());
				deferredRecord.setFee(buyRecord.getDeferredFee());
				deferredRecord.setPublisherId(buyRecord.getPublisherId());
				deferredRecord.setStrategyTypeId(strategyType.getId());
				deferredRecord.setStrategyTypeName(strategyType.getName());
				deferredRecordDao.create(deferredRecord);
			}
		}
		// 修改点买记录状态
		return changeState(buyRecord, false);
	}

	@Deprecated
	public BuyRecord deferred(Long id) {
		BuyRecord buyRecord = buyRecordDao.retrieve(id);
		if (buyRecord == null) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_NOT_FOUND_EXCEPTION);
		}
		if (!buyRecord.getDeferred()) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_USERNOTDEFERRED_EXCEPTION);
		}
		DeferredRecord deferredRecord = deferredRecordDao
				.retrieveByPublisherIdAndBuyRecordId(buyRecord.getPublisherId(), id);
		if (deferredRecord != null) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_ALREADY_DEFERRED_EXCEPTION);
		}
		// 获取策略类型
		StrategyTypeDto strategyType = strategyTypeBusiness.fetchById(buyRecord.getStrategyTypeId());
		deferredRecord = new DeferredRecord();
		deferredRecord.setBuyRecordId(id);
		deferredRecord.setCycle(strategyType.getCycle());
		deferredRecord.setDeferredTime(new Date());
		deferredRecord.setFee(new BigDecimal(strategyType.getDeferred()));
		deferredRecord.setPublisherId(buyRecord.getPublisherId());
		deferredRecord.setStrategyTypeId(strategyType.getId());
		deferredRecord.setStrategyTypeName(strategyType.getName());
		deferredRecordDao.create(deferredRecord);
		// buyRecord.setExpireTime(holidayBusiness.getAfterTradeDate(buyRecord.getExpireTime(),
		// 1));
		// buyRecordDao.update(buyRecord);
		// 扣递延费
		accountBusiness.deferredCharges(buyRecord.getPublisherId(), id, deferredRecord.getFee());
		return buyRecord;
	}

	public Integer strategyJoinCount(Long publisherId, Long strategyTypeId) {
		return buyRecordDao.strategyJoinCount(publisherId, strategyTypeId);
	}

	public Page<BuyRecord> pagesByPostedQuery(final StrategyPostedQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<BuyRecord> pages = buyRecordDao.page(new Specification<BuyRecord>() {
			@Override
			public Predicate toPredicate(Root<BuyRecord> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicatesList = new ArrayList<Predicate>();
				Predicate state = criteriaBuilder.in(root.get("state")).value(BuyRecordState.POSTED)
						.value(BuyRecordState.BUYLOCK);
				predicatesList.add(state);
				if (!StringUtils.isEmpty(query.getPublisherPhone())) {
					Predicate publisherPhoneQuery = criteriaBuilder.like(root.get("publisherPhone").as(String.class),
							"%" + query.getPublisherPhone() + "%");
					predicatesList.add(criteriaBuilder.and(publisherPhoneQuery));
				}
				if (!StringUtils.isEmpty(query.getStockName())) {
					Predicate stockNameQuery = criteriaBuilder.like(root.get("stockName").as(String.class),
							"%" + query.getStockName() + "%");
					predicatesList.add(criteriaBuilder.and(stockNameQuery));
				}
				if (!StringUtils.isEmpty(query.getBeginTime()) && !StringUtils.isEmpty(query.getEndTime())) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date beginTime = null;
					Date endTime = null;
					try {
						beginTime = sdf.parse(query.getBeginTime());
						endTime = sdf.parse(query.getEndTime());
					} catch (ParseException e) {
						throw new ServiceException(ExceptionConstant.DATETIME_ERROR);
					}
					Predicate createTimeQuery = criteriaBuilder.between(root.<Date>get("createTime").as(Date.class),
							beginTime, endTime);
					predicatesList.add(criteriaBuilder.and(createTimeQuery));
				}
				criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.<Date>get("createTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public Page<BuyRecord> pagesByHoldingQuery(final StrategyHoldingQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<BuyRecord> pages = buyRecordDao.page(new Specification<BuyRecord>() {
			@Override
			public Predicate toPredicate(Root<BuyRecord> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicatesList = new ArrayList<Predicate>();
				Predicate state = criteriaBuilder.in(root.get("state")).value(BuyRecordState.SELLAPPLY)
						.value(BuyRecordState.SELLLOCK).value(BuyRecordState.HOLDPOSITION);
				predicatesList.add(criteriaBuilder.and(state));
				if (!StringUtils.isEmpty(query.getPublisherPhone())) {
					Predicate publisherPhoneQuery = criteriaBuilder.like(root.get("publisherPhone").as(String.class),
							"%" + query.getPublisherPhone() + "%");
					predicatesList.add(criteriaBuilder.and(publisherPhoneQuery));
				}
				if (!StringUtils.isEmpty(query.getStockName())) {
					Predicate stockNameQuery = criteriaBuilder.like(root.get("stockName").as(String.class),
							"%" + query.getStockName() + "%");
					predicatesList.add(criteriaBuilder.and(stockNameQuery));
				}
				if (!StringUtils.isEmpty(query.getInvestorName())) {
					Predicate investorNameQuery = criteriaBuilder.like(root.get("investorName").as(String.class),
							"%" + query.getInvestorName() + "%");
					predicatesList.add(criteriaBuilder.and(investorNameQuery));
				}
				if (!StringUtils.isEmpty(query.getBeginTime()) && !StringUtils.isEmpty(query.getEndTime())) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date beginTime = null;
					Date endTime = null;
					try {
						beginTime = sdf.parse(query.getBeginTime());
						endTime = sdf.parse(query.getEndTime());
					} catch (ParseException e) {
						throw new ServiceException(ExceptionConstant.DATETIME_ERROR);
					}
					Predicate createTimeQuery = criteriaBuilder.between(root.<Date>get("createTime").as(Date.class),
							beginTime, endTime);
					predicatesList.add(criteriaBuilder.and(createTimeQuery));
				}
				criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.<Date>get("createTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public Page<BuyRecord> pagesByUnwindQuery(final StrategyUnwindQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<BuyRecord> pages = buyRecordDao.page(new Specification<BuyRecord>() {
			@Override
			public Predicate toPredicate(Root<BuyRecord> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicatesList = new ArrayList<Predicate>();
				Predicate state = criteriaBuilder.equal(root.get("state").as(BuyRecordState.class),
						BuyRecordState.UNWIND);
				predicatesList.add(criteriaBuilder.and(state));
				if (!StringUtils.isEmpty(query.getPublisherPhone())) {
					Predicate publisherPhoneQuery = criteriaBuilder.like(root.get("publisherPhone").as(String.class),
							"%" + query.getPublisherPhone() + "%");
					predicatesList.add(criteriaBuilder.and(publisherPhoneQuery));
				}
				if (!StringUtils.isEmpty(query.getStockName())) {
					Predicate stockNameQuery = criteriaBuilder.like(root.get("stockName").as(String.class),
							"%" + query.getStockName() + "%");
					predicatesList.add(criteriaBuilder.and(stockNameQuery));
				}
				if (!StringUtils.isEmpty(query.getInvestorName())) {
					Predicate investorNameQuery = criteriaBuilder.like(root.get("investorName").as(String.class),
							"%" + query.getInvestorName() + "%");
					predicatesList.add(criteriaBuilder.and(investorNameQuery));
				}
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date beginTime = null;
				Date endTime = null;
				Predicate createTimeQuery = null;
				try {
					if (!StringUtils.isEmpty(query.getBuyBeginTime()) && !StringUtils.isEmpty(query.getBuyEndTime())) {
						beginTime = sdf.parse(query.getBuyBeginTime());
						endTime = sdf.parse(query.getBuyEndTime());
						createTimeQuery = criteriaBuilder.between(root.<Date>get("createTime").as(Date.class),
								beginTime, endTime);
						predicatesList.add(criteriaBuilder.and(createTimeQuery));
					}
					if (!StringUtils.isEmpty(query.getSellBeginTime())
							&& !StringUtils.isEmpty(query.getSellEndTime())) {
						beginTime = sdf.parse(query.getSellBeginTime());
						endTime = sdf.parse(query.getSellEndTime());
						createTimeQuery = criteriaBuilder.between(root.<Date>get("createTime").as(Date.class),
								beginTime, endTime);
						predicatesList.add(criteriaBuilder.and(createTimeQuery));
					}
				} catch (ParseException e) {
					throw new ServiceException(ExceptionConstant.DATETIME_ERROR);
				}
				criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
				criteriaQuery.orderBy(criteriaBuilder.desc(root.<Date>get("createTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public BuyRecord revoke(Long id) {
		BuyRecord buyRecord = buyRecordDao.retrieve(id);
		if (buyRecord == null) {
			throw new ServiceException(ExceptionConstant.BUYRECORD_NOT_FOUND_EXCEPTION);
		}
//		if (!(buyRecord.getState() == BuyRecordState.POSTED || buyRecord.getState() == BuyRecordState.BUYLOCK )) {
//			throw new ServiceException(ExceptionConstant.BUYRECORD_REVOKE_NOTSUPPORT_EXCEPTION);
//		}
		// 撤单退款
		accountBusiness.revoke(buyRecord.getPublisherId(), id, buyRecord.getServiceFee(), buyRecord.getDeferredFee());
		// 修改点买记录状态
		buyRecord.setState(BuyRecordState.REVOKE);
		buyRecord.setUpdateTime(new Date());
		buyRecordDao.update(buyRecord);
		return buyRecord;
	}

}
