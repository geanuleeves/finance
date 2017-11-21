package com.waben.stock.applayer.tactics.dto.publisher;

import java.math.BigDecimal;

/**
 * 策略发布人和资金账号
 * 
 * @author luomengan
 *
 */
public class PublisherCapitalAccountDto {

	private Long id;
	/**
	 * 序列码
	 */
	private String serialCode;
	/**
	 * 电话
	 */
	private String phone;
	/**
	 * 推广码
	 */
	private String promotionCode;
	/**
	 * 账户余额
	 */
	private BigDecimal balance;
	/**
	 * 令牌
	 */
	private String token;

	public PublisherCapitalAccountDto() {
	}

	public PublisherCapitalAccountDto(PublisherDto publisher, CapitalAccountDto capitalAccount) {
		this.setBalance(capitalAccount.getBalance());
		this.setId(publisher.getId());
		this.setPhone(publisher.getPhone());
		this.setPromotionCode(publisher.getPromotionCode());
		this.setSerialCode(publisher.getSerialCode());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSerialCode() {
		return serialCode;
	}

	public void setSerialCode(String serialCode) {
		this.serialCode = serialCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPromotionCode() {
		return promotionCode;
	}

	public void setPromotionCode(String promotionCode) {
		this.promotionCode = promotionCode;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
