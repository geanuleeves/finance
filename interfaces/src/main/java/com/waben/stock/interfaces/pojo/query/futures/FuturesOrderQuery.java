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
	private Date stateTime;

	@ApiModelProperty(value = "结束时间")
	private Date endTime;

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

	public Date getStateTime() {
		return stateTime;
	}

	public void setStateTime(Date stateTime) {
		this.stateTime = stateTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
