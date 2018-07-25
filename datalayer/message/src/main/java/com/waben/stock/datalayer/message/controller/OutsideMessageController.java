package com.waben.stock.datalayer.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.message.service.OutsideMessageService;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.message.OutsideMessage;
import com.waben.stock.interfaces.service.message.OutsideMessageInterface;

/**
 * 站外消息 Controller
 *
 * @author lma
 */
@RestController
@RequestMapping("/outsidemsg")
public class OutsideMessageController implements OutsideMessageInterface {

	@Autowired
	private OutsideMessageService servcie;

	@Override
	public Response<String> send(@RequestBody OutsideMessage message) {
		servcie.send(message);
		return new Response<String>();
	}

	@Override
	public Response<String> sendAll(@RequestBody OutsideMessage message) {
		servcie.sendAll(message);
		return new Response<String>();
	}

}
