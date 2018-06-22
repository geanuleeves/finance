package com.waben.stock.futuresgateway.yisheng.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;

/**
 * 行情-分钟K Service
 * 
 * @author luomengan
 *
 */
@Service
public class FuturesQuoteMinuteKService {

	@Autowired
	private FuturesQuoteMinuteKDao futuresQuoteMinuteKDao;

	public FuturesQuoteMinuteK getFuturesQuoteMinuteKInfo(Long id) {
		return futuresQuoteMinuteKDao.retrieveFuturesQuoteMinuteKById(id);
	}

	@Transactional
	public FuturesQuoteMinuteK addFuturesQuoteMinuteK(FuturesQuoteMinuteK futuresQuoteMinuteK) {
		return futuresQuoteMinuteKDao.createFuturesQuoteMinuteK(futuresQuoteMinuteK);
	}

	@Transactional
	public FuturesQuoteMinuteK modifyFuturesQuoteMinuteK(FuturesQuoteMinuteK futuresQuoteMinuteK) {
		return futuresQuoteMinuteKDao.updateFuturesQuoteMinuteK(futuresQuoteMinuteK);
	}

	@Transactional
	public void deleteFuturesQuoteMinuteK(Long id) {
		futuresQuoteMinuteKDao.deleteFuturesQuoteMinuteKById(id);
	}

	@Transactional
	public void deleteFuturesQuoteMinuteKs(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresQuoteMinuteKDao.deleteFuturesQuoteMinuteKById(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<FuturesQuoteMinuteK> futuresQuoteMinuteKs(int page, int limit) {
		return futuresQuoteMinuteKDao.pageFuturesQuoteMinuteK(page, limit);
	}

	public List<FuturesQuoteMinuteK> list() {
		return futuresQuoteMinuteKDao.listFuturesQuoteMinuteK();
	}

	public FuturesQuoteMinuteK getByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo, Date time) {
		return futuresQuoteMinuteKDao.retrieveByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

	public FuturesQuoteMinuteK getNewestByCommodityNoAndContractNo(String commodityNo, String contractNo) {
		return futuresQuoteMinuteKDao.retrieveNewestByCommodityNoAndContractNo(commodityNo, contractNo);
	}

	public List<FuturesQuoteMinuteK> getByCommodityNoAndContractNoAndTimeStrLike(String commodityNo, String contractNo,
			String timeStr) {
		return futuresQuoteMinuteKDao.retriveByCommodityNoAndContractNoAndTimeStrLike(commodityNo, contractNo, timeStr);
	}

	public List<FuturesQuoteMinuteK> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		return futuresQuoteMinuteKDao.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
				commodityNo, contractNo, startTime, endTime);
	}

}
