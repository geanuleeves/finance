package com.waben.stock.interfaces.dto.organization;

import java.math.BigDecimal;

import io.swagger.annotations.ApiModelProperty;

/**
 * 期货代理价格
 * 
 * @author sl
 *
 */
public class FuturesAgentPriceDto {

	@ApiModelProperty(value = "代理价格ID")
	private Long id;
	/**
	 * 品种ID
	 */
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;
	/**
	 * 代理商ID
	 */
	@ApiModelProperty(value = "代理商ID")
	private Long orgId;
	/**
	 * 保证金（成本价格，一手）
	 */
	@ApiModelProperty(value = "保证金（成本价格，一手）")
	private BigDecimal costReserveFund;
	/**
	 * 开仓手续费（成本价格，一手）
	 */
	@ApiModelProperty(value = "开仓手续费（成本价格，一手）")
	private BigDecimal costOpenwindServiceFee;
	/**
	 * 平仓手续费（成本价格，一手）
	 */
	@ApiModelProperty(value = "平仓手续费（成本价格，一手）")
	private BigDecimal costUnwindServiceFee;
	/**
	 * 递延费（成本价格，一手）
	 */
	@ApiModelProperty(value = "递延费（成本价格，一手）")
	private BigDecimal costDeferredFee;
	/**
	 * 开仓手续费（销售价格，一手）
	 */
	@ApiModelProperty(value = "开仓手续费（销售价格，一手）")
	private BigDecimal saleOpenwindServiceFee;
	/**
	 * 平仓手续费（销售价格，一手）
	 */
	@ApiModelProperty(value = "平仓手续费（销售价格，一手）")
	private BigDecimal saleUnwindServiceFee;
	/**
	 * 递延费（销售价格，一手）
	 */
	@ApiModelProperty(value = "递延费（销售价格，一手）")
	private BigDecimal saleDeferredFee;
	/**
	 * 代理商名称
	 */
	@ApiModelProperty(value = "品种名称")
	private String commodityName;
	/**
	 * 代理商代码
	 */
	@ApiModelProperty(value = "品种代码")
	private String commoditySymbol;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public BigDecimal getCostReserveFund() {
		return costReserveFund;
	}

	public void setCostReserveFund(BigDecimal costReserveFund) {
		this.costReserveFund = costReserveFund;
	}

	public BigDecimal getCostOpenwindServiceFee() {
		return costOpenwindServiceFee;
	}

	public void setCostOpenwindServiceFee(BigDecimal costOpenwindServiceFee) {
		this.costOpenwindServiceFee = costOpenwindServiceFee;
	}

	public BigDecimal getCostUnwindServiceFee() {
		return costUnwindServiceFee;
	}

	public void setCostUnwindServiceFee(BigDecimal costUnwindServiceFee) {
		this.costUnwindServiceFee = costUnwindServiceFee;
	}

	public BigDecimal getCostDeferredFee() {
		return costDeferredFee;
	}

	public void setCostDeferredFee(BigDecimal costDeferredFee) {
		this.costDeferredFee = costDeferredFee;
	}

	public BigDecimal getSaleOpenwindServiceFee() {
		return saleOpenwindServiceFee;
	}

	public void setSaleOpenwindServiceFee(BigDecimal saleOpenwindServiceFee) {
		this.saleOpenwindServiceFee = saleOpenwindServiceFee;
	}

	public BigDecimal getSaleUnwindServiceFee() {
		return saleUnwindServiceFee;
	}

	public void setSaleUnwindServiceFee(BigDecimal saleUnwindServiceFee) {
		this.saleUnwindServiceFee = saleUnwindServiceFee;
	}

	public BigDecimal getSaleDeferredFee() {
		return saleDeferredFee;
	}

	public void setSaleDeferredFee(BigDecimal saleDeferredFee) {
		this.saleDeferredFee = saleDeferredFee;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getCommoditySymbol() {
		return commoditySymbol;
	}

	public void setCommoditySymbol(String commoditySymbol) {
		this.commoditySymbol = commoditySymbol;
	}

}
