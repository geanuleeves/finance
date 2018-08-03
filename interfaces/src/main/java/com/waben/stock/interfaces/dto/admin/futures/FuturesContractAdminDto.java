package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.dto.futures.FuturesContractTermDto;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesContractAdminDto {

	/**
	 * 合约期限ID
	 */
	private Long id;
	
	private Long exchangeId;
	
	/**
	 * 交易所代码
	 */
	private String exchangcode;
	
	/**
	 * 交易所全称
	 */
	private String exchangename;
	
	/**
	 * 交易所类型
	 * <ul>
	 * <li>1外盘</li>
	 * <li>2内盘</li>
	 * </ul>
	 */
	private Integer exchangeType;
	
	private Long commodityId;
	
	/**
	 * 产品代码
	 */
	private String symbol;
	
	/**
	 * 产品名称
	 */
	private String name;
	
	/**
	 * 产品类型
	 */
	private String productType;
	
	/**
	 * 合约编号
	 */
	private String contractNo;
	
	/**
	 * 合约名称
	 */
	private String contractName;
	
	/**
	 * 该合约总计的可使用的额度（手）
	 * 
	 * <p>
	 * 需将所有未平仓的订单量相加，买涨的为正数，买跌的为负数，相加结果取绝对值再和这个总额度比较
	 * </p>
	 */
	private BigDecimal userTotalLimit;
	
	/**
	 * 买涨持仓总额度
	 */
	private BigDecimal buyUpTotalLimit;
	
	/**
	 * 买跌持仓总额度
	 */
	private BigDecimal buyFullTotalLimit;
	
	/**
	 * 单笔订单额度限制（手）
	 */
	private BigDecimal perOrderLimit;
	
	/**
	 * The first day on which a notice of intent to deliver a commodity can be
	 * made by a clearinghouse to a buyer in order to fulfill a given futures
	 * contract
	 */
	private Date firstNoticeDate;
	
	/**
	 * The first day when an invesor who is short a commodify futures contract
	 * may notify the clearinghouse of the intention to deliver the commodity
	 */
	private Date firstPositonDate;
	
	/**
	 * 最后交易日
	 */
	private Date lastTradingDate;
	
	/**
	 * 到期日期
	 */
	private Date expirationDate;
	
	/**
	 * 强平时间
	 */
	private Date forceUnwindDate;
	

	/**
	 * 是否主力合约
	 */
	private Boolean mainForce;
	
	/**
	 * 是否可用
	 */
	private Boolean enable;
	
	/**
	 * 合约状态
	 */
	private Integer state;
	
	/**
	 * 是否app合约
	 */
	private Boolean appContract;
	
	/**
	 * 是否pc合约
	 */
	private Boolean pcContract;
	
	private Date createTime;
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(Long exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getExchangcode() {
		return exchangcode;
	}

	public void setExchangcode(String exchangcode) {
		this.exchangcode = exchangcode;
	}

	public String getExchangename() {
		return exchangename;
	}

	public void setExchangename(String exchangename) {
		this.exchangename = exchangename;
	}

	public Integer getExchangeType() {
		return exchangeType;
	}

	public void setExchangeType(Integer exchangeType) {
		this.exchangeType = exchangeType;
	}

	public Long getCommodityId() {
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
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

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
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

	public BigDecimal getBuyUpTotalLimit() {
		return buyUpTotalLimit;
	}

	public void setBuyUpTotalLimit(BigDecimal buyUpTotalLimit) {
		this.buyUpTotalLimit = buyUpTotalLimit;
	}

	public BigDecimal getBuyFullTotalLimit() {
		return buyFullTotalLimit;
	}

	public void setBuyFullTotalLimit(BigDecimal buyFullTotalLimit) {
		this.buyFullTotalLimit = buyFullTotalLimit;
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
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

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	
	
}
