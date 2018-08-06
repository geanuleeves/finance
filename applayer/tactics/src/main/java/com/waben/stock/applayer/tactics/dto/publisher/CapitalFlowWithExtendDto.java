package com.waben.stock.applayer.tactics.dto.publisher;

import com.waben.stock.interfaces.enums.CapitalFlowType;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class CapitalFlowWithExtendDto {

	private Long id;
	/**
	 * 金额
	 */
	@ApiModelProperty(value = "金额")
	private BigDecimal amount;
	/**
	 * 当前可用余额
	 * <p>
	 * 产生该流水之后的可用余额
	 * </p>
	 */
	@ApiModelProperty(value = "当前可用余额")
	private BigDecimal availableBalance;
	/**
	 * 流水类型
	 */
	@ApiModelProperty(value = "流水类型")
	private CapitalFlowType type;
	/**
	 * 流水号
	 */
	@ApiModelProperty(value = "流水号")
	private String flowNo;
	/**
	 * 产生时间
	 */
	@ApiModelProperty(value = "产生时间")
	private Date occurrenceTime;
	/**
	 * 发布人ID
	 */
	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;
	/**
	 * 发布人序列号
	 */
	private String publisherSerialCode;
	/**
	 * 股票代码
	 */
	private String stockCode;
	/**
	 * 股票名称
	 */
	private String stockName;
	/**
	 * 策略类型ID
	 */
	@ApiModelProperty(value = "")
	private Long strategyTypeId;
	/**
	 * 策略类型名称
	 */
	@ApiModelProperty(value = "")
	private String strategyTypeName;
	/**
	 * 充值方式
	 */
	@ApiModelProperty(value = "充值方式")
	private String paymentType;
	/**
	 * 交易代码（期货交易）
	 */
	@ApiModelProperty(value = "交易代码（期货交易）")
	private String commoditySymbol;
	/**
	 * 交易品种（期货交易）
	 */
	@ApiModelProperty(value = "交易品种（期货交易）")
	private String commodityName;
	/**
	 * 合约代码
	 */
	@ApiModelProperty(value = "合约代码")
	private String contractNo;

	/** 保证金（人民币） */
	@ApiModelProperty(value = "保证金（人民币）")
	private BigDecimal reserveFund;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public CapitalFlowType getType() {
		return type;
	}

	public void setType(CapitalFlowType type) {
		this.type = type;
	}

	public String getFlowText() {
		return type.getType();
	}

	public Date getOccurrenceTime() {
		return occurrenceTime;
	}

	public void setOccurrenceTime(Date occurrenceTime) {
		this.occurrenceTime = occurrenceTime;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherSerialCode() {
		return publisherSerialCode;
	}

	public void setPublisherSerialCode(String publisherSerialCode) {
		this.publisherSerialCode = publisherSerialCode;
	}

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public Long getStrategyTypeId() {
		return strategyTypeId;
	}

	public void setStrategyTypeId(Long strategyTypeId) {
		this.strategyTypeId = strategyTypeId;
	}

	public String getStrategyTypeName() {
		return strategyTypeName;
	}

	public void setStrategyTypeName(String strategyTypeName) {
		this.strategyTypeName = strategyTypeName;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
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

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}
}
