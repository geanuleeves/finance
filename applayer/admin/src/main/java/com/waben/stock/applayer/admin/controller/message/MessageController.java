package com.waben.stock.applayer.admin.controller.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.admin.business.message.MessageBusiness;
import com.waben.stock.interfaces.dto.message.MessagingDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.MessagingQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/message")
@Api(description = "消息通知")
public class MessageController {

	@Autowired
	private MessageBusiness business;
	
	
	@GetMapping("/page")
	@ApiOperation(value = "获取消息通知")
	public Response<PageInfo<MessagingDto>> page(MessagingQuery messagingQuery){
		return new Response<>(business.page(messagingQuery));
	}
	
	@RequestMapping(value = "/addMessaging", method = RequestMethod.POST)
	@ApiOperation(value = "新增消息通知")
	public Response<MessagingDto> addMessaging(MessagingDto dto){
		return new Response<>(business.addMessaging(dto));
	}
}
