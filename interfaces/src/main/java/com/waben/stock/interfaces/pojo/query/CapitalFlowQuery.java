package com.waben.stock.interfaces.pojo.query;

import com.waben.stock.interfaces.enums.CapitalFlowType;

import java.util.Date;

public class CapitalFlowQuery extends PageAndSortQuery {

	private Long publisherId;

	private CapitalFlowType[] types;
	private String publisherPhone;
	private Date startTime;
	private Date endTime;
	private String commodityName;


	public CapitalFlowQuery() {
		super();
	}

	public CapitalFlowQuery(int page, int size) {
		super();
		super.setPage(page);
		super.setSize(size);
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
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

	public CapitalFlowType[] getTypes() {
		return types;
	}

	public void setTypes(CapitalFlowType[] types) {
		this.types = types;
	}

	public String getPublisherPhone() {
		return publisherPhone;
	}

	public void setPublisherPhone(String publisherPhone) {
		this.publisherPhone = publisherPhone;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}
}
