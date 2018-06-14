package com.waben.stock.futuresgateway.yisheng.esapi;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.future.api.es.external.common.bean.TapAPICommodity;
import com.future.api.es.external.common.bean.TapAPIContract;
import com.future.api.es.external.common.constants.Constants;
import com.future.api.es.external.trade.bean.TapAPINewOrder;
import com.future.api.es.external.trade.bean.TapAPIOrderCancelReq;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesCommodity;
import com.waben.stock.futuresgateway.yisheng.entity.FuturesContract;
import com.waben.stock.futuresgateway.yisheng.exception.ExceptionEnum;
import com.waben.stock.futuresgateway.yisheng.exception.ServiceException;

@Component
public class EsEngine {

	@Autowired
	private EsQuoteWrapper quoteWrapper;

	@Autowired
	private EsTradeWrapper tradeWrapper;

	public int qryContract(TapAPICommodity commodity) {
		return quoteWrapper.getApi().qryContract(commodity);
	}

	public int subscribeQuote(TapAPIContract contract) {
		return quoteWrapper.getApi().subscribeQuote(contract);
	}

	public String getAccount() {
		return tradeWrapper.getTradeUsername();
	}

	public int placeOrder(FuturesCommodity commodity, FuturesContract contract, String action, BigDecimal totalQuantity,
			Integer orderType, BigDecimal entrustPrice) {
		TapAPINewOrder order = new TapAPINewOrder();
		order.setAccountNo(tradeWrapper.getTradeUsername());
		// TODO 测试账号先使用纽交所
		if ("TAIFEX".equals(commodity.getExchangeNo())) {
			order.setExchangeNo("COMEX");
		} else {
			order.setExchangeNo(commodity.getExchangeNo());
		}
		order.setCommodityNo(commodity.getCommodityNo());
		order.setCommodityType(commodity.getCommodityType().charAt(0));
		order.setContractNo(contract.getContractNo());
		if (orderType != null && orderType == 1) {
			order.setOrderType(Constants.TAPI_ORDER_TYPE_MARKET);
		} else if (orderType != null && orderType == 2) {
			order.setOrderType(Constants.TAPI_ORDER_TYPE_LIMIT);
			order.setOrderPrice(entrustPrice.doubleValue());
		} else {
			throw new ServiceException(ExceptionEnum.OrderType_NotSupported);
		}
		order.setOrderQty(totalQuantity.intValue());
		if (!("BUY".equals(action) || "SELL".equals(action))) {
			throw new ServiceException(ExceptionEnum.Action_NotSupported);
		}
		if ("BUY".equals(action)) {
			order.setOrderSide(Constants.TAPI_SIDE_BUY);
		} else {
			order.setOrderSide(Constants.TAPI_SIDE_SELL);
		}
		return tradeWrapper.getApi().insertOrder(order);
	}

	public int cancelOrder(String orderNo) {
		TapAPIOrderCancelReq req = new TapAPIOrderCancelReq();
		req.setOrderNo(orderNo);
		return tradeWrapper.getApi().cancelOrder(req);
	}

}
