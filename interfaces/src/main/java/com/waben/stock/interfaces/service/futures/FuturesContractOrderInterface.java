package com.waben.stock.interfaces.service.futures;

import com.waben.stock.interfaces.dto.futures.FuturesContractOrderDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractOrderViewDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 合约订单
 * 
 * @author chenk 2018/7/26
 */
@FeignClient(name = "futures", path = "contract_order", qualifier = "futuresContractOrderInterface")
public interface FuturesContractOrderInterface {

	/**
	 * 根据ID获取合约订单
	 * 
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Response<FuturesContractOrderDto> fetchById(@PathVariable("id") Long id);

	/**
	 * 根据合约ID获取合约订单
	 * 
	 * @param contractId
	 *            合约ID
	 * @return 订单
	 */
	@RequestMapping(value = "/contractId/{contractId}/{publisherId}", method = RequestMethod.GET)
	Response<FuturesContractOrderDto> fetchByContractIdAndPublisherId(@PathVariable("contractId") Long contractId, @PathVariable("publisherId") Long publisherId);

	/**
	 * 新增合约订单
	 *
	 * @param dto
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesContractOrderDto> save(@RequestBody FuturesContractOrderDto dto);

	/**
	 * 修改合约订单
	 *
	 * @param dto
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/modify", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesContractOrderDto> modify(@RequestBody FuturesContractOrderDto dto);

	/**
	 * 删除合约订单
	 *
	 * @param id
	 *            订单ID
	 * @return 订单
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<String> delete(@PathVariable("id") Long id);

	/**
	 * 根据条件查询合约订单
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/pages", method = RequestMethod.GET)
	Response<PageInfo<FuturesContractOrderViewDto>> pagesAdmin(@RequestBody FuturesContractOrderQuery query);

}
