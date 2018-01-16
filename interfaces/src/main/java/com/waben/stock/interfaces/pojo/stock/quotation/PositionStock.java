package com.waben.stock.interfaces.pojo.stock.quotation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class PositionStock implements Serializable{
    private Long buyRecordId;
    private BigDecimal buyingPrice;
    private String stockCode;
    private String stockName;
    private BigDecimal profitPosition;
    private BigDecimal lossPosition;
    private Long investorId;
    private Boolean deferred;
    private Date buyingTime;
    private Long strategyTypeId;
    private String tradeSession;
    public Long getBuyRecordId() {
        return buyRecordId;
    }

    public void setBuyRecordId(Long buyRecordId) {
        this.buyRecordId = buyRecordId;
    }
    public Long getInvestorId() {
        return investorId;
    }

    public void setInvestorId(Long investorId) {
        this.investorId = investorId;
    }

    public Boolean getDeferred() {
        return deferred;
    }

    public void setDeferred(Boolean deferred) {
        this.deferred = deferred;
    }

    public Date getBuyingTime() {
        return buyingTime;
    }

    public void setBuyingTime(Date buyingTime) {
        this.buyingTime = buyingTime;
    }

    public Long getStrategyTypeId() {
        return strategyTypeId;
    }

    public void setStrategyTypeId(Long strategyTypeId) {
        this.strategyTypeId = strategyTypeId;
    }

    public String getTradeSession() {
        return tradeSession;
    }

    public void setTradeSession(String tradeSession) {
        this.tradeSession = tradeSession;
    }

    public BigDecimal getProfitPosition() {
        return profitPosition;
    }

    public void setProfitPosition(BigDecimal profitPosition) {
        this.profitPosition = profitPosition;
    }

    public BigDecimal getLossPosition() {
        return lossPosition;
    }

    public void setLossPosition(BigDecimal lossPosition) {
        this.lossPosition = lossPosition;
    }

    public BigDecimal getBuyingPrice() {
        return buyingPrice;
    }

    public String getStockCode() {
        return stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setBuyingPrice(BigDecimal buyingPrice) {
        this.buyingPrice = buyingPrice;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    @Override
    public String toString() {
        return "PositionStock{" +
                "buyRecordId='" + buyRecordId + '\'' +
                ", buyingPrice=" + buyingPrice +
                ", stockCode='" + stockCode + '\'' +
                ", stockName='" + stockName + '\'' +
                ", profitPosition=" + profitPosition +
                ", lossPosition=" + lossPosition +
                ", investorId=" + investorId +
                ", deferred=" + deferred +
                ", buyingTime=" + buyingTime +
                ", strategyTypeId=" + strategyTypeId +
                ", tradeSession='" + tradeSession + '\'' +
                '}';
    }
}
