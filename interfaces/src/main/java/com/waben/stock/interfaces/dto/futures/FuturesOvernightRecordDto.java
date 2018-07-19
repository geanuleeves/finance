package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class FuturesOvernightRecordDto {

	private Long id;
	/**
	 * 发布人ID
	 */
	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;
	/**
	 * 对应的订单
	 */
	@ApiModelProperty(value = "对应的订单")
	private FuturesOrderDto order;
	/**
	 * 隔夜保证金
	 */
	@ApiModelProperty(value = "隔夜保证金")
	private BigDecimal overnightReserveFund;
	/**
	 * 隔夜递延费
	 */
	@ApiModelProperty(value = "隔夜递延费")
	private BigDecimal overnightDeferredFee;
	/**
	 * 递延日期
	 */
	@ApiModelProperty(value = "递延日期")
	private Date deferredTime;
	/**
	 * 扣费日期
	 */
	@ApiModelProperty(value = "扣费日期")
	private Date reduceTime;
	/**
	 * 品种名称
	 */
	@ApiModelProperty(value = "品种名称")
	private String commodityName;
	/**
	 * 品种编号
	 */
	@ApiModelProperty(value = "品种编号")
	private String commoditySymbol;
	/**
	 * 合约编号
	 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;

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

	public FuturesOrderDto getOrder() {
		return order;
	}

	public void setOrder(FuturesOrderDto order) {
		this.order = order;
	}

	public BigDecimal getOvernightReserveFund() {
		return overnightReserveFund;
	}

	public void setOvernightReserveFund(BigDecimal overnightReserveFund) {
		this.overnightReserveFund = overnightReserveFund;
	}

	public BigDecimal getOvernightDeferredFee() {
		return overnightDeferredFee;
	}

	public void setOvernightDeferredFee(BigDecimal overnightDeferredFee) {
		this.overnightDeferredFee = overnightDeferredFee;
	}

	public Date getDeferredTime() {
		return deferredTime;
	}

	public void setDeferredTime(Date deferredTime) {
		this.deferredTime = deferredTime;
	}

	public Date getReduceTime() {
		return reduceTime;
	}

	public void setReduceTime(Date reduceTime) {
		this.reduceTime = reduceTime;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommoditySymbol() {
		return commoditySymbol;
	}

	public void setCommoditySymbol(String commoditySymbol) {
		this.commoditySymbol = commoditySymbol;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

}
