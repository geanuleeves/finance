package com.waben.stock.datalayer.futures.business;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.FrozenCapitalDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;

@Service
public class CapitalAccountBusiness {

	@Autowired
	@Qualifier("capitalAccountInterface")
	private CapitalAccountInterface service;

	public CapitalAccountDto fetchByPublisherId(Long publisherId) {
		Response<CapitalAccountDto> response = service.fetchByPublisherId(publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto futuresOrderOvernight(Long publisherId, Long overnightId, BigDecimal deferredFee,
			BigDecimal reserveFund) {
		Response<CapitalAccountDto> response = service.futuresOrderOvernight(publisherId, overnightId, deferredFee,
				reserveFund);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto futuresReturnOvernightReserveFund(Long publisherId, Long overnightId,
			BigDecimal reserveFund) {
		Response<CapitalAccountDto> response = service.futuresReturnOvernightReserveFund(publisherId, overnightId,
				reserveFund);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto futuresOrderServiceFeeAndReserveFund(Long publisherId, Long orderId, BigDecimal serviceFee,
			BigDecimal reserveFund) {
		Response<CapitalAccountDto> response = service.futuresOrderServiceFeeAndReserveFund(publisherId, orderId,
				serviceFee, reserveFund);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FrozenCapitalDto futuresOrderFetchFrozenCapital(Long publisherId, Long orderId) {
		Response<FrozenCapitalDto> response = service.futuresOrderFetchFrozenCapital(publisherId, orderId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto futuresOrderSettlement(Long publisherId, Long entrustId, BigDecimal profitOrLoss) {
		Response<CapitalAccountDto> response = service.futuresOrderSettlement(publisherId, entrustId, profitOrLoss);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto futuresOrderRevoke(Long publisherId, Long orderId, BigDecimal serviceFee) {
		Response<CapitalAccountDto> response = service.futuresOrderRevoke(publisherId, orderId, serviceFee);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public CapitalAccountDto futuresReturnReserveFund(Long publisherId, Long contractOrderId, BigDecimal reserveFund) {
		Response<CapitalAccountDto> response = service.futuresReturnReserveFund(publisherId, contractOrderId,
				reserveFund);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
