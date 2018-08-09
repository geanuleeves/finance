package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 代理商开平结算订单
 * 
 * @author sl
 *
 */
public class FuturesTradeActionAgentDto {

	private Long id;

	/** 客户姓名 **/
	private String publisherName;
	/** 交易账号 **/
	private String publisherPhone;
	/** 品种名称 **/
	private String commodityName;
	/** 品种代码 **/
	private String commoditySymbol;
	/** 货币 */
	private String commodityCurrency;
	/** 合约编号 **/
	private String contractNo;
	/** 合约ID **/
	private Long contractId;
	/** 交易方向 ,1 买涨，2 买跌 **/
	private Integer orderType;
	/** 交易开平仓 类型 ，1 开仓，2 平仓 */
	private Integer tradeActionType;
	/** 成交手数 */
	private BigDecimal filled;
	/** 最终成交价格 */
	private BigDecimal tradePrice;
	/** 成交盈亏 */
	private BigDecimal publisherProfitOrLoss;
	/** 成交编号 */
	private String actionNo;
	/** 成交时间 */
	private Date tradeTime;
	/** 成交状态 ,5 已成交 */
	private Integer state;
	/** 定单类型，1 市价 ,2 限价 */
	private Integer priceType;
	/** 平仓类型 */
	private Integer windControlType;
	/** 合约编号 **/
	private String code;
	/** 合约编号 **/
	private String orgName;
	/** 止盈金额 **/
	private BigDecimal stopProfitAmount;
	/** 止损金额 **/
	private BigDecimal stopLossAmount;

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

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public Integer getOrderType() {
		return orderType;
	}

	public void setOrderType(Integer orderType) {
		this.orderType = orderType;
	}

	public Integer getTradeActionType() {
		return tradeActionType;
	}

	public void setTradeActionType(Integer tradeActionType) {
		this.tradeActionType = tradeActionType;
	}

	public BigDecimal getFilled() {
		return filled;
	}

	public void setFilled(BigDecimal filled) {
		this.filled = filled;
	}

	public BigDecimal getTradePrice() {
		return tradePrice;
	}

	public void setTradePrice(BigDecimal tradePrice) {
		this.tradePrice = tradePrice;
	}

	public BigDecimal getPublisherProfitOrLoss() {
		return publisherProfitOrLoss;
	}

	public void setPublisherProfitOrLoss(BigDecimal publisherProfitOrLoss) {
		this.publisherProfitOrLoss = publisherProfitOrLoss;
	}

	public String getActionNo() {
		return actionNo;
	}

	public void setActionNo(String actionNo) {
		this.actionNo = actionNo;
	}

	public Date getTradeTime() {
		return tradeTime;
	}

	public void setTradeTime(Date tradeTime) {
		this.tradeTime = tradeTime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public Integer getWindControlType() {
		return windControlType;
	}

	public void setWindControlType(Integer windControlType) {
		this.windControlType = windControlType;
	}

	public String getCommodityCurrency() {
		return commodityCurrency;
	}

	public void setCommodityCurrency(String commodityCurrency) {
		this.commodityCurrency = commodityCurrency;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public BigDecimal getStopProfitAmount() {
		return stopProfitAmount;
	}

	public void setStopProfitAmount(BigDecimal stopProfitAmount) {
		this.stopProfitAmount = stopProfitAmount;
	}

	public BigDecimal getStopLossAmount() {
		return stopLossAmount;
	}

	public void setStopLossAmount(BigDecimal stopLossAmount) {
		this.stopLossAmount = stopLossAmount;
	}

}
