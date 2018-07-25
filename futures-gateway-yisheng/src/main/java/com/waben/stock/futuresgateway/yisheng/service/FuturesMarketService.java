package com.waben.stock.futuresgateway.yisheng.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.future.api.es.external.common.bean.TapAPICommodity;
import com.future.api.es.external.common.bean.TapAPIContract;
import com.future.api.es.external.quote.bean.TapAPIQuoteWhole;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesCommodityDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDayKDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteLastDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKGroupDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.esapi.EsEngine;
import com.waben.stock.futuresgateway.yisheng.esapi.EsQuoteWrapper;
import com.waben.stock.futuresgateway.yisheng.pojo.FuturesContractLineData;
import com.waben.stock.futuresgateway.yisheng.pojo.FuturesQuoteData;
import com.waben.stock.futuresgateway.yisheng.util.CopyBeanUtils;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;
import com.waben.stock.futuresgateway.yisheng.util.TimeZoneUtil;

/**
 * 期货行情 Service
 * 
 * @author luomengan
 *
 */
@Service
public class FuturesMarketService {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesContractDao contractDao;

	@Autowired
	private FuturesQuoteDao quoteDao;

	@Autowired
	private FuturesQuoteLastDao quoteLastDao;

	@Autowired
	private FuturesQuoteMinuteKDao minuteKDao;

	@Autowired
	private FuturesQuoteMinuteKGroupDao minuteKGroupDao;

	@Autowired
	private FuturesQuoteDayKDao dayKDao;

	@Autowired
	private FuturesQuoteDayKService dayKServcie;

	@Autowired
	private FuturesQuoteMinuteKGroupService minuteKGroupServcie;

	@Autowired
	private EsQuoteWrapper quoteWrapper;

	@PostConstruct
	public void initQuoteCache() {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, TapAPIQuoteWhole> quoteCache = quoteWrapper.getQuoteCache();
		List<FuturesContract> contractList = contractDao.retriveByEnable(true);
		for (FuturesContract contract : contractList) {
			FuturesQuoteData quote = quote(contract.getCommodityNo(), contract.getContractNo());
			if (quote.getLastPrice().compareTo(BigDecimal.ZERO) > 0) {
				TapAPIQuoteWhole quoteWhole = new TapAPIQuoteWhole();
				quoteWhole.setQAskPrice(new double[] { quote.getAskPrice().doubleValue() });
				quoteWhole.setQAskQty(new long[] { quote.getAskSize().longValue() });
				quoteWhole.setQBidPrice(new double[] { quote.getBidPrice().doubleValue() });
				quoteWhole.setQBidQty(new long[] { quote.getBidSize().longValue() });
				if (quote.getTime() != null) {
					quoteWhole.setDateTimeStamp(fullSdf.format(quote.getTime()) + ".000");
				}
				quoteWhole.setQLastPrice(quote.getLastPrice().doubleValue());
				quoteWhole.setQLastQty(quote.getLastSize().longValue());
				if (quote.getNowClosePrice() != null) {
					quoteWhole.setQClosingPrice(quote.getNowClosePrice().doubleValue());
				}
				quoteWhole.setQPreClosingPrice(quote.getClosePrice().doubleValue());
				quoteWhole.setQHighPrice(quote.getHighPrice().doubleValue());
				quoteWhole.setQLowPrice(quote.getLowPrice().doubleValue());
				quoteWhole.setQOpeningPrice(quote.getOpenPrice().doubleValue());
				quoteWhole.setQTotalQty(quote.getTotalVolume());
				TapAPIContract apiContract = new TapAPIContract();
				apiContract.setContractNo1(quote.getContractNo());
				TapAPICommodity apiCommodity = new TapAPICommodity();
				apiCommodity.setCommodityNo(quote.getCommodityNo());
				apiContract.setCommodity(apiCommodity);
				quoteWhole.setContract(apiContract);

				quoteCache.put(quoteWrapper.getQuoteCacheKey(quote.getCommodityNo(), quote.getContractNo()),
						quoteWhole);
			}
		}
	}

