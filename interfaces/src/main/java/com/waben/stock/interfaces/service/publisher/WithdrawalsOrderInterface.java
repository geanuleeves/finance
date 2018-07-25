package com.waben.stock.interfaces.service.publisher;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.WithdrawalsOrderQuery;

/**
 * 支提现订单 reference服务接口
 *
 * @author lma
 */
@FeignClient(name = "publisher", path = "withdrawalsorder", qualifier = "withdrawalsOrderInterface")
public interface WithdrawalsOrderInterface {

	@RequestMapping(value = "/{withdrawalsNo}", method = RequestMethod.GET)
	Response<WithdrawalsOrderDto> fetchByWithdrawalsNo(@PathVariable("withdrawalsNo") String withdrawalsNo);
	@RequestMapping(value = "fetchById/{id}", method = RequestMethod.GET)
	Response<WithdrawalsOrderDto> fetchById(@PathVariable("id") Long id);

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<WithdrawalsOrderDto> addWithdrawalsOrder(@RequestBody WithdrawalsOrderDto withdrawalsOrderDto);
	
	@RequestMapping(value = "/addWithdrawalsOrderAdmin", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<WithdrawalsOrderDto> addWithdrawalsOrderAdmin(@RequestBody WithdrawalsOrderDto withdrawalsOrderDto);

	@RequestMapping(value = "/{withdrawalsNo}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<WithdrawalsOrderDto> saveWithdrawalsOrders(@RequestBody WithdrawalsOrderDto withdrawalsOrderDto,@PathVariable("withdrawalsNo") String withdrawalsNo);
	
	@RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<WithdrawalsOrderDto> modifyWithdrawalsOrder(@RequestBody WithdrawalsOrderDto withdrawalsOrderDto);

	@RequestMapping(value = "/{withdrawalsNo}", method = RequestMethod.PUT)
	public Response<WithdrawalsOrderDto> changeState(@PathVariable("withdrawalsNo") String withdrawalsNo,
			@RequestParam("stateIndex") String stateIndex);
	
	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<PageInfo<WithdrawalsOrderDto>> pagesByQuery(@RequestBody WithdrawalsOrderQuery query);
	
	@RequestMapping(value = "/getSumOrder", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<String> getSumOrder(@RequestBody WithdrawalsOrderQuery query);
	
	@RequestMapping(value = "/refuse/{id}", method = RequestMethod.PUT)
	public Response<WithdrawalsOrderDto> refuse(@PathVariable("id") Long id, @RequestParam("remark") String remark);

}
