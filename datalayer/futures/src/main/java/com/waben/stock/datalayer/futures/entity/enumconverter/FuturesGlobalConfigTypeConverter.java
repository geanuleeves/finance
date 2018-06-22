package com.waben.stock.datalayer.futures.entity.enumconverter;

import javax.persistence.AttributeConverter;

import com.waben.stock.interfaces.enums.FuturesGlobalConfigType;

public class FuturesGlobalConfigTypeConverter implements AttributeConverter<FuturesGlobalConfigType, Integer> {

	@Override
	public Integer convertToDatabaseColumn(FuturesGlobalConfigType attribute) {
		return Integer.parseInt(attribute.getIndex());
	}

	@Override
	public FuturesGlobalConfigType convertToEntityAttribute(Integer dbData) {
		return FuturesGlobalConfigType.getByIndex(String.valueOf(dbData));
	}

}
