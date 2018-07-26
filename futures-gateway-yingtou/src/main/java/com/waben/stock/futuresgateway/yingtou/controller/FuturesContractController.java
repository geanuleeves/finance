package com.waben.stock.futuresgateway.yingtou.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;
import com.waben.stock.futuresgateway.yingtou.pojo.Response;
import com.waben.stock.futuresgateway.yingtou.service.FuturesContractService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货合约 Controller
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/contract")
@Api(description = "期货合约接口列表")
public class FuturesContractController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public FuturesContractService futuresContractService;

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取期货合约")
	public Response<FuturesContract> fetchById(@PathVariable Long id) {
		return new Response<>(futuresContractService.getContractInfo(id));
	}

	@GetMapping("/checksymbol/{symbol}")
	@ApiOperation(value = "根据合约标识获取合约是否可用")
	public Response<FuturesContract> fetchBySymbol(@PathVariable String symbol) {
		return new Response<>(futuresContractService.getContractInfoByEnableAndSymbol(symbol));
	}

	@GetMapping("/page")
	@ApiOperation(value = "获取期货合约分页数据")
	public Response<Page<FuturesContract>> futuresContracts(int page, int limit) {
		return new Response<>((Page<FuturesContract>) futuresContractService.futuresContracts(page, limit));
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取期货合约列表")
	public Response<List<FuturesContract>> list() {
		return new Response<>(futuresContractService.list());
	}

	/******************************** 后台管理 **********************************/

	@PostMapping("/")
	@ApiOperation(value = "添加期货合约", hidden = true)
	public Response<FuturesContract> addition(FuturesContract futuresContract) {
		return new Response<>(futuresContractService.addContract(futuresContract));
	}

	@PutMapping("/")
	@ApiOperation(value = "修改期货合约", hidden = true)
	public Response<FuturesContract> modification(FuturesContract futuresContract) {
		return new Response<>(futuresContractService.modifyContract(futuresContract));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除期货合约", hidden = true)
	public Response<Long> delete(@PathVariable Long id) {
		futuresContractService.deleteContract(id);
		return new Response<Long>(id);
	}

	@PostMapping("/deletes")
	@ApiOperation(value = "批量删除期货合约（多个id以逗号分割）", hidden = true)
	public Response<Boolean> deletes(String ids) {
		futuresContractService.deleteContracts(ids);
		return new Response<Boolean>(true);
	}

	@GetMapping("/adminList")
	@ApiOperation(value = "获取期货合约列表(后台管理)", hidden = true)
	public Response<List<FuturesContract>> adminList() {
		return new Response<>(futuresContractService.list());
	}

}
