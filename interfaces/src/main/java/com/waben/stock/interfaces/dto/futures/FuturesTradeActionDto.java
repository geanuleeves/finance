package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesWindControlType;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesTradeActionDto {

	private Long id;
	/** 用户ID */
	@ApiModelProperty(value = "用户ID")
	private Long publisherId;
	/** 排队顺序 */
	@ApiModelProperty(value = "排队顺序")
	private Integer sort;
	/** 委托时间 */
	@ApiModelProperty(value = "委托时间")
	private Date entrustTime;
	/** 交易开平仓 类型 */
	@ApiModelProperty(value = "交易开平仓 类型")
	private FuturesTradeActionType tradeActionType;
	/** 风控类型 */
	@ApiModelProperty(value = "风控类型")
	private FuturesWindControlType windControlType;
	/** 委托数量 */
	@ApiModelProperty(value = "委托数量")
	private BigDecimal quantity;
	/** 委托状态 */
	@ApiModelProperty(value = "委托状态")
	private FuturesTradeEntrustState state;
	/** 交易成功时间 */
	@ApiModelProperty(value = "交易成功时间")
	private Date tradeTime;
	/** 成交量 */
	@ApiModelProperty(value = "成交量")
	private BigDecimal filled;
	/** 剩余未成交量 */
	@ApiModelProperty(value = "剩余未成交量")
	private BigDecimal remaining;
	/** 已成交部分均价 */
	@ApiModelProperty(value = "已成交部分均价")
	private BigDecimal avgFillPrice;
	/** 已成交部分总费用 */
	@ApiModelProperty(value = "已成交部分总费用")
	private BigDecimal totalFillCost;
	/** 最终成交价格 */
	@ApiModelProperty(value = "最终成交价格")
	private BigDecimal tradePrice;
	/** 盈亏（交易所货币） */
	@ApiModelProperty(value = "盈亏（交易所货币）")
	private BigDecimal currencyProfitOrLoss;
	/** 盈亏（人民币） */
	@ApiModelProperty(value = "盈亏（人民币）")
	private BigDecimal profitOrLoss;
	/** 发布人盈亏（人民币） */
	@ApiModelProperty(value = "发布人盈亏（人民币）")
	private BigDecimal publisherProfitOrLoss;
	/** 平台盈亏（人民币） */
	@ApiModelProperty(value = "平台盈亏（人民币）")
	private BigDecimal platformProfitOrLoss;
	/** 结算时的汇率 */
	@ApiModelProperty(value = "结算时的汇率")
	private BigDecimal settlementRate;
	/** 结算时间 */
	@ApiModelProperty(value = "结算时间")
	private Date settlementTime;
	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	/***************** 以下字段为额外数据 ********************/
	/** 合约ID */
	@ApiModelProperty(value = "合约ID")
	private Long contractId;
	/** 品种名称 */
	@ApiModelProperty(value = "品种名称")
	private String commodityName;
	/** 品种编号 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/** 合约编号 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/** 开仓成交均价 */
	@ApiModelProperty(value = "开仓成交均价")
	private BigDecimal openAvgFillPrice;
	/** 委托价格 */
	@ApiModelProperty(value = "委托价格")
	private BigDecimal entrustPrice;

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

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Date getEntrustTime() {
		return entrustTime;
	}

	public void setEntrustTime(Date entrustTime) {
		this.entrustTime = entrustTime;
	}

	public FuturesTradeActionType getTradeActionType() {
		return tradeActionType;
	}

	public void setTradeActionType(FuturesTradeActionType tradeActionType) {
		this.tradeActionType = tradeActionType;
	}

	public FuturesWindControlType getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(FuturesWindControlType windControlType) {
		this.windControlType = windControlType;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public FuturesTradeEntrustState getState() {
		return state;
	}

	public void setState(FuturesTradeEntrustState state) {
		this.state = state;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
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

	public BigDecimal getTotalFillCost() {
		return totalFillCost;
	}

	public void setTotalFillCost(BigDecimal totalFillCost) {
		this.totalFillCost = totalFillCost;
	}

	public BigDecimal getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}

	public BigDecimal getCurrencyProfitOrLoss() {
		return currencyProfitOrLoss;
	}

	public void setCurrencyProfitOrLoss(BigDecimal currencyProfitOrLoss) {
		this.currencyProfitOrLoss = currencyProfitOrLoss;
	}

	public BigDecimal getProfitOrLoss() {
		return profitOrLoss;
	}

	public void setProfitOrLoss(BigDecimal profitOrLoss) {
		this.profitOrLoss = profitOrLoss;
	}

	public BigDecimal getPublisherProfitOrLoss() {
		return publisherProfitOrLoss;
	}

	public void setPublisherProfitOrLoss(BigDecimal publisherProfitOrLoss) {
		this.publisherProfitOrLoss = publisherProfitOrLoss;
	}

	public BigDecimal getPlatformProfitOrLoss() {
		return platformProfitOrLoss;
	}

	public void setPlatformProfitOrLoss(BigDecimal platformProfitOrLoss) {
		this.platformProfitOrLoss = platformProfitOrLoss;
	}

	public BigDecimal getSettlementRate() {
		return settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
	}

	public Date getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(Date settlementTime) {
		this.settlementTime = settlementTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
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

	public BigDecimal getOpenAvgFillPrice() {
		return openAvgFillPrice;
	}

	public void setOpenAvgFillPrice(BigDecimal openAvgFillPrice) {
		this.openAvgFillPrice = openAvgFillPrice;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
	}

}
