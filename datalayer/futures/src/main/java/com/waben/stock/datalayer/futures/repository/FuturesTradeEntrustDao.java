package com.waben.stock.datalayer.futures.repository;

import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import com.waben.stock.interfaces.enums.FuturesTradeEntrustState;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * 交易委托
 * 
 * @author chenk 2018/7/26
 */
public interface FuturesTradeEntrustDao extends BaseDao<FuturesTradeEntrust, Long> {

	/**
	 * 判断合约是否在订单中使用
	 *
	 * @param contractId
	 * @return
	 */
	List<FuturesTradeEntrust> findByPublisherId(@PathVariable("contractId") Long contractId);

	List<FuturesTradeEntrust> retrieveByBackhandEntrustId(Long entrustId);

    List<FuturesTradeEntrust> retrieveByTradeActionTypeAndState(FuturesTradeActionType tradeActionType, FuturesTradeEntrustState state);

}
