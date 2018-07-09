package com.waben.stock.datalayer.futures.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 止损止盈
 * 
 * @author sl
 *
 */
@Entity
@Table(name = "f_futures_stop_loss_or_profit")
public class FuturesStopLossOrProfit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * 止损金额
	 */
	private BigDecimal stopLossFee;

	/**
	 * 保证金
	 */
	private BigDecimal reserveFund;

	/**
	 * 强平金额
	 */
	private BigDecimal strongLevelingAmount;

	/**
	 * 止盈金额
	 * 
	 */
	private String stopProfitFee;

	/**
	 * 对应的品种
	 */
	@ManyToOne
	@JoinColumn(name = "commodity_id")
	private FuturesCommodity commodity;

	/************************************ 分割线，以下字段为非数据库字段 **********************************************/

	@Transient
	private Long commodityId;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getStopLossFee() {
		return stopLossFee;
	}

	public void setStopLossFee(BigDecimal stopLossFee) {
		this.stopLossFee = stopLossFee;
	}

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public BigDecimal getStrongLevelingAmount() {
		return strongLevelingAmount;
	}

	public void setStrongLevelingAmount(BigDecimal strongLevelingAmount) {
		this.strongLevelingAmount = strongLevelingAmount;
	}

	public String getStopProfitFee() {
		return stopProfitFee;
	}

	public void setStopProfitFee(String stopProfitFee) {
		this.stopProfitFee = stopProfitFee;
	}

	public FuturesCommodity getCommodity() {
		return commodity;
	}

	public void setCommodity(FuturesCommodity commodity) {
		this.commodity = commodity;
	}

	public Long getCommodityId() {
		if (commodity != null) {
			return commodity.getId();
		}
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

}
