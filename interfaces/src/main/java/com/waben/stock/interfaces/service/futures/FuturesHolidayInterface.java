package com.waben.stock.interfaces.service.futures;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.admin.futures.FuturesHolidayDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesHolidayQuery;

@FeignClient(name = "futures", path = "holiday", qualifier = "futuresHolidayInterface")
public interface FuturesHolidayInterface {

	/**
	 * 新增节假日管理
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/addHoliday", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesHolidayDto> addHoliday(@RequestBody FuturesHolidayDto dto);
	
	/**
	 * 修改节假日管理
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/modityHoliday", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesHolidayDto> modityHoliday(@RequestBody FuturesHolidayDto dto);
	
	/**
	 * 按ID查询节假日管理
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/featchById", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesHolidayDto> featchById(@RequestBody FuturesHolidayQuery query);
	
	/**
	 * 启动
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/isEnable", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesHolidayDto> isEnable(@RequestBody FuturesHolidayQuery query);
	
	/**
	 * 按ID删除节假日管理
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<String> delete(@RequestBody FuturesHolidayQuery query);
	
	/**
	 * 分页查询节假日管理
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/page", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesHolidayDto>> page(@RequestBody FuturesHolidayQuery query);
}
