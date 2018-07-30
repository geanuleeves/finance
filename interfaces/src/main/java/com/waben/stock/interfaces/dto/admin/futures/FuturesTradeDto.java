package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;
import java.util.Date;

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
	
	/** 交易开平仓 类型 */
	private String tradeActionType;
	
	/** 成交量 */
	private BigDecimal filled;
	
	/** 已成交部分均价 */
	private BigDecimal avgFillPrice;
	
	/** 盈亏（人民币） */
	private BigDecimal profitOrLoss;
	
	/** 交易成功时间 */
	private Date tradeTime;
	
	/** 风控类型 */
	private String windControlType;
	
	/** 价格类型 */
	private String priceType;
	
	/** 是否为测试单 */
	private Boolean isTest;
	
	
}
