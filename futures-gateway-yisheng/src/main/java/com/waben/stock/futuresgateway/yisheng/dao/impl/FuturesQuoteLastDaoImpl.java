package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteLastDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesQuoteLastRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;

/**
 * 期货合约行情-最新 Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesQuoteLastDaoImpl implements FuturesQuoteLastDao {

	@Autowired
	private FuturesQuoteLastRepository futuresQuoteLastRepository;

	@Override
	public FuturesQuoteLast createFuturesQuoteLast(FuturesQuoteLast futuresQuoteLast) {
		return futuresQuoteLastRepository.save(futuresQuoteLast);
	}

	@Override
	public void deleteFuturesQuoteLastById(Integer id) {
		futuresQuoteLastRepository.delete(id);
	}

	@Override
	public FuturesQuoteLast updateFuturesQuoteLast(FuturesQuoteLast futuresQuoteLast) {
		return futuresQuoteLastRepository.save(futuresQuoteLast);
	}

	@Override
	public FuturesQuoteLast retrieveFuturesQuoteLastById(Integer id) {
		return futuresQuoteLastRepository.findById(id);
	}

	@Override
	public Page<FuturesQuoteLast> pageFuturesQuoteLast(int page, int limit) {
		return futuresQuoteLastRepository.findAll(new PageRequest(page, limit));
	}

	@Override
	public List<FuturesQuoteLast> listFuturesQuoteLast() {
		return futuresQuoteLastRepository.findAll();
	}

	@Override
	public FuturesQuoteLast retriveNewest(String commodityNo, String contractNo) {
		Pageable pageable = new PageRequest(0, 1, new Sort(new Sort.Order(Direction.DESC, "dateTimeStamp")));
		Page<FuturesQuoteLast> pages = futuresQuoteLastRepository.findByCommodityNoAndContractNo(commodityNo,
				contractNo, pageable);
		if (pages.getContent() != null && pages.getContent().size() > 0) {
			return pages.getContent().get(0);
		}
		return null;
	}

}
