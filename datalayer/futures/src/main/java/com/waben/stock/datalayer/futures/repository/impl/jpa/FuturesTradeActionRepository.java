package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.interfaces.enums.FuturesTradeActionType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 订单交易开平仓记录
 * @author chenk 2018/7/26
 */
public interface FuturesTradeActionRepository extends CustomJpaRepository<FuturesTradeAction, Long>  {

	List<FuturesTradeAction> findByTradeEntrust(FuturesTradeEntrust tradeEntrust, Sort sort);

	List<FuturesTradeAction> findByTradeEntrustAndTradeActionType(FuturesTradeEntrust tradeEntrust,
			FuturesTradeActionType tradeActionType, Sort sort);


	/**
	 * 查询今持仓
	 * @param publisherId 用户ID
	 * @param commodityNo 品种编号
	 * @param contractNo 合约编号
	 * @param tradeActionType 交易开平仓类型
	 * @return
	 */
	@Query(value = "SELECT sum(actions.filled) FROM f_futures_trade_action actions " +
			"LEFT JOIN f_futures_trade_entrust entrust ON entrust.id = trade_entrust_id " +
			"WHERE actions.publisherId = ?1 AND entrust.commodityNo = ?2 " +
			"AND entrust.contractNo = ?3 and DATEDIFF(actions.tradeTime,NOW())=0 " +
			"AND actions.tradeActionType = ?6 AND (actions.status = '4' OR actions.status = '5') " +
			"ORDER BY sort ASC ", nativeQuery = true)
	Integer findFilledNow(Long publisherId, String commodityNo, String contractNo, String tradeActionType);






}
