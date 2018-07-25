package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesOrderType;

/**
 * 期货订单类型 转换器
 * 
 * @author lma
 *
 */
public class FuturesOrderTypeConverter implements AttributeConverter<FuturesOrderType, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesOrderType attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesOrderType convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesOrderType.getByIndex(String.valueOf(dbData));
	}
}
