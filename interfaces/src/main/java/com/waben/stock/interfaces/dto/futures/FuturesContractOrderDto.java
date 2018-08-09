package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesContractOrderDto {

	private Long id;
	/** 发布人ID */
	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;
	/** 品种编号 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/** 品种名称 */
	@ApiModelProperty(value = "品种名称")
	private String commodityName;
	/** 合约编号 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/** 买涨手数（总，包括买入委托中的） */
	@ApiModelProperty(value = "买涨手数（总，包括买入委托中的）")
	private BigDecimal buyUpTotalQuantity;
	/** 买涨手数（成功） */
	@ApiModelProperty(value = "买涨手数（成功）")
	private BigDecimal buyUpQuantity;
	/** 买涨可平仓手数 */
	private BigDecimal buyUpCanUnwindQuantity;
	/** 买跌手数（总，包括买入委托中的） */
	@ApiModelProperty(value = "买跌手数（总，包括买入委托中的）")
	private BigDecimal buyFallTotalQuantity;
	/** 买跌手数（成功） */
	@ApiModelProperty(value = "买跌手数（成功）")
	private BigDecimal buyFallQuantity;
	/** 买涨可平仓手数 */
	private BigDecimal buyFallCanUnwindQuantity;
	/** 净手数 */
	@ApiModelProperty(value = "净手数")
	private BigDecimal lightQuantity;
	/** 保证金 */
	@ApiModelProperty(value = "保证金")
	private BigDecimal reserveFund;
	/**
	 * 触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "触发止盈类型（用户设置）")
	private Integer limitProfitType;
	/** 止盈金额（用户设置） */
	@ApiModelProperty(value = "止盈金额（用户设置）")
	private BigDecimal perUnitLimitProfitAmount;
	/**
	 * 触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "触发止损类型（用户设置）")
	private Integer limitLossType;
	/** 止损金额（用户设置） */
	@ApiModelProperty(value = "止损金额（用户设置）")
	private BigDecimal perUnitLimitLossAmount;
	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
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

	public Integer getLimitProfitType() {
		return limitProfitType;
	}

	public void setLimitProfitType(Integer limitProfitType) {
		this.limitProfitType = limitProfitType;
	}

	public BigDecimal getPerUnitLimitProfitAmount() {
		return perUnitLimitProfitAmount;
	}

	public void setPerUnitLimitProfitAmount(BigDecimal perUnitLimitProfitAmount) {
		this.perUnitLimitProfitAmount = perUnitLimitProfitAmount;
	}

	public Integer getLimitLossType() {
		return limitLossType;
	}

	public void setLimitLossType(Integer limitLossType) {
		this.limitLossType = limitLossType;
	}

	public BigDecimal getPerUnitLimitLossAmount() {
		return perUnitLimitLossAmount;
	}

	public void setPerUnitLimitLossAmount(BigDecimal perUnitLimitLossAmount) {
		this.perUnitLimitLossAmount = perUnitLimitLossAmount;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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

	public BigDecimal getBuyUpCanUnwindQuantity() {
		return buyUpCanUnwindQuantity;
	}

	public void setBuyUpCanUnwindQuantity(BigDecimal buyUpCanUnwindQuantity) {
		this.buyUpCanUnwindQuantity = buyUpCanUnwindQuantity;
	}

	public BigDecimal getBuyFallCanUnwindQuantity() {
		return buyFallCanUnwindQuantity;
	}

	public void setBuyFallCanUnwindQuantity(BigDecimal buyFallCanUnwindQuantity) {
		this.buyFallCanUnwindQuantity = buyFallCanUnwindQuantity;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

}
