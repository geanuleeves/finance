package com.waben.stock.datalayer.publisher.controller;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.publisher.entity.CapitalAccount;
import com.waben.stock.datalayer.publisher.service.CapitalAccountService;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.FrozenCapitalDto;
import com.waben.stock.interfaces.enums.WithdrawalsState;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.CapitalAccountQuery;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.JacksonUtil;

/**
 * 资金账户 Controller
 *
 * @author luomengan
 */
@RestController
@RequestMapping("/capitalAccount")
public class CapitalAccountController implements CapitalAccountInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CapitalAccountService capitalAccountService;

	@Override
	public Response<PageInfo<CapitalAccountDto>> pages(@RequestBody CapitalAccountQuery capitalAccountQuery) {
		Page<CapitalAccount> pages = capitalAccountService.pages(capitalAccountQuery);
		System.out.println(JacksonUtil.encode(pages));
		PageInfo<CapitalAccountDto> result = new PageInfo<>(pages, CapitalAccountDto.class);
		for (int i = 0; i < pages.getContent().size(); i++) {
			result.getContent().get(i).setPublisherId(pages.getContent().get(i).getPublisher().getId());
		}
		return new Response<>(result);
	}

	@Override
	public Response<CapitalAccountDto> fetchByPublisherSerialCode(@PathVariable String publisherSerialCode) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.findByPublisherSerialCode(publisherSerialCode), false));
	}

	@Override
	public Response<CapitalAccountDto> fetchByPublisherId(@PathVariable Long publisherId) {
		CapitalAccount response = capitalAccountService.findByPublisherId(publisherId);
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, response, false));
	}

	@Override
	public Response<CapitalAccountDto> serviceFeeAndReserveFund(@PathVariable Long publisherId,
			@PathVariable Long buyRecordId, @PathVariable BigDecimal serviceFee, @PathVariable BigDecimal reserveFund,
			BigDecimal deferredFee) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, capitalAccountService
				.serviceFeeAndReserveFund(publisherId, buyRecordId, serviceFee, reserveFund, deferredFee), false));
	}

	@Override
	public Response<CapitalAccountDto> returnReserveFund(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			String buyRecordSerialCode, @PathVariable BigDecimal profitOrLoss) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.returnReserveFund(publisherId, buyRecordId, buyRecordSerialCode, profitOrLoss),
				false));
	}

	@Override
	public Response<CapitalAccountDto> recharge(@PathVariable Long publisherId, @PathVariable BigDecimal amount,
			Long rechargeId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.recharge(publisherId, amount, rechargeId), false));
	}

	@Override
	public Response<CapitalAccountDto> csa(@PathVariable Long publisherId, @PathVariable BigDecimal amount,
			Long withdrawalsId) {
		CapitalAccount response = capitalAccountService.csa(publisherId, amount, withdrawalsId);
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, response, false));
	}

	@Override
	public Response<CapitalAccountDto> withdrawals(@PathVariable Long publisherId, Long withdrawalsId,
			String withdrawalsStateIndex) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, capitalAccountService
				.withdrawals(publisherId, withdrawalsId, WithdrawalsState.getByIndex(withdrawalsStateIndex)), false));
	}

	@Override
	public Response<CapitalAccountDto> deferredCharges(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			@PathVariable BigDecimal deferredCharges) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.deferredCharges(publisherId, buyRecordId, deferredCharges), false));
	}

	@Override
	public Response<Void> modifyPaymentPassword(@PathVariable Long publisherId, String paymentPassword) {
		capitalAccountService.modifyPaymentPassword(publisherId, paymentPassword);
		return new Response<>();
	}

	@Override
	public Response<FrozenCapitalDto> fetchFrozenCapital(@PathVariable Long publisherId,
			@PathVariable Long buyRecordId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(FrozenCapitalDto.class,
				capitalAccountService.findFrozenCapital(publisherId, buyRecordId), false));
	}

	@Override
	public Response<CapitalAccountDto> fetchById(@PathVariable Long capitalAccountId) {
		CapitalAccount account = capitalAccountService.findById(capitalAccountId);
		CapitalAccountDto accountDto = CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, account, false);
		accountDto.setPublisherId(account.getPublisher().getId());
		return new Response<>(accountDto);
	}

	@Override
	public Response<CapitalAccountDto> modifyCapitalAccount(@RequestBody CapitalAccountDto capitalAccountDto) {
		CapitalAccount capitalAccount = CopyBeanUtils.copyBeanProperties(CapitalAccount.class, capitalAccountDto,
				false);
		CapitalAccount account = capitalAccountService.revision(capitalAccount);
		CapitalAccountDto accountDto = CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class, account, false);
		return new Response<CapitalAccountDto>(accountDto);
	}

	@Override
	public Response<CapitalAccountDto> revoke(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			@PathVariable BigDecimal serviceFee, BigDecimal deferredFee) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.revoke(publisherId, buyRecordId, serviceFee, deferredFee), false));
	}

	@Override
	public Response<CapitalAccountDto> returnDeferredFee(@PathVariable Long publisherId, @PathVariable Long buyRecordId,
			@PathVariable BigDecimal deferredFee) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.returnDeferredFee(publisherId, buyRecordId, deferredFee), false));
	}

	/**************************************** 期权相关 ***************************************/
	@Override
	public Response<CapitalAccountDto> rightMoney(@PathVariable Long publisherId, @PathVariable Long optionTradeId,
			@PathVariable BigDecimal rightMoney) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.rightMoney(publisherId, optionTradeId, rightMoney), false));
	}

	@Override
	public Response<CapitalAccountDto> returnRightMoney(@PathVariable Long publisherId,
			@PathVariable Long optionTradeId, @PathVariable BigDecimal rightMoney) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.returnRightMoney(publisherId, optionTradeId, rightMoney), false));
	}

	@Override
	public Response<CapitalAccountDto> optionProfit(@PathVariable Long publisherId, @PathVariable Long optionTradeId,
			@PathVariable BigDecimal profit) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(CapitalAccountDto.class,
				capitalAccountService.optionProfit(publisherId, optionTradeId, profit), false));
	}

}
