package com.waben.stock.applayer.tactics.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.WithdrawalsOrderQuery;
import com.waben.stock.interfaces.service.publisher.WithdrawalsOrderInterface;

@Service
public class WithdrawalsOrderBusiness {

	@Autowired
	@Qualifier("withdrawalsOrderInterface")
	private WithdrawalsOrderInterface withdrawalsOrderReference;

	public PageInfo<WithdrawalsOrderDto> pagesByQuery(WithdrawalsOrderQuery query) {
		Response<PageInfo<WithdrawalsOrderDto>> response = withdrawalsOrderReference.pagesByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
