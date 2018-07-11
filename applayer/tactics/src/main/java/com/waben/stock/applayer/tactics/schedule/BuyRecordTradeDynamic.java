package com.waben.stock.applayer.tactics.schedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.waben.stock.applayer.tactics.business.HolidayBusiness;
import com.waben.stock.applayer.tactics.business.StockBusiness;
import com.waben.stock.applayer.tactics.dto.buyrecord.TradeDynamicDto;
import com.waben.stock.applayer.tactics.service.RedisCache;
import com.waben.stock.interfaces.commonapi.retrivestock.RetriveStockOverHttp;
import com.waben.stock.interfaces.commonapi.retrivestock.bean.StockMarket;
import com.waben.stock.interfaces.enums.RedisCacheKeyType;
import com.waben.stock.interfaces.util.JacksonUtil;
import com.waben.stock.interfaces.util.RandomUtil;

/**
 * 点买交易动态
 * 
 * @author luomengan
 *
 */
@Component
@EnableScheduling
public class BuyRecordTradeDynamic {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private HolidayBusiness holidayBusiness;

	@Autowired
	private StockBusiness stockBusiness;

	@PostConstruct
	public void init() {
		Map<String, String> tradeDynamicMap = redisCache.hgetAll(RedisCacheKeyType.TradeDynamic.getKey());
		if (tradeDynamicMap.size() == 0) {
			firstTradeDynamic();
			tradeDynamicMap = redisCache.hgetAll(RedisCacheKeyType.TradeDynamic.getKey());
		}
		if (tradeDynamicMap.size() < 20) {
			for (int i = 0; i < 20 - tradeDynamicMap.size(); i++) {
				initTradeDynamic();
			}
		}
	}

	public void firstTradeDynamic() {
		// 生成新的交易动态
		Long publisherId = 0L;
		String phone = randomPhone();
		Integer tradeType = 2;
		String[] randomStock = randomStock();
		String stockCode = randomStock[0];
		String stockName = randomStock[1];
		BigDecimal profit = randomProfit();
		Integer numberOfStrand = randomNumberOfStrand();
		TradeDynamicDto newTradeDynamic = new TradeDynamicDto();
		newTradeDynamic.setPublisherId(publisherId);
		newTradeDynamic.setPhone(phone);
		newTradeDynamic.setTradeType(tradeType);
		newTradeDynamic.setStockCode(stockCode);
		newTradeDynamic.setStockName(stockName);
		newTradeDynamic.setProfit(profit);
		newTradeDynamic.setNumberOfStrand(numberOfStrand);
		redisCache.hset(RedisCacheKeyType.TradeDynamic.getKey(), String.valueOf(System.currentTimeMillis()),
				JacksonUtil.encode(newTradeDynamic));
	}

	public void initTradeDynamic() {
		Map<String, String> tradeDynamicMap = redisCache.hgetAll(RedisCacheKeyType.TradeDynamic.getKey());
		TreeSet<String> sortKeySet = new TreeSet<>(tradeDynamicMap.keySet());
		String lastKey = sortKeySet.last();
		String tradeDynamicJson = tradeDynamicMap.get(lastKey);
		TradeDynamicDto tradeDynamic = JacksonUtil.decode(tradeDynamicJson, TradeDynamicDto.class);
		// 生成新的交易动态
		Long publisherId = 0L;
		String phone = randomPhone();
		Integer tradeType = tradeDynamic.getTradeType() == 1 ? 2 : 1;
		String[] randomStock = randomStock();
		String stockCode = randomStock[0];
		String stockName = randomStock[1];
		BigDecimal profit = randomProfit();
		Integer numberOfStrand = randomNumberOfStrand();
		TradeDynamicDto newTradeDynamic = new TradeDynamicDto();
		newTradeDynamic.setPublisherId(publisherId);
		newTradeDynamic.setPhone(phone);
		newTradeDynamic.setTradeType(tradeType);
		newTradeDynamic.setStockCode(stockCode);
		newTradeDynamic.setStockName(stockName);
		newTradeDynamic.setProfit(profit);
		newTradeDynamic.setNumberOfStrand(numberOfStrand);
		redisCache.hset(RedisCacheKeyType.TradeDynamic.getKey(), String.valueOf(System.currentTimeMillis()),
				JacksonUtil.encode(newTradeDynamic));
	}

	@Scheduled(cron = "0 45 0/1 * * ?")
	public void addTradeDynamic() {
		boolean isTradeTime = holidayBusiness.isTradeTime();
		if (isTradeTime) {
			Map<String, String> tradeDynamicMap = redisCache.hgetAll(RedisCacheKeyType.TradeDynamic.getKey());
			TreeSet<String> sortKeySet = new TreeSet<>(tradeDynamicMap.keySet());
			String lastKey = sortKeySet.last();
			String tradeDynamicJson = tradeDynamicMap.get(lastKey);
			TradeDynamicDto tradeDynamic = JacksonUtil.decode(tradeDynamicJson, TradeDynamicDto.class);
			// 生成新的交易动态
			Long publisherId = 0L;
			String phone = randomPhone();
			Integer tradeType = tradeDynamic.getTradeType() == 1 ? 2 : 1;
			String[] randomStock = randomStock();
			String stockCode = randomStock[0];
			String stockName = randomStock[1];
			BigDecimal profit = randomProfit();
			Integer numberOfStrand = randomNumberOfStrand();
			TradeDynamicDto newTradeDynamic = new TradeDynamicDto();
			newTradeDynamic.setPublisherId(publisherId);
			newTradeDynamic.setPhone(phone);
			newTradeDynamic.setTradeType(tradeType);
			newTradeDynamic.setStockCode(stockCode);
			newTradeDynamic.setStockName(stockName);
			newTradeDynamic.setProfit(profit);
			newTradeDynamic.setNumberOfStrand(numberOfStrand);
			redisCache.hset(RedisCacheKeyType.TradeDynamic.getKey(), String.valueOf(System.currentTimeMillis()),
					JacksonUtil.encode(newTradeDynamic));
		}
	}

