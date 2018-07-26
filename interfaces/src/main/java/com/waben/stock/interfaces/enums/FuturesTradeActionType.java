package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易开平仓 类型
 * 
 * @author lma
 *
 */
public enum FuturesTradeActionType implements CommonalityEnum {

	OPEN("1", "开仓"),

	CLOSE("2", "平仓");

	private String index;

	private String type;

	private FuturesTradeActionType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, FuturesTradeActionType> valueMap = new HashMap<String, FuturesTradeActionType>();

	static {
		for (FuturesTradeActionType _enum : FuturesTradeActionType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesTradeActionType getByIndex(String index) {
		FuturesTradeActionType result = valueMap.get(index);
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
