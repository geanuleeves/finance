package com.waben.stock.futuresgateway.yingtou.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 期货合约
 * 
 * @author lma
 *
 */
@Entity
@Table(name = "f_gateway_futures_contract")
public class FuturesContract {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	/**
	 * 合约标识
	 */
	private String symbol;
	/**
	 * 本地tws合约名称
	 */
	private String localSymbolName;
	/**
	 * 安全类型
	 */
	private String secType;
	/**
	 * 货币
	 */
	private String currency;
	/**
	 * 交易所ID
	 */
	private Long exchangeId;
	/**
	 * 交易所
	 */
	private String exchange;
	/**
	 * 最高价投标合同（买方开价）
	 */
	private BigDecimal bigPrice;
	/**
	 * 以投标价格提供的合同或批次数量（买方开价）
	 */
	private Integer bidSize;
	/**
	 * 最低价投标合同（卖方开价）
	 */
	private BigDecimal askPrice;
	/**
	 * 以投标价格提供的合同或批次数量（卖方开价）
	 */
	private Integer askSize;
	/**
	 * 最新价
	 */
	private BigDecimal lastPrice;
	/**
	 * 以最新价交易的合同或批次数量
	 */
	private Integer lastSize;
	/**
	 * 今天的开盘价
	 */
	private BigDecimal openPrice;
	/**
	 * 当天最高价
	 */
	private BigDecimal highPrice;
	/**
	 * 当天最低价
	 */
	private BigDecimal lowPrice;
	/**
	 * 昨天的收盘价
	 */
	private BigDecimal closePrice;
	/**
	 * 当天成交量
	 */
	private Integer volume;
	/**
	 * 是否可用
	 */
	private Boolean enable;

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

	public String getSecType() {
		return secType;
	}

	public void setSecType(String secType) {
		this.secType = secType;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public BigDecimal getBigPrice() {
		return bigPrice;
	}

	public void setBigPrice(BigDecimal bigPrice) {
		this.bigPrice = bigPrice;
	}

	public Integer getBidSize() {
		return bidSize;
	}

	public void setBidSize(Integer bidSize) {
		this.bidSize = bidSize;
	}

	public BigDecimal getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(BigDecimal askPrice) {
		this.askPrice = askPrice;
	}

	public Integer getAskSize() {
		return askSize;
	}

	public void setAskSize(Integer askSize) {
		this.askSize = askSize;
	}

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public Integer getLastSize() {
		return lastSize;
	}

	public void setLastSize(Integer lastSize) {
		this.lastSize = lastSize;
	}

	public BigDecimal getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(BigDecimal highPrice) {
		this.highPrice = highPrice;
	}

	public BigDecimal getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(BigDecimal lowPrice) {
		this.lowPrice = lowPrice;
	}

	public Integer getVolume() {
		return volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	public BigDecimal getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(BigDecimal closePrice) {
		this.closePrice = closePrice;
	}

	public Long getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(Long exchangeId) {
		this.exchangeId = exchangeId;
	}

	public String getLocalSymbolName() {
		return localSymbolName;
	}

	public void setLocalSymbolName(String localSymbolName) {
		this.localSymbolName = localSymbolName;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public BigDecimal getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(BigDecimal openPrice) {
		this.openPrice = openPrice;
	}

}
