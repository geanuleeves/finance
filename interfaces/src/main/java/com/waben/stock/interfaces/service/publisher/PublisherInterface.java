package com.waben.stock.interfaces.service.publisher;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.pojo.Response;

/**
 * @author Created by yuyidi on 2017/11/12.
 * @desc
 */
public interface PublisherInterface {

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Response<PublisherDto> fetchById(@PathVariable("id") Long id);

	@RequestMapping(value = "/phone/{phone}", method = RequestMethod.GET)
	Response<PublisherDto> fetchByPhone(@PathVariable("phone") String phone);

	@RequestMapping(value = "/register", method = RequestMethod.POST)
	Response<PublisherDto> register(@RequestParam(name = "phone") String phone,
			@RequestParam(name = "password") String password, @RequestParam(name = "promoter") String promoter);

	@RequestMapping(value = "/{phone}/modifyPassword", method = RequestMethod.PUT)
	Response<PublisherDto> modifyPassword(@PathVariable("phone") String phone,
			@RequestParam(name = "password") String password);

}
