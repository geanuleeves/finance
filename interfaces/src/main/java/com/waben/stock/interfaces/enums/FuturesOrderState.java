package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 期权订单状态 类型
 * 
 * @author luomengan
 *
 */
public enum FuturesOrderState implements CommonalityEnum {
	
	Posted("1", "已发布"),

	BuyingEntrust("2", "买入委托"),

	BuyingCanceled("3", "取消买入委托"),

	BuyingFailure("4", "买入委托失败"),

	PartPosition("5", "部分买入成功"),

	Position("6", "持仓中"),

	SellingEntrust("7", "卖出委托"),

	PartUnwind("8", "部分已平仓"),

	Unwind("9", "已平仓");

	private String index;

	private String type;

	private FuturesOrderState(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, FuturesOrderState> valueMap = new HashMap<String, FuturesOrderState>();

	static {
		for (FuturesOrderState _enum : FuturesOrderState.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesOrderState getByIndex(String index) {
		FuturesOrderState result = valueMap.get(index);
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
