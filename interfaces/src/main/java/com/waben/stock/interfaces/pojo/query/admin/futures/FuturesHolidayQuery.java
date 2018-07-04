package com.waben.stock.interfaces.pojo.query.admin.futures;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;

import io.swagger.annotations.ApiModelProperty;

public class FuturesHolidayQuery extends PageAndSortQuery {

	private Long id;
	
	@ApiModelProperty(value = "交易代码")
	private String commoditySymbol;
	
	@ApiModelProperty(value = "交易品种")
	private String commodityName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
