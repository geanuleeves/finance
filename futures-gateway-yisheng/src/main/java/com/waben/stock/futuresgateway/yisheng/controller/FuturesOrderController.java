package com.waben.stock.futuresgateway.yisheng.controller;

import java.math.BigDecimal;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;
import com.waben.stock.futuresgateway.yisheng.pojo.Response;
import com.waben.stock.futuresgateway.yisheng.service.FuturesOrderService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 期货订单 Controller
 * 
 * @author lma
 *
 */
@RestController
@RequestMapping("/futuresOrder")
@Api(description = "期货订单接口列表")
public class FuturesOrderController {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public FuturesOrderService futuresOrderService;

	@GetMapping("/{id}")
	@ApiOperation(value = "根据id获取期货订单")
	public Response<FuturesOrder> fetchById(@PathVariable Long id) {
		return new Response<>(futuresOrderService.getFuturesOrderInfo(id));
	}

	@GetMapping("/page")
	@ApiOperation(value = "获取期货订单分页数据")
	public Response<Page<FuturesOrder>> futuresOrders(int page, int limit) {
		return new Response<>((Page<FuturesOrder>) futuresOrderService.futuresOrders(page, limit));
	}

	@GetMapping("/list")
	@ApiOperation(value = "获取期货订单列表")
	public Response<List<FuturesOrder>> list() {
		return new Response<>(futuresOrderService.list());
	}

	@GetMapping("/checkConnection")
	@ApiOperation(value = "检查API连接是否已连接")
	public Response<Boolean> checkConnection() {
		return new Response<>(futuresOrderService.isConnected());
	}

	@PostMapping("/")
	@ApiOperation(value = "期货下单")
	public Response<FuturesOrder> palceOrder(@RequestParam(required = true) String domain,
			@RequestParam(required = true) String commodityNo, @RequestParam(required = true) String contractNo,
			@RequestParam(required = true) Long outerOrderId, @RequestParam(required = true) String action,
			@RequestParam(required = true) BigDecimal totalQuantity, @RequestParam(required = true) Integer orderType,
			BigDecimal entrustPrice) {
		return new Response<>(futuresOrderService.placeOrder(domain, commodityNo, contractNo, outerOrderId, action,
				totalQuantity, orderType, entrustPrice));
	}

	@PostMapping("/cancalOrder/{domain}/{gatewayOrderId}")
	@ApiOperation(value = "期货取消订单")
	public Response<FuturesOrder> cancalOrder(@PathVariable String domain, @PathVariable Long gatewayOrderId) {
		return new Response<>(futuresOrderService.cancelOrder(domain, gatewayOrderId));
	}

	/******************************** 后台管理 **********************************/

	@PutMapping("/")
	@ApiOperation(value = "修改期货订单", hidden = true)
	public Response<FuturesOrder> modification(FuturesOrder futuresOrder) {
		return new Response<>(futuresOrderService.modifyFuturesOrder(futuresOrder));
	}

	@DeleteMapping("/{id}")
	@ApiOperation(value = "删除期货订单", hidden = true)
	public Response<Long> delete(@PathVariable Long id) {
		futuresOrderService.deleteFuturesOrder(id);
		return new Response<Long>(id);
	}

	@PostMapping("/deletes")
	@ApiOperation(value = "批量删除期货订单（多个id以逗号分割）", hidden = true)
	public Response<Boolean> deletes(String ids) {
		futuresOrderService.deleteFuturesOrders(ids);
		return new Response<Boolean>(true);
	}

	@GetMapping("/adminList")
	@ApiOperation(value = "获取期货订单列表(后台管理)", hidden = true)
	public Response<List<FuturesOrder>> adminList() {
		return new Response<>(futuresOrderService.list());
	}

}
