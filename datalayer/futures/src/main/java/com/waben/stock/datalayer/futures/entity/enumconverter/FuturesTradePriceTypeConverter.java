package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesTradePriceType;

/**
 * 期权交易价格类型 转换器
 * 
 * @author lma
 *
 */
public class FuturesTradePriceTypeConverter implements AttributeConverter<FuturesTradePriceType, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesTradePriceType attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesTradePriceType convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesTradePriceType.getByIndex(String.valueOf(dbData));
	}
}
