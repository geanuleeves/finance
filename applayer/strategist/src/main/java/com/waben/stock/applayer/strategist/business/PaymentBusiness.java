package com.waben.stock.applayer.strategist.business;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.strategist.dto.payment.PayRequest;
import com.waben.stock.applayer.strategist.payapi.czpay.CzPayOverHttp;
import com.waben.stock.applayer.strategist.payapi.czpay.CzWithholdOverSocket;
import com.waben.stock.applayer.strategist.payapi.czpay.bean.CzPayCallback;
import com.waben.stock.applayer.strategist.payapi.czpay.bean.CzPayResponse;
import com.waben.stock.applayer.strategist.payapi.czpay.bean.CzPayReturn;
import com.waben.stock.applayer.strategist.payapi.czpay.bean.CzWithholdResponse;
import com.waben.stock.applayer.strategist.payapi.czpay.config.CzPayConfig;
import com.waben.stock.applayer.strategist.payapi.tfbpay.util.RSAUtils;
import com.waben.stock.applayer.strategist.payapi.tfbpay.util.RequestUtils;
import com.waben.stock.applayer.strategist.payapi.wabenpay.WabenPayOverHttp;
import com.waben.stock.applayer.strategist.payapi.wabenpay.bean.MessageRequestBean;
import com.waben.stock.applayer.strategist.payapi.wabenpay.bean.MessageResponseBean;
import com.waben.stock.applayer.strategist.payapi.wabenpay.bean.PayRequestBean;
import com.waben.stock.applayer.strategist.payapi.wabenpay.config.WBConfig;
import com.waben.stock.applayer.strategist.payapi.wabenpay.config.WabenPayConfig;
import com.waben.stock.applayer.strategist.reference.PaymentOrderReference;
import com.waben.stock.applayer.strategist.reference.WithdrawalsOrderReference;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.GatewayPayParam;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.GatewayPayRet;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.BindCardDto;
import com.waben.stock.interfaces.dto.publisher.PaymentOrderDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.dto.publisher.WithdrawalsOrderDto;
import com.waben.stock.interfaces.enums.PaymentState;
import com.waben.stock.interfaces.enums.PaymentType;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.enums.WithdrawalsState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

@Service
public class PaymentBusiness {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("paymentOrderReference")
	private PaymentOrderReference paymentOrderReference;

	@Autowired
	@Qualifier("withdrawalsOrderReference")
	private WithdrawalsOrderReference withdrawalsOrderReference;

	@Autowired
	private CapitalAccountBusiness accountBusiness;

	@Autowired
	private BindCardBusiness bindCardBusiness;
	
	@Autowired
	private PublisherBusiness publisherBusiness;
	
	@Autowired
    private RealNameBusiness realNameBusiness;

	@Autowired
	private WBConfig config;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

	@Value("${spring.profiles.active}")
	private String activeProfile;

	private boolean isProd = true;

	@PostConstruct
	public void init() {
		if ("prod".equals(activeProfile)) {
			isProd = true;
		} else {
			isProd = false;
		}
	}

	public String recharge(Long publisherId, PayRequest payReq) {
		payReq.setPaymentNo(UniqueCodeGenerator.generatePaymentNo());
		// 请求第三方支付
		String result = payment(payReq);
		// 保存支付订单
		PaymentOrderDto paymentOrder = new PaymentOrderDto();
		paymentOrder.setAmount(payReq.getAmount());
		paymentOrder.setPaymentNo(payReq.getPaymentNo());
		paymentOrder.setPublisherId(publisherId);
		paymentOrder.setType(payReq.getPaymentType());
		paymentOrder.setState(PaymentState.Unpaid);
		this.savePaymentOrder(paymentOrder);
		return result;
	}

	public void withdrawals(Long publisherId, BigDecimal amount, String name, String phone, String idCard,
			String bankCard, String bankCode) {
		// 请求提现
		String withdrawalsNo = UniqueCodeGenerator.generateWithdrawalsNo();
		CzWithholdResponse resp = CzWithholdOverSocket.withhold(withdrawalsNo, name, bankCard, phone, bankCode, amount);
		// 保存提现记录
		WithdrawalsOrderDto order = new WithdrawalsOrderDto();
		order.setAmount(amount);
		order.setBankCard(bankCard);
		order.setIdCard(idCard);
		order.setName(name);
		order.setPublisherId(publisherId);
		order.setThirdRespCode(resp.getRespCode());
		order.setThirdRespMsg(resp.getRespMsg());
		order.setWithdrawalsNo(withdrawalsNo);
		order.setState(resp.successful() ? WithdrawalsState.PROCESSING : WithdrawalsState.FAILURE);
		this.saveWithdrawalsOrder(order);
		// 提现异常
		if (!resp.successful()) {
			throw new ServiceException(ExceptionConstant.WITHDRAWALS_EXCEPTION, resp.getRespMsg());
		}
	}

