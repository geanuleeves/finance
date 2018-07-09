package com.waben.stock.datalayer.futures.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.waben.stock.datalayer.futures.entity.enumconverter.FuturesProductTypeConverter;
import com.waben.stock.interfaces.enums.FuturesProductType;

/**
 * 期货品种
 * 
 * @author luomengan
 *
 */
@Entity
@Table(name = "f_futures_commodity")
public class FuturesCommodity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 品种代码
	 */
	private String symbol;
	/**
	 * 品种名称
	 */
	private String name;
	/**
	 * 货币
	 */
	private String currency;
	/**
	 * 交易单位
	 */
	private String tradeUnit;
	/**
	 * 报价单位
	 */
	private String qutoteUnit;
	/**
	 * 图标
	 */
	private String icon;
	/**
	 * 描述
	 */
	private String contractDesc;
	/**
	 * 买涨开仓滑点
	 */
	private Integer buyUpOpenSlipPoint;
	/**
	 * 买涨平仓滑点
	 */
	private Integer buyUpCloseSlipPoint;
	/**
	 * 买跌开仓滑点
	 */
	private Integer buyFallOpenSlipPoint;
	/**
	 * 买跌平仓滑点
	 */
	private Integer buyFallCloseSlipPoint;
	/**
	 * 品种分类
	 */
	@Convert(converter = FuturesProductTypeConverter.class)
	private FuturesProductType productType;
	/**
	 * 乘数（1手等于多少股）
	 */
	private Integer multiplier;
	/**
	 * 最小波动
	 */
	private BigDecimal minWave;
	/**
	 * 1手合约价值
	 */
	private String perContractValue;
	/**
	 * 波动一次盈亏金额，单位为该品种的货币单位
	 */
	private BigDecimal perWaveMoney;
	/**
	 * 一手保证金
	 */
	private BigDecimal perUnitReserveFund;
	/**
	 * 一手强平点（亏损到剩余）
	 */
	private BigDecimal perUnitUnwindPoint;
	/**
	 * 强平点类型
	 * <ul>
	 * <li>1比例</li>
	 * <li>2金额</li>
	 * </ul>
	 */
	private Integer unwindPointType;
	/**
	 * 警戒线
	 */
	private BigDecimal cordon;
	/**
	 * 开仓手续费（人民币）
	 */
	private BigDecimal openwindServiceFee;
	/**
	 * 平仓手续费（人民币）
	 */
	private BigDecimal unwindServiceFee;
	/**
	 * 隔夜时间（交易所时间）
	 * <p>
	 * 格式如04:50:00，该时间为收取隔夜手续费和递延费的时间
	 * </p>
	 */
	private String overnightTime;
	/**
	 * 返还隔夜保证金的时间（交易所时间）
	 */
	private String returnOvernightReserveFundTime;
	/**
	 * 一手隔夜保证金
	 */
	private BigDecimal overnightPerUnitReserveFund;
	/**
	 * 一手隔夜递延费
	 */
	private BigDecimal overnightPerUnitDeferredFee;
	/**
	 * 周一交易时间(交易所)
	 * <p>
	 * 时间段用“-”隔开，多个时间段用“,”隔开，如“18:00-23:59”、“00:00-17:00,18:00-23:59”
	 * <p>
	 */
	private String monTradeTime;
	/**
	 * 周一交易时间描述
	 */
	private String monTradeTimeDesc;
	/**
	 * 周二交易时间(交易所)
	 */
	private String tueTradeTime;
	/**
	 * 周二交易时间描述
	 */
	private String tueTradeTimeDesc;
	/**
	 * 周三交易时间(交易所)
	 */
	private String wedTradeTime;
	/**
	 * 周三交易时间描述
	 */
	private String wedTradeTimeDesc;
	/**
	 * 周四交易时间(交易所)
	 */
	private String thuTradeTime;
	/**
	 * 周四交易时间描述
	 */
	private String thuTradeTimeDesc;
	/**
	 * 周五交易时间(交易所)
	 */
	private String friTradeTime;
	/**
	 * 周五交易时间描述
	 */
	private String friTradeTimeDesc;
	/**
	 * 周六交易时间(交易所)
	 */
	private String satTradeTime;
	/**
	 * 周六交易时间描述
	 */
	private String satTradeTimeDesc;
	/**
	 * 周日交易时间(交易所)
	 */
	private String sunTradeTime;
	/**
	 * 周日交易时间描述
	 */
	private String sunTradeTimeDesc;
	/**
	 * 是否可用
	 */
	private Boolean enable;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 对应的交易所
	 */
	@ManyToOne
	@JoinColumn(name = "exchange_id")
	private FuturesExchange exchange;
	/**
	 * 预设置的手数列表
	 */
	@OneToMany(cascade = { CascadeType.REMOVE }, fetch = FetchType.EAGER, mappedBy = "commodity")
	private Set<FuturesPreQuantity> preQuantitySet;

	private Date createTime;

	/***************** 分割线，以下字段为非数据库字段 ********************/

	/**
	 * 交易所ID
	 */
	@Transient
	private Long exchangeId;
	/**
	 * 交易所名称
	 */
	@Transient
	private String exchangeName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getTradeUnit() {
		return tradeUnit;
	}

	public void setTradeUnit(String tradeUnit) {
		this.tradeUnit = tradeUnit;
	}

	public String getQutoteUnit() {
		return qutoteUnit;
	}

	public void setQutoteUnit(String qutoteUnit) {
		this.qutoteUnit = qutoteUnit;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getContractDesc() {
		return contractDesc;
	}

	public void setContractDesc(String contractDesc) {
		this.contractDesc = contractDesc;
	}

	public FuturesProductType getProductType() {
		return productType;
	}

	public void setProductType(FuturesProductType productType) {
		this.productType = productType;
	}

	public Integer getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Integer multiplier) {
		this.multiplier = multiplier;
	}

	public BigDecimal getMinWave() {
		return minWave;
	}

	public void setMinWave(BigDecimal minWave) {
		this.minWave = minWave;
	}

	public String getPerContractValue() {
		return perContractValue;
	}

	public void setPerContractValue(String perContractValue) {
		this.perContractValue = perContractValue;
	}

	public BigDecimal getPerWaveMoney() {
		return perWaveMoney;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
	}

	public BigDecimal getPerUnitReserveFund() {
		return perUnitReserveFund;
	}

	public void setPerUnitReserveFund(BigDecimal perUnitReserveFund) {
		this.perUnitReserveFund = perUnitReserveFund;
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

	public BigDecimal getCordon() {
		return cordon;
	}

	public void setCordon(BigDecimal cordon) {
		this.cordon = cordon;
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

	public String getOvernightTime() {
		return overnightTime;
	}

	public void setOvernightTime(String overnightTime) {
		this.overnightTime = overnightTime;
	}

	public String getReturnOvernightReserveFundTime() {
		return returnOvernightReserveFundTime;
	}

	public void setReturnOvernightReserveFundTime(String returnOvernightReserveFundTime) {
		this.returnOvernightReserveFundTime = returnOvernightReserveFundTime;
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

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public FuturesExchange getExchange() {
		return exchange;
	}

	public void setExchange(FuturesExchange exchange) {
		this.exchange = exchange;
	}

	public Set<FuturesPreQuantity> getPreQuantitySet() {
		return preQuantitySet;
	}

	public void setPreQuantitySet(Set<FuturesPreQuantity> preQuantitySet) {
		this.preQuantitySet = preQuantitySet;
	}

	public Long getExchangeId() {
		if (exchange != null) {
			return exchange.getId();
		}
		return exchangeId;
	}

	public void setExchangeId(Long exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getExchangeName() {
		if (exchange != null) {
			return exchange.getName();
		}
		return exchangeName;
	}

	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	public String getMonTradeTime() {
		return monTradeTime;
	}

	public void setMonTradeTime(String monTradeTime) {
		this.monTradeTime = monTradeTime;
	}

	public String getMonTradeTimeDesc() {
		return monTradeTimeDesc;
	}

	public void setMonTradeTimeDesc(String monTradeTimeDesc) {
		this.monTradeTimeDesc = monTradeTimeDesc;
	}

	public String getTueTradeTime() {
		return tueTradeTime;
	}

	public void setTueTradeTime(String tueTradeTime) {
		this.tueTradeTime = tueTradeTime;
	}

	public String getTueTradeTimeDesc() {
		return tueTradeTimeDesc;
	}

	public void setTueTradeTimeDesc(String tueTradeTimeDesc) {
		this.tueTradeTimeDesc = tueTradeTimeDesc;
	}

	public String getWedTradeTime() {
		return wedTradeTime;
	}

	public void setWedTradeTime(String wedTradeTime) {
		this.wedTradeTime = wedTradeTime;
	}

	public String getWedTradeTimeDesc() {
		return wedTradeTimeDesc;
	}

	public void setWedTradeTimeDesc(String wedTradeTimeDesc) {
		this.wedTradeTimeDesc = wedTradeTimeDesc;
	}

	public String getThuTradeTime() {
		return thuTradeTime;
	}

	public void setThuTradeTime(String thuTradeTime) {
		this.thuTradeTime = thuTradeTime;
	}

	public String getThuTradeTimeDesc() {
		return thuTradeTimeDesc;
	}

	public void setThuTradeTimeDesc(String thuTradeTimeDesc) {
		this.thuTradeTimeDesc = thuTradeTimeDesc;
	}

	public String getFriTradeTime() {
		return friTradeTime;
	}

	public void setFriTradeTime(String friTradeTime) {
		this.friTradeTime = friTradeTime;
	}

	public String getFriTradeTimeDesc() {
		return friTradeTimeDesc;
	}

	public void setFriTradeTimeDesc(String friTradeTimeDesc) {
		this.friTradeTimeDesc = friTradeTimeDesc;
	}

	public String getSatTradeTime() {
		return satTradeTime;
	}

	public void setSatTradeTime(String satTradeTime) {
		this.satTradeTime = satTradeTime;
	}

	public String getSatTradeTimeDesc() {
		return satTradeTimeDesc;
	}

	public void setSatTradeTimeDesc(String satTradeTimeDesc) {
		this.satTradeTimeDesc = satTradeTimeDesc;
	}

	public String getSunTradeTime() {
		return sunTradeTime;
	}

	public void setSunTradeTime(String sunTradeTime) {
		this.sunTradeTime = sunTradeTime;
	}

	public String getSunTradeTimeDesc() {
		return sunTradeTimeDesc;
	}

	public void setSunTradeTimeDesc(String sunTradeTimeDesc) {
		this.sunTradeTimeDesc = sunTradeTimeDesc;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getBuyUpOpenSlipPoint() {
		return buyUpOpenSlipPoint;
	}

	public void setBuyUpOpenSlipPoint(Integer buyUpOpenSlipPoint) {
		this.buyUpOpenSlipPoint = buyUpOpenSlipPoint;
	}

	public Integer getBuyUpCloseSlipPoint() {
		return buyUpCloseSlipPoint;
	}

	public void setBuyUpCloseSlipPoint(Integer buyUpCloseSlipPoint) {
		this.buyUpCloseSlipPoint = buyUpCloseSlipPoint;
	}

	public Integer getBuyFallOpenSlipPoint() {
		return buyFallOpenSlipPoint;
	}

	public void setBuyFallOpenSlipPoint(Integer buyFallOpenSlipPoint) {
		this.buyFallOpenSlipPoint = buyFallOpenSlipPoint;
	}

	public Integer getBuyFallCloseSlipPoint() {
		return buyFallCloseSlipPoint;
	}

	public void setBuyFallCloseSlipPoint(Integer buyFallCloseSlipPoint) {
		this.buyFallCloseSlipPoint = buyFallCloseSlipPoint;
	}

}
