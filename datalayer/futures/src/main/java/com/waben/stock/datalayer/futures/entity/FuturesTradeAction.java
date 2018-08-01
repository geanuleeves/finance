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
import javax.persistence.Transient;

import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradeActionTypeConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradeEntrustStateConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesWindControlTypeConverter;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
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
	/** 交易开平仓记录编号 */
	private String actionNo;
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
	/** 委托状态(只存在全部成功，不存在部分成功情况) */
	@Convert(converter = FuturesTradeEntrustStateConverter.class)
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

	/***************** 分割线，以下字段为非数据库字段 ********************/
	/** 合约ID */
	@Transient
	private Long contractId;
	/** 品种名称 */
	@Transient
	private String commodityName;
	/** 品种编号 */
	@Transient
	private String commodityNo;
	/** 合约编号 */
	@Transient
	private String contractNo;
	/** 订单交易类型 */
	@Transient
	private FuturesOrderType orderType;
	/** 订单类型 */
	@Transient
	private FuturesTradePriceType futuresTradePriceType;
	/** 最小波动 */
	@Transient
	private BigDecimal minWave;
	/** 最小浮动价格 */
	@Transient
	private BigDecimal perWaveMoney;
	/** 货币缩写 */
	@Transient
	private String currency;
	/** 开仓成交均价 */
	@Transient
	private BigDecimal openAvgFillPrice;
	/** 委托价格 */
	@Transient
	private BigDecimal entrustPrice;

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

	public String getActionNo() {
		return actionNo;
	}

	public void setActionNo(String actionNo) {
		this.actionNo = actionNo;
	}

	public FuturesWindControlType getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(FuturesWindControlType windControlType) {
		this.windControlType = windControlType;
	}

	public Long getContractId() {
		if (order != null) {
			return order.getContractId();
		}
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getCommodityName() {
		if (order != null) {
			return order.getCommodityName();
		}
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommodityNo() {
		if (order != null) {
			return order.getCommoditySymbol();
		}
		return commodityNo;
	}

	public void setCommodityNo(String commodityNo) {
		this.commodityNo = commodityNo;
	}

	public String getContractNo() {
		if (order != null) {
			return order.getContractNo();
		}
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public FuturesOrderType getOrderType() {
		if (order != null) {
			return order.getOrderType();
		}
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public FuturesTradePriceType getFuturesTradePriceType() {
		if (tradeEntrust != null) {
			return tradeEntrust.getPriceType();
		}
		return futuresTradePriceType;
	}

	public void setFuturesTradePriceType(FuturesTradePriceType futuresTradePriceType) {
		this.futuresTradePriceType = futuresTradePriceType;
	}

	public BigDecimal getMinWave() {
		if (order.getContract() != null) {
			return order.getContract().getCommodity().getMinWave();
		}
		return minWave;
	}

	public void setMinWave(BigDecimal minWave) {
		this.minWave = minWave;
	}

	public BigDecimal getPerWaveMoney() {
		if (order.getContract() != null) {
			return order.getContract().getCommodity().getPerWaveMoney();
		}
		return perWaveMoney;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
	}

	public String getCurrency() {
		if (order.getContract() != null) {
			return order.getContract().getCommodity().getCurrency();
		}
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getOpenAvgFillPrice() {
		if (order != null) {
			return order.getOpenAvgFillPrice();
		}
		return openAvgFillPrice;
	}

	public void setOpenAvgFillPrice(BigDecimal openAvgFillPrice) {
		this.openAvgFillPrice = openAvgFillPrice;
	}

	public BigDecimal getEntrustPrice() {
		if (tradeEntrust != null && tradeEntrust.getPriceType() == FuturesTradePriceType.LMT) {
			return tradeEntrust.getEntrustPrice();
		}
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

}
