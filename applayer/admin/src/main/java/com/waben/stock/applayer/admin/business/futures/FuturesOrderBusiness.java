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
				BigDecimal totalQuantity = BigDecimal.ZERO;
				BigDecimal reserveFund = BigDecimal.ZERO;
				BigDecimal serviceFee = BigDecimal.ZERO;
				BigDecimal overnightServiceFee = BigDecimal.ZERO;
				for (FuturesOrderAdminDto adminDto : result.getContent()) {
					if (adminDto.getTotalQuantity() != null) {
						totalQuantity = totalQuantity.add(adminDto.getTotalQuantity());
					}
					if (adminDto.getReserveFund() != null) {
						reserveFund = reserveFund.add(adminDto.getReserveFund());
					}
					if (adminDto.getOpenwindServiceFee() != null) {
						serviceFee = serviceFee.add(adminDto.getOpenwindServiceFee());
					}
					if (adminDto.getUnwindServiceFee() != null) {
						serviceFee = serviceFee.add(adminDto.getUnwindServiceFee());
					}
					if (adminDto.getOvernightServiceFee() != null) {
						overnightServiceFee = overnightServiceFee.add(adminDto.getOvernightServiceFee());
					}
				}
				dto.setDeferred(overnightServiceFee);
				dto.setQuantity(totalQuantity);
				dto.setFee(serviceFee);
				dto.setFund(reserveFund);
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
		if (query.getPublisherPhone() != null && !"".equals(query.getPublisherPhone())) {
			if (publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult() != null) {
				String publisherId = publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult().getId()
						.toString();
				if (publisherId != null && !"".equals(publisherId)) {
					publisherIds.add(Long.valueOf(publisherId));
				} else {
					return null;
				}
			} else {
				return null;
			}
			;
		} else if (query.getPublisherName() != null && !"".equals(query.getPublisherName())) {
			List<RealNameDto> real = realnameInterface.findByName(query.getPublisherName()).getResult();
			if (real == null || real.size() == 0) {
				return null;
			} else {
				for (RealNameDto realNameDto : real) {
					publisherIds.add(Long.valueOf(realNameDto.getResourceId().toString()));
				}
			}

		}
		return publisherIds;
	}

	public PageInfo<FutresOrderEntrustDto> pagesOrderEntrust(FuturesTradeAdminQuery query) {
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new PageInfo<FutresOrderEntrustDto>();
		}else{
			query.setPublisherIds(publisherIds);
		}
		
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
					
					if(dto.getDealTime()==null){
						
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
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new PageInfo<FuturesOrderAdminDto>();
		}else{
			query.setPublisherIds(publisherIds);
		}
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
					if(dto.getPublisherProfitOrLoss()==null){
						if (market != null && contract != null && rate != null) {
							dto.setLastPrice(market.getLastPrice());
							if(dto.getOrderType()!=null && !"".equals(dto.getOrderType()) && "买涨".equals(dto.getOrderType())){
								dto.setFloatingProfitOrLoss(dto.getLastPrice().subtract(dto.getBuyingPrice()).multiply(dto.getTotalQuantity()));
							}else if(dto.getOrderType()!=null && !"".equals(dto.getOrderType()) && "买跌".equals(dto.getOrderType())){
								dto.setFloatingProfitOrLoss(dto.getBuyingPrice().subtract(dto.getLastPrice()).multiply(dto.getTotalQuantity()));
							}
						}
					}else{
						dto.setFloatingProfitOrLoss(dto.getPublisherProfitOrLoss());
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

	public FuturesOrderCountDto getSUMOrder(String state) {
		Response<FuturesOrderCountDto> response = reference.getSUMOrder(state);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
