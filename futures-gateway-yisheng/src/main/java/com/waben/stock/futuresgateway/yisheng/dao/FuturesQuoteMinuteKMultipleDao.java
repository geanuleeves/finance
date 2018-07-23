package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKMultiple;

/**
 * 行情-多分钟K Dao
 * 
 * @author luomengan
 *
 */
public interface FuturesQuoteMinuteKMultipleDao {

	public FuturesQuoteMinuteKMultiple createFuturesQuoteMinuteKMultiple(FuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple);

	public void deleteFuturesQuoteMinuteKMultipleById(Long id);

	public FuturesQuoteMinuteKMultiple updateFuturesQuoteMinuteKMultiple(FuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple);

	public FuturesQuoteMinuteKMultiple retrieveFuturesQuoteMinuteKMultipleById(Long id);

	public Page<FuturesQuoteMinuteKMultiple> pageFuturesQuoteMinuteKMultiple(int page, int limit);

	public List<FuturesQuoteMinuteKMultiple> listFuturesQuoteMinuteKMultiple();

	public FuturesQuoteMinuteKMultiple retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time);

	public FuturesQuoteMinuteKMultiple retrieveNewestByCommodityNoAndContractNo(String commodityNo, String contractNo);

	public List<FuturesQuoteMinuteKMultiple> retriveByCommodityNoAndContractNoAndTimeStrLike(String commodityNo,
			String contractNo, String timeStr);

	public List<FuturesQuoteMinuteKMultiple> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime);

}
