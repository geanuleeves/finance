package com.waben.stock.interfaces.dto.organization;

import java.math.BigDecimal;
import java.util.Date;

import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.enums.CapitalFlowType;

public class FuturesFowDto extends CapitalFlowDto{

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

	public String getStockCode() {
		return stockCode;
	}

	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getbStockCode() {
		return bStockCode;
	}

	public void setbStockCode(String bStockCode) {
		this.bStockCode = bStockCode;
	}

	public String getbStockName() {
		return bStockName;
	}

	public void setbStockName(String bStockName) {
		this.bStockName = bStockName;
	}

	public String getsStockCode() {
		return sStockCode;
	}

	public void setsStockCode(String sStockCode) {
		this.sStockCode = sStockCode;
	}

	public String getsStockName() {
		return sStockName;
	}

	public void setsStockName(String sStockName) {
		this.sStockName = sStockName;
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

	public String getAgentCode() {
		return agentCode;
	}

	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}

	public String getAgentCodeName() {
		return agentCodeName;
	}

	public void setAgentCodeName(String agentCodeName) {
		this.agentCodeName = agentCodeName;
	}

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
	 * 股票代码
	 */
	private String stockCode;
	/**
	 * 股票名称
	 */
	private String stockName;
	/**
	 * 股票代码（点买记录）
	 */
	private String bStockCode;
	/**
	 * 股票名称（点买记录）
	 */
	private String bStockName;
	
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
	/**
	 * 股票代码（期权交易）
	 */
	private String sStockCode;
	/**
	 * 股票名称（期权交易）
	 */
	private String sStockName;
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

	/**
	 * 所属代理商代码
	 */
	private String agentCode;

	/**
	 * 所属代理商代码名称
	 */
	private String agentCodeName;

}
