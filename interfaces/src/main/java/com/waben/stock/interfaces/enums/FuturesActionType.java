package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 期货交易动作 类型
 * 
 * @author lma
 *
 */
public enum FuturesActionType implements CommonalityEnum {

	BUY("1", "买入"),

	SELL("2", "卖出");

	private String index;

	private String type;

	private FuturesActionType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, FuturesActionType> valueMap = new HashMap<String, FuturesActionType>();

	static {
		for (FuturesActionType _enum : FuturesActionType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesActionType getByIndex(String index) {
		FuturesActionType result = valueMap.get(index);
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
