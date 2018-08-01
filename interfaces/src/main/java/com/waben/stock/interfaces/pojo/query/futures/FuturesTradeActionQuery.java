package com.waben.stock.interfaces.pojo.query.futures;

import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import com.waben.stock.interfaces.pojo.query.PageAndSortQuery;

import java.util.Date;

/**
 * 订单交易开平仓记录
 *
 * @author chenk 2018/7/27
 */
public class FuturesTradeActionQuery extends PageAndSortQuery {

	private Long id;
	/** 发布人ID */
	private Long publisherId;
	/** 名称（品种名称+合约编号模糊查询） */
	private String name;
	/** 交易成交时间-查询开始时间 */
	private Date startTime;
	/** 交易成交时间-查询结束时间 */
	private Date endTime;
	/** 开平仓类型 */
	private FuturesTradeActionType tradeActionType;
	/** 成交状态 */
	private FuturesTradeEntrustState[] states;

	public FuturesTradeActionQuery() {
		super();
	}

	public FuturesTradeActionQuery(int page, int size) {
		super();
		super.setPage(page);
		super.setSize(size);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public FuturesTradeActionType getTradeActionType() {
		return tradeActionType;
	}

	public void setTradeActionType(FuturesTradeActionType tradeActionType) {
		this.tradeActionType = tradeActionType;
	}

	public Long getPublisherId() {
		return publisherId;
	}

	public void setPublisherId(Long publisherId) {
		this.publisherId = publisherId;
	}

	public FuturesTradeEntrustState[] getStates() {
		return states;
	}

	public void setStates(FuturesTradeEntrustState[] states) {
		this.states = states;
	}

}
