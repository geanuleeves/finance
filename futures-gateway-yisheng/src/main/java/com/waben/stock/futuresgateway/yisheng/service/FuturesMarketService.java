package com.waben.stock.futuresgateway.yisheng.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesCommodityDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDayKDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteLastDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKGroupDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;
import com.waben.stock.futuresgateway.yisheng.pojo.FuturesContractLineData;
import com.waben.stock.futuresgateway.yisheng.pojo.FuturesQuoteData;
import com.waben.stock.futuresgateway.yisheng.util.CopyBeanUtils;
import com.waben.stock.futuresgateway.yisheng.util.JacksonUtil;
import com.waben.stock.futuresgateway.yisheng.util.StringUtil;

/**
 * 期货行情 Service
 * 
 * @author luomengan
 *
 */
@Service
public class FuturesMarketService {

	@Autowired
	private FuturesCommodityDao commodityDao;

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

	private SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
				result.setAskPrice(askPriceList.get(0).setScale(scale, RoundingMode.HALF_UP));
				result.setAskSize(askSizeList.get(0));
				result.setBidPrice(bidPriceList.get(0).setScale(scale, RoundingMode.HALF_UP));
				result.setBidSize(bidSizeList.get(0));
				result.setClosePrice(new BigDecimal(quote.getClosingPrice()).setScale(scale, RoundingMode.HALF_UP));
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
		result.setTotalVolume(0L);
		result.setVolume(0L);
		return result;
	}

	public List<FuturesContractLineData> dayLine(String commodityNo, String contractNo, String startTimeStr,
			String endTimeStr) {
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
		// 获取日K数据
		List<FuturesQuoteDayK> dayKList = dayKDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
						startTime, endTime);
		return CopyBeanUtils.copyListBeanPropertiesToList(dayKList, FuturesContractLineData.class);
	}

	public List<FuturesContractLineData> minsLine(String commodityNo, String contractNo, String startTimeStr,
			String endTimeStr) {
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
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			startTime = cal.getTime();
		}
		// 查询分时数据
		List<FuturesQuoteMinuteK> minuteKList = minuteKDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
						startTime, endTime);
		// 查询小时数据
		List<FuturesQuoteMinuteKGroup> minuteKGoupList = minuteKGroupDao
				.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
						startTime, endTime);
		// 统计
		List<FuturesContractLineData> result = new ArrayList<>();
		for (FuturesQuoteMinuteK minuteK : minuteKList) {
			result.add(CopyBeanUtils.copyBeanProperties(FuturesContractLineData.class, minuteK, false));
		}
		for (FuturesQuoteMinuteKGroup minuteKGoup : minuteKGoupList) {
			List<FuturesContractLineData> data = JacksonUtil.decode(minuteKGoup.getGroupData(),
					JacksonUtil.getGenericType(ArrayList.class, FuturesContractLineData.class));
			result.addAll(data);
		}
		// 排序
		Collections.sort(result);
		return result;
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

}
