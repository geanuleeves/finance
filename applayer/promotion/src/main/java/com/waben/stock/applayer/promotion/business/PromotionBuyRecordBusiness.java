package com.waben.stock.applayer.promotion.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.promotion.reference.organization.PromotionBuyRecordReference;
import com.waben.stock.interfaces.dto.organization.PromotionBuyRecordDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.PromotionBuyRecordQuery;

/**
 * 推广渠道产生的策略 Business
 * 
 * @author luomengan
 *
 */
@Service
public class PromotionBuyRecordBusiness {

	@Autowired
	@Qualifier("promotionBuyRecordReference")
	private PromotionBuyRecordReference reference;

	public PageInfo<PromotionBuyRecordDto> adminPage(PromotionBuyRecordQuery query) {
		Response<PageInfo<PromotionBuyRecordDto>> response = reference.adminPage(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
