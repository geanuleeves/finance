package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;

/**
 * 期货订单 Dao
 * 
 * @author lma
 *
 */
public interface FuturesOrderDao {

	public FuturesOrder createFuturesOrder(FuturesOrder futuresOrder);

	public void deleteFuturesOrderById(Long id);

	public FuturesOrder updateFuturesOrder(FuturesOrder futuresOrder);

	public FuturesOrder retrieveFuturesOrderById(Long id);

	public Page<FuturesOrder> pageFuturesOrder(int page, int limit);

	public List<FuturesOrder> listFuturesOrder();

	public FuturesOrder retrieveFuturesOrderByDomainAndOuterOrderId(String domain, Long outerOrderId);

	public FuturesOrder retrieveByOrderSessionId(int orderSessionId);

	public FuturesOrder retrieveByOrderNo(String orderNo);

	public FuturesOrder retriveByYtTwsOrderId(int ytTwsOrderId);

}
