package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteMinuteKMultipleDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesQuoteMinuteKMultipleRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKMultiple;

/**
 * 行情-多分钟K Dao实现
 * 
 * @author luomengan
 *
 */
@Repository
public class FuturesQuoteMinuteKMultipleDaoImpl implements FuturesQuoteMinuteKMultipleDao {

	@Autowired
	private FuturesQuoteMinuteKMultipleRepository repository;

	@Override
	public FuturesQuoteMinuteKMultiple createFuturesQuoteMinuteKMultiple(
			FuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple) {
		return repository.save(futuresQuoteMinuteKMultiple);
	}

	@Override
	public void deleteFuturesQuoteMinuteKMultipleById(Long id) {
		repository.delete(id);
	}

	@Override
	public FuturesQuoteMinuteKMultiple updateFuturesQuoteMinuteKMultiple(
			FuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple) {
		return repository.save(futuresQuoteMinuteKMultiple);
	}

	@Override
	public FuturesQuoteMinuteKMultiple retrieveFuturesQuoteMinuteKMultipleById(Long id) {
		return repository.findById(id);
	}

	@Override
	public Page<FuturesQuoteMinuteKMultiple> pageFuturesQuoteMinuteKMultiple(int page, int limit) {
		return repository.findAll(new PageRequest(page, limit));
	}

	@Override
	public List<FuturesQuoteMinuteKMultiple> listFuturesQuoteMinuteKMultiple() {
		return repository.findAll();
	}

	@Override
	public FuturesQuoteMinuteKMultiple retrieveByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo,
			Date time) {
		return repository.findByCommodityNoAndContractNoAndTime(commodityNo, contractNo, time);
	}

	@Override
	public FuturesQuoteMinuteKMultiple retrieveNewestByCommodityNoAndContractNo(String commodityNo, String contractNo) {
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "time"));
		List<FuturesQuoteMinuteKMultiple> list = repository.findByCommodityNoAndContractNo(commodityNo, contractNo,
				sort);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Override
	public List<FuturesQuoteMinuteKMultiple> retriveByCommodityNoAndContractNoAndTimeStrLike(String commodityNo,
			String contractNo, String timeStr) {
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "time"));
		return repository.findByCommodityNoAndContractNoAndTimeStrLike(commodityNo, contractNo, timeStr, sort);
	}

	@Override
	public List<FuturesQuoteMinuteKMultiple> retrieveByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(
			String commodityNo, String contractNo, Date startTime, Date endTime) {
		Sort sort = new Sort(new Sort.Order(Direction.ASC, "time"));
		return repository.findByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(commodityNo, contractNo,
				startTime, endTime, sort);
	}

}
