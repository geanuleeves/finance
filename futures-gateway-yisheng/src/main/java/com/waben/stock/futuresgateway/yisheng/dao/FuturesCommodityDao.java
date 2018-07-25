package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;

/**
 * 期货品种 Dao
 * 
 * @author lma
 *
 */
public interface FuturesCommodityDao {

	public FuturesCommodity createFuturesCommodity(FuturesCommodity futuresCommodity);

	public void deleteFuturesCommodityById(Long id);

	public FuturesCommodity updateFuturesCommodity(FuturesCommodity futuresCommodity);

	public FuturesCommodity retrieveFuturesCommodityById(Long id);

	public Page<FuturesCommodity> pageFuturesCommodity(int page, int limit);
	
	public List<FuturesCommodity> listFuturesCommodity();

	public FuturesCommodity retrieveByCommodityNo(String commodityNo);

	public List<FuturesCommodity> retrieveByEnable(boolean enable);

}
