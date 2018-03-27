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
		exceptionMap.put(ExceptionConstant.WITHDRAWALS_EXCEPTION, "提现异常");
		exceptionMap.put(ExceptionConstant.RECHARGE_EXCEPTION, "充值异常");
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
		exceptionMap.put(ExceptionConstant.ST_STOCK_CANNOTBUY_EXCEPTION, "ST、*ST不能购买");
		exceptionMap.put(ExceptionConstant.BLACKLIST_STOCK_EXCEPTION, "不支持的股票，请更换股票");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_2UPLIMIT_CANNOTBY_EXCEPTION, "连续两个涨停的股票不能申购");

		exceptionMap.put(ExceptionConstant.INVESTOR_NOT_FOUND_EXCEPTION, "投资人信息未找到");
		exceptionMap.put(ExceptionConstant.INVESTOR_SECURITIES_LOGIN_EXCEPTION, "投资人券商账户登陆异常");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKACCOUNT_MONEY_NOT_ENOUGH, "投资人券商账户资金账户余额不足");
		exceptionMap.put(ExceptionConstant.INVESTOR_EXCHANGE_TYPE_NOT_SUPPORT_EXCEPTION, "投资人券商账户不支持当前股票交易");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKACCOUNT_NOT_EXIST, "投资人券商账户没有可用的股东账户");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKENTRUST_BUY_ERROR, "投资人券商账户委托下单失败");
		exceptionMap.put(ExceptionConstant.INVESTOR_STOCKENTRUST_FETCH_ERROR, "投资人券商账户委托单查询异常");

		exceptionMap.put(ExceptionConstant.ORGANIZATION_NOTEXIST_EXCEPTION, "机构不存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATIONCATEGORY_NOTEXIST_EXCEPTION, "机构类别不存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATION_USER_NOT_FOUND, "机构用户不存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATION_USER_EXIST, "用户名已存在");
		exceptionMap.put(ExceptionConstant.ORGANIZATIONACCOUNT_OLDPAYMENTPASSWORD_NOTMATCH_EXCEPTION, "原始支付密码不匹配");
		exceptionMap.put(ExceptionConstant.ORGUSER_OLDPASSWORD_NOTMATCH_EXCEPTION, "原始登陆密码不匹配");
		exceptionMap.put(ExceptionConstant.ORGPUBLISHER_EXIST_EXCEPTION, "该发布人已绑定过机构码");
		exceptionMap.put(ExceptionConstant.WITHDRAWALSAPPLY_NOTSUPPORTED_EXCEPTION, "尚未绑卡，不能申请提现");
		exceptionMap.put(ExceptionConstant.ORGNAME_EXIST_EXCEPTION, "机构名称已存在");

		exceptionMap.put(ExceptionConstant.STOCKOPTION_AMOUNTMUSTGT20WAN_EXCEPTION, "名义本金20万起，且必须为10万的整数倍");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_STATE_NOTMATCH_OPERATION_NOTSUPPORT_EXCEPTION, "期权交易状态不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_PUBLISHERID_NOTMATCH_EXCEPTION, "自主行权发布人不匹配，不支持该操作");
		exceptionMap.put(ExceptionConstant.USERRIGHT_NOTMATCH_EXCEPTION, "持仓第二天之后才能申请行权");
		exceptionMap.put(ExceptionConstant.STOCKOPTION_QUOTENOTFOUND_EXCEPTION, "该只股票暂时没有机构报价，请更换一只股票");
		exceptionMap.put(ExceptionConstant.INQUIRY_RESULT_NOT_FOUND, "期权询价结果不存在");
	}
}
