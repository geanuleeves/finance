package com.waben.stock.futuresgateway.yisheng.esapi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.future.api.es.external.common.bean.TapAPIContract;
import com.future.api.es.external.quote.QuoteApi;
import com.future.api.es.external.quote.bean.TapAPIQuotLoginRspInfo;
import com.future.api.es.external.quote.bean.TapAPIQuoteCommodityInfo;
import com.future.api.es.external.quote.bean.TapAPIQuoteContractInfo;
import com.future.api.es.external.quote.bean.TapAPIQuoteLoginAuth;
import com.future.api.es.external.quote.bean.TapAPIQuoteWhole;
import com.future.api.es.external.quote.listener.QuoteApiListener;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Command;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.FuturesQuoteData;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.FuturesQuoteData.FuturesQuoteDataBase;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.FuturesQuoteFullData.FuturesQuoteFullDataBase;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.FuturesQuoteSimpleData.FuturesQuoteSimpleDataBase;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Message;
import com.waben.stock.futuresgateway.yisheng.common.protobuf.Message.MessageBase;
import com.waben.stock.futuresgateway.yisheng.esapi.schedule.QuoteDayKSchedule;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqConfiguration;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.RabbitmqProducer;
import com.waben.stock.futuresgateway.yisheng.rabbitmq.message.EsQuoteInfo;
import com.waben.stock.futuresgateway.yisheng.server.ChannelRepository;

