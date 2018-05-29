package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 期货风控类型
 * 
 * @author luomengan
 *
 */
public enum FuturesWindControlType implements CommonalityEnum {

	InitPosition("1", "初始持仓"),

	OvernightPosition("2", "隔夜持仓"),

	DayUnwind("3", "日内平仓"),

	UserApplyUnind("4", "用户申请平仓"),

	BackhandUnind("5", "市价反手平仓"),

	ReachProfitPoint("6", "达到止盈点"),

	ReachLossPoint("7", "达到止损点"),

	ReachContractExpiration("8", "合约到期平仓");

	private FuturesWindControlType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private String index;

	private String type;

	private static Map<String, FuturesWindControlType> valueMap = new HashMap<String, FuturesWindControlType>();

	static {
		for (FuturesWindControlType _enum : FuturesWindControlType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static FuturesWindControlType getByIndex(String index) {
		FuturesWindControlType result = valueMap.get(index);
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
