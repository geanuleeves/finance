package com.waben.stock.interfaces.service.futures;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.admin.futures.FuturesGlobalConfigDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesGlobalConfigQuery;

@FeignClient(name = "futures", path = "globalConfig", qualifier = "futuresGlobalConfigInterface")
public interface FuturesGlobalConfigInterface {

	@RequestMapping(value = "/addGlobalConfig", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesGlobalConfigDto> addConfig(@RequestBody FuturesGlobalConfigDto query);
	
	@RequestMapping(value = "/saveAndModify", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesGlobalConfigDto> saveAndModify(@RequestBody FuturesGlobalConfigDto query);
	
	@RequestMapping(value = "/modifyGlobalConfig", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesGlobalConfigDto> modifyConfig(@RequestBody FuturesGlobalConfigDto query);
	
	@RequestMapping(value = "/globalConfig/delete/{id}", method = RequestMethod.GET)
	void deleteConfig(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/pagesGlobal", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesGlobalConfigDto>> pagesConfig(@RequestBody FuturesGlobalConfigQuery query);
	
	@RequestMapping(value = "/findGolbalConfig", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesGlobalConfigDto>> findAll();
}