import io.netty.channel.Channel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

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
	/** 行情缓存 */
	private Map<String, TapAPIQuoteWhole> quoteCache = new ConcurrentHashMap<>();

	/**
	 * 行情api
	 */
	private QuoteApi api;

	@Autowired
	private RabbitmqProducer rabbitmqProducer;

	@Autowired
	private QuoteDayKSchedule dayKSchedule;

	private final AttributeKey<String> clientInfo = AttributeKey.valueOf("clientInfo");
	private final AttributeKey<Long> requestTypeInfo = AttributeKey.valueOf("requestTypeInfo");
	private final AttributeKey<String> hyInfo = AttributeKey.valueOf("hyInfo");
	private final AttributeKey<String> pzInfo = AttributeKey.valueOf("pzInfo");

	@Autowired
	@Qualifier("channelRepository")
	private ChannelRepository channelRepository;

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
		if(info.getQLastPrice() > 0) {
			// 放入队列和缓存
			EsQuoteInfo quoteInfo = new EsQuoteInfo();
			quoteInfo.setInfo(info);
			quoteInfo.setQuoteIndex(dayKSchedule.getQuoteIndex());
			dayKSchedule.increaseQuoteIndex();
			rabbitmqProducer.sendMessage(RabbitmqConfiguration.quoteQueueName, quoteInfo);
			String commodityNo = info.getContract().getCommodity().getCommodityNo();
			String contractNo = info.getContract().getContractNo1();
			String quoteCacheKey = getQuoteCacheKey(commodityNo, contractNo);
			quoteCache.put(quoteCacheKey, info);
			// 推送行情
			pushQuote(info);
		}
	}

	public Map<String, TapAPIQuoteWhole> getQuoteCache() {
		return quoteCache;
	}

	public String getQuoteCacheKey(String commodityNo, String contractNo) {
		return commodityNo + "-" + contractNo;
	}

	private Message.MessageBase buildSingleQuote(TapAPIQuoteWhole info) {
		String commodityNo = info.getContract().getCommodity().getCommodityNo();
		String contractNo = info.getContract().getContractNo1();
		if (EsEngine.commodityScaleMap.containsKey(commodityNo)) {
			Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
			// 构建行情推送对象
			FuturesQuoteDataBase data = FuturesQuoteDataBase.newBuilder().setCommodityNo(commodityNo)
					.setContractNo(contractNo)
					.setTime(info.getDateTimeStamp().substring(0, info.getDateTimeStamp().length() - 4))
					.setAskPrice(
							new BigDecimal(info.getQAskPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize(info.getQAskQty()[0])
					.setBidPrice(
							new BigDecimal(info.getQBidPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize(info.getQBidQty()[0])
					.setAskPrice2(
							new BigDecimal(info.getQAskPrice()[1]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize2(info.getQAskQty()[1])
					.setBidPrice2(
							new BigDecimal(info.getQBidPrice()[1]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize2(info.getQBidQty()[1])
					.setAskPrice3(
							new BigDecimal(info.getQAskPrice()[2]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize3(info.getQAskQty()[2])
					.setBidPrice3(
							new BigDecimal(info.getQBidPrice()[2]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize3(info.getQBidQty()[2])
					.setAskPrice4(
							new BigDecimal(info.getQAskPrice()[3]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize4(info.getQAskQty()[3])
					.setBidPrice4(
							new BigDecimal(info.getQBidPrice()[3]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize4(info.getQBidQty()[3])
					.setAskPrice5(
							new BigDecimal(info.getQAskPrice()[4]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize5(info.getQAskQty()[4])
					.setBidPrice5(
							new BigDecimal(info.getQBidPrice()[4]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize5(info.getQBidQty()[4])
					.setClosePrice(
							new BigDecimal(info.getQPreClosingPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setHighPrice(new BigDecimal(info.getQHighPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setLastPrice(new BigDecimal(info.getQLastPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setLastSize(info.getQLastQty())
					.setLowPrice(new BigDecimal(info.getQLowPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setOpenPrice(
							new BigDecimal(info.getQOpeningPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setVolume(info.getQLastQty()).setTotalVolume(info.getQTotalQty()).build();
			Message.MessageBase msg = Message.MessageBase.newBuilder().setCmd(Command.CommandType.PUSH_DATA)
					.setClientId("0").setType(1).setRequestType(1).setFq(data).build();
			return msg;
		} else {
			return null;
		}
	}

	private Message.MessageBase buildAllQuote(TapAPIQuoteWhole quote) {
		Message.MessageBase msg = Message.MessageBase.newBuilder().setCmd(Command.CommandType.PUSH_DATA)
				.setClientId("0").setType(1).setRequestType(2).build();
		String commodityNo = quote.getContract().getCommodity().getCommodityNo();
		String contractNo = quote.getContract().getContractNo1();
		if (EsEngine.commodityScaleMap.containsKey(commodityNo)) {
			Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
			FuturesQuoteSimpleDataBase simple = FuturesQuoteSimpleDataBase.newBuilder()
					.setTime(quote.getDateTimeStamp()).setCommodityNo(commodityNo).setContractNo(contractNo)
					.setAskPrice(
							new BigDecimal(quote.getQAskPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize(quote.getQAskQty()[0])
					.setBidPrice(
							new BigDecimal(quote.getQBidPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize(quote.getQBidQty()[0])
					.setLastPrice(
							new BigDecimal(quote.getQLastPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setOpenPrice(
							new BigDecimal(quote.getQOpeningPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setHighPrice(
							new BigDecimal(quote.getQHighPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setLowPrice(new BigDecimal(quote.getQLowPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setClosePrice(new BigDecimal(quote.getQPreClosingPrice()).setScale(scale, RoundingMode.HALF_UP)
							.toString())
					.build();
			msg = Message.MessageBase.newBuilder(msg).addFqList(simple).build();
		}
		return msg;
	}

	private void pushQuote(TapAPIQuoteWhole info) {
		String commodityNo = info.getContract().getCommodity().getCommodityNo();
		String contractNo = info.getContract().getContractNo1();
		Message.MessageBase singleQuote = buildSingleQuote(info);
		Message.MessageBase allQuote = buildAllQuote(info);
		Message.MessageBase allFullQuote = buildAllFullQuote(info);
		// 将行情推送到特定的通道中
		for (Map.Entry<String, Channel> entry : ChannelRepository.channelCache.entrySet()) {
			String clientId = entry.getKey();
			Channel channel = entry.getValue();
			// 请求类型
			Attribute<Long> rtinfo = channel.attr(requestTypeInfo);
			if (singleQuote != null && rtinfo.get() != null && rtinfo.get() == 1) {
				// 推送单个行情
				Attribute<String> hyinfo = channel.attr(hyInfo);
				Attribute<String> pzinfo = channel.attr(pzInfo);
				if (channel.isOpen() && hyinfo != null && pzinfo != null && commodityNo.equals(pzinfo.get())
						&& contractNo.equals(hyinfo.get())) {
					singleQuote = Message.MessageBase.newBuilder(singleQuote).setClientId(clientId).build();
					channel.writeAndFlush(singleQuote);
				}
			} else if (rtinfo.get() != null && rtinfo.get() == 2) {
				// 推送全部行情（部分信息）
				allQuote = Message.MessageBase.newBuilder(allQuote).setClientId(clientId).build();
				channel.writeAndFlush(allQuote);
			} else if(rtinfo.get() != null && rtinfo.get() == 3) {
				// 推送全部行情（全信息）
				allFullQuote = Message.MessageBase.newBuilder(allFullQuote).setClientId(clientId).build();
				channel.writeAndFlush(allFullQuote);
			}
		}
	}

	private MessageBase buildAllFullQuote(TapAPIQuoteWhole info) {
		String commodityNo = info.getContract().getCommodity().getCommodityNo();
		String contractNo = info.getContract().getContractNo1();
		if (EsEngine.commodityScaleMap.containsKey(commodityNo)) {
			Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
			// 构建行情推送对象
			FuturesQuoteFullDataBase data = FuturesQuoteFullDataBase.newBuilder().setCommodityNo(commodityNo)
					.setContractNo(contractNo)
					.setTime(info.getDateTimeStamp().substring(0, info.getDateTimeStamp().length() - 4))
					.setAskPrice(
							new BigDecimal(info.getQAskPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize(info.getQAskQty()[0])
					.setBidPrice(
							new BigDecimal(info.getQBidPrice()[0]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize(info.getQBidQty()[0])
					.setAskPrice2(
							new BigDecimal(info.getQAskPrice()[1]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize2(info.getQAskQty()[1])
					.setBidPrice2(
							new BigDecimal(info.getQBidPrice()[1]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize2(info.getQBidQty()[1])
					.setAskPrice3(
							new BigDecimal(info.getQAskPrice()[2]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize3(info.getQAskQty()[2])
					.setBidPrice3(
							new BigDecimal(info.getQBidPrice()[2]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize3(info.getQBidQty()[2])
					.setAskPrice4(
							new BigDecimal(info.getQAskPrice()[3]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize4(info.getQAskQty()[3])
					.setBidPrice4(
							new BigDecimal(info.getQBidPrice()[3]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize4(info.getQBidQty()[3])
					.setAskPrice5(
							new BigDecimal(info.getQAskPrice()[4]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize5(info.getQAskQty()[4])
					.setBidPrice5(
							new BigDecimal(info.getQBidPrice()[4]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize5(info.getQBidQty()[4])
					.setAskPrice6(
							new BigDecimal(info.getQAskPrice()[5]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize6(info.getQAskQty()[5])
					.setBidPrice6(
							new BigDecimal(info.getQBidPrice()[5]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize6(info.getQBidQty()[5])
					.setAskPrice7(
							new BigDecimal(info.getQAskPrice()[6]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize7(info.getQAskQty()[6])
					.setBidPrice7(
							new BigDecimal(info.getQBidPrice()[6]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize7(info.getQBidQty()[6])
					.setAskPrice8(
							new BigDecimal(info.getQAskPrice()[7]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize8(info.getQAskQty()[7])
					.setBidPrice8(
							new BigDecimal(info.getQBidPrice()[7]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize8(info.getQBidQty()[7])
					.setAskPrice9(
							new BigDecimal(info.getQAskPrice()[8]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize9(info.getQAskQty()[8])
					.setBidPrice9(
							new BigDecimal(info.getQBidPrice()[8]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize9(info.getQBidQty()[8])
					.setAskPrice10(
							new BigDecimal(info.getQAskPrice()[9]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setAskSize10(info.getQAskQty()[9])
					.setBidPrice10(
							new BigDecimal(info.getQBidPrice()[9]).setScale(scale, RoundingMode.HALF_UP).toString())
					.setBidSize10(info.getQBidQty()[9])
					.setClosePrice(
							new BigDecimal(info.getQPreClosingPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setNowClosePrice(new BigDecimal(info.getQClosingPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setHighPrice(new BigDecimal(info.getQHighPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setLastPrice(new BigDecimal(info.getQLastPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setLastSize(info.getQLastQty())
					.setLowPrice(new BigDecimal(info.getQLowPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setOpenPrice(
							new BigDecimal(info.getQOpeningPrice()).setScale(scale, RoundingMode.HALF_UP).toString())
					.setVolume(info.getQLastQty()).setTotalVolume(info.getQTotalQty()).build();
			Message.MessageBase msg = Message.MessageBase.newBuilder().setCmd(Command.CommandType.PUSH_DATA)
					.setClientId("0").setType(1).setRequestType(3).setFullFq(data).build();
			return msg;
		} else {
			return null;
		}
	}

	// @Scheduled(cron = "0/10 * * * * ?")
	@SuppressWarnings("unused")
	public void test() {
		// 构建行情推送对象

		FuturesQuoteData.FuturesQuoteDataBase data = FuturesQuoteData.FuturesQuoteDataBase.newBuilder()
				.setCommodityNo("t").setContractNo("t").setTime("2018-6-23 16:23:00")
				.setAskPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString()).setAskSize(1)
				.setBidPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString()).setBidSize(1)
				.setClosePrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setHighPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setLastPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString()).setLastSize(1)
				.setLowPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString())
				.setOpenPrice(new BigDecimal(1).setScale(2, RoundingMode.HALF_UP).toString()).setVolume(1)
				.setTotalVolume(1).build();

		Message.MessageBase msg = Message.MessageBase.newBuilder().setCmd(Command.CommandType.PUSH_DATA)
				.setClientId("132").setType(1).setFq(data).build();

		for (Map.Entry<String, Channel> entry : ChannelRepository.channelCache.entrySet()) {
			String clientid = entry.getKey();
			final Channel c = entry.getValue();
			final Attribute<String> clientinfo = c.attr(clientInfo);
			Attribute<String> hyinfo = c.attr(hyInfo);
			Attribute<String> pzinfo = c.attr(pzInfo);
			if (hyinfo != null && pzinfo != null) {
				String hyno = hyinfo.get();
				String pzno = pzinfo.get();
				if (!StringUtils.isEmpty(hyno) && !StringUtils.isEmpty(pzno) && hyno.equals("1808") && pzno.equals("GC")
						&& c.isOpen()) {
					logger.info("向客户端推送：clientid=" + clientid);
					c.writeAndFlush(msg);
				}
			}

		}
	}

}
