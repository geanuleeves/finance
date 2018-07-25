package com.waben.stock.futuresgateway.yisheng.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货品种
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_es_commodity")
public class FuturesCommodity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 品种编号 */
	private String commodityNo;
	/** 品种名称 */
	private String commodityName;
	/** 品种英文名称 */
	private String commodityEngName;
	/** 品种类型 */
	private String commodityType;
	/** 最小变动价位 */
	private BigDecimal commodityTickSize;
	/** 品种合约年限 */
	private Integer commodityContractLen;
	/** 货币 */
	private String currency;
	/** 每手乘数 */
	private BigDecimal contractSize;
	/** 交易所ID */
	private Long exchangeId;
	/** 交易所编号 */
	private String exchangeNo;
	/** 北京时间的时差和交易所 */
	private Integer timeZoneGap;
	/** 是否可用 */
	private Boolean enable;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCommodityNo() {
		return commodityNo;
	}

	public void setCommodityNo(String commodityNo) {
		this.commodityNo = commodityNo;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommodityEngName() {
		return commodityEngName;
	}

	public void setCommodityEngName(String commodityEngName) {
		this.commodityEngName = commodityEngName;
	}

	public String getCommodityType() {
		return commodityType;
	}

	public void setCommodityType(String commodityType) {
		this.commodityType = commodityType;
	}

	public BigDecimal getCommodityTickSize() {
		return commodityTickSize;
	}

	public void setCommodityTickSize(BigDecimal commodityTickSize) {
		this.commodityTickSize = commodityTickSize;
	}

	public Integer getCommodityContractLen() {
		return commodityContractLen;
	}

	public void setCommodityContractLen(Integer commodityContractLen) {
		this.commodityContractLen = commodityContractLen;
	}

	public BigDecimal getContractSize() {
		return contractSize;
	}

	public void setContractSize(BigDecimal contractSize) {
		this.contractSize = contractSize;
	}

	public Long getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(Long exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getExchangeNo() {
		return exchangeNo;
	}

	public void setExchangeNo(String exchangeNo) {
		this.exchangeNo = exchangeNo;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Integer getTimeZoneGap() {
		return timeZoneGap;
	}

	public void setTimeZoneGap(Integer timeZoneGap) {
		this.timeZoneGap = timeZoneGap;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

}
