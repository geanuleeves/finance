package com.waben.stock.interfaces.pojo.query.futures;

import java.util.Date;

import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;

import io.swagger.annotations.ApiModelProperty;

public class FuturesOrderQuery extends PageAndSortQuery {

	@ApiModelProperty(value = "用户ID")
	private Long publisherId;

	@ApiModelProperty(value = "订单ID")
	private Long orderId;

	@ApiModelProperty(value = "期货订单状态  持仓中： Position 委托中： BuyingEntrust,PartPosition,SellingEntrust,PartUnwind 已结算： Canceled,EntrustFailure,Unwind")
	private FuturesOrderState[] states;

	@ApiModelProperty(value = "是否测试订单")
	private Boolean isTest;

	@ApiModelProperty(value = "合约名称")
	private String contractName;

	@ApiModelProperty(value = "起始日期")
	private Date startBuyingTime;

	@ApiModelProperty(value = "结束时间")
	private Date endBuyingTime;

	@ApiModelProperty(value = "查询收益了的交易")
	private boolean onlyProfit;

	public FuturesOrderQuery() {
		super();
	}

	public FuturesOrderQuery(int page, int size) {
		super();
		super.setPage(page);
		super.setSize(size);
	}

	public FuturesOrderState[] getStates() {
		return states;
	}

	public void setStates(FuturesOrderState[] states) {
		this.states = states;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public Date getStartBuyingTime() {
		return startBuyingTime;
	}

	public void setStartBuyingTime(Date startBuyingTime) {
		this.startBuyingTime = startBuyingTime;
	}

	public Date getEndBuyingTime() {
		return endBuyingTime;
	}

	public void setEndBuyingTime(Date endBuyingTime) {
		this.endBuyingTime = endBuyingTime;
	}

	public boolean isOnlyProfit() {
		return onlyProfit;
	}

	public void setOnlyProfit(boolean onlyProfit) {
		this.onlyProfit = onlyProfit;
	}

}
