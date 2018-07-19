package com.waben.stock.datalayer.organization.repository;

import java.math.BigDecimal;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;

public interface FuturesCommissionAuditDao extends BaseDao<FuturesCommissionAudit, Long> {

	Integer countCommissionAudit();

	BigDecimal realMaidFee();

	FuturesCommissionAudit findByflowId(Long flowId);
	
	FuturesCommissionAudit findByOneCommission();
}
