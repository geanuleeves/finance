package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesOrderCountDto {

	private BigDecimal quantity;
	
	private BigDecimal fund;
	
	private BigDecimal fee;
	
	private BigDecimal deferred;

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getFund() {
		return fund;
	}

	public void setFund(BigDecimal fund) {
		this.fund = fund;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public BigDecimal getDeferred() {
		return deferred;
	}

	public void setDeferred(BigDecimal deferred) {
		this.deferred = deferred;
	}
	
	
}
