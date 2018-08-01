package com.waben.stock.applayer.tactics.dto.futures;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class FuturesOrderProfitDto {

	/**
	 * 盈亏
	 */
	@ApiModelProperty(value = "盈亏")
	private BigDecimal totalIncome;

	/** 保证金 */
	@ApiModelProperty(value = "保证金")
	private BigDecimal reserveFund;

	/**
	 * 总强平金额
	 */
	@ApiModelProperty(value = "总强平金额")
	private BigDecimal totalBalance;
	/**
	 * 汇率
	 */
	@ApiModelProperty(value = "汇率")
	private BigDecimal rate;
	/**
	 * 货币符号
	 */
	@ApiModelProperty(value = "货币符号，如￥、$")
	private String currencySign;

	public BigDecimal getTotalIncome() {
		return totalIncome;
	}

	public void setTotalIncome(BigDecimal totalIncome) {
		this.totalIncome = totalIncome;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCurrencySign() {
		return currencySign;
	}

	public void setCurrencySign(String currencySign) {
		this.currencySign = currencySign;
	}

	public BigDecimal getTotalBalance() {
		return totalBalance;
	}

	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}
}
