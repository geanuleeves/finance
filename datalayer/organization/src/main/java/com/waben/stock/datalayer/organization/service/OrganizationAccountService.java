package com.waben.stock.datalayer.organization.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationAccount;
import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountDao;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountFlowDao;
import com.waben.stock.datalayer.organization.repository.OrganizationDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.enums.OrganizationAccountFlowType;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.exception.DataNotFoundException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationAccountQuery;
import com.waben.stock.interfaces.util.PasswordCrypt;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

/**
 * 机构账户 Service
 *
 * @author luomengan
 */
@Service
public class OrganizationAccountService {

	@Autowired
	private OrganizationAccountDao organizationAccountDao;

	@Autowired
	private OrganizationAccountFlowDao flowDao;

	@Autowired
	private OrganizationDao organizationDao;

	public OrganizationAccount getOrganizationAccountInfo(Long id) {
		return organizationAccountDao.retrieve(id);
	}

	@Transactional
	public OrganizationAccount addOrganizationAccount(OrganizationAccount organizationAccount) {
		return organizationAccountDao.create(organizationAccount);
	}

	@Transactional
	public OrganizationAccount modifyOrganizationAccount(OrganizationAccount organizationAccount) {
		return organizationAccountDao.update(organizationAccount);
	}

	@Transactional
	public void deleteOrganizationAccount(Long id) {
		organizationAccountDao.delete(id);
	}

