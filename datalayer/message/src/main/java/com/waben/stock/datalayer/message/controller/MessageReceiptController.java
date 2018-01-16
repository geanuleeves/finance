package com.waben.stock.datalayer.message.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.message.entity.MessageReceipt;
import com.waben.stock.datalayer.message.service.MessageReceiptService;
import com.waben.stock.interfaces.dto.message.MessageReceiptDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.MessageReceiptQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.message.MessageReceiptInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

@RestController
@RequestMapping("/messageReceipt")
public class MessageReceiptController implements MessageReceiptInterface{

	@Autowired
	private MessageReceiptService messageReceiptService;
	
	@Override
	public Response<MessageReceiptDto> addMessageReceipt(@RequestBody MessageReceiptDto messageReceiptDto) {
		return new Response<MessageReceiptDto>(CopyBeanUtils.copyBeanProperties(MessageReceiptDto.class, messageReceiptService.save(
				CopyBeanUtils.copyBeanProperties(MessageReceipt.class, messageReceiptDto, false)), false));
	}

	@Override
	public Response<Long> dropMessageReceipt(@PathVariable("messageReceiptId") Long messageReceiptId) {
		return new Response<>(messageReceiptService.remove(messageReceiptId));
	}

	@Override
	public Response<MessageReceiptDto> modifyMessageReceipt(@RequestBody MessageReceiptDto messageReceiptDto) {
		return new Response<MessageReceiptDto>(CopyBeanUtils.copyBeanProperties(MessageReceiptDto.class, messageReceiptService.revision(
				CopyBeanUtils.copyBeanProperties(MessageReceipt.class, messageReceiptDto, false)), false));
	}

	@Override
	public Response<MessageReceiptDto> fetchMessageReceiptById(@PathVariable("messageReceiptId") Long messageReceiptId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(MessageReceiptDto.class, messageReceiptService.findById(messageReceiptId), false));
	}

	@Override
	public Response<PageInfo<MessageReceiptDto>> pages(@RequestBody MessageReceiptQuery messageReceiptQuery) {
		Page<MessageReceipt> pages = messageReceiptService.pages(messageReceiptQuery);
		PageInfo<MessageReceiptDto> result = new PageInfo<>(pages, MessageReceiptDto.class);
		return new Response<>(result);
	}

	
	
}
