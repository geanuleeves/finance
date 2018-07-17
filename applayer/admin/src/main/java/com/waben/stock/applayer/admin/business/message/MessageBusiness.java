package com.waben.stock.applayer.admin.business.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.message.MessagingDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.MessagingQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.message.MessagingInterface;

@Service
public class MessageBusiness {

	@Autowired
	private MessagingInterface reference;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public PageInfo<MessagingDto> page(MessagingQuery messagingQuery){
		Response<PageInfo<MessagingDto>> response = reference.pages(messagingQuery);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	public MessagingDto addMessaging(MessagingDto dto){
		Response<MessagingDto> response = reference.addMessaging(dto);
		if("200".equals(response.getCode())){
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
