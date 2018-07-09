package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;

import io.swagger.annotations.ApiModelProperty;

public class FuturesOrderDto {

	private Long id;
	/**
	 * 对应的发布人Id
	 */
	@ApiModelProperty(value = "对应的发布人Id")
	private Long publisherId;
	/**
	 * 订单号
	 */
	@ApiModelProperty(value = "订单号")
	private String tradeNo;
	/**
	 * 发布时间
	 */
	@ApiModelProperty(value = "发布时间")
	private Date postTime;
	/**
	 * 订单类型
	 */
	@ApiModelProperty(value = "订单类型")
	private FuturesOrderType orderType;
	/**
	 * 对应的合约ID
	 */
	@ApiModelProperty(value = "对应的合约ID")
	private Long contractId;
	/**
	 * 数量（手）
	 */
	@ApiModelProperty(value = "数量（手）")
	private BigDecimal totalQuantity;
	/**
	 * 保证金（人民币）
	 */
	@ApiModelProperty(value = "保证金（人民币）")
	private BigDecimal reserveFund;
	/**
	 * 服务费（服务费）
	 */
	@ApiModelProperty(value = "服务费（服务费）")
	private BigDecimal serviceFee;
	/**
	 * 品种代码（取期货品种设置快照）
	 */
	@ApiModelProperty(value = "品种代码（取期货品种设置快照）")
	private String commoditySymbol;
	/**
	 * 品种名称（取期货品种设置快照）
	 */
	@ApiModelProperty(value = "品种名称（取期货品种设置快照）")
	private String commodityName;
	/**
	 * 合约编号（取期货合约设置快照）
	 */
	@ApiModelProperty(value = "合约编号（取期货合约设置快照）")
	private String contractNo;
	/**
	 * 货币（取期货品种设置快照）
	 */
	@ApiModelProperty(value = "货币（取期货品种设置快照）")
	private String commodityCurrency;
	/**
	 * 开仓手续费（取期货合约设置快照）
	 */
	@ApiModelProperty(value = "开仓手续费（取期货合约设置快照）")
	private BigDecimal openwindServiceFee;
	/**
	 * 平仓手续费（取期货合约设置快照）
	 */
	@ApiModelProperty(value = "平仓手续费（取期货合约设置快照）")
	private BigDecimal unwindServiceFee;
	/**
	 * 一手强平点（取期货合约设置快照）
	 */
	@ApiModelProperty(value = "一手强平点（取期货合约设置快照）")
	private BigDecimal perUnitUnwindPoint;
	/**
	 * 强平点类型（取期货合约设置快照）
	 * <ul>
	 * <li>1比例</li>
	 * <li>2金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "强平点类型（取期货合约设置快照） 1 比例 2金额")
	private Integer unwindPointType;
	/**
	 * 一手隔夜保证金（取期货合约设置快照）
	 */
	@ApiModelProperty(value = "一手隔夜保证金（取期货合约设置快照）")
	private BigDecimal overnightPerUnitReserveFund;
	/**
	 * 一手隔夜递延费（取期货合约设置快照）
	 */
	@ApiModelProperty(value = "一手隔夜递延费（取期货合约设置快照）")
	private BigDecimal overnightPerUnitDeferredFee;
	/**
	 * 触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "触发止盈类型（用户设置）1 价格  2 金额")
	private Integer limitProfitType;
	/**
	 * 一手止盈金额（用户设置）
	 */
	@ApiModelProperty(value = "一手止盈金额（用户设置）")
	private BigDecimal perUnitLimitProfitAmount;
	/**
	 * 一手止盈价格点位
	 */
	@ApiModelProperty(value = "一手止盈价格点位")
	private BigDecimal perUnitLimitProfitPositon;
	/**
	 * 触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "触发止损类型（用户设置） 1 价格 2 金额")
	private Integer limitLossType;
	/**
	 * 一手止损金额（用户设置）
	 */
	@ApiModelProperty(value = "一手止损金额（用户设置）")
	private BigDecimal perUnitLimitLossAmount;
	/**
	 * 一手止损价格点位
	 */
	@ApiModelProperty(value = "一手止损价格点位")
	private BigDecimal perUnitLimitLossPosition;
	/**
	 * 订单状态
	 */
	@ApiModelProperty(value = "订单状态")
	private FuturesOrderState state;
	/**
	 * 订单状态名称
	 */
	@ApiModelProperty(value = "订单状态名称")
	private String stateName;
	/**
	 * 开仓对应的网关交易订单ID
	 */
	@ApiModelProperty(value = "开仓对应的网关交易订单ID")
	private Long openGatewayOrderId;
	/**
	 * 买入价格类型
	 */
	@ApiModelProperty(value = "买入价格类型")
	private FuturesTradePriceType buyingPriceType;
	/**
	 * 买入委托时间
	 */
	@ApiModelProperty(value = "买入委托时间")
	private Date buyingEntrustTime;
	/**
	 * 买入委托价格
	 */
	@ApiModelProperty(value = "买入委托价格")
	private BigDecimal buyingEntrustPrice;
	/**
	 * 买入时间
	 */
	@ApiModelProperty(value = "买入时间")
	private Date buyingTime;
	/**
	 * 买入价格
	 */
	@ApiModelProperty(value = "买入价格")
	private BigDecimal buyingPrice;
	/**
	 * 平仓对应的网关交易订单ID
	 */
	@ApiModelProperty(value = "平仓对应的网关交易订单ID")
	private Long closeGatewayOrderId;
	/**
	 * 卖出价格类型
	 */
	@ApiModelProperty(value = "卖出价格类型")
	private FuturesTradePriceType sellingPriceType;
	/**
	 * 卖出委托时间
	 */
	@ApiModelProperty(value = "卖出委托时间")
	private Date sellingEntrustTime;
	/**
	 * 卖出委托价格
	 */
	@ApiModelProperty(value = "卖出委托价格")
	private BigDecimal sellingEntrustPrice;
	/**
	 * 卖出时间
	 */
	@ApiModelProperty(value = "卖出时间")
	private Date sellingTime;
	/**
	 * 卖出价格
	 */
	@ApiModelProperty(value = "卖出价格")
	private BigDecimal sellingPrice;
	/**
	 * 风控类型
	 */
	@ApiModelProperty(value = "风控类型")
	private FuturesWindControlType windControlType;
	/**
	 * 盈亏（交易所货币）
	 */
	@ApiModelProperty(value = "盈亏（交易所货币）")
	private BigDecimal currencyProfitOrLoss;
	/**
	 * 盈亏（人民币）
	 */
	@ApiModelProperty(value = "盈亏（人民币）")
	private BigDecimal profitOrLoss;
	/**
	 * 发布人盈亏（人民币）
	 */
	@ApiModelProperty(value = "发布人盈亏（人民币）")
	private BigDecimal publisherProfitOrLoss;
	/**
	 * 平台盈亏（人民币）
	 */
	@ApiModelProperty(value = "平台盈亏（人民币）")
	private BigDecimal platformProfitOrLoss;
	/**
	 * 结算时的汇率
	 */
	@ApiModelProperty(value = "结算时的汇率")
	private BigDecimal settlementRate;
	/**
	 * 结算时间
	 */
	@ApiModelProperty(value = "结算时间")
	private Date settlementTime;
	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
	/**
	 * 是否为测试单
	 */
	@ApiModelProperty(value = "是否为测试单")
	private Boolean isTest;
	/**
	 * 交易所名称
	 */
	@ApiModelProperty(value = "交易所名称")
	private String exchangeName;
	/**
	 * 品种ID
	 */
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;
	/**
	 * 期货隔夜记录
	 */
	private List<FuturesOvernightRecordDto> futuresOvernightRecord;

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

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(BigDecimal totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getOpenwindServiceFee() {
		return openwindServiceFee;
	}

	public void setOpenwindServiceFee(BigDecimal openwindServiceFee) {
		this.openwindServiceFee = openwindServiceFee;
	}

	public BigDecimal getUnwindServiceFee() {
		return unwindServiceFee;
	}

	public void setUnwindServiceFee(BigDecimal unwindServiceFee) {
		this.unwindServiceFee = unwindServiceFee;
	}

	public BigDecimal getPerUnitUnwindPoint() {
		return perUnitUnwindPoint;
	}

	public void setPerUnitUnwindPoint(BigDecimal perUnitUnwindPoint) {
		this.perUnitUnwindPoint = perUnitUnwindPoint;
	}

	public Integer getUnwindPointType() {
		return unwindPointType;
	}

	public void setUnwindPointType(Integer unwindPointType) {
		this.unwindPointType = unwindPointType;
	}

	public BigDecimal getOvernightPerUnitReserveFund() {
		return overnightPerUnitReserveFund;
	}

	public void setOvernightPerUnitReserveFund(BigDecimal overnightPerUnitReserveFund) {
		this.overnightPerUnitReserveFund = overnightPerUnitReserveFund;
	}

	public BigDecimal getOvernightPerUnitDeferredFee() {
		return overnightPerUnitDeferredFee;
	}

	public void setOvernightPerUnitDeferredFee(BigDecimal overnightPerUnitDeferredFee) {
		this.overnightPerUnitDeferredFee = overnightPerUnitDeferredFee;
	}

	public BigDecimal getPerUnitLimitProfitAmount() {
		return perUnitLimitProfitAmount != null ? perUnitLimitProfitAmount.stripTrailingZeros() : null;
	}

	public void setPerUnitLimitProfitAmount(BigDecimal perUnitLimitProfitAmount) {
		this.perUnitLimitProfitAmount = perUnitLimitProfitAmount;
	}

	public BigDecimal getPerUnitLimitProfitPositon() {
		return perUnitLimitProfitPositon;
	}

	public void setPerUnitLimitProfitPositon(BigDecimal perUnitLimitProfitPositon) {
		this.perUnitLimitProfitPositon = perUnitLimitProfitPositon;
	}

	public BigDecimal getPerUnitLimitLossAmount() {
		return perUnitLimitLossAmount != null ? perUnitLimitLossAmount.stripTrailingZeros() : null;
	}

	public void setPerUnitLimitLossAmount(BigDecimal perUnitLimitLossAmount) {
		this.perUnitLimitLossAmount = perUnitLimitLossAmount;
	}

	public BigDecimal getPerUnitLimitLossPosition() {
		return perUnitLimitLossPosition;
	}

	public void setPerUnitLimitLossPosition(BigDecimal perUnitLimitLossPosition) {
		this.perUnitLimitLossPosition = perUnitLimitLossPosition;
	}

	public FuturesOrderState getState() {
		return state;
	}

	public void setState(FuturesOrderState state) {
		this.state = state;
	}

	public Long getOpenGatewayOrderId() {
		return openGatewayOrderId;
	}

	public void setOpenGatewayOrderId(Long openGatewayOrderId) {
		this.openGatewayOrderId = openGatewayOrderId;
	}

	public FuturesTradePriceType getBuyingPriceType() {
		return buyingPriceType;
	}

	public void setBuyingPriceType(FuturesTradePriceType buyingPriceType) {
		this.buyingPriceType = buyingPriceType;
	}

	public BigDecimal getBuyingEntrustPrice() {
		return buyingEntrustPrice != null ? buyingEntrustPrice.stripTrailingZeros() : null;
	}

	public void setBuyingEntrustPrice(BigDecimal buyingEntrustPrice) {
		this.buyingEntrustPrice = buyingEntrustPrice;
	}

	public Date getBuyingTime() {
		return buyingTime;
	}

	public void setBuyingTime(Date buyingTime) {
		this.buyingTime = buyingTime;
	}

	public BigDecimal getBuyingPrice() {
		return buyingPrice != null ? buyingPrice.stripTrailingZeros() : null;
	}

	public void setBuyingPrice(BigDecimal buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	public Long getCloseGatewayOrderId() {
		return closeGatewayOrderId;
	}

	public void setCloseGatewayOrderId(Long closeGatewayOrderId) {
		this.closeGatewayOrderId = closeGatewayOrderId;
	}

	public FuturesTradePriceType getSellingPriceType() {
		return sellingPriceType;
	}

	public void setSellingPriceType(FuturesTradePriceType sellingPriceType) {
		this.sellingPriceType = sellingPriceType;
	}

	public BigDecimal getSellingEntrustPrice() {
		return sellingEntrustPrice != null ? sellingEntrustPrice.stripTrailingZeros() : null;
	}

	public void setSellingEntrustPrice(BigDecimal sellingEntrustPrice) {
		this.sellingEntrustPrice = sellingEntrustPrice;
	}

	public Date getSellingTime() {
		return sellingTime;
	}

	public void setSellingTime(Date sellingTime) {
		this.sellingTime = sellingTime;
	}

	public BigDecimal getSellingPrice() {
		return sellingPrice != null ? sellingPrice.stripTrailingZeros() : null;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public FuturesWindControlType getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(FuturesWindControlType windControlType) {
		this.windControlType = windControlType;
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

	public List<FuturesOvernightRecordDto> getFuturesOvernightRecord() {
		return futuresOvernightRecord;
	}

	public void setFuturesOvernightRecord(List<FuturesOvernightRecordDto> futuresOvernightRecord) {
		this.futuresOvernightRecord = futuresOvernightRecord;
	}

	public String getCommoditySymbol() {
		return commoditySymbol;
	}

	public void setCommoditySymbol(String commoditySymbol) {
		this.commoditySymbol = commoditySymbol;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getCommodityCurrency() {
		return commodityCurrency;
	}

	public void setCommodityCurrency(String commodityCurrency) {
		this.commodityCurrency = commodityCurrency;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Integer getLimitProfitType() {
		return limitProfitType;
	}

	public void setLimitProfitType(Integer limitProfitType) {
		this.limitProfitType = limitProfitType;
	}

	public Integer getLimitLossType() {
		return limitLossType;
	}

	public void setLimitLossType(Integer limitLossType) {
		this.limitLossType = limitLossType;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Boolean getIsTest() {
		return isTest;
	}

	public void setIsTest(Boolean isTest) {
		this.isTest = isTest;
	}

	public BigDecimal getSettlementRate() {
		return settlementRate;
	}

	public void setSettlementRate(BigDecimal settlementRate) {
		this.settlementRate = settlementRate;
	}

	public String getExchangeName() {
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getStateName() {
		if (state != null) {
			return state.getType();
		}
		return stateName;
	}

	public Date getBuyingEntrustTime() {
		return buyingEntrustTime;
	}

	public void setBuyingEntrustTime(Date buyingEntrustTime) {
		this.buyingEntrustTime = buyingEntrustTime;
	}

	public Date getSellingEntrustTime() {
		return sellingEntrustTime;
	}

	public void setSellingEntrustTime(Date sellingEntrustTime) {
		this.sellingEntrustTime = sellingEntrustTime;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

}
