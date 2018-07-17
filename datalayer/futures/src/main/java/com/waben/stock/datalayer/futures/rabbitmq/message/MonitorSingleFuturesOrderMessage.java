package com.waben.stock.datalayer.futures.rabbitmq.message;

public class MonitorSingleFuturesOrderMessage {

	/**
	 * 订单ID
	 */
	private Long orderId;
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

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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
