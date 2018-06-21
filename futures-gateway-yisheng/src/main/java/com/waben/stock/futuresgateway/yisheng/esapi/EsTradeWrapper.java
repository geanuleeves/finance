package com.waben.stock.futuresgateway.yisheng.esapi;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.future.api.es.external.common.bean.TapAPICommodity;
import com.future.api.es.external.common.bean.TapAPIExchangeInfo;
import com.future.api.es.external.trade.TradeApi;
import com.future.api.es.external.trade.bean.TapAPIAccountInfo;
import com.future.api.es.external.trade.bean.TapAPICommodityInfo;
import com.future.api.es.external.trade.bean.TapAPIFillInfo;
import com.future.api.es.external.trade.bean.TapAPIFundData;
import com.future.api.es.external.trade.bean.TapAPIOrderActionRsp;
import com.future.api.es.external.trade.bean.TapAPIOrderInfo;
import com.future.api.es.external.trade.bean.TapAPIOrderInfoNotice;
import com.future.api.es.external.trade.bean.TapAPIPositionProfitNotice;
import com.future.api.es.external.trade.bean.TapAPITradeContractInfo;
import com.future.api.es.external.trade.bean.TapAPITradeLoginAuth;
import com.future.api.es.external.trade.bean.TapAPITradeLoginRspInfo;
import com.future.api.es.external.trade.listener.TradeApiAdapter;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqProducer;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.OrderActionMessage;

/**
 * 易盛交易
 * 
 * @author luomengan
 *
 */
@Component
public class EsTradeWrapper extends TradeApiAdapter {

	Logger logger = LoggerFactory.getLogger(getClass());

	/** 交易IP */
	@Value("${es.trade.ip}")
	private String tradeIp;
	/** 交易端口 */
	@Value("${es.trade.port}")
	private short tradePort;
	/** 交易用户名 */
	@Value("${es.trade.username}")
	private String tradeUsername;
	/** 交易密码 */
	@Value("${es.trade.password}")
	private String tradePassword;
	/** 交易Token */
	@Value("${es.trade.authcode}")
	private String tradeAuthCode;

	/**
	 * 交易api
	 */
	private TradeApi api;

