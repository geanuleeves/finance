package com.waben.stock.interfaces.service.stockoption;

import com.waben.stock.interfaces.dto.stockoption.StockOptionOrgDto;
import com.waben.stock.interfaces.pojo.Response;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * 期权第三方机构 reference服务接口
 *
 * @author lma
 */
@FeignClient(name = "stockoption", path = "stockoptionorg", qualifier = "stockOptionOrgInterface")
public interface StockOptionOrgInterface {

	@RequestMapping(value = "/", method = RequestMethod.GET)
	Response<List<StockOptionOrgDto>> lists();

}
