package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesCommodityDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesCommodityRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;

/**
 * 期货品种 Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesCommodityDaoImpl implements FuturesCommodityDao {

	@Autowired
	private FuturesCommodityRepository futuresCommodityRepository;

	@Override
	public FuturesCommodity createFuturesCommodity(FuturesCommodity futuresCommodity) {
		return futuresCommodityRepository.save(futuresCommodity);
	}

	@Override
	public void deleteFuturesCommodityById(Long id) {
		futuresCommodityRepository.delete(id);
	}

	@Override
	public FuturesCommodity updateFuturesCommodity(FuturesCommodity futuresCommodity) {
		return futuresCommodityRepository.save(futuresCommodity);
	}

	@Override
	public FuturesCommodity retrieveFuturesCommodityById(Long id) {
		return futuresCommodityRepository.findById(id);
	}

	@Override
	public Page<FuturesCommodity> pageFuturesCommodity(int page, int limit) {
		return futuresCommodityRepository.findAll(new PageRequest(page, limit));
	}
	
	@Override
	public List<FuturesCommodity> listFuturesCommodity() {
		return futuresCommodityRepository.findAll();
	}

	@Override
	public FuturesCommodity retrieveByCommodityNo(String commodityNo) {
		return futuresCommodityRepository.findByCommodityNo(commodityNo);
	}

	@Override
	public List<FuturesCommodity> retrieveByEnable(boolean enable) {
		return futuresCommodityRepository.findByEnable(enable);
	}

}
