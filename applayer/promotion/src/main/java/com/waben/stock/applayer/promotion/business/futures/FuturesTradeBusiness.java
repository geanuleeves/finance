package com.waben.stock.applayer.promotion.business.futures;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FutresOrderEntrustDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderAdminDto;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.organization.FuturesFowDto;
import com.waben.stock.interfaces.dto.organization.FuturesTradeOrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.dto.publisher.PublisherDto;
import com.waben.stock.interfaces.dto.publisher.RealNameDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesFowQuery;
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
		
		if(query.getOrgId()!=null){
			Response<OrganizationDto> result = orgReference.fetchByOrgId(query.getOrgId());
			if(result.getResult()!=null && result.getResult().getTreeCode()!=null && !"".equals(result.getResult().getTreeCode())){
				query.setTreeCode(result.getResult().getTreeCode());
			}else{
				throw new ServiceException(ExceptionConstant.ORGCODE_NOTEXIST_EXCEPTION);
			}
		}
		
		Response<List<OrganizationPublisherDto>> result = organizationPublisherReference.queryByTreeCode(query.getTreeCode());
		List<Long> publisherIds = new ArrayList<Long>();
		for(OrganizationPublisherDto publisher:result.getResult()){
			publisherIds.add(publisher.getPublisherId());
		}
		return publisherIds;
	}
	public Response<FuturesOrderCountDto> countOrderState(FuturesTradeAdminQuery query){
		query.setSize(Integer.MAX_VALUE);
		query.setPage(0);
		
		FuturesOrderCountDto dto = new FuturesOrderCountDto();
		Response<PageInfo<FuturesTradeOrganizationDto>> result = pagesOrganizationOrder(query);
		if("200".equals(result.getCode())&&result.getResult()!=null){
			if(result.getResult().getContent().size()>0){
				List<FuturesTradeOrganizationDto> orgDto = result.getResult().getContent();
				BigDecimal totalQuantity = BigDecimal.ZERO;
				BigDecimal reserveFund = BigDecimal.ZERO;
				BigDecimal serviceFee = BigDecimal.ZERO;
				BigDecimal overnightServiceFee = BigDecimal.ZERO;
				for(FuturesTradeOrganizationDto futures : orgDto){
					if(futures.getTotalQuantity()!=null){
						totalQuantity.add(futures.getTotalQuantity());
					}
					if(futures.getReserveFund()!=null){
						reserveFund.add(futures.getReserveFund());
					}
					if(futures.getOpenwindServiceFee()!=null){
						serviceFee.add(futures.getOpenwindServiceFee());
					}
					if(futures.getState().equals("已平仓")){
						serviceFee.add(futures.getUnwindServiceFee());
					}
					if(futures.getOvernightServiceFee()!=null){
						overnightServiceFee.add(futures.getOvernightServiceFee());
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
	
	private List<Long> queryPublishIds(FuturesTradeAdminQuery query){
		List<Long> orgPublisher = getOrgId(query);
		List<Long> publisherIds = new ArrayList<Long>();
		if(query.getPublisherPhone()!=null){
			if(publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult()!=null){
				String publisherId = publisherInterface.fetchByPhone(query.getPublisherPhone()).getResult().getId().toString();
				publisherIds.add(Long.valueOf(publisherId));
				publisherIds = getRepetition(orgPublisher, publisherIds);
			};
		}
		
		if(query.getPublisherName()!=null && !"".equals(query.getPublisherName())){
			if(publisherIds.size()==0 ){
				List<RealNameDto> real = realnameInterface.findByName(query.getPublisherName()).getResult();
				for (RealNameDto realNameDto : real) {
					publisherIds.add(Long.valueOf(realNameDto.getResourceId().toString()));
				}
				
			}else{
				publisherIds.clear();
			}
			publisherIds = getRepetition(orgPublisher, publisherIds);
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
		query.setPublisherIds(queryPublishIds(query));
		Response<PageInfo<FuturesTradeOrganizationDto>> pagesResponse = new Response<PageInfo<FuturesTradeOrganizationDto>>();
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
		query.setPublisherIds(queryPublishIds(query));
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
				}
			}
		}
		return response;
	}
}
