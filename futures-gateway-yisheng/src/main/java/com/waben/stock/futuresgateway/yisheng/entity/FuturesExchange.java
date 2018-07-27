package com.waben.stock.futuresgateway.yisheng.entity;

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
@Table(name = "f_gateway_futures_es_exchange")
public class FuturesExchange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 交易所编号
	 * <p>
	 * 一般为代码
	 * </p>
	 */
	private String exchangeNo;
	/**
	 * 交易所全称
	 */
	private String exchangeFullName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getExchangeNo() {
		return exchangeNo;
	}

	public void setExchangeNo(String exchangeNo) {
		this.exchangeNo = exchangeNo;
	}

	public String getExchangeFullName() {
		return exchangeFullName;
	}

	public void setExchangeFullName(String exchangeFullName) {
		this.exchangeFullName = exchangeFullName;
	}

}
