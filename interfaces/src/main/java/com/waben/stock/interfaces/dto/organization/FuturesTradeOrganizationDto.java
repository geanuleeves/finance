package com.waben.stock.interfaces.dto.organization;

import java.math.BigDecimal;
import java.util.Date;

import com.waben.stock.interfaces.enums.FuturesTradePriceType;

import io.swagger.annotations.ApiModelProperty;

public class FuturesTradeOrganizationDto {

	/**
	 * 交易ID
	 */
	private Long id;
	/**
	 * 用户Id
	 */
	private Long publisherId;
	/**
	 * 合约ID
	 */
	private Long contractId;
	/**
	 * 合约编号
	 */
	private String contractNo;
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
	 * 订单编号
	 */
	private String tradeNo;
	/**
	 * 开仓对应的交易编号
	 */
	private Long openGatewayOrderId;
	/**
	 * 开仓对应的交易编号
	 */
	private Long closeGatewayOrderId;
	/**
	 * 交易方向
	 */
	private String orderType;
	/**
	 * 交易状态
	 */
	private String state;
	/**
	 * 合约代码（取期货合约设置快照）
	 */
	private String commoditySymbol;
	/**
	 * 合约名称（取期货合约设置快照）
	 */
	private String commodityName;
	/**
	 * 货币（取期货合约设置快照）
	 */
	private String commodityCurrency;
	/**
	 * 数量（手）
	 */
	private BigDecimal totalQuantity;
	/**
	 * 买入时间
	 */
	private Date buyingTime;
	/**
	 * 买入价格
	 */
	private BigDecimal buyingPrice;
	/**
	 * 浮动盈亏
	 */
	private BigDecimal profit;
	/**
	 * 开仓手续费
	 */
	private BigDecimal openwindServiceFee;
	/**
	 * 保证金（人民币）
	 */
	private BigDecimal reserveFund;
	/**
	 * 服务费（人民币）
	 */
	private BigDecimal serviceFee;
	/**
	 * 隔夜手续费
	 */
	private BigDecimal overnightServiceFee;
	/**
	 * 隔夜保证金
	 */
	private BigDecimal overnightReserveFund;
	/**
	 * 触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer limitProfitType;
	/**
	 * 止盈金额
	 */
	private BigDecimal perUnitLimitProfitAmount;
	/**
	 * 触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	private Integer limitLossType;
	/**
	 * 止损金额
	 */
	private BigDecimal perUnitLimitLossAmount;
	/**
	 * 持仓天数
	 */
	private String positionDays;
	/**
	 * 持仓截止日期
	 */
	private Date positionEndTime;
	/**
	 * 平仓时间
	 */
	private Date sellingTime;
	/**
	 * 平仓价格
	 */
	private BigDecimal sellingPrice;
	/**
	 * 平仓盈亏
	 */
	private BigDecimal sellingProfit;
	/**
	 * 平仓手续费
	 */
	private BigDecimal unwindServiceFee;

	private String windControlState;
	/**
	 * 风控状态
	 */
	private String windControlType;
	/**
	 * 定单类型
	 */
	private FuturesTradePriceType buyingPriceType;
	/**
	 * 当前价
	 */
	private BigDecimal lastPrice;
	/**
	 * 浮动盈亏
	 */
	@ApiModelProperty(value = "浮动盈亏")
	private BigDecimal floatingProfitOrLoss;
	/**
	 * 一手隔夜保证金
	 */
	private BigDecimal overnightPerUnitReserveFund;
	/**
	 * 一手隔夜递延费
	 */
	private BigDecimal overnightPerUnitDeferredFee;
	/**
	 * 买入委托时间
	 */
	private Date buyingEntrustTime;
	/**
	 * 卖出委托时间
	 */
	private Date sellingEntrustTime;
	/**
	 * 盈亏（人民币）
	 */
	private BigDecimal profitOrLoss;
	/**
	 * 发布人盈亏（人民币）
	 */
	private BigDecimal publisherProfitOrLoss;
	/**
	 * 所属代理商名称
	 */
	private String orgName;

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

	public Long getOpenGatewayOrderId() {
		return openGatewayOrderId;
	}

	public void setOpenGatewayOrderId(Long openGatewayOrderId) {
		this.openGatewayOrderId = openGatewayOrderId;
	}

	public Long getCloseGatewayOrderId() {
		return closeGatewayOrderId;
	}

	public void setCloseGatewayOrderId(Long closeGatewayOrderId) {
		this.closeGatewayOrderId = closeGatewayOrderId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public BigDecimal getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(BigDecimal totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public Date getBuyingTime() {
		return buyingTime;
	}

	public void setBuyingTime(Date buyingTime) {
		this.buyingTime = buyingTime;
	}

	public BigDecimal getBuyingPrice() {
		return buyingPrice;
	}

	public void setBuyingPrice(BigDecimal buyingPrice) {
		this.buyingPrice = buyingPrice;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public BigDecimal getOpenwindServiceFee() {
		return openwindServiceFee;
	}

	public void setOpenwindServiceFee(BigDecimal openwindServiceFee) {
		this.openwindServiceFee = openwindServiceFee;
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

	public BigDecimal getOvernightServiceFee() {
		return overnightServiceFee;
	}

	public void setOvernightServiceFee(BigDecimal overnightServiceFee) {
		this.overnightServiceFee = overnightServiceFee;
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

	public Date getPositionEndTime() {
		return positionEndTime;
	}

	public void setPositionEndTime(Date positionEndTime) {
		this.positionEndTime = positionEndTime;
	}

	public Date getSellingTime() {
		return sellingTime;
	}

	public void setSellingTime(Date sellingTime) {
		this.sellingTime = sellingTime;
	}

	public BigDecimal getSellingPrice() {
		return sellingPrice;
	}

	public void setSellingPrice(BigDecimal sellingPrice) {
		this.sellingPrice = sellingPrice;
	}

	public BigDecimal getSellingProfit() {
		return sellingProfit;
	}

	public void setSellingProfit(BigDecimal sellingProfit) {
		this.sellingProfit = sellingProfit;
	}

	public BigDecimal getUnwindServiceFee() {
		return unwindServiceFee;
	}

	public void setUnwindServiceFee(BigDecimal unwindServiceFee) {
		this.unwindServiceFee = unwindServiceFee;
	}

	public String getWindControlState() {
		return windControlState;
	}

	public void setWindControlState(String windControlState) {
		this.windControlState = windControlState;
	}

	public String getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(String windControlType) {
		this.windControlType = windControlType;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
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

	public String getCommodityCurrency() {
		return commodityCurrency;
	}

	public void setCommodityCurrency(String commodityCurrency) {
		this.commodityCurrency = commodityCurrency;
	}

	public BigDecimal getOvernightReserveFund() {
		return overnightReserveFund;
	}

	public void setOvernightReserveFund(BigDecimal overnightReserveFund) {
		this.overnightReserveFund = overnightReserveFund;
	}

	public String getPositionDays() {
		return positionDays;
	}

	public void setPositionDays(String positionDays) {
		this.positionDays = positionDays;
	}

	public FuturesTradePriceType getBuyingPriceType() {
		return buyingPriceType;
	}

	public void setBuyingPriceType(FuturesTradePriceType buyingPriceType) {
		this.buyingPriceType = buyingPriceType;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getFloatingProfitOrLoss() {
		return floatingProfitOrLoss;
	}

	public void setFloatingProfitOrLoss(BigDecimal floatingProfitOrLoss) {
		this.floatingProfitOrLoss = floatingProfitOrLoss;
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
}
