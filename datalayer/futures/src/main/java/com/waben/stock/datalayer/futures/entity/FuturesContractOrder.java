package com.waben.stock.datalayer.futures.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 合约订单
 * 
 * <p>
 * 一个用户一个合约有且仅有一条记录
 * </p>
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_futures_contract_order")
public class FuturesContractOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/** 发布人ID */
	private Long publisherId;
	/** 对应的合约 */
	@ManyToOne
	@JoinColumn(name = "contract_id")
	private FuturesContract contract;
	/** 品种编号 */
	private String commodityNo;
	/** 合约编号 */
	private String contractNo;
	/** 买涨手数（总，包括买入委托中的） */
	private BigDecimal buyUpTotalQuantity;
	/** 买涨手数（成功） */
	private BigDecimal buyUpQuantity;
	/** 买跌手数（总，包括买入委托中的） */
	private BigDecimal buyFallTotalQuantity;
	/** 买跌手数（成功） */
	private BigDecimal buyFallQuantity;
	/** 净手数 */
	private BigDecimal lightQuantity;
	/** 保证金 */
	private BigDecimal reserveFund;

	/**
	 * 买-触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer buyUpLimitProfitType;
	/** 买-止盈金额（用户设置） */
	private BigDecimal buyUpPerUnitLimitProfitAmount;
	/**
	 * 买-触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer buyUpLimitLossType;
	/** 买-止损金额（用户设置） */
	private BigDecimal buyUpPerUnitLimitLossAmount;

	/**
	 * 卖-触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer buyFallLimitProfitType;
	/** 卖-止盈金额（用户设置） */
	private BigDecimal buyFallLerUnitLimitProfitAmount;
	/**
	 * 卖-触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer buyFallLimitLossType;
	/** 卖-止损金额（用户设置） */
	private BigDecimal buyFallPerUnitLimitLossAmount;
	/** 更新时间 */
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public FuturesContract getContract() {
		return contract;
	}

	public void setContract(FuturesContract contract) {
		this.contract = contract;
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

	public BigDecimal getBuyUpQuantity() {
		return buyUpQuantity;
	}

	public void setBuyUpQuantity(BigDecimal buyUpQuantity) {
		this.buyUpQuantity = buyUpQuantity;
	}

	public BigDecimal getBuyFallQuantity() {
		return buyFallQuantity;
	}

	public void setBuyFallQuantity(BigDecimal buyFallQuantity) {
		this.buyFallQuantity = buyFallQuantity;
	}

	public BigDecimal getLightQuantity() {
		return lightQuantity;
	}

	public void setLightQuantity(BigDecimal lightQuantity) {
		this.lightQuantity = lightQuantity;
	}

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getBuyUpLimitProfitType() {
		return buyUpLimitProfitType;
	}

	public void setBuyUpLimitProfitType(Integer buyUpLimitProfitType) {
		this.buyUpLimitProfitType = buyUpLimitProfitType;
	}

	public BigDecimal getBuyUpPerUnitLimitProfitAmount() {
		return buyUpPerUnitLimitProfitAmount;
	}

	public void setBuyUpPerUnitLimitProfitAmount(BigDecimal buyUpPerUnitLimitProfitAmount) {
		this.buyUpPerUnitLimitProfitAmount = buyUpPerUnitLimitProfitAmount;
	}

	public Integer getBuyUpLimitLossType() {
		return buyUpLimitLossType;
	}

	public void setBuyUpLimitLossType(Integer buyUpLimitLossType) {
		this.buyUpLimitLossType = buyUpLimitLossType;
	}

	public BigDecimal getBuyUpPerUnitLimitLossAmount() {
		return buyUpPerUnitLimitLossAmount;
	}

	public void setBuyUpPerUnitLimitLossAmount(BigDecimal buyUpPerUnitLimitLossAmount) {
		this.buyUpPerUnitLimitLossAmount = buyUpPerUnitLimitLossAmount;
	}

	public Integer getBuyFallLimitProfitType() {
		return buyFallLimitProfitType;
	}

	public void setBuyFallLimitProfitType(Integer buyFallLimitProfitType) {
		this.buyFallLimitProfitType = buyFallLimitProfitType;
	}

	public BigDecimal getBuyFallLerUnitLimitProfitAmount() {
		return buyFallLerUnitLimitProfitAmount;
	}

	public void setBuyFallLerUnitLimitProfitAmount(BigDecimal buyFallLerUnitLimitProfitAmount) {
		this.buyFallLerUnitLimitProfitAmount = buyFallLerUnitLimitProfitAmount;
	}

	public Integer getBuyFallLimitLossType() {
		return buyFallLimitLossType;
	}

	public void setBuyFallLimitLossType(Integer buyFallLimitLossType) {
		this.buyFallLimitLossType = buyFallLimitLossType;
	}

	public BigDecimal getBuyFallPerUnitLimitLossAmount() {
		return buyFallPerUnitLimitLossAmount;
	}

	public void setBuyFallPerUnitLimitLossAmount(BigDecimal buyFallPerUnitLimitLossAmount) {
		this.buyFallPerUnitLimitLossAmount = buyFallPerUnitLimitLossAmount;
	}

	public BigDecimal getBuyUpTotalQuantity() {
		return buyUpTotalQuantity;
	}

	public void setBuyUpTotalQuantity(BigDecimal buyUpTotalQuantity) {
		this.buyUpTotalQuantity = buyUpTotalQuantity;
	}

	public BigDecimal getBuyFallTotalQuantity() {
		return buyFallTotalQuantity;
	}

	public void setBuyFallTotalQuantity(BigDecimal buyFallTotalQuantity) {
		this.buyFallTotalQuantity = buyFallTotalQuantity;
	}

}
