package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;
import java.util.Date;

import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.enums.FuturesWindControlType;

/**
 * 代理商订单记录Dto
 * 
 * @author sl
 *
 */
public class AgentOrderRecordDto {

	private Long id;

	/** 客户姓名 **/
	private String publisherName;
	/** 交易账号 **/
	private String publisherPhone;
	/** 品种代码 **/
	private String symbol;
	/** 品种名称 **/
	private String name;
	/** 品种编号 **/
	private String contractNo;
	/** 流水号 **/
	private String tradeNo;
	/** 交易方向 **/
	private Integer orderType;
	/** 状态 **/
	private Integer state;
	/** 手数 **/
	private BigDecimal totalQuantity;
	/** 买入时间 **/
	private Date buyingTime;
	/** 买入价格 **/
	private BigDecimal buyingPrice;
	/** 用户盈亏 **/
	private BigDecimal publisherProfitOrLoss;
	/** 定单类型 **/
	private FuturesTradePriceType buyingPriceType;
	/** 开仓手续费 **/
	private BigDecimal openwindServiceFee;
	/** 平仓手续费 **/
	private BigDecimal unwindServiceFee;
	/** 服务费 **/
	private BigDecimal reserveFund;
	/** 隔夜手续费 **/
	private BigDecimal overnightServiceFee;
	/** 隔夜服务费 **/
	private BigDecimal overnightReserveFund;
	/** 止损金额 **/
	private BigDecimal perUnitLimitLossAmount;
	/** 止盈金额 **/
	private BigDecimal perUnitLimitProfitAmount;
	/** 卖出时间 **/
	private Date sellingTime;
	/** 卖出价格 **/
	private BigDecimal sellingPrice;
	/** 盈亏 **/
	private BigDecimal profitOrLoss;
	/** 风控类型 **/
	private FuturesWindControlType windControlType;
	/** 代理商名称 **/
	private String orgName;
	/** 合约id **/
	private Long contractId;
	/** 品种货币 **/
	private String commodityCurrency;
	/** 代理商代码 **/
	private String code;

	/*******************************************************/
	/** 最新价格 **/
	private BigDecimal LastPrice;
	/** 持仓小时 **/
	private String positionDays;
	private BigDecimal sellingProfit;
	private Date positionEndTime;
	private BigDecimal floatingProfitOrLoss;

	private BigDecimal profit;

	private Date dealTime;
	/**
	 * 委托价
	 */
	private BigDecimal entrustAppointPrice;
	/**
	 * 发布时间
	 */
	private Date postTime;
	/**
	 * 服务费（人民币）
	 */
	private BigDecimal serviceFee;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getTradeNo() {
		return tradeNo;
	}

	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
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

	public BigDecimal getPublisherProfitOrLoss() {
		return publisherProfitOrLoss;
	}

	public void setPublisherProfitOrLoss(BigDecimal publisherProfitOrLoss) {
		this.publisherProfitOrLoss = publisherProfitOrLoss;
	}

	public FuturesTradePriceType getBuyingPriceType() {
		return buyingPriceType;
	}

	public void setBuyingPriceType(FuturesTradePriceType buyingPriceType) {
		this.buyingPriceType = buyingPriceType;
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

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public BigDecimal getOvernightServiceFee() {
		return overnightServiceFee;
	}

	public void setOvernightServiceFee(BigDecimal overnightServiceFee) {
		this.overnightServiceFee = overnightServiceFee;
	}

	public BigDecimal getOvernightReserveFund() {
		return overnightReserveFund;
	}

	public void setOvernightReserveFund(BigDecimal overnightReserveFund) {
		this.overnightReserveFund = overnightReserveFund;
	}

	public BigDecimal getPerUnitLimitLossAmount() {
		return perUnitLimitLossAmount;
	}

	public void setPerUnitLimitLossAmount(BigDecimal perUnitLimitLossAmount) {
		this.perUnitLimitLossAmount = perUnitLimitLossAmount;
	}

	public BigDecimal getPerUnitLimitProfitAmount() {
		return perUnitLimitProfitAmount;
	}

	public void setPerUnitLimitProfitAmount(BigDecimal perUnitLimitProfitAmount) {
		this.perUnitLimitProfitAmount = perUnitLimitProfitAmount;
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

	public BigDecimal getProfitOrLoss() {
		return profitOrLoss;
	}

	public void setProfitOrLoss(BigDecimal profitOrLoss) {
		this.profitOrLoss = profitOrLoss;
	}

	public FuturesWindControlType getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(FuturesWindControlType windControlType) {
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

	public String getCommodityCurrency() {
		return commodityCurrency;
	}

	public void setCommodityCurrency(String commodityCurrency) {
		this.commodityCurrency = commodityCurrency;
	}

	public BigDecimal getLastPrice() {
		return LastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		LastPrice = lastPrice;
	}

	public String getPositionDays() {
		return positionDays;
	}

	public void setPositionDays(String positionDays) {
		this.positionDays = positionDays;
	}

	public BigDecimal getSellingProfit() {
		return sellingProfit;
	}

	public void setSellingProfit(BigDecimal sellingProfit) {
		this.sellingProfit = sellingProfit;
	}

	public Date getPositionEndTime() {
		return positionEndTime;
	}

	public void setPositionEndTime(Date positionEndTime) {
		this.positionEndTime = positionEndTime;
	}

	public BigDecimal getFloatingProfitOrLoss() {
		return floatingProfitOrLoss;
	}

	public void setFloatingProfitOrLoss(BigDecimal floatingProfitOrLoss) {
		this.floatingProfitOrLoss = floatingProfitOrLoss;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Date getDealTime() {
		return dealTime;
	}

	public void setDealTime(Date dealTime) {
		this.dealTime = dealTime;
	}

	public BigDecimal getEntrustAppointPrice() {
		return entrustAppointPrice;
	}

	public void setEntrustAppointPrice(BigDecimal entrustAppointPrice) {
		this.entrustAppointPrice = entrustAppointPrice;
	}

	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

}
