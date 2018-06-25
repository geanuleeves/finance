package com.waben.stock.futuresgateway.yisheng.esapi;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.future.api.es.external.common.bean.TapAPIContract;
import com.future.api.es.external.quote.QuoteApi;
import com.future.api.es.external.quote.bean.TapAPIQuotLoginRspInfo;
import com.future.api.es.external.quote.bean.TapAPIQuoteCommodityInfo;
import com.future.api.es.external.quote.bean.TapAPIQuoteContractInfo;
import com.future.api.es.external.quote.bean.TapAPIQuoteLoginAuth;
import com.future.api.es.external.quote.bean.TapAPIQuoteWhole;
import com.future.api.es.external.quote.listener.QuoteApiListener;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqProducer;

/**
 * 易盛行情
 * 
 * @author luomengan
 *
 */
@Component
public class EsQuoteWrapper implements QuoteApiListener {

	Logger logger = LoggerFactory.getLogger(getClass());

	/** 行情IP */
	@Value("${es.quote.ip}")
	private String quoteIp;
	/** 行情端口 */
	@Value("${es.quote.port}")
	private short quotePort;
	/** 行情用户名 */
	@Value("${es.quote.username}")
	private String quoteUsername;
	/** 行情密码 */
	@Value("${es.quote.password}")
	private String quotePassword;
	/** 行情Token */
	@Value("${es.quote.authcode}")
	private String quoteAuthCode;

	/**
	 * 行情api
	 */
	private QuoteApi api;

	@Autowired
	private RabbitmqProducer rabbitmqProducer;

	/**
	 * 初始化
	 */
	@PostConstruct
	public void init() {
		api = new QuoteApi(quoteAuthCode, "", true);
		api.setHostAddress(quoteIp, quotePort);
		api.setApiListener(this);
		connect();
	}

	/**
	 * 连接
	 */
	public void connect() {
		api.login(new TapAPIQuoteLoginAuth(quoteUsername, 'N', quotePassword, null, null, 'N', null));
	}

	/**
	 * 重新连接
	 */
	public void reconnect() {
		try {
			api.disconnect();
			api.destory();
		} finally {
			logger.info("重新开始连接易盛行情API。。。 ");
			init();
		}
	}

	/************************** 以下方法为setter和getter ***********************/

	public QuoteApi getApi() {
		return api;
	}

	/************************** 以下方法为回调方法 ***********************/

	@Override
	public void onAPIReady() {
		logger.info("易盛行情API连接成功 ");
		// api连接成功后，查询所有品种
		api.qryCommodity();
	}

	@Override
	public void onDisconnected(int reasonCode) {
		logger.info("易盛行情API断开连接 " + reasonCode);
		this.connect();
	}

	@Override
	public void onRspLogin(int errorCode, TapAPIQuotLoginRspInfo info) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRspQryCommodity(int sessionID, int errorCode, boolean isLast, TapAPIQuoteCommodityInfo info) {
		rabbitmqProducer.sendMessage(RabbitmqConfiguration.commodityQueueName, info);
	}

	@Override
	public void onRspQryContract(int sessionID, int errorCode, boolean isLast, TapAPIQuoteContractInfo info) {
		rabbitmqProducer.sendMessage(RabbitmqConfiguration.contractQueueName, info);
	}

	@Override
	public void onRspSubscribeQuote(int sessionID, int errorCode, boolean isLast, TapAPIQuoteWhole quoteWhole) {
		logger.info("品种{}，合约{}，行情订阅成功!", quoteWhole.getContract().getCommodity().getCommodityNo(),
				quoteWhole.getContract().getContractNo1());
	}

	@Override
	public void onRspUnSubscribeQuote(int sessionID, int errorCode, boolean isLast, TapAPIContract info) {
		logger.info("品种{}，合约{}，取消行情订阅成功!", info.getCommodity().getCommodityNo(), info.getContractNo1());
	}

	@Override
	public void onRtnQuote(TapAPIQuoteWhole info) {
		// 放入队列
		rabbitmqProducer.sendMessage(RabbitmqConfiguration.quoteQueueName, info);
//		String commodityNo = info.getContract().getCommodity().getCommodityNo();
//		if(EsEngine.commodityScaleMap.containsKey(commodityNo)) {
//			Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
//			// 构建行情推送对象
//			FuturesQuoteDataBase data = FuturesQuoteDataBase.newBuilder()
//			.setTime(info.getDateTimeStamp().substring(0, info.getDateTimeStamp().length() - 4))
//			.setAskPrice(new BigDecimal(info.getQAskPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setAskSize(info.getQAskQty()[0])
//			.setBidPrice(new BigDecimal(info.getQBidPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setBidSize(info.getQBidQty()[0])
//			.setClosePrice(new BigDecimal(info.getQClosingPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setHighPrice(new BigDecimal(info.getQHighPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setLastPrice(new BigDecimal(info.getQLastPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setLastSize(info.getQLastQty())
//			.setLowPrice(new BigDecimal(info.getQLowPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setOpenPrice(new BigDecimal(info.getQOpeningPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
//			.setVolume(info.getQLastQty())
//			.setTotalVolume(info.getQTotalQty()).build();
//		}
	}

}
