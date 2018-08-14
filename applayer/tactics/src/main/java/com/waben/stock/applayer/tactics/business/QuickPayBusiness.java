package com.waben.stock.applayer.tactics.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.waben.stock.applayer.tactics.payapi.caituopay.config.PayConfig;
import com.waben.stock.applayer.tactics.payapi.paypal.config.PayPalConfig;
import com.waben.stock.applayer.tactics.payapi.paypal.config.RSAUtil;
import com.waben.stock.applayer.tactics.payapi.paypal.utils.HttpUtil;
import com.waben.stock.applayer.tactics.payapi.paypal.utils.LianLianRSA;
import com.waben.stock.applayer.tactics.payapi.shande.bean.PayRequestBean;
import com.waben.stock.applayer.tactics.payapi.shande.config.SandPayConfig;
import com.waben.stock.applayer.tactics.payapi.shande.utils.FormRequest;
import com.waben.stock.applayer.tactics.payapi.wbpay.config.WBConfig;
import com.waben.stock.applayer.tactics.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.applayer.tactics.rabbitmq.RabbitmqProducer;
import com.waben.stock.applayer.tactics.rabbitmq.message.WithdrawQueryMessage;
import com.waben.stock.interfaces.commonapi.wabenpay.WabenPayOverHttp;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.GatewayPayParam;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.GatewayPayRet;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.SwiftPayParam;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.SwiftPayRet;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.WithdrawParam;
import com.waben.stock.interfaces.commonapi.wabenpay.bean.WithdrawRet;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.publisher.FuturesComprehensiveFeeDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
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
import com.waben.stock.interfaces.service.publisher.FuturesComprehensiveFeeInterface;
import com.waben.stock.interfaces.service.publisher.PaymentOrderInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.WithdrawalsOrderInterface;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.Md5Util;
import com.waben.stock.interfaces.util.StringUtil;
import com.waben.stock.interfaces.util.UniqueCodeGenerator;

@Service
public class QuickPayBusiness {

    Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    @Qualifier("paymentOrderInterface")
    private PaymentOrderInterface paymentOrderReference;
    @Autowired
    @Qualifier("publisherInterface")
    private PublisherInterface publisherReference;
    @Autowired
    @Qualifier("withdrawalsOrderInterface")
    private WithdrawalsOrderInterface withdrawalsOrderReference;
    
    @Autowired
    @Qualifier("futuresComprehensiveFeeInterface")
    private FuturesComprehensiveFeeInterface feeReference;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

    @Autowired
    private CapitalAccountBusiness accountBusiness;

    @Autowired
    private BindCardBusiness bindCardBusiness;

    @Autowired
    private PublisherBusiness publisherBusiness;
    
    @Autowired
    private RealNameBusiness realNameBusiness;
    
    @Autowired
    private WBConfig wbConfig;
    
    @Autowired
	private RabbitmqProducer producer;
    
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

    public Map<String, String> quickpay(BigDecimal amount, String phone) {

        Response<PublisherDto> response = publisherReference.fetchByPhone(phone);
        if (!"200".equals(response.getCode())) {
            throw new ServiceException(response.getCode());
        }
        //创建订单
        PaymentOrderDto paymentOrder = new PaymentOrderDto();
        paymentOrder.setAmount(amount);
        String paymentNo = UniqueCodeGenerator.generatePaymentNo();
        paymentOrder.setPaymentNo(paymentNo);
        paymentOrder.setType(PaymentType.QuickPay);
        paymentOrder.setState(PaymentState.Unpaid);
        paymentOrder.setPublisherId(response.getResult().getId());
        paymentOrder.setCreateTime(new Date());
        paymentOrder.setUpdateTime(new Date());
        this.savePaymentOrder(paymentOrder);
        //封装请求参数
        PayRequestBean request = new PayRequestBean();
        request.setUserId(phone.substring(1));
        request.setMchNo(SandPayConfig.mchNo);
        request.setMchType(SandPayConfig.mchType);
        request.setPayChannel(SandPayConfig.payChannel);
        request.setPayChannelTypeNo(SandPayConfig.payChannelTypeNo);
        request.setNotifyUrl(SandPayConfig.notifyUrl);
        request.setFrontUrl(SandPayConfig.fontUrl);
        request.setOrderNo(paymentNo);
        request.setAmount(amount.toString());
        request.setGoodsName(SandPayConfig.goodsName);
        request.setGoodsDesc(SandPayConfig.goodsDesc);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        String timeStamp = format.format(date);
        request.setTimeStamp(timeStamp);
        Map<String, String> paramMap = (Map<String, String>) JacksonUtil.decode(JacksonUtil.encode(request),
                Map.class);
        TreeMap<String, String> sortParamMap = new TreeMap<>(paramMap);
        StringBuilder strForSign = new StringBuilder();
        String toSign = "";
        for (String key : sortParamMap.keySet()) {
            toSign += key + "=" + sortParamMap.get(key) + "&";
        }
        toSign += "key=" + SandPayConfig.key;
        System.out.println(toSign);
        String sign = DigestUtils.md5Hex(toSign);
        sortParamMap.put("sign", sign);
        return sortParamMap;
    }

    public String sdPayReturn() {
        StringBuilder result = new StringBuilder();
        result.append("<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><title>回调页面</title></head><body>");
        String paymentNo = "";
        String stateStr = "已支付";
        String scriptContent = "<script>function call() {if(window.appInterface) {window.appInterface.rechargeCallback('%s', '%s');} else {window.webkit.messageHandlers.callback.postMessage({paymentNo:'%s',result:'%s'});}} call();</script></body></html>";
        result.append(String.format(scriptContent, paymentNo, stateStr, paymentNo, stateStr));
        return result.toString();
    }

