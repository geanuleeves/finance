package com.waben.stock.applayer.tactics.dto.publisher;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.WithdrawalsState;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WithdrawalsRecordDto {

	private Long id;
	/**
	 * 提现单号
	 */
	@ApiModelProperty(value = "提现单号")
	private String withdrawalsNo;
	/**
	 * 金额
	 */
	@ApiModelProperty(value = "金额")
	private BigDecimal amount;
	/**
	 * 提现状态
	 */
	@ApiModelProperty(value = "提现状态")
	private WithdrawalsState state;
	/**
	 * 审核状态
	 * <ul>
	 * <li>0待审核</li>
	 * <li>1审核通过</li>
	 * <li>2审核不通过</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "审核状态（0待审核，1审核通过，2审核不通过）")
	private Integer comprehensiveState;
	/**
	 * 备注
	 */
	@ApiModelProperty(value = "备注")
	private String remark;
	/**
	 * 发布人ID
	 */
	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;
	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getWithdrawalsNo() {
		return withdrawalsNo;
	}

	public void setWithdrawalsNo(String withdrawalsNo) {
		this.withdrawalsNo = withdrawalsNo;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public WithdrawalsState getState() {
		return state;
	}
	
	public String getStateStr() {
		if(state != null) {
			return state.getStatus();
		}
		return null;
	}

	public void setState(WithdrawalsState state) {
		this.state = state;
	}

	public Integer getComprehensiveState() {
		return comprehensiveState;
	}

	public void setComprehensiveState(Integer comprehensiveState) {
		this.comprehensiveState = comprehensiveState;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

}
