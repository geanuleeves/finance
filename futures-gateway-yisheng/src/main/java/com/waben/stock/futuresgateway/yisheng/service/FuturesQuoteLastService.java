package com.waben.stock.futuresgateway.yisheng.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesQuoteLastDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesQuoteLast;

/**
 * 期货合约行情-最新 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesQuoteLastService {

	@Autowired
	private FuturesQuoteLastDao futuresQuoteLastDao;

	public FuturesQuoteLast getFuturesQuoteLastInfo(Integer id) {
		return futuresQuoteLastDao.retrieveFuturesQuoteLastById(id);
	}

	@Transactional
	public FuturesQuoteLast addFuturesQuoteLast(FuturesQuoteLast futuresQuoteLast) {
		return futuresQuoteLastDao.createFuturesQuoteLast(futuresQuoteLast);
	}

	@Transactional
	public FuturesQuoteLast modifyFuturesQuoteLast(FuturesQuoteLast futuresQuoteLast) {
		return futuresQuoteLastDao.updateFuturesQuoteLast(futuresQuoteLast);
	}

	@Transactional
	public void deleteFuturesQuoteLast(Integer id) {
		futuresQuoteLastDao.deleteFuturesQuoteLastById(id);
	}
	
	@Transactional
	public void deleteFuturesQuoteLasts(String ids) {
		if(ids != null) {
			String[] idArr= ids.split(",");
			for(String id : idArr) {
				if(!"".equals(id.trim())) {
					futuresQuoteLastDao.deleteFuturesQuoteLastById(Integer.parseInt(id.trim()));
				}
			}
		}
	}

	public Page<FuturesQuoteLast> futuresQuoteLasts(int page, int limit) {
		return futuresQuoteLastDao.pageFuturesQuoteLast(page, limit);
	}
	
	public List<FuturesQuoteLast> list() {
		return futuresQuoteLastDao.listFuturesQuoteLast();
	}

}
