package com.waben.stock.interfaces.dto.admin.futures;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesContractTimeDto {

	private Long id;
	
	/**
	 * 合约编号
	 */
	private String contractNo;
	
	/**
	 * The first day on which a notice of intent to deliver a commodity can be
	 * made by a clearinghouse to a buyer in order to fulfill a given futures
	 * contract
	 */
	private Date firstNoticeDate;
	
	/**
	 * The first day when an invesor who is short a commodify futures contract
	 * may notify the clearinghouse of the intention to deliver the commodity
	 */
	private Date firstPositonDate;
	
	/**
	 * 最后交易日
	 */
	private Date lastTradingDate;
	
	/**
	 * 到期日期
	 */
	private Date expirationDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Date getFirstNoticeDate() {
		return firstNoticeDate;
	}

	public void setFirstNoticeDate(Date firstNoticeDate) {
		this.firstNoticeDate = firstNoticeDate;
	}

	public Date getFirstPositonDate() {
		return firstPositonDate;
	}

	public void setFirstPositonDate(Date firstPositonDate) {
		this.firstPositonDate = firstPositonDate;
	}

	public Date getLastTradingDate() {
		return lastTradingDate;
	}

	public void setLastTradingDate(Date lastTradingDate) {
		this.lastTradingDate = lastTradingDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
}
