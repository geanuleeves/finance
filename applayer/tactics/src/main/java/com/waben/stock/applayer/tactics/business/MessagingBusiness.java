package com.waben.stock.applayer.tactics.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.message.MessagingDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.MessagingQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.message.MessagingInterface;

/**
 * 消息 Business
 * 
 * @author lma
 *
 */
@Service
public class MessagingBusiness {

	@Autowired
	@Qualifier("messagingInterface")
	private MessagingInterface service;

	public PageInfo<MessagingDto> pages(MessagingQuery messagingQuery) {
		Response<PageInfo<MessagingDto>> response = service.pages(messagingQuery);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	
	public MessagingDto readMessage(Long recipient, Long id) {
		Response<MessagingDto> response = service.readMessage(recipient, id);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
