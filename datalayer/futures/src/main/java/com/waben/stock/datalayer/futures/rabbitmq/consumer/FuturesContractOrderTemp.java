package com.waben.stock.datalayer.futures.rabbitmq.consumer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;

/**
 * 合约订单-临时对象
 * 
 * @author lma
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FuturesContractOrderTemp extends FuturesContractOrder {

	private FuturesContractOrder originOrder;

	public FuturesContractOrder getOriginOrder() {
		return originOrder;
	}

	public void setOriginOrder(FuturesContractOrder originOrder) {
		this.originOrder = originOrder;
	}

}
