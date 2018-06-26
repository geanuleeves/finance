package com.waben.stock.applayer.promotion.business.futures;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.applayer.promotion.business.ProfileBusiness;
import com.waben.stock.applayer.promotion.security.SecurityUtil;
import com.waben.stock.interfaces.commonapi.retrivefutures.RetriveFuturesOverHttp;
import com.waben.stock.interfaces.commonapi.retrivefutures.bean.FuturesContractMarket;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.futures.FuturesContractDto;
import com.waben.stock.interfaces.dto.futures.FuturesCurrencyRateDto;
import com.waben.stock.interfaces.dto.organization.FuturesFowDto;
import com.waben.stock.interfaces.dto.organization.FuturesTradeOrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.enums.FuturesOrderState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesFowQuery;
import com.waben.stock.interfaces.service.futures.FuturesContractInterface;
import com.waben.stock.interfaces.service.futures.FuturesCurrencyRateInterface;
import com.waben.stock.interfaces.service.futures.FuturesTradeInterface;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;
import com.waben.stock.interfaces.service.organization.OrganizationPublisherInterface;
import com.waben.stock.interfaces.service.publisher.PublisherInterface;
import com.waben.stock.interfaces.service.publisher.RealNameInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

@Service
public class FuturesTradeBusiness {

	@Autowired
	@Qualifier("futuresTradeInterface")
	private FuturesTradeInterface reference;
	
	@Autowired
	private OrganizationPublisherInterface organizationPublisherReference;
	
	@Autowired
	private PublisherInterface publisherInterface;
	
	@Autowired
	private RealNameInterface realnameInterface;
	
	@Autowired
	private OrganizationInterface orgReference;
	
	@Autowired
	@Qualifier("futurescontractInterface")
	private FuturesContractInterface futuresContractInterface;

	@Autowired
	@Qualifier("futuresCurrencyRateInterface")
	private FuturesCurrencyRateInterface futuresCurrencyRateInterface;
	
	@Autowired
	private ProfileBusiness profileBusiness;
	
