package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteKMultiple;

/**
 * 行情-多分钟K Dao
 * 
 * @author lma
 *
 */
public interface FuturesQuoteMinuteKMultipleDao {

	public MongoFuturesQuoteMinuteKMultiple createFuturesQuoteMinuteKMultiple(
			MongoFuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple);

	public void deleteFuturesQuoteMinuteKMultipleById(String commodityNo, String contractNo, String id);

	public MongoFuturesQuoteMinuteKMultiple retrieveFuturesQuoteMinuteKMultipleById(String commodityNo,
			String contractNo, String id);

	public Page<MongoFuturesQuoteMinuteKMultiple> pageFuturesQuoteMinuteKMultiple(String commodityNo, String contractNo,
			int page, int limit);

	public List<MongoFuturesQuoteMinuteKMultiple> listFuturesQuoteMinuteKMultiple(String commodityNo,
			String contractNo);

	public MongoFuturesQuoteMinuteKMultiple retrieveByCommodityNoAndContractNoAndTime(String commodityNo,
			String contractNo, Date time);

	public MongoFuturesQuoteMinuteKMultiple retrieveNewestByCommodityNoAndContractNo(String commodityNo,
			String contractNo);

	public List<MongoFuturesQuoteMinuteKMultiple> retriveByCommodityNoAndContractNoAndTimeStrLike(String commodityNo,
			String contractNo, String timeStr);

	public List<MongoFuturesQuoteMinuteKMultiple> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime);

}
