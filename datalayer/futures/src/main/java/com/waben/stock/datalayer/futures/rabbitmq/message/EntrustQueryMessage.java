package com.waben.stock.datalayer.futures.rabbitmq.message;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 委托查询消息
 * 
 * @author lma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EntrustQueryMessage {

	/**
	 * 交易委托ID
	 */
	private Long entrustId;
	/**
	 * 委托类型
	 * <ul>
	 * <li>1开仓</li>
	 * <li>2平仓</li>
	 * <li>3反手</li>
	 * </ul>
	 */
	private Integer entrustType;
	/**
	 * 是否达到止损止盈价格
	 */
	private boolean isStopLossOrProfit;
	/**
	 * 止损止盈价格
	 */
	private BigDecimal stopLossOrProfitPrice;
	/**
	 * 当前消息消费次数
	 */
	private int consumeCount;
	/**
	 * 最大消息消费次数
	 * <p>
	 * 如果为小于或者等于0的值，则表示一直消费
	 * </p>
	 */
	private int maxConsumeCount;

	public EntrustQueryMessage() {
	}

	public Long getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(Long entrustId) {
		this.entrustId = entrustId;
	}

	public Integer getEntrustType() {
		return entrustType;
	}

	public void setEntrustType(Integer entrustType) {
		this.entrustType = entrustType;
	}

	public int getConsumeCount() {
		return consumeCount;
	}

	public void setConsumeCount(int consumeCount) {
		this.consumeCount = consumeCount;
	}

	public int getMaxConsumeCount() {
		return maxConsumeCount;
	}

	public void setMaxConsumeCount(int maxConsumeCount) {
		this.maxConsumeCount = maxConsumeCount;
	}

	public boolean isStopLossOrProfit() {
		return isStopLossOrProfit;
	}

	public void setStopLossOrProfit(boolean isStopLossOrProfit) {
		this.isStopLossOrProfit = isStopLossOrProfit;
	}

	public BigDecimal getStopLossOrProfitPrice() {
		return stopLossOrProfitPrice;
	}

	public void setStopLossOrProfitPrice(BigDecimal stopLossOrProfitPrice) {
		this.stopLossOrProfitPrice = stopLossOrProfitPrice;
	}

}
