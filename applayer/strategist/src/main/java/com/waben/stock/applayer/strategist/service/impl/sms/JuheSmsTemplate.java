package com.waben.stock.applayer.strategist.service.impl.sms;

import java.util.HashMap;
import java.util.Map;

import com.waben.stock.interfaces.enums.CommonalityEnum;
import com.waben.stock.interfaces.enums.SmsType;

public enum JuheSmsTemplate implements CommonalityEnum {

	RegistVerificationCode("1", "56142", "注册验证码", new String[] { "code" }),

	ModifyPasswordCode("2", "56142", "修改密码验证码", new String[] { "code" }),

	BindCardCode("3", "56142", "绑定银行卡验证码", new String[] { "code" }),

	StrategyWarning("4", "50597", "点买持仓提醒", new String[] { "stgyName", "delayDay" });

	private String index;

	private String templateCode;

	private String name;

	private String[] paramNames;

	private JuheSmsTemplate(String index, String templateCode, String name, String[] paramNames) {
		this.index = index;
		this.templateCode = templateCode;
		this.name = name;
		this.paramNames = paramNames;
	}

	private static Map<String, JuheSmsTemplate> valueMap = new HashMap<String, JuheSmsTemplate>();

	static {
		for (JuheSmsTemplate _enum : JuheSmsTemplate.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static JuheSmsTemplate getByIndex(Integer index) {
		JuheSmsTemplate result = valueMap.get(index);
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + index);
		}
		return result;
	}

	public static JuheSmsTemplate getBySmsType(SmsType smsType) {
		JuheSmsTemplate result = valueMap.get(smsType.getIndex());
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + smsType.getIndex());
		}
		return result;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getTemplateCode() {
		return templateCode;
	}

	public void setTemplateCode(String templateCode) {
		this.templateCode = templateCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getParamNames() {
		return paramNames;
	}

	public void setParamNames(String[] paramNames) {
		this.paramNames = paramNames;
	}

}
