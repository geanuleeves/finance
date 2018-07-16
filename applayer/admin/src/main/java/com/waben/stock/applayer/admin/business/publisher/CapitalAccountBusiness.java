package com.waben.stock.applayer.admin.business.publisher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.business.futures.FuturesTradeBusiness;
import com.waben.stock.applayer.admin.security.CustomUserDetails;
import com.waben.stock.applayer.admin.security.SecurityUtil;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderMarketDto;
import com.waben.stock.interfaces.dto.admin.publisher.CapitalAccountAdminDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.publisher.CapitalAccountAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesOrderInterface;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

/**
 * 资金账户 Business
 * 
 * @author luomengan
 */
@Service
public class CapitalAccountBusiness {

	@Autowired
	@Qualifier("capitalAccountInterface")
	private CapitalAccountInterface reference;
	
	@Autowired
	@Qualifier("futuresOrderInterface")
	private FuturesOrderInterface futuresOrderInterface;
	
	@Autowired
	private FuturesTradeBusiness business;
	
	public PageInfo<CapitalAccountAdminDto> adminPagesByQuery(CapitalAccountAdminQuery query) {
		Response<PageInfo<CapitalAccountAdminDto>> response = reference.adminPagesByQuery(query);
		if ("200".equals(response.getCode())) {
			List<CapitalAccountAdminDto> dtolist = response.getResult().getContent();
		    for (CapitalAccountAdminDto dto : dtolist) {
		    	FuturesOrderQuery orderQuery = new FuturesOrderQuery();
				FuturesOrderState[] states = { FuturesOrderState.Unwind };
				orderQuery.setStates(states);
				orderQuery.setPage(0);
				orderQuery.setSize(Integer.MAX_VALUE);
				orderQuery.setStartBuyingTime(getCurrentDay());
				orderQuery.setPublisherId(dto.getPublisherId());
				List<FuturesOrderMarketDto> list = business.pageOrderMarket(orderQuery).getContent();
				BigDecimal totalIncome = new BigDecimal(0);
				for (FuturesOrderMarketDto futuresOrderMarketDto : list) {
					totalIncome = totalIncome.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
				}
				dto.setDaySettledProfit(totalIncome.setScale(2, RoundingMode.DOWN));
				
				FuturesOrderState[] statesp = { FuturesOrderState.Position };
				orderQuery.setStates(statesp);
				List<FuturesOrderMarketDto> listp = business.pageOrderMarket(orderQuery).getContent();
				BigDecimal totalIncomep = new BigDecimal(0);
				for (FuturesOrderMarketDto futuresOrderMarketDto : listp) {
					totalIncomep = totalIncomep.add(futuresOrderMarketDto.getPublisherProfitOrLoss());
				}
				dto.setDayHoldingProfit(totalIncomep.setScale(2, RoundingMode.DOWN));
			}
			
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto revisionState(Long id, Integer state) {
		Response<CapitalAccountDto> response = reference.modifyState(id, state);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto revisionAccount(Long id, BigDecimal availableBalance, String remarket) {
		CustomUserDetails userDetails = SecurityUtil.getUserDetails();
		Response<CapitalAccountDto> response = reference.modifyAccount(userDetails.getUserId(),id, availableBalance,remarket);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	private Date getCurrentDay() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return cal.getTime();
	}
	
}
