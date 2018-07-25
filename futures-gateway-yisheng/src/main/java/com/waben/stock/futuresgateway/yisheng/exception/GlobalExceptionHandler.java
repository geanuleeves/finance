package com.waben.stock.futuresgateway.yisheng.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.waben.stock.futuresgateway.yisheng.pojo.Response;

@ControllerAdvice(basePackages = { "com.waben.stock.futuresgateway.yisheng" })
public class GlobalExceptionHandler {

	Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(ServiceException.class)
	@ResponseBody
	public Response<? extends Object> exceptionHandler(ServiceException ex, HttpServletRequest request,
			HttpServletResponse response) {
		ex.printStackTrace();
		Response<? extends Object> result = new Response<>(null);
		result.setCode(ex.getCode());
		result.setMessage(ex.getErrorMsg());
		return result;
	}

	@ExceptionHandler(Exception.class)
	@ResponseBody
	public Response<? extends Object> exceptionHandler(Exception ex, HttpServletRequest request,
			HttpServletResponse response) {
		ex.printStackTrace();
		String message = ExceptionEnum.Unknow.getErrorMsg() + ":" + ex.getMessage();
		String servletPath = request.getServletPath();
		logger.error(String.format("%s:%s:%s", message, servletPath, ex.getMessage()), ex);
		Response<? extends Object> result = new Response<>(null);
		result.setCode(ExceptionEnum.Unknow.getCode());
		result.setMessage(message);
		return result;
	}

}
