package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 期货订单类型 类型
 * 
 * @author lma
 *
 */
public enum FuturesOrderType implements CommonalityEnum {

	BuyUp("1", "买涨"),

	BuyFall("2", "买跌");

	private String index;

	private String type;

	private FuturesOrderType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, FuturesOrderType> valueMap = new HashMap<String, FuturesOrderType>();

	static {
		for (FuturesOrderType _enum : FuturesOrderType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesOrderType getByIndex(String index) {
		FuturesOrderType result = valueMap.get(index);
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
