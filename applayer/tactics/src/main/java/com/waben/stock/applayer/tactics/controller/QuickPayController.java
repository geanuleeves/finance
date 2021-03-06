package com.waben.stock.applayer.tactics.controller;

import com.waben.stock.applayer.tactics.business.*;
import com.waben.stock.applayer.tactics.payapi.wbpay.config.WBConfig;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.commonapi.wabenpay.common.WabenBankType;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.publisher.BindCardDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.BankType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.util.PasswordCrypt;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;


@Controller
@RequestMapping("/quickpay")
public class QuickPayController {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private QuickPayBusiness quickPayBusiness;

    @Autowired
    private PublisherBusiness publisherBusiness;

    @Autowired
    private BindCardBusiness bindCardBusiness;

    @Autowired
    private CapitalAccountBusiness capitalAccountBusiness;

    @Autowired
    private PaymentBusiness paymentBusiness;
    
    @Autowired
    private WBConfig wbConfig;


    @RequestMapping("/alipay")
    @ApiOperation(value = "支付宝支付")
    @ResponseBody
    public Response<Map<String, String>> alipay(@RequestParam(required = true) BigDecimal amount, HttpServletRequest request) {
        String endType = request.getHeader("endType");
        Response<Map<String, String>> result = quickPayBusiness.wabenPayforalipay(amount, SecurityUtil.getUserId(), endType);
        return result;
    }

    @GetMapping("/sdquickpay")
    @ApiOperation(value = "杉德快捷支付")
    public String quickPay1(Model model, @RequestParam(required = true) BigDecimal amount,
                            @RequestParam(required = true) Long phone) {
        Map<String, String> map = quickPayBusiness.quickpay(amount, phone.toString());
        model.addAttribute("result", map);
        return "shandepay/payment";
    }


