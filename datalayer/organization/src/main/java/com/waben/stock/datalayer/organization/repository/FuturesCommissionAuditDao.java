package com.waben.stock.datalayer.organization.repository;

import java.math.BigDecimal;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;

public interface FuturesCommissionAuditDao extends BaseDao<FuturesCommissionAudit, Long> {

	Integer countCommissionAudit(Long orgId);

	BigDecimal realMaidFee(Long orgId);
}
