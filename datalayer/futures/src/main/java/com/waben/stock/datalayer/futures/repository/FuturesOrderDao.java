package com.waben.stock.datalayer.futures.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;

import feign.Param;

/**
 * 期货订单 Dao
 * 
 * @author sunl
 *
 */
public interface FuturesOrderDao extends BaseDao<FuturesOrder, Long> {

	Integer countOrderByType(Long contractId, FuturesOrderType orderType);

	/**
	 * 根据合约ID和用户ID获取用户购买该合约总数
	 * 
	 * @param contractId
	 *            合约ID
	 * @param publisherId
	 *            用户ID
	 * @return 合约总数
	 */
	Integer sumByListOrderContractIdAndPublisherId(Long contractId, Long publisherId, Integer type);

	/**
	 * 判断合约期限是否在订单中使用
	 * 
	 * @param contractTermId
	 * @return
	 */
	List<FuturesOrder> findByContractTermId(@Param("contractTermId") List<Long> contractTermId);

	/**
	 * 判断合约是否在订单中使用
	 * 
	 * @param contractId
	 * @return
	 */
	List<FuturesOrder> findByContractId(@PathVariable("contractId") List<Long> contractId);

	/**
	 * 获取持仓中列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 持仓中列表
	 */
	List<FuturesOrder> getListFuturesOrderPositionByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取持仓中总收益
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 持仓中总收益
	 */
	BigDecimal settlementOrderPositionByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取委托中列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 委托中列表
	 */
	List<FuturesOrder> getListFuturesOrderEntrustByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取委托中总收益
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 委托中总收益
	 */
	BigDecimal settlementOrderEntrustByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取已结算列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 已结算列表
	 */
	List<FuturesOrder> getListFuturesOrderUnwindByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 获取已结算总收益
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 已结算总收益
	 */
	BigDecimal settlementOrderUnwindByPublisherId(@PathVariable("publisherId") Long publisherId);

	/**
	 * 根据状态获取用户订单列表
	 * 
	 * @param publisherId
	 *            用户ID
	 * @param state
	 *            订单状态
	 * @return 订单列表
	 */
	List<FuturesOrder> retrieveByPublisherIdAndState(Long publisherId, FuturesOrderState state);

	/**
	 * 根据反手源订单ID获取订单
	 * 
	 * @param backhandSourceOrderId
	 *            反手源订单ID
	 * @return 订单
	 */
	List<FuturesOrder> retrieveByBackhandSourceOrderId(Long backhandSourceOrderId);

	/**
	 * 获取总数
	 * 
	 * @param state
	 * @return
	 */
	List<Object> queryByState(@PathVariable("state") List<Integer> state);

	/**
	 * 根据订单ID和用户ID获取订单信息
	 * 
	 * @param orderId
	 *            订单ID
	 * @param publisherId
	 *            用户ID
	 * @return 订单信息
	 */
	FuturesOrder retrieveByOrderIdAndPublisherId(Long orderId, Long publisherId);

	/**
	 * 根据用户ID获取未完成订单条数
	 * 
	 * @param publisherId
	 *            用户ID
	 * @return 未完成订单条数
	 */
	Integer countByPublisherId(Long publisherId);

	/**
	 * 已成交部分均价
	 * 
	 * @param publisherId
	 *            用户ID
	 * @param contractNo
	 *            合约编号
	 * @param commodityNo
	 *            产品编号
	 * @return 已成交部分均价
	 */
	BigDecimal getAvgFillPrice(Long publisherId, String contractNo, String commodityNo, String orderType);

	/**
	 * 根据合约订单获取订单列表
	 * 
	 * @param contractOrder
	 *            合约订单
	 * @return 订单列表
	 */
	List<FuturesOrder> retrieveByContractOrder(FuturesContractOrder contractOrder);

}
