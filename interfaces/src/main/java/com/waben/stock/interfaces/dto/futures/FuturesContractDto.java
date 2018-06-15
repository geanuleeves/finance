package com.waben.stock.interfaces.dto.futures;

import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;

public class FuturesContractDto extends FuturesCommodityDto {

	@ApiModelProperty(value = "合约ID")
	private Long id;
	/**
	 * 品种ID
	 */
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;
	/**
	 * 合约编号
	 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/**
	 * 合约名称
	 */
	@ApiModelProperty(value = "合约名称")
	private String contractName;
	/**
	 * 该合约总计的可使用的额度（手）
	 * 
	 * <p>
	 * 需将所有未平仓的订单量相加，买涨的为正数，买跌的为负数，相加结果取绝对值再和这个总额度比较
	 * </p>
	 */
	@ApiModelProperty(value = "该合约总计的可使用的额度（手）")
	private BigDecimal userTotalLimit;
	/**
	 * 单笔订单额度限制（手）
	 */
	@ApiModelProperty(value = "单笔订单额度限制（手）")
	private BigDecimal perOrderLimit;
	/**
	 * The first day on which a notice of intent to deliver a commodity can be
	 * made by a clearinghouse to a buyer in order to fulfill a given futures
	 * contract
	 */
	@ApiModelProperty(value = "")
	private Date firstNoticeDate;
	/**
	 * The first day when an invesor who is short a commodify futures contract
	 * may notify the clearinghouse of the intention to deliver the commodity
	 */
	@ApiModelProperty(value = "")
	private Date firstPositonDate;
	/**
	 * 最后交易日
	 */
	@ApiModelProperty(value = "")
	private Date lastTradingDate;
	/**
	 * 到期日期
	 */
	@ApiModelProperty(value = "")
	private Date expirationDate;
	/**
	 * 强平时间
	 */
	@ApiModelProperty(value = "")
	private Date forceUnwindDate;
	/**
	 * 是否主力合约
	 */
	@ApiModelProperty(value = "是否主力合约")
	private Boolean mainForce;
	/**
	 * 是否可用
	 */
	@ApiModelProperty(value = "是否可用")
	private Boolean enable;
	/**
	 * 是否app合约
	 */
	@ApiModelProperty(value = "是否app合约")
	private Boolean appContract;
	/**
	 * 是否pc合约
	 */
	@ApiModelProperty(value = "是否pc合约")
	private Boolean pcContract;
	/**
	 * 更新时间
	 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	/*********************** 一些额外的属性 ***********************/

	/**
	 * 期货合约状态
	 * 
	 * <ul>
	 * <li>1 交易中</li>
	 * <li>2 休市中</li>
	 * <li>3 异常</li>
	 * </ul>
	 * </p>
	 */
	@ApiModelProperty(value = "期货合约状态,1 交易中;2 休市中;3 异常")
	private Integer state;
	/**
	 * 当天交易时间描述
	 */
	@ApiModelProperty(value = "当天交易时间描述")
	private String currentTradeTimeDesc;
	/**
	 * 交易所是否可用
	 */
	@ApiModelProperty(value = "value")
	private Boolean exchangeEnable;
	/**
	 * 北京时间的时差和交易所
	 */
	@ApiModelProperty(value = "北京时间的时差和交易所")
	private Integer timeZoneGap;
	/**
	 * 汇率
	 */
	@ApiModelProperty(value = "汇率")
	private BigDecimal rate;
	/**
	 * 货币名称
	 */
	@ApiModelProperty(value = "货币名称")
	private String currencyName;
	/**
	 * 货币符号，如“$”,表示美元
	 */
	@ApiModelProperty(value = "货币符号，如“$”,表示美元")
	private String currencySign;
	/**
	 * 本时段持仓最后时间
	 */
	@ApiModelProperty(value = "本时段持仓最后时间")
	private String currentHoldingTime;
	/**
	 * 下一个交易时间
	 */
	@ApiModelProperty(value = "下一个交易时间")
	private String nextTradingTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContractNo() {
		return contractNo;
	}

	public void setContractNo(String contractNo) {
		this.contractNo = contractNo;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public BigDecimal getUserTotalLimit() {
		return userTotalLimit;
	}

	public void setUserTotalLimit(BigDecimal userTotalLimit) {
		this.userTotalLimit = userTotalLimit;
	}

	public BigDecimal getPerOrderLimit() {
		return perOrderLimit;
	}

	public void setPerOrderLimit(BigDecimal perOrderLimit) {
		this.perOrderLimit = perOrderLimit;
	}

	public Date getFirstNoticeDate() {
		return firstNoticeDate;
	}

	public void setFirstNoticeDate(Date firstNoticeDate) {
		this.firstNoticeDate = firstNoticeDate;
	}

	public Date getFirstPositonDate() {
		return firstPositonDate;
	}

	public void setFirstPositonDate(Date firstPositonDate) {
		this.firstPositonDate = firstPositonDate;
	}

	public Date getLastTradingDate() {
		return lastTradingDate;
	}

	public void setLastTradingDate(Date lastTradingDate) {
		this.lastTradingDate = lastTradingDate;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}

	public Date getForceUnwindDate() {
		return forceUnwindDate;
	}

	public void setForceUnwindDate(Date forceUnwindDate) {
		this.forceUnwindDate = forceUnwindDate;
	}

	public Boolean getMainForce() {
		return mainForce;
	}

	public void setMainForce(Boolean mainForce) {
		this.mainForce = mainForce;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public Boolean getAppContract() {
		return appContract;
	}

	public void setAppContract(Boolean appContract) {
		this.appContract = appContract;
	}

	public Boolean getPcContract() {
		return pcContract;
	}

	public void setPcContract(Boolean pcContract) {
		this.pcContract = pcContract;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getCurrentTradeTimeDesc() {
		return currentTradeTimeDesc;
	}

	public void setCurrentTradeTimeDesc(String currentTradeTimeDesc) {
		this.currentTradeTimeDesc = currentTradeTimeDesc;
	}

	public Boolean getExchangeEnable() {
		return exchangeEnable;
	}

	public void setExchangeEnable(Boolean exchangeEnable) {
		this.exchangeEnable = exchangeEnable;
	}

	public Integer getTimeZoneGap() {
		return timeZoneGap;
	}

	public void setTimeZoneGap(Integer timeZoneGap) {
		this.timeZoneGap = timeZoneGap;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getCurrencyName() {
		return currencyName;
	}

	public void setCurrencyName(String currencyName) {
		this.currencyName = currencyName;
	}

	public String getCurrencySign() {
		return currencySign;
	}

	public void setCurrencySign(String currencySign) {
		this.currencySign = currencySign;
	}

	public String getCurrentHoldingTime() {
		return currentHoldingTime;
	}

	public void setCurrentHoldingTime(String currentHoldingTime) {
		this.currentHoldingTime = currentHoldingTime;
	}

	public String getNextTradingTime() {
		return nextTradingTime;
	}

	public void setNextTradingTime(String nextTradingTime) {
		this.nextTradingTime = nextTradingTime;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

}