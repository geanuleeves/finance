package com.waben.stock.futuresgateway.yisheng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesCommodityDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;

/**
 * 期货品种 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesCommodityService {

	@Autowired
	private FuturesCommodityDao futuresCommodityDao;

	public FuturesCommodity getFuturesCommodityInfo(Long id) {
		return futuresCommodityDao.retrieveFuturesCommodityById(id);
	}

	@Transactional
	public FuturesCommodity addFuturesCommodity(FuturesCommodity futuresCommodity) {
		return futuresCommodityDao.createFuturesCommodity(futuresCommodity);
	}

	@Transactional
	public FuturesCommodity modifyFuturesCommodity(FuturesCommodity futuresCommodity) {
		return futuresCommodityDao.updateFuturesCommodity(futuresCommodity);
	}

	@Transactional
	public void deleteFuturesCommodity(Long id) {
		futuresCommodityDao.deleteFuturesCommodityById(id);
	}

	@Transactional
	public void deleteFuturesCommoditys(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresCommodityDao.deleteFuturesCommodityById(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<FuturesCommodity> futuresCommoditys(int page, int limit) {
		return futuresCommodityDao.pageFuturesCommodity(page, limit);
	}

	public List<FuturesCommodity> list() {
		return futuresCommodityDao.listFuturesCommodity();
	}

	public FuturesCommodity getByCommodityNo(String commodityNo) {
		return futuresCommodityDao.retrieveByCommodityNo(commodityNo);
	}

}
