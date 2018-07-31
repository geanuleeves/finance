package com.waben.stock.applayer.admin.business.futures;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.business.ProfileBusiness;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderMarketDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
import com.waben.stock.interfaces.dto.futures.FuturesStopLossOrProfitDto;
import com.waben.stock.interfaces.dto.futures.TurnoverStatistyRecordDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesCommodityInterface;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.futures.FuturesOrderInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

@Service
public class FuturesTradeBusiness {


	@Autowired
	@Qualifier("futuresOrderInterface")
	private FuturesOrderInterface futuresOrderInterface;

	@Autowired
	@Qualifier("futuresCurrencyRateInterface")
	private FuturesCurrencyRateInterface futuresCurrencyRateInterface;

	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface futuresContractInterface;

	@Autowired
	@Qualifier("publisherInterface")
	private PublisherInterface publisherInterface;

	@Autowired
	@Qualifier("futuresCommodityInterface")
	private FuturesCommodityInterface futuresCommodityInterface;
	
	@Autowired
	private ProfileBusiness profileBusiness;

	public Integer sumUserNum(Long contractId, Long publisherId, Integer type) {
		Response<Integer> response = futuresOrderInterface.sumByListOrderContractIdAndPublisherId(contractId,
				publisherId, type);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesOrderDto> pageOrder(FuturesOrderQuery orderQuery) {
		Response<PageInfo<FuturesOrderDto>> response = futuresOrderInterface.pagesOrder(orderQuery);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesOrderMarketDto> pageOrderMarket(FuturesOrderQuery orderQuery) {
		PageInfo<FuturesOrderDto> pageOrder = pageOrder(orderQuery);
		List<FuturesOrderMarketDto> orderMarketList = CopyBeanUtils.copyListBeanPropertiesToList(pageOrder.getContent(),
				FuturesOrderMarketDto.class);
		orderMarketList = getListFuturesOrders(orderMarketList);
		return new PageInfo<>(orderMarketList, pageOrder.getTotalPages(), pageOrder.getLast(),
				pageOrder.getTotalElements(), pageOrder.getSize(), pageOrder.getNumber(), pageOrder.getFrist());
	}

	public FuturesCurrencyRateDto findByCurrency(String currency) {
		Response<FuturesCurrencyRateDto> response = futuresCurrencyRateInterface.findByCurrency(currency);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesContractDto findByContractId(Long contractId) {
		Response<FuturesContractDto> response = futuresContractInterface.findByContractId(contractId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public String getQuoteCacheKey(String commodityNo, String contractNo) {
		return commodityNo + "-" + contractNo;
	}

	/**
	 * 计算订单止损止盈及用户盈亏
	 * 
	 * @param orderList
	 *            订单数据
	 * @return 订单列表
	 */
	public List<FuturesOrderMarketDto> getListFuturesOrders(List<FuturesOrderMarketDto> orderList) {
		// 封装合约
		Map<Long, FuturesContractDto> contractMap = new HashMap<Long, FuturesContractDto>();
		List<FuturesContractDto> contractList = getListContract();
		for (FuturesContractDto futuresContractDto : contractList) {
			contractMap.put(futuresContractDto.getId(), futuresContractDto);
		}
		// 封装汇率
		Map<String, FuturesCurrencyRateDto> rateMap = new HashMap<String, FuturesCurrencyRateDto>();
		List<FuturesCurrencyRateDto> rateList = getListCurrencyRate();
		for (FuturesCurrencyRateDto futuresCurrencyRateDto : rateList) {
			rateMap.put(futuresCurrencyRateDto.getCurrency(), futuresCurrencyRateDto);
		}
		// 封装行情
		Map<String, FuturesContractMarket> marketMap = RetriveFuturesOverHttp.marketAll(profileBusiness.isProd());

		if (orderList != null && orderList.size() > 0) {
			for (FuturesOrderMarketDto orderMarket : orderList) {
				FuturesCurrencyRateDto rate = rateMap.get(orderMarket.getCommodityCurrency());
				if (rate == null) {
					break;
				}
				FuturesContractDto contract = contractMap.get(orderMarket.getContractId());
				if (contract == null) {
					break;
				}
				FuturesContractMarket market = marketMap
						.get(getQuoteCacheKey(orderMarket.getCommoditySymbol(), orderMarket.getContractNo()));
				if (market == null) {
					break;
				}
				if (orderMarket.getOrderType() == FuturesOrderType.BuyUp) {
					orderMarket.setBuyOrderTypeDesc(
							"买涨" + Integer.valueOf(orderMarket.getTotalQuantity().intValue()) + "手");
				} else {
					orderMarket.setBuyOrderTypeDesc(
							"买跌" + Integer.valueOf(orderMarket.getTotalQuantity().intValue()) + "手");
				}

				orderMarket.setRate(rate.getRate());
				orderMarket.setCurrencySign(rate.getCurrencySign());
				orderMarket.setPerWaveMoney(contract.getPerWaveMoney());
				orderMarket.setMinWave(contract.getMinWave());

				orderMarket.setLastPrice(market.getLastPrice());
				// 订单结算状态为 已取消或委托失败时 不计算用户盈亏
//				if (orderMarket.getState() != FuturesOrderState.BuyingCanceled
//						&& orderMarket.getState() != FuturesOrderState.BuyingFailure) {
//					if (orderMarket.getPublisherProfitOrLoss() == null && orderMarket.getBuyingPrice() != null) {
//						// 用户买涨盈亏 = （最新价 - 买入价） / 最小波动点 * 波动一次盈亏金额 * 汇率 *手数
//						if (orderMarket.getOrderType() == FuturesOrderType.BuyUp) {
//							orderMarket.setPublisherProfitOrLoss(market.getLastPrice()
//									.subtract(orderMarket.getBuyingPrice())
//									.divide(contract.getMinWave() == null ? BigDecimal.ZERO : contract.getMinWave())
//									.multiply(contract.getPerWaveMoney()).multiply(rate.getRate())
//									.multiply(orderMarket.getTotalQuantity()));
//						} else {
//							// 用户买跌盈亏 = （买入价 - 最新价） / 最小波动点 * 波动一次盈亏金额 * 汇率 *手数
//							orderMarket.setPublisherProfitOrLoss(orderMarket.getBuyingPrice()
//									.subtract(market.getLastPrice())
//									.divide(contract.getMinWave() == null ? BigDecimal.ZERO : contract.getMinWave())
//									.multiply(contract.getPerWaveMoney()).multiply(rate.getRate())
//									.multiply(orderMarket.getTotalQuantity()));
//						}
//					}
//				}
			}
		}
		return orderList;
	}

	public Integer settingStopLoss(Long orderId, Integer limitProfitType, BigDecimal perUnitLimitProfitAmount,
			Integer limitLossType, BigDecimal perUnitLimitLossAmount, Long publisherId, Long stopLossOrProfitId) {
		Response<FuturesOrderDto> response = futuresOrderInterface.settingStopLoss(orderId, limitProfitType,
				perUnitLimitProfitAmount, limitLossType, perUnitLimitLossAmount, publisherId, stopLossOrProfitId);
		if ("200".equals(response.getCode())) {
			return 1;
		}
		throw new ServiceException(response.getCode());
	}

	public TurnoverStatistyRecordDto getTurnoverStatistyRecord(Long publisherId) {
		Response<TurnoverStatistyRecordDto> response = futuresOrderInterface.getTurnoverStatisty(publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}


	public PublisherDto fetchById(Long id) {
		Response<PublisherDto> response = publisherInterface.fetchById(id);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesContractDto getFuturesByContractId(Long contractId) {
		Response<FuturesContractDto> response = futuresContractInterface.findByContractId(contractId);
		if (response.getCode().equals("200")) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesOrderDto fetchByOrderId(Long orderId) {
		Response<FuturesOrderDto> response = futuresOrderInterface.fetchById(orderId);
		if (response.getCode().equals("200")) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BigDecimal getUnsettledProfitOrLoss(Long publisherId) {
		Response<BigDecimal> response = futuresOrderInterface.getUnsettledProfitOrLoss(publisherId);
		if (response.getCode().equals("200")) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}


	public FuturesStopLossOrProfitDto getLossOrProfitsById(Long id) {
		Response<FuturesStopLossOrProfitDto> response = futuresCommodityInterface.getLossOrProfitsById(id);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public List<FuturesContractDto> getListContract() {
		FuturesContractQuery query = new FuturesContractQuery();
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		Response<PageInfo<FuturesContractDto>> response = futuresContractInterface.pagesContract(query);
		if ("200".equals(response.getCode())) {
			return response.getResult().getContent();
		}
		throw new ServiceException(response.getCode());
	}

	public List<FuturesCurrencyRateDto> getListCurrencyRate() {
		Response<PageInfo<FuturesCurrencyRateDto>> response = futuresCurrencyRateInterface.list();
		if ("200".equals(response.getCode())) {
			return response.getResult().getContent();
		}
		throw new ServiceException(response.getCode());
	}
}
