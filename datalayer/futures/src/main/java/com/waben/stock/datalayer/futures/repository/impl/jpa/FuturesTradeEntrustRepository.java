package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;

import java.util.List;

/**
 * 交易委托
 * @author chenk 2018/7/26
 */
public interface FuturesTradeEntrustRepository extends CustomJpaRepository<FuturesTradeEntrust, Long>  {

    List<FuturesTradeEntrust> findByPublisherId(Long publisherId);

}
