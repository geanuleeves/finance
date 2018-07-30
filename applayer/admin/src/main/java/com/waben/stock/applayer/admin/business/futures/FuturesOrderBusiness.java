package com.waben.stock.applayer.admin.business.futures;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.admin.business.ProfileBusiness;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionDto;
import com.waben.stock.interfaces.dto.futures.FuturesTradeActionViewDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.futures.FuturesTradeActionInterface;
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
	
	@Autowired
	private ProfileBusiness profileBusiness;
	
	@Autowired
	@Qualifier("futuresTradeActionInterface")
	private FuturesTradeActionInterface actionReference;

	Logger logger = LoggerFactory.getLogger(getClass());
	
	public PageInfo<FuturesTradeActionViewDto> pageTradeAdmin(FuturesTradeAdminQuery query){
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new PageInfo<FuturesTradeActionViewDto>();
		}else{
			query.setPublisherIds(publisherIds);
		}
		Response<PageInfo<FuturesTradeActionViewDto>> response = actionReference.pagesTradeAdmin(query);
		if ("200".equals(response.getCode())) {

			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

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
						serviceFee = serviceFee.add(adminDto.getOpenwindServiceFee().multiply(adminDto.getTotalQuantity()));
					}
					if (adminDto.getUnwindServiceFee() != null) {
						serviceFee = serviceFee.add(adminDto.getUnwindServiceFee().multiply(adminDto.getTotalQuantity()));
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
		}
		if (query.getPublisherName() != null && !"".equals(query.getPublisherName())) {
			List<RealNameDto> real = realnameInterface.findByName(query.getPublisherName()).getResult();
			if (real == null || real.size() == 0) {
				return null;
			} else {
				if(publisherIds.size()==0){
					for (RealNameDto realNameDto : real) {
						publisherIds.add(Long.valueOf(realNameDto.getResourceId().toString()));
					}
				}else{
					List<Long> realName = new ArrayList<Long>();
					for (RealNameDto realNameDto : real) {
						realName.add(Long.valueOf(realNameDto.getResourceId().toString()));
					}
					publisherIds = getRepetition(realName, publisherIds);
				}
			}

		}
		if(query.getIsTest()!=null){
			Response<List<PublisherDto>> result = publisherInterface.fetchByIsTest(query.getIsTest());
			List<PublisherDto> pu = result.getResult();
			if(pu ==null && pu.size() == 0){
				return null;
			}else{
				if(publisherIds.size()==0){
					for(PublisherDto dto : pu){
						publisherIds.add(dto.getId());
					}
				}else{
					List<Long> publisher = new ArrayList<Long>();
					for(PublisherDto dto : pu){
						publisher.add(dto.getId());
					}
					publisherIds = getRepetition(publisher, publisherIds);
				}
			}
		}
		
		return publisherIds;
	}
	
	public static List<Long> getRepetition(List<Long> list1,  
            List<Long> list2) {  
        List<Long> result = new ArrayList<Long>();  
        for (Long l : list2) {//遍历list1  
            if (list1.contains(l)) {//如果存在这个数  
                result.add(l);//放进一个list里面，这个list就是交集  
            }  
        }  
        return result;  
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
							if(pu.getIsTest()!=null){
								dto.setTest(pu.getIsTest());
							}
						}
						RealNameDto re = realnameInterface.fetchByResourceId(dto.getPublisherId()).getResult();
						if (re != null) {
							dto.setPublisherName(re.getName());
						}
					}
					
					if(dto.getDealTime()==null){
						
					}
					if (dto.getState() != null) {
						if (dto.getState() != FuturesOrderState.SellingEntrust.getType()&& dto.getState() != FuturesOrderState.PartUnwind.getType()) {
							dto.setEntrustPrice(dto.getEntrustAppointPrice());
						}else{
							dto.setEntrustPrice(dto.getSellingEntrustPrice());
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
						if(pu.getIsTest()!=null){
							dto.setTest(pu.getIsTest());
						}
					}
					RealNameDto re = realnameInterface.fetchByResourceId(dto.getPublisherId()).getResult();
					if (re != null) {
						dto.setPublisherName(re.getName());
					}
					// 获取行情信息
					FuturesContractMarket market = RetriveFuturesOverHttp.market(profileBusiness.isProd(), dto.getSymbol(), dto.getContractNo());
					// 获取合约信息
					FuturesContractDto contract = findByContractId(dto.getContractId());
					// 获取汇率信息
					FuturesCurrencyRateDto rate = findByCurrency(dto.getCommodityCurrency());
					if(market != null){
						dto.setLastPrice(market.getLastPrice());
					}
					if(dto.getPublisherProfitOrLoss()==null && dto.getBuyingPrice()!=null){
						if (market != null && contract != null && rate != null) {
							
							// 用户买涨盈亏 = （最新价 - 买入价） / 最小波动点 * 波动一次盈亏金额 * 汇率 *手数
							if (dto.getOrderType()!=null && !"".equals(dto.getOrderType()) && "买涨".equals(dto.getOrderType())) {
								dto.setPublisherProfitOrLoss(
										market.getLastPrice().subtract(dto.getBuyingPrice())
												.divide(contract.getMinWave(),2, BigDecimal.ROUND_HALF_EVEN).multiply(contract.getPerWaveMoney())
												.multiply(rate.getRate()).multiply(dto.getTotalQuantity()));
							} else if(dto.getOrderType()!=null && !"".equals(dto.getOrderType()) && "买跌".equals(dto.getOrderType())){
								// 用户买跌盈亏 = （买入价 - 最新价） / 最小波动点 * 波动一次盈亏金额 * 汇率 *手数
								dto.setPublisherProfitOrLoss(
										dto.getBuyingPrice().subtract(market.getLastPrice())
												.divide(contract.getMinWave(),2, BigDecimal.ROUND_HALF_EVEN).multiply(contract.getPerWaveMoney())
												.multiply(rate.getRate()).multiply(dto.getTotalQuantity()));
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

	public FuturesOrderCountDto getSUMOrder(FuturesTradeAdminQuery query) {
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new FuturesOrderCountDto();
		}else{
			query.setPublisherIds(publisherIds);
		}
		Response<FuturesOrderCountDto> response = reference.getSUMOrder(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

}
