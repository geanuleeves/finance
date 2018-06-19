package com.waben.stock.applayer.tactics.business.futures;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.tactics.dto.futures.FuturesContractQuotationDto;
import com.waben.stock.applayer.tactics.security.SecurityUtil;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.organization.FuturesAgentPriceDto;
import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.dto.publisher.CapitalAccountDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractQuery;
import com.waben.stock.interfaces.service.futures.FuturesBrokerInterface;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesOrderInterface;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;
import com.waben.stock.interfaces.service.organization.OrganizationPublisherInterface;
import com.waben.stock.interfaces.service.publisher.CapitalAccountInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.StringUtil;

@Service
public class FuturesContractBusiness {

	@Autowired
	@Qualifier("capitalAccountInterface")
	private CapitalAccountInterface service;

	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface futuresContractInterface;

	@Autowired
	@Qualifier("futuresOrderInterface")
	private FuturesOrderInterface futuresOrderInterface;

	@Autowired
	@Qualifier("futuresBrokerInterface")
	private FuturesBrokerInterface futuresBrokerInterface;

	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface organizationInterface;

	@Autowired
	@Qualifier("organizationPublisherInterface")
	private OrganizationPublisherInterface organizationPublisherInterface;

	public CapitalAccountDto findByPublisherId(Long publisherId) {
		Response<CapitalAccountDto> response = service.fetchByPublisherId(publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesContractDto> pagesContract(FuturesContractQuery query) {
		Response<PageInfo<FuturesContractDto>> response = futuresContractInterface.pagesContract(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<FuturesContractQuotationDto> pagesQuotations(List<FuturesContractDto> list) {
		List<FuturesContractQuotationDto> quotationList = CopyBeanUtils.copyListBeanPropertiesToList(list,
				FuturesContractQuotationDto.class);
		if (quotationList.size() > 0) {
			for (FuturesContractQuotationDto quotation : quotationList) {
				FuturesContractMarket market = RetriveFuturesOverHttp.market(quotation.getSymbol(),
						quotation.getContractNo());
				// 设置行情信息
				quotation.setLastPrice(market.getLastPrice());
				quotation.setUpDropPrice(market.getUpDropPrice());
				quotation.setUpDropSpeed(market.getUpDropSpeed());
				quotation.setOpenPrice(market.getOpenPrice());
				quotation.setHighPrice(market.getHighPrice());
				quotation.setLowPrice(market.getLowPrice());
				quotation.setClosePrice(market.getClosePrice());
				quotation.setAskPrice(market.getAskPrice());
				quotation.setAskSize(market.getAskSize());
				quotation.setBidPrice(market.getBidPrice());
				quotation.setBidSize(market.getBidSize());
				quotation.setVolume(market.getVolume());
				quotation.setCurrentHoldingTime(
						timeZoneConversion(quotation.getTimeZoneGap(), quotation.getCurrentHoldingTime()));
				quotation.setNextTradingTime(
						timeZoneConversion(quotation.getTimeZoneGap(), quotation.getNextTradingTime()));
			}
		}
		return quotationList;
	}

	/*
	 * public FuturesBrokerDto findBybrokerId(Long brokerId) {
	 * Response<FuturesBrokerDto> response =
	 * futuresBrokerInterface.findByrokerId(brokerId); if
	 * ("200".equals(response.getCode())) { return response.getResult(); } throw
	 * new ServiceException(response.getCode()); }
	 */

	public FuturesContractDto getContractByOne(FuturesContractQuery query) {
		Response<PageInfo<FuturesContractDto>> response = futuresContractInterface.pagesContract(query);
		if ("200".equals(response.getCode())) {
			FuturesContractDto contractDto = null;
			List<FuturesContractDto> contractList = response.getResult().getContent();
			if (contractList != null && contractList.size() > 0) {
				contractDto = contractList.get(0);
				if (contractDto == null) {
					// 该合约不存在
					throw new ServiceException(ExceptionConstant.CONTRACT_DOESNOT_EXIST_EXCEPTION);
				}
				/*
				 * FuturesBrokerDto brokerDto =
				 * findBybrokerId(contractDto.getGatewayId()); if (brokerDto ==
				 * null || !brokerDto.getEnable()) { // 期货网关不支持该合约 throw new
				 * ServiceException(ExceptionConstant.
				 * GATEWAY_DOESNOT_SUPPORT_CONTRACT_EXCEPTION); }
				 */
				if (contractDto.getExchangeEnable() != null && !contractDto.getExchangeEnable()) {
					// 该合约交易所不可用
					throw new ServiceException(ExceptionConstant.EXCHANGE_ISNOT_AVAILABLE_EXCEPTION);
				}
				if (contractDto.getEnable() != null && !contractDto.getEnable()) {
					// 该合约异常不可用
					throw new ServiceException(ExceptionConstant.CONTRACT_ABNORMALITY_EXCEPTION);
				}
				// 判断该合约是否处于交易中
				if (contractDto.getState() != 1) {
					// 该合约不在交易中
					throw new ServiceException(ExceptionConstant.CONTRACT_ISNOTIN_TRADE_EXCEPTION);
				}

				// OrganizationPublisherDto publisher = fetchOrgPublisher(SecurityUtil.getUserDetails().getUserId());
				// if (publisher != null) {
					// FuturesAgentPriceDto agentPrice =
					// getCurrentAgentPrice(publisher.getOrgId(),
					// contractDto.getId());
					// if (agentPrice != null) {
					// contractDto.setPerUnitReserveFund(agentPrice.getCostReserveFund());
					// contractDto.setOpenwindServiceFee(agentPrice.getSaleOpenwindServiceFee());
					// contractDto.setUnwindServiceFee(agentPrice.getSaleUnwindServiceFee());
					// contractDto.setOvernightPerUnitDeferredFee(agentPrice.getSaleDeferredFee());
					// }
				// }
			}

			return contractDto;
		}
		throw new ServiceException(response.getCode());
	}

	/**
	 * 将时间转成国内时间
	 * 
	 * @param timeZoneGap
	 *            时差
	 * @param time
	 *            国外时间
	 * @return 国内时间
	 */
	private String timeZoneConversion(Integer timeZoneGap, String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d HH:mm");
		String timeStr = "";
		try {
			if (StringUtil.isEmpty(time)) {
				return "";
			}
			timeStr = sdf.format(retriveExchangeTime(sdf.parse(time.toString()), timeZoneGap));
		} catch (ParseException e) {
			return "";
		}

		return timeStr;
	}

	private Date retriveExchangeTime(Date localTime, Integer timeZoneGap) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(localTime);
		cal.add(Calendar.HOUR_OF_DAY, timeZoneGap);
		return cal.getTime();
	}

	/**
	 * 获取当前期货代理价格数据
	 * 
	 * @param orgId
	 *            代理商ID
	 * @param contractId
	 *            合约ID
	 * @return 期货代理价格
	 */
	public FuturesAgentPriceDto getCurrentAgentPrice(Long orgId, Long contractId) {
		Response<FuturesAgentPriceDto> response = organizationInterface.getCurrentAgentPrice(orgId, contractId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	/**
	 * 根据用户获取代理商用户信息
	 * 
	 * @param publisherId
	 *            用户iD
	 * @return 代理商用户
	 */
	public OrganizationPublisherDto fetchOrgPublisher(Long publisherId) {
		Response<OrganizationPublisherDto> response = organizationPublisherInterface.fetchOrgPublisher(publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
