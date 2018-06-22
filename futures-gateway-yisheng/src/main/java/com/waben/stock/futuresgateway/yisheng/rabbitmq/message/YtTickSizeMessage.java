package com.waben.stock.futuresgateway.yisheng.rabbitmq.message;

public class YtTickSizeMessage {

	private int tickerId;

	private int field;

	private int size;

	public YtTickSizeMessage() {
		super();
	}

	public YtTickSizeMessage(int tickerId, int field, int size) {
		super();
		this.tickerId = tickerId;
		this.field = field;
		this.size = size;
	}

	public int getTickerId() {
		return tickerId;
	}

	public void setTickerId(int tickerId) {
		this.tickerId = tickerId;
	}

	public int getField() {
		return field;
	}

	public void setField(int field) {
		this.field = field;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
