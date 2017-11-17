package com.waben.stock.applayer.tactics.service.fallback;

import java.util.List;

import org.springframework.stereotype.Component;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.stockcontent.StockDto;
import com.waben.stock.interfaces.dto.stockcontent.StockRecommendDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.service.StockInterface;

/**
 * 轮播 断路器回调
 * 
 * @author luomengan
 *
 */
@Component
public class StockServiceFallback implements StockInterface {

	@Override
	public List<StockDto> selectStock(String keyword) {
		throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION);
	}

	@Override
	public List<StockRecommendDto> getStockRecommendList() {
		throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION);
	}

}
