package com.waben.stock.datalayer.buyrecord.repository;

import java.util.List;

import com.waben.stock.datalayer.buyrecord.entity.BuyRecord;
import com.waben.stock.datalayer.buyrecord.entity.Settlement;

/**
 * 结算 Dao
 * 
 * @author luomengan
 *
 */
public interface SettlementDao extends BaseDao<Settlement, Long> {

    Settlement retrieveByBuyRecord(Long id);
    
    List<Settlement> retrieveByBuyRecord(BuyRecord buyRecord);
    
}
