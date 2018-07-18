package com.waben.stock.interfaces.dto.organization;

import java.math.BigDecimal;
import java.util.Date;

public class FuturesCommissionAuditDto extends OrganizationAccountFlowWithTradeInfoDto {

	/**
	 * 佣金审核ID
	 */
	private Long auditId;
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
	 * 佣金审核备注
	 */
	private String auditRemark;
	/**
	 * 审核时间
	 */
	private Date examineTime;

	public Long getAuditId() {
		return auditId;
	}

	public void setAuditId(Long auditId) {
		this.auditId = auditId;
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

	public String getAuditRemark() {
		return auditRemark;
	}

	public void setAuditRemark(String auditRemark) {
		this.auditRemark = auditRemark;
	}

	public Date getExamineTime() {
		return examineTime;
	}

	public void setExamineTime(Date examineTime) {
		this.examineTime = examineTime;
	}

}
