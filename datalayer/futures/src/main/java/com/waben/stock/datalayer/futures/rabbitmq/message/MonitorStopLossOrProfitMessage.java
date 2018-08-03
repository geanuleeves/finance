package com.waben.stock.datalayer.futures.rabbitmq.message;

public class MonitorStopLossOrProfitMessage {

	/**
	 * 合约订单ID
	 */
	private Long contractOrderId;
	/**
	 * 当前消息消费次数
	 */
	private int consumeCount;
	/**
	 * 最大消息消费次数
	 * <p>
	 * 如果为小于或者等于0的值，则表示一直消费
	 * </p>
	 */
	private int maxConsumeCount;

	public Long getContractOrderId() {
		return contractOrderId;
	}

	public void setContractOrderId(Long contractOrderId) {
		this.contractOrderId = contractOrderId;
	}

	public int getConsumeCount() {
		return consumeCount;
	}

	public void setConsumeCount(int consumeCount) {
		this.consumeCount = consumeCount;
	}

	public int getMaxConsumeCount() {
		return maxConsumeCount;
	}

	public void setMaxConsumeCount(int maxConsumeCount) {
		this.maxConsumeCount = maxConsumeCount;
	}

}