	public PaymentOrderDto savePaymentOrder(PaymentOrderDto paymentOrder) {
		Response<PaymentOrderDto> orderResp = paymentOrderReference.addPaymentOrder(paymentOrder);
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}
	
	public PaymentOrderDto modifyPaymentOrder(PaymentOrderDto paymentOrder) {
        Response<PaymentOrderDto> orderResp = paymentOrderReference.modifyPaymentOrder(paymentOrder);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }

	public WithdrawalsOrderDto saveWithdrawalsOrder(WithdrawalsOrderDto withdrawalsOrderDto) {
		Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.addWithdrawalsOrder(withdrawalsOrderDto);
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}

	public WithdrawalsOrderDto revisionWithdrawalsOrder(WithdrawalsOrderDto withdrawalsOrderDto) {
		Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.modifyWithdrawalsOrder(withdrawalsOrderDto);
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}

	public PaymentOrderDto findByPaymentNo(String paymentNo) {
		Response<PaymentOrderDto> orderResp = paymentOrderReference.fetchByPaymentNo(paymentNo);
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}

	public PaymentOrderDto findById(Long paymentId) {
		Response<PaymentOrderDto> orderResp = paymentOrderReference.fetchById(paymentId);
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}

	public WithdrawalsOrderDto findByWithdrawalsNo(String withdrawalsNo) {
		Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.fetchByWithdrawalsNo(withdrawalsNo);
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}

	public PaymentOrderDto changeState(String paymentNo, PaymentState state) {
		Response<PaymentOrderDto> orderResp = paymentOrderReference.changeState(paymentNo, state.getIndex());
		if ("200".equals(orderResp.getCode())) {
			return orderResp.getResult();
		}
		throw new ServiceException(orderResp.getCode());
	}

	private String payment(PayRequest payReq) {
		if (payReq.getPaymentType() == PaymentType.UnionPay) {
			// 请求第三方支付
			CzPayResponse resp = CzPayOverHttp.payment(payReq.getPaymentNo(), payReq.getAmount(), payReq.getBankCode());
			if ("00".equals(resp.getRespCode())) {
				StringBuilder htmlBuiler = new StringBuilder();
				htmlBuiler.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>支付页面</title><script>");
				htmlBuiler.append("var url = \"" + resp.getHtml() + "\";window.location.href = url;");
				htmlBuiler.append("</script></head><body></body></html>");
				return htmlBuiler.toString();
			} else {
				logger.error("请求第三方支付异常：{}，{}", resp.getRespCode(), resp.getRespDesc());
				throw new ServiceException(ExceptionConstant.RECHARGE_EXCEPTION, resp.getRespDesc());
			}
		} else {
			return null;
		}
	}

	public String tbfPaycallback(String cipherData) {
		try {
			// 解码
			String data = RSAUtils.decrypt(cipherData);
			HashMap<String, String> dataMap = RequestUtils.parseString(data);
			// 验签
			boolean isSuccess = RSAUtils.verify(data, dataMap.get("sign"));
			if (!isSuccess) {
				return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><retcode>207267</retcode><retmsg></retmsg>校验签名失败</root>";
			}

			String paymentNo = dataMap.get("spbillno");
			String state = dataMap.get("result");
			if (paymentNo != null && !"".equals(paymentNo) && "1".equals(state)) {
				// 支付成功
				payCallback(paymentNo, PaymentState.Paid);
			}
			return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><retcode>00</retcode></root>";
		} catch (Exception ex) {
			return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><retcode>207267</retcode><retmsg></retmsg>校验签名失败</root>";
		}
	}

	public String tbfPayReturn(String cipherData) {
		StringBuilder result = new StringBuilder();
		result.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>回调页面</title></head><body>");
		String paymentNo = "";
		String stateStr = "";
		String scriptContent = "<script>function call() {window.webkit.messageHandlers.callback.postMessage({paymentNo:'%s',result:'%s'});} call();</script>";
		String bodyContent = "<p>%s!</p>";
		try {
			// 解码
			String data = RSAUtils.decrypt(cipherData);
			HashMap<String, String> dataMap = RequestUtils.parseString(data);
			// 验签
			boolean isSuccess = RSAUtils.verify(data, dataMap.get("sign"));
			if (isSuccess) {
				paymentNo = dataMap.get("spbillno");
				String state = dataMap.get("result");
				stateStr = "1".equals(state) ? "支付成功" : "支付异常，请稍后重试或调接口查询结果";
			} else {
				stateStr = "验签失败";
			}
		} catch (Exception ex) {
			stateStr = "验签失败";
		}

		result.append(String.format(scriptContent, paymentNo, stateStr));
		result.append(String.format(bodyContent, stateStr));
		result.append("<button type=\"button\" id=\"myBtn\" onclick=\"call()\">返回APP</button></body></html>");
		return result.toString();
	}

