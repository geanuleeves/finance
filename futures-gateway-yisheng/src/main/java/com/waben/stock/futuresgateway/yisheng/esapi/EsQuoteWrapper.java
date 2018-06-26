package com.waben.stock.futuresgateway.yisheng.esapi;

import com.future.api.es.external.common.bean.TapAPIContract;
import com.future.api.es.external.quote.QuoteApi;
import com.future.api.es.external.quote.bean.*;
import com.future.api.es.external.quote.listener.QuoteApiListener;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Command;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.FuturesQuoteData;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Message;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqProducer;
import com.waben.stock.futuresgateway.yisheng.server.ChannelRepository;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

/**
 * 易盛行情
 * 
 * @author luomengan
 *
 */
@Component
public class EsQuoteWrapper implements QuoteApiListener {

	final Logger logger = LoggerFactory.getLogger(getClass());

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

	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");
	private final AttributeKey<String> hyInfo = AttributeKey.valueOf("hyInfo");
	private final AttributeKey<String> pzInfo = AttributeKey.valueOf("pzInfo");

	@Autowired
	@Qualifier("channelRepository")
	ChannelRepository channelRepository;

	/**
	 * 初始化
	 */
	// @PostConstruct
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
		String commodityNo = info.getContract().getCommodity().getCommodityNo();
		if(EsEngine.commodityScaleMap.containsKey(commodityNo)) {
			Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
			// 构建行情推送对象
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

		}
	}

	@Scheduled(cron = "0/10 * * * * ?")
	public void test()  {
		// 构建行情推送对象

		FuturesQuoteData.FuturesQuoteDataBase data = FuturesQuoteData.FuturesQuoteDataBase.newBuilder()
				.setCommodityNo("t").setContractNo("t")
				.setTime("2018-6-23 16:23:00")
				.setAskPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setAskSize(1)
				.setBidPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setBidSize(1)
				.setClosePrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setHighPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setLastPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setLastSize(1)
				.setLowPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setOpenPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setVolume(1)
				.setTotalVolume(1).build();

		Message.MessageBase msg = Message.MessageBase.newBuilder()
				.setCmd(Command.CommandType.PUSH_DATA).setClientId("132")
				.setType(1).setFq(data).build();

		for(Map.Entry<String,Channel> entry : ChannelRepository.channelCache.entrySet()){
			String clientid = entry.getKey();
			final Channel c = entry.getValue();
			final Attribute<String> clientinfo = c.attr(clientInfo);
			Attribute<String> hyinfo = c.attr(hyInfo);
			Attribute<String> pzinfo = c.attr(pzInfo);
			if(hyinfo != null && pzinfo != null){
				String hyno = hyinfo.get();
				String pzno = pzinfo.get();
				if(!StringUtils.isEmpty(hyno) && !StringUtils.isEmpty(pzno) &&
						hyno.equals("1808") && pzno.equals("GC") && c.isOpen()){
					logger.info("向客户端推送：clientid=" + clientid);
					c.writeAndFlush(
							msg
					);
				}
			}

		}
	}



}
