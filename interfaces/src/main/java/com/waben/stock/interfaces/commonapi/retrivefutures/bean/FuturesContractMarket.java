package com.waben.stock.interfaces.commonapi.retrivefutures.bean;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 期货合约行情
 * 
 * @author luomengan
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "期货合约行情")
public class FuturesContractMarket {

	/** 时间 */
	@ApiModelProperty(value = "时间")
	private Date time;
	/**
	 * 品种编号
	 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/**
	 * 合约编号
	 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/**
	 * 最高价投标合同（买方开价）
	 */
	@ApiModelProperty(value = "最高价投标合同（买方开价）")
	private BigDecimal bidPrice;
	private BigDecimal bidPrice2;
	private BigDecimal bidPrice3;
	private BigDecimal bidPrice4;
	private BigDecimal bidPrice5;
	/**
	 * 以投标价格提供的合同或批次数量（买方开价）
	 */
	@ApiModelProperty(value = "以投标价格提供的合同或批次数量（买方开价）")
	private Long bidSize;
	private Long bidSize2;
	private Long bidSize3;
	private Long bidSize4;
	private Long bidSize5;
	/**
	 * 最低价投标合同（卖方开价）
	 */
	@ApiModelProperty(value = "最低价投标合同（卖方开价）")
	private BigDecimal askPrice;
	private BigDecimal askPrice2;
	private BigDecimal askPrice3;
	private BigDecimal askPrice4;
	private BigDecimal askPrice5;
	/**
	 * 以投标价格提供的合同或批次数量（卖方开价）
	 */
	@ApiModelProperty(value = "以投标价格提供的合同或批次数量（卖方开价）")
	private Long askSize;
	private Long askSize2;
	private Long askSize3;
	private Long askSize4;
	private Long askSize5;
	/**
	 * 最新价
	 */
	@ApiModelProperty(value = "最新价")
	private BigDecimal lastPrice;
	/**
	 * 以最新价交易的合同或批次数量
	 */
	@ApiModelProperty(value = "以最新价交易的合同或批次数量")
	private Long lastSize;
	/**
	 * 今天的开盘价
	 */
	@ApiModelProperty(value = "今天的开盘价")
	private BigDecimal openPrice;
	/**
	 * 当天最高价
	 */
	@ApiModelProperty(value = "当天最高价")
	private BigDecimal highPrice;
	/**
	 * 当天最低价
	 */
	@ApiModelProperty(value = "当天最低价")
	private BigDecimal lowPrice;
	/**
	 * 昨天的收盘价
	 */
	@ApiModelProperty(value = "昨天的收盘价")
	private BigDecimal closePrice;
	/**
	 * 当前行情收盘价
	 */
	@ApiModelProperty(value = "当前行情收盘价")
	private BigDecimal nowClosePrice;
	/**
	 * 当天成交量
	 */
	@ApiModelProperty(value = "当天成交量")
	private Long volume;
	/**
	 * 当日总成交量
	 */
	@ApiModelProperty(value = "当日总成交量")
	private Long totalVolume;
	/**
	 * 跌涨价格
	 */
	@ApiModelProperty(value = "跌涨价格")
	private BigDecimal upDropPrice;
	/**
	 * 跌涨幅度
	 */
	@ApiModelProperty(value = "跌涨幅度")
	private BigDecimal upDropSpeed;

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

	public void setUpDropPrice(BigDecimal upDropPrice) {
		this.upDropPrice = upDropPrice;
	}

	public void setUpDropSpeed(BigDecimal upDropSpeed) {
		this.upDropSpeed = upDropSpeed;
	}

	public BigDecimal getUpDropPrice() {
		if (closePrice != null && lastPrice != null && closePrice.compareTo(BigDecimal.ZERO) > 0) {
			return lastPrice.subtract(closePrice);
		}
		return upDropPrice;
	}

	public BigDecimal getUpDropSpeed() {
		if (closePrice != null && lastPrice != null && closePrice.compareTo(BigDecimal.ZERO) > 0) {
			return lastPrice.subtract(closePrice).divide(closePrice, 4, RoundingMode.DOWN);
		}
		return upDropSpeed;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public BigDecimal getBidPrice2() {
		return bidPrice2;
	}

	public void setBidPrice2(BigDecimal bidPrice2) {
		this.bidPrice2 = bidPrice2;
	}

	public BigDecimal getBidPrice3() {
		return bidPrice3;
	}

	public void setBidPrice3(BigDecimal bidPrice3) {
		this.bidPrice3 = bidPrice3;
	}

	public BigDecimal getBidPrice4() {
		return bidPrice4;
	}

	public void setBidPrice4(BigDecimal bidPrice4) {
		this.bidPrice4 = bidPrice4;
	}

	public BigDecimal getBidPrice5() {
		return bidPrice5;
	}

	public void setBidPrice5(BigDecimal bidPrice5) {
		this.bidPrice5 = bidPrice5;
	}

	public Long getBidSize2() {
		return bidSize2;
	}

	public void setBidSize2(Long bidSize2) {
		this.bidSize2 = bidSize2;
	}

	public Long getBidSize3() {
		return bidSize3;
	}

	public void setBidSize3(Long bidSize3) {
		this.bidSize3 = bidSize3;
	}

	public Long getBidSize4() {
		return bidSize4;
	}

	public void setBidSize4(Long bidSize4) {
		this.bidSize4 = bidSize4;
	}

	public Long getBidSize5() {
		return bidSize5;
	}

	public void setBidSize5(Long bidSize5) {
		this.bidSize5 = bidSize5;
	}

	public BigDecimal getAskPrice2() {
		return askPrice2;
	}

	public void setAskPrice2(BigDecimal askPrice2) {
		this.askPrice2 = askPrice2;
	}

	public BigDecimal getAskPrice3() {
		return askPrice3;
	}

	public void setAskPrice3(BigDecimal askPrice3) {
		this.askPrice3 = askPrice3;
	}

	public BigDecimal getAskPrice4() {
		return askPrice4;
	}

	public void setAskPrice4(BigDecimal askPrice4) {
		this.askPrice4 = askPrice4;
	}

	public BigDecimal getAskPrice5() {
		return askPrice5;
	}

	public void setAskPrice5(BigDecimal askPrice5) {
		this.askPrice5 = askPrice5;
	}

	public Long getAskSize2() {
		return askSize2;
	}

	public void setAskSize2(Long askSize2) {
		this.askSize2 = askSize2;
	}

	public Long getAskSize3() {
		return askSize3;
	}

	public void setAskSize3(Long askSize3) {
		this.askSize3 = askSize3;
	}

	public Long getAskSize4() {
		return askSize4;
	}

	public void setAskSize4(Long askSize4) {
		this.askSize4 = askSize4;
	}

	public Long getAskSize5() {
		return askSize5;
	}

	public void setAskSize5(Long askSize5) {
		this.askSize5 = askSize5;
	}

	public BigDecimal getNowClosePrice() {
		return nowClosePrice;
	}

	public void setNowClosePrice(BigDecimal nowClosePrice) {
		this.nowClosePrice = nowClosePrice;
	}

}
