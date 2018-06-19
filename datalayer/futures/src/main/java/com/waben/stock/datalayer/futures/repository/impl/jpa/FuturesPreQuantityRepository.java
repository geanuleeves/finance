package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;

public interface FuturesPreQuantityRepository extends CustomJpaRepository<FuturesPreQuantity, Long> {

	List<FuturesPreQuantity> findByCommodityId(Long commodityId);
	
	@Transactional
	@Modifying(clearAutomatically = true)  
	@Query(value = "DELETE FROM f_futures_prequantity where commodity_id=?1", nativeQuery = true) 
	int deleteByCommodityId(Long commodityId);
}
