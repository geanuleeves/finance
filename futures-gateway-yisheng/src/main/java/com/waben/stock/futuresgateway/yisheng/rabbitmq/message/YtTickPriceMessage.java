package com.waben.stock.futuresgateway.yisheng.rabbitmq.message;

public class YtTickPriceMessage {

	private int tickerId;

	private int field;

	private double price;

	private int canAutoExecute;

	public YtTickPriceMessage() {
		super();
	}

	public YtTickPriceMessage(int tickerId, int field, double price, int canAutoExecute) {
		super();
		this.tickerId = tickerId;
		this.field = field;
		this.price = price;
		this.canAutoExecute = canAutoExecute;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getCanAutoExecute() {
		return canAutoExecute;
	}

	public void setCanAutoExecute(int canAutoExecute) {
		this.canAutoExecute = canAutoExecute;
	}

}
