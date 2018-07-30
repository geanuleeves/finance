package com.waben.stock.datalayer.futures.repository.impl.jpa;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

/**
 * 期货订单 Repository
 * 
 * @author sunl
 *
 */
public interface FuturesOrderRepository extends CustomJpaRepository<FuturesOrder, Long> {

	@Query(value = "SELECT count(*) FROM f_futures_order o LEFT JOIN f_futures_contract c ON o.contract_id = c.id  where c.id=?1 and o.order_type = ?2", nativeQuery = true)
	Integer countOrderByType(Long contractId, FuturesOrderType orderType);

	@Query(value = "SELECT SUM(t1.total_quantity) AS user_num FROM  f_futures_order t1  where t1.state in(2, 4, 5, 6, 7, 8) AND t1.publisher_id= ?2 AND t1.contract_id = ?1 AND t1.order_type =?3", nativeQuery = true)
	Integer sumByListOrderContractIdAndPublisherId(Long contractId, Long publisherId, Integer type);

	@Query(value = "select * from f_futures_order where contract_term_id in ?1 and state != '8'", nativeQuery = true)
	List<FuturesOrder> findByContractTermId(@PathVariable("contractTermId") List<Long> contractTermId);

	@Query(value = "select * from f_futures_order where contract_id in ?1 and state != '8'", nativeQuery = true)
	List<FuturesOrder> findByContractId(@PathVariable("contractId") List<Long> contractId);

	@Query(value = "select COUNT(f1.total_quantity) as quantity,count(f1.reserve_fund) as fund,count(f1.service_fee) as fee,(f2.overnight_deferred_fee) as deferred from f_futures_order f1 "
			+ " LEFT JOIN f_futures_overnight_record f2 ON f1.id = f2.order_id "
			+ " where f1.state in ?1", nativeQuery = true)
	List<Object> queryByState(@PathVariable("state") List<Integer> state);

	/**
	 * 获取持仓中列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 持仓中列表
	 */
	@Query(value = "select * from f_futures_order f where f.publisher_id = ?1 and f.state = 5", nativeQuery = true)
	List<FuturesOrder> getListFuturesOrderPositionByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取持仓中总收益
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 持仓中总收益
	 */
	@Query(value = "select IF(sum(f.publisher_profit_or_loss) IS NULL,0,sum(f.publisher_profit_or_loss)) AS num from f_futures_order f where f.publisher_id = ?1 and f.state = 5", nativeQuery = true)
	BigDecimal settlementOrderPositionByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取委托中列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 委托中列表
	 */
	@Query(value = "select * from f_futures_order f where f.publisher_id = ?1 and f.state in( 3,4,6,7 )", nativeQuery = true)
	List<FuturesOrder> getListFuturesOrderEntrustByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取委托中总收益
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 委托中总收益
	 */
	@Query(value = "select IF(sum(f.publisher_profit_or_loss) IS NULL,0,sum(f.publisher_profit_or_loss)) AS num from f_futures_order f where f.publisher_id = ?1 and f.state in( 3,4,6,7 )", nativeQuery = true)
	BigDecimal settlementOrderEntrustByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取已结算列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 已结算列表
	 */
	@Query(value = "select * from f_futures_order f where f.publisher_id = ?1 and f.state = 8", nativeQuery = true)
	List<FuturesOrder> getListFuturesOrderUnwindByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取已结算总收益
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 已结算总收益
	 */
	@Query(value = "select IF(sum(f.publisher_profit_or_loss) IS NULL,0,sum(f.publisher_profit_or_loss)) AS num from f_futures_order f where f.publisher_id = ?1 and f.state = 8", nativeQuery = true)
	BigDecimal settlementOrderUnwindByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 根据状态获取用户订单列表
	 * 
	 * @param state
	 *            订单状态
	 * @return 订单列表
	 */
	List<FuturesOrder> findByPublisherIdAndState(Long publisherId, FuturesOrderState state);

	FuturesOrder findByIdAndPublisherId(Long orderId, Long publisherId);

	@Query(value = "select count(*) from f_futures_order t where t.state in(1,2,5,6,7,8) and t.publisher_id = ?1", nativeQuery = true)
	Integer countByPublisherId(Long publisherId);

	/**
	 * 已成交部分均价（开仓）
	 * @param publisherId 用户ID
	 * @param contractNo 合约编号
	 * @param commodityNo 产品编号
	 * @return
	 */
	@Query(value = "SELECT sum(openTotalFillCost)/sum(openFilled)  FROM f_futures_order t " +
			"LEFT JOIN f_futures_contract contract ON contract.id = t.contract_id " +
			"LEFT JOIN f_futures_commodity commodity ON commodity.id = contract.commodity_id " +
			"WHERE t.publisherId = ?1 AND contract.contractNo = ?2 AND commodity.symbol = ?3 " +
			"AND t.orderType = ?4", nativeQuery = true)
	BigDecimal getAvgFillPrice(Long publisherId, String contractNo, String commodityNo, String orderType);

	List<FuturesOrder> findByContractOrder(FuturesContractOrder contractOrder, Sort sort);

}
