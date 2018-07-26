package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesOrderDto {

	private Long id;
	/** 对应的发布人Id */
	@ApiModelProperty(value = "对应的发布人Id")
	private Long publisherId;
	/** 发布时间 */
	@ApiModelProperty(value = "发布时间")
	private Date postTime;
	/** 订单编号 */
	@ApiModelProperty(value = "订单编号")
	private String tradeNo;
	/** 订单类型 */
	@ApiModelProperty(value = "订单类型")
	private FuturesOrderType orderType;
	/** 总量（手） */
	@ApiModelProperty(value = "总量（手）")
	private BigDecimal totalQuantity;
	/** 已成交量（开仓） */
	@ApiModelProperty(value = "已成交量（开仓）")
	private BigDecimal openFilled;
	/** 剩余未成交量（开仓） */
	@ApiModelProperty(value = "剩余未成交量（开仓）")
	private BigDecimal openRemaining;
	/** 已成交部分均价（开仓） */
	@ApiModelProperty(value = "已成交部分均价（开仓）")
	private BigDecimal openAvgFillPrice;
	/** 已成交部分总费用（开仓） */
	@ApiModelProperty(value = "已成交部分总费用（开仓）")
	private BigDecimal openTotalFillCost;
	/** 已成交量（平仓） */
	@ApiModelProperty(value = "已成交量（平仓）")
	private BigDecimal closeFilled;
	/** 剩余未成交量（平仓） */
	@ApiModelProperty(value = "剩余未成交量（平仓）")
	private BigDecimal closeRemaining;
	/** 已成交部分均价（平仓） */
	@ApiModelProperty(value = "已成交部分均价（平仓）")
	private BigDecimal closeAvgFillPrice;
	/** 已成交部分总费用（平仓） */
	@ApiModelProperty(value = "已成交部分总费用（平仓）")
	private BigDecimal closeTotalFillCost;
	/** 保证金（人民币） */
	@ApiModelProperty(value = "保证金（人民币）")
	private BigDecimal reserveFund;
	/** 服务费（人民币） */
	@ApiModelProperty(value = "服务费（人民币）")
	private BigDecimal serviceFee;
	/** 品种代码（取期货品种设置快照） */
	@ApiModelProperty(value = "品种代码（取期货品种设置快照）")
	private String commoditySymbol;
	/** 品种名称（取期货品种设置快照） */
	@ApiModelProperty(value = "品种名称（取期货品种设置快照）")
	private String commodityName;
	/** 合约编号（取期货合约设置快照） */
	@ApiModelProperty(value = "合约编号（取期货合约设置快照）")
	private String contractNo;
	/** 货币（取期货品种设置快照） */
	@ApiModelProperty(value = "货币（取期货品种设置快照）")
	private String commodityCurrency;
	/** 开仓手续费（取期货品种设置快照） */
	@ApiModelProperty(value = "开仓手续费（取期货品种设置快照）")
	private BigDecimal openwindServiceFee;
	/** 平仓手续费（取期货品种设置快照） */
	@ApiModelProperty(value = "平仓手续费（取期货品种设置快照）")
	private BigDecimal unwindServiceFee;
	/** 订单状态 */
	@ApiModelProperty(value = "订单状态")
	private FuturesOrderState state;
	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/** 是否为测试单 */
	@ApiModelProperty(value = "是否为测试单")
	private Boolean isTest;

	/***************** 分割线，以下字段为非数据库字段 ********************/
	/** 品种ID */
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;
	/** 合约ID */
	@ApiModelProperty(value = "合约ID")
	private Long contractId;
	/** 交易所名称 */
	@ApiModelProperty(value = "交易所名称")
	private String exchangeName;

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

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(BigDecimal totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public BigDecimal getOpenFilled() {
		return openFilled;
	}

	public void setOpenFilled(BigDecimal openFilled) {
		this.openFilled = openFilled;
	}

	public BigDecimal getOpenRemaining() {
		return openRemaining;
	}

	public void setOpenRemaining(BigDecimal openRemaining) {
		this.openRemaining = openRemaining;
	}

	public BigDecimal getOpenAvgFillPrice() {
		return openAvgFillPrice;
	}

	public void setOpenAvgFillPrice(BigDecimal openAvgFillPrice) {
		this.openAvgFillPrice = openAvgFillPrice;
	}

	public BigDecimal getOpenTotalFillCost() {
		return openTotalFillCost;
	}

	public void setOpenTotalFillCost(BigDecimal openTotalFillCost) {
		this.openTotalFillCost = openTotalFillCost;
	}

	public BigDecimal getCloseFilled() {
		return closeFilled;
	}

	public void setCloseFilled(BigDecimal closeFilled) {
		this.closeFilled = closeFilled;
	}

	public BigDecimal getCloseRemaining() {
		return closeRemaining;
	}

	public void setCloseRemaining(BigDecimal closeRemaining) {
		this.closeRemaining = closeRemaining;
	}

	public BigDecimal getCloseAvgFillPrice() {
		return closeAvgFillPrice;
	}

	public void setCloseAvgFillPrice(BigDecimal closeAvgFillPrice) {
		this.closeAvgFillPrice = closeAvgFillPrice;
	}

	public BigDecimal getCloseTotalFillCost() {
		return closeTotalFillCost;
	}

	public void setCloseTotalFillCost(BigDecimal closeTotalFillCost) {
		this.closeTotalFillCost = closeTotalFillCost;
	}

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public String getCommoditySymbol() {
		return commoditySymbol;
	}

	public void setCommoditySymbol(String commoditySymbol) {
		this.commoditySymbol = commoditySymbol;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCommodityCurrency() {
		return commodityCurrency;
	}

	public void setCommodityCurrency(String commodityCurrency) {
		this.commodityCurrency = commodityCurrency;
	}

	public BigDecimal getOpenwindServiceFee() {
		return openwindServiceFee;
	}

	public void setOpenwindServiceFee(BigDecimal openwindServiceFee) {
		this.openwindServiceFee = openwindServiceFee;
	}

	public BigDecimal getUnwindServiceFee() {
		return unwindServiceFee;
	}

	public void setUnwindServiceFee(BigDecimal unwindServiceFee) {
		this.unwindServiceFee = unwindServiceFee;
	}

	public FuturesOrderState getState() {
		return state;
	}

	public void setState(FuturesOrderState state) {
		this.state = state;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

}
