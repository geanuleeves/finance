package com.waben.stock.futuresgateway.yisheng.dao.impl.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;

/**
 * 期货合约行情-最新 Repository
 * 
 * @author lma
 *
 */
public interface FuturesQuoteLastRepository extends Repository<FuturesQuoteLast, Integer> {

	FuturesQuoteLast save(FuturesQuoteLast futuresQuoteLast);

	void delete(Integer id);

	Page<FuturesQuoteLast> findAll(Pageable pageable);
	
	List<FuturesQuoteLast> findAll();

	FuturesQuoteLast findById(Integer id);

	Page<FuturesQuoteLast> findByCommodityNoAndContractNo(String commodityNo, String contractNo, Pageable pageable);
	
}
