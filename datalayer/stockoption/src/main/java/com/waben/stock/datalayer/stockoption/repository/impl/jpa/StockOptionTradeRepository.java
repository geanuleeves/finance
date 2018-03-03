package com.waben.stock.datalayer.stockoption.repository.impl.jpa;

import com.waben.stock.datalayer.stockoption.entity.StockOptionTrade;
import org.springframework.data.jpa.repository.Query;

public interface StockOptionTradeRepository extends CustomJpaRepository<StockOptionTrade,Long>{
    @Query("select s from StockOptionTrade s where b.offline_trade=?1")
    StockOptionTrade findByOfflineTrade(Long offlineTrade);
}
