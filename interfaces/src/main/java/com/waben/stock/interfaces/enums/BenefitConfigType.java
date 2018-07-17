package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 分成配置类型
 * 
 * @author luomengan
 *
 */
public enum BenefitConfigType implements CommonalityEnum {

	ServiceFee("1", "服务费分成配置"),

	DeferredFee("2", "递延费分成配置"),

	RightMoney("3", "权利金收益分成配置"),

	FuturesOpeningFee("4", "期货开仓分成配置"),

	FuturesCloseFee("5", "期货平仓分成配置"),

	FuturesDeferredFee("6", "期货递延分成配置"),

	FuturesComprehensiveFee("7", "期货交易综合费分成");

	private String index;
	private String type;

	BenefitConfigType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, BenefitConfigType> valueMap = new HashMap<>();

	static {
		for (BenefitConfigType _enum : BenefitConfigType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static BenefitConfigType getByIndex(String index) {
		BenefitConfigType result = valueMap.get(index);
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + index);
		}
		return result;
	}

	@Override
	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}
}
