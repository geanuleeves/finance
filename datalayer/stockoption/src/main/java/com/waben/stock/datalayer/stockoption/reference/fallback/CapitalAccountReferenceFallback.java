package com.waben.stock.datalayer.stockoption.reference.fallback;

import com.waben.stock.datalayer.stockoption.reference.CapitalAccountReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.FrozenCapitalDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalAccountQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CapitalAccountReferenceFallback implements CapitalAccountReference {

	@Override
	public Response<PageInfo<CapitalAccountDto>> pages(CapitalAccountQuery publisherQuery) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> fetchByPublisherSerialCode(String serialCode) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> fetchByPublisherId(Long publisherId) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> recharge(Long publisherId, BigDecimal amount) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> withdrawals(Long publisherId, String withdrawalsNo,
			String withdrawalsStateIndex) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> serviceFeeAndReserveFund(Long publisherId, Long buyRecordId,
			BigDecimal serviceFee, BigDecimal reserveFund, BigDecimal deferredFee) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<FrozenCapitalDto> fetchFrozenCapital(Long publisherId, Long buyRecordId) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> deferredCharges(Long publisherId, Long buyRecordId, BigDecimal deferredCharges) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> returnReserveFund(Long publisherId, Long buyRecordId, String buyRecordSerialCode,
			BigDecimal profitOrLoss) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<Void> modifyPaymentPassword(Long publisherId, String paymentPassword) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> fetchById(Long id) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}
	
	@Override
	public Response<CapitalAccountDto> revoke(Long publisherId, Long buyRecordId, BigDecimal serviceFee,
			BigDecimal deferredFee) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

	@Override
	public Response<CapitalAccountDto> modifyCapitalAccount(CapitalAccountDto capitalAccountDto) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}
	
	@Override
	public Response<CapitalAccountDto> returnDeferredFee(Long publisherId, Long buyRecordId, BigDecimal deferredFee) {
		throw new NetflixCircuitException(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION);
	}

}
