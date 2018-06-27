package com.waben.stock.applayer.tactics.dto.futures;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
 * PC端当天持仓、平仓盈亏（计算当天开仓、当天平仓）
 * 
 * @author sl
 *
 */
public class FuturesOrderDayGainLossDto {

	/**
	 * 持仓中盈亏
	 */
	@ApiModelProperty(value = "持仓浮动盈亏")
	private BigDecimal positionFee;
	/**
	 * 平仓盈亏
	 */
	@ApiModelProperty(value = "平仓盈亏")
	private BigDecimal unwindProFee;

	/**
	 * 账户余额
	 */
	@ApiModelProperty(value = "账户余额")
	private BigDecimal balance;
	/**
	 * 账户可用余额
	 */
	@ApiModelProperty(value = "账户可用余额")
	private BigDecimal availableBalance;
	/**
	 * 冻结资金
	 */
	@ApiModelProperty(value = "员工id")
	private BigDecimal frozenCapital;

	public BigDecimal getPositionFee() {
		return positionFee;
	}

	public void setPositionFee(BigDecimal positionFee) {
		this.positionFee = positionFee;
	}

	public BigDecimal getUnwindProFee() {
		return unwindProFee;
	}

	public void setUnwindProFee(BigDecimal unwindProFee) {
		this.unwindProFee = unwindProFee;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public BigDecimal getFrozenCapital() {
		return frozenCapital;
	}

	public void setFrozenCapital(BigDecimal frozenCapital) {
		this.frozenCapital = frozenCapital;
	}

}
