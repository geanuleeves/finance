package com.waben.stock.futuresgateway.yisheng.exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private String code;

	private String errorMsg;

	public ServiceException() {
		super();
	}

	public ServiceException(ExceptionEnum exEnum) {
		super(exEnum.getErrorMsg());
		this.code = exEnum.getCode();
		this.errorMsg = exEnum.getErrorMsg();
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

}
