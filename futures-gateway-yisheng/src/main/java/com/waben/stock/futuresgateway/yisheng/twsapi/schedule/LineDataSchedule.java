package com.waben.stock.futuresgateway.yisheng.twsapi.schedule;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ib.client.Contract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.service.FuturesContractService;
import com.waben.stock.futuresgateway.yisheng.twsapi.TwsConstant;
import com.waben.stock.futuresgateway.yisheng.twsapi.TwsEngine;

/**
 * K线图定时作业
 * 
 * @author lma
 *
 */
// @Component
public class LineDataSchedule {

	Logger logger = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private FuturesContractService contractService;

	@Autowired
	private TwsEngine twsEngine;

	// @PostConstruct
	public void initTask() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(60000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// 日K
				Timer dayLineTimer = new Timer();
				dayLineTimer.schedule(new RetriveTask(TwsConstant.DayLine_TickerId_Prefix, 4 * 60 * 60 * 1000, "2 D", "1 day"),
						90 * 1000);
				// 分时
				Timer timeLineTimer = new Timer();
				timeLineTimer.schedule(new RetriveTask(TwsConstant.TimeLine_TickerId_Prefix, 1 * 60 * 1000, "300 S", "1 min"),
						15 * 1000);
				// 1分钟K线
				Timer min1LineTimer = new Timer();
				min1LineTimer.schedule(new RetriveTask(TwsConstant.Min1Line_TickerId_Prefix, 1 * 60 * 1000, "300 S", "1 min"),
						30 * 1000);
				// 3分钟K线
				Timer mins3LineTimer = new Timer();
				mins3LineTimer.schedule(
						new RetriveTask(TwsConstant.Mins3Line_TickerId_Prefix, 3 * 60 * 1000, "900 S", "3 mins"), 45 * 1000);
				// 5分钟K线
				Timer mins5LineTimer = new Timer();
				mins5LineTimer.schedule(
						new RetriveTask(TwsConstant.Mins5Line_TickerId_Prefix, 5 * 60 * 1000, "1500 S", "5 mins"), 60 * 1000);
				// 15分钟K线
				Timer mins15LineTimer = new Timer();
				mins15LineTimer.schedule(
						new RetriveTask(TwsConstant.Mins15Line_TickerId_Prefix, 15 * 60 * 1000, "4500 S", "15 mins"),
						75 * 1000);
			}
		}).start();
	}

	public void next(String prefix, long interval, String timeFrame, String timeSize) {
		Timer timer = new Timer();
		timer.schedule(new RetriveTask(prefix, interval, timeFrame, timeSize), interval);
	}

	@SuppressWarnings("unused")
	private class RetriveTask extends TimerTask {
		/**
		 * tickerId前缀
		 */
		private String prefix;
		/**
		 * 作业间隔
		 */
		private long interval;
		/**
		 * 行情数据持续时间
		 */
		private String timeFrame;
		/**
		 * 行情数据间隔时间
		 */
		private String timeSize;

		public RetriveTask(String prefix, long interval, String timeFrame, String timeSize) {
			super();
			this.prefix = prefix;
			this.interval = interval;
			this.timeFrame = timeFrame;
			this.timeSize = timeSize;
		}

		@Override
		public void run() {
			try {
				List<FuturesContract> contractList = contractService.getByEnable(true);
				if (contractList != null && contractList.size() > 0) {
					for (FuturesContract futuresContract : contractList) {
						Contract contract = new Contract();
						contract.localSymbol(futuresContract.getYtCurrency());
						contract.secType(futuresContract.getYtSecType());
						contract.currency(futuresContract.getYtCurrency());
						contract.exchange(futuresContract.getYtExchange());
						int tickerId = Integer.parseInt(prefix + String.valueOf(futuresContract.getId()));
						twsEngine.historicalDataRequests(twsEngine.getClient(), contract, tickerId, timeFrame,
								timeSize);
					}
				}
			} finally {
				next(prefix, interval, timeFrame, timeSize);
				this.cancel();
			}
		}

		public String getPrefix() {
			return prefix;
		}

		public void setPrefix(String prefix) {
			this.prefix = prefix;
		}

		public long getInterval() {
			return interval;
		}

		public void setInterval(long interval) {
			this.interval = interval;
		}

		public String getTimeFrame() {
			return timeFrame;
		}

		public void setTimeFrame(String timeFrame) {
			this.timeFrame = timeFrame;
		}

		public String getTimeSize() {
			return timeSize;
		}

		public void setTimeSize(String timeSize) {
			this.timeSize = timeSize;
		}

	}

}
