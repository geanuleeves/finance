package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 期货交易价格 类型
 * 
 * @author lma
 *
 */
public enum FuturesTradePriceType implements CommonalityEnum {

	MKT("1", "市价", "MKT"),

	LMT("2", "限价", "LMT");

	private String index;

	private String type;

	private String value;

	private FuturesTradePriceType(String index, String type, String value) {
		this.index = index;
		this.type = type;
		this.value = value;
	}

	private static Map<String, FuturesTradePriceType> valueMap = new HashMap<String, FuturesTradePriceType>();

	static {
		for (FuturesTradePriceType _enum : FuturesTradePriceType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesTradePriceType getByIndex(String index) {
		FuturesTradePriceType result = valueMap.get(index);
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
