package com.waben.stock.datalayer.organization.repository;

import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

/**
 * 机构推广的发布人 Dao
 * 
 * @author lma
 *
 */
public interface OrganizationPublisherDao extends BaseDao<OrganizationPublisher, Long> {

	OrganizationPublisher retrieveByPublisherId(Long publisherId);

	List<OrganizationPublisher> retrieveOrganizationPublishersByOrgCode(String code);
	
	List<OrganizationPublisher> findByOrdId(List<Long> orgId);
	
	List<OrganizationPublisher> findByOrgCode(String orgCode);

}
