package com.waben.stock.datalayer.organization.repository.impl.jpa;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;
import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;

public interface FuturesCommissionAuditRepository extends CustomJpaRepository<FuturesCommissionAudit, Long> {

	@Query("select count(f) from FuturesCommissionAudit f where f.state=1")
	Integer countCommissionAudit();

	@Query(value = "select IF(SUM(f.real_maid_fee) IS NULL,0,SUM(f.real_maid_fee)) from p_futures_commission_audit f where f.state = 2", nativeQuery = true)
	BigDecimal realMaidFee();

	@Query(value = "select * from p_futures_commission_audit t1 where t1.flow_id=?1 order by t1.examine_time desc limit 0,1", nativeQuery = true)
	FuturesCommissionAudit findByflowId(Long flowId);
}
