package com.waben.stock.futuresgateway.yisheng.controller;

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

import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.pojo.Response;
import com.waben.stock.futuresgateway.yisheng.service.FuturesCommodityService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货品种 Controller
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/futuresCommodity")
@Api(description = "期货品种接口列表")
public class FuturesCommodityController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public FuturesCommodityService futuresCommodityService;

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取期货品种")
	public Response<FuturesCommodity> fetchById(@PathVariable Long id) {
		return new Response<>(futuresCommodityService.getFuturesCommodityInfo(id));
	}

	@GetMapping("/page")
	@ApiOperation(value = "获取期货品种分页数据")
	public Response<Page<FuturesCommodity>> futuresCommoditys(int page, int limit) {
		return new Response<>((Page<FuturesCommodity>) futuresCommodityService.futuresCommoditys(page, limit));
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取期货品种列表")
	public Response<List<FuturesCommodity>> list() {
		return new Response<>(futuresCommodityService.list());
	}

	/******************************** 后台管理 **********************************/

	@PostMapping("/")
	@ApiOperation(value = "添加期货品种", hidden = true)
	public Response<FuturesCommodity> addition(FuturesCommodity futuresCommodity) {
		return new Response<>(futuresCommodityService.addFuturesCommodity(futuresCommodity));
	}

	@PutMapping("/")
	@ApiOperation(value = "修改期货品种", hidden = true)
	public Response<FuturesCommodity> modification(FuturesCommodity futuresCommodity) {
		return new Response<>(futuresCommodityService.modifyFuturesCommodity(futuresCommodity));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除期货品种", hidden = true)
	public Response<Long> delete(@PathVariable Long id) {
		futuresCommodityService.deleteFuturesCommodity(id);
		return new Response<Long>(id);
	}

	@PostMapping("/deletes")
	@ApiOperation(value = "批量删除期货品种（多个id以逗号分割）", hidden = true)
	public Response<Boolean> deletes(String ids) {
		futuresCommodityService.deleteFuturesCommoditys(ids);
		return new Response<Boolean>(true);
	}

	@GetMapping("/adminList")
	@ApiOperation(value = "获取期货品种列表(后台管理)", hidden = true)
	public Response<List<FuturesCommodity>> adminList() {
		return new Response<>(futuresCommodityService.list());
	}

}
