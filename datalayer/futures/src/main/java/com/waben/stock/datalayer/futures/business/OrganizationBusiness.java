package com.waben.stock.datalayer.futures.business;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.organization.FuturesAgentPriceDto;
import com.waben.stock.interfaces.exception.NetflixCircuitException;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;
import com.waben.stock.interfaces.service.organization.OrganizationSettlementInterface;

@Service
public class OrganizationBusiness {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface organizationInterface;

	@Autowired
	@Qualifier("organizationSettlementInterface")
	private OrganizationSettlementInterface orgSettleInterface;

	/**
	 * 获取用户的期货代理商价格数据
	 * 
	 * @param publisherId
	 *            发布人ID
	 * @param commodityId
	 *            品种ID
	 * @return 期货代理价格
	 */
	public FuturesAgentPriceDto getCurrentAgentPrice(Long publisherId, Long commodityId) {
		Response<FuturesAgentPriceDto> response = organizationInterface.getCurrentAgentPrice(publisherId, commodityId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public void futuresSettlement(Long publisherId, Long commodityId, Long futuresOrderId, String tradeNo,
			BigDecimal totalQuantity, BigDecimal openingFee, BigDecimal closeFee) {
		try {
			Response<String> response = orgSettleInterface.futuresSettlement(publisherId, commodityId, futuresOrderId,
					tradeNo, totalQuantity, openingFee, closeFee);
			String code = response.getCode();
			if ("200".equals(code)) {
				return;
			} else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
				throw new NetflixCircuitException(code);
			}
			throw new ServiceException(response.getCode());
		} catch (Exception ex) {
			logger.error("调用机构期货订单结算发生异常!{}_{}_{}", publisherId, futuresOrderId, tradeNo);
		}
	}

	public void futuresDeferredSettlement(Long publisherId, Long commodityId, Long overnightRecordId, String tradeNo,
			BigDecimal totalQuantity, BigDecimal deferredFee) {
		try {
			Response<String> response = orgSettleInterface.futuresDeferredSettlement(publisherId, commodityId,
					overnightRecordId, tradeNo, totalQuantity, deferredFee);
			String code = response.getCode();
			if ("200".equals(code)) {
				return;
			} else if (ExceptionConstant.NETFLIX_CIRCUIT_EXCEPTION.equals(code)) {
				throw new NetflixCircuitException(code);
			}
			throw new ServiceException(response.getCode());
		} catch (Exception ex) {
			logger.error("调用机构期货订单隔夜递延发生异常!{}_{}_{}", publisherId, overnightRecordId, tradeNo);
		}
	}

}
