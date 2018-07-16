package com.waben.stock.interfaces.dto.admin.publisher;

import java.math.BigDecimal;
import java.util.Date;

import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;

public class FuturesComprehensiveFeeDto {

	/**
	 * 申请id
	 */
	private Long id;
	
	/**
	 * 提现金额
	 */
	private BigDecimal amount;
	
	/**
	 * 用户ID
	 */
	private Long publisherId;
	
	/**
	 * 交易账号
	 */
	private String publisherPhone;
	
	/**
	 * 客户姓名
	 */
	private String publisherName;
	
	/**
	 * 申请时间
	 */
	private Date createTime;
	
	/**
	 * 状态
	 */
	private Integer state;
	
	/**
	 * 备注
	 */
	private String remarke;
	
	
	private String webConfigKey;
	
	private String merchantNo;
	
	private String bankName;
	
	private String withdrawalsNo;
	
	
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

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public String getPublisherPhone() {
		return publisherPhone;
	}

	public void setPublisherPhone(String publisherPhone) {
		this.publisherPhone = publisherPhone;
	}

	public String getPublisherName() {
		return publisherName;
	}

	public void setPublisherName(String publisherName) {
		this.publisherName = publisherName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getRemarke() {
		return remarke;
	}

	public void setRemarke(String remarke) {
		this.remarke = remarke;
	}

	public String getWebConfigKey() {
		return webConfigKey;
	}

	public void setWebConfigKey(String webConfigKey) {
		this.webConfigKey = webConfigKey;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getWithdrawalsNo() {
		return withdrawalsNo;
	}

	public void setWithdrawalsNo(String withdrawalsNo) {
		this.withdrawalsNo = withdrawalsNo;
	}
	
	
	
}
