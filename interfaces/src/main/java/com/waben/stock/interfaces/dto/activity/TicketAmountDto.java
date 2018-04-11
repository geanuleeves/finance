package com.waben.stock.interfaces.dto.activity;

import java.math.BigDecimal;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class TicketAmountDto {

	
	private long ticketAmountId;
	
	/**
	 * 券类型 1:期权抵扣券 2：话费券
	 */
	private int ticketType;
	
	/**
	 * 券名称
	 */
	private String ticketName;
	
	/**
	 * 开始时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date startTime;
	
	/**
	 * 结束时间
	 */
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date endTime;
	
	/**
	 * 金额
	 */
	private BigDecimal amount;
	
	/**
	 * 数量
	 */
	private int num;
	
	/**
	 * 使用数量
	 */
	private int usednum;
	
	/**
	 * 备注
	 */
	private String memo;

	public long getTicketAmountId() {
		return ticketAmountId;
	}

	public void setTicketAmountId(long ticketAmountId) {
		this.ticketAmountId = ticketAmountId;
	}

	public int getTicketType() {
		return ticketType;
	}

	public void setTicketType(int ticketType) {
		this.ticketType = ticketType;
	}

	public String getTicketName() {
		return ticketName;
	}

	public void setTicketName(String ticketName) {
		this.ticketName = ticketName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getUsednum() {
		return usednum;
	}

	public void setUsednum(int usednum) {
		this.usednum = usednum;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}
	
	

}
