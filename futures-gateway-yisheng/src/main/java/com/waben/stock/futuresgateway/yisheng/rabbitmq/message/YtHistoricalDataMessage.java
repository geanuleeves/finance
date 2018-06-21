package com.waben.stock.futuresgateway.yisheng.rabbitmq.message;

public class YtHistoricalDataMessage {

	private int reqId;

	private String date;

	private double open;

	private double high;

	private double low;

	private double close;

	private int volume;

	private int count;

	private double WAP;

	private boolean hasGaps;

	public YtHistoricalDataMessage() {
		super();
	}

	public YtHistoricalDataMessage(int reqId, String date, double open, double high, double low, double close, int volume,
			int count, double wAP, boolean hasGaps) {
		super();
		this.reqId = reqId;
		this.date = date;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.count = count;
		WAP = wAP;
		this.hasGaps = hasGaps;
	}

	public int getReqId() {
		return reqId;
	}

	public void setReqId(int reqId) {
		this.reqId = reqId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getOpen() {
		return open;
	}

	public void setOpen(double open) {
		this.open = open;
	}

	public double getHigh() {
		return high;
	}

	public void setHigh(double high) {
		this.high = high;
	}

	public double getLow() {
		return low;
	}

	public void setLow(double low) {
		this.low = low;
	}

	public double getClose() {
		return close;
	}

	public void setClose(double close) {
		this.close = close;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public double getWAP() {
		return WAP;
	}

	public void setWAP(double wAP) {
		WAP = wAP;
	}

	public boolean isHasGaps() {
		return hasGaps;
	}

	public void setHasGaps(boolean hasGaps) {
		this.hasGaps = hasGaps;
	}

}
