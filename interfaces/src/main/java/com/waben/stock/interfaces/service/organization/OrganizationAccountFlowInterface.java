package com.waben.stock.interfaces.service.organization;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.waben.stock.interfaces.dto.organization.AgentCapitalManageDto;
import com.waben.stock.interfaces.dto.organization.FuturesCommissionAuditDto;
import com.waben.stock.interfaces.dto.organization.OrganizationAccountFlowDto;
import com.waben.stock.interfaces.dto.organization.OrganizationAccountFlowWithTradeInfoDto;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.AgentCapitalManageQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesCommissionAuditQuery;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationAccountFlowQuery;

@FeignClient(name = "organization", path = "organizationAccountFlow", qualifier = "organizationAccountFlowInterface")
public interface OrganizationAccountFlowInterface {

	@RequestMapping(value = "/pagesWithTradeInfo", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<OrganizationAccountFlowWithTradeInfoDto>> pagesWithTradeInfo(
			@RequestBody OrganizationAccountFlowQuery query);

	@RequestMapping(value = "/list", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<List<OrganizationAccountFlowDto>> list();

	/**
	 * 获取资金流水和佣金结算数据
	 * 
	 * @param flowQuery
	 *            查询条件
	 * @return 资金流水和佣金结算
	 */
	@RequestMapping(value = "/agent/capital/manage", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<AgentCapitalManageDto>> pageAgentCapitalManage(@RequestBody AgentCapitalManageQuery query);

	/**
	 * 获取资金流水和佣金结算数据
	 * 
	 * @param flowQuery
	 *            查询条件
	 * @return 资金流水和佣金结算
	 */
	@RequestMapping(value = "/futures/commission/audit", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE)
	Response<PageInfo<FuturesCommissionAuditDto>> pagesCommissionAudit(@RequestBody FuturesCommissionAuditQuery query);

	/**
	 * 获取佣金审核待审核订单条数
	 * 
	 * @return 待审核订单条数
	 */
	@RequestMapping(value = "/count/commission/audit/{orgId}", method = RequestMethod.GET)
	public Response<Integer> countCommissionAudit(@PathVariable("orgId") Long orgId);

	/**
	 * 获取实际返佣总金额
	 * 
	 * @return 实际返佣总金额
	 */
	@RequestMapping(value = "/realMaidFee/{orgId}", method = RequestMethod.GET)
	public Response<BigDecimal> realMaidFee(@PathVariable("orgId") Long orgId);

	/**
	 * 佣金审核
	 * 
	 * @param auditId
	 *            佣金审核ID
	 * @param state
	 *            状态
	 * @param remarks
	 *            备注
	 * @param realMaidFee
	 *            实际返佣金额
	 * @return
	 */
	@RequestMapping(value = "/edit/{auditId}/{state}/{remarks}/{realMaidFee}", method = RequestMethod.POST)
	public Response<Integer> editCommissionAudit(@PathVariable("auditId") Long auditId,
			@PathVariable("state") Integer state, @PathVariable("remarks") String remarks,
			@PathVariable("realMaidFee") BigDecimal realMaidFee);

}
