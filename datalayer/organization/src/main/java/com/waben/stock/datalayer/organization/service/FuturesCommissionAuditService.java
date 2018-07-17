package com.waben.stock.datalayer.organization.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;
import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationAccount;
import com.waben.stock.datalayer.organization.repository.FuturesCommissionAuditDao;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountDao;
import com.waben.stock.datalayer.organization.repository.OrganizationDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.util.PasswordCrypt;

/**
 * 佣金审核service
 * 
 * @author Administrator
 *
 */
@Service
public class FuturesCommissionAuditService {

	@Autowired
	private FuturesCommissionAuditDao auditDao;

	@Autowired
	private OrganizationAccountDao organizationAccountDao;

	@Autowired
	private OrganizationDao organizationDao;

	public Integer countCommissionAudit(Long orgId) {
		return auditDao.countCommissionAudit(orgId);
	}

	public BigDecimal realMaidFee(Long orgId) {
		return auditDao.realMaidFee(orgId);
	}

	public Integer editCommissionAudit(Long auditId, Integer state, String remarks, BigDecimal realMaidFee) {
		FuturesCommissionAudit audit = auditDao.retrieve(auditId);
		if (audit == null) {
			// 该审核记录不存在
			throw new ServiceException(ExceptionConstant.THE_AUDIT_RECORD_DOESNOT_EXIST_EXCEPTION);
		}
		if (audit.getState() == 2 || audit.getState() == 3) {
			// 该记录已审核
			throw new ServiceException(ExceptionConstant.THE_STATE_ISNOT_AUDITED_EXCEPTION);
		}
		if (audit.getAccountFlow() == null) {
			// 该审核记录不存在
			throw new ServiceException(ExceptionConstant.THE_AUDIT_RECORD_DOESNOT_EXIST_EXCEPTION);
		}
		if (realMaidFee.abs().compareTo(audit.getAccountFlow().getAmount().abs()) > 0) {
			// 实际返佣资金不能大于系统返佣金额
			throw new ServiceException(ExceptionConstant.THAN_AMOUNT_SYSTEM_RETURNS_EXCEPTION);
		}
		String remark = "";
		Date date = new Date();
		OrganizationAccount account = null;
		Organization org = audit.getAccountFlow().getOrg();
		if (state != 1) {
			if (org != null) {
				if (org.getLevel() != 1) {
					account = organizationAccountDao.retrieveByOrg(org);
					if (account == null) {
						account = initAccount(org, null);
					}
					remark += "系统返佣金额：" + audit.getAccountFlow().getAmount() + "元，实际返佣：" + realMaidFee + "元，";
					increaseAmount(account, realMaidFee, date);
					if (realMaidFee.abs().compareTo(audit.getAccountFlow().getAmount().abs()) < 0) {
						Organization orgParent = null;
						List<Organization> orgList = organizationDao.listByLevel(1);
						if (orgList != null && orgList.size() > 0) {
							orgParent = orgList.get(0);
							account = organizationAccountDao.retrieveByOrg(orgParent);
							BigDecimal surplusFee = audit.getAccountFlow().getAmount().subtract(realMaidFee);
							if (account == null) {
								account = initAccount(org, null);
							}
							remark += "剩余" + surplusFee + "元返佣给平台。";
							increaseAmount(account, surplusFee, date);
						}
					}
				}
			}
		}
		audit.setState(state);
		if (state == 2) {
			audit.setAuditRemark(remark);
		} else {
			audit.setAuditRemark(remarks);
		}
		audit.setRealMaidFee(realMaidFee);
		audit.getAccountFlow().setAvailableBalance(account == null ? new BigDecimal(0) : account.getAvailableBalance());
		audit = auditDao.update(audit);
		if (audit != null) {
			return 1;
		}
		return null;
	}

	private OrganizationAccount initAccount(Organization org, String paymentPassword) {
		OrganizationAccount account = new OrganizationAccount();
		account.setAvailableBalance(new BigDecimal("0"));
		account.setBalance(new BigDecimal("0"));
		account.setFrozenCapital(new BigDecimal("0"));
		account.setOrg(org);
		if (paymentPassword != null) {
			account.setPaymentPassword(PasswordCrypt.crypt(paymentPassword));
		}
		account.setState(1);
		account.setUpdateTime(new Date());
		return organizationAccountDao.create(account);
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
}
