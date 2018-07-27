package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesTradeActionType;

/**
 * 订单开平仓 类型 转换器
 * 
 * @author lma
 *
 */
public class FuturesTradeActionTypeConverter implements AttributeConverter<FuturesTradeActionType, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesTradeActionType attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesTradeActionType convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesTradeActionType.getByIndex(String.valueOf(dbData));
	}
}
