package com.waben.stock.interfaces.dto.admin.futures;

import java.util.Date;

public class FuturesTradeLimitDto {

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
	 * 交易所类型
	 * <ul>
	 * <li>1外盘</li>
	 * <li>2内盘</li>
	 * </ul>
	 */
	private Integer exchangeType;

	/**
	 * 合约ID
	 */
	private Long contractId;
	/**
	 * 品种ID
	 */
	private Long commodityId;

	/**
	 * 交易代码
	 */
	private String symbol;
	/**
	 * 交易名称
	 */
	private String name;

	/**
	 * 合约代码
	 */
	private String contractNo;

	/**
	 * 限制类型
	 */
	private String limitType;

	/**
	 * 星期
	 */
	private Integer weekDay;

	/**
	 * 开始时间
	 */
	private String startLimitTime;

	/**
	 * 结束时间
	 */
	private String endLimitTime;

	/**
	 * 是否可用
	 */
	private Boolean enable;

	/**
	 * 更新时间
	 */
	private Date updateTime;

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

	public Integer getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(Integer exchangeType) {
		this.exchangeType = exchangeType;
	}

	public String getStartLimitTime() {
		return startLimitTime;
	}

	public void setStartLimitTime(String startLimitTime) {
		this.startLimitTime = startLimitTime;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getLimitType() {
		return limitType;
	}

	public void setLimitType(String limitType) {
		this.limitType = limitType;
	}

	public Integer getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(Integer weekDay) {
		this.weekDay = weekDay;
	}

	public String getEndLimitTime() {
		return endLimitTime;
	}

	public void setEndLimitTime(String endLimitTime) {
		this.endLimitTime = endLimitTime;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}
}
