package com.waben.stock.futuresgateway.yisheng.entity;

import java.util.Date;

/**
 * 期货合约行情
 * 
 * @author lma
 *
 */
public class FuturesQuote {

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
	/** 时间戳 */
	private String dateTimeStamp;
	/** 昨收盘价 */
	private String preClosingPrice;
	/** 昨结算价 */
	private String preSettlePrice;
	/** 昨持仓量 */
	private long prePositionQty;
	/** 开盘价 */
	private String openingPrice;
	/** 最新价 */
	private String lastPrice;
	/** 最高价 */
	private String highPrice;
	/** 最低价 */
	private String lowPrice;
	/** 历史最高价 */
	private String hisHighPrice;
	/** 历史最低价 */
	private String hisLowPrice;
	/** 涨停价 */
	private String limitUpPrice;
	/** 跌停价 */
	private String limitDownPrice;
	/** 当日总成交量 */
	private long totalQty;
	/** 当日成交金额 */
	private String totalTurnover;
	/** 持仓量 */
	private long positionQty;
	/** 均价 */
	private String averagePrice;
	/** 收盘价 */
	private String closingPrice;
	/** 结算价 */
	private String settlePrice;
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
	private String impliedBidPrice;
	/** 隐含买量 */
	private long impliedBidQty;
	/** 隐含卖价 */
	private String impliedAskPrice;
	/** 隐含卖量 */
	private long impliedAskQty;
	/** 昨虚实度 */
	private String preDelta;
	/** 今虚实度 */
	private String currDelta;
	/** 内盘量 */
	private long insideQty;
	/** 外盘量 */
	private long outsideQty;
	/** 换手率 */
	private String turnoverRate;
	/** 五日均量 */
	private long d5AvgQty;
	/** 市盈率 */
	private String peRatio;
	/** 总市值 */
	private String totalValue;
	/** 流通市值 */
	private String negotiableValue;
	/** 持仓走势 */
	private long positionTrend;
	/** 涨速 */
	private String changeSpeed;
	/** 涨幅 */
	private String changeRate;
	/** 涨跌值 */
	private String changeValue;
	/** 振幅 */
	private String swing;
	/** 委买总量 */
	private long totalBidQty;
	/** 委卖总量 */
	private long totalAskQty;
	/** 行情索引 */
	private Long quoteIndex;

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

	public String getDateTimeStamp() {
		return dateTimeStamp;
	}

	public void setDateTimeStamp(String dateTimeStamp) {
		this.dateTimeStamp = dateTimeStamp;
	}

	public String getPreClosingPrice() {
		return preClosingPrice;
	}

	public void setPreClosingPrice(String preClosingPrice) {
		this.preClosingPrice = preClosingPrice;
	}

	public String getPreSettlePrice() {
		return preSettlePrice;
	}

	public void setPreSettlePrice(String preSettlePrice) {
		this.preSettlePrice = preSettlePrice;
	}

	public long getPrePositionQty() {
		return prePositionQty;
	}

	public void setPrePositionQty(long prePositionQty) {
		this.prePositionQty = prePositionQty;
	}

	public String getOpeningPrice() {
		return openingPrice;
	}

	public void setOpeningPrice(String openingPrice) {
		this.openingPrice = openingPrice;
	}

	public String getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(String lastPrice) {
		this.lastPrice = lastPrice;
	}

	public String getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(String highPrice) {
		this.highPrice = highPrice;
	}

	public String getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(String lowPrice) {
		this.lowPrice = lowPrice;
	}

	public String getHisHighPrice() {
		return hisHighPrice;
	}

	public void setHisHighPrice(String hisHighPrice) {
		this.hisHighPrice = hisHighPrice;
	}

	public String getHisLowPrice() {
		return hisLowPrice;
	}

	public void setHisLowPrice(String hisLowPrice) {
		this.hisLowPrice = hisLowPrice;
	}

