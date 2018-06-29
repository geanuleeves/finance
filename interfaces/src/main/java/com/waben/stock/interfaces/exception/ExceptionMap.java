package com.waben.stock.interfaces.exception;

import java.util.HashMap;
import java.util.Map;

import com.waben.stock.interfaces.constants.ExceptionConstant;

public class ExceptionMap {

	public static Map<String, String> exceptionMap = new HashMap<String, String>();

	static {
		exceptionMap.put(ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION, "服务通讯异常");
		exceptionMap.put(ExceptionConstant.UNKNOW_EXCEPTION, "服务器未知异常");
		exceptionMap.put(ExceptionConstant.DATANOTFOUND_EXCEPTION, "数据没有找到");
		exceptionMap.put(ExceptionConstant.ARGUMENT_EXCEPTION, "参数异常");
		exceptionMap.put(ExceptionConstant.SECURITY_METHOD_UNSUPPORT_EXCEPTION, "安全验证方法不支持异常");

		exceptionMap.put(ExceptionConstant.MENU_SERVICE_EXCEPTION, "菜单服务异常");
		exceptionMap.put(ExceptionConstant.STAFF_NOT_FOUND_EXCEPTION, "员工信息未找到");
		exceptionMap.put(ExceptionConstant.ROLE_NOT_FOUND_EXCEPTION, "角色信息未找到");
		exceptionMap.put(ExceptionConstant.PERMISSION_NOT_FOUND_EXCEPTION, "权限信息未找到");

		exceptionMap.put(ExceptionConstant.SENDMESSAGE_FAILED_EXCEPTION, "发送短信失败");
		exceptionMap.put(ExceptionConstant.SENDMESSAGE_INTERVAL_TOOSHORT_EXCEPTION, "短信发送间隔时间太短");
		exceptionMap.put(ExceptionConstant.VERIFICATIONCODE_INVALID_EXCEPTION, "验证码错误或者验证码已过期");
		exceptionMap.put(ExceptionConstant.PHONE_BEEN_REGISTERED_EXCEPTION, "该手机号已被注册");
		exceptionMap.put(ExceptionConstant.PHONE_WRONG_EXCEPTION, "错误的手机号码");
		exceptionMap.put(ExceptionConstant.USERNAME_OR_PASSWORD_ERROR_EXCEPTION, "用户名或者密码错误");
		exceptionMap.put(ExceptionConstant.PHONE_ISNOT_REGISTERED_EXCEPTION, "该手机号尚未注册");
		exceptionMap.put(ExceptionConstant.BANKCARD_ALREADY_BIND_EXCEPTION, "该银行卡已绑定，不能重复绑定");
		exceptionMap.put(ExceptionConstant.STOCK_ALREADY_FAVORITE_EXCEPTION, "该股票已收藏，不能重复收藏");
		exceptionMap.put(ExceptionConstant.ORIGINAL_PASSWORD_MISMATCH_EXCEPTION, "原始密码不匹配");
		exceptionMap.put(ExceptionConstant.PHONE_MISMATCH_EXCEPTION, "手机号不匹配");
		exceptionMap.put(ExceptionConstant.MODIFY_PAYMENTPASSWORD_NEEDVALIDCODE_EXCEPTION, "已经设置过支付密码，修改支付密码需要验证码");
		exceptionMap.put(ExceptionConstant.CREDITCARD_NOTSUPPORT_EXCEPTION, "不能绑定信用卡");
		exceptionMap.put(ExceptionConstant.BANKCARD_NOTRECOGNITION_EXCEPTION, "未找到该卡号对应的银行，请检查输入的信息是否正确");
		exceptionMap.put(ExceptionConstant.BANKCARD_NOTSUPPORT_EXCEPTION, "不支持的银行卡号");
		exceptionMap.put(ExceptionConstant.PUBLISHERID_NOTMATCH_EXCEPTION, "用户不匹配");
		exceptionMap.put(ExceptionConstant.BANKCARDINFO_NOTMATCH_EXCEPTION, "银行卡信息有误，请检查输入的信息是否正确");
		exceptionMap.put(ExceptionConstant.BANKCARDINFO_WRONG_EXCEPTION, "信息输入有误");
		exceptionMap.put(ExceptionConstant.REALNAME_EXIST_EXCEPTION, "已实名认证，不能重复操作");
		exceptionMap.put(ExceptionConstant.REALNAME_WRONG_EXCEPTION, "实名认证信息错误");
		exceptionMap.put(ExceptionConstant.ORGCODE_NOTEXIST_EXCEPTION, "代理商代码不存在");
		exceptionMap.put(ExceptionConstant.BANKCARD_ALREADY_USERED_EXCEPTION, "该银行卡已被使用");
		exceptionMap.put(ExceptionConstant.REALNAME_ALREADY_USERED_EXCEPTION, "该实名信息已被使用");
		exceptionMap.put(ExceptionConstant.CAPITALACCOUNT_FROZEN_EXCEPTION, "资金账户已冻结，不能执行资金相关的操作");
		exceptionMap.put(ExceptionConstant.PUBLISHER_DISABLED_EXCEPITON, "您的账号已被冻结无法登录");
		exceptionMap.put(ExceptionConstant.NOTREALNAME_EXEPTION, "您的账户尚未实名认证");
		exceptionMap.put(ExceptionConstant.IDCARD_FORMAT_WRONG_EXCEPTION, "身份证号码格式有误");
		exceptionMap.put(ExceptionConstant.AGENOTBETTEN18AND65_EXCEPTION, "年龄必须介于18~65周岁");

		exceptionMap.put(ExceptionConstant.AVAILABLE_BALANCE_NOTENOUGH_EXCEPTION, "账户可用余额不足");
		exceptionMap.put(ExceptionConstant.BUYRECORD_ISNOTLOCK_EXCEPTION, "买入或者卖出前需进行锁定操作");
		exceptionMap.put(ExceptionConstant.BUYRECORD_INVESTORID_NOTMATCH_EXCEPTION, "投资人必须和买入锁定时的投资人一致");
		exceptionMap.put(ExceptionConstant.BUYRECORD_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION, "点买记录状态不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.BUYRECORD_PUBLISHERID_NOTMATCH_EXCEPTION, "申请卖出发布人不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.BUYRECORD_NOT_FOUND_EXCEPTION, "点买记录不存在");
		exceptionMap.put(ExceptionConstant.BUYRECORD_POST_DEBITFAILED_EXCEPTION, "点买发布扣款发生异常，如账户已扣款，请联系客服人员");
		exceptionMap.put(ExceptionConstant.PAYMENTPASSWORD_NOTSET_EXCEPTION, "支付密码未设置");
		exceptionMap.put(ExceptionConstant.PAYMENTPASSWORD_WRONG_EXCEPTION, "支付密码错误");
		exceptionMap.put(ExceptionConstant.BUYRECORD_SAVE_EXCEPTION, "点买异常");
		exceptionMap.put(ExceptionConstant.BUYRECORD_SELLAPPLY_NOTMATCH_EXCEPTION, "申请卖出只能由发布人本人操作");
		exceptionMap.put(ExceptionConstant.BUYRECORD_RETURNRESERVEFUND_EXCEPTION, "退回保证金发生异常");
		exceptionMap.put(ExceptionConstant.BUYRECORD_NONTRADINGPERIOD_EXCEPTION, "非交易时间段");
		exceptionMap.put(ExceptionConstant.STOCK_SUSPENSION_EXCEPTION, "该股票已停牌");
		exceptionMap.put(ExceptionConstant.WITHDRAWALS_EXCEPTION, "提现失败");
		exceptionMap.put(ExceptionConstant.RECHARGE_EXCEPTION, "充值失败");
		exceptionMap.put(ExceptionConstant.BUYRECORD_USERNOTDEFERRED_EXCEPTION, "用户选择不递延，不能进行递延操作");
		exceptionMap.put(ExceptionConstant.BUYRECORD_ALREADY_DEFERRED_EXCEPTION, "该点买记录已经递延过了，不能重复递延");
		exceptionMap.put(ExceptionConstant.TESTUSER_NOWITHDRAWALS_EXCEPTION, "当前用户为测试用户，不能进行提现操作");
		exceptionMap.put(ExceptionConstant.STRATEGYQUALIFY_NOTENOUGH_EXCEPTION, "当前用户不能参与该策略，或该策略为一次性参与活动且当前用户已经参与");
		exceptionMap.put(ExceptionConstant.APPLYAMOUNT_NOTENOUGH_BUYSTOCK_EXCEPTION, "申请市值不足购买一手");
		exceptionMap.put(ExceptionConstant.USERSELLAPPLY_NOTMATCH_EXCEPTION, "持仓第二天之后才能申请卖出");
		exceptionMap.put(ExceptionConstant.BUYRECORD_REVOKE_NOTSUPPORT_EXCEPTION, "买入中和买入锁定状态下才能撤单");
		exceptionMap.put(ExceptionConstant.DEVELOPSTOCK_NOTSUPPORT_EXCEPTION, "不支持购买创业板的股票");
		exceptionMap.put(ExceptionConstant.UNIONPAY_SINGLELIMIT_EXCEPTION, "银联支付单笔最大额度为5000");
		exceptionMap.put(ExceptionConstant.STOCK_ARRIVEUPLIMIT_EXCEPTION, "该股票已涨停，不能购买");
		exceptionMap.put(ExceptionConstant.STOCK_ARRIVEDOWNLIMIT_EXCEPTION, "该股票已跌停，不能购买");
		exceptionMap.put(ExceptionConstant.ST_STOCK_CANNOTBUY_EXCEPTION, "ST股无法申购请购买其它股票");
		exceptionMap.put(ExceptionConstant.BLACKLIST_STOCK_EXCEPTION, "不支持的股票，请更换股票");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_2UPLIMIT_CANNOTBY_EXCEPTION, "连续两个涨停的股票不能申购");
		exceptionMap.put(ExceptionConstant.REQUEST_RECHARGE_EXCEPTION, "请求充值失败");

