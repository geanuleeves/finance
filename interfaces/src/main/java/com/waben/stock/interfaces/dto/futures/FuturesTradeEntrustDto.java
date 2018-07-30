package com.waben.stock.interfaces.dto.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesTradeEntrustDto {

	private Long id;
	/** 委托编号 */
	@ApiModelProperty(value = "委托编号")
	private String entrustNo;
	/** 用户ID */
	@ApiModelProperty(value = "用户ID")
	private Long publisherId;
	/** 品种编号 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/** 合约编号 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/** 订单交易类型 */
	@ApiModelProperty(value = "订单交易类型")
	private FuturesOrderType orderType;
	/** 委托时间 */
	@ApiModelProperty(value = "委托时间")
	private Date entrustTime;
	/** 价格类型 */
	@ApiModelProperty(value = "价格类型")
	private FuturesTradePriceType priceType;
	/** 委托价格 */
	@ApiModelProperty(value = "委托价格")
	private BigDecimal entrustPrice;
	/** 交易开平仓 类型 */
	@ApiModelProperty(value = "交易开平仓 类型")
	private FuturesTradeActionType tradeActionType;
	/** 委托数量 */
	@ApiModelProperty(value = "委托数量")
	private BigDecimal quantity;
	/** 委托状态 */
	@ApiModelProperty(value = "委托状态")
	private FuturesTradeEntrustState state;
	/** 已成交量 */
	@ApiModelProperty(value = "已成交量")
	private BigDecimal filled;
	/** 剩余未成交量 */
	@ApiModelProperty(value = "剩余未成交量")
	private BigDecimal remaining;
	/** 已成交部分均价 */
	@ApiModelProperty(value = "已成交部分均价")
	private BigDecimal avgFillPrice;
	/** 已成交部分总费用 */
	@ApiModelProperty(value = "已成交部分总费用")
	private BigDecimal totalFillCost;
	/** 最终成交价格 */
	@ApiModelProperty(value = "最终成交价格")
	private BigDecimal tradePrice;
	/** 交易成功时间 */
	@ApiModelProperty(value = "交易成功时间")
	private Date tradeTime;
	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/** 合约名称 */
	@ApiModelProperty(value = "合约名称 */")
	private String contractName;
	/** 交易类型（买/卖）*/
	private FuturesTradeActionType tradeType;

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

	public String getEntrustNo() {
		return entrustNo;
	}

	public void setEntrustNo(String entrustNo) {
		this.entrustNo = entrustNo;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public FuturesTradeActionType getTradeType() {
		return tradeType;
	}

	public void setTradeType(FuturesTradeActionType tradeType) {
		this.tradeType = tradeType;
	}
}
