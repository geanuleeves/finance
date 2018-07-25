package com.waben.stock.interfaces.service.stockoption;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.stockoption.StockOptionCycleDto;
import com.waben.stock.interfaces.pojo.Response;

/**
 * 期权周期 reference服务接口
 *
 * @author lma
 */
@FeignClient(name = "stockoption", path = "stockoptioncycle", qualifier = "stockOptionCycleInterface")
public interface StockOptionCycleInterface {

	@RequestMapping(value = "/lists", method = RequestMethod.GET)
	Response<List<StockOptionCycleDto>> lists();

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Response<StockOptionCycleDto> fetchById(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/cycle/{cycle}", method = RequestMethod.GET)
	Response<StockOptionCycleDto> fetchByCycle(@PathVariable("cycle") Integer cycle);

}
