package com.waben.stock.applayer.admin.controller.futures;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.futures.FuturesContractBusiness;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesGatewayContract;
import com.waben.stock.interfaces.dto.admin.futures.FuturesContractAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesPreQuantityDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesExchangeDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesContractAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesExchangeAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesPreQuantityQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货设置Controller
 * 
 * @author pengzhenliang
 *
 */
@RestController
@RequestMapping("/futuresContract")
@Api(description = "期货设置")
public class FurutesContractController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesContractBusiness business;

	@RequestMapping(value = "/lists/{commodityId}", method = RequestMethod.GET)
	@ApiOperation(value = "根据品种ID获取合约")
	public Response<List<FuturesContractDto>> listByCommodityId(@PathVariable("commodityId") Long commodityId) {
		return new Response<>(business.listByCommodityId(commodityId));
	}

	@GetMapping("/getContractTime")
	@ApiOperation(value = "获取合约")
	public Response<FuturesGatewayContract> getContractTime(String contractNo, String commodityNo) {
		return business.getcontractTime(contractNo, commodityNo);
	}
	
	@GetMapping("/enableContractNo")
	@ApiOperation(value = "根据品种ID获取合约编号")
	public Response<List<String>> enableContractNo(String commodityNo){
		return business.enableContractNo(commodityNo);
	}

	@GetMapping("/queryPreQuantity")
	@ApiOperation(value = "设置预置手数")
	public Response<PageInfo<FuturesPreQuantityDto>> pagePre(FuturesPreQuantityQuery query) {
		return new Response<>(business.queryPre(query));
	}

	@PutMapping("/isContractEnable")
	@ApiOperation(value = "启用/停用合约")
	public Response<String> isEnable(Long id) {
		return business.isEnable(id);
	}

	@GetMapping("/pagesContractAdmin")
	@ApiOperation(value = "查询合约")
	public Response<PageInfo<FuturesContractAdminDto>> pageContracntAdmin(FuturesContractAdminQuery query) {
		return new Response<>(business.pagesContractAdmin(query));
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ApiOperation(value = "新增期货市场")
	public Response<FuturesExchangeDto> save(FuturesExchangeDto query) {
		FuturesExchangeDto result = business.save(query);
		return new Response<>(result);
	}

	@GetMapping("/pagesExchange")
	@ApiOperation(value = "查询期货市场")
	public Response<PageInfo<FuturesExchangeDto>> pagesExchange(FuturesExchangeAdminQuery query) {
		PageInfo<FuturesExchangeDto> response = business.pagesExchange(query);
		return new Response<>(response);
	}

	@PutMapping("/modify")
	@ApiOperation(value = "修改期货市场")
	public Response<FuturesExchangeDto> modify(FuturesExchangeDto exchangeDto) {
		FuturesExchangeDto result = business.modify(exchangeDto);
		return new Response<>(result);
	}

	@DeleteMapping("/delete/{id}")
	@ApiOperation(value = "删除期货市场")
	public Response<String> delete(@PathVariable("id") Long id) {
		return business.delete(id);
	}

	@RequestMapping(value = "/futuresContract/save", method = RequestMethod.POST)
	@ApiOperation(value = "添加合约")
	public Response<FuturesContractAdminDto> savec(FuturesContractAdminDto query) {
		FuturesContractAdminDto result = business.save(query);
		return new Response<>(result);
	}

	@PostMapping("/futuresContract/modify")
	@ApiOperation(value = "修改合约")
	public Response<FuturesContractAdminDto> modifyc(FuturesContractAdminDto query) {
		FuturesContractAdminDto result = business.modify(query);
		return new Response<>(result);
	}

	@DeleteMapping("/futuresContract/delete/{id}")
	@ApiOperation(value = "删除合约")
	public Response<String> deleteContract(@PathVariable("id") Long id) {
		return business.deleteContract(id);
	}

}
