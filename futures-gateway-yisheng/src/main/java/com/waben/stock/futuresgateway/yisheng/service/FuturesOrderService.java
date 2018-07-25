package com.waben.stock.futuresgateway.yisheng.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.futuresgateway.yisheng.dao.FuturesCommodityDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesContractDao;
import com.waben.stock.futuresgateway.yisheng.dao.FuturesOrderDao;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesOrder;
import com.waben.stock.futuresgateway.yisheng.esapi.EsEngine;
import com.waben.stock.futuresgateway.yisheng.exception.ExceptionEnum;
import com.waben.stock.futuresgateway.yisheng.exception.ServiceException;

/**
 * 期货订单 Service
 * 
 * @author lma
 *
 */
@Service
public class FuturesOrderService {

	@Autowired
	private FuturesCommodityDao commodityDao;

	@Autowired
	private FuturesContractDao futuresContractDao;

	@Autowired
	private FuturesOrderDao futuresOrderDao;

	@Autowired
	private EsEngine esEngine;

	public FuturesOrder getFuturesOrderInfo(Long id) {
		FuturesOrder order = futuresOrderDao.retrieveFuturesOrderById(id);
		return order;
	}

	@Transactional
	public FuturesOrder modifyFuturesOrder(FuturesOrder futuresOrder) {
		return futuresOrderDao.updateFuturesOrder(futuresOrder);
	}

	@Transactional
	public void deleteFuturesOrder(Long id) {
		futuresOrderDao.deleteFuturesOrderById(id);
	}

	@Transactional
	public void deleteFuturesOrders(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					futuresOrderDao.deleteFuturesOrderById(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<FuturesOrder> futuresOrders(int page, int limit) {
		return futuresOrderDao.pageFuturesOrder(page, limit);
	}

	public List<FuturesOrder> list() {
		return futuresOrderDao.listFuturesOrder();
	}

	/**
	 * 检查API连接是否已连接
	 * 
	 * @return 是否已连接
	 */
	public boolean isConnected() {
		return true;
	}

	/**
	 * 取消订单
	 * 
	 * @param domain
	 *            应用域
	 * @param outerOrderId
	 *            外部订单ID
	 */
	@Transactional
	public FuturesOrder cancelOrder(String domain, Long id) {
		if (!isConnected()) {
			throw new ServiceException(ExceptionEnum.Client_NotConnected);
		}
		FuturesOrder order = futuresOrderDao.retrieveFuturesOrderById(id);
		if (order == null) {
			throw new ServiceException(ExceptionEnum.Order_NotExist);
		}
		if (order.getOrderState() == 4) {
			esEngine.cancelOrder(order.getOrderNo());
		} else if (order.getOrderState() == 5) {
			throw new ServiceException(ExceptionEnum.PartFilled_CannotCancel);
		} else {
			throw new ServiceException(ExceptionEnum.CurrentStatus_CannotCancel);
		}
		return order;
	}

	/**
	 * 下单
	 * 
	 * @param domain
	 *            应用域
	 * @param commodityNo
	 *            品种编号
	 * @param contractNo
	 *            合约编号
	 * @param outerOrderId
	 *            外部订单ID
	 * @param action
	 *            交易方向
	 * @param totalQuantity
	 *            交易总量
	 * @param orderType
	 *            订单类型
	 * @param entrustPrice
	 *            委托价格
	 * @return 订单
	 */
	@Transactional
	public synchronized FuturesOrder placeOrder(String domain, String commodityNo, String contractNo, Long outerOrderId,
			String action, BigDecimal totalQuantity, Integer orderType, BigDecimal entrustPrice) {
		if (!isConnected()) {
			throw new ServiceException(ExceptionEnum.Client_NotConnected);
		}
		FuturesCommodity commodity = commodityDao.retrieveByCommodityNo(commodityNo);
		FuturesContract contract = futuresContractDao.retrieveByCommodityNoAndContractNo(commodityNo, contractNo);
		if (commodity == null || contract == null || commodity.getEnable() == null || !commodity.getEnable()
				|| contract.getEnable() == null || !contract.getEnable()) {
			throw new ServiceException(ExceptionEnum.Contract_NotSupported);
		}
		if (!("BUY".equals(action) || "SELL".equals(action))) {
			throw new ServiceException(ExceptionEnum.Action_NotSupported);
		}
		if (!((orderType != null && orderType == 1) || (orderType != null && orderType == 2))) {
			throw new ServiceException(ExceptionEnum.OrderType_NotSupported);
		}
		// step 1 : 初始化部分订单信息
		FuturesOrder order = new FuturesOrder();
		order.setAccount(esEngine.getAccount());
		order.setAction(action);
		order.setCommodityNo(commodityNo);
		order.setContractNo(contractNo);
		order.setCreateTime(new Date());
		order.setDomain(domain);
		order.setEntrustPrice(entrustPrice);
		order.setOrderState(0);
		order.setOrderType(orderType);
		order.setOuterOrderId(outerOrderId);
		order.setBrokerType(1);
		order.setTotalQuantity(totalQuantity);
		order.setUpdateTime(new Date());
		order = futuresOrderDao.createFuturesOrder(order);
		// step 2 : 请求上游下单
		int orderSessionId = esEngine.placeOrder(commodity, contract, action, totalQuantity, orderType, entrustPrice);
		order.setOrderSessionId(orderSessionId);
		order.setUpdateTime(new Date());
		return futuresOrderDao.updateFuturesOrder(order);
	}

	public FuturesOrder getByYtTwsOrderId(int ytTwsOrderId) {
		return futuresOrderDao.retriveByYtTwsOrderId(ytTwsOrderId);
	}

}
