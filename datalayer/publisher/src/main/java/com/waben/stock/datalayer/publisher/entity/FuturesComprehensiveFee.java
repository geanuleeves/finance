package com.waben.stock.datalayer.publisher.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * 提现审核表
 * @author pzl
 *
 */
@Entity
@Table(name = "f_futures_comprehensive_fee")
public class FuturesComprehensiveFee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "publisherId")
	private Publisher publisher;
	
	/**
	 * 提现金额
	 */
	@Column(name = "amount")
	private BigDecimal amount;
	
	
	/**
	 * 申请时间
	 */
	@Column(name = "create_time", nullable = false)
	private Date createTime;
	
	/**
	 * 备注
	 */
	@Column(name = "remarke")
	private String remarke;
	
	/**
	 * 状态
	 */
	@Column(name = "state")
	private Integer state;
	
	@Column(name = "web_config_key")
	private String webConfigKey;
	
	@Column(name = "merchant_no")
	private String merchantNo;
	
	@Column(name = "back_name")
	private String bankName;
	
	@Column(name = "withdrawals_no")
	private String withdrawalsNo;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Publisher getPublisher() {
		return publisher;
	}

	public void setPublisher(Publisher publisher) {
		this.publisher = publisher;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getRemarke() {
		return remarke;
	}

	public void setRemarke(String remarke) {
		this.remarke = remarke;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getWebConfigKey() {
		return webConfigKey;
	}

	public void setWebConfigKey(String webConfigKey) {
		this.webConfigKey = webConfigKey;
	}

	public String getMerchantNo() {
		return merchantNo;
	}

	public void setMerchantNo(String merchantNo) {
		this.merchantNo = merchantNo;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getWithdrawalsNo() {
		return withdrawalsNo;
	}

	public void setWithdrawalsNo(String withdrawalsNo) {
		this.withdrawalsNo = withdrawalsNo;
	}
	
	
}
