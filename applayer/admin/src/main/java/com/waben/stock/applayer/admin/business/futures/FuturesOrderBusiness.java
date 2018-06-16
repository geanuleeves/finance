package com.waben.stock.applayer.admin.business.futures;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.futures.FuturesTradeInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;

/**
 * 期货交易 Business
 * 
 * @author pengzhenliang
 */
@Service
public class FuturesOrderBusiness {

	@Autowired
	@Qualifier("futuresTradeInterface")
	private FuturesTradeInterface reference;

	@Autowired
	private PublisherInterface publisherInterface;

	@Autowired
	private RealNameInterface realnameInterface;

	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface futuresContractInterface;

	@Autowired
	@Qualifier("futuresCurrencyRateInterface")
	private FuturesCurrencyRateInterface futuresCurrencyRateInterface;

	Logger logger = LoggerFactory.getLogger(getClass());

	public Response<FuturesOrderCountDto> countOrderState(FuturesTradeAdminQuery query) {
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		FuturesOrderCountDto dto = new FuturesOrderCountDto();
		if (query.getQueryType() == 0) {
			PageInfo<FuturesOrderAdminDto> result = adminPagesByQuery(query);
			if (result.getContent().size() > 0) {
				double totalQuantity = 0.00;
				double reserveFund = 0.00;
				double serviceFee = 0.00;
				double overnightServiceFee = 0.00;
				for (FuturesOrderAdminDto adminDto : result.getContent()) {
					if (adminDto.getTotalQuantity() != null) {
						totalQuantity += adminDto.getTotalQuantity().doubleValue();
					}
					if (adminDto.getReserveFund() != null) {
						reserveFund += adminDto.getReserveFund().doubleValue();
					}
					if (adminDto.getOpenwindServiceFee() != null) {
						serviceFee += adminDto.getOpenwindServiceFee().doubleValue();
					}
					if (adminDto.getState().equals("已平仓")) {
						serviceFee += adminDto.getUnwindServiceFee().doubleValue();
					}
					if (adminDto.getOvernightServiceFee() != null) {
						overnightServiceFee += adminDto.getOvernightServiceFee().doubleValue();
					}
				}
				dto.setDeferred(new BigDecimal(overnightServiceFee));
				dto.setQuantity(new BigDecimal(totalQuantity));
				dto.setFee(new BigDecimal(serviceFee));
				dto.setFund(new BigDecimal(reserveFund));
			}
		}
		Response<FuturesOrderCountDto> res = new Response<FuturesOrderCountDto>();
		res.setCode("200");
		res.setResult(dto);
		res.setMessage("响应成功");
		return res;
	}

	private List<Long> queryPublishIds(FuturesTradeAdminQuery query) {
		List<Long> publisherIds = new ArrayList<Long>();
		if (query.getPublisherPhone() != null) {
			if (publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult() != null) {
				String publisherId = publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult().getId()
						.toString();
				publisherIds.add(Long.valueOf(publisherId));
			}
			;
		}

		if (query.getPublisherName() != null && !"".equals(query.getPublisherName())) {
			if (publisherIds.size() == 0) {
				List<RealNameDto> real = realnameInterface.findByName(query.getPublisherName()).getResult();
				for (RealNameDto realNameDto : real) {
					publisherIds.add(Long.valueOf(realNameDto.getResourceId().toString()));
				}
			} else {
				publisherIds.clear();
				publisherIds.add(Long.valueOf("-1"));
			}
		}
		return publisherIds;
	}

	public PageInfo<FutresOrderEntrustDto> pagesOrderEntrust(FuturesTradeAdminQuery query) {
		query.setPublisherIds(queryPublishIds(query));
		Response<PageInfo<FutresOrderEntrustDto>> response = reference.pagesOrderEntrust(query);
		if (response.getResult() != null && response.getResult().getContent() != null) {
			if (response.getResult().getContent().size() > 0) {
				for (FutresOrderEntrustDto dto : response.getResult().getContent()) {
					if (dto.getPublisherId() != null) {
						PublisherDto pu = publisherInterface.fetchById(dto.getPublisherId()).getResult();
						if (pu != null) {
							dto.setPublisherPhone(pu.getPhone());
						}
						RealNameDto re = realnameInterface.fetchByResourceId(dto.getPublisherId()).getResult();
						if (re != null) {
							dto.setPublisherName(re.getName());
						}
					}
				}
			}

		}
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesOrderAdminDto> adminPagesByQuery(FuturesTradeAdminQuery query) {
		List<Long> publisherIds = new ArrayList<Long>();
		if (query.getPublisherPhone() != null && !"".equals(query.getPublisherPhone())) {
			if (publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult() != null) {
				String publisherId = publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult().getId()
						.toString();
				if (publisherId != null && !"".equals(publisherId)) {
					publisherIds.add(Long.valueOf(publisherId));
				} else {
					return new PageInfo<FuturesOrderAdminDto>();
				}
			} else {
				return new PageInfo<FuturesOrderAdminDto>();
			}
			;
		} else if (query.getPublisherName() != null && !"".equals(query.getPublisherName())) {
			List<RealNameDto> real = realnameInterface.findByName(query.getPublisherName()).getResult();
			if (real == null || real.size() == 0) {
				return new PageInfo<FuturesOrderAdminDto>();
			} else {
				for (RealNameDto realNameDto : real) {
					publisherIds.add(Long.valueOf(realNameDto.getResourceId().toString()));
				}
			}

		}

		query.setPublisherIds(publisherIds);
		Response<PageInfo<FuturesOrderAdminDto>> response = reference.adminPagesByQuery(query);
		if (response.getResult() != null) {
			for (FuturesOrderAdminDto dto : response.getResult().getContent()) {
				if (dto.getPublisherId() != null) {
					PublisherDto pu = publisherInterface.fetchById(dto.getPublisherId()).getResult();
					if (pu != null) {
						dto.setPublisherPhone(pu.getPhone());
					}
					RealNameDto re = realnameInterface.fetchByResourceId(dto.getPublisherId()).getResult();
					if (re != null) {
						dto.setPublisherName(re.getName());
					}
					// 获取行情信息
					FuturesContractMarket market = RetriveFuturesOverHttp.market(dto.getSymbol(), dto.getContractNo());
					// 获取合约信息
					FuturesContractDto contract = findByContractId(dto.getContractId());
					// 获取汇率信息
					FuturesCurrencyRateDto rate = findByCurrency(dto.getCommodityCurrency());
					if (market != null && contract != null && rate != null) {
						dto.setLastPrice(market.getLastPrice());
						BigDecimal profitOrLoss = dto.getProfitOrLoss() == null ? new BigDecimal(0)
								: dto.getProfitOrLoss();
						dto.setFloatingProfitOrLoss(profitOrLoss.multiply(rate.getRate()));
					}
				}
			}
		}
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

	public FuturesCurrencyRateDto findByCurrency(String currency) {
		Response<FuturesCurrencyRateDto> response = futuresCurrencyRateInterface.findByCurrency(currency);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
