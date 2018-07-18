package com.waben.stock.datalayer.organization.repository;

import java.math.BigDecimal;
import java.util.List;

import com.waben.stock.datalayer.organization.entity.FuturesCommissionAudit;
import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;

public interface FuturesCommissionAuditDao extends BaseDao<FuturesCommissionAudit, Long> {

	Integer countCommissionAudit();

	BigDecimal realMaidFee();

	FuturesCommissionAudit findByflowId(Long flowId);
}
