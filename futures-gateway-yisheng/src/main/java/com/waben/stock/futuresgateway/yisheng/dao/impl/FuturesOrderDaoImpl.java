package com.waben.stock.futuresgateway.yisheng.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesOrderDao;
import com.waben.stock.futuresgateway.yisheng.dao.impl.jpa.FuturesOrderRepository;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;

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
	public FuturesOrder retrieveFuturesOrderByDomainAndOuterOrderId(String domain, Long outerOrderId) {
		return futuresOrderRepository.findByDomainAndOuterOrderId(domain, outerOrderId);
	}

	@Override
	public FuturesOrder retrieveByOrderSessionId(int orderSessionId) {
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "createTime"));
		List<FuturesOrder> orderList = futuresOrderRepository.findByOrderSessionId(orderSessionId, sort);
		if (orderList != null && orderList.size() > 0) {
			return orderList.get(0);
		}
		return null;
	}

	@Override
	public FuturesOrder retrieveByOrderNo(String orderNo) {
		return futuresOrderRepository.findByOrderNo(orderNo);
	}

	@Override
	public FuturesOrder retriveByYtTwsOrderId(int ytTwsOrderId) {
		Sort sort = new Sort(new Sort.Order(Direction.DESC, "createTime"));
		List<FuturesOrder> orderList = futuresOrderRepository.findByYtTwsOrderId(ytTwsOrderId, sort);
		if (orderList != null && orderList.size() > 0) {
			return orderList.get(0);
		}
		return null;
	}

}
