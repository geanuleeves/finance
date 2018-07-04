package com.waben.stock.interfaces.pojo.query.admin.futures;

import java.util.List;

import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;

public class FuturesTradeAdminQuery extends PageAndSortQuery {

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
	 * 订单/委托编号
	 */
	private String tradeNo;

	/**
	 * 交易方向
	 */
	private String orderType;

	/**
	 * 订单状态
	 */
	private String orderState;

	private String treeCode;

	private Long orgId;

	/**
	 * 定单类型，1、市价，2、限价
	 */
	private String priceType;

	private List<Long> publisherIds;

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

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderState() {
		return orderState;
	}

	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}

	public String getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(String windControlType) {
		this.windControlType = windControlType;
	}

	public Integer getQueryType() {
		return queryType;
	}

	public void setQueryType(Integer queryType) {
		this.queryType = queryType;
	}

	/**
	 * 风控类型
	 */
	private String windControlType;

	/**
	 * 查询类型
	 * <ul>
	 * <li>0订单列表</li>
	 * <li>1持仓列表</li>
	 * <li>2平仓列表</li>
	 * <li>3委托列表</li>
	 * </ul>
	 */
	private Integer queryType;

	public List<Long> getPublisherIds() {
		return publisherIds;
	}

	public void setPublisherIds(List<Long> publisherIds) {
		this.publisherIds = publisherIds;
	}

	public String getTreeCode() {
		return treeCode;
	}

	public void setTreeCode(String treeCode) {
		this.treeCode = treeCode;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getPriceType() {
		return priceType;
	}

	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}

}