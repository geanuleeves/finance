package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;

import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 期货订单与行情Dto
 * 
 * @author sl
 *
 */
public class FuturesOrderMarketDto extends FuturesOrderDto {

	/**
	 * 最新价
	 */
	@ApiModelProperty(value = "最新价")
	private BigDecimal lastPrice;

	/**
	 * 订单类型描述
	 */
	@ApiModelProperty(value = "订单类型描述 如 ：买涨1手")
	private String buyOrderTypeDesc;

	/**
	 * 浮动盈亏
	 */
	@ApiModelProperty(value = "浮动盈亏")
	private BigDecimal floatingProfitOrLoss;

	/**
	 * 汇率，如“7.0”，表示1美元=7.0*1人民币
	 */
	@ApiModelProperty(value = "汇率，如“7.0”，表示1美元=7.0*1人民币")
	private BigDecimal rate;

	/**
	 * 波动一次盈亏金额，单位为该合约的货币单位
	 */
	@ApiModelProperty(value = "波动一次盈亏金额，单位为该合约的货币单位")
	private BigDecimal perWaveMoney;
	/**
	 * 最小波动
	 */
	@ApiModelProperty(value = "最小波动")
	private BigDecimal minWave;

	/**
	 * 货币符号，如“$”,表示美元
	 */
	@ApiModelProperty(value = "货币符号，如“$”,表示美元")
	private String currencySign;

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public String getBuyOrderTypeDesc() {
		return buyOrderTypeDesc;
	}

	public void setBuyOrderTypeDesc(String buyOrderTypeDesc) {
		this.buyOrderTypeDesc = buyOrderTypeDesc;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getFloatingProfitOrLoss() {
		return floatingProfitOrLoss;
	}

	public void setFloatingProfitOrLoss(BigDecimal floatingProfitOrLoss) {
		this.floatingProfitOrLoss = floatingProfitOrLoss;
	}

	public BigDecimal getPerWaveMoney() {
		return perWaveMoney;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
	}

	public BigDecimal getMinWave() {
		return minWave;
	}

	public void setMinWave(BigDecimal minWave) {
		this.minWave = minWave;
	}

	public String getCurrencySign() {
		return currencySign;
	}

	public void setCurrencySign(String currencySign) {
		this.currencySign = currencySign;
	}

}
