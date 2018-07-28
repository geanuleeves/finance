package com.waben.stock.datalayer.futures.repository.impl.jpa;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;

/**
 * 订单交易开平仓记录
 * @author chenk 2018/7/26
 */
public interface FuturesTradeActionRepository extends CustomJpaRepository<FuturesTradeAction, Long>  {

	List<FuturesTradeAction> findByTradeEntrust(FuturesTradeEntrust tradeEntrust, Sort sort);

	List<FuturesTradeAction> findByTradeEntrustAndTradeActionType(FuturesTradeEntrust tradeEntrust,
			FuturesTradeActionType tradeActionType, Sort sort);
}
