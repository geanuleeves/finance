package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;

/**
 * 交易委托状态 转换器
 * 
 * @author lma
 *
 */
public class FuturesTradeEntrustStateConverter implements AttributeConverter<FuturesTradeEntrustState, Integer> {

	/**
	 * 将枚举类型转换成数据库字段值
	 */
	@Override
	public Integer convertToDatabaseColumn(FuturesTradeEntrustState attribute) {
		if (attribute == null) {
			return null;
		}
		return Integer.parseInt(attribute.getIndex());
	}

	/**
	 * 将数据库字段值转换成枚举
	 */
	@Override
	public FuturesTradeEntrustState convertToEntityAttribute(Integer dbData) {
		if (dbData == null) {
			return null;
		}
		return FuturesTradeEntrustState.getByIndex(String.valueOf(dbData));
	}
}
