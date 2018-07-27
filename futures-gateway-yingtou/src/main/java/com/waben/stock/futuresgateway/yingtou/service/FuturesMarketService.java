package com.waben.stock.futuresgateway.yingtou.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.futuresgateway.yingtou.cache.RedisCache;
import com.waben.stock.futuresgateway.yingtou.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yingtou.entity.FuturesContract;
import com.waben.stock.futuresgateway.yingtou.pojo.FuturesContractLineData;
import com.waben.stock.futuresgateway.yingtou.twsapi.TwsConstant;
import com.waben.stock.futuresgateway.yingtou.util.JacksonUtil;
import com.waben.stock.futuresgateway.yingtou.util.StringUtil;

/**
 * 期货行情 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesMarketService {

	@Autowired
	private FuturesContractDao contractDao;

	@Autowired
	private RedisCache redisCache;

	private SimpleDateFormat daySdf = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public FuturesContract market(String symbol) {
		return contractDao.retrieveContractBySymbol(symbol);
	}

	public List<FuturesContractLineData> timeLine(String symbol, Integer dayCount) {
		List<FuturesContractLineData> result = new ArrayList<>();
		// 从redis中获取
		FuturesContract contract = contractDao.retrieveContractBySymbol(symbol);
		Map<String, String> map = redisCache
				.hgetAll(String.format(TwsConstant.TimeLine_RedisKey, contract.getId() + "_" + contract.getSymbol()));
		List<String> datas = new ArrayList<>(map.values());
		// 按时间倒序排序
		Collections.sort(datas, new TimeStringDownComparator());
		// 返回特定天数的分时数据
		if (dayCount == null || dayCount <= 0) {
			dayCount = 1;
		}
		Map<String, Boolean> dayMap = new HashMap<>();
		for (String data : datas) {
			FuturesContractLineData lineData = JacksonUtil.decode(data, FuturesContractLineData.class);
			String timeStr = daySdf.format(lineData.getTime());
			if (dayMap.containsKey(timeStr)) {
				result.add(lineData);
			} else {
				if (dayMap.keySet().size() < dayCount) {
					result.add(lineData);
					dayMap.put(timeStr, true);
				}
			}
		}
		return result;
	}

	public List<FuturesContractLineData> dayLine(String symbol, String startTimeStr, String endTimeStr) {
		List<FuturesContractLineData> result = new ArrayList<>();
		// 获取开始和结束时间
		Date startTime = null;
		Date endTime = null;
		try {
			if (!StringUtil.isEmpty(startTimeStr)) {
				startTime = fullSdf.parse(startTimeStr);
			}
			if (!StringUtil.isEmpty(endTimeStr)) {
				endTime = fullSdf.parse(endTimeStr);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (endTime == null) {
			endTime = new Date();
		}
		if (startTime == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTime);
			cal.add(Calendar.YEAR, -1);
			startTime = cal.getTime();
		}
		// 从redis中获取
		FuturesContract contract = contractDao.retrieveContractBySymbol(symbol);
		Map<String, String> map = redisCache
				.hgetAll(String.format(TwsConstant.DayLine_RedisKey, contract.getId() + "_" + contract.getSymbol()));
		List<String> datas = new ArrayList<>(map.values());
		// 按时间倒序排序
		Collections.sort(datas, new TimeStringDownComparator());
		for (String data : datas) {
			FuturesContractLineData lineData = JacksonUtil.decode(data, FuturesContractLineData.class);
			Date time = lineData.getTime();
			if (time.getTime() >= startTime.getTime() && time.getTime() <= endTime.getTime()) {
				result.add(lineData);
			}
		}
		return result;
	}

	public List<FuturesContractLineData> minsLine(String symbol, Integer mins, Integer dayCount) {
		List<FuturesContractLineData> result = new ArrayList<>();
		// 从redis中获取
		String redisKey = null;
		if (mins == null || mins < 1) {
			redisKey = TwsConstant.Min1Line_RedisKey;
		} else if (mins == 1) {
			redisKey = TwsConstant.Min1Line_RedisKey;
		} else if (mins == 3) {
			redisKey = TwsConstant.Mins3Line_RedisKey;
		} else if (mins == 5) {
			redisKey = TwsConstant.Mins5Line_RedisKey;
		} else if (mins == 15) {
			redisKey = TwsConstant.Mins15Line_RedisKey;
		} else {
			return result;
		}
		FuturesContract contract = contractDao.retrieveContractBySymbol(symbol);
		Map<String, String> map = redisCache
				.hgetAll(String.format(redisKey, contract.getId() + "_" + contract.getSymbol()));
		List<String> datas = new ArrayList<>(map.values());
		// 按时间倒序排序
		Collections.sort(datas, new TimeStringDownComparator());
		// 返回特定天数的分时数据
		if (dayCount == null || dayCount <= 0) {
			dayCount = 1;
		}
		Map<String, Boolean> dayMap = new HashMap<>();
		for (String data : datas) {
			FuturesContractLineData lineData = JacksonUtil.decode(data, FuturesContractLineData.class);
			String timeStr = daySdf.format(lineData.getTime());
			if (dayMap.containsKey(timeStr)) {
				result.add(lineData);
			} else {
				if (dayMap.keySet().size() < dayCount) {
					result.add(lineData);
					dayMap.put(timeStr, true);
				}
			}
		}
		return result;
	}

	private class TimeStringDownComparator implements Comparator<String> {
		@Override
		public int compare(String o1, String o2) {
			int time1Index = o1.indexOf("\"time\"");
			String time1 = o1.substring(time1Index + 8, time1Index + 25);
			int time2Index = o2.indexOf("\"time\"");
			String time2 = o2.substring(time2Index + 8, time2Index + 25);
			return time2.compareTo(time1);
		}
	}
	
}
