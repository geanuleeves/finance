package com.waben.stock.futuresgateway.yisheng.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货合约行情-最新
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_es_quote_last")
public class FuturesQuoteLast {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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
	/** 时间戳 */
	private String dateTimeStamp;
	/** 昨收盘价 */
	private BigDecimal preClosingPrice;
	/** 昨结算价 */
	private BigDecimal preSettlePrice;
	/** 昨持仓量 */
	private long prePositionQty;
	/** 开盘价 */
	private BigDecimal openingPrice;
	/** 最新价 */
	private BigDecimal lastPrice;
	/** 最高价 */
	private BigDecimal highPrice;
	/** 最低价 */
	private BigDecimal lowPrice;
	/** 历史最高价 */
	private BigDecimal hisHighPrice;
	/** 历史最低价 */
	private BigDecimal hisLowPrice;
	/** 涨停价 */
	private BigDecimal limitUpPrice;
	/** 跌停价 */
	private BigDecimal limitDownPrice;
	/** 当日总成交量 */
	private long totalQty;
	/** 当日成交金额 */
	private BigDecimal totalTurnover;
	/** 持仓量 */
	private long positionQty;
	/** 均价 */
	private BigDecimal averagePrice;
	/** 收盘价 */
	private BigDecimal closingPrice;
	/** 结算价 */
	private BigDecimal settlePrice;
	/** 最新成交量 */
	private long lastQty;
	/** 买价1-20档，逗号分隔 */
	private String bidPrice;
	/** 买量1-20档，逗号分隔 */
	private String bidQty;
	/** 卖价1-20档 ，逗号分隔 */
	private String askPrice;
	/** 卖量1-20档 ，逗号分隔 */
	private String askQty;
	/** 隐含买价 */
	private BigDecimal impliedBidPrice;
	/** 隐含买量 */
	private long impliedBidQty;
	/** 隐含卖价 */
	private BigDecimal impliedAskPrice;
	/** 隐含卖量 */
	private long impliedAskQty;
	/** 昨虚实度 */
	private BigDecimal preDelta;
	/** 今虚实度 */
	private BigDecimal currDelta;
	/** 内盘量 */
	private long insideQty;
	/** 外盘量 */
	private long outsideQty;
	/** 换手率 */
	private BigDecimal turnoverRate;
	/** 五日均量 */
	private long d5AvgQty;
	/** 市盈率 */
	private BigDecimal peRatio;
	/** 总市值 */
	private BigDecimal totalValue;
	/** 流通市值 */
	private BigDecimal negotiableValue;
	/** 持仓走势 */
	private long positionTrend;
	/** 涨速 */
	private BigDecimal changeSpeed;
	/** 涨幅 */
	private BigDecimal changeRate;
	/** 涨跌值 */
	private BigDecimal changeValue;
	/** 振幅 */
	private BigDecimal swing;
	/** 委买总量 */
	private long totalBidQty;
	/** 委卖总量 */
	private long totalAskQty;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(String dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public BigDecimal getPreClosingPrice() {
		return preClosingPrice;
	}

	public void setPreClosingPrice(BigDecimal preClosingPrice) {
		this.preClosingPrice = preClosingPrice;
	}

	public BigDecimal getPreSettlePrice() {
		return preSettlePrice;
	}

	public void setPreSettlePrice(BigDecimal preSettlePrice) {
		this.preSettlePrice = preSettlePrice;
	}

	public long getPrePositionQty() {
		return prePositionQty;
	}

	public void setPrePositionQty(long prePositionQty) {
		this.prePositionQty = prePositionQty;
	}

	public BigDecimal getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(BigDecimal openingPrice) {
		this.openingPrice = openingPrice;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
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

	public BigDecimal getHisHighPrice() {
		return hisHighPrice;
	}

	public void setHisHighPrice(BigDecimal hisHighPrice) {
		this.hisHighPrice = hisHighPrice;
	}

	public BigDecimal getHisLowPrice() {
		return hisLowPrice;
	}

	public void setHisLowPrice(BigDecimal hisLowPrice) {
		this.hisLowPrice = hisLowPrice;
	}

	public BigDecimal getLimitUpPrice() {
		return limitUpPrice;
	}

	public void setLimitUpPrice(BigDecimal limitUpPrice) {
		this.limitUpPrice = limitUpPrice;
	}

	public BigDecimal getLimitDownPrice() {
		return limitDownPrice;
	}

	public void setLimitDownPrice(BigDecimal limitDownPrice) {
		this.limitDownPrice = limitDownPrice;
	}

	public long getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(long totalQty) {
		this.totalQty = totalQty;
	}

	public BigDecimal getTotalTurnover() {
		return totalTurnover;
	}

	public void setTotalTurnover(BigDecimal totalTurnover) {
		this.totalTurnover = totalTurnover;
	}

	public long getPositionQty() {
		return positionQty;
	}

	public void setPositionQty(long positionQty) {
		this.positionQty = positionQty;
	}

	public BigDecimal getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(BigDecimal averagePrice) {
		this.averagePrice = averagePrice;
	}

	public BigDecimal getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(BigDecimal closingPrice) {
		this.closingPrice = closingPrice;
	}

	public BigDecimal getSettlePrice() {
		return settlePrice;
	}

	public void setSettlePrice(BigDecimal settlePrice) {
		this.settlePrice = settlePrice;
	}

	public long getLastQty() {
		return lastQty;
	}

	public void setLastQty(long lastQty) {
		this.lastQty = lastQty;
	}

	public String getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(String bidPrice) {
		this.bidPrice = bidPrice;
	}

	public String getBidQty() {
		return bidQty;
	}

	public void setBidQty(String bidQty) {
		this.bidQty = bidQty;
	}

	public String getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(String askPrice) {
		this.askPrice = askPrice;
	}

	public String getAskQty() {
		return askQty;
	}

	public void setAskQty(String askQty) {
		this.askQty = askQty;
	}

	public BigDecimal getImpliedBidPrice() {
		return impliedBidPrice;
	}

	public void setImpliedBidPrice(BigDecimal impliedBidPrice) {
		this.impliedBidPrice = impliedBidPrice;
	}

	public long getImpliedBidQty() {
		return impliedBidQty;
	}

	public void setImpliedBidQty(long impliedBidQty) {
		this.impliedBidQty = impliedBidQty;
	}

	public BigDecimal getImpliedAskPrice() {
		return impliedAskPrice;
	}

	public void setImpliedAskPrice(BigDecimal impliedAskPrice) {
		this.impliedAskPrice = impliedAskPrice;
	}

	public long getImpliedAskQty() {
		return impliedAskQty;
	}

	public void setImpliedAskQty(long impliedAskQty) {
		this.impliedAskQty = impliedAskQty;
	}

	public BigDecimal getPreDelta() {
		return preDelta;
	}

	public void setPreDelta(BigDecimal preDelta) {
		this.preDelta = preDelta;
	}

	public BigDecimal getCurrDelta() {
		return currDelta;
	}

	public void setCurrDelta(BigDecimal currDelta) {
		this.currDelta = currDelta;
	}

	public long getInsideQty() {
		return insideQty;
	}

	public void setInsideQty(long insideQty) {
		this.insideQty = insideQty;
	}

	public long getOutsideQty() {
		return outsideQty;
	}

	public void setOutsideQty(long outsideQty) {
		this.outsideQty = outsideQty;
	}

	public BigDecimal getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(BigDecimal turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public long getD5AvgQty() {
		return d5AvgQty;
	}

	public void setD5AvgQty(long d5AvgQty) {
		this.d5AvgQty = d5AvgQty;
	}

	public BigDecimal getPeRatio() {
		return peRatio;
	}

	public void setPeRatio(BigDecimal peRatio) {
		this.peRatio = peRatio;
	}

	public BigDecimal getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(BigDecimal totalValue) {
		this.totalValue = totalValue;
	}

	public BigDecimal getNegotiableValue() {
		return negotiableValue;
	}

	public void setNegotiableValue(BigDecimal negotiableValue) {
		this.negotiableValue = negotiableValue;
	}

	public long getPositionTrend() {
		return positionTrend;
	}

	public void setPositionTrend(long positionTrend) {
		this.positionTrend = positionTrend;
	}

	public BigDecimal getChangeSpeed() {
		return changeSpeed;
	}

	public void setChangeSpeed(BigDecimal changeSpeed) {
		this.changeSpeed = changeSpeed;
	}

	public BigDecimal getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(BigDecimal changeRate) {
		this.changeRate = changeRate;
	}

	public BigDecimal getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(BigDecimal changeValue) {
		this.changeValue = changeValue;
	}

	public BigDecimal getSwing() {
		return swing;
	}

	public void setSwing(BigDecimal swing) {
		this.swing = swing;
	}

	public long getTotalBidQty() {
		return totalBidQty;
	}

	public void setTotalBidQty(long totalBidQty) {
		this.totalBidQty = totalBidQty;
	}

	public long getTotalAskQty() {
		return totalAskQty;
	}

	public void setTotalAskQty(long totalAskQty) {
		this.totalAskQty = totalAskQty;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

}
