package com.waben.stock.interfaces.service.publisher;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalFlowQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

public interface CapitalFlowInterface {

	/**
	 * 分页查询资金流水
	 * 
	 * @param query
	 *            查询条件
	 * @return 资金流水
	 */
	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<CapitalFlowDto>> pagesByQuery(@RequestBody CapitalFlowQuery query);

}
