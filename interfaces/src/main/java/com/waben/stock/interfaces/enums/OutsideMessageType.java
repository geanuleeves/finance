package com.waben.stock.interfaces.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * 站外消息类型
 * 
 * @author lma
 *
 */
public enum OutsideMessageType implements CommonalityEnum {

	/****************** 股票相关推送 *****************/

	BUY_POSTED("1", "已发布"),

	BUY_BUYLOCK("2", "买入锁定"),

	BUY_HOLDPOSITION("3", "持仓中"),

	BUY_SELLAPPLY("4", "卖出申请"),

	BUY_SELLLOCK("5", "卖出锁定"),

	BUY_UNWIND("6", "已平仓"),

	BUY_BUYFAILED("7", "买入失败"),

	BUY_SELLFAILED("8", "卖出失败"),

	/****************** 充值、提现相关推送 *****************/

	ACCOUNT_RECHARGESUCCESS("9", "充值成功"),

	ACCOUNT_WITHDRAWALSSUCCESS("10", "提现成功"),

	ACCOUNT_WITHDRAWALFAILED("11", "提现失败"),

	/****************** 期权相关推送 *****************/

	OPTION_WAITCONFIRMED("12", "待确认"),

	OPTION_FAILURE("13", "申购失败"),

	OPTION_TURNOVER("14", "持仓中"),

	OPTION_INSETTLEMENT("15", "结算中"),

	OPTION_SETTLEMENTED("16", "已结算"),

	/****************** START 期货相关推送 *****************/
	
	/***** 持仓通知 *****/
	
	Futures_Position("17", "持仓中"),
	
	/***** 平仓通知 *****/
	
	Futures_ReachLossPoint("18", "达到止损平仓"),
	
	Futures_ReachProfitPoint("19", "达到止盈平仓"),
	
	Futures_ApplyUnwind("20", "手动申请平仓"),
	
	Futures_DayUnwind("21", "日内平仓"),
	
	Futures_ReachStrongPoint("22", "达到强平点平仓"),
	
	Futures_ReachContractExpiration("23", "合约到期平仓"),
	
	/***** 委托通知 *****/
	
	Futures_BuyingEntrust("24", "委托中"),
	
	Futures_EntrustPosition("25", "委托持仓中"),
	
	Futures_BuyingCanceled("26", "委托取消"),
	
	Futures_BuyingFailure("27", "委托失败"),
	
	/***** 警戒线通知 *****/

	Futures_Warning("28", "触发警戒线"),
	
	/***** 资金通知 *****/
	
	Account_FuturesServiceFee("29", "期货交易手续费"),
	
	Account_FuturesReserveFund("30", "冻结期货履约保证金"),
	
	Account_FuturesReturnReserveFund("31", "退回期货履约保证金"),
	
	Account_FuturesOvernightDeferredFee("32", "期货隔夜递延费"),
	
	Account_FuturesOvernightReserveFund("33", "期货隔夜保证金"),
	
	Account_FuturesReturnOvernightReserveFund("34", "退回期货隔夜保证金"),
	
	Account_FuturesLoss("35", "期货盈亏"),
	
	Account_FuturesProfit("36", "期货盈利"),
	
	Admin_Back("37", "后台消息推送");
	
	/****************** END 期货相关推送 *****************/

	private String index;
	private String type;

	OutsideMessageType(String index, String type) {
		this.index = index;
		this.type = type;
	}

	private static Map<String, OutsideMessageType> valueMap = new HashMap<>();

	static {
		for (OutsideMessageType _enum : OutsideMessageType.values()) {
			valueMap.put(_enum.getIndex(), _enum);
		}
	}

	public static OutsideMessageType getByType(String index) {
		OutsideMessageType result = valueMap.get(index);
		if (result == null) {
			throw new IllegalArgumentException("No element matches " + index);
		}
		return result;
	}

	@Override
	public String getIndex() {
		return index;
	}

	public String getType() {
		return type;
	}
}
