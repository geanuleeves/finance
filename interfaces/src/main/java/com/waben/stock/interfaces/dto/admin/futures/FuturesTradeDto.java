package com.waben.stock.interfaces.dto.admin.futures;

public class FuturesTradeDto {

	/**
	 * 交易ID
	 */
	private Long id;

	private Long publisherId;
	
	
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
	 * 合约代码
	 */
	private String symbol;

	/**
	 * 合约名称
	 */
	private String name;
	
	/**
	 * 合约ID
	 */
	private Long contractId;

	/**
	 * 合约编号
	 */
	private String contractNo;
	
	/**
	 * 交易方向
	 */
	private String orderType;
}
