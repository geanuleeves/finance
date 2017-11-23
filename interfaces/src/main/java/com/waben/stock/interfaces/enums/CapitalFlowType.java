package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 资金流水 类型
 * 
 * @author luomengan
 *
 */
public enum CapitalFlowType implements CommonalityEnum {

	Recharge("1", "充值"),

	Withdrawals("2", "提现"),

	ServiceFee("3", "信息服务费"),

	CompensateMoney("4", "赔付保证金"),

	DeferredCharges("5", "递延费"),

	ReturnCompensate("6", "退回赔付保证金"),

	Loss("7", "亏损"),

	Profit("8", "盈利");

	private String index;

	private String type;

	private CapitalFlowType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, CapitalFlowType> valueMap = new HashMap<String, CapitalFlowType>();

	static {
		for (CapitalFlowType _enum : CapitalFlowType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static CapitalFlowType getByIndex(String index) {
		CapitalFlowType result = valueMap.get(index);
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
