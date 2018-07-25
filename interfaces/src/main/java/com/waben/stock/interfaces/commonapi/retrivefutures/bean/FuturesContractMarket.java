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
 * @author lma
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
	private BigDecimal bidPrice6;
	private BigDecimal bidPrice7;
	private BigDecimal bidPrice8;
	private BigDecimal bidPrice9;
	private BigDecimal bidPrice10;
	/**
	 * 以投标价格提供的合同或批次数量（买方开价）
	 */
	@ApiModelProperty(value = "以投标价格提供的合同或批次数量（买方开价）")
	private Long bidSize;
	private Long bidSize2;
	private Long bidSize3;
	private Long bidSize4;
	private Long bidSize5;
	private Long bidSize6;
	private Long bidSize7;
	private Long bidSize8;
	private Long bidSize9;
	private Long bidSize10;
	/**
	 * 最低价投标合同（卖方开价）
	 */
	@ApiModelProperty(value = "最低价投标合同（卖方开价）")
	private BigDecimal askPrice;
	private BigDecimal askPrice2;
	private BigDecimal askPrice3;
	private BigDecimal askPrice4;
	private BigDecimal askPrice5;
	private BigDecimal askPrice6;
	private BigDecimal askPrice7;
	private BigDecimal askPrice8;
	private BigDecimal askPrice9;
	private BigDecimal askPrice10;
	/**
	 * 以投标价格提供的合同或批次数量（卖方开价）
	 */
	@ApiModelProperty(value = "以投标价格提供的合同或批次数量（卖方开价）")
	private Long askSize;
	private Long askSize2;
	private Long askSize3;
	private Long askSize4;
	private Long askSize5;
	private Long askSize6;
	private Long askSize7;
	private Long askSize8;
	private Long askSize9;
	private Long askSize10;
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

	public BigDecimal getBidPrice6() {
		return bidPrice6;
	}

	public void setBidPrice6(BigDecimal bidPrice6) {
		this.bidPrice6 = bidPrice6;
	}

	public BigDecimal getBidPrice7() {
		return bidPrice7;
	}

	public void setBidPrice7(BigDecimal bidPrice7) {
		this.bidPrice7 = bidPrice7;
	}

	public BigDecimal getBidPrice8() {
		return bidPrice8;
	}

	public void setBidPrice8(BigDecimal bidPrice8) {
		this.bidPrice8 = bidPrice8;
	}

	public BigDecimal getBidPrice9() {
		return bidPrice9;
	}

	public void setBidPrice9(BigDecimal bidPrice9) {
		this.bidPrice9 = bidPrice9;
	}

	public BigDecimal getBidPrice10() {
		return bidPrice10;
	}

	public void setBidPrice10(BigDecimal bidPrice10) {
		this.bidPrice10 = bidPrice10;
	}

	public Long getBidSize6() {
		return bidSize6;
	}

	public void setBidSize6(Long bidSize6) {
		this.bidSize6 = bidSize6;
	}

	public Long getBidSize7() {
		return bidSize7;
	}

	public void setBidSize7(Long bidSize7) {
		this.bidSize7 = bidSize7;
	}

	public Long getBidSize8() {
		return bidSize8;
	}

	public void setBidSize8(Long bidSize8) {
		this.bidSize8 = bidSize8;
	}

	public Long getBidSize9() {
		return bidSize9;
	}

	public void setBidSize9(Long bidSize9) {
		this.bidSize9 = bidSize9;
	}

	public Long getBidSize10() {
		return bidSize10;
	}

	public void setBidSize10(Long bidSize10) {
		this.bidSize10 = bidSize10;
	}

	public BigDecimal getAskPrice6() {
		return askPrice6;
	}

	public void setAskPrice6(BigDecimal askPrice6) {
		this.askPrice6 = askPrice6;
	}

	public BigDecimal getAskPrice7() {
		return askPrice7;
	}

	public void setAskPrice7(BigDecimal askPrice7) {
		this.askPrice7 = askPrice7;
	}

	public BigDecimal getAskPrice8() {
		return askPrice8;
	}

	public void setAskPrice8(BigDecimal askPrice8) {
		this.askPrice8 = askPrice8;
	}

	public BigDecimal getAskPrice9() {
		return askPrice9;
	}

	public void setAskPrice9(BigDecimal askPrice9) {
		this.askPrice9 = askPrice9;
	}

	public BigDecimal getAskPrice10() {
		return askPrice10;
	}

	public void setAskPrice10(BigDecimal askPrice10) {
		this.askPrice10 = askPrice10;
	}

	public Long getAskSize6() {
		return askSize6;
	}

	public void setAskSize6(Long askSize6) {
		this.askSize6 = askSize6;
	}

	public Long getAskSize7() {
		return askSize7;
	}

	public void setAskSize7(Long askSize7) {
		this.askSize7 = askSize7;
	}

	public Long getAskSize8() {
		return askSize8;
	}

	public void setAskSize8(Long askSize8) {
		this.askSize8 = askSize8;
	}

	public Long getAskSize9() {
		return askSize9;
	}

	public void setAskSize9(Long askSize9) {
		this.askSize9 = askSize9;
	}

	public Long getAskSize10() {
		return askSize10;
	}

	public void setAskSize10(Long askSize10) {
		this.askSize10 = askSize10;
	}

}
