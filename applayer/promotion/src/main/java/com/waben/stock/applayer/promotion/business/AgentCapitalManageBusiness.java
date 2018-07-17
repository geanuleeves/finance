package com.waben.stock.applayer.promotion.business;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.waben.stock.interfaces.dto.organization.AgentCapitalManageDto;
import com.waben.stock.interfaces.dto.organization.FuturesCommissionAuditDto;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.AgentCapitalManageQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesCommissionAuditQuery;
import com.waben.stock.interfaces.service.organization.OrganizationAccountFlowInterface;

@Service
public class AgentCapitalManageBusiness {

	@Autowired
	@Qualifier("organizationAccountFlowInterface")
	private OrganizationAccountFlowInterface accountFlowInterface;

	public PageInfo<AgentCapitalManageDto> pageAgentCapitalManage(AgentCapitalManageQuery query) {
		Response<PageInfo<AgentCapitalManageDto>> response = accountFlowInterface.pageAgentCapitalManage(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public PageInfo<FuturesCommissionAuditDto> pagesCommissionAudit(FuturesCommissionAuditQuery query) {
		Response<PageInfo<FuturesCommissionAuditDto>> response = accountFlowInterface.pagesCommissionAudit(query);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public Integer countCommissionAudit(Long orgId) {
		Response<Integer> response = accountFlowInterface.countCommissionAudit(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public BigDecimal realMaidFee(Long orgId) {
		Response<BigDecimal> response = accountFlowInterface.realMaidFee(orgId);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}

	public Integer editCommissionAudit(Long auditId, Integer state, String remarks, BigDecimal realMaidFee) {
		Response<Integer> response = accountFlowInterface.editCommissionAudit(auditId, state, remarks, realMaidFee);
		if ("200".equals(response.getCode())) {
			return response.getResult();
		}
		throw new ServiceException(response.getCode());
	}
}
