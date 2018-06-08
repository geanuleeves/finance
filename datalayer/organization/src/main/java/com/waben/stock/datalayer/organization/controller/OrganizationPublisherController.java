package com.waben.stock.datalayer.organization.controller;

import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.datalayer.organization.service.OrganizationPublisherService;
import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.service.organization.OrganizationPublisherInterface;
import com.waben.stock.interfaces.util.CopyBeanUtils;

import java.util.List;

/**
 * 机构推广的发布人 Controller
 *
 * @author luomengan
 */
@RestController
@RequestMapping("/orgpublisher")
public class OrganizationPublisherController implements OrganizationPublisherInterface {

	Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	public OrganizationPublisherService service;

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

	@Override
	public Response<List<OrganizationPublisherDto>> findByOrgId(List<Long> orgId) {
		List<OrganizationPublisher> result = service.findByOrgId(orgId);
		List<OrganizationPublisherDto> response = CopyBeanUtils.copyListBeanPropertiesToList(result, OrganizationPublisherDto.class);
		return new Response<>(response);
	}

}
