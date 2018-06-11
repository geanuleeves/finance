package com.waben.stock.futuresgateway.yisheng.dao;

import java.util.List;

import org.springframework.data.domain.Page;

import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;

/**
 * 期货订单 Dao
 * 
 * @author luomengan
 *
 */
public interface FuturesOrderDao {

	public FuturesOrder createFuturesOrder(FuturesOrder futuresOrder);

	public void deleteFuturesOrderById(Long id);

	public FuturesOrder updateFuturesOrder(FuturesOrder futuresOrder);

	public FuturesOrder retrieveFuturesOrderById(Long id);

	public Page<FuturesOrder> pageFuturesOrder(int page, int limit);
	
	public List<FuturesOrder> listFuturesOrder();

	public FuturesOrder retrieveFuturesOrderByTwsOrderId(int twsOrderId);

	public FuturesOrder retrieveFuturesOrderByDomainAndOuterOrderId(String domain, Long outerOrderId);

}
