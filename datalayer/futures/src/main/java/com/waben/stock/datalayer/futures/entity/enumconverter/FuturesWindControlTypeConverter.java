package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesWindControlType;

/**
 * 期权风控类型 转换器
 * 
 * @author lma
 *
 */
public class FuturesWindControlTypeConverter implements AttributeConverter<FuturesWindControlType, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesWindControlType attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesWindControlType convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesWindControlType.getByIndex(String.valueOf(dbData));
	}
}
