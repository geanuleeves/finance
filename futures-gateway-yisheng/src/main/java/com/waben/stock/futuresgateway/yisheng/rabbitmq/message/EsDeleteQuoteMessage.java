package com.waben.stock.futuresgateway.yisheng.rabbitmq.message;

public class EsDeleteQuoteMessage {

	/** 品种编号 */
	private String commodityNo;
	/** 合约编号 */
	private String contractNo;
	/** 行情ID */
	private String quoteId;
	/**
	 * 类型
	 * <ul>
	 * <li>1表示删除FuturesContractQuote</li>
	 * <li>2表示删除FuturesQuoteMinuteK</li>
	 * </ul>
	 */
	private Integer type;

	public String getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(String quoteId) {
		this.quoteId = quoteId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getCommodityNo() {
		return commodityNo;
	}

	public void setCommodityNo(String commodityNo) {
		this.commodityNo = commodityNo;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

}
