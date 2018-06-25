package com.waben.stock.interfaces.service.organization;

import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.organization.OrganizationPublisherDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;

/**
 * 机构推广的发布人 reference服务接口
 * 
 * @author luomengan
 *
 */
@FeignClient(name = "organization", path = "orgpublisher", qualifier = "organizationPublisherInterface")
public interface OrganizationPublisherInterface {

	@RequestMapping(value = "/", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<OrganizationPublisherDto> addOrgPublisher(@RequestBody OrganizationPublisherDto orgPublisher);

	@RequestMapping(value = "/code/{code}", method = RequestMethod.GET)
	Response<List<OrganizationPublisherDto>> fetchOrganizationPublishersByCode(@PathVariable("code") String code);

	@RequestMapping(value = "/publisherId/{publisherId}", method = RequestMethod.GET)
	Response<OrganizationPublisherDto> fetchOrgPublisher(@PathVariable("publisherId") Long publisherId);

	@RequestMapping(value = "/all", method = RequestMethod.GET)
	Response<List<OrganizationPublisherDto>> fetchAll();
	
	@RequestMapping(value = "/queryByTreeCode", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<List<OrganizationPublisherDto>> queryByTreeCode(@RequestBody FuturesTradeAdminQuery query);

}
