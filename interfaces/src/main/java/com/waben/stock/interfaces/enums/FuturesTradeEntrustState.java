package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易委托状态 类型
 * 
 * @author lma
 *
 */
public enum FuturesTradeEntrustState implements CommonalityEnum {

	Queuing("1", "排队中"),

	Canceled("2", "委托取消"),

	Failure("3", "委托失败"),
	
	PartSuccess("4", "部分委托成功"),
	
	Success("5", "委托成功");

	private String index;

	private String type;

	private FuturesTradeEntrustState(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, FuturesTradeEntrustState> valueMap = new HashMap<String, FuturesTradeEntrustState>();

	static {
		for (FuturesTradeEntrustState _enum : FuturesTradeEntrustState.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesTradeEntrustState getByIndex(String index) {
		FuturesTradeEntrustState result = valueMap.get(index);
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + index);
		}
		return result;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
