package com.waben.stock.applayer.admin.controller.futures;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.futures.FuturesCurrencyRateBusiness;
import com.waben.stock.applayer.admin.business.futures.FuturesTradeLimitBusiness;
import com.waben.stock.interfaces.dto.admin.futures.FuturesGlobalConfigDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTradeLimitDto;
import com.waben.stock.interfaces.dto.admin.futures.PutForwardDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesGlobalConfigQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeLimitQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/futuresConRisk")
@Api(description="期货风控")
public class FurutesRiskController {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FuturesCurrencyRateBusiness business;
	
	@Autowired
	private FuturesTradeLimitBusiness limitBusiness;
	
	@RequestMapping(value = "/currencyRate/save", method = RequestMethod.POST)
    @ApiOperation(value = "新增汇率")
    public Response<FuturesCurrencyRateDto> save(FuturesCurrencyRateDto query){
		FuturesCurrencyRateDto result = business.save(query);
        return new Response<>(result);
    }
	
	@RequestMapping(value = "/futuresGlobalConfig/Frozen", method = RequestMethod.POST)
    @ApiOperation(value = "提现冻结设置")
	public Response<FuturesGlobalConfigDto> saveAndModify(FuturesGlobalConfigDto global){
		FuturesGlobalConfigDto result = limitBusiness.saveAndModify(global);
		return new Response<>(result);
	}
	
	@RequestMapping(value = "/futuresGlobalConfig/queryFrozen", method = RequestMethod.POST)
    @ApiOperation(value = "查询提现冻结设置")
	public Response<PageInfo<FuturesGlobalConfigDto>> pageConfig(FuturesGlobalConfigQuery query){
		PageInfo<FuturesGlobalConfigDto> result = limitBusiness.pageConfig(query);
		return new Response<>(result);
	}
	
	@RequestMapping(value = "/currencyRate/modify", method = RequestMethod.POST)
    @ApiOperation(value = "修改汇率")
    public Response<FuturesCurrencyRateDto> modify(FuturesCurrencyRateDto exchangeDto){
		FuturesCurrencyRateDto result = business.modify(exchangeDto);
        return new Response<>(result);
    }
	
	@PutMapping(value = "/currencyRate/delete/{id}")
    @ApiOperation(value = "删除汇率")
    public Response<Integer> delete(@PathVariable("id") Long id){
        business.deleteCurrencyRate(id);
        return new Response<>(1);
    }
	
	@RequestMapping(value = "/currencyRate/listAll", method = RequestMethod.POST)
    @ApiOperation(value = "查询所有汇率信息")
	public Response<PageInfo<FuturesCurrencyRateDto>> list(){
		return new Response<>(business.list());
	}
	
	@GetMapping(value = "/pagePutForward")
    @ApiOperation(value = "查询提现设置")
	public Response<PageInfo<PutForwardDto>> pagePutForward(){
		return business.pagesPutForward();
	}
	
	@RequestMapping(value = "/saveAndModify", method = RequestMethod.POST)
    @ApiOperation(value = "设置提现时间")
	public Response<PutForwardDto> saveAndModify(PutForwardDto dto){
		return new Response<>(business.saveAndModify(dto));
	}
	
	@RequestMapping(value = "/tradeLimit/addLimit", method = RequestMethod.POST)
	@ApiOperation(value = "添加合约风控")
	public Response<FuturesTradeLimitDto> addLimit(FuturesTradeLimitDto limit){
		return new Response<>(limitBusiness.save(limit));
	}
	
	@PutMapping(value = "/tradeLimit/modifyLimit")
	@ApiOperation(value = "修改合约风控")
	public Response<FuturesTradeLimitDto> modifyLimit(FuturesTradeLimitDto limit){
		return new Response<>(limitBusiness.modify(limit));
	}
	
	@DeleteMapping(value = "/tradeLimit/delete/{id}")
	@ApiOperation(value = "删除合约风控")
	public Response<Integer> deleteLimit(@PathVariable("id") Long id){
		limitBusiness.delete(id);
		return new Response<>(1);
	}
	
	@GetMapping(value = "/tradeLimit/pagesLimit")
	@ApiOperation("查询合约风控")
	public Response<PageInfo<FuturesTradeLimitDto>> pagesLimit(FuturesTradeLimitQuery query){
		return new Response<>(limitBusiness.pagesLimiet(query));
	}
}
