package com.waben.stock.futuresgateway.yisheng.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDayKDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;

/**
 * 行情-日K Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesQuoteDayKService {

	@Autowired
	private FuturesQuoteDayKDao futuresQuoteDayKDao;

	public FuturesQuoteDayK getFuturesQuoteDayKInfo(Long id) {
		return futuresQuoteDayKDao.retrieveFuturesQuoteDayKById(id);
	}

	@Transactional
	public FuturesQuoteDayK addFuturesQuoteDayK(FuturesQuoteDayK futuresQuoteDayK) {
		return futuresQuoteDayKDao.createFuturesQuoteDayK(futuresQuoteDayK);
	}

	@Transactional
	public FuturesQuoteDayK modifyFuturesQuoteDayK(FuturesQuoteDayK futuresQuoteDayK) {
		return futuresQuoteDayKDao.updateFuturesQuoteDayK(futuresQuoteDayK);
	}

	@Transactional
	public void deleteFuturesQuoteDayK(Long id) {
		futuresQuoteDayKDao.deleteFuturesQuoteDayKById(id);
	}

	@Transactional
	public void deleteFuturesQuoteDayKs(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresQuoteDayKDao.deleteFuturesQuoteDayKById(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<FuturesQuoteDayK> futuresQuoteDayKs(int page, int limit) {
		return futuresQuoteDayKDao.pageFuturesQuoteDayK(page, limit);
	}

	public List<FuturesQuoteDayK> list() {
		return futuresQuoteDayKDao.listFuturesQuoteDayK();
	}

	public FuturesQuoteDayK getByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo, Date time) {
		return futuresQuoteDayKDao.retrieveByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

}
