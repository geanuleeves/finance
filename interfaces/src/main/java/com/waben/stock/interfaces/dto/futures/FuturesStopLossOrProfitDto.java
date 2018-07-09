package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

public class FuturesStopLossOrProfitDto {

	private Long id;

	/**
	 * 止损金额
	 */
	@ApiModelProperty(value = "止损金额")
	private BigDecimal stopLossFee;

	/**
	 * 保证金
	 */
	@ApiModelProperty(value = "保证金")
	private BigDecimal reserveFund;

	/**
	 * 强平金额
	 */
	@ApiModelProperty(value = "强平金额")
	private BigDecimal strongLevelingAmount;

	/**
	 * 止盈金额
	 */
	@ApiModelProperty(value = "止盈金额")
	private String stopProfitFee;

	/**
	 * 品种ID
	 */
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;

//	/**
//	 * 货币符号，如“$”,表示美元
//	 */
//	@ApiModelProperty(value = "货币符号，如“$”,表示美元")
//	private String currencySign;

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

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

//	public String getCurrencySign() {
//		return currencySign;
//	}
//
//	public void setCurrencySign(String currencySign) {
//		this.currencySign = currencySign;
//	}

}
