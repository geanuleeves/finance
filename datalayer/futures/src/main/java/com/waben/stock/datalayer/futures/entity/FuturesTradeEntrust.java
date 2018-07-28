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

import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesOrderTypeConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradeActionTypeConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesTradePriceTypeConverter;
import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesWindControlTypeConverter;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;

/**
 * 交易委托
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_futures_trade_entrust")
public class FuturesTradeEntrust {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 用户ID */
	private Long publisherId;
	/** 对应的合约 */
	@ManyToOne
	@JoinColumn(name = "contract_id")
	private FuturesContract contract;
	/** 品种编号 */
	private String commodityNo;
	/** 合约编号 */
	private String contractNo;
	/** 订单交易类型 */
	@Convert(converter = FuturesOrderTypeConverter.class)
	private FuturesOrderType orderType;
	/** 委托时间 */
	private Date entrustTime;
	/** 价格类型 */
	@Convert(converter = FuturesTradePriceTypeConverter.class)
	private FuturesTradePriceType priceType;
	/** 委托价格 */
	private BigDecimal entrustPrice;
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
	/** 已成交量 */
	private BigDecimal filled;
	/** 剩余未成交量 */
	private BigDecimal remaining;
	/** 已成交部分均价 */
	private BigDecimal avgFillPrice;
	/** 已成交部分总费用 */
	private BigDecimal totalFillCost;
	/** 最终成交价格 */
	private BigDecimal tradePrice;
	/** 交易成功时间 */
	private Date tradeTime;
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

	public FuturesContract getContract() {
		return contract;
	}

	public void setContract(FuturesContract contract) {
		this.contract = contract;
	}

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

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public Date getEntrustTime() {
		return entrustTime;
	}

	public void setEntrustTime(Date entrustTime) {
		this.entrustTime = entrustTime;
	}

	public FuturesTradePriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(FuturesTradePriceType priceType) {
		this.priceType = priceType;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
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

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public FuturesWindControlType getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(FuturesWindControlType windControlType) {
		this.windControlType = windControlType;
	}

}
