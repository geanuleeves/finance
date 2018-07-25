package com.waben.stock.futuresgateway.yisheng.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKDao;
import com.waben.stock.futuresgateway.yisheng.entity.MongoFuturesQuoteMinuteK;

/**
 * 行情-分钟K Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesQuoteMinuteKService {

	@Autowired
	private FuturesQuoteMinuteKDao futuresQuoteMinuteKDao;

	public MongoFuturesQuoteMinuteK getFuturesQuoteMinuteKInfo(String commodityNo, String contractNo, String id) {
		return futuresQuoteMinuteKDao.retrieveFuturesQuoteMinuteKById(commodityNo, contractNo, id);
	}

	@Transactional
	public MongoFuturesQuoteMinuteK addFuturesQuoteMinuteK(MongoFuturesQuoteMinuteK futuresQuoteMinuteK) {
		return futuresQuoteMinuteKDao.createFuturesQuoteMinuteK(futuresQuoteMinuteK);
	}

	@Transactional
	public void deleteFuturesQuoteMinuteK(String commodityNo, String contractNo, String id) {
		futuresQuoteMinuteKDao.deleteFuturesQuoteMinuteKById(commodityNo, contractNo, id);
	}

	@Transactional
	public void deleteFuturesQuoteMinuteKs(String commodityNo, String contractNo, String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresQuoteMinuteKDao.deleteFuturesQuoteMinuteKById(commodityNo, contractNo, id.trim());
				}
			}
		}
	}

	public Page<MongoFuturesQuoteMinuteK> futuresQuoteMinuteKs(String commodityNo, String contractNo, int page, int limit) {
		return futuresQuoteMinuteKDao.pageFuturesQuoteMinuteK(commodityNo, contractNo, page, limit);
	}

	public List<MongoFuturesQuoteMinuteK> list(String commodityNo, String contractNo) {
		return futuresQuoteMinuteKDao.listFuturesQuoteMinuteK(commodityNo, contractNo);
	}

	public MongoFuturesQuoteMinuteK getByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo, Date time) {
		return futuresQuoteMinuteKDao.retrieveByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

	public MongoFuturesQuoteMinuteK getNewestByCommodityNoAndContractNo(String commodityNo, String contractNo) {
		return futuresQuoteMinuteKDao.retrieveNewestByCommodityNoAndContractNo(commodityNo, contractNo);
	}

	public List<MongoFuturesQuoteMinuteK> getByCommodityNoAndContractNoAndTimeStrLike(String commodityNo, String contractNo,
			String timeStr) {
		return futuresQuoteMinuteKDao.retriveByCommodityNoAndContractNoAndTimeStrLike(commodityNo, contractNo, timeStr);
	}

	public List<MongoFuturesQuoteMinuteK> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		return futuresQuoteMinuteKDao.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
				commodityNo, contractNo, startTime, endTime);
	}

}
