package com.waben.stock.datalayer.futures.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货交易所（交易市场）
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_futures_exchange")
public class FuturesExchange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 交易所代码
	 */
	private String code;
	/**
	 * 交易所全称
	 */
	private String name;
	/**
	 * 交易所类型
	 * <ul>
	 * <li>1外盘</li>
	 * <li>2内盘</li>
	 * </ul>
	 */
	private Integer exchangeType;
	/**
	 * 是否可用
	 */
	private Boolean enable;
	/**
	 * 北京时间和交易所的时差
	 */
	private Integer timeZoneGap;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTimeZoneGap() {
		return timeZoneGap;
	}

	public void setTimeZoneGap(Integer timeZoneGap) {
		this.timeZoneGap = timeZoneGap;
	}

	public Integer getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(Integer exchangeType) {
		this.exchangeType = exchangeType;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
