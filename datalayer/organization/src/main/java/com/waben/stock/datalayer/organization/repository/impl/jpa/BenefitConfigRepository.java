package com.waben.stock.datalayer.organization.repository.impl.jpa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.organization.entity.BenefitConfig;
import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.interfaces.enums.BenefitConfigType;

/**
 * 分成配置 Jpa
 * 
 * @author lma
 *
 */
public interface BenefitConfigRepository extends CustomJpaRepository<BenefitConfig, Long> {

	List<BenefitConfig> findByOrgAndResourceType(Organization org, Integer resourceType, Sort sort);

	List<BenefitConfig> findByOrgAndTypeAndResourceTypeAndResourceId(Organization org, BenefitConfigType type,
			Integer resourceType, Long resourceId);

	List<BenefitConfig> findByOrgAndTypeAndResourceType(Organization org, BenefitConfigType type, Integer resourceType);

	@Query(value = "SELECT SUM(t1.ratio) FROM p_benefit_config t1 LEFT JOIN p_organization t2 ON t2.id = t1.org_id where t2.tree_code LIKE ?1", nativeQuery = true)
	BigDecimal surplusRatio(String treeCode);

}
