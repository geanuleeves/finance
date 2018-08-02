package com.waben.stock.applayer.admin.controller.publisher;

import java.math.BigDecimal;

import com.waben.stock.applayer.admin.util.PhoneUtil;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalFlowAdminDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.publisher.FuturesComprehensiveFeeBusiness;
import com.waben.stock.interfaces.dto.admin.publisher.FuturesComprehensiveFeeDto;
import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.FuturesComprehensiveFeeQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/futurescomprehensiveFee")
@Api(description = "发布人")
public class FuturesComprehensiveFeeController {

	@Autowired
	private FuturesComprehensiveFeeBusiness business;
	
	@GetMapping("/pages")
	@ApiOperation(value = "查询提现审核记录")
	public Response<PageInfo<WithdrawalsOrderDto>> page(FuturesComprehensiveFeeQuery query){
		PageInfo<WithdrawalsOrderDto> pageInfo = business.page(query);
		for(WithdrawalsOrderDto dto : pageInfo.getContent()) {
			dto.setPublisherPhone(PhoneUtil.encodedPhone(dto.getPublisherPhone()));
		}
		return new Response<>(pageInfo);
	}
	
	@GetMapping("/wbWithdrawalsAdmin")
	@ApiOperation(value = "审核")
	public Response<WithdrawalsOrderDto> wbWithdrawalsAdmin(WithdrawalsOrderDto compre){
		return new Response<>(business.wbWithdrawalsAdmin(compre));
	}
	
	@GetMapping("/wbWithdrawalsAdminCancle")
	@ApiOperation(value = "审核拒绝")
	public Response<WithdrawalsOrderDto> wbWithdrawalsAdminCancle(WithdrawalsOrderDto compre){
		return new Response<>(business.wbWithdrawalsAdminCancle(compre));
	}
	
	@GetMapping("/getSumOrder")
	@ApiOperation(value = "金额")
	public Response<String> getSumOrder(FuturesComprehensiveFeeQuery query){
		String count  = business.getSumOrder(query);
		Response<String> response = new Response<>();
		response.setCode("200");
		response.setMessage("响应成功");
		response.setResult(count);
		return response;
	}
}
