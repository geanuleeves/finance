package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;

public interface FuturesHolidayRepository extends CustomJpaRepository<FuturesHoliday, Long> {

	List<FuturesHoliday> findByCommodity(FuturesCommodity commodity);

	@Query("select f from FuturesHoliday f where f.commodity.id = ?1")
	List<FuturesHoliday> findByCommodityId(Long commodityId);

}
