package com.waben.stock.datalayer.futures.repository;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;

import java.util.List;

/**
 * 订单交易开平仓记录
 * 
 * @author chenk 2018/7/26
 */
public interface FuturesTradeActionDao extends BaseDao<FuturesTradeAction, Long> {

	List<FuturesTradeAction> retrieveByTradeEntrust(FuturesTradeEntrust tradeEntrust);

	List<FuturesTradeAction> retrieveByTradeEntrustAndTradeActionType(FuturesTradeEntrust tradeEntrust,
			FuturesTradeActionType tradeActionType);

	/**
	 * 查询今持仓
	 * @param publisherId 用户ID
	 * @param commodityNo 品种编号
	 * @param contractNo 合约编号
	 * @param tradeActionType 交易开平仓类型
	 * @return
	 */
	Integer findFilledNow(Long publisherId, String commodityNo, String contractNo, String tradeActionType);

}
