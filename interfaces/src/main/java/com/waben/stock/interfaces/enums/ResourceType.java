package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源 类型
 * 
 * @author lma
 *
 */
public enum ResourceType implements CommonalityEnum {

	BUYRECORD("1", "点买记录"),

	PUBLISHER("2", "发布人"),

	STOCKOPTIONTRADE("3", "期权交易记录"),

	ORGWITHDRAWALSAPPLY("4", "机构提现申请"),

	ORGANIZATION("5", "机构"),

	FUTURESORDER("6", "期货订单交易记录"),
	
	FUTURESOVERNIGHTRECORD("7", "期货订单过夜记录"),
	
	FUTURESTRADEENTRUST("8", "期货交易委托");

	private String index;
	private String type;

	private ResourceType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, ResourceType> valueMap = new HashMap<String, ResourceType>();

	static {
		for (ResourceType _enum : ResourceType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static ResourceType getByIndex(String index) {
		ResourceType result = valueMap.get(index);
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
