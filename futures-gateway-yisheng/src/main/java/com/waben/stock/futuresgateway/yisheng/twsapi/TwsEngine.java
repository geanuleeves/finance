package com.waben.stock.futuresgateway.yisheng.twsapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.waben.stock.futuresgateway.yisheng.cache.RedisCache;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.service.FuturesOrderService;

// @Service
public class TwsEngine {

	@Value("${tws.account}")
	private String account;

	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private FuturesOrderService orderService;

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private WabenEWrapper wrapper;

	private EClientSocket client;

	// @PostConstruct
	public void init() {
		final TwsEngine _this = this;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				_this.client = wrapper.getClient();
				_this.wrapper.connect();
				List<FuturesContract> contractList = contractService.getByEnable(true);
				// step 1 : 获取行情
				// _this.initMarketData(contractList);
				// step 2 : 获取分时、K线数据
				_this.initLineData(contractList);
			}
		}).start();
	}

	@SuppressWarnings("unused")
	private void initMarketData(List<FuturesContract> contractList) {
		if (contractList != null && contractList.size() > 0) {
			for (FuturesContract futuresContract : contractList) {
				// 获取行情快照
				this.reqMktData(client, futuresContract, true);
				// 获取行情推送
				this.reqMktData(client, futuresContract, false);
			}
		}
	}

	private void initLineData(List<FuturesContract> contractList) {
		if (contractList != null && contractList.size() > 0) {
			for (FuturesContract futuresContract : contractList) {
				Contract contract = new Contract();
				contract.localSymbol(futuresContract.getYtLocalSymbolName());
				contract.secType(futuresContract.getYtSecType());
				contract.currency(futuresContract.getYtCurrency());
				contract.exchange(futuresContract.getYtExchange());
				// 日K
				int dayLineTickerId = Integer
						.parseInt(TwsConstant.DayLine_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, dayLineTickerId, "4 y", "1 day");
				// 分时
				int timeLineTickerId = Integer
						.parseInt(TwsConstant.TimeLine_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, timeLineTickerId, "5 d", "1 min");
				// 1分钟K线
				int min1LineTickerId = Integer
						.parseInt(TwsConstant.Min1Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, min1LineTickerId, "2 d", "1 min");
				// 3分钟K线
				int mins3LineTickerId = Integer
						.parseInt(TwsConstant.Mins3Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, mins3LineTickerId, "2 d", "3 mins");
				// 5分钟K线
				int mins5LineTickerId = Integer
						.parseInt(TwsConstant.Mins5Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, mins5LineTickerId, "2 d", "5 mins");
				// 15分钟K线
				int mins15LineTickerId = Integer
						.parseInt(TwsConstant.Mins15Line_TickerId_Prefix + String.valueOf(futuresContract.getId()));
				historicalDataRequests(client, contract, mins15LineTickerId, "2 d", "15 mins");
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
		contract.localSymbol(futuresContract.getYtLocalSymbolName());
		contract.secType(futuresContract.getYtSecType());
		contract.currency(futuresContract.getYtCurrency());
		contract.exchange(futuresContract.getYtExchange());
		// TODO 因还未订阅数据，先写死合约
//		contract = new Contract();
//		contract.symbol("EUR");
//		contract.secType("CASH");
//		contract.currency("GBP");
//		contract.exchange("IDEALPRO");
		
		contract = new Contract();
		contract.symbol("GC");
		contract.localSymbol("GCQ8");
		contract.secType("FUT");
		contract.currency("USD");
		contract.exchange("NYMEX");
		String prefix = snapshot ? TwsConstant.MarketSnapshot_TickerId_Prefix : TwsConstant.MarketPush_TickerId_Prefix;
		client.reqMktData(Integer.parseInt(prefix + futuresContract.getId()), contract, "", snapshot, null);
	}

	public void historicalDataRequests(EClientSocket client, Contract contract, int tickerId, String timeFrame,
			String timeInterval) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String formatted = form.format(cal.getTime());
		// TODO 因还未订阅数据，先写死合约
		contract = new Contract();
		contract.symbol("GC");
		contract.localSymbol("GCQ8");
		contract.secType("FUT");
		contract.currency("USD");
		contract.exchange("NYMEX");
		client.reqHistoricalData(tickerId, contract, formatted, timeFrame, timeInterval, "MIDPOINT", 1, 1, null);
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
		return contractService;
	}

	public void setFuturesContractService(FuturesContractService futuresContractService) {
		this.contractService = futuresContractService;
	}

	public FuturesOrderService getFuturesOrderService() {
		return orderService;
	}

	public void setFuturesOrderService(FuturesOrderService futuresOrderService) {
		this.orderService = futuresOrderService;
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