    @PostMapping("/sdpaycallback")
    @ApiOperation(value = "杉德支付后台回调")
    public void sdPayCallback(HttpServletRequest request, HttpServletResponse httpResp)
            throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.sdPaycallback(request);
        // 响应回调
        httpResp.setContentType("text/xml;charset=UTF-8");
        try {
            PrintWriter writer = httpResp.getWriter();
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException("http write interrupt");
        }
    }

    @GetMapping("/sdpayreturn")
    @ApiOperation(value = "杉德页面回调")
    public void sdPayReturn(HttpServletResponse httpResp) throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.sdPayReturn();
        // 响应回调
        httpResp.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter writer = httpResp.getWriter();
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException("http write interrupt");
        }
    }


    @GetMapping("/qqh5")
    @ApiOperation(value = "彩拓QQh5")
    @ResponseBody
    public Response<Map> qqh5(@RequestParam(required = true) BigDecimal amount,
                              @RequestParam(required = true) Long phone) {
        Response<Map> result = quickPayBusiness.ctQQh5(amount, phone.toString());

        return result;
    }

    @GetMapping("/jdh5")
    @ApiOperation(value = "彩拓京东h5")
    @ResponseBody
    public Response<Map> jdh5(@RequestParam(required = true) BigDecimal amount,
                              @RequestParam(required = true) Long phone) {
        Response<Map> result = quickPayBusiness.jdh5(amount, phone.toString());
        return result;
    }


    @GetMapping("/qqcallback")
    @ApiOperation(value = "彩拓QQ后台回调")
    @ResponseBody
    public String qqPayCallback(HttpServletRequest request, HttpServletResponse httpResp)
            throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.qqPaycallback(request);
        // 响应回调
        logger.info("回调响应的结果是:{}", result);
        return result;
    }

    @GetMapping("/jdcallback")
    @ApiOperation(value = "彩拓京东后台回调")
    @ResponseBody
    public String jdPayCallback(HttpServletRequest request, HttpServletResponse httpResp)
            throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.jdPaycallback(request);
        // 响应回调
        logger.info("回调响应的结果是:{}", result);
        return result;
    }

    @GetMapping("/qqpayreturn")
    @ApiOperation(value = "彩拓QQ支付页面回调")
    public void qqPayReturn(HttpServletResponse httpResp) throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.sdPayReturn();
        // 响应回调
        httpResp.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter writer = httpResp.getWriter();
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException("http write interrupt");
        }
    }

    @GetMapping("/jdpayreturn")
    @ApiOperation(value = "彩拓京东页面回调")
    public void jdPayReturn(HttpServletResponse httpResp) throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.sdPayReturn();
        // 响应回调
        httpResp.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter writer = httpResp.getWriter();
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException("http write interrupt");
        }
    }
    
    /**************************网贝支付**********************/

    @RequestMapping("/quickbank")
    @ApiOperation(value = "网贝收银台支付调接口")
    @ResponseBody
    public Response<Map<String, String>> quickBank(@RequestParam(required = true) BigDecimal amount, HttpServletRequest request) {
    	String endType = request.getHeader("endType");
        Response<Map<String, String>> result = quickPayBusiness.wabenPay(amount, SecurityUtil.getUserId(), endType);
        return result;
    }

    @RequestMapping("/platform")
    @ApiOperation(value = "网贝收银台支付调接口")
    @ResponseBody
    public Response<Map> platform(@RequestParam(required = true) BigDecimal amount,String paytype) {
//        Response<Map> result = quickPayBusiness.platform(amount, SecurityUtil.getUserId(),paytype);
        return null;
    }

    @RequestMapping("/wbreturn")
    @ApiOperation(value = "网贝收银台同步回调接口")
    @ResponseBody
    public void callback(HttpServletResponse httpResp) throws UnsupportedEncodingException {
        // 处理回调
        String result = quickPayBusiness.sdPayReturn();
        // 响应回调
        httpResp.setContentType("text/html;charset=UTF-8");
        try {
            PrintWriter writer = httpResp.getWriter();
            writer.write(result);
        } catch (IOException e) {
            throw new RuntimeException("http write interrupt");
        }
    }
    
    @RequestMapping("/h5wbreturn")
    @ApiOperation(value = "网贝收银台同步回调接口")
    @ResponseBody
    public void h5Callback(HttpServletResponse httpResp) throws UnsupportedEncodingException {
    	try {
			httpResp.sendRedirect(wbConfig.getH5FrontUrl());
		} catch (IOException e) {
		}
    }

    @RequestMapping("/wbcallback")
    @ApiOperation(value = "网贝收银台异步回调接口")
    @ResponseBody
    public String notify(HttpServletRequest request) {
        String result = quickPayBusiness.wbCallback(request);
        return result;
    }

    @PostMapping("/wbcsa")
    @ApiOperation(value = "网贝提现")
    @ResponseBody
    public Response<String> wbcsa(@RequestParam(required = true) BigDecimal amount,
                                          @RequestParam(required = true) Long bindCardId, @RequestParam(required = true) String paymentPassword) {
        // 判断是否为测试用户，测试用户不能提现
        PublisherDto publisher = publisherBusiness.findById(SecurityUtil.getUserId());
        if (publisher.getIsTest() != null && publisher.getIsTest()) {
            throw new ServiceException(ExceptionConstant.TESTUSER_NOWITHDRAWALS_EXCEPTION);
        }
        // 验证支付密码
        CapitalAccountDto capitalAccount = capitalAccountBusiness.findByPublisherId(SecurityUtil.getUserId());
        String storePaymentPassword = capitalAccount.getPaymentPassword();
        if (storePaymentPassword == null || "".equals(storePaymentPassword)) {
            throw new ServiceException(ExceptionConstant.PAYMENTPASSWORD_NOTSET_EXCEPTION);
        }
        if (!PasswordCrypt.match(paymentPassword, storePaymentPassword)) {
            throw new ServiceException(ExceptionConstant.PAYMENTPASSWORD_WRONG_EXCEPTION);
        }
        // 检查余额
        if (amount.compareTo(capitalAccount.getAvailableBalance()) > 0) {
            throw new ServiceException(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION);
        }
        Response<String> resp = new Response<String>();
        BindCardDto bindCard = bindCardBusiness.findById(bindCardId);
        // CzBankType bankType = CzBankType.getByPlateformBankType(BankType.getByBank(bindCard.getBankName()));
        WabenBankType bankType = WabenBankType.getByPlateformBankType(BankType.getByCode(bindCard.getBankCode()));
        if (bankType == null) {
            throw new ServiceException(ExceptionConstant.BANKCARD_NOTSUPPORT_EXCEPTION);
        }
        logger.info("验证通过,提现开始");
        quickPayBusiness.wbWithdrawals(SecurityUtil.getUserId(), amount, bindCard.getName(), bindCard.getPhone(),
                bindCard.getIdCard(), bindCard.getBankCard(), bankType.getCode(), bankType.getBank());
        resp.setResult("success");
        return resp;
    }

    @RequestMapping("/protocolcallback")
    @ApiOperation(value = "网贝提现异步通知")
    @ResponseBody
    public String protocolCallBack(HttpServletRequest request){
        String result = quickPayBusiness.protocolCallBack(request);
        return result;
    }

}
