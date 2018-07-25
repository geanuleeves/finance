package com.waben.stock.futuresgateway.yisheng.dao.impl.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;

/**
 * 期货品种 Repository
 * 
 * @author lma
 *
 */
public interface FuturesCommodityRepository extends Repository<FuturesCommodity, Long> {

	FuturesCommodity save(FuturesCommodity futuresCommodity);

	void delete(Long id);

	Page<FuturesCommodity> findAll(Pageable pageable);

	List<FuturesCommodity> findAll();

	FuturesCommodity findById(Long id);

	FuturesCommodity findByCommodityNo(String commodityNo);

	List<FuturesCommodity> findByEnable(boolean enable);

}
