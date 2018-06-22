package com.waben.stock.futuresgateway.yisheng.rabbitmq.message;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.future.api.es.external.trade.bean.TapAPIOrderActionRsp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EsOrderActionMessage {

	private int sessionID;

	private int errorCode;

	private TapAPIOrderActionRsp info;

	public int getSessionID() {
		return sessionID;
	}

	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public TapAPIOrderActionRsp getInfo() {
		return info;
	}

	public void setInfo(TapAPIOrderActionRsp info) {
		this.info = info;
	}

}
