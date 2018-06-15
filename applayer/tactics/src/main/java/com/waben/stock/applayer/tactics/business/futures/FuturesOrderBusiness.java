package com.waben.stock.applayer.tactics.business.futures;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.tactics.business.AnalogDataBusiness;
import com.waben.stock.applayer.tactics.dto.futures.FuturesOrderMarketDto;
import com.waben.stock.applayer.tactics.dto.futures.TransactionDynamicsDto;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.futures.FuturesOrderDto;
import com.waben.stock.interfaces.dto.futures.TurnoverStatistyRecordDto;
import com.waben.stock.interfaces.dto.manage.AnalogDataDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.enums.AnalogDataType;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.enums.FuturesOrderType;
import com.waben.stock.interfaces.enums.FuturesTradePriceType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.futures.FuturesOrderQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.futures.FuturesOrderInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.JacksonUtil;

@Service
public class FuturesOrderBusiness {

	Logger logger = LoggerFactory.getLogger(getClass());

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
	private AnalogDataBusiness analogDataBusiness;

	public Integer sumUserNum(Long contractId, Long publisherId) {
		Response<Integer> response = futuresOrderInterface.sumByListOrderContractIdAndPublisherId(contractId,
				publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesOrderDto applyUnwind(Long orderId, FuturesTradePriceType sellingPriceType,
			BigDecimal sellingEntrustPrice, Long publisherId) {
		Response<FuturesOrderDto> response = futuresOrderInterface.applyUnwind(orderId, sellingPriceType.getIndex(),
				sellingEntrustPrice, publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesOrderDto backhandUnwind(Long orderId, Long publisherId) {
		Response<FuturesOrderDto> response = futuresOrderInterface.backhandUnwind(orderId, publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public void applyUnwindAll(Long publisherId) {
		Response<Void> response = futuresOrderInterface.applyUnwindAll(publisherId);
		if ("200".equals(response.getCode())) {
			return;
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesOrderDto cancelOrder(Long orderId, Long publisherId) {
		Response<FuturesOrderDto> response = futuresOrderInterface.cancelOrder(orderId, publisherId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public FuturesOrderDto buy(FuturesOrderDto orderDto) {
		Response<FuturesOrderDto> response = futuresOrderInterface.addOrder(orderDto);
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

	/**
	 * 计算订单止损止盈及用户盈亏
	 * 
	 * @param orderList
	 *            订单数据
	 * @return 订单列表
	 */
	public List<FuturesOrderMarketDto> getListFuturesOrders(List<FuturesOrderMarketDto> orderList) {
		if (orderList != null && orderList.size() > 0) {
			for (FuturesOrderMarketDto orderMarket : orderList) {
				if (orderMarket.getOrderType() == FuturesOrderType.BuyUp) {
					orderMarket.setBuyOrderTypeDesc(
							"买涨" + Integer.valueOf(orderMarket.getTotalQuantity().intValue()) + "手");
				} else {
					orderMarket.setBuyOrderTypeDesc(
							"买跌" + Integer.valueOf(orderMarket.getTotalQuantity().intValue()) + "手");
				}
				// 获取合约信息
				FuturesContractDto contract = findByContractId(orderMarket.getContractId());
				if (contract == null) {
					break;
				}
				// 获取汇率信息
				FuturesCurrencyRateDto rate = findByCurrency(orderMarket.getCommodityCurrency());
				orderMarket.setRate(rate.getRate());
				orderMarket.setCurrencySign(rate.getCurrencySign());
				// 买入价
				BigDecimal buyingPrice = new BigDecimal(0);
				if (orderMarket.getBuyingPriceType() == FuturesTradePriceType.MKT) {
					buyingPrice = orderMarket.getBuyingPrice() == null ? new BigDecimal(0)
							: orderMarket.getBuyingPrice();
				} else {
					buyingPrice = orderMarket.getBuyingEntrustPrice() == null ? new BigDecimal(0)
							: orderMarket.getBuyingEntrustPrice();
				}
				// 止盈
				if (orderMarket.getLimitProfitType() != null && orderMarket.getPerUnitLimitProfitAmount() != null) {
					// 按用户设置价格计算止盈金额
					if (orderMarket.getLimitProfitType() == 1) {
						// | 止盈金额 = （设置价格 - 买入价）/ 最小波动点位 * 波动一次盈亏金额 * 汇率 |
						orderMarket.setPerUnitLimitProfitPositon(orderMarket.getPerUnitLimitProfitAmount()
								.subtract(buyingPrice).divide(contract.getMinWave())
								.multiply(contract.getPerWaveMoney()).multiply(rate.getRate()).abs());
					} else {
						orderMarket.setPerUnitLimitProfitPositon(orderMarket.getPerUnitLimitProfitAmount());
					}
				}
				// 止损
				if (orderMarket.getLimitLossType() != null && orderMarket.getPerUnitLimitLossAmount() != null) {
					// 按用户设置价格计算止损金额
					if (orderMarket.getLimitLossType() == 1) {
						// 止损金额 = （设置价格 - 买入价）/ 最小波动点位 * 波动一次盈亏金额 * 汇率
						orderMarket.setPerUnitLimitLossPosition(orderMarket.getPerUnitLimitLossAmount()
								.subtract(buyingPrice).divide(contract.getMinWave())
								.multiply(contract.getPerWaveMoney()).multiply(rate.getRate()));
					} else {
						orderMarket.setPerUnitLimitLossPosition(orderMarket.getPerUnitLimitLossAmount());
					}
				}
				// 订单结算状态为 已取消或委托失败时 不计算用户盈亏
				if (orderMarket.getState() != FuturesOrderState.BuyingCanceled
						&& orderMarket.getState() != FuturesOrderState.BuyingFailure) {
					// 获取行情信息
					FuturesContractMarket market = RetriveFuturesOverHttp.market(orderMarket.getCommoditySymbol(),
							orderMarket.getContractNo());
					if (market == null) {
						break;
					}
					orderMarket.setPerWaveMoney(contract.getPerWaveMoney());
					orderMarket.setMinWave(contract.getMinWave());
					orderMarket.setLastPrice(market.getLastPrice());
					// 用户盈亏 = （最新价 - 买入价） / 最小波动点 * 波动一次盈亏金额 * 汇率
					orderMarket.setFloatingProfitOrLoss(
							market.getLastPrice().subtract(buyingPrice).divide(contract.getMinWave())
									.multiply(contract.getPerWaveMoney()).multiply(rate.getRate()));
				}

			}
		}
		return orderList;
	}

	public Integer settingStopLoss(Long orderId, Integer limitProfitType, BigDecimal perUnitLimitProfitAmount,
			Integer limitLossType, BigDecimal perUnitLimitLossAmount, Long publisherId) {
		Response<FuturesOrderDto> response = futuresOrderInterface.settingStopLoss(orderId, limitProfitType,
				perUnitLimitProfitAmount, limitLossType, perUnitLimitLossAmount, publisherId);
		if ("200".equals(response.getCode())) {
			return 1;
		}
		throw new ServiceException(response.getCode());
	}

	public TurnoverStatistyRecordDto getTurnoverStatistyRecord() {
		Response<TurnoverStatistyRecordDto> response = futuresOrderInterface.getTurnoverStatisty();
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<TransactionDynamicsDto> transactionDynamics(int page, int size) {
		// 已平仓订单
		FuturesOrderQuery unwindQuery = new FuturesOrderQuery();
		FuturesOrderState[] unwindStates = { FuturesOrderState.Unwind };
		unwindQuery.setStates(unwindStates);
		unwindQuery.setPage(page);
		unwindQuery.setSize(size / 2);
		PageInfo<FuturesOrderDto> pageUnwindOrder = pageOrder(unwindQuery);

		// 持仓中订单
		FuturesOrderQuery positionQuery = new FuturesOrderQuery();
		FuturesOrderState[] positionStates = { FuturesOrderState.Position };
		positionQuery.setStates(positionStates);
		positionQuery.setPage(page);
		positionQuery.setSize(size - pageUnwindOrder.getContent().size());
		PageInfo<FuturesOrderDto> pagePositionOrder = pageOrder(positionQuery);

		boolean isSettlement = true;
		List<TransactionDynamicsDto> content = new ArrayList<TransactionDynamicsDto>();

		int total = pageUnwindOrder.getContent().size() + pagePositionOrder.getContent().size();
		for (int i = 0; i < total; i++) {
			if (isSettlement && pageUnwindOrder.getContent().size() > 0) {
				FuturesOrderDto unwindOrder = pageUnwindOrder.getContent().remove(0);
				PublisherDto publisher = fetchById(unwindOrder.getPublisherId());
				FuturesContractDto contract = getFuturesByContractId(unwindOrder.getContractId());
				TransactionDynamicsDto unwind = new TransactionDynamicsDto();
				unwind.setPublisherProfitOrLoss(unwindOrder.getPublisherProfitOrLoss());
				unwind.setContractId(unwindOrder.getContractId());
				unwind.setContractNo(unwindOrder.getContractNo());
				unwind.setContractName(unwindOrder.getCommodityName());
				unwind.setContractSymbol(unwindOrder.getCommoditySymbol());
				unwind.setPublisherId(unwindOrder.getPublisherId());
				unwind.setTotalQuantity(unwindOrder.getTotalQuantity());
				unwind.setOrderType(unwindOrder.getOrderType());
				unwind.setPhone(publisher == null ? "" : publisher.getPhone());
				unwind.setState(contract == null ? 3 : contract.getState());
				content.add(unwind);
				isSettlement = false;
			} else {
				if (pagePositionOrder.getContent().size() > 0) {
					FuturesOrderDto positionOrder = pagePositionOrder.getContent().remove(0);
					PublisherDto publisher = fetchById(positionOrder.getPublisherId());
					FuturesContractDto contract = getFuturesByContractId(positionOrder.getContractId());
					TransactionDynamicsDto position = new TransactionDynamicsDto();
					if (position.getOrderType() == FuturesOrderType.BuyUp) {
						position.setBuyOrderTypeDesc("买涨" + Integer.valueOf(positionOrder.getTotalQuantity() == null ? 0
								: positionOrder.getTotalQuantity().intValue()) + "手");
					} else {
						position.setBuyOrderTypeDesc("买跌" + Integer.valueOf(positionOrder.getTotalQuantity() == null ? 0
								: positionOrder.getTotalQuantity().intValue()) + "手");
					}
					position.setContractId(positionOrder.getContractId());
					position.setContractName(positionOrder.getCommodityName());
					position.setContractSymbol(positionOrder.getCommoditySymbol());
					position.setPublisherId(positionOrder.getPublisherId());
					position.setTotalQuantity(positionOrder.getTotalQuantity());
					position.setOrderType(positionOrder.getOrderType());
					position.setPhone(publisher == null ? "" : publisher.getPhone());
					position.setState(contract == null ? 3 : contract.getState());
					content.add(position);
					isSettlement = true;
				} else {
					isSettlement = true;
					total++;
				}
			}
		}
		if (content.size() < size) {
			PageInfo<AnalogDataDto> analogDataPage = analogDataBusiness.pagesByType(AnalogDataType.FUTURESTRADEDYNAMIC,
					0, size - content.size());
			if (analogDataPage.getContent().size() > 0) {
				for (AnalogDataDto analogData : analogDataPage.getContent()) {
					try {
						TransactionDynamicsDto dynamic = JacksonUtil.decode(analogData.getData(),
								TransactionDynamicsDto.class);
						if (content.size() > 0
								&& content.get(content.size() - 1).getOrderType() == FuturesOrderType.BuyUp) {
							dynamic.setOrderType(FuturesOrderType.BuyUp);
							dynamic.setBuyOrderTypeDesc("买涨" + Integer.valueOf(
									dynamic.getTotalQuantity() == null ? 0 : dynamic.getTotalQuantity().intValue())
									+ "手");
						} else {
							dynamic.setOrderType(FuturesOrderType.BuyFall);
							dynamic.setBuyOrderTypeDesc("买跌" + Integer.valueOf(
									dynamic.getTotalQuantity() == null ? 0 : dynamic.getTotalQuantity().intValue())
									+ "手");
						}
						content.add(dynamic);
					} catch (Exception ex) {
						logger.error("期权交易动态模拟数据格式错误?" + analogData.getData());
					}
				}
			}
		}

		return new PageInfo<TransactionDynamicsDto>(content, 0, false, 0L, size, page, false);
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

}