    public String sdPaycallback(HttpServletRequest request) {
        logger.info("回调开始");
        Map<String, String> responseResult = paramter2Map(request);
        Map<String, String> resultMap = new TreeMap<>(responseResult);
        logger.info("通知的参数是:{}", resultMap.toString());
        String toSign = "";
        for (String key : resultMap.keySet()) {
            if (!"msg".equals(key) && !"result".equals(key) && !"sign".equals(key)) {
                if (!StringUtils.isBlank(resultMap.get(key))) {
                    toSign += key + "=" + resultMap.get(key) + "&";
                }
            }
        }
        toSign += "key=" + SandPayConfig.key;
        logger.info("回调签名验证原串:{}", toSign);
        String resultSign = DigestUtils.md5Hex(toSign);
        logger.info("回调签名验证加密串:{}", resultSign);
        String result = responseResult.get("result");
        String sign = responseResult.get("sign");
        logger.info("通知签名串:{}", sign);
        String paymentNo = responseResult.get("orderNo");
        String thirdPaymentNo = responseResult.get("gwTradeNo");
        try {
            if (paymentNo != null && !"".equals(paymentNo) && "SUCCESS".equals(result)) {
                //验证签名
                if (sign.equals(resultSign)) {
                    logger.info("快捷移动验证签名成功");
                    // 支付成功
                    payCallback(paymentNo, PaymentState.Paid);
                }
                return "success";
            }
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><retcode>207267</retcode><retmsg></retmsg>支付失败</root>";
        } catch (Exception ex) {
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><root><retcode>207267</retcode><retmsg></retmsg>校验签名失败</root>";
        }
    }


    public String qqPaycallback(HttpServletRequest request) {
        Map<String, String> responseResult = paramter2Map(request);
        logger.info("QQ支付回调的结果是{}:", responseResult.toString());
        String result = responseResult.get("result");
        String paymentNo = responseResult.get("Out_trade_no");
        String message = responseResult.get("message");
        String thirdPaymentNo = responseResult.get("order_no");
        String code = responseResult.get("code");
        String sign = responseResult.get("sign");
        String resultSign = DigestUtils.md5Hex(PayConfig.appId + thirdPaymentNo + PayConfig.appSecret).toUpperCase();
        if (paymentNo != null && !"".equals(paymentNo) && "1".equals(code)) {
            //验证签名
            logger.info("QQ验证签名通过");
            if (sign.equals(resultSign)) {
                payCallback(paymentNo, PaymentState.Paid);
                return "success";
            }
            return "false";
        }
        return "false";
    }


    public String jdPaycallback(HttpServletRequest request) {
        Map<String, String> responseResult = paramter2Map(request);
        logger.info("京东支付回调的结果是{}:", responseResult.toString());
        String result = responseResult.get("result");
        String paymentNo = responseResult.get("Out_trade_no");
        String message = responseResult.get("message");
        String thirdPaymentNo = responseResult.get("order_no");
        String code = responseResult.get("code");
        String sign = responseResult.get("sign");
        String resultSign = DigestUtils.md5Hex(PayConfig.appId + thirdPaymentNo + PayConfig.appSecret).toUpperCase();
        if (paymentNo != null && !"".equals(paymentNo) && "1".equals(code)) {
            // 支付成功,验证签名
            if (sign.equals(resultSign)) {
                logger.info("京东验证签名通过");
                payCallback(paymentNo, PaymentState.Paid);
                return "success";
            }
            return "false";
        }
        return "false";
    }


