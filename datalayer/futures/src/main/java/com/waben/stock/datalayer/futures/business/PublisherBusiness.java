package com.waben.stock.datalayer.futures.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;

/**
 * 发布人 Business
 * 
 * @author lma
 *
 */
@Service
public class PublisherBusiness {

	@Autowired
	@Qualifier("publisherInterface")
	private PublisherInterface publisherReference;

	public PublisherDto findById(Long publisherId) {
		Response<PublisherDto> response = publisherReference.fetchById(publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
