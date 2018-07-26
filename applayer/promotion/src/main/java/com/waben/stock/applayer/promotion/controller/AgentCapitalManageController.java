package com.waben.stock.applayer.promotion.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.waben.stock.applayer.promotion.business.AgentCapitalManageBusiness;
import com.waben.stock.applayer.promotion.business.OrganizationBusiness;
import com.waben.stock.applayer.promotion.security.SecurityUtil;
import com.waben.stock.applayer.promotion.util.PoiUtil;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.organization.AgentCapitalManageDto;
import com.waben.stock.interfaces.dto.organization.FuturesCommissionAuditDto;
import com.waben.stock.interfaces.enums.OrganizationAccountFlowType;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.Response;
import com.waben.stock.interfaces.pojo.query.PageInfo;
import com.waben.stock.interfaces.pojo.query.organization.AgentCapitalManageQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesCommissionAuditQuery;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 代理商资金管理
 * 
 * @author sl
 *
 */
@RestController
@RequestMapping("/agentcapital")
@Api(description = "代理商资金管理")
public class AgentCapitalManageController {

	@Autowired
	public AgentCapitalManageBusiness agentCapitalManageBusiness;

	@Autowired
	private OrganizationBusiness organizationBusiness;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping(value = "/capitalFlow", method = RequestMethod.GET)
	@ApiOperation(value = "资金流水")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "types", value = "流水类型", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "contractCodeOrName", value = "合约代码/名称", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "orgCodeOrName", value = "代理商代码/名称", required = false) })
	public Response<PageInfo<AgentCapitalManageDto>> pagesCapitalFlow(int page, int size, String types,
			String contractCodeOrName, String orgCodeOrName) {
		AgentCapitalManageQuery query = new AgentCapitalManageQuery();
		query.setPage(page);
		query.setSize(size);
		query.setContractCodeOrName(contractCodeOrName);
		query.setOrgCodeOrName(orgCodeOrName);
		query.setTypes(types);
		// query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		return new Response<>(agentCapitalManageBusiness.pageAgentCapitalManage(query));
	}

	@RequestMapping(value = "/commission/audit", method = RequestMethod.GET)
	@ApiOperation(value = "佣金审核列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "types", value = "流水类型", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "commoditySymbol", value = "交易代码", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "String", name = "commodityName", value = "交易名称", required = false),
			@ApiImplicitParam(paramType = "query", dataType = "Integer", name = "states", value = "佣金审核状态", required = false) })
	public Response<PageInfo<FuturesCommissionAuditDto>> pagesCommissionAudit(int page, int size, String types,
			String commoditySymbol, String commodityName, String states) {
		FuturesCommissionAuditQuery query = new FuturesCommissionAuditQuery();
		query.setPage(page);
		query.setSize(size);
		query.setCommoditySymbol(commoditySymbol);
		query.setCommodityName(commodityName);
		query.setTypes(types);
		query.setStates(states);
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		return new Response<>(agentCapitalManageBusiness.pagesCommissionAudit(query));
	}

	@RequestMapping(value = "/count/commission/audit", method = RequestMethod.GET)
	@ApiOperation(value = "待审核订单条数")
	public Response<Integer> countCommissionAudit() {
		return new Response<>(
				agentCapitalManageBusiness.countCommissionAudit(SecurityUtil.getUserDetails().getOrgId()));
	}

	@RequestMapping(value = "/real/maid/fee", method = RequestMethod.GET)
	@ApiOperation(value = "实际返佣金额")
	public Response<BigDecimal> realMaidFee() {
		return new Response<>(agentCapitalManageBusiness.realMaidFee(SecurityUtil.getUserDetails().getOrgId()));
	}

	@RequestMapping(value = "/edit/commission/{auditId}/{state}/{remarks}/{realMaidFee}", method = RequestMethod.POST)
	@ApiOperation(value = "佣金审核修改")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "path", dataType = "Long", name = "auditId", value = "佣金审核ID", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "Integer", name = "state", value = "状态", required = true),
			@ApiImplicitParam(paramType = "path", dataType = "String", name = "remarks", value = "备注", required = false),
			@ApiImplicitParam(paramType = "path", dataType = "BigDecimal", name = "realMaidFee", value = "实际返佣金额", required = true) })
	public Response<Integer> editCommissionAudit(@PathVariable("auditId") Long auditId,
			@PathVariable("state") Integer state, @PathVariable("remarks") String remarks,
			@PathVariable("realMaidFee") BigDecimal realMaidFee) {
		return new Response<>(agentCapitalManageBusiness.editCommissionAudit(auditId, state, remarks, realMaidFee));
	}

	@RequestMapping(value = "/commission/settlement", method = RequestMethod.GET)
	@ApiOperation(value = "佣金结算")
	public Response<PageInfo<AgentCapitalManageDto>> pagesCommissionSettlement(AgentCapitalManageQuery query) {
		// query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		return new Response<>(agentCapitalManageBusiness.pageAgentCapitalManage(query));
	}

	@RequestMapping(value = "/current/sum/ratio", method = RequestMethod.GET)
	@ApiOperation(value = "获取当前代理商可设比例")
	public Response<BigDecimal> getSumRatio() {
		return new Response<>(organizationBusiness.getSumRatio(SecurityUtil.getUserDetails().getOrgId()));
	}

	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ApiOperation(value = "资金流水及佣金结算导出")
	@ApiImplicitParam(paramType = "query", dataType = "int", name = "queryType", value = "1 资金流水，2 佣金结算", required = true)
	public void export(Integer queryType, HttpServletResponse svrResponse) {
		AgentCapitalManageQuery query = new AgentCapitalManageQuery();
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		// query.setTreeCode(SecurityUtil.getUserDetails().getTreeCode());
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		PageInfo<AgentCapitalManageDto> result = agentCapitalManageBusiness.pageAgentCapitalManage(query);
		File file = null;
		FileInputStream is = null;
		String fileName = "";
		try {
			// queryType = 1 ? 资金流水 ： 佣金结算
			if (queryType != null && queryType == 1) {
				fileName = "capitalflow__" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				List<String> columnDescList = columnDescList();
				List<List<String>> dataList = dataList(result.getContent());
				PoiUtil.writeDataToExcel("资金流水", file, columnDescList, dataList);
			} else {
				fileName = "commission__" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				List<String> columnDescList = columnCommList();
				List<List<String>> dataList = dataCommList(result.getContent());
				PoiUtil.writeDataToExcel("佣金结算", file, columnDescList, dataList);
			}

			is = new FileInputStream(file);
			svrResponse.setContentType("application/vnd.ms-excel");
			svrResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
			IOUtils.copy(is, svrResponse.getOutputStream());
			svrResponse.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "导出代理商资金数据到excel异常：" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (file != null) {
				file.delete();
			}
		}
	}

	// 资金流水
	private List<List<String>> dataList(List<AgentCapitalManageDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (AgentCapitalManageDto trade : content) {
			List<String> data = new ArrayList<>();
			String type = "";
			if (trade.getType() != null) {
				type = OrganizationAccountFlowType.getByIndex(trade.getType().toString()).getType();
			}
			data.add(trade.getoTradeNo() == null ? "" : trade.getoTradeNo());
			data.add(trade.getOccurrenceTime() != null ? sdf.format(trade.getOccurrenceTime()) : "");
			data.add(type);
			data.add(String.valueOf(trade.getAmount() == null ? "" : trade.getAmount()));
			data.add(String.valueOf(trade.getAvailableBalance() == null ? "" : trade.getAvailableBalance()));
			data.add(trade.getCommoditySymbol() == null ? "" : trade.getCommoditySymbol());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName());
			data.add(trade.getOrgCode() + "/" + trade.getOrgName());
			result.add(data);
		}
		return result;
	}

	private List<String> columnDescList() {
		List<String> result = new ArrayList<>();
		result.add("订单编号");
		result.add("交易时间");
		result.add("交易类型");
		result.add("交易金额");
		result.add("账户余额");
		result.add("合约代码");
		result.add("交易品种");
		result.add("所属代理商代码/名称");
		return result;
	}

	// 佣金结算
	private List<List<String>> dataCommList(List<AgentCapitalManageDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (AgentCapitalManageDto trade : content) {
			List<String> data = new ArrayList<>();
			String type = "";
			if (trade.getType() != null) {
				type = OrganizationAccountFlowType.getByIndex(trade.getType().toString()).getType();
			}
			data.add(String.valueOf(trade.getId() == null ? "" : trade.getId()));
			data.add(trade.getoTradeNo() == null ? "" : trade.getoTradeNo());
			data.add(trade.getoPublisherName() == null ? "" : trade.getoPublisherName());
			data.add(trade.getoPublisherPhone() == null ? "" : trade.getoPublisherPhone());
			data.add(trade.getCommoditySymbol() == null ? "" : trade.getCommoditySymbol());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName());
			data.add(type);
			data.add(String.valueOf(trade.getCommission() == null ? "" : trade.getCommission()));
			data.add(String.valueOf(trade.getAmountRemaid() == null ? "" : trade.getAmountRemaid()));
			data.add(trade.getOccurrenceTime() != null ? sdf.format(trade.getOccurrenceTime()) : "");
			data.add(trade.getOrgCode() + "/" + trade.getOrgName());
			result.add(data);
		}
		return result;
	}

	private List<String> columnCommList() {
		List<String> result = new ArrayList<>();
		result.add("订单编号");
		result.add("流水号");
		result.add("客户姓名");
		result.add("客户账号");
		result.add("合约代码");
		result.add("产品名称");
		result.add("佣金类型");
		result.add("交易佣金");
		result.add("返佣金额");
		result.add("流水时间");
		result.add("所属代理商代码/名称");
		return result;
	}

	@RequestMapping(value = "/audit/export", method = RequestMethod.GET)
	@ApiOperation(value = "佣金审核及审核记录导出")
	public void auditExport(FuturesCommissionAuditQuery query, HttpServletResponse svrResponse) {
		query.setPage(0);
		query.setSize(Integer.MAX_VALUE);
		query.setCurrentOrgId(SecurityUtil.getUserDetails().getOrgId());
		PageInfo<FuturesCommissionAuditDto> result = agentCapitalManageBusiness.pagesCommissionAudit(query);
		File file = null;
		FileInputStream is = null;
		String fileName = "";
		try {
			if (query.getStates().equals("1")) {
				fileName = "commissionreview__" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				List<String> columnDescList = columnAuditDescList();
				List<List<String>> dataList = dataAuditList(result.getContent());
				PoiUtil.writeDataToExcel("佣金审核", file, columnDescList, dataList);
			} else {
				fileName = "auditrecord__" + String.valueOf(System.currentTimeMillis());
				file = File.createTempFile(fileName, ".xls");
				List<String> columnDescList = columnAuditCommList();
				List<List<String>> dataList = dataAuditCommList(result.getContent());
				PoiUtil.writeDataToExcel("审核记录", file, columnDescList, dataList);
			}

			is = new FileInputStream(file);
			svrResponse.setContentType("application/vnd.ms-excel");
			svrResponse.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
			IOUtils.copy(is, svrResponse.getOutputStream());
			svrResponse.getOutputStream().flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "导出佣金审核数据到excel异常：" + e.getMessage());
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (file != null) {
				file.delete();
			}
		}
	}

	// 佣金审核
	private List<List<String>> dataAuditList(List<FuturesCommissionAuditDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (FuturesCommissionAuditDto trade : content) {
			List<String> data = new ArrayList<>();
			String type = "";
			if (trade.getType() != null) {
				type = OrganizationAccountFlowType.getByIndex(trade.getType().toString()).getType();
			}
			String state = "";
			if (trade.getState() == 1) {
				state = "审核中";
			} else if (trade.getState() == 2) {
				state = "审核通过";
			} else if (trade.getState() == 3) {
				state = "审核不通过";
			}
			data.add(trade.getoTradeNo() == null ? "" : trade.getoTradeNo());
			data.add(trade.getFlowNo() == null ? "" : trade.getFlowNo());
			data.add(trade.getoPublisherName() == null ? "" : trade.getoPublisherName());
			data.add(trade.getoPublisherPhone() == null ? "" : trade.getoPublisherPhone());
			data.add(trade.getCommoditySymbol() == null ? "" : trade.getCommoditySymbol());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName());
			data.add(type);
			data.add(String.valueOf(trade.getCommission() == null ? "" : trade.getCommission()));
			data.add(String.valueOf(trade.getAmountRemaid() == null ? "" : trade.getAmountRemaid()));
			data.add(String.valueOf(trade.getRealMaidFee() == null ? "" : trade.getRealMaidFee()));
			data.add(trade.getExamineTime() != null ? sdf.format(trade.getExamineTime()) : "");
			data.add(trade.getOrgCode() + "/" + trade.getOrgName());
			data.add(state);
			result.add(data);
		}
		return result;
	}

	private List<String> columnAuditDescList() {
		List<String> result = new ArrayList<>();
		result.add("订单编号");
		result.add("流水号");
		result.add("客户姓名");
		result.add("交易账号");
		result.add("交易代码");
		result.add("交易品种");
		result.add("佣金类型");
		result.add("交易金额");
		result.add("系统返佣金额");
		result.add("实际返佣金额");
		result.add("流水时间");
		result.add("所属代理商代码/名称");
		result.add("佣金状态");
		return result;
	}

	// 审核记录
	private List<List<String>> dataAuditCommList(List<FuturesCommissionAuditDto> content) {
		List<List<String>> result = new ArrayList<>();
		for (FuturesCommissionAuditDto trade : content) {
			List<String> data = new ArrayList<>();
			String type = "";
			if (trade.getType() != null) {
				type = OrganizationAccountFlowType.getByIndex(trade.getType().toString()).getType();
			}
			String state = "";
			if (trade.getState() == 1) {
				state = "审核中";
			} else if (trade.getState() == 2) {
				state = "审核通过";
			} else if (trade.getState() == 3) {
				state = "审核不通过";
			}
			data.add(trade.getoTradeNo() == null ? "" : trade.getoTradeNo());
			data.add(trade.getFlowNo() == null ? "" : trade.getFlowNo());
			data.add(trade.getoPublisherName() == null ? "" : trade.getoPublisherName());
			data.add(trade.getoPublisherPhone() == null ? "" : trade.getoPublisherPhone());
			data.add(trade.getCommoditySymbol() == null ? "" : trade.getCommoditySymbol());
			data.add(trade.getCommodityName() == null ? "" : trade.getCommodityName());
			data.add(type);
			data.add(String.valueOf(trade.getCommission() == null ? "" : trade.getCommission()));
			data.add(String.valueOf(trade.getAmountRemaid() == null ? "" : trade.getAmountRemaid()));
			data.add(String.valueOf(trade.getRealMaidFee() == null ? "" : trade.getRealMaidFee()));
			data.add(trade.getExamineTime() != null ? sdf.format(trade.getExamineTime()) : "");
			data.add(trade.getOrgCode() + "/" + trade.getOrgName());
			data.add(state);
			data.add(trade.getAuditRemark() == null ? "" : trade.getAuditRemark());
			result.add(data);
		}
		return result;
	}

	private List<String> columnAuditCommList() {
		List<String> result = new ArrayList<>();
		result.add("订单编号");
		result.add("流水号");
		result.add("客户姓名");
		result.add("交易账号");
		result.add("交易代码");
		result.add("交易品种");
		result.add("佣金类型");
		result.add("交易金额");
		result.add("系统返佣金额");
		result.add("实际返佣金额");
		result.add("流水时间");
		result.add("所属代理商代码/名称");
		result.add("佣金状态");
		result.add("备注");
		return result;
	}
}
