package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;
import java.util.Date;

public class FuturesTradeDto {

	/**
	 * 交易ID
	 */
	private Long id;

	private Long publisherId;
	
	
	/**
	 * 发布人姓名
	 * <p>
	 * 实名认证的姓名
	 * </p>
	 */
	private String publisherName;

	/**
	 * 发布人手机号
	 */
	private String publisherPhone;
	
	/**
	 * 合约代码
	 */
	private String symbol;

	/**
	 * 合约名称
	 */
	private String name;
	
	/**
	 * 合约ID
	 */
	private Long contractId;

	/**
	 * 合约编号
	 */
	private String contractNo;
	
	/**
	 * 交易方向
	 */
	private Integer orderType;
	
	/**
	 * 委托状态
	 */
	private Integer state;
	
	/** 交易开平仓 类型 */
	private Integer tradeActionType;
	
	/** 委托价格 */
	private BigDecimal entrustPrice;
	
	/** 委托数量 */
	private BigDecimal quantity;
	
	/** 成交量 */
	private BigDecimal filled;
	
	/** 已成交部分均价 */
	private BigDecimal avgFillPrice;
	
	/** 盈亏（人民币） */
	private BigDecimal profitOrLoss;
	
	/** 已成交部分总费用 */
	private BigDecimal totalFillCost;
	
	/** 最终成交价格 */
	private BigDecimal tradePrice;
	
	/** 交易成功时间 */
	private Date tradeTime;
	
	/** 委托时间 */
	private Date entrustTime;
	
	/** 委托编号 */
	private String entrustNo;
	
	/** 风控类型 */
	private String windControlType;
	
	/** 价格类型 */
	private Integer priceType;
	
	/** 退回保证金（人民币） */
	private BigDecimal returnReserveFund;
	
	/** 是否为测试单 */
	private Boolean isTest;

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

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPublisherPhone() {
		return publisherPhone;
	}

	public void setPublisherPhone(String publisherPhone) {
		this.publisherPhone = publisherPhone;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getTradeActionType() {
		return tradeActionType;
	}

	public void setTradeActionType(Integer tradeActionType) {
		this.tradeActionType = tradeActionType;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getFilled() {
		return filled;
	}

	public void setFilled(BigDecimal filled) {
		this.filled = filled;
	}

	public BigDecimal getAvgFillPrice() {
		return avgFillPrice;
	}

	public void setAvgFillPrice(BigDecimal avgFillPrice) {
		this.avgFillPrice = avgFillPrice;
	}

	public BigDecimal getProfitOrLoss() {
		return profitOrLoss;
	}

	public void setProfitOrLoss(BigDecimal profitOrLoss) {
		this.profitOrLoss = profitOrLoss;
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

	public Date getEntrustTime() {
		return entrustTime;
	}

	public void setEntrustTime(Date entrustTime) {
		this.entrustTime = entrustTime;
	}

	public String getEntrustNo() {
		return entrustNo;
	}

	public void setEntrustNo(String entrustNo) {
		this.entrustNo = entrustNo;
	}

	public String getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(String windControlType) {
		this.windControlType = windControlType;
	}

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public BigDecimal getReturnReserveFund() {
		return returnReserveFund;
	}

	public void setReturnReserveFund(BigDecimal returnReserveFund) {
		this.returnReserveFund = returnReserveFund;
	}

	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}
	
	
}
