package com.waben.stock.interfaces.dto.admin.futures;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesHolidayDto {

	private Long id;
	
	/**
	 * 开始日期
	 */
	private Date startTime;
	
	/**
	 * 结束日期
	 */
	private Date endTime;
	
	/**
	 * 下一个交易日期
	 */
	private Date nextTradeTime;
	
	/**
	 * 节假日备注
	 */
	private String remark;
	
	/**
	 * 是否生效
	 */
	private Boolean enable;
	
	/**
	 * 状态
	 */
	private String state;
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	/**
	 * 品种ID
	 */
	private Long commodityId;
	
	/**
	 * 交易代码
	 */
	private String commoditySymbol;
	
	/**
	 * 交易品种
	 */
	private String commodityName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Date getNextTradeTime() {
		return nextTradeTime;
	}

	public void setNextTradeTime(Date nextTradeTime) {
		this.nextTradeTime = nextTradeTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public String getCommoditySymbol() {
		return commoditySymbol;
	}

	public void setCommoditySymbol(String commoditySymbol) {
		this.commoditySymbol = commoditySymbol;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
	
}
