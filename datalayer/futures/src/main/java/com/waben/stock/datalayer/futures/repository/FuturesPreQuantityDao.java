package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;

public interface FuturesPreQuantityDao extends BaseDao<FuturesPreQuantity, Long> {

	List<FuturesPreQuantity> findByCommodityId(Long commodityId);
	
	int deleteByCommodityId(Long commodityId);
}
