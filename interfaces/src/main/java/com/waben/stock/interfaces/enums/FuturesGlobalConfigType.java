package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

public enum FuturesGlobalConfigType implements CommonalityEnum {
	FROZEN("1", "提现冻结");
	
	private String index;

	private String type;
	
	private FuturesGlobalConfigType(String index, String type) {
		this.index = index;
		this.type = type;
	}
	
	private static Map<String, FuturesGlobalConfigType> valueMap = new HashMap<String, FuturesGlobalConfigType>();
	
	static {
		for (FuturesGlobalConfigType _enum : FuturesGlobalConfigType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}
	
	public static FuturesGlobalConfigType getByIndex(String index) {
		FuturesGlobalConfigType result = valueMap.get(index);
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + index);
		}
		return result;
	}

	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setIndex(String index) {
		this.index = index;
	}
	
	

}
