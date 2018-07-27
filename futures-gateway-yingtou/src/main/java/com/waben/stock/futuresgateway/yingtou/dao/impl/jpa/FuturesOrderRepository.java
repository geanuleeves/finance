package com.waben.stock.futuresgateway.yingtou.dao.impl.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import com.waben.stock.futuresgateway.yingtou.entity.FuturesOrder;

/**
 * 期货订单 Repository
 * 
 * @author lma
 *
 */
public interface FuturesOrderRepository extends Repository<FuturesOrder, Long> {

	FuturesOrder save(FuturesOrder futuresOrder);

	void delete(Long id);

	Page<FuturesOrder> findAll(Pageable pageable);
	
	List<FuturesOrder> findAll();

	FuturesOrder findById(Long id);

	FuturesOrder findByTwsOrderId(int twsOrderId);

	FuturesOrder findByDomainAndOuterOrderId(String domain, Long outerOrderId);
	
}
