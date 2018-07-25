package com.waben.stock.datalayer.stockoption.repository.impl.jpa;

import java.util.List;

import com.waben.stock.datalayer.stockoption.entity.StockOptionCycle;

/**
 * 期权周期 Jpa
 * 
 * @author lma
 *
 */
public interface StockOptionCycleRepository extends CustomJpaRepository<StockOptionCycle, Long> {

	List<StockOptionCycle> findByCycle(Integer cycle);

}
