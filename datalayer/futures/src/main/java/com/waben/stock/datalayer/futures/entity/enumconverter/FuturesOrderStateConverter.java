package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesOrderState;

/**
 * 期货订单状态 转换器
 * 
 * @author lma
 *
 */
public class FuturesOrderStateConverter implements AttributeConverter<FuturesOrderState, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesOrderState attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesOrderState convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesOrderState.getByIndex(String.valueOf(dbData));
	}
}
