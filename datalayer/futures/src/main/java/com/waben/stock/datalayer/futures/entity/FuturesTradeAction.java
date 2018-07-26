package com.waben.stock.datalayer.futures.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradeActionTypeConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesWindControlTypeConverter;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesWindControlType;

/**
 * 订单交易开平仓记录
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_futures_trade_action")
public class FuturesTradeAction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 用户ID */
	private Long publisherId;
	/** 对应的订单 */
	@ManyToOne
	@JoinColumn(name = "order_id")
	private FuturesOrder order;
	/** 对应的委托 */
	@ManyToOne
	@JoinColumn(name = "trade_entrust_id")
	private FuturesTradeEntrust tradeEntrust;
	/** 排队顺序 */
	private Integer sort;
	/** 委托时间 */
	private Date entrustTime;
	/** 交易开平仓 类型 */
	@Convert(converter = FuturesTradeActionTypeConverter.class)
	private FuturesTradeActionType tradeActionType;
	/** 风控类型 */
	@Convert(converter = FuturesWindControlTypeConverter.class)
	private FuturesWindControlType windControlType;
	/** 委托数量 */
	private BigDecimal quantity;
	/** 委托状态 */
	private FuturesTradeEntrustState state;
	/** 交易成功时间 */
	private Date tradeTime;
	/** 成交量 */
	private BigDecimal filled;
	/** 剩余未成交量 */
	private BigDecimal remaining;
	/** 已成交部分均价 */
	private BigDecimal avgFillPrice;
	/** 已成交部分总费用 */
	private BigDecimal totalFillCost;
	/** 最终成交价格 */
	private BigDecimal tradePrice;
	/** 盈亏（交易所货币） */
	private BigDecimal currencyProfitOrLoss;
	/** 盈亏（人民币） */
	private BigDecimal profitOrLoss;
	/** 发布人盈亏（人民币） */
	private BigDecimal publisherProfitOrLoss;
	/** 平台盈亏（人民币） */
	private BigDecimal platformProfitOrLoss;
	/** 结算时的汇率 */
	private BigDecimal settlementRate;
	/** 结算时间 */
	private Date settlementTime;
	/** 更新时间 */
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public FuturesOrder getOrder() {
		return order;
	}

	public void setOrder(FuturesOrder order) {
		this.order = order;
	}

	public FuturesTradeEntrust getTradeEntrust() {
		return tradeEntrust;
	}

	public void setTradeEntrust(FuturesTradeEntrust tradeEntrust) {
		this.tradeEntrust = tradeEntrust;
	}

	public Date getEntrustTime() {
		return entrustTime;
	}

	public void setEntrustTime(Date entrustTime) {
		this.entrustTime = entrustTime;
	}

	public FuturesTradeActionType getTradeActionType() {
		return tradeActionType;
	}

	public void setTradeActionType(FuturesTradeActionType tradeActionType) {
		this.tradeActionType = tradeActionType;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public FuturesTradeEntrustState getState() {
		return state;
	}

	public void setState(FuturesTradeEntrustState state) {
		this.state = state;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public BigDecimal getFilled() {
		return filled;
	}

	public void setFilled(BigDecimal filled) {
		this.filled = filled;
	}

	public BigDecimal getRemaining() {
		return remaining;
	}

	public void setRemaining(BigDecimal remaining) {
		this.remaining = remaining;
	}

	public BigDecimal getAvgFillPrice() {
		return avgFillPrice;
	}

	public void setAvgFillPrice(BigDecimal avgFillPrice) {
		this.avgFillPrice = avgFillPrice;
	}

	public BigDecimal getTotalFillCost() {
		return totalFillCost;
	}

	public void setTotalFillCost(BigDecimal totalFillCost) {
		this.totalFillCost = totalFillCost;
	}

	public BigDecimal getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public BigDecimal getCurrencyProfitOrLoss() {
		return currencyProfitOrLoss;
	}

	public void setCurrencyProfitOrLoss(BigDecimal currencyProfitOrLoss) {
		this.currencyProfitOrLoss = currencyProfitOrLoss;
	}

	public BigDecimal getProfitOrLoss() {
		return profitOrLoss;
	}

	public void setProfitOrLoss(BigDecimal profitOrLoss) {
		this.profitOrLoss = profitOrLoss;
	}

	public BigDecimal getPublisherProfitOrLoss() {
		return publisherProfitOrLoss;
	}

	public void setPublisherProfitOrLoss(BigDecimal publisherProfitOrLoss) {
		this.publisherProfitOrLoss = publisherProfitOrLoss;
	}

	public BigDecimal getPlatformProfitOrLoss() {
		return platformProfitOrLoss;
	}

	public void setPlatformProfitOrLoss(BigDecimal platformProfitOrLoss) {
		this.platformProfitOrLoss = platformProfitOrLoss;
	}

	public BigDecimal getSettlementRate() {
		return settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
	}

	public Date getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(Date settlementTime) {
		this.settlementTime = settlementTime;
	}

}
