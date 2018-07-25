package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteDayKDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesQuoteDayKRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteDayK;

/**
 * 行情-日K Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesQuoteDayKDaoImpl implements FuturesQuoteDayKDao {

	@Autowired
	private FuturesQuoteDayKRepository futuresQuoteDayKRepository;

	@Override
	public FuturesQuoteDayK createFuturesQuoteDayK(FuturesQuoteDayK futuresQuoteDayK) {
		return futuresQuoteDayKRepository.save(futuresQuoteDayK);
	}

	@Override
	public void deleteFuturesQuoteDayKById(Long id) {
		futuresQuoteDayKRepository.delete(id);
	}

	@Override
	public FuturesQuoteDayK updateFuturesQuoteDayK(FuturesQuoteDayK futuresQuoteDayK) {
		return futuresQuoteDayKRepository.save(futuresQuoteDayK);
	}

	@Override
	public FuturesQuoteDayK retrieveFuturesQuoteDayKById(Long id) {
		return futuresQuoteDayKRepository.findById(id);
	}

	@Override
	public Page<FuturesQuoteDayK> pageFuturesQuoteDayK(int page, int limit) {
		return futuresQuoteDayKRepository.findAll(new PageRequest(page, limit));
	}

	@Override
	public List<FuturesQuoteDayK> listFuturesQuoteDayK() {
		return futuresQuoteDayKRepository.findAll();
	}

	@Override
	public FuturesQuoteDayK retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time) {
		return futuresQuoteDayKRepository.findByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

	@Override
	public List<FuturesQuoteDayK> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "time"));
		return futuresQuoteDayKRepository.findByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
				commodityNo, contractNo, startTime, endTime, sort);
	}

}
