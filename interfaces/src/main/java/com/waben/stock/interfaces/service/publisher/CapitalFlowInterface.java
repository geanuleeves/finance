package com.waben.stock.interfaces.service.publisher;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowAdminDto;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowFuturesAdminDto;
import com.waben.stock.interfaces.dto.publisher.CapitalFlowDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalFlowQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalFlowAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalFlowFuturesAdminQuery;

/**
 * 资金流水 reference服务接口
 * 
 * @author luomengan
 *
 */
@FeignClient(name = "publisher", path = "capitalFlow", qualifier = "capitalFlowInterface")
public interface CapitalFlowInterface {

	/**
	 * 分页查询资金流水（管理后台）
	 * 
	 * @param query
	 *            查询条件
	 * @return 资金流水
	 */
	@RequestMapping(value = "/adminpages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<CapitalFlowAdminDto>> adminPagesByQuery(@RequestBody CapitalFlowAdminQuery query);
	
	/**
	 * 分页查询资金流水（管理后台-期货）
	 * 
	 * @param query
	 *            查询条件
	 * @return 资金流水
	 */
	@RequestMapping(value = "/adminFuturesPagesByQuery", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<CapitalFlowFuturesAdminDto>> adminFuturesPagesByQuery(@RequestBody CapitalFlowFuturesAdminQuery query);

	/**
	 * 查询流水累积金额（管理后台-期货）
	 * 
	 * @param query
	 *            查询条件
	 * @return 资金流水累积金额
	 */
	@RequestMapping(value = "/adminAccumulateFuturesAmount", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BigDecimal> adminAccumulateFuturesAmountByQuery(@RequestBody CapitalFlowFuturesAdminQuery query);
	
	/**
	 * 查询流水累积金额（管理后台）
	 * 
	 * @param query
	 *            查询条件
	 * @return 资金流水累积金额
	 */
	@RequestMapping(value = "/adminAccumulateAmount", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<BigDecimal> adminAccumulateAmountByQuery(@RequestBody CapitalFlowAdminQuery query);

	/**
	 * 分页查询资金流水
	 * 
	 * @param query
	 *            查询条件
	 * @return 资金流水
	 */
	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<CapitalFlowDto>> pagesByQuery(@RequestBody CapitalFlowQuery query);

	/**
	 * 获取推广赚取的总佣金
	 * 
	 * @param publisherId
	 *            发布人ID
	 * @return 推广赚取的总佣金
	 */
	@RequestMapping(value = "/{publisherId}/promotion/amount", method = RequestMethod.GET)
	Response<BigDecimal> promotionTotalAmount(@PathVariable("publisherId") Long publisherId);

	/**
	 * 根据流水ID获取流水信息
	 * 
	 * @param capitalFlowId
	 * @return
	 */
	@RequestMapping(value = "/{capitalFlowId}/view/", method = RequestMethod.GET)
	Response<CapitalFlowDto> fetchById(@PathVariable("capitalFlowId") Long capitalFlowId);

	/**
	 * 根据产生流水的对象信息获取流水
	 * 
	 * @param extendTypeIndex
	 *            产生流水的对象类型
	 * @param extendId
	 *            产生流水的对象ID
	 * @return 流水列表
	 */
	@RequestMapping(value = "/fetchByExtend", method = RequestMethod.GET)
	Response<List<CapitalFlowDto>> fetchByExtendTypeAndExtendId(@RequestParam("extendTypeIndex") String extendTypeIndex,
			@RequestParam("extendId") Long extendId);

}
