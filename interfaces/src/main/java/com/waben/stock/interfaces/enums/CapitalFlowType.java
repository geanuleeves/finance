package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 资金流水 类型
 * 
 * @author lma
 *
 */
public enum CapitalFlowType implements CommonalityEnum {

	/****************** 充值、提现 *******************/

	Recharge("1", "充值"),

	Withdrawals("2", "提现"),

	/****************** 股票 *******************/

	ServiceFee("3", "服务费"),

	ReserveFund("4", "冻结履约保证金"),

	DeferredCharges("5", "递延费"),

	ReturnReserveFund("6", "退回履约保证金"),

	Loss("7", "亏损"),

	Profit("8", "盈利"),

	Promotion("9", "推广佣金"),

	Revoke("10", "撤单退款"),

	ReturnDeferredCharges("11", "退回递延费"),

	/****************** 期权 *******************/

	RightMoney("12", "期权权利金"),

	ReturnRightMoney("13", "退回期权权利金"),

	StockOptionProfit("14", "期权盈利"),

	/****************** 期货 *******************/

	FuturesServiceFee("15", "期货交易手续费"),

	FuturesReserveFund("16", "冻结期货履约保证金"),

	FuturesReturnReserveFund("17", "退回期货履约保证金"),

	FuturesRevoke("18", "撤单退回交易手续费"),

	FuturesOvernightDeferredFee("19", "期货隔夜递延费"),

	FuturesOvernightReserveFund("20", "期货隔夜保证金"),

	FuturesReturnOvernightReserveFund("21", "退回期货隔夜保证金"),

	FuturesLoss("22", "期货亏损"),

	FuturesProfit("23", "期货盈利"),
	
	AdminOperation("24", "管理员操作");

	private String index;

	private String type;

	private CapitalFlowType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, CapitalFlowType> valueMap = new HashMap<String, CapitalFlowType>();

	static {
		for (CapitalFlowType _enum : CapitalFlowType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static CapitalFlowType getByIndex(String index) {
		CapitalFlowType result = valueMap.get(index);
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + index);
		}
		return result;
	}

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