	public void payCallback(String paymentNo, PaymentState state) {
		PaymentOrderDto origin = findByPaymentNo(paymentNo);
		if (origin.getState() != state) {
			// 更新支付订单的状态
			changeState(paymentNo, state);
			// 给发布人账号中充值
			if (state == PaymentState.Paid) {
				accountBusiness.recharge(origin.getPublisherId(), origin.getAmount(), origin.getId());
			}
		}
	}

	public String czPaycallback(String data) {
		CzPayCallback callback = JacksonUtil.decode(data, CzPayCallback.class);
		String paymentNo = callback.getOrgSendSeqId();
		if ("00".equals(callback.getPayResult())) {
			// 支付成功
			payCallback(paymentNo, PaymentState.Paid);
			return "success";
		} else {
			return "fail";
		}
	}

	public String czPayReturn(String data) {
		String paymentNo = "";
		String stateStr = "";
		String code = "";
		try {
			CzPayReturn returnObj = JacksonUtil.decode(data, CzPayReturn.class);
			paymentNo = returnObj.getSendSeqId();
			code = returnObj.getRespCode();
			if ("00".equals(code)) {
				stateStr = "支付成功";
			} else {
				stateStr = returnObj.getRespDesc();
			}
		} catch (Exception ex) {
			stateStr = "支付异常";
		}
		try {
			return CzPayConfig.webReturnUrl + "?paymentNo" + paymentNo + "&code=" + code + "&message="
					+ URLEncoder.encode(stateStr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("utf-8 not supported?");
		}
	}

	public void czWithholdCallback(String withdrawalsNo, WithdrawalsState withdrawalsState, String thirdRespCode,
			String thirdRespMsg) {
		WithdrawalsOrderDto order = this.findByWithdrawalsNo(withdrawalsNo);
		order.setThirdRespCode(thirdRespCode);
		order.setThirdRespMsg(thirdRespMsg);
		this.revisionWithdrawalsOrder(order);
		if (order.getState() == WithdrawalsState.PROCESSING) {
			accountBusiness.withdrawals(order.getPublisherId(), order.getId(), withdrawalsState);
		}
	}

	public String quickPayMessage(BigDecimal amount, Long bindCardId, Long publisherId) {
		BindCardDto bindCard = bindCardBusiness.findById(bindCardId);
		if (bindCard == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		if (publisherId.compareTo(bindCard.getResourceId()) != 0) {
			throw new ServiceException(ExceptionConstant.PUBLISHERID_NOTMATCH_EXCEPTION);
		}
		if (bindCard.getContractNo() == null || "".equals(bindCard.getContractNo().trim())) {
			bindCard.setContractNo(bindCardBusiness.getWabenContractNo(bindCard));
			bindCardBusiness.revision(bindCard);
		}
		// 请求发送快捷短信
		amount.setScale(2, RoundingMode.DOWN);
		MessageRequestBean request = new MessageRequestBean();
		request.setAmount(amount.multiply(new BigDecimal(100)).setScale(0).toString());
		request.setContractNo(bindCard.getContractNo());
		request.setMember(String.valueOf(bindCard.getResourceId()));
		try {
			MessageResponseBean msgResp = WabenPayOverHttp.message(request);
			// 请求成功，创建订单，并返回订单号
			PaymentOrderDto paymentOrder = new PaymentOrderDto();
			paymentOrder.setAmount(amount);
			paymentOrder.setPaymentNo(UniqueCodeGenerator.generatePaymentNo());
			paymentOrder.setPublisherId(bindCard.getResourceId());
			paymentOrder.setType(PaymentType.QuickPay);
			paymentOrder.setState(PaymentState.Unpaid);
			paymentOrder.setThirdPaymentNo(msgResp.getTransactNo());
			paymentOrder.setCreateTime(sdf.parse(msgResp.getOrderTime()));
			this.savePaymentOrder(paymentOrder);
			return paymentOrder.getPaymentNo();
		} catch (Exception ex) {
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, ex.getMessage());
		}
	}

	public String quickPay(String paymentNo, Long bindCardId, String validaCode, Long publisherId) {
		PaymentOrderDto paymentOrder = findByPaymentNo(paymentNo);
		if (paymentOrder == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		if (publisherId.compareTo(paymentOrder.getPublisherId()) != 0) {
			throw new ServiceException(ExceptionConstant.PUBLISHERID_NOTMATCH_EXCEPTION);
		}
		BindCardDto bindCard = bindCardBusiness.findById(bindCardId);
		if (bindCard == null) {
			throw new ServiceException(ExceptionConstant.DATANOTFOUND_EXCEPTION);
		}
		if (publisherId.compareTo(bindCard.getResourceId()) != 0) {
			throw new ServiceException(ExceptionConstant.PUBLISHERID_NOTMATCH_EXCEPTION);
		}
		// 请求快捷支付
		PayRequestBean request = new PayRequestBean();
		request.setOutTradeNo(paymentOrder.getPaymentNo());
		request.setTimeStart(sdf.format(paymentOrder.getCreateTime()));
		request.setContractNo(bindCard.getContractNo());
		request.setBankAccount(bindCard.getBankCard());
		request.setNotifyUrl(isProd ? WabenPayConfig.prodNotifyUrl : WabenPayConfig.testNotifyUrl);
		request.setValidaCode(validaCode);
		request.setTransactNo(paymentOrder.getThirdPaymentNo());
		request.setAmount(paymentOrder.getAmount().multiply(new BigDecimal(100)).setScale(0).toString());
		try {
			WabenPayOverHttp.pay(request);
			return paymentOrder.getPaymentNo();
		} catch (Exception ex) {
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, ex.getMessage());
		}
	}

	/*
	public String wabenNetbankPay(Long publisherId, BigDecimal amount) {
		// 保存支付订单
		PaymentOrderDto paymentOrder = new PaymentOrderDto();
		paymentOrder.setAmount(amount);
		paymentOrder.setPaymentNo(UniqueCodeGenerator.generatePaymentNo());
		paymentOrder.setPublisherId(publisherId);
		paymentOrder.setType(PaymentType.UnionPay);
		paymentOrder.setState(PaymentState.Unpaid);
		paymentOrder = this.savePaymentOrder(paymentOrder);
		// 请求第三方支付
		UnionPayRequestBean request = new UnionPayRequestBean();
		request.setAmount(String.valueOf(amount.multiply(new BigDecimal("100")).setScale(0, RoundingMode.DOWN)));
		request.setBankCode("CCB");
		request.setOutTradeNo(paymentOrder.getPaymentNo());
		request.setTimeStart(sdf.format(new Date()));
		UnionPayResponseBean response = WabenPayOverHttp.netbankPay(request, config);
		// 更新第三方支付单号
		paymentOrder.setThirdPaymentNo(response.getTransNo());
		savePaymentOrder(paymentOrder);
		return "http://b.waben.com.cn" + response.getRedirectURL();
	}
	*/
	
	public String wabenNetbankPay(Long publisherId, BigDecimal amount) {
        PublisherDto publisher = publisherBusiness.findById(publisherId);
        RealNameDto realNameDto = realNameBusiness.fetch(ResourceType.PUBLISHER, publisherId);
        //创建订单
        PaymentOrderDto paymentOrder = new PaymentOrderDto();
        paymentOrder.setAmount(amount);
        String paymentNo = UniqueCodeGenerator.generatePaymentNo();
        paymentOrder.setPaymentNo(paymentNo);
        paymentOrder.setType(PaymentType.QuickPay);
        paymentOrder.setState(PaymentState.Unpaid);
        paymentOrder.setPublisherId(publisher.getId());
        paymentOrder.setCreateTime(new Date());
        paymentOrder.setUpdateTime(new Date());
        paymentOrder = this.savePaymentOrder(paymentOrder);
        // 封装请求参数
        GatewayPayParam param = new GatewayPayParam();
		param.setAppId(config.getMerchantNo());
		param.setUserId(String.valueOf(publisherId));
		param.setSubject(publisherId + "充值");
		param.setBody(publisherId + "充值" + amount + "元");
		param.setTotalFee(isProd ? amount : new BigDecimal("0.01"));
		param.setOutOrderNo(paymentNo);
		param.setFrontSkipUrl(config.getUnionpayFrontUrl());
		param.setReturnUrl(config.getUnionpayNotifyUrl());
		param.setTimestamp(sdf.format(new Date()));
		param.setVersion("1.0");
		param.setBankCode("01050000");
		GatewayPayRet payRet = com.waben.stock.interfaces.commonapi.wabenpay.WabenPayOverHttp.gatewayPay(param, config.getKey());
		if(payRet != null && payRet.getTradeNo() != null) {
			paymentOrder.setThirdPaymentNo(payRet.getTradeNo());
			this.modifyPaymentOrder(paymentOrder);
		}
        if (payRet.getCode() == 1) {
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", payRet.getPayUrl());
    		return resultUrl.get("url");
        } else {
        	throw new ServiceException(ExceptionConstant.REQUEST_RECHARGE_EXCEPTION);
        }
	}

}
