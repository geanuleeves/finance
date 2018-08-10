package com.waben.stock.applayer.admin.business.buyrecord;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.reference.StrategyTypeReference;
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
	@Qualifier("strategyTypeReference")
	private StrategyTypeReference service;

	public List<StrategyTypeDto> listsAll() {
		Response<List<StrategyTypeDto>> response = service.lists(true);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<StrategyTypeDto> lists() {
		Response<List<StrategyTypeDto>> response = service.lists(true);
		if ("200".equals(response.getCode())) {
			List<StrategyTypeDto> result = new ArrayList<>();
			if (response.getResult() != null && response.getResult().size() > 0) {
				for (StrategyTypeDto type : response.getResult()) {
					if (type.getServiceFeePerWan().compareTo(new BigDecimal(0)) > 0) {
						result.add(type);
					}
				}
			}
			return result;
		}
		throw new ServiceException(response.getCode());
	}

	public StrategyTypeDto retriveExperienceStrategyType() {
		Response<List<StrategyTypeDto>> response = service.lists(true);
		if ("200".equals(response.getCode())) {
			StrategyTypeDto result = null;
			if (response.getResult() != null && response.getResult().size() > 0) {
				for (StrategyTypeDto type : response.getResult()) {
					if (type.getId().intValue() == 3) {
						result = type;
					}
				}
			}
			return result;
		}
		throw new ServiceException(response.getCode());
	}

	public Map<Long, Integer> strategyTypeMap() {
		Map<Long, Integer> result = new HashMap<Long, Integer>();
		List<StrategyTypeDto> list = this.lists();
		if (list != null && list.size() > 0) {
			for (StrategyTypeDto strategy : list) {
				result.put(strategy.getId(), strategy.getCycle());
			}
		}
		return result;
	}
	
	public Map<Long, String> strategyNameMap() {
		Map<Long, String> result = new HashMap<Long, String>();
		List<StrategyTypeDto> list = this.lists();
		if (list != null && list.size() > 0) {
			for (StrategyTypeDto strategy : list) {
				result.put(strategy.getId(), strategy.getName());
			}
		}
		return result;
	}

}