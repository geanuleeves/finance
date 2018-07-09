package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;

public interface FuturesCommodityRepository extends CustomJpaRepository<FuturesCommodity, Long> {

	List<FuturesCommodity> findByExchange(FuturesExchange exchange);

	FuturesCommodity findBySymbol(String symbol);
}
