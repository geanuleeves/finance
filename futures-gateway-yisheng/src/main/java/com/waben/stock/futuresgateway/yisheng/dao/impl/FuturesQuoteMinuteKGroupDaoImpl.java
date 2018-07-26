package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKGroupDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesQuoteMinuteKGroupRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKGroup;

/**
 * 行情-分钟K组合 Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesQuoteMinuteKGroupDaoImpl implements FuturesQuoteMinuteKGroupDao {

	@Autowired
	private FuturesQuoteMinuteKGroupRepository futuresQuoteMinuteKGroupRepository;

	@Override
	public FuturesQuoteMinuteKGroup createFuturesQuoteMinuteKGroup(FuturesQuoteMinuteKGroup futuresQuoteMinuteKGroup) {
		return futuresQuoteMinuteKGroupRepository.save(futuresQuoteMinuteKGroup);
	}

	@Override
	public void deleteFuturesQuoteMinuteKGroupById(Long id) {
		futuresQuoteMinuteKGroupRepository.delete(id);
	}

	@Override
	public FuturesQuoteMinuteKGroup updateFuturesQuoteMinuteKGroup(FuturesQuoteMinuteKGroup futuresQuoteMinuteKGroup) {
		return futuresQuoteMinuteKGroupRepository.save(futuresQuoteMinuteKGroup);
	}

	@Override
	public FuturesQuoteMinuteKGroup retrieveFuturesQuoteMinuteKGroupById(Long id) {
		return futuresQuoteMinuteKGroupRepository.findById(id);
	}

	@Override
	public Page<FuturesQuoteMinuteKGroup> pageFuturesQuoteMinuteKGroup(int page, int limit) {
		return futuresQuoteMinuteKGroupRepository.findAll(new PageRequest(page, limit));
	}

	@Override
	public List<FuturesQuoteMinuteKGroup> listFuturesQuoteMinuteKGroup() {
		return futuresQuoteMinuteKGroupRepository.findAll();
	}

	@Override
	public FuturesQuoteMinuteKGroup retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time) {
		return futuresQuoteMinuteKGroupRepository.findByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

	@Override
	public List<FuturesQuoteMinuteKGroup> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "time"));
		return futuresQuoteMinuteKGroupRepository.findByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
				commodityNo, contractNo, startTime, endTime, sort);
	}

}
