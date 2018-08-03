package com.waben.stock.interfaces.dto.admin.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesTermAdminDto {

	private Long id;
	
	/**
	 * 交易所代码
	 */
	private String exchangcode;
	
	/**
	 * 交易所全称
	 */
	private String exchangename;
	
	/**
	 * 产品代码
	 */
	private String symbol;
	
	/**
	 * 产品名称
	 */
	private String name;
	
	private Long contractId;
	
	/**
	 * 合约期限编号
	 */
	private String contractNo;
	
	
	/**
	 * 是否为当前实施的期限
	 */
	private boolean current;
	
	/**
	 * The first day on which a notice of intent to deliver a commodity can be
	 * made by a clearinghouse to a buyer in order to fulfill a given futures
	 * contract
	 */
	private String firstNoticeDate;
	
	/**
	 * The first day when an invesor who is short a commodify futures contract
	 * may notify the clearinghouse of the intention to deliver the commodity
	 */
	private String firstPositonDate;
	
	/**
	 * 最后交易日
	 */
	private String lastTradingDate;
	
	/**
	 * 到期日期
	 */
	private String expirationDate;
	
	/**
	 * 强平时间
	 */
	private String forceUnwindDate;
	/**
	 * 周一交易时间(交易所)
	 * <p>
	 * 时间段用“-”隔开，多个时间段用“,”隔开，如“18:00-23:59”、“00:00-17:00,18:00-23:59”
	 * <p>
	 */
	private String monTradeTime;
	/**
	 * 周一交易时间描述
	 */
	private String monTradeTimeDesc;
	/**
	 * 周二交易时间(交易所)
	 */
	private String tueTradeTime;
	/**
	 * 周二交易时间描述
	 */
	private String tueTradeTimeDesc;
	/**
	 * 周三交易时间(交易所)
	 */
	private String wedTradeTime;
	/**
	 * 周三交易时间描述
	 */
	private String wedTradeTimeDesc;
	/**
	 * 周四交易时间(交易所)
	 */
	private String thuTradeTime;
	/**
	 * 周四交易时间描述
	 */
	private String thuTradeTimeDesc;
	/**
	 * 周五交易时间(交易所)
	 */
	private String friTradeTime;
	/**
	 * 周五交易时间描述
	 */
	private String friTradeTimeDesc;
	/**
	 * 周六交易时间(交易所)
	 */
	private String satTradeTime;
	/**
	 * 周六交易时间描述
	 */
	private String satTradeTimeDesc;
	/**
	 * 周日交易时间(交易所)
	 */
	private String sunTradeTime;
	/**
	 * 周日交易时间描述
	 */
	private String sunTradeTimeDesc;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getExchangcode() {
		return exchangcode;
	}
	public void setExchangcode(String exchangcode) {
		this.exchangcode = exchangcode;
	}
	public String getExchangename() {
		return exchangename;
	}
	public void setExchangename(String exchangename) {
		this.exchangename = exchangename;
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
	
	public boolean isCurrent() {
		return current;
	}
	public void setCurrent(boolean current) {
		this.current = current;
	}
	public String getFirstNoticeDate() {
		return firstNoticeDate;
	}
	public void setFirstNoticeDate(String firstNoticeDate) {
		this.firstNoticeDate = firstNoticeDate;
	}
	public String getFirstPositonDate() {
		return firstPositonDate;
	}
	public void setFirstPositonDate(String firstPositonDate) {
		this.firstPositonDate = firstPositonDate;
	}
	public String getLastTradingDate() {
		return lastTradingDate;
	}
	public void setLastTradingDate(String lastTradingDate) {
		this.lastTradingDate = lastTradingDate;
	}
	public String getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}
	public String getForceUnwindDate() {
		return forceUnwindDate;
	}
	public void setForceUnwindDate(String forceUnwindDate) {
		this.forceUnwindDate = forceUnwindDate;
	}
	public String getMonTradeTime() {
		return monTradeTime;
	}
	public void setMonTradeTime(String monTradeTime) {
		this.monTradeTime = monTradeTime;
	}
	public String getMonTradeTimeDesc() {
		return monTradeTimeDesc;
	}
	public void setMonTradeTimeDesc(String monTradeTimeDesc) {
		this.monTradeTimeDesc = monTradeTimeDesc;
	}
	public String getTueTradeTime() {
		return tueTradeTime;
	}
	public void setTueTradeTime(String tueTradeTime) {
		this.tueTradeTime = tueTradeTime;
	}
	public String getTueTradeTimeDesc() {
		return tueTradeTimeDesc;
	}
	public void setTueTradeTimeDesc(String tueTradeTimeDesc) {
		this.tueTradeTimeDesc = tueTradeTimeDesc;
	}
	public String getWedTradeTime() {
		return wedTradeTime;
	}
	public void setWedTradeTime(String wedTradeTime) {
		this.wedTradeTime = wedTradeTime;
	}
	public String getWedTradeTimeDesc() {
		return wedTradeTimeDesc;
	}
	public void setWedTradeTimeDesc(String wedTradeTimeDesc) {
		this.wedTradeTimeDesc = wedTradeTimeDesc;
	}
	public String getThuTradeTime() {
		return thuTradeTime;
	}
	public void setThuTradeTime(String thuTradeTime) {
		this.thuTradeTime = thuTradeTime;
	}
	public String getThuTradeTimeDesc() {
		return thuTradeTimeDesc;
	}
	public void setThuTradeTimeDesc(String thuTradeTimeDesc) {
		this.thuTradeTimeDesc = thuTradeTimeDesc;
	}
	public String getFriTradeTime() {
		return friTradeTime;
	}
	public void setFriTradeTime(String friTradeTime) {
		this.friTradeTime = friTradeTime;
	}
	public String getFriTradeTimeDesc() {
		return friTradeTimeDesc;
	}
	public void setFriTradeTimeDesc(String friTradeTimeDesc) {
		this.friTradeTimeDesc = friTradeTimeDesc;
	}
	public String getSatTradeTime() {
		return satTradeTime;
	}
	public void setSatTradeTime(String satTradeTime) {
		this.satTradeTime = satTradeTime;
	}
	public String getSatTradeTimeDesc() {
		return satTradeTimeDesc;
	}
	public void setSatTradeTimeDesc(String satTradeTimeDesc) {
		this.satTradeTimeDesc = satTradeTimeDesc;
	}
	public String getSunTradeTime() {
		return sunTradeTime;
	}
	public void setSunTradeTime(String sunTradeTime) {
		this.sunTradeTime = sunTradeTime;
	}
	public String getSunTradeTimeDesc() {
		return sunTradeTimeDesc;
	}
	public void setSunTradeTimeDesc(String sunTradeTimeDesc) {
		this.sunTradeTimeDesc = sunTradeTimeDesc;
	}
	

}
