package com.waben.stock.interfaces.commonapi.retrivefutures.bean;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 期货合约历史图数据（分时、日K、分钟K）
 * 
 * @author lma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "期货合约历史图数据（分时、日K、分钟K）")
public class FuturesContractLineData {

	/** 品种编号 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/** 合约编号 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/** 时间 */
	@ApiModelProperty(value = "时间")
	private Date time;
	/** 时间字符串 */
	@ApiModelProperty(value = "时间字符串")
	private String timeStr;
	/** 开盘价 */
	@ApiModelProperty(value = "开盘价")
	private BigDecimal openPrice;
	/** 最高价 */
	@ApiModelProperty(value = "最高价")
	private BigDecimal highPrice;
	/** 最低价 */
	@ApiModelProperty(value = "最低价")
	private BigDecimal lowPrice;
	/** 收盘价 */
	@ApiModelProperty(value = "收盘价")
	private BigDecimal closePrice;
	/** 成交量 */
	@ApiModelProperty(value = "成交量")
	private Long volume;
	/** 当天总成交量 */
	@ApiModelProperty(value = "当天总成交量")
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

	public Long getVolume() {
		if (volume == null || volume < 0) {
			return 0L;
		}
		return volume;
	}

	public void setVolume(Long volume) {
		this.volume = volume;
	}

	public Long getTotalVolume() {
		if (totalVolume == null || totalVolume < 0) {
			return 0L;
		}
		return totalVolume;
	}

	public void setTotalVolume(Long totalVolume) {
		this.totalVolume = totalVolume;
	}

}
