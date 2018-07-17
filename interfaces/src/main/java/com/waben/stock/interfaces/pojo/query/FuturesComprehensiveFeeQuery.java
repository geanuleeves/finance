package com.waben.stock.interfaces.pojo.query;

import java.util.List;

public class FuturesComprehensiveFeeQuery extends PageAndSortQuery {

	private List<Long> publisherId;
	
	private String phone;
	
	private String name;
	
	private Integer state;

	public List<Long> getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(List<Long> publisherId) {
		this.publisherId = publisherId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}
	
	
	
	
}