	public PageInfo<FuturesFowDto> futuresFowPageByQuery(FuturesFowQuery query){
		if(query.getCurrentOrgId()!=null){
			Response<OrganizationDto> result = orgReference.fetchByOrgId(query.getCurrentOrgId());
			if(result.getResult()!=null && result.getResult().getTreeCode()!=null && !"".equals(result.getResult().getTreeCode())){
				query.setTreeCode(result.getResult().getTreeCode());
			}else{
				throw new ServiceException(ExceptionConstant.ORGCODE_NOTEXIST_EXCEPTION);
			}
		}
		Response<PageInfo<FuturesFowDto>> response =  orgReference.futuresFowPageByQuery(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
	
	private List<Long> getOrgId(FuturesTradeAdminQuery query){
		Long orgId = SecurityUtil.getUserDetails().getOrgId();
		query.setOrgId(orgId);
		if(query.getOrgId()!=null){
			Response<OrganizationDto> result = orgReference.fetchByOrgId(query.getOrgId());
			if(result.getResult()!=null && result.getResult().getTreeCode()!=null && !"".equals(result.getResult().getTreeCode())){
				query.setTreeCode(result.getResult().getTreeCode());
			}else{
				throw new ServiceException(ExceptionConstant.ORGCODE_NOTEXIST_EXCEPTION);
			}
		}
		
		Response<List<OrganizationPublisherDto>> result = organizationPublisherReference.queryByTreeCode(query);
		List<Long> publisherIds = new ArrayList<Long>();
		for(OrganizationPublisherDto publisher:result.getResult()){
			publisherIds.add(publisher.getPublisherId());
		}
		return publisherIds;
	}
	public Response<FuturesOrderCountDto> countOrderState(FuturesTradeAdminQuery query){
		query.setSize(Integer.MAX_VALUE);
		query.setPage(0);
		
		
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new Response<FuturesOrderCountDto>();
		}else{
			query.setPublisherIds(publisherIds);
		}
		Response<FuturesOrderCountDto> res = orgReference.getSUMOrder(query);
		return res;
	}
	
	private List<Long> queryPublishIds(FuturesTradeAdminQuery query){
		
		
		List<Long> orgPublisher = getOrgId(query);
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
		if(publisherIds.size()>0){
			publisherIds = getRepetition(orgPublisher, publisherIds);
		}else{
			publisherIds = orgPublisher;
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
	
	public Response<PageInfo<FuturesTradeOrganizationDto>> pagesOrganizationOrder(FuturesTradeAdminQuery query){
		Response<PageInfo<FuturesTradeOrganizationDto>> pagesResponse = new Response<PageInfo<FuturesTradeOrganizationDto>>();
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return pagesResponse;
		}else{
			query.setPublisherIds(publisherIds);
		}
		Response<PageInfo<FuturesOrderAdminDto>> response = reference.adminPagesByQuery(query);
		if(response!=null && response.getResult()!=null){
			List<FuturesOrderAdminDto> list = response.getResult().getContent();
			List<FuturesTradeOrganizationDto> result = CopyBeanUtils.copyListBeanPropertiesToList(list, FuturesTradeOrganizationDto.class);
			for(FuturesTradeOrganizationDto dto:result){
				if(dto.getPublisherId()!=null){
					PublisherDto pu = publisherInterface.fetchById(dto.getPublisherId()).getResult();
					if(pu!=null){
						dto.setPublisherPhone(pu.getPhone());
					}
					RealNameDto re = realnameInterface.fetchByResourceId(dto.getPublisherId()).getResult();
					if(re!=null){
						dto.setPublisherName(re.getName());
					}
					Response<OrganizationPublisherDto> opDto = organizationPublisherReference.fetchOrgPublisher(dto.getPublisherId());
					if(opDto.getResult()!=null){
						Response<OrganizationDto> org = orgReference.fetchByOrgId(opDto.getResult().getOrgId());
						if(org.getResult()!=null){
							dto.setOrgName(org.getResult().getCode()+"/"+org.getResult().getName());
						}
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
			PageInfo<FuturesTradeOrganizationDto> pageInfo = new PageInfo<>(result, response.getResult().getTotalPages(), response.getResult().getLast(), response.getResult().getTotalElements(), query.getSize(), query.getPage(), response.getResult().getFrist());
			pagesResponse.setCode("200");
			pagesResponse.setResult(pageInfo);
			pagesResponse.setMessage("响应成功");
		}
		return pagesResponse;
	}
	
	public Response<PageInfo<FutresOrderEntrustDto>> pagesOrganizationEntrustOrder(FuturesTradeAdminQuery query){
		List<Long> publisherIds = queryPublishIds(query);
		if(publisherIds==null){
			return new Response<PageInfo<FutresOrderEntrustDto>>();
		}else{
			query.setPublisherIds(publisherIds);
		}
		Response<PageInfo<FutresOrderEntrustDto>> response = reference.pagesOrderEntrust(query);
		if(response.getCode().equals("200")&&response.getResult()!=null&&response.getResult().getContent()!=null){
			for(FutresOrderEntrustDto dto : response.getResult().getContent()){
				if(dto.getPublisherId()!=null){
					PublisherDto pu = publisherInterface.fetchById(dto.getPublisherId()).getResult();
					if(pu!=null){
						dto.setPublisherPhone(pu.getPhone());
					}
					RealNameDto re = realnameInterface.fetchByResourceId(dto.getPublisherId()).getResult();
					if(re!=null){
						dto.setPublisherName(re.getName());
					}
					Response<OrganizationPublisherDto> opDto = organizationPublisherReference.fetchOrgPublisher(dto.getPublisherId());
					if(opDto.getResult()!=null){
						Response<OrganizationDto> org = orgReference.fetchByOrgId(opDto.getResult().getOrgId());
						if(org.getResult()!=null){
							dto.setOrgName(org.getResult().getCode()+"/"+org.getResult().getName());
						}
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
		return response;
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
