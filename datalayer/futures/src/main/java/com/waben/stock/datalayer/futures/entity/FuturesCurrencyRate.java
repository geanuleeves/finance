package com.waben.stock.datalayer.futures.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货货币汇率
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_futures_currency_rate")
public class FuturesCurrencyRate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 货币，如“USD”
	 */
	@Column(unique = true)
	private String currency;
	/**
	 * 货币名称，如“美元”
	 */
	private String currencyName;
	/**
	 * 汇率，如“7.0”，表示1美元=7.0*1人民币
	 */
	private BigDecimal rate;

	/**
	 * 货币符号，如“$”,表示美元
	 */
	private String currencySign;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCurrencySign() {
		return currencySign;
	}

	public void setCurrencySign(String currencySign) {
		this.currencySign = currencySign;
	}

}
