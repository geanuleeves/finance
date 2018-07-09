package com.waben.stock.interfaces.dto.admin.futures;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesCommodityAdminDto {

	/**
	 * 品种ID
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

	/**
	 * 产品代码
	 */
	private String symbol;

	/**
	 * 产品名称
	 */
	private String name;

	/**
	 * 交易单位
	 */
	private String tradeUnit;

	/**
	 * 报价单位
	 */
	private String qutoteUnit;

	/**
	 * 货币
	 */
	private String currency;

	/**
	 * 图标
	 */
	private String icon;

	/**
	 * 汇率
	 */
	private BigDecimal rate;

	/**
	 * 产品类型
	 */
	private String productType;
	/**
	 * 乘数（1手等于多少股）
	 */
	private Integer multiplier;
	/**
	 * 最小波动
	 */
	private BigDecimal minWave;
	/**
	 * 波动一次盈亏金额，单位为该合约的货币单位
	 */
	private BigDecimal perWaveMoney;
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
	 * 1手合约价值
	 */
	private String perContractValue;
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

	private BigDecimal cordon;
	/**
	 * 该合约总计的可使用的额度（手）
	 * 
	 * <p>
	 * 需将所有未平仓的订单量相加，买涨的为正数，买跌的为负数，相加结果取绝对值再和这个总额度比较
	 * </p>
	 */
	private BigDecimal userTotalLimit;
	/**
	 * 单笔订单额度限制（手）
	 */
	private BigDecimal perOrderLimit;
	/**
	 * 开仓手续费（人民币）
	 */
	private BigDecimal openwindServiceFee;
	/**
	 * 平仓手续费（人民币）
	 */
	private BigDecimal unwindServiceFee;
	/**
	 * 隔夜时间
	 */
	private String overnightTime;
	/**
	 * 一手隔夜保证金
	 */
	private BigDecimal overnightPerUnitReserveFund;
	/**
	 * 一手隔夜递延费
	 */
	private BigDecimal overnightPerUnitDeferredFee;

	/**
	 * 是否可用
	 */
	private Boolean enable;

	/**
	 * 期货合约状态
	 * 
	 * <p>
	 * 1 交易中
	 * </p>
	 * <p>
	 * 2 休市中
	 * </p>
	 * <p>
	 * 3 异常
	 * </p>
	 */
	// private Integer state;

	/**
	 * 每个合约的描述
	 */
	private String contractDesc;

	/**
	 * 返还隔夜保证金的时间（交易所时间）
	 */
	private String returnOvernightReserveFundTime;

	private List<FuturesPreQuantityDto> preQuantityDto;

	private Date createTime;

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

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
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

	public BigDecimal getPerWaveMoney() {
		return perWaveMoney;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
	}

	public String getPerContractValue() {
		return perContractValue;
	}

	public void setPerContractValue(String perContractValue) {
		this.perContractValue = perContractValue;
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

	public String getContractDesc() {
		return contractDesc;
	}

	public void setContractDesc(String contractDesc) {
		this.contractDesc = contractDesc;
	}

	public String getReturnOvernightReserveFundTime() {
		return returnOvernightReserveFundTime;
	}

	public void setReturnOvernightReserveFundTime(String returnOvernightReserveFundTime) {
		this.returnOvernightReserveFundTime = returnOvernightReserveFundTime;
	}

	public List<FuturesPreQuantityDto> getPreQuantityDto() {
		return preQuantityDto;
	}

	public void setPreQuantityDto(List<FuturesPreQuantityDto> preQuantityDto) {
		this.preQuantityDto = preQuantityDto;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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
