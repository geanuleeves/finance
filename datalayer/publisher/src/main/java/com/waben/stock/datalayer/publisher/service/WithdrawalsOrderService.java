package com.waben.stock.datalayer.publisher.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.publisher.entity.CapitalAccount;
import com.waben.stock.datalayer.publisher.entity.FrozenCapital;
import com.waben.stock.datalayer.publisher.entity.WithdrawalsOrder;
import com.waben.stock.datalayer.publisher.repository.CapitalAccountDao;
import com.waben.stock.datalayer.publisher.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.publisher.repository.FrozenCapitalDao;
import com.waben.stock.datalayer.publisher.repository.WithdrawalsOrderDao;
import com.waben.stock.datalayer.publisher.repository.impl.MethodDesc;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalAdminDto;
import com.waben.stock.interfaces.enums.FrozenCapitalStatus;
import com.waben.stock.interfaces.enums.FrozenCapitalType;
import com.waben.stock.interfaces.enums.WithdrawalsState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.query.WithdrawalsOrderQuery;

/**
 * 提现订单 Service
 * 
 * @author luomengan
 *
 */
@Service
public class WithdrawalsOrderService {

	@Autowired
	private WithdrawalsOrderDao withdrawalsOrderDao;

	@Autowired
	private CapitalAccountDao accountDao;
	
	@Autowired
	private FrozenCapitalDao frozenDao;
	
	@Autowired
	private CapitalAccountService accountService;
	
	@Autowired
	private DynamicQuerySqlDao sqlDao;

	public WithdrawalsOrder save(WithdrawalsOrder withdrawalsOrder) {
		withdrawalsOrder.setCreateTime(new Date());
		if (withdrawalsOrder.getState() == WithdrawalsState.PROCESSING) {
			// 修改账户上的可用金额和冻结金额
			CapitalAccount account = accountDao.retriveByPublisherId(withdrawalsOrder.getPublisherId());
			if (withdrawalsOrder.getAmount().compareTo(account.getAvailableBalance()) > 0) {
				throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
			}
			account.setFrozenCapital(account.getFrozenCapital().add(withdrawalsOrder.getAmount()));
			account.setAvailableBalance(account.getAvailableBalance().subtract(withdrawalsOrder.getAmount()));
			// 添加冻结记录
			FrozenCapital frozen = new FrozenCapital();
			frozen.setAmount(withdrawalsOrder.getAmount());
			frozen.setFrozenTime(new Date());
			frozen.setPublisherId(withdrawalsOrder.getPublisherId());
			frozen.setStatus(FrozenCapitalStatus.Frozen);
			frozen.setType(FrozenCapitalType.Withdrawals);
			frozen.setWithdrawalsNo(withdrawalsOrder.getWithdrawalsNo());
			frozenDao.create(frozen);
		}
		return withdrawalsOrderDao.create(withdrawalsOrder);
	}
	
	public WithdrawalsOrder saveAdmin(WithdrawalsOrder withdrawalsOrder) {
		withdrawalsOrder.setCreateTime(new Date());
		if (withdrawalsOrder.getState() == WithdrawalsState.INIT) {
			// 修改账户上的可用金额和冻结金额
			CapitalAccount account = accountDao.retriveByPublisherId(withdrawalsOrder.getPublisherId());
			if (withdrawalsOrder.getAmount().compareTo(account.getAvailableBalance()) > 0) {
				throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
			}
			account.setFrozenCapital(account.getFrozenCapital().add(withdrawalsOrder.getAmount()));
			account.setAvailableBalance(account.getAvailableBalance().subtract(withdrawalsOrder.getAmount()));
			// 添加冻结记录
			FrozenCapital frozen = new FrozenCapital();
			frozen.setAmount(withdrawalsOrder.getAmount());
			frozen.setFrozenTime(new Date());
			frozen.setPublisherId(withdrawalsOrder.getPublisherId());
			frozen.setStatus(FrozenCapitalStatus.Frozen);
			frozen.setType(FrozenCapitalType.Withdrawals);
			frozen.setWithdrawalsNo(withdrawalsOrder.getWithdrawalsNo());
			frozenDao.create(frozen);
		}
		return withdrawalsOrderDao.create(withdrawalsOrder);
	}
	
