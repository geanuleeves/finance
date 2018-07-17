package com.waben.stock.applayer.tactics.dto.publisher;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.PaymentState;
import com.waben.stock.interfaces.enums.PaymentType;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentRecordDto {

	private Long id;
	/**
	 * 支付单号
	 */
	@ApiModelProperty(value = "支付单号")
	private String paymentNo;
	/**
	 * 金额
	 */
	@ApiModelProperty(value = "金额")
	private BigDecimal amount;
	/**
	 * 支付方式
	 */
	@ApiModelProperty(value = "支付方式")
	private PaymentType type;
	/**
	 * 支付状态
	 */
	@ApiModelProperty(value = "支付状态")
	private PaymentState state;
	/**
	 * 发布人ID
	 */
	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;
	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPaymentNo() {
		return paymentNo;
	}

	public void setPaymentNo(String paymentNo) {
		this.paymentNo = paymentNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public PaymentType getType() {
		return type;
	}

	public void setType(PaymentType type) {
		this.type = type;
	}

	public PaymentState getState() {
		return state;
	}
	
	public String getStateStr() {
		if(state != null) {
			return state.getStatus();
		}
		return null;
	}

	public void setState(PaymentState state) {
		this.state = state;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
