package com.waben.stock.futuresgateway.yisheng.dao.impl.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteK;

/**
 * 行情-分钟K Repository
 * 
 * @author lma
 *
 */
public interface FuturesQuoteMinuteKRepository extends Repository<FuturesQuoteMinuteK, Long> {

	FuturesQuoteMinuteK save(FuturesQuoteMinuteK futuresQuoteMinuteK);

	void delete(Long id);

	Page<FuturesQuoteMinuteK> findAll(Pageable pageable);

	List<FuturesQuoteMinuteK> findAll();

	FuturesQuoteMinuteK findById(Long id);

	FuturesQuoteMinuteK findByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo, Date time);

	List<FuturesQuoteMinuteK> findByCommodityNoAndContractNo(String commodityNo, String contractNo, Sort sort);

	List<FuturesQuoteMinuteK> findByCommodityNoAndContractNoAndTimeStrLike(String commodityNo, String contractNo,
			String timeStr, Sort sort);

	List<FuturesQuoteMinuteK> findByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(String commodityNo,
			String contractNo, Date startTime, Date endTime, Sort sort);

}