	private Integer randomNumberOfStrand() {
		int numberOfStrand = RandomUtil.getRandomInt(110);
		while ((numberOfStrand = RandomUtil.getRandomInt(110)) < 10) {
		}
		return numberOfStrand;
	}

	private BigDecimal randomProfit() {
		int profit = RandomUtil.getRandomInt(10000);
		while ((profit = RandomUtil.getRandomInt(10000)) < 200) {
		}
		return new BigDecimal(profit).multiply(new BigDecimal(9));
	}

	private String[] randomStock() {
		String[] stockArr = "002747,002348,002354,002413,002910,002607,000541,000976,002309,000002,000597,000505,000922,002392,002012,002328,000587,000528,603277,000636,000911,002503,002590,000916,000663,002882,002026,002006,002169,002272,002444,002025,002142,002138,002379,002173,002110,002632,002861,002315,002163,002154,002290,002522,002343,002795,000514,002687,002111,000557,000739,000950,002414,002691,000099,002048,002808,000546,000810,000901,002877,002355,002131,000811,002855,002628,600903,002055,002294,002865,000868,000690,002740,000592,000812,002507,000043,000727,603183,000638,002651,002849,603721,002370,002398,002145,000669,000677,000014,002907,002364,002526,000605,002238,603880,002293,002819,000637,000900,002721,002875,000582,002082,002132,002484,000899,002684,002384,002216,002901,002103,002327,000007,000632,000088,000538,002375,002248,000882,000519,002563,000920,002650,002171,000965,002184,000709,002182,002561,002057,000498,000700,002536,000820,002569,000158,000975,000860,002518,000488,002741,000422,002551,000531,001696,603357,002241,002190,002404,000801,002356,002616,000711,002117,002751,002491,002732,000639,002728,000022,000970,000665,002388,002003,002767,002681,000026,002624,603725,000731,002274,000915,002249,002358,000503,002256,002236,000703,002266,000520,000049,002008,000060,000688,002034,002705,000555,000521,002823,002412,000793,002170,002235,002448,000615,603260,002143,002353,000065,000809,000627,002837,002454,002542,002641,000898,002662,002485,002326,002788,000560,002773,000889,002153,002835,002217,002731,002475,002431,000835,002763,603963,000715,002339,000533,002227,002155,002717,000066,002541,002568,002088,000428,002013,000785,002420,002807,002259,002652,000686,002174,002402,002002,002245,002268,002265,000031,002533,002850,000729,002373,000798,000037,002194,000619,002549,002476,002408,002786,002908,002283,002494,000034,002468,000613,000691,002177,002898,603083,002097,000848,002688,002725,002007,002337,002703,002815,002021,002743,002214,002212,002299,000023,000155,000593,000910,002509,002056,002074,002880,000012,002077,002291,603499,000404,002264,002856,002188,000058,000585,000897,603825,000788,000981,000409,002543,002643,002087,002673,000050,002186,002391,002566,000009,000888,000930,002514,000710,000623,002289,002101,002870,002053,002165,002567,000952,002535,002866,002598,603466,002520,002756,002737,000927,002455,002039,000795,000851,000564,000995,002129,000803,000977,002075,002242,002572,000598,002317,002501,002630,002440,000539,002321,002813,000967,603363,002004,002548,002903,002341,002319,000890,002496,603289,603365,002197,002481,000570,603386,002134,002753,000591,002189,002893,000540,002253,002449,000069,000680,000039,002671,000559,000517,002243,000620,002657,002040,002043,002730,002538,002700,002655,002682,002332,002530,002036,002211,002278,000971,002493,002791,002020,002136,000631,002625,000829,000611,002663,000584,000979,002209,002233,002383,002219,000963,002306,000839,002385,002407,002827,002038,002601,002736,002482,000045,000928,002539,002180,002776,000968,002218,000667,002797,000797,002675,000166,000510,000863,002782,000552,002206,002366,002445,000651,000766,002369,002604,002746,000718,000905,002417,000156,002556,000010,002669,000776,002832,000735,002224,002477,002897,000537,002023,002845,002161,000895,002230,002755,002500,002658,000040,000400,000806,002410,002151,002723,002843,000033,002582,002222,002465,000062,000828,000416,002602,002733,002244,002126,002858,000917,002631,000048,002137,002280,002637"
				.split(",");
		String stockCode = stockArr[RandomUtil.getRandomInt(stockArr.length)];
		while (true) {
			try {
				stockBusiness.checkStock(stockCode);
				break;
			} catch (Exception ex) {
				stockCode = stockArr[RandomUtil.getRandomInt(stockArr.length)];
			}
		}
		List<String> codes = new ArrayList<>();
		codes.add(stockCode);
		StockMarket market = RetriveStockOverHttp.listStockMarket(restTemplate, codes).get(0);
		return new String[] { stockCode, market.getName() };
	}

	private String randomPhone() {
		String[] phoneArr = new String[] { "134", "135", "136", "137", "138", "139", "150", "151", "152", "158", "159",
				"130", "131", "132", "155", "156", "185", "186" };
		return phoneArr[RandomUtil.getRandomInt(phoneArr.length)] + "00000000";
	}

	public static void testMain(String[] args) {
		Set<String> set = new HashSet<>();
		set.add("100450011");
		set.add("100450012");

		TreeSet<String> sortKeySet = new TreeSet<>(set);
		System.out.println(sortKeySet);
		System.out.println(sortKeySet.last());
	}

}
