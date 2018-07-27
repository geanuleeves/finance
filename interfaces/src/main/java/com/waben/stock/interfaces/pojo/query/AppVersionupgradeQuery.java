package com.waben.stock.interfaces.pojo.query;

public class AppVersionupgradeQuery extends PageAndSortQuery {

	private Long id;
	
	private Integer deviceType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(Integer deviceType) {
		this.deviceType = deviceType;
	}
	
}
