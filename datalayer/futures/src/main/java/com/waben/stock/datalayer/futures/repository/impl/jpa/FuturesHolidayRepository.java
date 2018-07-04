package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.util.List;


import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;

public interface FuturesHolidayRepository extends CustomJpaRepository<FuturesHoliday, Long> {
	
	List<FuturesHoliday> findByCommodity(FuturesCommodity commodity);

}
