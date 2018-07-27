package com.waben.stock.futuresgateway.yingtou.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货交易所
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_exchange")
public class FuturesExchange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 交易所名称
	 * <p>
	 * 一般为代码
	 * </p>
	 */
	private String exchange;
	/**
	 * 交易所全称
	 */
	private String exchangeFullName;
	/**
	 * 北京时间的时差和交易所
	 */
	private Integer timeZoneGap;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public String getExchangeFullName() {
		return exchangeFullName;
	}

	public void setExchangeFullName(String exchangeFullName) {
		this.exchangeFullName = exchangeFullName;
	}

	public Integer getTimeZoneGap() {
		return timeZoneGap;
	}

	public void setTimeZoneGap(Integer timeZoneGap) {
		this.timeZoneGap = timeZoneGap;
	}

}
