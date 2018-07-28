package com.waben.stock.datalayer.futures.repository;

import java.util.List;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;

/**
 * 订单交易开平仓记录
 * 
 * @author chenk 2018/7/26
 */
public interface FuturesTradeActionDao extends BaseDao<FuturesTradeAction, Long> {

	List<FuturesTradeAction> retrieveByTradeEntrust(FuturesTradeEntrust tradeEntrust);

	List<FuturesTradeAction> retrieveByTradeEntrustAndTradeActionType(FuturesTradeEntrust tradeEntrust,
			FuturesTradeActionType tradeActionType);

}
