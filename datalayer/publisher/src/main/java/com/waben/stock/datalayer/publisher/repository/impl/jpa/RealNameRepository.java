package com.waben.stock.datalayer.publisher.repository.impl.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.publisher.entity.RealName;
import com.waben.stock.interfaces.enums.ResourceType;

import feign.Param;

/**
 * 实名认证 Jpa
 * 
 * @author lma
 *
 */
public interface RealNameRepository extends CustomJpaRepository<RealName, Long> {

	RealName findByResourceTypeAndResourceId(ResourceType resourceType, Long resourceId);

	List<RealName> findByNameAndIdCard(String name, String idCard);
	
	@Query(value = "select * from real_name where name like %?1%", nativeQuery=true)
	List<RealName> findByName(String name);

	RealName findByResourceId(Long resourceId);
}
