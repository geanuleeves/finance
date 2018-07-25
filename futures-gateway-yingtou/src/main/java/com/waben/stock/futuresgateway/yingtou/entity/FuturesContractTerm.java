package com.waben.stock.futuresgateway.yingtou.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货合约期限
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_contract_term")
public class FuturesContractTerm {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 合约ID
	 */
	private Long contractId;
	/**
	 * 合约编号
	 */
	private Integer contractNo;
	/**
	 * 合约名称
	 */
	private String symbol;
	/**
	 * 是否为当前实施的期限
	 */
	private boolean current;
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
	/**
	 * 周一交易时间(交易所)
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

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
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

	public Integer getContractNo() {
		return contractNo;
	}

	public void setContractNo(Integer contractNo) {
		this.contractNo = contractNo;
	}

}