		exceptionMap.put(ExceptionConstant.INVESTOR_NOT_FOUND_EXCEPTION, "投资人信息未找到");
		exceptionMap.put(ExceptionConstant.INVESTOR_SECURITIES_LOGIN_EXCEPTION, "投资人券商账户登陆异常");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKACCOUNT_MONEY_NOT_ENOUGH, "投资人券商账户资金账户余额不足");
		exceptionMap.put(ExceptionConstant.INVESTOR_EXCHANGE_TYPE_NOT_SUPPORT_EXCEPTION, "投资人券商账户不支持当前股票交易");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKACCOUNT_NOT_EXIST, "投资人券商账户没有可用的股东账户");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKENTRUST_BUY_ERROR, "投资人券商账户委托下单失败");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKENTRUST_FETCH_ERROR, "投资人券商账户委托单查询异常");

		exceptionMap.put(ExceptionConstant.USER_ROLE_EXCEPITON, "该角色已被使用,不能删除。");
		exceptionMap.put(ExceptionConstant.AGENT_DISABLED_EXCEPITON, "当前用户已被冻结");
		exceptionMap.put(ExceptionConstant.ORGANIZATION_NOTEXIST_EXCEPTION, "代理商不存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATIONCATEGORY_NOTEXIST_EXCEPTION, "代理商类别不存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATION_USER_NOT_FOUND, "代理商用户不存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATION_USER_EXIST, "用户名已存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATIONACCOUNT_OLDPAYMENTPASSWORD_NOTMATCH_EXCEPTION, "原始支付密码不匹配");
		exceptionMap.put(ExceptionConstant.ORGUSER_OLDPASSWORD_NOTMATCH_EXCEPTION, "原始登陆密码不匹配");
		exceptionMap.put(ExceptionConstant.ORGPUBLISHER_EXIST_EXCEPTION, "该发布人已绑定过代理商代理");
		exceptionMap.put(ExceptionConstant.WITHDRAWALSAPPLY_NOTSUPPORTED_EXCEPTION, "尚未绑卡，不能申请提现");
		exceptionMap.put(ExceptionConstant.ORGNAME_EXIST_EXCEPTION, "代理商名称已存在");
		exceptionMap.put(ExceptionConstant.LEVELONE_CANNOT_WITHDRAWAL_EXCEPTION, "一级代理商不能申请提现");
		exceptionMap.put(ExceptionConstant.BALANCE_NOTENOUGHFROZEN_EXCEPTION, "账户余额不足以冻结");
		exceptionMap.put(ExceptionConstant.SETTLEMENT_METHOD_EXCEPITON, "该结算下代理商尚有未完成订单，请完成订单后再切换结算方式!");
		exceptionMap.put(ExceptionConstant.FORM_RATIO_EXCEPITON, "上级的比例不能为空,请设置上级表单比例");
		exceptionMap.put(ExceptionConstant.FORM_RATIO_COMPARE_EXCEPITON, "当前的表单的比例不能大于上级的比例");
		exceptionMap.put(ExceptionConstant.ROLE_EXISTENCE_EXCEPITON, "该角色已存在，请重新输入！");
		exceptionMap.put(ExceptionConstant.RAKEBACK_RATIO_WRONG_EXCEPTION, "返佣比例设置错误，当前比例不能大于上级比例");
		exceptionMap.put(ExceptionConstant.PROCESSFEE_NOT_ENOUGH_EXCEPTION, "提现金额不足以支付提现手续费");
		exceptionMap.put(ExceptionConstant.MODIFY_DISABLED_EXCEPITON, "修改不成功，该用户有未完成订单");

		exceptionMap.put(ExceptionConstant.STOCKOPTION_AMOUNTMUSTGT20WAN_EXCEPTION, "名义本金20万起，且必须为10万的整数倍");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION,
				"期权交易状态不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_PUBLISHERID_NOTMATCH_EXCEPTION, "自主行权发布人不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.USERRIGHT_NOTMATCH_EXCEPTION, "T+3才能申请行权");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_QUOTENOTFOUND_EXCEPTION, "该股票暂时无法申购，请购买其它股票");
		exceptionMap.put(ExceptionConstant.INQUIRY_RESULT_NOT_FOUND, "期权询价结果不存在");
		exceptionMap.put(ExceptionConstant.NONTRADINGDAY_EXCEPTION, "非交易日不能申请行权");
		exceptionMap.put(ExceptionConstant.STOCK_ABNORMAL_EXCEPTION, "该股票暂时无法申购，请购买其它股票");
		exceptionMap.put(ExceptionConstant.STOCK_AMOUNTLIMIT_EXCEPTION, "今日额度已用完，明日09：00开售");

		exceptionMap.put(ExceptionConstant.NO_LIVEPLAYER_EXCEPTION, "无直播频道");

		exceptionMap.put(ExceptionConstant.INSUFFICIENT_NUMBER_OF_DRAW, "抽奖次数不足");
		exceptionMap.put(ExceptionConstant.OVERSTEP_NUMBER_OF_DRAW, "今日抽奖已达上限");
		exceptionMap.put(ExceptionConstant.PRIZE_IS_EMPTY, "奖品已空");
		exceptionMap.put(ExceptionConstant.FAILED_TO_UPLOAD_PICTURES, "上传图片失败");

		exceptionMap.put(ExceptionConstant.FUTURESORDER_STATE_NOTMATCH_EXCEPTION, "期货订单状态不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.SINGLE_TRANSACTION_QUANTITY_EXCEPTION, "单笔交易数量过大");
		exceptionMap.put(ExceptionConstant.UPPER_LIMIT_HOLDING_CAPACITY_EXCEPTION, "该用户持仓量已达上限");
		exceptionMap.put(ExceptionConstant.CONTRACT_HOLDING_CAPACITY_INSUFFICIENT_EXCEPTION, "选择交易数量大于用户持仓总量");
		exceptionMap.put(ExceptionConstant.CONTRACT_DOESNOT_EXIST_EXCEPTION, "该合约不存在");
		exceptionMap.put(ExceptionConstant.CONTRACT_ABNORMALITY_EXCEPTION, "该合约异常不可用");
		exceptionMap.put(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION, "不在交易时间段，无法操作");
		exceptionMap.put(ExceptionConstant.CONTRACTTERM_NOTAVAILABLE_EXCEPTION, "没有可用的期货合约期限，暂时不能交易");
		exceptionMap.put(ExceptionConstant.EXCHANGE_ISNOT_AVAILABLE_EXCEPTION, "该合约交易所不可用");
		exceptionMap.put(ExceptionConstant.GATEWAY_DOESNOT_SUPPORT_CONTRACT_EXCEPTION, "期货网关不支持该合约");
		exceptionMap.put(ExceptionConstant.FUTURESORDER_PARTSUCCESS_CANNOTCANCEL_EXCEPTION, "委托已部分成交，不能取消");
		exceptionMap.put(ExceptionConstant.FUTURESAPI_ABNORMAL_EXCEPTION, "API网关异常，请稍后再试");
		exceptionMap.put(ExceptionConstant.FUTURESAPI_NOTCONNECTED_EXCEPTION, "API网关异常，请稍后再试");
		exceptionMap.put(ExceptionConstant.FUTURESAPI_CANCELFAILED_EXCEPTION, "委托取消失败，请稍后再试或者联系客服");
		exceptionMap.put(ExceptionConstant.ORDER_DOESNOT_EXIST_EXCEPTION, "订单不存在");
		exceptionMap.put(ExceptionConstant.FUTURESORDER_BACKHAND_BALANCENOTENOUGH_EXCEPTION, "市价反手账户余额不足");
		exceptionMap.put(ExceptionConstant.PARTCONTRACT_NOTINTRADETIME_EXCEPTION, "一键平仓部分订单在非交易时间段");
		exceptionMap.put(ExceptionConstant.BACKHANDSOURCEORDER_NOTUNWIND_EXCEPTION, "反手源订单暂未平仓");
		exceptionMap.put(ExceptionConstant.FUTURESORDER_ALREADYBACKHAND_EXCEPTION, "订单已反手，不能重复反手");
		exceptionMap.put(ExceptionConstant.CONTRACTTERM_ORDER_OCCUPIED_EXCEPTION, "合约在被订单使用");
		exceptionMap.put(ExceptionConstant.CONTRACT_CORDON_UNITUNWINDPOINT_EXCEPTION, "警戒线低于强平点");
		exceptionMap.put(ExceptionConstant.CONTRACTTERM_ISCURRENT_EXCEPTION, "品种没有可用的合约");
		exceptionMap.put(ExceptionConstant.CONTRACT_PREQUANTITY_EXCEPTION, "合约没有预设置手数");
		exceptionMap.put(ExceptionConstant.NOT_OPEN_GRANARY_PROVIDE_RELIEF_EXCEPTION, "当前时间该交易品种禁止开仓");
		exceptionMap.put(ExceptionConstant.CLOSE_POSITION_EXCEPTION, "当前时间该交易品种禁止平仓");
		exceptionMap.put(ExceptionConstant.USER_ORDER_DOESNOT_EXIST_EXCEPTION, "该用户订单不存在");
		exceptionMap.put(ExceptionConstant.ORDER_HAS_BEEN_CLOSED_EXCEPTION, "该订单已平仓不能修改");

		exceptionMap.put(ExceptionConstant.COST_MARGIN_CANNOT_LOWER_GLOBAL_SETTING_EXCEPTION, "%s成本保证金不能比上级设置的%s低");
		exceptionMap.put(ExceptionConstant.COST_OPENINGCHARGES_CANNOT_LOWER_GLOBAL_SETTING_EXCEPTION,
				"%s成本开仓手续费不能比上级设置的%s低");
		exceptionMap.put(ExceptionConstant.SALES_OPENINGFEE_CANNOT_LOWER_COST_OPENINGFEE_EXCEPTION,
				"%s销售开仓手续费不能比成本开仓手续费的低");
		exceptionMap.put(ExceptionConstant.COST_NOT_LOWER_OVERALL_SETTING_EXCEPTION, "%s成本平仓手续费不能比上级设置的%s低");
		exceptionMap.put(ExceptionConstant.SALES_CLOSINGFEE_CANNOT_LOWER_COST_OPENINGFEE_EXCEPTION,
				"%s销售平仓手续费不能比成本开仓手续费的低");
		exceptionMap.put(ExceptionConstant.COST_DEFERREDFEE_SHOULD_NOT_LOWER_GLOBAL_SETTING_EXCEPTION,
				"%s成本递延费不能比上级设置的%s低");
		exceptionMap.put(ExceptionConstant.SALES_DEFERRED_CHARGES_CANNOT_LOWER_COST_DEFERRED_CHARGES_EXCEPTION,
				"%s销售递延费不能比成本递延费的低");
		exceptionMap.put(ExceptionConstant.CONTRACT_PARAMETER_INCOMPLETE_EXCEPTION, "合约参数不全");
		exceptionMap.put(ExceptionConstant.COMMODITY_TRADETIME_ISNULL_EXCEPTION, "品种没有设置交易时间");

		exceptionMap.put(ExceptionConstant.NOT_GLOBAL_COST_PRICE_ISSET_EXCEPTION, "没有设置全局成本价");
		exceptionMap.put(ExceptionConstant.COST_MARGIN_CANNOT_LOWER_THAN_GLOBAL_SETTING_EXCEPTION,
				"%s成本保证金不能比全局设置的%s低");
		exceptionMap.put(ExceptionConstant.COST_OPENING_WAREHOUSE_SHOULDNOT_LOWER_OVERALL_SETTING_EXCEPTION,
				"%s成本开仓手续费不能比全局设置的%s低");
		exceptionMap.put(ExceptionConstant.COST_ISNOT_LOWER_OVERALL_SETTING_EXCEPTION, "%s成本平仓手续费不能比全局设置的%s低");
		exceptionMap.put(ExceptionConstant.COST_DEFERREDFEE_SHOULD_NOTBE_LOWER_GLOBAL_SETTING_EXCEPTION,
				"%s成本递延费不能比全局设置的%s低");
		exceptionMap.put(ExceptionConstant.CONTRACT_COMMODITYID_ISNULL_EXCEPTION, "合约没有选择品种");
		exceptionMap.put(ExceptionConstant.MAXIMUM_CAPACITY_USER_EMPTY_EXCEPTION, "用户最大可持仓量为空");
		exceptionMap.put(ExceptionConstant.USER_SINGLE_MAXIMUM_CAPACITY_USER_EMPTY_EXCEPTION, "用户单笔最大可交易数量为空");
		exceptionMap.put(ExceptionConstant.COMMODITY_HAVING_CONTRACT_EXCEPTION, "品种绑定有合约，请先删除合约");
		exceptionMap.put(ExceptionConstant.CURRENTSTATUS_CANNOTCANCEL_EXCEPTION, "当前订单状态已排队，不能被取消");

		exceptionMap.put(ExceptionConstant.TOTAL_AMOUNT_BUYUP_CAPACITY_INSUFFICIENT_EXCEPTION, "买涨持仓总额度已达上限");
		exceptionMap.put(ExceptionConstant.TOTAL_AMOUNT_BUYFULL_CAPACITY_INSUFFICIENT_EXCEPTION, "买跌持仓总额度已达上限");
		exceptionMap.put(ExceptionConstant.MIN_PLACE_ORDER_EXCEPTION, "合约临近到期，不能下单");
		exceptionMap.put(ExceptionConstant.WINDCONTROL_PARAMETERS_ISNUM_EXCEPTION, "提现冻结设置参数必须为大于0的正整数");
		exceptionMap.put(ExceptionConstant.UNWINDORDER_CANNOTCANCEL_EXCEPTION, "平仓订单不能取消委托操作");
		exceptionMap.put(ExceptionConstant.ISNOT_EXIST_PUTFORWARDTIME_EXCEPTION, "不在可提现时间内");

	}
}