	public String getLimitUpPrice() {
		return limitUpPrice;
	}

	public void setLimitUpPrice(String limitUpPrice) {
		this.limitUpPrice = limitUpPrice;
	}

	public String getLimitDownPrice() {
		return limitDownPrice;
	}

	public void setLimitDownPrice(String limitDownPrice) {
		this.limitDownPrice = limitDownPrice;
	}

	public long getTotalQty() {
		return totalQty;
	}

	public void setTotalQty(long totalQty) {
		this.totalQty = totalQty;
	}

	public String getTotalTurnover() {
		return totalTurnover;
	}

	public void setTotalTurnover(String totalTurnover) {
		this.totalTurnover = totalTurnover;
	}

	public long getPositionQty() {
		return positionQty;
	}

	public void setPositionQty(long positionQty) {
		this.positionQty = positionQty;
	}

	public String getAveragePrice() {
		return averagePrice;
	}

	public void setAveragePrice(String averagePrice) {
		this.averagePrice = averagePrice;
	}

	public String getClosingPrice() {
		return closingPrice;
	}

	public void setClosingPrice(String closingPrice) {
		this.closingPrice = closingPrice;
	}

	public String getSettlePrice() {
		return settlePrice;
	}

	public void setSettlePrice(String settlePrice) {
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

	public String getImpliedBidPrice() {
		return impliedBidPrice;
	}

	public void setImpliedBidPrice(String impliedBidPrice) {
		this.impliedBidPrice = impliedBidPrice;
	}

	public long getImpliedBidQty() {
		return impliedBidQty;
	}

	public void setImpliedBidQty(long impliedBidQty) {
		this.impliedBidQty = impliedBidQty;
	}

	public String getImpliedAskPrice() {
		return impliedAskPrice;
	}

	public void setImpliedAskPrice(String impliedAskPrice) {
		this.impliedAskPrice = impliedAskPrice;
	}

	public long getImpliedAskQty() {
		return impliedAskQty;
	}

	public void setImpliedAskQty(long impliedAskQty) {
		this.impliedAskQty = impliedAskQty;
	}

	public String getPreDelta() {
		return preDelta;
	}

	public void setPreDelta(String preDelta) {
		this.preDelta = preDelta;
	}

	public String getCurrDelta() {
		return currDelta;
	}

	public void setCurrDelta(String currDelta) {
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

	public String getTurnoverRate() {
		return turnoverRate;
	}

	public void setTurnoverRate(String turnoverRate) {
		this.turnoverRate = turnoverRate;
	}

	public long getD5AvgQty() {
		return d5AvgQty;
	}

	public void setD5AvgQty(long d5AvgQty) {
		this.d5AvgQty = d5AvgQty;
	}

	public String getPeRatio() {
		return peRatio;
	}

	public void setPeRatio(String peRatio) {
		this.peRatio = peRatio;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

	public String getNegotiableValue() {
		return negotiableValue;
	}

	public void setNegotiableValue(String negotiableValue) {
		this.negotiableValue = negotiableValue;
	}

	public long getPositionTrend() {
		return positionTrend;
	}

	public void setPositionTrend(long positionTrend) {
		this.positionTrend = positionTrend;
	}

	public String getChangeSpeed() {
		return changeSpeed;
	}

	public void setChangeSpeed(String changeSpeed) {
		this.changeSpeed = changeSpeed;
	}

	public String getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(String changeRate) {
		this.changeRate = changeRate;
	}

	public String getChangeValue() {
		return changeValue;
	}

	public void setChangeValue(String changeValue) {
		this.changeValue = changeValue;
	}

	public String getSwing() {
		return swing;
	}

	public void setSwing(String swing) {
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

	public Long getQuoteIndex() {
		return quoteIndex;
	}

	public void setQuoteIndex(Long quoteIndex) {
		this.quoteIndex = quoteIndex;
	}

}
