package com.waben.stock.applayer.strategist.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.strategist.reference.PublisherReference;
import com.waben.stock.applayer.strategist.reference.SmsReference;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.SmsType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;

/**
 * 绑卡 Business
 * 
 * @author luomengan
 *
 */
@Service
public class SmsBusiness {

	@Autowired
	@Qualifier("smsReference")
	private SmsReference smsReference;

	@Autowired
	@Qualifier("publisherReference")
	private PublisherReference publisherReference;

	public void sendAuthCode(String phone, SmsType type) {
		if (type == SmsType.RegistVerificationCode) {
			Response<PublisherDto> publisherResp = publisherReference.fetchByPhone(phone);
			if (!"200".equals(publisherResp.getCode())
					&& !ExceptionConstant.DATANOTFOUND_EXCEPTION.equals(publisherResp.getCode())) {
				throw new ServiceException(publisherResp.getCode());
			}
			if (publisherResp.getResult() != null) {
				throw new ServiceException(ExceptionConstant.PHONE_BEEN_REGISTERED_EXCEPTION);
			}
		} else if (type == SmsType.ModifyPasswordCode) {
			Response<PublisherDto> publisherResp = publisherReference.fetchByPhone(phone);
			if (!"200".equals(publisherResp.getCode())
					&& !ExceptionConstant.DATANOTFOUND_EXCEPTION.equals(publisherResp.getCode())) {
				throw new ServiceException(publisherResp.getCode());
			}
			if (publisherResp.getResult() == null) {
				throw new ServiceException(ExceptionConstant.PHONE_ISNOT_REGISTERED_EXCEPTION);
			}
		}
		send(phone, type);
	}

	public void send(String phone, SmsType type) {
		Response<String> response = smsReference.send(phone, type.getIndex());
		if ("200".equals(response.getCode())) {
			return;
		}
		throw new ServiceException(response.getCode());
	}

}
