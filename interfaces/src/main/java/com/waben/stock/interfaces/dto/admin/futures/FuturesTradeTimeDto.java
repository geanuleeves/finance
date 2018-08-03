package com.waben.stock.interfaces.dto.admin.futures;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesTradeTimeDto {

	private Long commodityId;
	
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

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
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
