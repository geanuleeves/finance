package com.waben.stock.futuresgateway.yisheng.dao.impl.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;

/**
 * 期货合约 Repository
 * 
 * @author lma
 *
 */
public interface FuturesContractRepository extends Repository<FuturesContract, Long> {

	FuturesContract save(FuturesContract futuresContract);

	void delete(Long id);

	Page<FuturesContract> findAll(Pageable pageable);

	List<FuturesContract> findAll();

	FuturesContract findById(Long id);

	FuturesContract findByCommodityNoAndContractNo(String commodityNo, String contractNo);

	List<FuturesContract> findByEnable(Boolean enable);

	List<FuturesContract> findByCommodityNoAndEnable(String commodityNo, boolean enable);

}
