package com.waben.stock.applayer.strategist.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.strategist.reference.StrategyTypeReference;
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
	private StrategyTypeReference strategyTypeReference;

	public List<StrategyTypeDto> lists() {
		Response<List<StrategyTypeDto>> response = strategyTypeReference.lists(true);
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
		Response<List<StrategyTypeDto>> response = strategyTypeReference.lists(true);
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

}
