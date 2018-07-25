package com.waben.stock.futuresgateway.yingtou.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货订单
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_order")
public class FuturesOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 所属域
	 */
	private String domain;
	/**
	 * 下游订单ID
	 */
	private Long outerOrderId;
	/**
	 * 本地tws订单ID
	 */
	private Integer twsOrderId;
	/**
	 * 合约ID
	 */
	private Long contractId;
	/**
	 * 合约名称
	 */
	private String symbol;
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
	private Integer userOrderType;
	/**
	 * 委托价格
	 */
	private BigDecimal entrustPrice;
	/**
	 * 本地tws订单类型
	 * 
	 * {@see com.ib.client.OrderType}
	 */
	private String twsOrderType;
	/**
	 * 账户
	 */
	private String account;
	/**
	 * 状态 {@see com.ib.client.OrderStatus}
	 * <ul>
	 * <li>Init表示初始提交的状态</li>
	 * <li>ApiPending正在期货券商提交API</li>
	 * <li>ApiCancelled期货券商提交API取消</li>
	 * <li>PreSubmitted预提交订单</li>
	 * <li>PendingCancel正在取消订单</li>
	 * <li>Cancelled已取消订单</li>
	 * <li>Submitted已提交订单</li>
	 * <li>Filled已成交订单</li>
	 * <li>Inactive不活动的订单</li>
	 * <li>PendingSubmit正在提交订单</li>
	 * </ul>
	 */
	private String status;
	/**
	 * 货币
	 */
	private String currency;
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
	 * 第三方订单ID
	 */
	private Integer permId;
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

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
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

	public Integer getPermId() {
		return permId;
	}

	public void setPermId(Integer permId) {
		this.permId = permId;
	}

	public Integer getTwsOrderId() {
		return twsOrderId;
	}

	public void setTwsOrderId(Integer twsOrderId) {
		this.twsOrderId = twsOrderId;
	}

	public Integer getUserOrderType() {
		return userOrderType;
	}

	public void setUserOrderType(Integer userOrderType) {
		this.userOrderType = userOrderType;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

	public String getTwsOrderType() {
		return twsOrderType;
	}

	public void setTwsOrderType(String twsOrderType) {
		this.twsOrderType = twsOrderType;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
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

	public Long getOuterOrderId() {
		return outerOrderId;
	}

	public void setOuterOrderId(Long outerOrderId) {
		this.outerOrderId = outerOrderId;
	}

}
