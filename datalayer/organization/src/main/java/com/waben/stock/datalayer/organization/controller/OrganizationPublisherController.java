package com.waben.stock.datalayer.organization.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;
import com.waben.stock.datalayer.organization.service.OrganizationPublisherService;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationQuery;
import com.waben.stock.interfaces.service.organization.OrganizationInterface;
import com.waben.stock.interfaces.service.organization.OrganizationPublisherInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.StringUtil;

/**
 * 机构推广的发布人 Controller
 *
 * @author lma
 */
@RestController
@RequestMapping("/orgpublisher")
public class OrganizationPublisherController implements OrganizationPublisherInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public OrganizationPublisherService service;
	
	@Autowired
	@Qualifier("organizationInterface")
	private OrganizationInterface orgReference;

	@Override
	public Response<OrganizationPublisherDto> addOrgPublisher(@RequestBody OrganizationPublisherDto orgPublisher) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(OrganizationPublisherDto.class,
				service.addOrgPublisher(orgPublisher.getOrgCode(), orgPublisher.getPublisherId()), false));
	}

	@Override
	public Response<List<OrganizationPublisherDto>> fetchOrganizationPublishersByCode(@PathVariable String code) {
		List<OrganizationPublisher> result = service.findOrganizationPublishersByCode(code);
		List<OrganizationPublisherDto> response = CopyBeanUtils.copyListBeanPropertiesToList(result, OrganizationPublisherDto.class);
		return new Response<>(response);
	}
	@Override
	public Response<OrganizationPublisherDto> fetchOrgPublisher(@PathVariable Long publisherId) {
		return new Response<>(CopyBeanUtils.copyBeanProperties(OrganizationPublisherDto.class,
				service.findOrgPulisher(publisherId), false));
	}

	@Override
	public Response<List<OrganizationPublisherDto>> fetchAll() {
		List<OrganizationPublisher> result = service.findAll();
		List<OrganizationPublisherDto> response = CopyBeanUtils.copyListBeanPropertiesToList(result, OrganizationPublisherDto.class);
		return new Response<>(response);
	}

	public Response<List<OrganizationPublisherDto>> findByTreeCode(String code) {
		OrganizationQuery query = new OrganizationQuery();
		query.setTreeCode(code);
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		//查询改机构下的所有子机构
		Response<PageInfo<OrganizationDto>> response = orgReference.adminPage(query);
		List<OrganizationDto> list = response.getResult().getContent();
		List<Long> orgIds = new ArrayList<Long>();
		for(OrganizationDto dto:list){
			orgIds.add(dto.getId());
		}
		
		List<OrganizationPublisher> result = service.findByOrgId(orgIds);
		List<OrganizationPublisherDto> resultDto = CopyBeanUtils.copyListBeanPropertiesToList(result, OrganizationPublisherDto.class);
		return new Response<>(resultDto);
	}

	@Override
	public Response<List<OrganizationPublisherDto>> queryByTreeCode(@RequestBody FuturesTradeAdminQuery query) {
		if(query.getTreeCode() !=null && !"".equals(query.getTreeCode())){
			List<OrganizationPublisher> result = service.findByOrgCode(query.getTreeCode());
			List<OrganizationPublisherDto> resultDto = CopyBeanUtils.copyListBeanPropertiesToList(result, OrganizationPublisherDto.class);
			return new Response<>(resultDto);
		}else{
			return new Response<>();
		}
	}

	@Override
	public Response<OrganizationPublisherDto> addOrgPublisher(@PathVariable Long publisherId) {
		List<Organization> org = service.listByLevel(1);
		if(org!=null && org.size()>0){
			Organization zation = org.get(0);
			if(!StringUtil.isEmpty(zation.getCode())){
				return new Response<>(CopyBeanUtils.copyBeanProperties(OrganizationPublisherDto.class,
						service.addOrgPublisherAdmin(zation.getCode(), publisherId), false));
			}
		}
		return new Response<>(new OrganizationPublisherDto());
	}
	
	

}
