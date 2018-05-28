package com.waben.stock.futuresgateway.twsapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.waben.stock.futuresgateway.entity.FuturesContract;
import com.waben.stock.futuresgateway.service.FuturesContractService;
import com.waben.stock.futuresgateway.service.FuturesOrderService;
import com.waben.stock.futuresgateway.service.RedisCache;

@Service
public class TwsEngine {

	private WabenEWrapper wrapper;
	private EClientSocket client;

	private String account = "DU1066508";

	@Autowired
	private FuturesContractService futuresContractService;

	@Autowired
	private FuturesOrderService futuresOrderService;

	@Autowired
	private RedisCache redisCache;

	@PostConstruct
	public void init() {
		this.wrapper = new WabenEWrapper();
		this.wrapper.setFuturesContractService(futuresContractService);
		this.wrapper.setFuturesOrderService(futuresOrderService);
		this.wrapper.setRedisCache(redisCache);
		this.client = wrapper.getClient();
		client.eConnect("127.0.0.1", 7497, 0);
		final EReader reader = new EReader(client, wrapper.getSignal());
		reader.start();
		new Thread() {
			public void run() {
				while (client.isConnected()) {
					wrapper.getSignal().waitForSignal();
					try {
						reader.processMsgs();
					} catch (Exception e) {
						System.out.println("Exception: " + e.getMessage());
					}
				}
			}
		}.start();

		List<FuturesContract> contractList = futuresContractService.list();
		// step 1 : 获取行情
		initMarketData(contractList);
		// step 2 : 获取分时、K线数据
		initLineData(contractList);
	}

	public void initMarketData(List<FuturesContract> contractList) {
		if (contractList != null && contractList.size() > 0) {
			for (FuturesContract futuresContract : contractList) {
				Contract contract = new Contract();
				contract.localSymbol(futuresContract.getLocalSymbolName());
				contract.secType(futuresContract.getSecType());
				contract.currency(futuresContract.getCurrency());
				contract.exchange(futuresContract.getExchange());
				// 获取行情快照
				client.reqMktData(
						Integer.parseInt(TwsConstant.MarketSnapshot_TickerId_Prefix + futuresContract.getId()),
						contract, "", true, null);
				// 获取行情推送
				// TODO 行情数据需要订阅，订阅需要付费
				// client.reqMktData(Integer.parseInt(TwsConstant.MarketPush_TickerId_Prefix
				// + futuresContract.getId()),
				// contract, "", false, null);
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

	private void historicalDataRequests(EClientSocket client, Contract contract, int tickerId, String timeFrame,
			String timeInterval) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String formatted = form.format(cal.getTime());
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

}