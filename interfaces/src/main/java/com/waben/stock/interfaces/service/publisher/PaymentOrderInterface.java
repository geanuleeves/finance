package com.waben.stock.interfaces.service.publisher;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.publisher.PaymentOrderDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.PaymentOrderQuery;

/**
 * 支付订单 reference服务接口
 *
 * @author luomengan
 */
@FeignClient(name = "publisher", path = "paymentorder", qualifier = "paymentOrderInterface")
public interface PaymentOrderInterface {
	
	@RequestMapping(value = "/{paymentNo}", method = RequestMethod.GET)
	Response<PaymentOrderDto> fetchByPaymentNo(@PathVariable("paymentNo") String paymentNo);
	
	@RequestMapping(value = "/id/{id}", method = RequestMethod.GET)
	Response<PaymentOrderDto> fetchById(@PathVariable("id") Long id);

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<PaymentOrderDto> addPaymentOrder(@RequestBody PaymentOrderDto paymentOrderDto);
	
	@RequestMapping(value = "/", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<PaymentOrderDto> modifyPaymentOrder(@RequestBody PaymentOrderDto paymentOrderDto);

	@RequestMapping(value = "/{paymentNo}", method = RequestMethod.PUT)
	public Response<PaymentOrderDto> changeState(@PathVariable("paymentNo") String paymentNo,
			@RequestParam("stateIndex") String stateIndex);
	
	@RequestMapping(value = "/pages", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response<PageInfo<PaymentOrderDto>> pagesByQuery(@RequestBody PaymentOrderQuery query);

}
