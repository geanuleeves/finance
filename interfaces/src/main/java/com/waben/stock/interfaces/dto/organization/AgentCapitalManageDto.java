package com.waben.stock.interfaces.dto.organization;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

/**
 * 代理商资金管理Dto
 * 
 * @author sl
 *
 */
public class AgentCapitalManageDto {

	@ApiModelProperty(value = "佣金编号")
	private Long id;

	@ApiModelProperty(value = "期货交易编号")
	private String oTradeNo;

	@ApiModelProperty(value = "金额")
	private BigDecimal amount;

	@ApiModelProperty(value = "流水号")
	private String flowNo;

	@ApiModelProperty(value = "流水时间")
	private Date occurrenceTime;

	@ApiModelProperty(value = "账户可用余额")
	private BigDecimal availableBalance;

	@ApiModelProperty(value = "备注")
	private String remark;

	@ApiModelProperty(value = "客户姓名")
	private String oPublisherName;

	@ApiModelProperty(value = "客户账号")
	private String oPublisherPhone;

	@ApiModelProperty(value = "合约代码")
	private String commoditySymbol;

	@ApiModelProperty(value = "产品名称")
	private String commodityName;

	@ApiModelProperty(value = "佣金及流水类型")
	private Integer type;

	@ApiModelProperty(value = "交易 佣金")
	private BigDecimal commission;

	@ApiModelProperty(value = "返佣金额")
	private BigDecimal amountRemaid;

	@ApiModelProperty(value = "所属代理商代码")
	private String orgCode;

	@ApiModelProperty(value = "所属代理商名称")
	private String orgName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFlowNo() {
		return flowNo;
	}

	public void setFlowNo(String flowNo) {
		this.flowNo = flowNo;
	}

	public Date getOccurrenceTime() {
		return occurrenceTime;
	}

	public void setOccurrenceTime(Date occurrenceTime) {
		this.occurrenceTime = occurrenceTime;
	}

	public BigDecimal getAvailableBalance() {
		return availableBalance;
	}

	public void setAvailableBalance(BigDecimal availableBalance) {
		this.availableBalance = availableBalance;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getoPublisherName() {
		return oPublisherName;
	}

	public void setoPublisherName(String oPublisherName) {
		this.oPublisherName = oPublisherName;
	}

	public String getoPublisherPhone() {
		return oPublisherPhone;
	}

	public void setoPublisherPhone(String oPublisherPhone) {
		this.oPublisherPhone = oPublisherPhone;
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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getAmountRemaid() {
		return amountRemaid;
	}

	public void setAmountRemaid(BigDecimal amountRemaid) {
		this.amountRemaid = amountRemaid;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getoTradeNo() {
		return oTradeNo;
	}

	public void setoTradeNo(String oTradeNo) {
		this.oTradeNo = oTradeNo;
	}

}
