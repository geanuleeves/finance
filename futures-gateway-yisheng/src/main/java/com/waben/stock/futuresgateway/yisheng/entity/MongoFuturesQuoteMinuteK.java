package com.waben.stock.futuresgateway.yisheng.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 行情-分钟K
 * 
 * @author lma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MongoFuturesQuoteMinuteK {

	private String id;
	/** 品种ID */
	private Long commodityId;
	/** 品种编号 */
	private String commodityNo;
	/** 合约ID */
	private Long contractId;
	/** 合约编号 */
	private String contractNo;
	/** 时间 */
	private Date time;
	/** 时间字符串 */
	private String timeStr;
	/** 开盘价 */
	private BigDecimal openPrice;
	/** 最高价 */
	private BigDecimal highPrice;
	/** 最低价 */
	private BigDecimal lowPrice;
	/** 收盘价 */
	private BigDecimal closePrice;
	/** 昨结算价 */
	private BigDecimal preSettlePrice;
	/** 成交量 */
	private long volume;
	/** 开始成交总量 */
	private Long startTotalQty;
	/** 结束成交总量 */
	private Long endTotalQty;
	/** 当天总持仓量 */
	private long totalVolume;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public String getCommodityNo() {
		return commodityNo;
	}

	public void setCommodityNo(String commodityNo) {
		this.commodityNo = commodityNo;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
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

	public long getVolume() {
		return volume;
	}

	public void setVolume(long volume) {
		this.volume = volume;
	}

	public long getTotalVolume() {
		return totalVolume;
	}

	public void setTotalVolume(long totalVolume) {
		this.totalVolume = totalVolume;
	}

	public Long getStartTotalQty() {
		return startTotalQty;
	}

	public void setStartTotalQty(Long startTotalQty) {
		this.startTotalQty = startTotalQty;
	}

	public Long getEndTotalQty() {
		return endTotalQty;
	}

	public void setEndTotalQty(Long endTotalQty) {
		this.endTotalQty = endTotalQty;
	}

	public BigDecimal getPreSettlePrice() {
		return preSettlePrice;
	}

	public void setPreSettlePrice(BigDecimal preSettlePrice) {
		this.preSettlePrice = preSettlePrice;
	}

}