    public void payPalCSA(Long publisherId, BigDecimal amount, String name, String phone, String idCard,
                          String bankCard, String bankCode, String branchName) {

        // 请求提现
        String withdrawalsNo = UniqueCodeGenerator.generateWithdrawalsNo();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<String, String> map = new TreeMap<>();
        map.put("oid_partner", PayPalConfig.oid_partner);
        map.put("api_version", PayPalConfig.csa_version);
        map.put("sign_type", PayPalConfig.sign_type);
        map.put("no_order", withdrawalsNo);
        map.put("dt_order", time.format(new Date()));
        map.put("money_order", amount.toString());
        map.put("card_no", bankCard);
        map.put("acct_name", name);
        map.put("info_order", PayPalConfig.info_order);
        map.put("flag_card", PayPalConfig.flag_card);//对私
        map.put("notify_url", PayPalConfig.csa_notifyurl);
        map.put("memo", PayPalConfig.memo);

        JSONObject jsonObject = (JSONObject) JSONObject.toJSON(map);
        String tosign = genSignData(jsonObject);
        logger.info("代付签名的参数是:{}", tosign);
        String sign = RSAUtil.sign(PayPalConfig.private_key, tosign);
        map.put("sign", sign);
        logger.info("代付的参数是:{}", map.toString());
        logger.info("代付请求发起");
        JSONObject jsonObject1 = (JSONObject) JSONObject.toJSON(map);
        String sign1 = null;
        try {
            sign1 = LianLianRSA.encrypt(jsonObject1.toJSONString(), PayPalConfig.public_key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject response = new JSONObject();
        response.put("pay_load", sign1);
        response.put("oid_partner", PayPalConfig.oid_partner);
        logger.info("代付请求的参数是:{}", response.toJSONString());
        String result = HttpUtil.doPost(PayPalConfig.csa_url, response, "utf-8");
        JSONObject jsStr = JSONObject.parseObject(result);
        logger.info("代付请求的结果是:{}", jsStr.toJSONString());
        String confirm_code = jsStr.getString("confirm_code");
        //如果被判定为 疑似重复提交订单则进行确认请求
        if (!StringUtils.isBlank(confirm_code)) {
            Map<String, String> confirmMap = new TreeMap<>();
            confirmMap.put("oid_partner", PayPalConfig.oid_partner);
            confirmMap.put("sign_type", PayPalConfig.sign_type);
            confirmMap.put("api_version", PayPalConfig.csa_version);
            confirmMap.put("no_order", withdrawalsNo);
            confirmMap.put("confirm_code", confirm_code);
            confirmMap.put("notify_url", PayPalConfig.csa_notifyurl);
            JSONObject confirmObject = (JSONObject) JSONObject.toJSON(confirmMap);
            String confirmToSign = genSignData(confirmObject);
            logger.info("确认提交签名的参数是:{}", confirmToSign);
            String confirmSign = RSAUtil.sign(PayPalConfig.private_key, confirmToSign);
            confirmMap.put("sign", confirmSign);
            logger.info("代付确认的参数是:{}", confirmMap.toString());
            logger.info("代付确认请求发起");
            JSONObject confirmObject1 = (JSONObject) JSONObject.toJSON(confirmMap);
            String confirmSign1 = null;
            try {
                confirmSign1 = LianLianRSA.encrypt(confirmObject1.toJSONString(), PayPalConfig.public_key);
            } catch (Exception e) {
                e.printStackTrace();
            }
            JSONObject confirmResponse = new JSONObject();
            confirmResponse.put("pay_load", confirmSign1);
            confirmResponse.put("oid_partner", PayPalConfig.oid_partner);
            logger.info("代付确认请求的参数是:{}", confirmResponse.toJSONString());
            String confirmResult = HttpUtil.doPost(PayPalConfig.csa_confirm_url, confirmResponse, "utf-8");
            jsStr = JSONObject.parseObject(confirmResult);
            logger.info("代付确认请求的结果是:{}", jsStr.toJSONString());

        }
        //生成提现订单
        logger.info("保存提现订单");
        WithdrawalsOrderDto order = new WithdrawalsOrderDto();
        order.setWithdrawalsNo(withdrawalsNo);
        order.setAmount(amount);
        order.setState(WithdrawalsState.PROCESSING);
        order.setName(name);
        order.setIdCard(idCard);
        order.setBankCard(bankCard);
        order.setPublisherId(publisherId);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        this.saveWithdrawalsOrders(order);
        //如果请求失败 抛出异常
        if (!"0000".equals(jsStr.getString("ret_code"))) {
            throw new ServiceException(ExceptionConstant.WITHDRAWALS_EXCEPTION, jsStr.getString("ret_msg"));
        }
    }

    public void payPalWithholdCallback(String withdrawalsNo, WithdrawalsState withdrawalsState, String thirdRespCode,
                                       String thirdRespMsg) {
        logger.info("连连代付异步回调");
        WithdrawalsOrderDto order = this.findByWithdrawalsNo(withdrawalsNo);
        order.setThirdRespCode(thirdRespCode);
        order.setThirdRespMsg(thirdRespMsg);
        this.revisionWithdrawalsOrder(order);
        if (order.getState() == WithdrawalsState.PROCESSING) {
            accountBusiness.withdrawals(order.getPublisherId(), order.getId(), withdrawalsState);
        }
    }


    public Response<Map> ctQQh5(BigDecimal amount, String phone) {
        Response<PublisherDto> response = publisherReference.fetchByPhone(phone);
        if (!"200".equals(response.getCode())) {
            throw new ServiceException(response.getCode());
        }
        //创建订单
        PaymentOrderDto paymentOrder = new PaymentOrderDto();
        paymentOrder.setAmount(amount);
        String paymentNo = UniqueCodeGenerator.generatePaymentNo();
        paymentOrder.setPaymentNo(paymentNo);
        paymentOrder.setType(PaymentType.QuickPay);
        paymentOrder.setState(PaymentState.Unpaid);
        paymentOrder.setPublisherId(response.getResult().getId());
        paymentOrder.setCreateTime(new Date());
        paymentOrder.setUpdateTime(new Date());
        this.savePaymentOrder(paymentOrder);
        //封装请求参数
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String timeStamp = format.format(date);
        Map<String, String> map = new TreeMap<>();
        String appId = PayConfig.appId;
        String appSecret = PayConfig.appSecret;
        String url = PayConfig.qqurl;
        map.put("appId", appId);
        map.put("version", PayConfig.version);
        map.put("return_url", PayConfig.qq_return_url);
        map.put("device_info", PayConfig.device_info);
        map.put("mch_app_name", PayConfig.mch_app_name);
        map.put("mch_app_id", PayConfig.mch_app_id);
        map.put("total_fee", amount.toString());
        map.put("front_skip_url", PayConfig.qq_front_skip_url);
        map.put("subject", PayConfig.subject);
        map.put("body", PayConfig.qqbody);
        map.put("out_order_no", paymentNo);
        map.put("timestamp", timeStamp);
        String sign = DigestUtils.md5Hex(appId + appSecret + timeStamp + paymentNo);
        map.put("sign", sign);
        logger.info("请求的参数是{}:", map.toString());
        String result = FormRequest.doPost(map, url);
        JSONObject jsStr = JSONObject.parseObject(result);
        String payUrl = jsStr.getString("payUrl");
        logger.info("请求的结果是{}:", jsStr.toString());
        Response<Map> resp = new Response<Map>();
        if ("1".equals(jsStr.getString("code"))) {
            resp.setCode("200");
            resp.setMessage(jsStr.getString("message"));
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", payUrl);
            resp.setResult(resultUrl);
        } else {
            resp.setCode(jsStr.getString("code"));
            resp.setMessage(jsStr.getString("message"));
        }
        if (StringUtils.isBlank(payUrl)) {
            throw new ServiceException(ExceptionConstant.RECHARGE_EXCEPTION, jsStr.getString("message"));
        }
        return resp;
    }

    public Response<Map> jdh5(BigDecimal amount, String phone) {
        Response<PublisherDto> response = publisherReference.fetchByPhone(phone);
        if (!"200".equals(response.getCode())) {
            throw new ServiceException(response.getCode());
        }
        //创建订单
        PaymentOrderDto paymentOrder = new PaymentOrderDto();
        paymentOrder.setAmount(amount);
        String paymentNo = UniqueCodeGenerator.generatePaymentNo();
        paymentOrder.setPaymentNo(paymentNo);
        paymentOrder.setType(PaymentType.QuickPay);
        paymentOrder.setState(PaymentState.Unpaid);
        paymentOrder.setPublisherId(response.getResult().getId());
        paymentOrder.setCreateTime(new Date());
        paymentOrder.setUpdateTime(new Date());
        this.savePaymentOrder(paymentOrder);
        //封装请求参数
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String timeStamp = format.format(date);
        Map<String, String> map = new TreeMap<>();
        String appId = PayConfig.appId;
        String appSecret = PayConfig.appSecret;
        String url = PayConfig.jdurl;
        map.put("appId", appId);
        map.put("version", PayConfig.version);
        map.put("return_url", PayConfig.jd_return_url);
        map.put("total_fee", amount.toString());
        map.put("front_skip_url", PayConfig.jd_front_skip_url);
        map.put("subject", PayConfig.subject);
        map.put("body", PayConfig.jdbody);
        map.put("out_order_no", paymentNo);
        map.put("timestamp", timeStamp);
        String sign = DigestUtils.md5Hex(appId + appSecret + timeStamp + paymentNo);
        map.put("sign", sign);
        logger.info("请求的参数是{}:", map.toString());
        String result = FormRequest.doPost(map, url);
        JSONObject jsStr = JSONObject.parseObject(result);
        String payUrl = jsStr.getString("payUrl");
        logger.info("请求的结果是{}:", jsStr.toString());
        Response<Map> resp = new Response();
        if ("1".equals(jsStr.getString("code"))) {
            resp.setCode("200");
            resp.setMessage(jsStr.getString("message"));
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", payUrl);
            resp.setResult(resultUrl);
        } else {
            resp.setCode(jsStr.getString("code"));
            resp.setMessage(jsStr.getString("message"));
        }
        if (StringUtils.isBlank(payUrl)) {
            throw new ServiceException(ExceptionConstant.RECHARGE_EXCEPTION, jsStr.getString("message"));
        }
        return resp;
    }


    public void withdrawals(Long publisherId, BigDecimal amount, String name, String phone, String idCard,
                            String bankCard, String bankCode, String branchName) {
        //生成提现订单
        logger.info("保存提现订单");
        WithdrawalsOrderDto order = new WithdrawalsOrderDto();
        String withdrawalsNo = UniqueCodeGenerator.generateWithdrawalsNo();
        order.setWithdrawalsNo(withdrawalsNo);
        order.setAmount(amount);
        order.setState(WithdrawalsState.PROCESSING);
        order.setName(name);
        order.setIdCard(idCard);
        order.setBankCard(bankCard);
        order.setPublisherId(publisherId);
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        this.saveWithdrawalsOrder(order);
        // 请求提现
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        Map<String, String> map = new TreeMap();
        map.put("mchNo", SandPayConfig.mchNo);
        map.put("payChannel", SandPayConfig.payChannel);
        map.put("orderNo", withdrawalsNo);
        map.put("amount", amount.toString());
        map.put("bankType", SandPayConfig.bankType);
        map.put("accNo", bankCard);
        map.put("accName", name);
        map.put("bankName", "中国银行");
        map.put("timeStamp", time.format(new Date()));
        //签名
        String toSign = "";
        for (String key : map.keySet()) {
            toSign += key + "=" + map.get(key) + "&";
        }
        toSign += "key=" + SandPayConfig.key;
        logger.info("代付签名的参数是:{}", toSign);
        String sign = DigestUtils.md5Hex(toSign);
        map.put("sign", sign);
        logger.info("代付的参数是:{}", map.toString());
        logger.info("代付请求发起");
        String result = FormRequest.doPost(map, SandPayConfig.csaUrl);
        JSONObject jsStr = JSONObject.parseObject(result);
        System.out.println(jsStr.toString());
        logger.info("代付请求的结果是:{}", jsStr);
//        CzWithholdResponse resp = CzWithholdOverSocket.withhold(withdrawalsNo, name, bankCard, phone, bankCode, amount);
//        // 提现异常
        JSONObject jsonData = jsStr.getJSONObject("data");
        String resultFlag = jsonData.getString("resultFlag");
        if ("SUCCESS".equals(jsStr.getString("result")) || "0".equals(resultFlag) || "2".equals(resultFlag)) {
            WithdrawalsOrderDto origin = findWithdrawalsOrder(withdrawalsNo);
            accountBusiness.csa(origin.getPublisherId(), origin.getAmount(), order.getId());
            if (origin.getState() != WithdrawalsState.PROCESSED) {
                // 更新代付订单的状态
                withdrawalsOrderReference.changeState(withdrawalsNo, WithdrawalsState.PROCESSED.getIndex());
            }
        } else {
            throw new ServiceException(ExceptionConstant.WITHDRAWALS_EXCEPTION, jsStr.getString("msg"));
        }


    }
    
    
    public void wbWithdrawalsAdmin(Long publisherId, BigDecimal amount, String name, String phone, String idCard,
            String bankCard, String bankCode, String bankName){
    	CapitalAccountDto account = accountBusiness.findByPublisherId(publisherId);
    	if (account.getState() != null && account.getState() == 2) {
			throw new ServiceException(ExceptionConstant.CAPITALACCOUNT_FROZEN_EXCEPTION);
		}
        logger.info("保存提现订单");
        String withdrawalsNo = UniqueCodeGenerator.generateWithdrawalsNo();
        WithdrawalsOrderDto order = new WithdrawalsOrderDto();
        order.setWithdrawalsNo(withdrawalsNo);
        order.setAmount(amount);
        order.setState(WithdrawalsState.INIT);
        order.setComprehensiveState(0);
        order.setName(name);
        order.setIdCard(idCard);
        order.setBankCard(bankCard);
        order.setPublisherId(publisherId);
        order.setBankCode(bankCode);
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        order = this.saveWithdrawalsOrdersAdmin(order);
    	
    }

    public void wbWithdrawals(Long publisherId, BigDecimal amount, String name, String phone, String idCard,
                              String bankCard, String bankCode, String bankName){
    	CapitalAccountDto account = accountBusiness.findByPublisherId(publisherId);
    	if (account.getState() != null && account.getState() == 2) {
			throw new ServiceException(ExceptionConstant.CAPITALACCOUNT_FROZEN_EXCEPTION);
		}
        logger.info("保存提现订单");
        String withdrawalsNo = UniqueCodeGenerator.generateWithdrawalsNo();
        WithdrawalsOrderDto order = new WithdrawalsOrderDto();
        order.setWithdrawalsNo(withdrawalsNo);
        order.setAmount(amount);
        order.setState(WithdrawalsState.PROCESSING);
        order.setComprehensiveState(0);
        order.setName(name);
        order.setIdCard(idCard);
        order.setBankCard(bankCard);
        order.setPublisherId(publisherId);
        Date date = new Date();
        order.setCreateTime(date);
        order.setUpdateTime(date);
        order = this.saveWithdrawalsOrders(order);

        logger.info("发起提现申请:{}_{}_{}_{}", name, idCard, phone, bankCard);
        WithdrawParam param = new WithdrawParam();
		param.setAppId(wbConfig.getMerchantNo());
		param.setBankAcctName(name);
		param.setBankNo(bankCard);
		param.setBankCode(bankCode);
		param.setBankName(bankName);
		param.setCardType("0");
		param.setOutOrderNo(withdrawalsNo);
		param.setTimestamp(sdf.format(date));
		param.setTotalAmt(isProd ? amount : new BigDecimal("0.01"));
		param.setVersion("1.0");
//		WithdrawRet withdrawRet = WabenPayOverHttp.withdraw(param, wbConfig.getKey());
//        if(1 == withdrawRet.getStatus()) {
//        	// 提现请求成功，使用队列查询
//        	WithdrawQueryMessage message = new WithdrawQueryMessage();
//    		message.setAppId(wbConfig.getMerchantNo());
//    		message.setOutOrderNo(withdrawalsNo);
//    		message.setOrderNo(withdrawRet.getOrderNo());
//    		producer.sendMessage(RabbitmqConfiguration.withdrawQueryQueueName, message);
//    		// 更新订单状态
//        	order.setThirdWithdrawalsNo(withdrawRet.getOrderNo());
//        	this.revisionWithdrawalsOrder(order);
//        } else {
//        	WithdrawalsOrderDto orders = this.findByWithdrawalsNo(withdrawalsNo);
//            accountBusiness.withdrawals(publisherId, orders.getId(),WithdrawalsState.FAILURE);
//            throw new ServiceException(ExceptionConstant.WITHDRAWALS_EXCEPTION);
//        }
		// 发起提现请求前，预使用队列查询
    	WithdrawQueryMessage message = new WithdrawQueryMessage();
		message.setAppId(wbConfig.getMerchantNo());
		message.setOutOrderNo(withdrawalsNo);
		producer.sendMessage(RabbitmqConfiguration.withdrawQueryQueueName, message);
		// 发起提现请求
		WithdrawRet withdrawRet = WabenPayOverHttp.withdraw(param, wbConfig.getKey());
		if(withdrawRet != null && !StringUtil.isEmpty(withdrawRet.getOrderNo())) {
			// 更新支付系统第三方订单状态
        	order.setThirdWithdrawalsNo(withdrawRet.getOrderNo());
        	this.revisionWithdrawalsOrder(order);
		}
    }

    public Response<Map<String, String>> wabenPay(BigDecimal amount, Long userId, String endType) {
    	CapitalAccountDto account = accountBusiness.findByPublisherId(userId);
    	if (account.getState() != null && account.getState() == 2) {
			throw new ServiceException(ExceptionConstant.CAPITALACCOUNT_FROZEN_EXCEPTION);
		}
        PublisherDto publisher = publisherBusiness.findById(userId);
        RealNameDto realNameDto = realNameBusiness.fetch(ResourceType.PUBLISHER, userId);
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
        /* 以下注释的代码为旧网贝支付系统
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String timeStamp = format.format(new Date());
        Map<String, String> map = new TreeMap<>();
        String url = WBConfig.quick_bank_url;
        map.put("merchantNo", wbConfig.getMerchantNo());
        map.put("notifyUrl", wbConfig.getNotifyUrl());
        map.put("amount", (amount.movePointRight(2)).toString());
        map.put("name", realNameDto.getName());
        map.put("tradeType", WBConfig.tradeType);
        map.put("payment", "auth");
        map.put("timeStart", timeStamp);
        map.put("outTradeNo", paymentNo);
        map.put("frontUrl", "H5".equals(endType) ? wbConfig.getH5ProxyfrontUrl() : wbConfig.getFrontUrl());
        map.put("idCard", realNameDto.getIdCard());
        String signStr = "";
        map.put("sign", "001");
        logger.info("请求的参数是{}:", map.toString());
        String result = FormRequest.doPost(map, url);
        JSONObject jsStr = JSONObject.parseObject(result);
        JSONObject result1 = jsStr.getJSONObject("result");
        logger.info("请求的结果是{}:", jsStr.toString());
        Response<Map> resp = new Response<Map>();
        if ("200".equals(jsStr.getString("code"))) {
            resp.setCode("200");
            resp.setMessage(jsStr.getString("message"));
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", WBConfig.domain + result1.getString("redirectURL"));
            resp.setResult(resultUrl);
        } else {
            resp.setCode(jsStr.getString("code"));
            resp.setMessage(jsStr.getString("message"));
        }
        return resp;
        */
        // 封装请求参数
        SwiftPayParam param = new SwiftPayParam();
		param.setAppId(wbConfig.getMerchantNo());
		param.setSubject(userId + "充值");
		param.setBody(userId + "充值" + amount + "元");
		param.setTotalFee(isProd ? amount : new BigDecimal("0.01"));
		param.setOutOrderNo(paymentNo);
		param.setFrontSkipUrl("H5".equals(endType) ? wbConfig.getH5ProxyfrontUrl() : wbConfig.getFrontUrl());
		param.setReturnUrl(wbConfig.getNotifyUrl());
		param.setTimestamp(sdf.format(new Date()));
		param.setUserId(String.valueOf(userId));
		param.setVersion("1.0");
		param.setAcctName(realNameDto.getName());
		param.setIdNum(realNameDto.getIdCard());
		SwiftPayRet payRet = WabenPayOverHttp.swiftPay(param, wbConfig.getKey());
		if(payRet != null && payRet.getTradeNo() != null) {
			paymentOrder.setThirdPaymentNo(payRet.getTradeNo());
			this.modifyPaymentOrder(paymentOrder);
		}
		Response<Map<String, String>> resp = new Response<Map<String, String>>();
        if (payRet.getCode() == 1) {
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", payRet.getPayUrl());
            resp.setResult(resultUrl);
            // 支付请求成功，使用队列查询
//        	PayQueryMessage message = new PayQueryMessage();
//    		message.setAppId(wbConfig.getMerchantNo());
//    		message.setOutOrderNo(paymentOrder.getPaymentNo());
//    		message.setOrderNo(paymentOrder.getThirdPaymentNo());
//    		producer.sendMessage(RabbitmqConfiguration.payQueryQueueName, message);
        } else {
        	throw new ServiceException(ExceptionConstant.REQUEST_RECHARGE_EXCEPTION);
        }
        return resp;
    }

    public Response<Map<String, String>> wabenPayforalipay(BigDecimal amount, Long userId, String endType) {
        CapitalAccountDto account = accountBusiness.findByPublisherId(userId);
        if (account.getState() != null && account.getState() == 2) {
            throw new ServiceException(ExceptionConstant.CAPITALACCOUNT_FROZEN_EXCEPTION);
        }
        PublisherDto publisher = publisherBusiness.findById(userId);
        RealNameDto realNameDto = realNameBusiness.fetch(ResourceType.PUBLISHER, userId);
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
        /* 以下注释的代码为旧网贝支付系统
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String timeStamp = format.format(new Date());
        Map<String, String> map = new TreeMap<>();
        String url = WBConfig.quick_bank_url;
        map.put("merchantNo", wbConfig.getMerchantNo());
        map.put("notifyUrl", wbConfig.getNotifyUrl());
        map.put("amount", (amount.movePointRight(2)).toString());
        map.put("name", realNameDto.getName());
        map.put("tradeType", WBConfig.tradeType);
        map.put("payment", "auth");
        map.put("timeStart", timeStamp);
        map.put("outTradeNo", paymentNo);
        map.put("frontUrl", "H5".equals(endType) ? wbConfig.getH5ProxyfrontUrl() : wbConfig.getFrontUrl());
        map.put("idCard", realNameDto.getIdCard());
        String signStr = "";
        map.put("sign", "001");
        logger.info("请求的参数是{}:", map.toString());
        String result = FormRequest.doPost(map, url);
        JSONObject jsStr = JSONObject.parseObject(result);
        JSONObject result1 = jsStr.getJSONObject("result");
        logger.info("请求的结果是{}:", jsStr.toString());
        Response<Map> resp = new Response<Map>();
        if ("200".equals(jsStr.getString("code"))) {
            resp.setCode("200");
            resp.setMessage(jsStr.getString("message"));
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", WBConfig.domain + result1.getString("redirectURL"));
            resp.setResult(resultUrl);
        } else {
            resp.setCode(jsStr.getString("code"));
            resp.setMessage(jsStr.getString("message"));
        }
        return resp;
        */
        // 封装请求参数
        SwiftPayParam param = new SwiftPayParam();
        param.setAppId(wbConfig.getMerchantNo());
        param.setSubject(userId + "充值");
        param.setBody(userId + "充值" + amount + "元");
        param.setTotalFee(isProd ? amount : new BigDecimal("0.01"));
        param.setOutOrderNo(paymentNo);
        param.setFrontSkipUrl("H5".equals(endType) ? wbConfig.getH5ProxyfrontUrl() : wbConfig.getFrontUrl());
        param.setReturnUrl(wbConfig.getNotifyUrl());
        param.setTimestamp(sdf.format(new Date()));
        param.setUserId(String.valueOf(userId));
        param.setVersion("1.0");
        param.setAcctName(realNameDto.getName());
        param.setIdNum(realNameDto.getIdCard());
        SwiftPayRet payRet = WabenPayOverHttp.alipay(param, wbConfig.getKey());

        if(payRet != null && payRet.getTradeNo() != null) {
            paymentOrder.setThirdPaymentNo(payRet.getTradeNo());
            this.modifyPaymentOrder(paymentOrder);
        }
        Response<Map<String, String>> resp = new Response<Map<String, String>>();
        if (payRet.getCode() == 1) {
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", payRet.getPayUrl());
            resp.setResult(resultUrl);
            // 支付请求成功，使用队列查询
//        	PayQueryMessage message = new PayQueryMessage();
//    		message.setAppId(wbConfig.getMerchantNo());
//    		message.setOutOrderNo(paymentOrder.getPaymentNo());
//    		message.setOrderNo(paymentOrder.getThirdPaymentNo());
//    		producer.sendMessage(RabbitmqConfiguration.payQueryQueueName, message);
        } else {
            throw new ServiceException(ExceptionConstant.REQUEST_RECHARGE_EXCEPTION);
        }
        return resp;
    }


    public Response<Map> platform(BigDecimal amount, Long userId,String paytype){
        PublisherDto publisher = publisherBusiness.findById(userId);
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
        this.savePaymentOrder(paymentOrder);
        //封装请求参数

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String timeStamp = format.format(new Date());
        Map<String, String> map = new TreeMap<>();
        String url = WBConfig.quick_bank_url;
        map.put("timeStart", timeStamp);
        map.put("payment", paytype);
        map.put("notifyUrl", wbConfig.getNotifyUrl());
        map.put("tradeType", WBConfig.platformType);
        map.put("amount", (amount.movePointRight(2)).toString());
        map.put("merchantNo", wbConfig.getMerchantNo());
        map.put("outTradeNo", paymentNo);
        map.put("frontUrl", wbConfig.getFrontUrl());
        String signStr = "";
        map.put("sign", "001");
        logger.info("请求的参数是{}:", map.toString());
        String result = FormRequest.doPost(map, url);
        JSONObject jsStr = JSONObject.parseObject(result);
        JSONObject result1 = jsStr.getJSONObject("result");
        logger.info("请求的结果是{}:", jsStr.toString());
        Response<Map> resp = new Response<Map>();
        if ("200".equals(jsStr.getString("code"))) {
            resp.setCode("200");
            resp.setMessage(jsStr.getString("message"));
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", WBConfig.domain + result1.getString("result"));
            resp.setResult(resultUrl);
        } else {
            resp.setCode(jsStr.getString("code"));
            resp.setMessage(jsStr.getString("message"));
        }
        return resp;
    }


    public String protocolCallBack(HttpServletRequest request) {
        Map<String, String> result = paramter2Map(request);
        logger.info("网贝代付回调的结果是:{}", result);
        String paymentNo = result.get("outTradeNo");
        String thirdNo=result.get("transactNo");
        String sign = result.get("signValue");
        Map<String,String> checksign = new TreeMap<>(result);

        checksign.put("transactTime",checksign.get("transactTime").replaceAll("-","").replaceAll(":","").replaceAll(" ",""));
        String  signStr = "";
        for (String keys : checksign.keySet()) {
            if(!"signValue".equals(keys)){
                signStr += checksign.get(keys);
            }
        }
        signStr+= wbConfig.getKey();
        String check =  DigestUtils.md5Hex(signStr);
        //签名验证
        if (paymentNo != null && !"".equals(paymentNo)) {
            //验证签名
            if (check.equals(sign)) {
                logger.info("网贝代付异步回调");
                WithdrawalsOrderDto order = this.findByWithdrawalsNo(paymentNo);
                order.setThirdWithdrawalsNo(thirdNo);
                this.revisionWithdrawalsOrder(order);
                if (order.getState() == WithdrawalsState.PROCESSING) {
                    accountBusiness.withdrawals(order.getPublisherId(), order.getId(),WithdrawalsState.PROCESSED);
                    return "SUCCESS";
                }
            }
        }
        return "FALSE";
    }

    public String wbCallback(HttpServletRequest request) {
    	// 以下注释的代码为旧网贝支付系统
    	/*
        Map<String, String> result = paramter2Map(request);
        logger.info("网贝回调的结果是:{}", result);
        String paymentNo = result.get("outTradeNo");
        String sign = result.get("signValue");
        Map<String,String> checksign = new TreeMap<>(result);

        checksign.put("transactTime",checksign.get("transactTime").replaceAll("-","").replaceAll(":","").replaceAll(" ",""));
        String  signStr = "";
        for (String keys : checksign.keySet()) {
            if(!"signValue".equals(keys)){
                signStr += checksign.get(keys);
            }
        }
        signStr+= wbConfig.getKey();
        String check =  DigestUtils.md5Hex(signStr);
        //签名验证
        if (paymentNo != null && !"".equals(paymentNo)) {
            //验证签名
            if (check.equals(sign)) {
                logger.info("网贝快捷验证签名通过");
                payCallback(paymentNo, PaymentState.Paid);
                return "SUCCESS";
            }
        }
        return "FALSE";
        */
    	Map<String, String> result = paramter2Map(request);
        logger.info("网贝支付回调的结果是:{}", result);
        String paymentNo = result.get("out_order_no");
        String orderNo = result.get("order_no");
        String sign = result.get("sign");
        String code = result.get("code");
        // 验证签名
        String checkSign = Md5Util.md5(wbConfig.getMerchantNo() + orderNo + wbConfig.getKey()).toUpperCase();
        if(sign.equalsIgnoreCase(checkSign)) {
        	if("1".equals(code)) {
        		payCallback(paymentNo, PaymentState.Paid);
        	}
        	return "success";
        } else {
        	logger.error("网贝支付回调验证签名失败");
        	return "failed";
        }
    }

    public void payCallback(String paymentNo, PaymentState state) {
        PaymentOrderDto origin = findByPaymentNo(paymentNo);
        if (origin.getState() != state) {
            // 更新支付订单的状态
            logger.info("更新支付订单的状态");
            changeState(paymentNo, state);
            // 给发布人账号中充值
            if (state == PaymentState.Paid) {
                logger.info("给发布人账号中充值");
                accountBusiness.recharge(origin.getPublisherId(), origin.getAmount(), origin.getId());
            }
        }
    }

    public PaymentOrderDto findByPaymentNo(String paymentNo) {
        Response<PaymentOrderDto> orderResp = paymentOrderReference.fetchByPaymentNo(paymentNo);
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

    public WithdrawalsOrderDto saveWithdrawalsOrder(WithdrawalsOrderDto withdrawalsOrderDto) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.saveWithdrawalsOrders(withdrawalsOrderDto, withdrawalsOrderDto.getWithdrawalsNo());
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }
    
    public WithdrawalsOrderDto saveWithdrawalsOrders(WithdrawalsOrderDto withdrawalsOrderDto) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.addWithdrawalsOrder(withdrawalsOrderDto);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }
    
    public WithdrawalsOrderDto saveWithdrawalsOrdersAdmin(WithdrawalsOrderDto withdrawalsOrderDto) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.addWithdrawalsOrderAdmin(withdrawalsOrderDto);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }

