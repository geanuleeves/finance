package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 订单交易开平仓记录
 * 
 * @author chenk 2018/7/26
 */
public interface FuturesTradeActionRepository extends CustomJpaRepository<FuturesTradeAction, Long> {

	List<FuturesTradeAction> findByOrder(FuturesOrder order);

	List<FuturesTradeAction> findByTradeEntrust(FuturesTradeEntrust tradeEntrust, Sort sort);

	List<FuturesTradeAction> findByTradeEntrustAndTradeActionType(FuturesTradeEntrust tradeEntrust,
			FuturesTradeActionType tradeActionType, Sort sort);

	/**
	 * 查询今持仓
	 * 
	 * @param publisherId
	 *            用户ID
	 * @param commodityNo
	 *            品种编号
	 * @param contractNo
	 *            合约编号
	 * @return
	 */
	@Query(value = "SELECT sum(o.open_filled - o.close_filled) FROM f_futures_order o " +
			"WHERE o.publisher_id = ?1 AND DATEDIFF(o.open_trade_time,NOW())=0 " +
			"AND o.commodity_symbol = ?2 AND o.contract_no = ?3 " +
			"AND o.order_type = ?4 ", nativeQuery = true)
	Integer findFilledNow(Long publisherId, String commodityNo, String contractNo, String orderType);

}
