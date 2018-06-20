package com.waben.stock.datalayer.futures.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import io.swagger.annotations.ApiModelProperty;

/**
 * 期货合约
 * 
 * @author luomengan
 *
 */
@Entity
@Table(name = "f_futures_contract")
public class FuturesContract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
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
	 * 首次通知日 The first day on which a notice of intent to deliver a commodity
	 * can be made by a clearinghouse to a buyer in order to fulfill a given
	 * futures contract
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
	 * 是否app合约
	 */
	private Boolean appContract;
	/**
	 * 是否pc合约
	 */
	private Boolean pcContract;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
	/**
	 * 对应的品种
	 */
	@ManyToOne
	@JoinColumn(name = "commodity_id")
	private FuturesCommodity commodity;

	/***************** 分割线，以下字段为非数据库字段 ********************/

	/**
	 * 品种ID
	 */
	@Transient
	@ApiModelProperty(value = "品种ID")
	private Long commodityId;

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

	public FuturesCommodity getCommodity() {
		return commodity;
	}

	public void setCommodity(FuturesCommodity commodity) {
		this.commodity = commodity;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
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

	public Long getCommodityId() {
		if (commodity != null) {
			return commodity.getId();
		}
		return commodityId;
	}

	public void setCommodityId(Long commodityId) {
		this.commodityId = commodityId;
	}

}
