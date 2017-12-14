package com.waben.stock.applayer.strategist.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.strategist.service.StrategyTypeService;
import com.waben.stock.interfaces.dto.stockcontent.StrategyTypeDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;

/**
 * 策略类型 Business
 * 
 * @author luomengan
 *
 */
@Service
public class StrategyTypeBusiness {

	@Autowired
	private StrategyTypeService service;

	public List<StrategyTypeDto> lists() {
		Response<List<StrategyTypeDto>> response = service.lists(true);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
