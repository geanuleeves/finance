package com.waben.stock.datalayer.organization.repository.impl.jpa;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Query;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;

public interface FuturesCommissionAuditRepository extends CustomJpaRepository<FuturesCommissionAudit, Long> {

	@Query("select count(f) from FuturesCommissionAudit f where f.state=1")
	Integer countCommissionAudit(Long orgId);

	@Query("select SUM(f.realMaidFee) from FuturesCommissionAudit f where f.state = 2")
	BigDecimal realMaidFee(Long orgId);
}
