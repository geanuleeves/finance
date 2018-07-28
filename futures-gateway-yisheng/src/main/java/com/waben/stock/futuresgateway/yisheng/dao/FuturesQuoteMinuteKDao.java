package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteK;

/**
 * 行情-分钟K Dao
 * 
 * @author lma
 *
 */
public interface FuturesQuoteMinuteKDao {
	
	public List<FuturesQuoteMinuteK> listDbMinuteK();
	
	public Page<FuturesQuoteMinuteK> pageDbMinuteK(int page, int size);

	public MongoFuturesQuoteMinuteK createFuturesQuoteMinuteK(MongoFuturesQuoteMinuteK futuresQuoteMinuteK);

	public void deleteFuturesQuoteMinuteKById(String commodityNo, String contractNo, String id);

	public MongoFuturesQuoteMinuteK retrieveFuturesQuoteMinuteKById(String commodityNo, String contractNo, String id);

	public Page<MongoFuturesQuoteMinuteK> pageFuturesQuoteMinuteK(String commodityNo, String contractNo, int page, int limit);

	public List<MongoFuturesQuoteMinuteK> listFuturesQuoteMinuteK(String commodityNo, String contractNo);

	public MongoFuturesQuoteMinuteK retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time);

	public MongoFuturesQuoteMinuteK retrieveNewestByCommodityNoAndContractNo(String commodityNo, String contractNo);

	public List<MongoFuturesQuoteMinuteK> retriveByCommodityNoAndContractNoAndTimeStrLike(String commodityNo,
			String contractNo, String timeStr);

	public List<MongoFuturesQuoteMinuteK> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime);

}
