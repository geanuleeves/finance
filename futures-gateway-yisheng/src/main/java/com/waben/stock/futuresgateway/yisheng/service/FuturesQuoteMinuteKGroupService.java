package com.waben.stock.futuresgateway.yisheng.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKGroupDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;

/**
 * 行情-分钟K组合 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesQuoteMinuteKGroupService {

	@Autowired
	private FuturesQuoteMinuteKGroupDao futuresQuoteMinuteKGroupDao;

	public FuturesQuoteMinuteKGroup getFuturesQuoteMinuteKGroupInfo(Long id) {
		return futuresQuoteMinuteKGroupDao.retrieveFuturesQuoteMinuteKGroupById(id);
	}

	@Transactional
	public FuturesQuoteMinuteKGroup addFuturesQuoteMinuteKGroup(FuturesQuoteMinuteKGroup futuresQuoteMinuteKGroup) {
		return futuresQuoteMinuteKGroupDao.createFuturesQuoteMinuteKGroup(futuresQuoteMinuteKGroup);
	}

	@Transactional
	public FuturesQuoteMinuteKGroup modifyFuturesQuoteMinuteKGroup(FuturesQuoteMinuteKGroup futuresQuoteMinuteKGroup) {
		return futuresQuoteMinuteKGroupDao.updateFuturesQuoteMinuteKGroup(futuresQuoteMinuteKGroup);
	}

	@Transactional
	public void deleteFuturesQuoteMinuteKGroup(Long id) {
		futuresQuoteMinuteKGroupDao.deleteFuturesQuoteMinuteKGroupById(id);
	}

	@Transactional
	public void deleteFuturesQuoteMinuteKGroups(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresQuoteMinuteKGroupDao.deleteFuturesQuoteMinuteKGroupById(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<FuturesQuoteMinuteKGroup> futuresQuoteMinuteKGroups(int page, int limit) {
		return futuresQuoteMinuteKGroupDao.pageFuturesQuoteMinuteKGroup(page, limit);
	}

	public List<FuturesQuoteMinuteKGroup> list() {
		return futuresQuoteMinuteKGroupDao.listFuturesQuoteMinuteKGroup();
	}

	public FuturesQuoteMinuteKGroup getByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time) {
		return futuresQuoteMinuteKGroupDao.retrieveByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

	public List<FuturesQuoteMinuteKGroup> getByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		return futuresQuoteMinuteKGroupDao.retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
				commodityNo, contractNo, startTime, endTime);
	}

}
