package com.waben.stock.futuresgateway.yisheng.dao.impl.jpa;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuote;

/**
 * 期货合约行情 Repository
 * 
 * @author lma
 *
 */
public interface FuturesQuoteRepository extends Repository<FuturesQuote, Long> {

	FuturesQuote save(FuturesQuote futuresContractQuote);

	void delete(Long id);

	Page<FuturesQuote> findAll(Pageable pageable);

	List<FuturesQuote> findAll();

	FuturesQuote findById(Long id);

	List<FuturesQuote> findByCommodityNoAndContractNoAndDateTimeStampLike(String commodityNo, String contractNo,
			String dateTimeStamp, Sort sort);

	Long countByTimeGreaterThanEqual(Date time);

	Page<FuturesQuote> findByCommodityNoAndContractNo(String commodityNo, String contractNo, Pageable pageable);

}