	public WithdrawalsOrder findByid(Long id){
		return withdrawalsOrderDao.retrieve(id);
	}

	public WithdrawalsOrder add(WithdrawalsOrder withdrawalsOrder) {
		withdrawalsOrder.setCreateTime(new Date());
		return withdrawalsOrderDao.create(withdrawalsOrder);
	}

	public WithdrawalsOrder changeState(String withdrawalsNo, WithdrawalsState state) {
		WithdrawalsOrder withdrawalsOrder = withdrawalsOrderDao.retrieveByWithdrawalsNo(withdrawalsNo);
		withdrawalsOrder.setState(state);
		withdrawalsOrder.setUpdateTime(new Date());
		return withdrawalsOrderDao.update(withdrawalsOrder);
	}

	public WithdrawalsOrder findByWithdrawalsNo(String withdrawalsNo) {
		return withdrawalsOrderDao.retrieveByWithdrawalsNo(withdrawalsNo);
	}

	public WithdrawalsOrder revision(WithdrawalsOrder withdrawalsOrder) {
		withdrawalsOrder.setUpdateTime(new Date());
		return withdrawalsOrderDao.update(withdrawalsOrder);
	}

	public Page<WithdrawalsOrder> pagesByQuery(WithdrawalsOrderQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<WithdrawalsOrder> pages = withdrawalsOrderDao.page(new Specification<WithdrawalsOrder>() {
			@Override
			public Predicate toPredicate(Root<WithdrawalsOrder> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (query.getPublisherId() != null && query.getPublisherId() > 0) {
					predicateList
							.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
				}
				if (query.getStartTime() != null) {
					predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("updateTime").as(Date.class),
							query.getStartTime()));
				}
				if (query.getEndTime() != null) {
					predicateList.add(
							criteriaBuilder.lessThan(root.get("updateTime").as(Date.class), query.getEndTime()));
				}
				if(query.getState()!=null){
					if(query.getState()==0){
						predicateList.add(criteriaBuilder.equal(root.get("comprehensiveState").as(Integer.class), query.getState()));
					}else{
						predicateList.add(criteriaBuilder.notEqual(root.get("comprehensiveState").as(Integer.class), 0));
					}
				}
				
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime").as(Long.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}
	
	public String getSumOrder(WithdrawalsOrderQuery query){
		String pulisherIdConditon = "";
		if(query.getPublisherId()!=null){
			pulisherIdConditon = " and t1.publisher_id = '"+ query.getPublisherId() +"'";
		}
		
		String sql = String.format("select sum(t1.amount) as amount from withdrawals_order t1 where (t1.comprehensive_state=1 or t1.comprehensive_state=2) %s", pulisherIdConditon);
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));
		BigDecimal count = sqlDao.executeComputeSql(sql);
		return count.toString() ;
	}

	@Transactional
	public WithdrawalsOrder refuse(Long id, String remark) {
		WithdrawalsOrder order = withdrawalsOrderDao.retrieve(id);
		if(order == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		if(order.getComprehensiveState() != 0) {
			return order;
		}
		CapitalAccount account = accountDao.retriveByPublisherId(order.getPublisherId());
		BigDecimal amount = order.getAmount();
		Date date = new Date();
		// step 1 : 更新订单审核状态
		order.setRemark(remark);
		order.setComprehensiveState(2);
		order.setState(WithdrawalsState.RETREAT);
		order.setUpdateTime(new Date());
		order = withdrawalsOrderDao.update(order);
		// step 2 : 提现拒绝，冻结金额退回账号余额
		FrozenCapital frozen = frozenDao.retriveByPublisherIdAndWithdrawalsNo(order.getPublisherId(), order.getWithdrawalsNo());
		frozen.setStatus(FrozenCapitalStatus.Thaw);
		frozen.setThawTime(date);
		frozenDao.update(frozen);
		// step 3 : 修改账户的总金额、冻结金额
		account.setFrozenCapital(account.getFrozenCapital().subtract(amount));
		account.setAvailableBalance(account.getAvailableBalance().add(amount));
		accountDao.update(account);
		accountService.sendWithdrawalsOutsideMessage(order.getPublisherId(), amount.abs(), false);
		return order;
	}

}
