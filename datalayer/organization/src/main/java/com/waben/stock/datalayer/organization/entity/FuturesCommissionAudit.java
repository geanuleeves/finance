package com.waben.stock.datalayer.organization.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 佣金审核
 * 
 * @author Administrator
 *
 */
@Entity
@Table(name = "p_futures_commission_audit")
public class FuturesCommissionAudit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 审核状态
	 * <ul>
	 * <li>1、审核中</li>
	 * <li>2、审核通过</li>
	 * <li>3、审核不通过</li>
	 * </ul>
	 */
	private Integer state;

	/**
	 * 实际返佣金额
	 */
	private BigDecimal realMaidFee;

	/**
	 * 备注
	 */
	private String auditRemark;

	/**
	 * 返佣金额对象
	 */
	@OneToOne
	@JoinColumn(name = "flow_id")
	private OrganizationAccountFlow accountFlow;

	/*************************** 以下字段非数据库字段 ********************************************/

	/**
	 * 资金流水ID
	 */
	@Transient
	private Long flowId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public BigDecimal getRealMaidFee() {
		return realMaidFee;
	}

	public void setRealMaidFee(BigDecimal realMaidFee) {
		this.realMaidFee = realMaidFee;
	}

	public OrganizationAccountFlow getAccountFlow() {
		return accountFlow;
	}

	public void setAccountFlow(OrganizationAccountFlow accountFlow) {
		this.accountFlow = accountFlow;
	}

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public Long getFlowId() {
		if (accountFlow != null) {
			return accountFlow.getId();
		}
		return flowId;
	}

	public void setFlowId(Long flowId) {
		this.flowId = flowId;
	}

}
