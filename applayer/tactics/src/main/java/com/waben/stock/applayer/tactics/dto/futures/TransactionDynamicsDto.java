package com.waben.stock.applayer.tactics.dto.futures;

import java.math.BigDecimal;

import com.waben.stock.interfaces.enums.FuturesOrderType;

import io.swagger.annotations.ApiModelProperty;

/**
 * 交易动态 Dto
 * 
 * @author sl
 *
 */
public class TransactionDynamicsDto {

	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;

	@ApiModelProperty(value = "手机号")
	private String phone;

	@ApiModelProperty(value = "合约ID")
	private Long contractId;

	@ApiModelProperty(value = "合约代码")
	private String contractSymbol;

	@ApiModelProperty(value = "合约名称")
	private String contractName;

	@ApiModelProperty(value = "订单类型,买涨或买跌")
	private FuturesOrderType orderType;

	@ApiModelProperty(value = "发布人盈亏（人民币）")
	private BigDecimal publisherProfitOrLoss;

	@ApiModelProperty(value = "订单类型描述 如 ：买涨1手")
	private String buyOrderTypeDesc;

	@ApiModelProperty(value = "数量（手）")
	private BigDecimal totalQuantity;

//	@ApiModelProperty(value = "期货合约状态,1 交易中;2 休市中;3 异常")
//	private Integer state;

	@ApiModelProperty(value = "合约编号")
	private String contractNo;

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getContractSymbol() {
		return contractSymbol;
	}

	public void setContractSymbol(String contractSymbol) {
		this.contractSymbol = contractSymbol;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getPublisherProfitOrLoss() {
		return publisherProfitOrLoss;
	}

	public void setPublisherProfitOrLoss(BigDecimal publisherProfitOrLoss) {
		this.publisherProfitOrLoss = publisherProfitOrLoss;
	}

	public String getBuyOrderTypeDesc() {
		return buyOrderTypeDesc;
	}

	public void setBuyOrderTypeDesc(String buyOrderTypeDesc) {
		this.buyOrderTypeDesc = buyOrderTypeDesc;
	}

	public BigDecimal getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(BigDecimal totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	/*public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}*/

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

}
