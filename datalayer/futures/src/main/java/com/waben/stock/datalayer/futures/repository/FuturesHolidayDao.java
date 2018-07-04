package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;

public interface FuturesHolidayDao extends BaseDao<FuturesHoliday, Long> {

	List<FuturesHoliday> findByCommodity(FuturesCommodity commodity);
}
