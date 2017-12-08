package com.waben.stock.interfaces.dto.manage;

/**
 * 银行信息
 * 
 * @author luomengan
 *
 */
public class BankInfoDto {

	private Long id;
	/**
	 * 银行代码
	 */
	private String bankCode;
	/**
	 * 银行名称
	 */
	private String bankName;
	/**
	 * 图标链接
	 */
	private String iconLink;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getIconLink() {
		return iconLink;
	}

	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}

}
