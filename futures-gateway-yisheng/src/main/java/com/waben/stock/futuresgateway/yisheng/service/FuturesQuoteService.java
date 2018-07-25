package com.waben.stock.futuresgateway.yisheng.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;

/**
 * 期货合约行情 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesQuoteService {

	@Autowired
	private FuturesQuoteDao quoteDao;

	public FuturesQuote getFuturesQuoteInfo(String commodityNo, String contractNo, String id) {
		return quoteDao.retrieveFuturesQuoteById(commodityNo, contractNo, id);
	}

	@Transactional
	public FuturesQuote addFuturesQuote(FuturesQuote futuresQuote) {
		return quoteDao.createFuturesQuote(futuresQuote);
	}

	@Transactional
	public void deleteFuturesQuote(String commodityNo, String contractNo, String id) {
		quoteDao.deleteFuturesQuoteById(commodityNo, contractNo, id);
	}

	@Transactional
	public void deleteFuturesQuotes(String commodityNo, String contractNo, String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					quoteDao.deleteFuturesQuoteById(commodityNo, contractNo, id);
				}
			}
		}
	}

	public Page<FuturesQuote> futuresQuotes(String commodityNo, String contractNo, int page, int limit) {
		return quoteDao.pageFuturesQuote(commodityNo, contractNo, page, limit);
	}

	public List<FuturesQuote> list(String commodityNo, String contractNo) {
		return quoteDao.listFuturesQuote(commodityNo, contractNo);
	}

	public List<FuturesQuote> getByCommodityNoAndContractNoAndDateTimeStampLike(String commodityNo, String contractNo,
			String dateTimeStamp) {
		return quoteDao.retrieveByCommodityNoAndContractNoAndDateTimeStampLike(commodityNo, contractNo, dateTimeStamp);
	}

	public Long countByTimeGreaterThanEqual(String commodityNo, String contractNo, Date time) {
		return quoteDao.countByTimeGreaterThanEqual(commodityNo, contractNo, time);
	}

}