	@Transactional
	public void deleteOrganizationAccounts(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					organizationAccountDao.delete(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<OrganizationAccount> organizationAccounts(int page, int limit) {
		return organizationAccountDao.page(page, limit);
	}

	public List<OrganizationAccount> list() {
		return organizationAccountDao.list();
	}

	public OrganizationAccount getByOrgId(Long orgId) {
		Organization org = organizationDao.retrieve(orgId);
		if (org == null) {
			throw new DataNotFoundException();
		}
		OrganizationAccount account = organizationAccountDao.retrieveByOrg(org);
		if (account == null) {
			account = initAccount(org, null);
		}
		return account;
	}

	public void modifyPaymentPassword(Long orgId, String oldPaymentPassword, String paymentPassword) {
		Organization org = organizationDao.retrieve(orgId);
		if (org == null) {
			throw new DataNotFoundException();
		}
		OrganizationAccount account = organizationAccountDao.retrieveByOrg(org);
		if (account != null) {
			String dbOldPaymentPassword = account.getPaymentPassword();
			if (!PasswordCrypt.match(oldPaymentPassword, dbOldPaymentPassword)) {
				throw new ServiceException(ExceptionConstant.ORGANIZATIONACCOUNT_OLDPAYMENTPASSWORD_NOTMATCH_EXCEPTION);
			}
			account.setPaymentPassword(PasswordCrypt.crypt(paymentPassword));
			organizationAccountDao.update(account);
		} else {
			account = initAccount(org, paymentPassword);
		}
	}

	public synchronized OrganizationAccount benefit(Organization org, BigDecimal originAmount, BigDecimal amount,
			OrganizationAccountFlowType flowType, ResourceType resourceType, Long resourceId, String resourceTradeNo) {
		Date date = new Date();
		OrganizationAccount account = null;
		if (org != null) {
			account = organizationAccountDao.retrieveByOrg(org);
			if (account == null) {
				account = initAccount(org, null);
			}
			increaseAmount(account, amount, date);
		}
		// 产生流水
		OrganizationAccountFlow flow = new OrganizationAccountFlow();
		flow.setAmount(amount);
		flow.setOriginAmount(originAmount);
		flow.setFlowNo(UniqueCodeGenerator.generateFlowNo());
		flow.setOccurrenceTime(date);
		flow.setOrg(org);
		flow.setResourceType(resourceType);
		flow.setResourceId(resourceId);
		flow.setType(flowType);
		flow.setResourceTradeNo(resourceTradeNo);
		flow.setRemark(flowType.getType());
		flowDao.create(flow);
		return account;
	}

	private OrganizationAccount initAccount(Organization org, String paymentPassword) {
		OrganizationAccount account = new OrganizationAccount();
		account.setAvailableBalance(new BigDecimal("0"));
		account.setBalance(new BigDecimal("0"));
		account.setFrozenCapital(new BigDecimal("0"));
		account.setOrg(org);
		if(paymentPassword != null) {
			account.setPaymentPassword(PasswordCrypt.crypt(paymentPassword));
		}
		account.setUpdateTime(new Date());
		return organizationAccountDao.create(account);
	}

	public synchronized OrganizationAccount withdrawals(Organization org, BigDecimal amount, Long applyId, String applyNo) {
		OrganizationAccount account = organizationAccountDao.retrieveByOrg(org);
		Date date = new Date();
		reduceAmount(account, amount, date);
		// 生成流水
		OrganizationAccountFlow flow = new OrganizationAccountFlow();
		flow.setAmount(amount.abs().multiply(new BigDecimal("-1")));
		flow.setOriginAmount(amount.abs().multiply(new BigDecimal("-1")));
		flow.setFlowNo(UniqueCodeGenerator.generateFlowNo());
		flow.setOccurrenceTime(date);
		flow.setOrg(org);
		flow.setResourceId(applyId);
		flow.setResourceType(ResourceType.ORGWITHDRAWALSAPPLY);
		flow.setResourceTradeNo(applyNo);
		flow.setType(OrganizationAccountFlowType.Withdrawals);
		flow.setRemark(OrganizationAccountFlowType.Withdrawals.getType());
		flowDao.create(flow);
		return account;
	}

	public synchronized OrganizationAccount withdrawalsFailure(Organization org, BigDecimal amount, Long applyId, String applyNo) {
		OrganizationAccount account = organizationAccountDao.retrieveByOrg(org);
		Date date = new Date();
		increaseAmount(account, amount, date);
		// 生成流水
		OrganizationAccountFlow flow = new OrganizationAccountFlow();
		flow.setAmount(amount.abs());
		flow.setOriginAmount(amount.abs());
		flow.setFlowNo(UniqueCodeGenerator.generateFlowNo());
		flow.setOccurrenceTime(date);
		flow.setOrg(org);
		flow.setResourceId(applyId);
		flow.setResourceType(ResourceType.ORGWITHDRAWALSAPPLY);
		flow.setResourceTradeNo(applyNo);
		flow.setType(OrganizationAccountFlowType.WithdrawalsFailure);
		flow.setRemark(OrganizationAccountFlowType.WithdrawalsFailure.getType());
		flowDao.create(flow);
		return account;
	}

	/**
	 * 账户增加金额
	 * 
	 * @param account
	 *            资金账户
	 * @param amount
	 *            金额
	 */
	private synchronized void increaseAmount(OrganizationAccount account, BigDecimal amount, Date date) {
		account.setBalance(account.getBalance().add(amount));
		account.setAvailableBalance(account.getAvailableBalance().add(amount));
		account.setUpdateTime(date);
		organizationAccountDao.update(account);
	}

	/**
	 * 账户减少金额
	 * 
	 * @param account
	 *            资金账户
	 * @param amount
	 *            金额
	 */
	private synchronized void reduceAmount(OrganizationAccount account, BigDecimal amount, Date date) {
		BigDecimal amountAbs = amount.abs();
		// 判断账余额是否足够
		if (account.getAvailableBalance().compareTo(amountAbs) < 0) {
			throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
		}
		account.setBalance(account.getBalance().subtract(amount));
		account.setAvailableBalance(account.getAvailableBalance().subtract(amount));
		account.setUpdateTime(date);
		organizationAccountDao.update(account);
	}

	@Transactional
	public Page<OrganizationAccount> pagesByQuery(final OrganizationAccountQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<OrganizationAccount> pages = organizationAccountDao.page(new Specification<OrganizationAccount>() {
			@Override
			public Predicate toPredicate(Root<OrganizationAccount> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicates = new ArrayList<>();
				criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

}
