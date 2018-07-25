package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;

/**
 * 行情-分钟K组合 Dao
 * 
 * @author lma
 *
 */
public interface FuturesQuoteMinuteKGroupDao {

	public FuturesQuoteMinuteKGroup createFuturesQuoteMinuteKGroup(FuturesQuoteMinuteKGroup futuresQuoteMinuteKGroup);

	public void deleteFuturesQuoteMinuteKGroupById(Long id);

	public FuturesQuoteMinuteKGroup updateFuturesQuoteMinuteKGroup(FuturesQuoteMinuteKGroup futuresQuoteMinuteKGroup);

	public FuturesQuoteMinuteKGroup retrieveFuturesQuoteMinuteKGroupById(Long id);

	public Page<FuturesQuoteMinuteKGroup> pageFuturesQuoteMinuteKGroup(int page, int limit);
	
	public List<FuturesQuoteMinuteKGroup> listFuturesQuoteMinuteKGroup();

	public FuturesQuoteMinuteKGroup retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time);

	public List<FuturesQuoteMinuteKGroup> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime);

}