	@Autowired
	private RabbitmqProducer rabbitmqProducer;

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		api = new TradeApi(tradeAuthCode, "", true);
		api.setHostAddress(tradeIp, tradePort);
		api.setApiListener(this);
		connect();
	}

	/**
	 * 连接
	 */
	public void connect() {
		api.login(new TapAPITradeLoginAuth(tradeUsername, 'N', tradePassword, null));
	}
	
	/**
	 * 重新连接
	 */
	public void reconnect() {
		try {
			api.disconnect();
			api.destory();
		} finally {
			logger.info("重新开始连接易盛交易API。。。 ");
			init();
		}
	}

	/************************** 以下方法为setter和getter ***********************/

	public TradeApi getApi() {
		return api;
	}

	public String getTradeUsername() {
		return tradeUsername;
	}

	/************************** 以下方法为回调方法 ***********************/

	/**
	 * 连接成功回调通知
	 */
	public void onConnect() {
		logger.info("易盛交易API连接成功 ");
	}

	/**
	 * API和服务失去连接的回调 <br>
	 * 在API使用过程中主动或者被动与服务器服务失去连接后都会触发此回调通知用户与服务器的连接已经断开。
	 * 
	 * @param reasonCode
	 *            断开原因代码。具体原因请参见错误码列表
	 */
	public void onDisconnected(int reasonCode) {
		logger.info("易盛交易API断开连接 " + reasonCode);
		this.connect();
	}

	/**
	 * 通知用户API准备就绪。<br>
	 * 只有用户回调收到此就绪通知时才能进行后续的各种行情数据查询操作。此回调函数是API能否正常工作的标志。<br>
	 * 就绪后才可以进行后续正常操作
	 */
	public void onAPIReady() {
		// api连接成功后，查询所有品种
		api.qryCommodity();
	}

	/**
	 * 订单操作应答 下单、撤单、改单应答。下单都会有次应答回调，如果下单请求结构中没有填写合约或者资金账号，则仅返回错误号。
	 * 撤单、改单错误由应答和OnRtnOrder，成功仅返回OnRtnOrder回调。
	 * sessionID标识请求对应的sessionID，以便确定该笔应答对应的请求。
	 * 
	 * @param sessionID
	 *            请求的会话ID；
	 * @param errorCode
	 *            错误码。0 表示成功。
	 * @param info
	 *            订单应答具体类型，包含订单操作类型和订单信息。订单信息部分情况下可能为空，如果为空，可以通过SessiuonID找到对应请求获取请求类型。
	 */
	public void onRspOrderAction(int sessionID, int errorCode, TapAPIOrderActionRsp info) {
		OrderActionMessage actionMsg = new OrderActionMessage();
		actionMsg.setSessionID(sessionID);
		actionMsg.setErrorCode(errorCode);
		actionMsg.setInfo(info);
		rabbitmqProducer.sendMessage(RabbitmqConfiguration.orderActionQueueName, actionMsg);
	}

	/**
	 * 返回新委托。新下的或者其他地方下的推送过来的。<br>
	 * 如果不关注此项内容，可以设定Login时的NoticeIgnoreFlag以屏蔽。
	 * 服务器接收到客户下的委托内容后就会保存起来等待触发，同时向用户回馈一个
	 * 新委托信息说明服务器正确处理了用户的请求，返回的信息中包含了全部的委托信息， 同时有一个用来标示此委托的委托号。
	 * 
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRtnOrder(TapAPIOrderInfoNotice info) {
		rabbitmqProducer.sendMessage(RabbitmqConfiguration.orderStateQueueName, info);
	}

	/**
	 * 推送来的成交信息<br>
	 * 用户的委托成交后将向用户推送成交信息。 如果不关注此项内容，可以设定Login时的NoticeIgnoreFlag以屏蔽。
	 * 
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRtnFill(TapAPIFillInfo info) {
	}

	/**
	 * 返回资金账户的资金信息
	 * 
	 * @param sessionID
	 *            请求的会话ID；
	 * @param errorCode
	 *            错误码。0 表示成功。
	 * @param isLast
	 *            标示是否是最后一批数据；
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRspQryFund(int sessionID, int errorCode, boolean isLast, TapAPIFundData info) {
	}

	/**
	 * 持仓盈亏通知
	 * 
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 * @note 如果不关注此项内容，可以设定Login时的NoticeIgnoreFlag以屏蔽。
	 */
	public void onRtnPositionProfit(TapAPIPositionProfitNotice info) {
	}

	/**
	 * 返回查询的委托信息
	 * 
	 * @param sessionID
	 *            请求的会话ID；
	 * @param errorCode
	 *            错误码。0 表示成功。
	 * @param isLast
	 *            标示是否是最后一批数据；
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRspQryOrder(int sessionID, int errorCode, boolean isLast, TapAPIOrderInfo infoBytes) {
	}

	/**
	 * 返回用户信息<br>
	 * 此回调接口向用户返回查询的资金账号的详细信息。用户有必要将得到的账号编号保存起来，然后在后续的函数调用中使用。
	 * 
	 * @param sessionID
	 *            请求的会话ID；
	 * @param errorCode
	 *            错误码。0 表示成功。
	 * @param isLast
	 *            标示是否是最后一批数据；
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRspQryAccount(int sessionID, int errorCode, boolean isLast, TapAPIAccountInfo infoBytes) {
	}

	/**
	 * 返回系统中的交易所信息
	 * 
	 * @param sessionID
	 *            请求的会话ID；
	 * @param errorCode
	 *            错误码。0 表示成功。
	 * @param isLast
	 *            标示是否是最后一批数据；
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRspQryExchange(int sessionID, int errorCode, boolean isLast, TapAPIExchangeInfo infoBytes) {
	}

	/**
	 * 返回系统中品种信息<br>
	 * 此回调接口用于向用户返回得到的所有品种信息。
	 * 
	 * @param sessionID
	 *            请求的会话ID，和GetAllCommodities()函数返回对应；
	 * @param errorCode
	 *            错误码。0 表示成功。
	 * @param isLast
	 *            标示是否是最后一批数据；
	 * @param info
	 *            指向返回的信息结构体。当errorCode不为0时，info为空。
	 */
	public void onRspQryCommodity(int sessionID, int errorCode, boolean isLast, TapAPICommodityInfo infoBytes) {
		if (infoBytes.getCommodityType() == 'F'
				&& EsEngine.testCommodityExchangeMap.containsKey(infoBytes.getCommodityNo())) {
			TapAPICommodity commodity = new TapAPICommodity();
			commodity.setCommodityNo(infoBytes.getCommodityNo());
			commodity.setExchangeNo(infoBytes.getExchangeNo());
			commodity.setCommodityType(infoBytes.getCommodityType());
			api.qryContract(commodity);
		}
	}

	public void onRspQryContract(int sessionID, int errorCode, boolean isLast, TapAPITradeContractInfo info) {
		rabbitmqProducer.sendMessage(RabbitmqConfiguration.tradeContractQueueName, info);
	}

	/**
	 * 系统登录过程回调。<br>
	 * 此函数为Login()登录函数的回调，调用Login()成功后建立了链路连接，然后API将向服务器发送登录认证信息，
	 * 登录期间的数据发送情况和登录的回馈信息传递到此回调函数中。 该回调返回成功，说明用户登录成功。但是不代表API准备完毕。
	 * 
	 * @param errorCode
	 *            返回错误码,0表示成功。
	 * @param loginRspInfo
	 *            登陆应答信息，如果errorCode!=0，则loginRspInfo=NULL。
	 */
	public void onRspLogin(int errorCode, TapAPITradeLoginRspInfo info) {
	}

}
