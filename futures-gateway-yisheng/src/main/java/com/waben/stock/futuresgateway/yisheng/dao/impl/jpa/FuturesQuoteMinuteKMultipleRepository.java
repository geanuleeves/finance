package com.waben.stock.futuresgateway.yisheng.dao.impl.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteMinuteKMultiple;

/**
 * 行情-多分钟K Repository
 * 
 * @author luomengan
 *
 */
public interface FuturesQuoteMinuteKMultipleRepository extends Repository<FuturesQuoteMinuteKMultiple, Long> {

	FuturesQuoteMinuteKMultiple save(FuturesQuoteMinuteKMultiple futuresQuoteMinuteKMultiple);

	void delete(Long id);

	Page<FuturesQuoteMinuteKMultiple> findAll(Pageable pageable);

	List<FuturesQuoteMinuteKMultiple> findAll();

	FuturesQuoteMinuteKMultiple findById(Long id);

	FuturesQuoteMinuteKMultiple findByCommodityNoAndContractNoAndTime(String commodityNo, String contractNo, Date time);

	List<FuturesQuoteMinuteKMultiple> findByCommodityNoAndContractNo(String commodityNo, String contractNo, Sort sort);

	List<FuturesQuoteMinuteKMultiple> findByCommodityNoAndContractNoAndTimeStrLike(String commodityNo, String contractNo,
			String timeStr, Sort sort);

	List<FuturesQuoteMinuteKMultiple> findByCommodityNoAndContractNoAndTimeGreaterThanEqualAndTimeLessThan(String commodityNo,
			String contractNo, Date startTime, Date endTime, Sort sort);

}
