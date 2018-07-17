package com.waben.stock.interfaces.pojo.query;

import java.util.Date;

public class WithdrawalsOrderQuery extends PageAndSortQuery {

	private Long publisherId;

	private Date startTime;

	private Date endTime;

	public WithdrawalsOrderQuery() {
		super();
	}

	public WithdrawalsOrderQuery(int page, int size) {
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

}
