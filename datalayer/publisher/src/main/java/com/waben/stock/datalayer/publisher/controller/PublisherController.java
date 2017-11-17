package com.waben.stock.datalayer.publisher.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.publisher.service.PublisherService;
import com.waben.stock.interfaces.dto.BindCardDto;
import com.waben.stock.interfaces.dto.PublisherCapitalAccountDto;
import com.waben.stock.interfaces.dto.PublisherDto;
import com.waben.stock.interfaces.service.PublisherInterface;
import com.waben.stock.interfaces.pojo.Response;

/**
 * @author Created by yuyidi on 2017/11/5.
 * @desc
 */
@RestController
@RequestMapping("/publisher")
public class PublisherController implements PublisherInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private PublisherService publisherService;

	@Override
	public Response<PublisherDto> findById(@PathVariable Long id) {
		logger.info("获取发布策略人信息:{}", id);
		return new Response<>(publisherService.findById(id));
	}

	@Override
	public Response<PublisherCapitalAccountDto> register(String phone, String password) {
		return new Response<>(publisherService.register(phone, password));
	}

	@Override
	public Response<PublisherCapitalAccountDto> getCurrent(String serialCode) {
		return new Response<>(publisherService.findBySerialCode(serialCode));
	}

	@Override
	public Response<PublisherCapitalAccountDto> modifyPassword(String phone, String password) {
		return new Response<>(publisherService.modifyPassword(phone, password));
	}

	@Override
	public Response<PublisherDto> findByPhone(String phone) {
		return new Response<>(publisherService.findByPhone(phone));
	}

	@Override
	public Response<String> modifyPaymentPassword(String serialCode, String paymentPassword) {
		publisherService.modifyPaymentPassword(serialCode, paymentPassword);
		return new Response<>("修改支付密码成功");
	}

	@Override
	public Response<BindCardDto> bindBankCard(String serialCode, String name, String idCard, String phone,
			String bankCard) {
		return new Response<>(publisherService.bindBankCard(serialCode, name, idCard, phone, bankCard));
	}

	@Override
	public Response<List<BindCardDto>> publisherBankCardList(String serialCode) {
		return new Response<>(publisherService.publisherBankCardList(serialCode));
	}

}
