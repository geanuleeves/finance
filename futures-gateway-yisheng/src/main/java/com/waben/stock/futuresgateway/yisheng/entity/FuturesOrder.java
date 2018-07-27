package com.waben.stock.futuresgateway.yisheng.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.future.api.es.external.common.constants.Constants;

/**
 * 期货订单
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_es_order")
public class FuturesOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 下单通道类型
	 * <ul>
	 * <li>1易盛</li>
	 * <li>2盈透</li>
	 * <ul>
	 */
	private Integer brokerType;
	/** 所属域 */
	private String domain;
	/** 品种ID */
	private Long commodityId;
	/** 品种编号 */
	private String commodityNo;
	/** 合约ID */
	private Long contractId;
	/** 合约编号 */
	private String contractNo;
	/** 下游订单ID */
	private Long outerOrderId;
	/** 上游订单编号 */
	private String orderNo;
	/** 上游下单会话ID */
	private Integer orderSessionId;
	/**
	 * 订单方向
	 * <ul>
	 * <li>BUY买入</li>
	 * <li>SELL卖出</li>
	 * </ul>
	 */
	private String action;
	/**
	 * 用户订单类型
	 * <ul>
	 * <li>1市价订单</li>
	 * <li>2委托价订单</li>
	 * </ul>
	 */
	private Integer orderType;
	/**
	 * 委托价格
	 */
	private BigDecimal entrustPrice;
	/**
	 * 账户
	 */
	private String account;
	/**
	 * 订单状态(易盛)
	 * 
	 * @see Constants#TAPI_ORDER_STATE_SUBMIT
	 * @see Constants#TAPI_ORDER_STATE_ACCEPT
	 * @see Constants#TAPI_ORDER_STATE_TRIGGERING
	 * @see Constants#TAPI_ORDER_STATE_EXCTRIGGERING
	 * @see Constants#TAPI_ORDER_STATE_QUEUED
	 * @see Constants#TAPI_ORDER_STATE_PARTFINISHED
	 * @see Constants#TAPI_ORDER_STATE_FINISHED
	 * @see Constants#TAPI_ORDER_STATE_CANCELING
	 * @see Constants#TAPI_ORDER_STATE_MODIFYING
	 * @see Constants#TAPI_ORDER_STATE_CANCELED
	 * @see Constants#TAPI_ORDER_STATE_LEFTDELETED
	 * @see Constants#TAPI_ORDER_STATE_FAIL
	 * @see Constants#TAPI_ORDER_STATE_DELETED
	 * @see Constants#TAPI_ORDER_STATE_SUPPENDED
	 * @see Constants#TAPI_ORDER_STATE_DELETEDFOREXPIRE
	 * @see Constants#TAPI_ORDER_STATE_EFFECT
	 * @see Constants#TAPI_ORDER_STATE_APPLY
	 */
	private Integer orderState;
	/**
	 * 订单状态(盈透)
	 */
	private String orderStatus;
	/**
	 * tws订单ID(盈透)
	 */
	private Integer ytTwsOrderId;
	/**
	 * 总量
	 */
	private BigDecimal totalQuantity;
	/**
	 * 已成交量
	 */
	private BigDecimal filled;
	/**
	 * 剩余未成交量
	 */
	private BigDecimal remaining;
	/**
	 * 已成交部分均价
	 */
	private BigDecimal avgFillPrice;
	/**
	 * 最后填报价格
	 */
	private BigDecimal lastFillPrice;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 更新时间
	 */
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public String getCommodityNo() {
		return commodityNo;
	}

	public void setCommodityNo(String commodityNo) {
		this.commodityNo = commodityNo;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Long getOuterOrderId() {
		return outerOrderId;
	}

	public void setOuterOrderId(Long outerOrderId) {
		this.outerOrderId = outerOrderId;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Integer getOrderState() {
		return orderState;
	}

	public void setOrderState(Integer orderState) {
		this.orderState = orderState;
	}

	public BigDecimal getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(BigDecimal totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public BigDecimal getFilled() {
		return filled;
	}

	public void setFilled(BigDecimal filled) {
		this.filled = filled;
	}

	public BigDecimal getRemaining() {
		return remaining;
	}

	public void setRemaining(BigDecimal remaining) {
		this.remaining = remaining;
	}

	public BigDecimal getAvgFillPrice() {
		return avgFillPrice;
	}

	public void setAvgFillPrice(BigDecimal avgFillPrice) {
		this.avgFillPrice = avgFillPrice;
	}

	public BigDecimal getLastFillPrice() {
		return lastFillPrice;
	}

	public void setLastFillPrice(BigDecimal lastFillPrice) {
		this.lastFillPrice = lastFillPrice;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getOrderSessionId() {
		return orderSessionId;
	}

	public void setOrderSessionId(Integer orderSessionId) {
		this.orderSessionId = orderSessionId;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Integer getYtTwsOrderId() {
		return ytTwsOrderId;
	}

	public void setYtTwsOrderId(Integer ytTwsOrderId) {
		this.ytTwsOrderId = ytTwsOrderId;
	}

	public Integer getBrokerType() {
		return brokerType;
	}

	public void setBrokerType(Integer brokerType) {
		this.brokerType = brokerType;
	}

}
