package com.waben.stock.datalayer.publisher.controller;

import java.math.BigDecimal;

import com.waben.stock.datalayer.publisher.entity.CapitalAccount;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.pojo.query.CapitalAccountQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.publisher.service.CapitalAccountService;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.FrozenCapitalDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

/**
 * 资金账户 Controller
 * 
 * @author luomengan
 *
 */
@RestController
@RequestMapping("/capitalAccount")
public class CapitalAccountController implements CapitalAccountInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CapitalAccountService capitalAccountService;

	@Override
	public Response<PageInfo<CapitalAccountDto>> pages(CapitalAccountQuery capitalAccountQuery) {
		Page<CapitalAccount> pages = capitalAccountService.pages(capitalAccountQuery);
		PageInfo<CapitalAccountDto> result = new PageInfo<>(pages, CapitalAccountDto.class);
		return new Response<>(result);
	}

	@Override
	public Response<CapitalAccountDto> fetchByPublisherSerialCode(@PathVariable String publisherSerialCode) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.findByPublisherSerialCode(publisherSerialCode), false));
	}

	@Override
	public Response<CapitalAccountDto> fetchByPublisherId(@PathVariable Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.findByPublisherId(publisherId), false));
	}

	@Override
	public Response<CapitalAccountDto> serviceFeeAndReserveFund(@PathVariable Long publisherId,
			@PathVariable Long buyRecordId, String buyRecordSerialCode, @PathVariable BigDecimal serviceFee,
			@PathVariable BigDecimal reserveFund) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, capitalAccountService
				.serviceFeeAndReserveFund(publisherId, buyRecordId, buyRecordSerialCode, serviceFee, reserveFund),
				false));
	}

	@Override
	public Response<CapitalAccountDto> returnReserveFund(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			String buyRecordSerialCode, @PathVariable BigDecimal profitOrLoss) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.returnReserveFund(publisherId, buyRecordId, buyRecordSerialCode, profitOrLoss),
				false));
	}

	@Override
	public Response<CapitalAccountDto> recharge(@PathVariable Long publisherId, @PathVariable BigDecimal amount) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.recharge(publisherId, amount), false));
	}

	@Override
	public Response<CapitalAccountDto> withdrawals(@PathVariable Long publisherId, @PathVariable BigDecimal amount) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.withdrawals(publisherId, amount), false));
	}

	@Override
	public Response<CapitalAccountDto> deferredCharges(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			@PathVariable BigDecimal deferredCharges) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.deferredCharges(publisherId, buyRecordId, deferredCharges), false));
	}

	@Override
	public Response<String> modifyPaymentPassword(@PathVariable Long publisherId, String paymentPassword) {
		capitalAccountService.modifyPaymentPassword(publisherId, paymentPassword);
		return new Response<>("successful");
	}

	@Override
	public Response<FrozenCapitalDto> fetchFrozenCapital(@PathVariable Long publisherId,
			@PathVariable Long buyRecordId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FrozenCapitalDto.class,
				capitalAccountService.findFrozenCapital(publisherId, buyRecordId), false));
	}

}
