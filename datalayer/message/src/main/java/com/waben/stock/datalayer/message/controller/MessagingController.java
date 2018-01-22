package com.waben.stock.datalayer.message.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.message.entity.Messaging;
import com.waben.stock.datalayer.message.service.MessagingService;
import com.waben.stock.interfaces.dto.message.MessagingDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.MessagingQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.message.MessagingInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

/**
 * 
 * @author Created by hujian on 2018/1/3.
 *
 */
@RestController
@RequestMapping("/messaging")
public class MessagingController implements MessagingInterface{

	@Autowired
	private MessagingService messagingService;
	
	@Override
	public Response<MessagingDto> addMessaging(@RequestBody MessagingDto messagingDto) {
		Messaging messaging = CopyBeanUtils.copyBeanProperties(Messaging.class, messagingDto, false);
		Messaging resultMessaging = messagingService.save(messaging);
		return new Response<>(CopyBeanUtils.copyBeanProperties(MessagingDto.class,resultMessaging, false));
	}

	@Override
	public Response<Long> dropMessaging(@PathVariable("messagingId") Long messagingId) {
		return new Response<>(messagingService.remove(messagingId));
	}

	@Override
	public Response<MessagingDto> modifyMessaging(@RequestBody MessagingDto messagingDto) {
		Messaging messaging = CopyBeanUtils.copyBeanProperties(Messaging.class, messagingDto, false);
		Messaging resultMessaging = messagingService.revision(messaging);
		return new Response<>(CopyBeanUtils.copyBeanProperties(MessagingDto.class,resultMessaging, false));
	}

	@Override
	public Response<MessagingDto> fetchMessagingById(@PathVariable("messagingId") Long messagingId) {
		Messaging messaging = messagingService.findById(messagingId);
		return new Response<>(CopyBeanUtils.copyBeanProperties(MessagingDto.class, messaging,false));
	}

	@Override
	public Response<PageInfo<MessagingDto>> pages(@RequestBody MessagingQuery messagingQuery) {
		Page<Messaging> pages = messagingService.pages(messagingQuery);
		PageInfo<MessagingDto> result = new PageInfo<>(pages, MessagingDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<List<MessagingDto>> fetchNotProduceReceiptAllByRecipient(@PathVariable("recipient") String recipient) {
		List<Messaging> pages = messagingService.findNotProduceReceiptMessage(recipient);
		List<MessagingDto> result = CopyBeanUtils.copyListBeanPropertiesToList(pages,MessagingDto.class);
		return new Response<>(result);
	}

	
}
