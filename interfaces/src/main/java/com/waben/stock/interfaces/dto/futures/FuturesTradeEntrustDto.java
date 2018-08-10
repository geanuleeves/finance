package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesTradeEntrustDto {

	private Long id;
	/** 用户ID */
	@ApiModelProperty(value = "用户ID")
	private Long publisherId;
	/** 委托编号 */
	@ApiModelProperty(value = "委托编号")
	private String entrustNo;
	/** 品种编号 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/** 合约编号 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/** 订单交易类型 */
	@ApiModelProperty(value = "订单交易类型")
	private FuturesOrderType orderType;
	/** 委托时间 */
	@ApiModelProperty(value = "委托时间 ")
	private Date entrustTime;
	/** 价格类型 */
	@ApiModelProperty(value = "价格类型")
	private FuturesTradePriceType priceType;
	/** 委托价格 */
	@ApiModelProperty(value = "委托价格")
	private BigDecimal entrustPrice;
	/** 交易开平仓 类型 */
	@ApiModelProperty(value = "交易开平仓 类型")
	private FuturesTradeActionType tradeActionType;
	/** 风控类型 */
	@ApiModelProperty(value = "风控类型")
	private FuturesWindControlType windControlType;
	/** 委托数量 */
	@ApiModelProperty(value = "委托数量")
	private BigDecimal quantity;
	/** 保证金（人民币） */
	@ApiModelProperty(value = "保证金（人民币）")
	private BigDecimal reserveFund;
	/** 退回保证金（人民币） */
	@ApiModelProperty(value = "退回保证金（人民币）")
	private BigDecimal returnReserveFund;
	/** 委托状态 */
	@ApiModelProperty(value = "委托状态")
	private FuturesTradeEntrustState state;
	/** 已成交量 */
	@ApiModelProperty(value = "已成交量")
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
	/** 交易成功时间 */
	@ApiModelProperty(value = "交易成功时间")
	private Date tradeTime;
	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	/***************** 以下字段为额外数据 ********************/
	/** 合约ID */
	@ApiModelProperty(value = "合约id")
	private Long contractId;
	/** 品种名称 */
	@ApiModelProperty(value = "品种名称 ")
	private String commodityName;
	/** 当前价 */
	@ApiModelProperty(value = "当前价")
	private BigDecimal lastPrice;
	/** 最小波动 */
	@ApiModelProperty(value = "最小波动")
	private BigDecimal minWave;
	/** 最小浮动价格 */
	@ApiModelProperty(value = "最小浮动价格")
	private BigDecimal perWaveMoney;
	/** 汇率 */
	@ApiModelProperty(value = "汇率")
	private BigDecimal rate;
	/** 货币标识 */
	@ApiModelProperty(value = "货币标识")
	private String currencySign;
	/** 货币缩写 */
	@ApiModelProperty(value = "货币缩写")
	private String currency;
	/** 服务费 */
	@ApiModelProperty(value = "服务费")
	private BigDecimal serviceFee;
	@ApiModelProperty(value = "发布人盈亏（人民币）")
	private BigDecimal publisherProfitOrLoss;
	/** 结算时的汇率 */
	@ApiModelProperty(value = "结算时的汇率")
	private BigDecimal settlementRate;
	/** 结算时间 */
	@ApiModelProperty(value = "结算时间")
	private Date settlementTime;
	/** 开仓成交均价 */
	@ApiModelProperty(value = "开仓成交均价")
	private BigDecimal openAvgFillPrice;

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

	public String getEntrustNo() {
		return entrustNo;
	}

	public void setEntrustNo(String entrustNo) {
		this.entrustNo = entrustNo;
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

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public Date getEntrustTime() {
		return entrustTime;
	}

	public void setEntrustTime(Date entrustTime) {
		this.entrustTime = entrustTime;
	}

	public FuturesTradePriceType getPriceType() {
		return priceType;
	}

	public void setPriceType(FuturesTradePriceType priceType) {
		this.priceType = priceType;
	}

	public BigDecimal getEntrustPrice() {
		return entrustPrice != null ? new BigDecimal(entrustPrice.stripTrailingZeros().toPlainString()) : null;
	}

	public void setEntrustPrice(BigDecimal entrustPrice) {
		this.entrustPrice = entrustPrice;
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

	public BigDecimal getReserveFund() {
		return reserveFund != null ? new BigDecimal(reserveFund.stripTrailingZeros().toPlainString()) : null;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public BigDecimal getReturnReserveFund() {
		return returnReserveFund != null ? new BigDecimal(returnReserveFund.stripTrailingZeros().toPlainString()) : null;
	}

	public void setReturnReserveFund(BigDecimal returnReserveFund) {
		this.returnReserveFund = returnReserveFund;
	}

	public FuturesTradeEntrustState getState() {
		return state;
	}

	public void setState(FuturesTradeEntrustState state) {
		this.state = state;
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
		return avgFillPrice != null ? new BigDecimal(avgFillPrice.stripTrailingZeros().toPlainString()) : null;
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
		return tradePrice != null ? new BigDecimal(tradePrice.stripTrailingZeros().toPlainString()) : null;
	}

	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
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

	public BigDecimal getLastPrice() {
		return lastPrice != null ? new BigDecimal(lastPrice.stripTrailingZeros().toPlainString()) : null;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getMinWave() {
		return minWave != null ? new BigDecimal(minWave.stripTrailingZeros().toPlainString()) : null;
	}

	public void setMinWave(BigDecimal minWave) {
		this.minWave = minWave;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCurrencySign() {
		return currencySign;
	}

	public void setCurrencySign(String currencySign) {
		this.currencySign = currencySign;
	}

	public BigDecimal getPerWaveMoney() {
		return perWaveMoney != null ? new BigDecimal(perWaveMoney.stripTrailingZeros().toPlainString()) : null;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public BigDecimal getServiceFee() {
		return serviceFee != null ? new BigDecimal(serviceFee.stripTrailingZeros().toPlainString()) : null;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getPublisherProfitOrLoss() {
		return publisherProfitOrLoss != null ? new BigDecimal(publisherProfitOrLoss.stripTrailingZeros().toPlainString()) : null;
	}

	public void setPublisherProfitOrLoss(BigDecimal publisherProfitOrLoss) {
		this.publisherProfitOrLoss = publisherProfitOrLoss;
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

	public BigDecimal getOpenAvgFillPrice() {
		return openAvgFillPrice != null ? new BigDecimal(openAvgFillPrice.stripTrailingZeros().toPlainString()) : null;
	}

	public void setOpenAvgFillPrice(BigDecimal openAvgFillPrice) {
		this.openAvgFillPrice = openAvgFillPrice;
	}
}
