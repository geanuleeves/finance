package com.waben.stock.interfaces.pojo.query.organization;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;

public class FuturesCommissionAuditQuery extends PageAndSortQuery {

	private String commoditySymbol;

	private String commodityName;

	private Long currentOrgId;

	private String types;

	/**
	 * 审核状态
	 * <ul>
	 * <li>1、审核中</li>
	 * <li>2、审核通过</li>
	 * <li>3、审核不通过</li>
	 * </ul>
	 */
	private String states;

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

	public Long getCurrentOrgId() {
		return currentOrgId;
	}

	public void setCurrentOrgId(Long currentOrgId) {
		this.currentOrgId = currentOrgId;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}

}
