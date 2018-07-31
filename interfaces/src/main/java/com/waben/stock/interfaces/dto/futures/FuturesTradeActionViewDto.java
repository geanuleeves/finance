package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author chenk 2018/7/30
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesTradeActionViewDto extends FuturesTradeActionDto {

	/** 订单交易类型 */
	@ApiModelProperty(value = "订单交易类型")
	private FuturesOrderType orderType;
	/** 订单类型 */
	@ApiModelProperty(value = "订单类型")
	private FuturesTradePriceType futuresTradePriceType;
	/** 当前价 */
	@ApiModelProperty(value = "当前价")
	private BigDecimal lastPrice;
	/** 最小波动 */
	@ApiModelProperty(value = "最小波动")
	private BigDecimal minWave;
	/** 最小浮动价格 */
	@ApiModelProperty(value = "最小浮动价格")
	private BigDecimal perWaveMoney;
	/** 汇率 */
	@ApiModelProperty(value = "汇率")
	private BigDecimal rate;
	/** 货币标识 */
	@ApiModelProperty(value = "货币标识")
	private String currencySign;
	/** 货币缩写 */
	@ApiModelProperty(value = "货币缩写")
	private String currency;

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public FuturesTradePriceType getFuturesTradePriceType() {
		return futuresTradePriceType;
	}

	public void setFuturesTradePriceType(FuturesTradePriceType futuresTradePriceType) {
		this.futuresTradePriceType = futuresTradePriceType;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getMinWave() {
		if(minWave != null) {
			return minWave.stripTrailingZeros();
		}
		return minWave;
	}

	public void setMinWave(BigDecimal minWave) {
		this.minWave = minWave;
	}

	public BigDecimal getPerWaveMoney() {
		return perWaveMoney;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
