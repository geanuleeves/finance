package com.waben.stock.futuresgateway.yisheng.pojo;

import java.math.BigDecimal;
import java.util.Date;

public class FuturesQuoteData {

	/** 时间 */
	private Date time;
	/**
	 * 品种编号
	 */
	private String commodityNo;
	/**
	 * 合约编号
	 */
	private String contractNo;
	/**
	 * 最高价投标合同（买方开价）
	 */
	private BigDecimal bidPrice;
	/**
	 * 以投标价格提供的合同或批次数量（买方开价）
	 */
	private Long bidSize;
	/**
	 * 最低价投标合同（卖方开价）
	 */
	private BigDecimal askPrice;
	/**
	 * 以投标价格提供的合同或批次数量（卖方开价）
	 */
	private Long askSize;
	/**
	 * 最新价
	 */
	private BigDecimal lastPrice;
	/**
	 * 以最新价交易的合同或批次数量
	 */
	private Long lastSize;
	/**
	 * 今天的开盘价
	 */
	private BigDecimal openPrice;
	/**
	 * 当天最高价
	 */
	private BigDecimal highPrice;
	/**
	 * 当天最低价
	 */
	private BigDecimal lowPrice;
	/**
	 * 昨天的收盘价
	 */
	private BigDecimal closePrice;
	/**
	 * 当天成交量
	 */
	private Long volume;
	/**
	 * 当日总成交量
	 */
	private Long totalVolume;

	public String getCommodityNo() {
		return commodityNo;
	}

	public void setCommodityNo(String commodityNo) {
		this.commodityNo = commodityNo;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	public Long getBidSize() {
		return bidSize;
	}

	public void setBidSize(Long bidSize) {
		this.bidSize = bidSize;
	}

	public BigDecimal getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice;
	}

	public Long getAskSize() {
		return askSize;
	}

	public void setAskSize(Long askSize) {
		this.askSize = askSize;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public Long getLastSize() {
		return lastSize;
	}

	public void setLastSize(Long lastSize) {
		this.lastSize = lastSize;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

	public BigDecimal getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}

	public BigDecimal getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public Long getVolume() {
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public Long getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(Long totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
