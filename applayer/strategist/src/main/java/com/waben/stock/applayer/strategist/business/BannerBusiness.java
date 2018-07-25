package com.waben.stock.applayer.strategist.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.manage.BannerDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.BannerQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.manage.BannerInterface;

/**
 * 轮播图 Business
 * 
 * @author lma
 *
 */
@Service
public class BannerBusiness {

	@Autowired
	@Qualifier("bannerInterface")
	private BannerInterface bannerReference;

	public PageInfo<BannerDto> pages(BannerQuery query) {
		Response<PageInfo<BannerDto>> response = bannerReference.pages(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
