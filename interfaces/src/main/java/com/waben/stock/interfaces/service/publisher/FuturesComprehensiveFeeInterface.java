package com.waben.stock.interfaces.service.publisher;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.admin.publisher.FuturesComprehensiveFeeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.FuturesComprehensiveFeeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

@FeignClient(name = "publisher", path = "comprehensive", qualifier = "futuresComprehensiveFeeInterface")
public interface FuturesComprehensiveFeeInterface {

	@RequestMapping(value = "/saveComprehensiveFee", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesComprehensiveFeeDto> save(@RequestBody FuturesComprehensiveFeeDto t);
	
	@RequestMapping(value = "/modifyComprehensiveFee", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesComprehensiveFeeDto> modify(@RequestBody FuturesComprehensiveFeeDto t);
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<Long> delete(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/retrieve/{id}", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesComprehensiveFeeDto> retrieve(@PathVariable("id") Long id);
	
	@RequestMapping(value = "/pages", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesComprehensiveFeeDto>> page(@RequestBody FuturesComprehensiveFeeQuery query);
}
