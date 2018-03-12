package com.waben.stock.applayer.promotion.business;

import com.waben.stock.applayer.promotion.service.organization.PromotionStockOptionTradeService;
import com.waben.stock.interfaces.dto.organization.PromotionStockOptionTradeDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.PromotionStockOptionTradeQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 推广渠道产生的期权交易 Business
 * 
 * @author luomengan
 *
 */
@Service
public class PromotionStockOptionTradeBusiness {

	@Autowired
	@Qualifier("promotionStockOptionTradeReference")
	private PromotionStockOptionTradeService reference;

	public PageInfo<PromotionStockOptionTradeDto> adminPage(PromotionStockOptionTradeQuery query) {
		Response<PageInfo<PromotionStockOptionTradeDto>> response = reference.adminPage(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
