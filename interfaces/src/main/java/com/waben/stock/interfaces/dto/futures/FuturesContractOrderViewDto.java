package com.waben.stock.interfaces.dto.futures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.interfaces.enums.FuturesOrderType;

import io.swagger.annotations.ApiModelProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesContractOrderViewDto implements Serializable, Cloneable {

	private Long id;
	/** 发布人ID */
	@ApiModelProperty(value = "发布人ID")
	private Long publisherId;
	/** 品种编号 */
	@ApiModelProperty(value = "品种编号")
	private String commodityNo;
	/** 合约编号 */
	@ApiModelProperty(value = "合约编号")
	private String contractNo;
	/** 买涨手数（总，包括买入委托中的） */
	@ApiModelProperty(value = "买涨手数（总，包括买入委托中的）")
	private BigDecimal buyUpTotalQuantity;
	/** 买涨手数（成功） */
	@ApiModelProperty(value = "买涨手数（成功）")
	private BigDecimal buyUpQuantity;
	/** 买跌手数（总，包括买入委托中的） */
	@ApiModelProperty(value = "买跌手数（总，包括买入委托中的）")
	private BigDecimal buyFallTotalQuantity;
	/** 买跌手数（成功） */
	@ApiModelProperty(value = "买跌手数（成功）")
	private BigDecimal buyFallQuantity;
	/** 净手数 */
	@ApiModelProperty(value = "净手数")
	private BigDecimal lightQuantity;

	/** 订单交易类型 */
	@ApiModelProperty(value = "订单交易类型")
	private FuturesOrderType orderType;
	/**
	 * 触发止盈类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "触发止盈类型（用户设置）")
	private Integer limitProfitType;
	/** 止盈金额（用户设置） */
	@ApiModelProperty(value = "止盈金额（用户设置）")
	private BigDecimal perUnitLimitProfitAmount;
	/**
	 * 触发止损类型（用户设置）
	 * <ul>
	 * <li>1 价格</li>
	 * <li>2 金额</li>
	 * </ul>
	 */
	@ApiModelProperty(value = "触发止损类型（用户设置）")
	private Integer limitLossType;
	/** 止损金额（用户设置） */
	@ApiModelProperty(value = "止损金额（用户设置）")
	private BigDecimal perUnitLimitLossAmount;

	/** 更新时间 */
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;

	/** 合约名称 */
	@ApiModelProperty(value = "产品名称")
	private String commodityName;

	/** 今持仓（成功） */
	@ApiModelProperty(value = " 今持仓（成功）")
	private BigDecimal quantityNow;

	/** 已成交部分均价：已成交部分总费用/已成交总手数 累计 */
	@ApiModelProperty(value = "已成交部分均价（开仓）")
	private BigDecimal avgFillPrice;

	/** 浮动盈亏=（最新价格-成交价格）/ 最小波动 * 最小波动价格* 手数 */
	@ApiModelProperty(value = "浮动盈亏")
	private BigDecimal floatingProfitAndLoss;

	/** 交易综合费 :累计 */
	@ApiModelProperty(value = "交易综合费")
	private BigDecimal serviceFee;

	/** 保证金 :累计 */
	@ApiModelProperty(value = "保证金")
	private BigDecimal reserveFund;

	/** 当前价 */
	@ApiModelProperty(value = "当前价")
	private BigDecimal lastPrice;

	/** 最小波动 */
	@ApiModelProperty(value = "最小波动")
	private BigDecimal minWave;

	/** 汇率 */
	@ApiModelProperty(value = "汇率")
	private BigDecimal rate;

	/** 货币标识 */
	@ApiModelProperty(value = "货币标识")
	private String currencySign;

	/** 最小浮动价格 */
	@ApiModelProperty(value = "最小浮动价格")
	private BigDecimal perWaveMoney;

	/** 合约id */
	@ApiModelProperty(value = "合约id")
	private Long contractId;

	/**
	 * 一手强平点（亏损到剩余）
	 */
	@ApiModelProperty(value = "一手强平点（亏损到剩余）")
	private Integer unwindPointType;

	/**
	 * 一手强平点（亏损到剩余）
	 */
	@ApiModelProperty(value = "一手强平点（亏损到剩余）")
	private BigDecimal perUnitUnwindPoint;

	/**
	 * 买方对手价
	 */
	@ApiModelProperty(value = "买方对手价")
	private BigDecimal bidPrice;

	/**
	 * 卖方对手价
	 */
	@ApiModelProperty(value = "卖方对手价")
	private BigDecimal askPrice;

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

	public BigDecimal getBuyUpQuantity() {
		return buyUpQuantity;
	}

	public void setBuyUpQuantity(BigDecimal buyUpQuantity) {
		this.buyUpQuantity = buyUpQuantity;
	}

	public BigDecimal getBuyFallQuantity() {
		return buyFallQuantity;
	}

	public void setBuyFallQuantity(BigDecimal buyFallQuantity) {
		this.buyFallQuantity = buyFallQuantity;
	}

	public BigDecimal getLightQuantity() {
		return lightQuantity;
	}

	public void setLightQuantity(BigDecimal lightQuantity) {
		this.lightQuantity = lightQuantity;
	}

	public BigDecimal getBuyUpTotalQuantity() {
		return buyUpTotalQuantity;
	}

	public void setBuyUpTotalQuantity(BigDecimal buyUpTotalQuantity) {
		this.buyUpTotalQuantity = buyUpTotalQuantity;
	}

	public BigDecimal getBuyFallTotalQuantity() {
		return buyFallTotalQuantity;
	}

	public void setBuyFallTotalQuantity(BigDecimal buyFallTotalQuantity) {
		this.buyFallTotalQuantity = buyFallTotalQuantity;
	}

	public FuturesOrderType getOrderType() {
		return orderType;
	}

	public void setOrderType(FuturesOrderType orderType) {
		this.orderType = orderType;
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

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCommodityName() {
		return commodityName;
	}

	public void setCommodityName(String commodityName) {
		this.commodityName = commodityName;
	}

	public BigDecimal getQuantityNow() {
		return quantityNow;
	}

	public void setQuantityNow(BigDecimal quantityNow) {
		this.quantityNow = quantityNow;
	}

	public BigDecimal getFloatingProfitAndLoss() {
		return floatingProfitAndLoss;
	}

	public void setFloatingProfitAndLoss(BigDecimal floatingProfitAndLoss) {
		this.floatingProfitAndLoss = floatingProfitAndLoss;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public BigDecimal getReserveFund() {
		return reserveFund;
	}

	public void setReserveFund(BigDecimal reserveFund) {
		this.reserveFund = reserveFund;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getMinWave() {
		if (minWave != null) {
			return minWave.stripTrailingZeros();
		}
		return minWave;
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
		return perWaveMoney;
	}

	public void setPerWaveMoney(BigDecimal perWaveMoney) {
		this.perWaveMoney = perWaveMoney;
	}

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public Integer getUnwindPointType() {
		return unwindPointType;
	}

	public void setUnwindPointType(Integer unwindPointType) {
		this.unwindPointType = unwindPointType;
	}

	public BigDecimal getPerUnitUnwindPoint() {
		return perUnitUnwindPoint;
	}

	public void setPerUnitUnwindPoint(BigDecimal perUnitUnwindPoint) {
		this.perUnitUnwindPoint = perUnitUnwindPoint;
	}

	public BigDecimal getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(BigDecimal bidPrice) {
		this.bidPrice = bidPrice;
	}

	public BigDecimal getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice;
	}

	public BigDecimal getAvgFillPrice() {
		return avgFillPrice != null ? avgFillPrice.stripTrailingZeros() : null;
	}

	public void setAvgFillPrice(BigDecimal avgFillPrice) {
		this.avgFillPrice = avgFillPrice;
	}

	/**
	 * 使用序列化技术实现深拷贝
	 * 
	 * @return
	 */
	public FuturesContractOrderViewDto deepClone() throws IOException, ClassNotFoundException {
		// 将对象写入流中
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(this);
		// 从流中取出
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		return (FuturesContractOrderViewDto) objectInputStream.readObject();
	}

}
