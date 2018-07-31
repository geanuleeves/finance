package com.waben.stock.interfaces.dto.admin.futures;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;

public class FuturesHoldPositionAgentDto extends FuturesContractOrderViewDto {

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
	/** 合约ID **/
	private Long contractId;
	/** 最新价 */
	private BigDecimal lastPrice;
	/** 递延费 */
	private BigDecimal deferredFee;
	/** 持仓小时 */
	private Integer holdingHours;
	/** 定单类型，1 市价 ,2 限价 */
	private Integer priceType;
	/** 状态 */
	private Integer state;

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

	public BigDecimal getLastPrice() {
		return lastPrice;
	}

	public void setLastPrice(BigDecimal lastPrice) {
		this.lastPrice = lastPrice;
	}

	public BigDecimal getDeferredFee() {
		return deferredFee;
	}

	public void setDeferredFee(BigDecimal deferredFee) {
		this.deferredFee = deferredFee;
	}

	public Integer getHoldingHours() {
		return holdingHours;
	}

	public void setHoldingHours(Integer holdingHours) {
		this.holdingHours = holdingHours;
	}

	public Integer getPriceType() {
		return priceType;
	}

	public void setPriceType(Integer priceType) {
		this.priceType = priceType;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	/**
	 * 使用序列化技术实现深拷贝
	 * 
	 * @return
	 */
	public FuturesHoldPositionAgentDto deepClone() throws IOException, ClassNotFoundException {
		// 将对象写入流中
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
		objectOutputStream.writeObject(this);
		// 从流中取出
		ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
		return (FuturesHoldPositionAgentDto) objectInputStream.readObject();
	}

}
