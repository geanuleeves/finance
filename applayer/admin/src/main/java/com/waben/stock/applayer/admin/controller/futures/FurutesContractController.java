package com.waben.stock.applayer.admin.controller.futures;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

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

import com.waben.stock.applayer.admin.business.futures.FuturesContractBusiness;
import com.waben.stock.interfaces.dto.admin.futures.FuturesContractAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesTermAdminDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractTermDto;
import com.waben.stock.interfaces.dto.futures.FuturesExchangeDto;
import com.waben.stock.interfaces.dto.manage.StaffDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesExchangeAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 期货设置Controller
 * @author pengzhenliang
 *
 */
@RestController
@RequestMapping("/futuresContract")
@Api(description="期货设置")
public class FurutesContractController {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FuturesContractBusiness business;
	
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
    @ApiOperation(value = "新增期货市场")
    public Response<FuturesExchangeDto> save(FuturesExchangeDto query){
		FuturesExchangeDto result = business.save(query);
        return new Response<>(result);
    }
	
	@GetMapping("/pagesExchange")
    @ApiOperation(value = "新增期货市场")
	public Response<PageInfo<FuturesExchangeDto>> pagesExchange(FuturesExchangeAdminQuery query){
		PageInfo<FuturesExchangeDto> response = business.pagesExchange(query);
		return new Response<>(response);
	}
	
	@PutMapping("/modify")
    @ApiOperation(value = "修改期货市场")
    public Response<FuturesExchangeDto> modify(FuturesExchangeDto exchangeDto){
		FuturesExchangeDto result = business.modify(exchangeDto);
        return new Response<>(result);
    }
	
	@DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除期货市场")
    public Response<Integer> delete(@PathVariable("id") Long id){
        business.delete(id);
        return new Response<>(1);
    }
	
	@RequestMapping(value = "/futuresContract/save", method = RequestMethod.POST)
	@ApiOperation(value = "添加期货品种")
	public Response<FuturesContractAdminDto> savec(FuturesContractAdminDto query){
		FuturesContractAdminDto result = business.save(query);
		return new Response<>(result);
	}
	
	@PutMapping("/futuresContract/modify")
	@ApiOperation(value = "修改期货品种")
	public Response<FuturesContractAdminDto> modifyc(FuturesContractAdminDto query){
		FuturesContractAdminDto result = business.modify(query);
		return new Response<>(result);
	}
	
	@DeleteMapping("/futuresContract/delete/{id}")
    @ApiOperation(value = "删除期货品种")
    public Response<Integer> deleteContract(@PathVariable("id") Long id){
        business.deleteContract(id);
        return new Response<>(1);
    }
	
	
}
