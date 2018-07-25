package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 期货交易限制 类型
 * 
 * @author lma
 *
 */
public enum FuturesTradeLimitType implements CommonalityEnum {

	LimitOpenwind("1", "限制开仓"),

	LimitUnwind("2", "限制平仓");

	private String index;

	private String type;

	private FuturesTradeLimitType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, FuturesTradeLimitType> valueMap = new HashMap<String, FuturesTradeLimitType>();

	static {
		for (FuturesTradeLimitType _enum : FuturesTradeLimitType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesTradeLimitType getByIndex(String index) {
		FuturesTradeLimitType result = valueMap.get(index);
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
