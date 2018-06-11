package com.waben.stock.datalayer.organization.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货代理价格
 * 
 * @author sl
 *
 */
@Entity
@Table(name = "p_futures_agent_price")
public class FuturesAgentPrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 合约ID
	 */
	private Long contractId;
	/**
	 * 代理商ID
	 */
	private Long orgId;
	/**
	 * 保证金（成本价格，一手）
	 */
	private BigDecimal costReserveFund;
	/**
	 * 开仓手续费（成本价格，一手）
	 */
	private BigDecimal costOpenwindServiceFee;
	/**
	 * 平仓手续费（成本价格，一手）
	 */
	private BigDecimal costUnwindServiceFee;
	/**
	 * 递延费（成本价格，一手）
	 */
	private BigDecimal costDeferredFee;
	/**
	 * 开仓手续费（销售价格，一手）
	 */
	private BigDecimal saleOpenwindServiceFee;
	/**
	 * 平仓手续费（销售价格，一手）
	 */
	private BigDecimal saleUnwindServiceFee;
	/**
	 * 一手隔夜递延费（销售价格，一手）
	 */
	private BigDecimal saleDeferredFee;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public BigDecimal getCostReserveFund() {
		return costReserveFund;
	}

	public void setCostReserveFund(BigDecimal costReserveFund) {
		this.costReserveFund = costReserveFund;
	}

	public BigDecimal getCostOpenwindServiceFee() {
		return costOpenwindServiceFee;
	}

	public void setCostOpenwindServiceFee(BigDecimal costOpenwindServiceFee) {
		this.costOpenwindServiceFee = costOpenwindServiceFee;
	}

	public BigDecimal getCostUnwindServiceFee() {
		return costUnwindServiceFee;
	}

	public void setCostUnwindServiceFee(BigDecimal costUnwindServiceFee) {
		this.costUnwindServiceFee = costUnwindServiceFee;
	}

	public BigDecimal getCostDeferredFee() {
		return costDeferredFee;
	}

	public void setCostDeferredFee(BigDecimal costDeferredFee) {
		this.costDeferredFee = costDeferredFee;
	}

	public BigDecimal getSaleOpenwindServiceFee() {
		return saleOpenwindServiceFee;
	}

	public void setSaleOpenwindServiceFee(BigDecimal saleOpenwindServiceFee) {
		this.saleOpenwindServiceFee = saleOpenwindServiceFee;
	}

	public BigDecimal getSaleUnwindServiceFee() {
		return saleUnwindServiceFee;
	}

	public void setSaleUnwindServiceFee(BigDecimal saleUnwindServiceFee) {
		this.saleUnwindServiceFee = saleUnwindServiceFee;
	}

	public BigDecimal getSaleDeferredFee() {
		return saleDeferredFee;
	}

	public void setSaleDeferredFee(BigDecimal saleDeferredFee) {
		this.saleDeferredFee = saleDeferredFee;
	}

}
