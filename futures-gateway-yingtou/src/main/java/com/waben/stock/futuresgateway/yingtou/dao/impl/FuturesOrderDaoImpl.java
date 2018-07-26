package com.waben.stock.futuresgateway.yingtou.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yingtou.dao.FuturesOrderDao;
import com.waben.stock.futuresgateway.yingtou.dao.impl.jpa.FuturesOrderRepository;
import com.waben.stock.futuresgateway.yingtou.entity.FuturesOrder;

/**
 * 期货订单 Dao实现
 * 
 * @author lma
 *
 */
@Repository
public class FuturesOrderDaoImpl implements FuturesOrderDao {

	@Autowired
	private FuturesOrderRepository futuresOrderRepository;

	@Override
	public FuturesOrder createFuturesOrder(FuturesOrder futuresOrder) {
		return futuresOrderRepository.save(futuresOrder);
	}

	@Override
	public void deleteFuturesOrderById(Long id) {
		futuresOrderRepository.delete(id);
	}

	@Override
	public FuturesOrder updateFuturesOrder(FuturesOrder futuresOrder) {
		return futuresOrderRepository.save(futuresOrder);
	}

	@Override
	public FuturesOrder retrieveFuturesOrderById(Long id) {
		return futuresOrderRepository.findById(id);
	}

	@Override
	public Page<FuturesOrder> pageFuturesOrder(int page, int limit) {
		return futuresOrderRepository.findAll(new PageRequest(page, limit));
	}
	
	@Override
	public List<FuturesOrder> listFuturesOrder() {
		return futuresOrderRepository.findAll();
	}

	@Override
	public FuturesOrder retrieveFuturesOrderByTwsOrderId(int twsOrderId) {
		return futuresOrderRepository.findByTwsOrderId(twsOrderId);
	}

	@Override
	public FuturesOrder retrieveFuturesOrderByDomainAndOuterOrderId(String domain, Long outerOrderId) {
		return futuresOrderRepository.findByDomainAndOuterOrderId(domain, outerOrderId);
	}

}
