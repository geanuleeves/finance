package com.waben.stock.interfaces.pojo.param.futures;

import java.math.BigDecimal;

import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;

/**
 * 下单参数
 * 
 * @author lma
 *
 */
public class PlaceOrderParam {

	/** 品种编号 */
	private String commoditySymbol;
	/** 品种名称 */
	private String commodityName;
	/** 品种货币 */
	private String commodityCurrency;
	/** 合约ID */
	private Long contractId;
	/** 合约编号 */
	private String contractNo;
	/** 开仓手续费 */
	private BigDecimal openwindServiceFee;
	/** 平仓手续费 */
	private BigDecimal unwindServiceFee;
	/** 发布人ID */
	private Long publisherId;
	/** 订单类型 */
	private FuturesOrderType orderType;
	/** 订单总量 */
	private BigDecimal totalQuantity;
	/** 保证金 */
	private BigDecimal reserveFund;
	/** 综合手续费 */
	private BigDecimal serviceFee;
	/** 交易价格类型 */
	private FuturesTradePriceType tradePriceType;
	/** 委托价格 */
	private BigDecimal entrustPrice;
	/**
	 * 触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer limitProfitType;
	/** 止盈金额（用户设置） */
	private BigDecimal perUnitLimitProfitAmount;
	/**
	 * 触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer limitLossType;
	/** 止损金额（用户设置） */
	private BigDecimal perUnitLimitLossAmount;
	/** 是否测试单 */
	private Boolean isTest;

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

	public String getCommodityCurrency() {
		return commodityCurrency;
	}

	public void setCommodityCurrency(String commodityCurrency) {
		this.commodityCurrency = commodityCurrency;
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

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
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

	public FuturesTradePriceType getTradePriceType() {
		return tradePriceType;
	}

	public void setTradePriceType(FuturesTradePriceType tradePriceType) {
		this.tradePriceType = tradePriceType;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

	public Integer getLimitProfitType() {
		return limitProfitType;
	}

	public void setLimitProfitType(Integer limitProfitType) {
		this.limitProfitType = limitProfitType;
	}

	public BigDecimal getPerUnitLimitProfitAmount() {
		return perUnitLimitProfitAmount;
	}

	public void setPerUnitLimitProfitAmount(BigDecimal perUnitLimitProfitAmount) {
		this.perUnitLimitProfitAmount = perUnitLimitProfitAmount;
	}

	public Integer getLimitLossType() {
		return limitLossType;
	}

	public void setLimitLossType(Integer limitLossType) {
		this.limitLossType = limitLossType;
	}

	public BigDecimal getPerUnitLimitLossAmount() {
		return perUnitLimitLossAmount;
	}

	public void setPerUnitLimitLossAmount(BigDecimal perUnitLimitLossAmount) {
		this.perUnitLimitLossAmount = perUnitLimitLossAmount;
	}

	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}

}
