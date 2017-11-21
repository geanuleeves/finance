package com.waben.stock.interfaces.service.manage;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.manage.CircularsDto;
import com.waben.stock.interfaces.pojo.Response;

public interface CircularsInterface {

	@RequestMapping(value = "/getByEnable", method = RequestMethod.GET)
	Response<List<CircularsDto>> getByEnable(boolean enable);

}
