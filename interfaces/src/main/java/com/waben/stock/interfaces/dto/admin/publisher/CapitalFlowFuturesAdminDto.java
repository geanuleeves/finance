package com.waben.stock.interfaces.dto.admin.publisher;

import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;

public class CapitalFlowFuturesAdminDto extends CapitalFlowDto {

	/**
	 * 发布人姓名
	 * <p>
	 * 实名认证的姓名
	 * </p>
	 */
	private String publisherName;
	
	/**
	 * 发布人手机号
	 */
	private String publisherPhone;
	
	/**
	 * 交易代码（期货交易）
	 */
	private String commoditySymbol;
	
	/**
	 * 交易品种（期货交易）
	 */
	private String commodityName;
	
	/**
	 * 合约代码
	 */
	private String contractNo;
	
	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public String getPublisherPhone() {
		return publisherPhone;
	}

	public void setPublisherPhone(String publisherPhone) {
		this.publisherPhone = publisherPhone;
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

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Integer getPaymentType() {
		return paymentType;
	}

	public void setPaymentType(Integer paymentType) {
		this.paymentType = paymentType;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}

	/**
	 * 充值方式
	 */
	private Integer paymentType;
	
	/**
	 * 银行卡号
	 */
	private String bankCard;
	
	/**
	 * 银行名称
	 */
	private String bankName;
	
	/**
	 * 是否测试
	 */
	private Boolean isTest;
}
