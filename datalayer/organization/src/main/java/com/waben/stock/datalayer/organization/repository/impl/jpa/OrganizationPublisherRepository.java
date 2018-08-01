package com.waben.stock.datalayer.organization.repository.impl.jpa;

import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 机构推广的发布人 Jpa
 * 
 * @author lma
 *
 */
public interface OrganizationPublisherRepository extends CustomJpaRepository<OrganizationPublisher, Long> {

	OrganizationPublisher findByPublisherId(Long publisherId);

	List<OrganizationPublisher> findOrganizationPublishersByOrgCode(String code);
	
	@Query(value ="select * from p_organization_publisher where org_id in ?1", nativeQuery=true)
	List<OrganizationPublisher> findByOrdId(@PathVariable("orId") List<Long> orgId);
	
	@Query(value = "select * from p_organization_publisher where org_code like %:orgCode", nativeQuery=true)
	List<OrganizationPublisher> findByOrgCode(@PathVariable("orgCode") String orgCode);
	
	List<OrganizationPublisher> findByTreeCode(String treeCode);
}
