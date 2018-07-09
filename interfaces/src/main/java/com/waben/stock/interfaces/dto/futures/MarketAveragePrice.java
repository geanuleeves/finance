package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;

/**
 * 行情均价
 * 
 * @author luomengan
 *
 */
public class MarketAveragePrice {

	/**
	 * 品种编号
	 */
	private String commodityNo;
	/**
	 * 合约编号
	 */
	private String contractNo;
	/**
	 * 总量（手）
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
	 * 已成交部分总费用
	 */
	private BigDecimal totalFillCost;

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

	public BigDecimal getTotalFillCost() {
		return totalFillCost;
	}

	public void setTotalFillCost(BigDecimal totalFillCost) {
		this.totalFillCost = totalFillCost;
	}

}
