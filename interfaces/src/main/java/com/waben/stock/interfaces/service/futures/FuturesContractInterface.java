package com.waben.stock.interfaces.service.futures;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.admin.futures.FuturesContractAdminDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesContractAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractQuery;

@FeignClient(name = "futures", path = "contract", qualifier = "futurescontractInterface")
public interface FuturesContractInterface {

	/**
	 * 查询期货合约数据
	 * 
	 * @param futuresContractQuery
	 *            查询条件
	 * @return 期货合约
	 */
	@RequestMapping(value = "/pages", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesContractDto>> pagesContract(@RequestBody FuturesContractQuery futuresContractQuery);

	@RequestMapping(value = "/pagesContractAdmin", method = RequestMethod.POST, consumes = "application/json")
	Response<PageInfo<FuturesContractAdminDto>> pagesContractAdmin(@RequestBody FuturesContractAdminQuery query);

	/**
	 * 添加期货合约数据
	 * 
	 * @param contractDto
	 * @return
	 */
	@RequestMapping(value = "/saveContract", method = RequestMethod.POST, consumes = "application/json")
	Response<FuturesContractAdminDto> addContract(@RequestBody FuturesContractAdminDto contractDto);

	/**
	 * 修改期货合约数据
	 * 
	 * @param contractDto
	 * @return
	 */
	@RequestMapping(value = "/modifyContract", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesContractAdminDto> modifyContract(@RequestBody FuturesContractAdminDto contractDto);

	/**
	 * 删除期货合约数据
	 * 
	 * @param id
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	Response<String> deleteContract(@PathVariable(value = "id") Long id);

	/**
	 * 根据合约ID获取期货合约信息
	 * 
	 * @param contractId
	 *            合约ID
	 * @return 合约信息
	 */
	@RequestMapping(value = "/contract/{contractId}", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<FuturesContractDto> findByContractId(@PathVariable(value = "contractId") Long contractId);

	@RequestMapping(value = "/contract/isEnable", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<String> isCurrent(@RequestParam(value = "id") Long id);

	@RequestMapping(value = "/lists/{commodityId}", method = RequestMethod.GET)
	Response<List<FuturesContractDto>> listByCommodityId(@PathVariable(value = "commodityId") Long commodityId);

	@RequestMapping(value = "/listAll", method = RequestMethod.GET)
	Response<List<FuturesContractDto>> list();

}
