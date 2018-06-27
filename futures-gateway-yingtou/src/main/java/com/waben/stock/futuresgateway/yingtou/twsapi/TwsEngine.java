package com.waben.stock.futuresgateway.yingtou.twsapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.waben.stock.futuresgateway.yingtou.cache.RedisCache;
import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;
import com.waben.stock.futuresgateway.yingtou.service.FuturesContractService;
import com.waben.stock.futuresgateway.yingtou.service.FuturesOrderService;

@Service
public class TwsEngine {

	@Value("${tws.account}")
	private String account;

	@Autowired
	private FuturesContractService futuresContractService;

	@Autowired
	private FuturesOrderService futuresOrderService;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private WabenEWrapper wrapper;

	private EClientSocket client;

	@PostConstruct
	public void init() {
		this.client = wrapper.getClient();
		this.wrapper.connect();

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		List<FuturesContract> contractList = futuresContractService.list();
		// step 1 : 获取行情
		initMarketData(contractList);
		// step 2 : 获取分时、K线数据
		initLineData(contractList);
	}

	private void initMarketData(List<FuturesContract> contractList) {
		if (contractList != null && contractList.size() > 0) {
			for (FuturesContract futuresContract : contractList) {
				// 获取行情快照
				// this.reqMktData(client, futuresContract, true);
				// 获取行情推送
				// this.reqMktData(client, futuresContract, false);
			}
		}
	}

	private void initLineData(List<FuturesContract> contractList) {
		if (contractList != null && contractList.size() > 0) {
			for (FuturesContract futuresContract : contractList) {
				Contract contract = new Contract();
				contract.localSymbol(futuresContract.getLocalSymbolName());
				contract.secType(futuresContract.getSecType());
				contract.currency(futuresContract.getCurrency());
				contract.exchange(futuresContract.getExchange());
				// 日K
				int dayLineTickerId = Integer
						.parseInt(TwsConstant.DayLine_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				// historicalDataRequests(client, contract, dayLineTickerId, "4 y", "1 day");
				// 分时
				int timeLineTickerId = Integer
						.parseInt(TwsConstant.TimeLine_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				// historicalDataRequests(client, contract, timeLineTickerId, "5 d", "1 min");
				// 1分钟K线
				int min1LineTickerId = Integer
						.parseInt(TwsConstant.Min1Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, min1LineTickerId, "1 d", "1 min");
				// 3分钟K线
				int mins3LineTickerId = Integer
						.parseInt(TwsConstant.Mins3Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				// historicalDataRequests(client, contract, mins3LineTickerId, "2 d", "3 mins");
				// 5分钟K线
				int mins5LineTickerId = Integer
						.parseInt(TwsConstant.Mins5Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				// historicalDataRequests(client, contract, mins5LineTickerId, "2 d", "5 mins");
				// 15分钟K线
				int mins15LineTickerId = Integer
						.parseInt(TwsConstant.Mins15Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				// historicalDataRequests(client, contract, mins15LineTickerId, "2 d", "15 mins");
				try {
					Thread.sleep(5000);
					client.cancelHistoricalData(dayLineTickerId);
					client.cancelHistoricalData(timeLineTickerId);
					client.cancelHistoricalData(min1LineTickerId);
					client.cancelHistoricalData(mins3LineTickerId);
					client.cancelHistoricalData(mins5LineTickerId);
					client.cancelHistoricalData(mins15LineTickerId);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void reqMktData(EClientSocket client, FuturesContract futuresContract, boolean snapshot) {
		Contract contract = new Contract();
		contract.localSymbol(futuresContract.getLocalSymbolName());
		contract.secType(futuresContract.getSecType());
		contract.currency(futuresContract.getCurrency());
		contract.exchange(futuresContract.getExchange());

		// TODO 因还未订阅数据，先写死合约
		contract = new Contract();
		contract.symbol("EUR");
		contract.secType("CASH");
		contract.currency("GBP");
		contract.exchange("IDEALPRO");

		String prefix = snapshot ? TwsConstant.MarketSnapshot_TickerId_Prefix : TwsConstant.MarketPush_TickerId_Prefix;
		// client.reqMktData(Integer.parseInt(prefix + futuresContract.getId()), contract, "", snapshot, null);
	}

	public void historicalDataRequests(EClientSocket client, Contract contract, int tickerId, String timeFrame,
			String timeInterval) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String formatted = form.format(cal.getTime());
		// TODO 因还未订阅数据，先写死合约
		contract = new Contract();
//		contract.symbol("EUR");
//		contract.secType("CASH");
//		contract.currency("GBP");
//		contract.exchange("IDEALPRO");
		
		contract.symbol("GC");
		contract.localSymbol("GCQ8");
		contract.secType("FUT");
		contract.currency("USD");
		contract.exchange("NYMEX");
		
		client.reqHistoricalData(tickerId, contract, formatted, timeFrame, timeInterval, "TRADES", 1, 1, null);
	}

	public String getAccount() {
		return account;
	}

	public WabenEWrapper getWrapper() {
		return wrapper;
	}

	public EClientSocket getClient() {
		return client;
	}

	public FuturesContractService getFuturesContractService() {
		return futuresContractService;
	}

	public void setFuturesContractService(FuturesContractService futuresContractService) {
		this.futuresContractService = futuresContractService;
	}

	public FuturesOrderService getFuturesOrderService() {
		return futuresOrderService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.futuresOrderService = futuresOrderService;
	}

	public RedisCache getRedisCache() {
		return redisCache;
	}

	public void setRedisCache(RedisCache redisCache) {
		this.redisCache = redisCache;
	}

	public void setWrapper(WabenEWrapper wrapper) {
		this.wrapper = wrapper;
	}

	public void setClient(EClientSocket client) {
		this.client = client;
	}

	public void setAccount(String account) {
		this.account = account;
	}

}