    public WithdrawalsOrderDto findWithdrawalsOrder(String withdrawalsNo) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.fetchByWithdrawalsNo(withdrawalsNo);
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

    public WithdrawalsOrderDto revisionWithdrawalsOrder(WithdrawalsOrderDto withdrawalsOrderDto) {
        Response<WithdrawalsOrderDto> orderResp = withdrawalsOrderReference.modifyWithdrawalsOrder(withdrawalsOrderDto);
        if ("200".equals(orderResp.getCode())) {
            return orderResp.getResult();
        }
        throw new ServiceException(orderResp.getCode());
    }

    private Map<String, String> paramter2Map(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"),
            // "utf-8");
            params.put(name, valueStr);
        }
        return params;
    }

    public String genSignData(com.alibaba.fastjson.JSONObject jsonObject) {
        StringBuffer content = new StringBuffer();

        // 按照key做首字母升序排列
        List<String> keys = new ArrayList<String>(jsonObject.keySet());
        Collections.sort(keys, String.CASE_INSENSITIVE_ORDER);
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if ("sign".equals(key)) {
                continue;
            }
            String value = jsonObject.getString(key);
            // 空串不参与签名
            if (isnull(value)) {
                continue;
            }
            content.append((i == 0 ? "" : "&") + key + "=" + value);

        }
        String signSrc = content.toString();
        if (signSrc.startsWith("&")) {
            signSrc = signSrc.replaceFirst("&", "");
        }
        return signSrc;
    }

    public boolean isnull(String str) {
        if (null == str || str.equalsIgnoreCase("null") || str.equals("")) {
            return true;
        } else
            return false;
    }

	public Response<Map<String, String>> netBank(BigDecimal amount, Long publisherId, String endType) {
		CapitalAccountDto account = accountBusiness.findByPublisherId(publisherId);
    	if (account.getState() != null && account.getState() == 2) {
			throw new ServiceException(ExceptionConstant.CAPITALACCOUNT_FROZEN_EXCEPTION);
		}
        PublisherDto publisher = publisherBusiness.findById(publisherId);
        RealNameDto realNameDto = realNameBusiness.fetch(ResourceType.PUBLISHER, publisherId);
        //创建订单
        PaymentOrderDto paymentOrder = new PaymentOrderDto();
        paymentOrder.setAmount(amount);
        String paymentNo = UniqueCodeGenerator.generatePaymentNo();
        paymentOrder.setPaymentNo(paymentNo);
        paymentOrder.setType(PaymentType.UnionPay);
        paymentOrder.setState(PaymentState.Unpaid);
        paymentOrder.setPublisherId(publisher.getId());
        paymentOrder.setCreateTime(new Date());
        paymentOrder.setUpdateTime(new Date());
        paymentOrder = this.savePaymentOrder(paymentOrder);
        // 封装请求参数
        GatewayPayParam param = new GatewayPayParam();
		param.setAppId(wbConfig.getUnionpayMerchantNo());
		param.setUserId(String.valueOf(publisherId));
		param.setSubject(publisherId + "充值");
		param.setBody(publisherId + "充值" + amount + "元");
		param.setTotalFee(isProd ? amount : new BigDecimal("0.01"));
		param.setOutOrderNo(paymentNo);
		param.setFrontSkipUrl(wbConfig.getUnionpayTempFrontUrl());
		param.setReturnUrl(wbConfig.getNotifyUrl());
		param.setTimestamp(sdf.format(new Date()));
		param.setVersion("1.0");
//		param.setBankCode("01050000");
		GatewayPayRet payRet = WabenPayOverHttp.gatewayPay(param, wbConfig.getUnionpayKey());
		if(payRet != null && payRet.getTradeNo() != null) {
			paymentOrder.setThirdPaymentNo(payRet.getTradeNo());
			this.modifyPaymentOrder(paymentOrder);
		}
		
		Response<Map<String, String>> resp = new Response<Map<String, String>>();
        if (payRet.getCode() == 1) {
            Map<String, String> resultUrl = new HashMap<>();
            resultUrl.put("url", payRet.getPayUrl());
            resp.setResult(resultUrl);
        } else {
        	throw new ServiceException(ExceptionConstant.REQUEST_RECHARGE_EXCEPTION);
        }
        return resp;
	}
}