	public FuturesQuoteData quote(String commodityNo, String contractNo) {
		FuturesQuoteData result = new FuturesQuoteData();
		result.setCommodityNo(commodityNo);
		result.setContractNo(contractNo);
		// 查询品种
		FuturesCommodity commodity = commodityDao.retrieveByCommodityNo(commodityNo);
		if (commodity != null && commodity.getCommodityTickSize() != null) {
			Integer scale = getScale(commodity.getCommodityTickSize());
			// 查询行情
			FuturesQuote quote = quoteDao.retriveNewest(commodityNo, contractNo);
			if (quote != null) {
				List<BigDecimal> askPriceList = JacksonUtil.decode(quote.getAskPrice(),
						JacksonUtil.getGenericType(List.class, BigDecimal.class));
				List<Long> askSizeList = JacksonUtil.decode(quote.getAskQty(),
						JacksonUtil.getGenericType(List.class, Long.class));
				List<BigDecimal> bidPriceList = JacksonUtil.decode(quote.getBidPrice(),
						JacksonUtil.getGenericType(List.class, BigDecimal.class));
				List<Long> bidSizeList = JacksonUtil.decode(quote.getBidQty(),
						JacksonUtil.getGenericType(List.class, Long.class));
				result.setTime(quote.getTime());
				// 卖1~卖10
				result.setAskPrice(askPriceList.get(0).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice2(askPriceList.get(1).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice3(askPriceList.get(2).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice4(askPriceList.get(3).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice5(askPriceList.get(4).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice6(askPriceList.get(5).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice7(askPriceList.get(6).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice8(askPriceList.get(7).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice9(askPriceList.get(8).setScale(scale, RoundingMode.HALF_UP));
				result.setAskPrice10(askPriceList.get(9).setScale(scale, RoundingMode.HALF_UP));
				result.setAskSize(askSizeList.get(0));
				result.setAskSize2(askSizeList.get(1));
				result.setAskSize3(askSizeList.get(2));
				result.setAskSize4(askSizeList.get(3));
				result.setAskSize5(askSizeList.get(4));
				result.setAskSize6(askSizeList.get(5));
				result.setAskSize7(askSizeList.get(6));
				result.setAskSize8(askSizeList.get(7));
				result.setAskSize9(askSizeList.get(8));
				result.setAskSize10(askSizeList.get(9));
				// 买1~买10
				result.setBidPrice(bidPriceList.get(0).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice2(bidPriceList.get(1).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice3(bidPriceList.get(2).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice4(bidPriceList.get(3).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice5(bidPriceList.get(4).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice6(bidPriceList.get(5).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice7(bidPriceList.get(6).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice8(bidPriceList.get(7).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice9(bidPriceList.get(8).setScale(scale, RoundingMode.HALF_UP));
				result.setBidPrice10(bidPriceList.get(9).setScale(scale, RoundingMode.HALF_UP));
				result.setBidSize(bidSizeList.get(0));
				result.setBidSize2(bidSizeList.get(1));
				result.setBidSize3(bidSizeList.get(2));
				result.setBidSize4(bidSizeList.get(3));
				result.setBidSize5(bidSizeList.get(4));
				result.setBidSize6(bidSizeList.get(5));
				result.setBidSize7(bidSizeList.get(6));
				result.setBidSize8(bidSizeList.get(7));
				result.setBidSize9(bidSizeList.get(8));
				result.setBidSize10(bidSizeList.get(9));
				result.setClosePrice(new BigDecimal(quote.getPreClosingPrice()).setScale(scale, RoundingMode.HALF_UP));
				result.setNowClosePrice(new BigDecimal(quote.getClosingPrice()).setScale(scale, RoundingMode.HALF_UP));
				result.setHighPrice(new BigDecimal(quote.getHighPrice()).setScale(scale, RoundingMode.HALF_UP));
				result.setLastPrice(new BigDecimal(quote.getLastPrice()).setScale(scale, RoundingMode.HALF_UP));
				result.setLastSize(quote.getLastQty());
				result.setLowPrice(new BigDecimal(quote.getLowPrice()).setScale(scale, RoundingMode.HALF_UP));
				result.setOpenPrice(new BigDecimal(quote.getOpeningPrice()).setScale(scale, RoundingMode.HALF_UP));
				result.setVolume(quote.getLastQty());
				result.setTotalVolume(quote.getTotalQty());
				return result;
			}
			// 行情中没有查询到，查新行情-最新
			FuturesQuoteLast quoteLast = quoteLastDao.retriveNewest(commodityNo, contractNo);
			if (quoteLast != null) {
				List<BigDecimal> askPriceList = JacksonUtil.decode(quoteLast.getAskPrice(),
						JacksonUtil.getGenericType(List.class, BigDecimal.class));
				List<Long> askSizeList = JacksonUtil.decode(quoteLast.getAskQty(),
						JacksonUtil.getGenericType(List.class, Long.class));
				List<BigDecimal> bidPriceList = JacksonUtil.decode(quoteLast.getBidPrice(),
						JacksonUtil.getGenericType(List.class, BigDecimal.class));
				List<Long> bidSizeList = JacksonUtil.decode(quoteLast.getBidQty(),
						JacksonUtil.getGenericType(List.class, Long.class));
				result.setAskPrice(askPriceList.get(0).setScale(scale, RoundingMode.HALF_UP));
				result.setAskSize(askSizeList.get(0));
				result.setBidPrice(bidPriceList.get(0).setScale(scale, RoundingMode.HALF_UP));
				result.setBidSize(bidSizeList.get(0));
				result.setClosePrice(quoteLast.getClosingPrice());
				result.setHighPrice(quoteLast.getHighPrice());
				result.setLastPrice(quoteLast.getLastPrice());
				result.setLastSize(quoteLast.getLastQty());
				result.setLowPrice(quoteLast.getLowPrice());
				result.setOpenPrice(quoteLast.getOpeningPrice());
				result.setVolume(quoteLast.getLastQty());
				result.setTotalVolume(quoteLast.getTotalQty());
				return result;
			}
		}
		// 未查询到最新行情，全部初始化值为0
		result.setAskPrice(BigDecimal.ZERO);
		result.setAskSize(0L);
		result.setBidSize(0L);
		result.setBidPrice(BigDecimal.ZERO);
		result.setClosePrice(BigDecimal.ZERO);
		result.setHighPrice(BigDecimal.ZERO);
		result.setLastPrice(BigDecimal.ZERO);
		result.setLastSize(0L);
		result.setLowPrice(BigDecimal.ZERO);
		result.setOpenPrice(BigDecimal.ZERO);
		result.setNowClosePrice(BigDecimal.ZERO);
		result.setTotalVolume(0L);
		result.setVolume(0L);
		return result;
	}

	public List<FuturesContractLineData> dayLine(String commodityNo, String contractNo, String startTimeStr,
			String endTimeStr) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// 获取开始和结束时间
		Date startTime = null;
		Date betweenTime = null;
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
		boolean isNeedAddToday = false;
		if (endTime == null) {
			endTime = new Date();
			isNeedAddToday = true;
		}
		if (startTime == null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(endTime);
			cal.add(Calendar.YEAR, -1);
			startTime = cal.getTime();
		}

		FuturesContract contract = contractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (contract == null) {
			return new ArrayList<>();
		} else {
			if (contract.getDayKMainContractEndTime() != null
					&& contract.getDayKMainContractEndTime().getTime() > startTime.getTime()) {
				betweenTime = contract.getDayKMainContractEndTime();
			} else {
				betweenTime = startTime;
			}
		}

		// 获取主力合约的日K数据
		List<FuturesQuoteDayK> mainDayKList = dayKDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, "main",
						startTime, betweenTime);
		if (mainDayKList == null) {
			mainDayKList = new ArrayList<>();
		}
		// 获取日K数据
		List<FuturesQuoteDayK> dayKList = dayKDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
						betweenTime, endTime);
		if (dayKList == null) {
			dayKList = new ArrayList<>();
		}
		mainDayKList.addAll(dayKList);

		if (isNeedAddToday) {
			FuturesQuoteDayK last = null;
			if (mainDayKList != null && mainDayKList.size() > 0) {
				last = mainDayKList.get(mainDayKList.size() - 1);
			}
			TapAPIQuoteWhole quote = quoteWrapper.getQuoteCache()
					.get(quoteWrapper.getQuoteCacheKey(commodityNo, contractNo));
			if (quote != null && quote.getDateTimeStamp().substring(0, quote.getDateTimeStamp().length() - 13)
					.equals(sdf.format(new Date()))) {
				if (last == null || quote.getDateTimeStamp().substring(0, quote.getDateTimeStamp().length() - 13)
						.compareTo(last.getTimeStr().substring(0, last.getTimeStr().length() - 9)) > 0) {
					if (quote.getQClosingPrice() > 0 && quote.getQHighPrice() > 0 && quote.getQLowPrice() > 0
							&& quote.getQOpeningPrice() > 0) {
						Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
						if (scale != null) {
							FuturesQuoteDayK add = new FuturesQuoteDayK();
							add.setClosePrice(
									new BigDecimal(quote.getQLastPrice()).setScale(scale, RoundingMode.HALF_UP));
							add.setCommodityNo(commodityNo);
							add.setContractNo(contractNo);
							add.setEndTotalQty(quote.getQTotalQty());
							add.setHighPrice(
									new BigDecimal(quote.getQHighPrice()).setScale(scale, RoundingMode.HALF_UP));
							add.setLowPrice(new BigDecimal(quote.getQLowPrice()).setScale(scale, RoundingMode.HALF_UP));
							add.setOpenPrice(
									new BigDecimal(quote.getQOpeningPrice()).setScale(scale, RoundingMode.HALF_UP));
							try {
								add.setTime(sdf.parse(
										quote.getDateTimeStamp().substring(0, quote.getDateTimeStamp().length() - 13)));
								add.setTimeStr(
										quote.getDateTimeStamp().substring(0, quote.getDateTimeStamp().length() - 13));
								add.setVolume(quote.getQTotalQty());
								add.setTotalVolume(quote.getQPositionQty());
								mainDayKList.add(add);
							} catch (ParseException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		return CopyBeanUtils.copyListBeanPropertiesToList(mainDayKList, FuturesContractLineData.class);
	}

	public List<FuturesContractLineData> minsLine(String commodityNo, String contractNo, String startTimeStr,
			String endTimeStr, Integer mins) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 获取开始和结束时间
		Date startTime = null;
		Date betweenTime = null;
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
			int weekDay = cal.get(Calendar.DAY_OF_WEEK);
			if (weekDay == 7) {
				// 星期六
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 1);
			} else if (weekDay == 1) {
				// 星期日
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 1);
			} else if (weekDay == 2 && fullSdf.format(endTime)
					.compareTo(sdf.format(endTime) + " " + TimeZoneUtil.getOpenTime(commodityNo)) < 0) {
				// 星期一
				cal.add(Calendar.DAY_OF_MONTH, -2);
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 1);
			}
			Date computeTime = cal.getTime();
			Date[] timeArr = TimeZoneUtil.retriveBeijingTimeInterval(computeTime, commodityNo);
			startTime = timeArr[0];
		}

		// 判断是否需要获取主力合约的历史数据
		FuturesContract contract = contractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (contract == null) {
			return new ArrayList<>();
		} else {
			if (contract.getMinuteKMainContractEndTime() != null
					&& contract.getMinuteKMainContractEndTime().getTime() > startTime.getTime()) {
				betweenTime = contract.getMinuteKMainContractEndTime();
			} else {
				betweenTime = startTime;
			}
		}
		// 获取主力合约的分K数据（main）
		List<FuturesQuoteMinuteKGroup> mainMinuteKGroupList = minuteKGroupDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, "main",
						startTime, betweenTime);
		if (mainMinuteKGroupList == null) {
			mainMinuteKGroupList = new ArrayList<>();
		}
		// 查询分时数据（constractNo）
		List<MongoFuturesQuoteMinuteK> minuteKList = minuteKDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
						startTime, endTime);
		// 查询小时数据（constractNo）
		List<FuturesQuoteMinuteKGroup> minuteKGoupList = minuteKGroupDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
						startTime, endTime);
		// 统计
		List<FuturesContractLineData> result = new ArrayList<>();
		for (FuturesQuoteMinuteKGroup minuteKGoup : mainMinuteKGroupList) {
			List<FuturesContractLineData> dataList = JacksonUtil.decode(minuteKGoup.getGroupData(),
					JacksonUtil.getGenericType(ArrayList.class, FuturesContractLineData.class));
			for (FuturesContractLineData data : dataList) {
				if (data.getOpenPrice() != null && data.getOpenPrice().compareTo(BigDecimal.ZERO) > 0) {
					result.add(data);
				}
			}
		}
		for (MongoFuturesQuoteMinuteK minuteK : minuteKList) {
			if (minuteK.getOpenPrice() != null && minuteK.getOpenPrice().compareTo(BigDecimal.ZERO) > 0) {
				result.add(CopyBeanUtils.copyBeanProperties(FuturesContractLineData.class, minuteK, false));
			}
		}
		for (FuturesQuoteMinuteKGroup minuteKGoup : minuteKGoupList) {
			List<FuturesContractLineData> dataList = JacksonUtil.decode(minuteKGoup.getGroupData(),
					JacksonUtil.getGenericType(ArrayList.class, FuturesContractLineData.class));
			for (FuturesContractLineData data : dataList) {
				if (data.getOpenPrice() != null && data.getOpenPrice().compareTo(BigDecimal.ZERO) > 0) {
					result.add(data);
				}
			}
		}
		// 排序
		Collections.sort(result);
		if (mins > 1) {
			List<FuturesContractLineData> minsResult = new ArrayList<>();
			for (int i = 0; i < result.size(); i++) {
				FuturesContractLineData data = result.get(i);
				Date date = data.getTime();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				int min = cal.get(Calendar.MINUTE);
				if (min % mins == 0) {
					List<FuturesContractLineData> computeList = new ArrayList<>();
					BigDecimal highPrice = data.getHighPrice();
					BigDecimal lowPrice = data.getLowPrice();
					Long volume = 0L;
					for (int j = i; j > i - mins; j--) {
						if (j >= 0 && date.getTime() - result.get(j).getTime().getTime() < mins * 60 * 1000) {
							FuturesContractLineData jData = result.get(j);
							if (jData.getHighPrice().compareTo(highPrice) > 0) {
								highPrice = jData.getHighPrice();
							}
							if (jData.getLowPrice().compareTo(BigDecimal.ZERO) > 0
									&& jData.getLowPrice().compareTo(lowPrice) < 0) {
								lowPrice = jData.getLowPrice();
							}
							volume += jData.getVolume();
							computeList.add(jData);
						}
					}
					FuturesContractLineData compute = new FuturesContractLineData();
					compute.setClosePrice(computeList.get(0).getClosePrice());
					compute.setCommodityNo(data.getCommodityNo());
					compute.setContractNo(data.getContractNo());
					compute.setHighPrice(highPrice);
					compute.setLowPrice(lowPrice);
					compute.setOpenPrice(computeList.get(computeList.size() - 1).getOpenPrice());
					compute.setTime(data.getTime());
					compute.setTimeStr(data.getTimeStr());
					compute.setTotalVolume(data.getTotalVolume());
					compute.setVolume(volume);
					minsResult.add(compute);
				}
			}
			return minsResult;
		} else {
			return result;
		}
	}

	private int getScale(BigDecimal num) {
		StringBuilder numStrBuilder = new StringBuilder(num.toString());
		while (true) {
			char last = numStrBuilder.charAt(numStrBuilder.length() - 1);
			if (last == 48) {
				numStrBuilder.deleteCharAt(numStrBuilder.length() - 1);
			} else {
				break;
			}
		}
		return new BigDecimal(numStrBuilder.toString()).scale();
	}

	public void computeDayline(String commodityNo, String contractNo, Date time) {
		innerComputeDayK(commodityNo, contractNo, time);
	}

	private void innerComputeDayK(String commodityNo, String contractNo, Date date) {
		SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date[] arr = retriveBeijingTimeInterval(date);
		List<FuturesQuoteMinuteKGroup> groupList = minuteKGroupServcie
				.getByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo, arr[0],
						arr[1]);
		if (groupList != null && groupList.size() > 0) {
			FuturesQuoteDayK dayK = dayKServcie.getByCommodityNoAndContractNoAndTime(commodityNo, contractNo, date);
			if (dayK == null) {
				dayK = new FuturesQuoteDayK();
			}
			// 初始化部分数据
			dayK.setCommodityNo(commodityNo);
			dayK.setContractNo(contractNo);
			dayK.setTime(date);
			dayK.setTimeStr(fullSdf.format(date));
			dayK.setTotalVolume(groupList.get(groupList.size() - 1).getTotalVolume());
			dayK.setVolume(groupList.get(groupList.size() - 1).getTotalVolume());
			dayK.setOpenPrice(groupList.get(0).getOpenPrice());
			dayK.setClosePrice(groupList.get(groupList.size() - 1).getClosePrice());
			// 计算最高价、最低价
			BigDecimal highPrice = groupList.get(0).getHighPrice();
			BigDecimal lowPrice = groupList.get(0).getLowPrice();
			for (FuturesQuoteMinuteKGroup group : groupList) {
				if (group.getHighPrice().compareTo(highPrice) > 0) {
					highPrice = group.getHighPrice();
				}
				if (group.getLowPrice().compareTo(lowPrice) < 0) {
					lowPrice = group.getLowPrice();
				}
			}
			dayK.setHighPrice(highPrice);
			dayK.setLowPrice(lowPrice);
			// 保存计算出来的日K数据
			dayKServcie.addFuturesQuoteDayK(dayK);

		}
	}

	private Date[] retriveBeijingTimeInterval(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, 5);
		cal.add(Calendar.MINUTE, 1);
		Date endTime = cal.getTime();

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(date);
		startCal.add(Calendar.HOUR_OF_DAY, -18);
		startCal.add(Calendar.MINUTE, 1);
		Date startTime = startCal.getTime();

		return new Date[] { startTime, endTime };
	}

	public Map<String, FuturesQuoteData> quoteAll() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMdd HH:mm:ss");
		Map<String, FuturesQuoteData> result = new HashMap<>();
		Map<String, TapAPIQuoteWhole> quoteCache = quoteWrapper.getQuoteCache();
		for (Map.Entry<String, TapAPIQuoteWhole> entry : quoteCache.entrySet()) {
			TapAPIQuoteWhole info = entry.getValue();
			String commodityNo = info.getContract().getCommodity().getCommodityNo();
			Integer scale = EsEngine.commodityScaleMap.get(commodityNo);
			if (scale != null) {
				try {
					FuturesQuoteData data = new FuturesQuoteData();
					data.setCommodityNo(commodityNo);
					data.setContractNo(entry.getValue().getContract().getContractNo1());
					if (info.getDateTimeStamp() != null) {
						data.setTime(
								sdf.parse(info.getDateTimeStamp().substring(0, info.getDateTimeStamp().length() - 4)));
					}
					data.setAskPrice(new BigDecimal(info.getQAskPrice()[0]).setScale(scale, RoundingMode.HALF_UP));
					data.setAskSize(info.getQAskQty()[0]);
					data.setBidPrice(new BigDecimal(info.getQBidPrice()[0]).setScale(scale, RoundingMode.HALF_UP));
					data.setBidSize(info.getQBidQty()[0]);
					if (info.getQAskPrice().length > 1) {
						// 卖2~卖10
						data.setAskPrice2(new BigDecimal(info.getQAskPrice()[1]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice3(new BigDecimal(info.getQAskPrice()[2]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice4(new BigDecimal(info.getQAskPrice()[3]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice5(new BigDecimal(info.getQAskPrice()[4]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice6(new BigDecimal(info.getQAskPrice()[5]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice7(new BigDecimal(info.getQAskPrice()[6]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice8(new BigDecimal(info.getQAskPrice()[7]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice9(new BigDecimal(info.getQAskPrice()[8]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskPrice10(
								new BigDecimal(info.getQAskPrice()[9]).setScale(scale, RoundingMode.HALF_UP));
						data.setAskSize2(info.getQAskQty()[1]);
						data.setAskSize3(info.getQAskQty()[2]);
						data.setAskSize4(info.getQAskQty()[3]);
						data.setAskSize5(info.getQAskQty()[4]);
						data.setAskSize6(info.getQAskQty()[5]);
						data.setAskSize7(info.getQAskQty()[6]);
						data.setAskSize8(info.getQAskQty()[7]);
						data.setAskSize9(info.getQAskQty()[8]);
						data.setAskSize10(info.getQAskQty()[9]);
						// 买2~买10
						data.setBidPrice2(new BigDecimal(info.getQBidPrice()[1]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice3(new BigDecimal(info.getQBidPrice()[2]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice4(new BigDecimal(info.getQBidPrice()[3]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice5(new BigDecimal(info.getQBidPrice()[4]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice6(new BigDecimal(info.getQBidPrice()[5]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice7(new BigDecimal(info.getQBidPrice()[6]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice8(new BigDecimal(info.getQBidPrice()[7]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice9(new BigDecimal(info.getQBidPrice()[8]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidPrice10(
								new BigDecimal(info.getQBidPrice()[9]).setScale(scale, RoundingMode.HALF_UP));
						data.setBidSize2(info.getQBidQty()[1]);
						data.setBidSize3(info.getQBidQty()[2]);
						data.setBidSize4(info.getQBidQty()[3]);
						data.setBidSize5(info.getQBidQty()[4]);
						data.setBidSize6(info.getQBidQty()[5]);
						data.setBidSize7(info.getQBidQty()[6]);
						data.setBidSize8(info.getQBidQty()[7]);
						data.setBidSize9(info.getQBidQty()[8]);
						data.setBidSize10(info.getQBidQty()[9]);
					}
					data.setNowClosePrice(
							new BigDecimal(info.getQClosingPrice()).setScale(scale, RoundingMode.HALF_UP));
					data.setClosePrice(
							new BigDecimal(info.getQPreClosingPrice()).setScale(scale, RoundingMode.HALF_UP));
					data.setHighPrice(new BigDecimal(info.getQHighPrice()).setScale(scale, RoundingMode.HALF_UP));
					data.setLastPrice(new BigDecimal(info.getQLastPrice()).setScale(scale, RoundingMode.HALF_UP));
					data.setLastSize(info.getQLastQty());
					data.setLowPrice(new BigDecimal(info.getQLowPrice()).setScale(scale, RoundingMode.HALF_UP));
					data.setOpenPrice(new BigDecimal(info.getQOpeningPrice()).setScale(scale, RoundingMode.HALF_UP));
					data.setVolume(info.getQLastQty());
					data.setTotalVolume(info.getQTotalQty());
					result.put(entry.getKey(), data);
				} catch (ParseException e) {
					logger.error("行情日期格式有误：{}-{}-{}", commodityNo, entry.getValue().getContract().getContractNo1(),
							info.getDateTimeStamp().substring(0, info.getDateTimeStamp().length() - 4));
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	public void subcribe(String commodityNo, String contractNo) {
		FuturesCommodity commodityEntity = commodityDao.retrieveByCommodityNo(commodityNo);
		FuturesContract contractEntity = contractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (commodityEntity != null && "F".equals(commodityEntity.getCommodityType()) && contractEntity != null) {
			TapAPIContract contract = new TapAPIContract(
					new TapAPICommodity(commodityEntity.getExchangeNo(), 'F', commodityEntity.getCommodityNo()),
					contractEntity.getContractNo(), null, 'N', null, null, 'N');
			quoteWrapper.getApi().subscribeQuote(contract);
		}
	}

	public void unsubcribe(String commodityNo, String contractNo) {
		FuturesCommodity commodityEntity = commodityDao.retrieveByCommodityNo(commodityNo);
		FuturesContract contractEntity = contractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (commodityEntity != null && "F".equals(commodityEntity.getCommodityType()) && contractEntity != null) {
			TapAPIContract contract = new TapAPIContract(
					new TapAPICommodity(commodityEntity.getExchangeNo(), 'F', commodityEntity.getCommodityNo()),
					contractEntity.getContractNo(), null, 'N', null, null, 'N');
			quoteWrapper.getApi().unSubscribeQuote(contract);
		}
	}

}
