package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesTradeLimitType;

/**
 * 期货交易限制 转换器
 * 
 * @author lma
 *
 */
public class FuturesTradeLimitTypeConverter implements AttributeConverter<FuturesTradeLimitType, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesTradeLimitType attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesTradeLimitType convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesTradeLimitType.getByIndex(String.valueOf(dbData));
	}
}
