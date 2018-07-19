package com.waben.stock.datalayer.organization.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.organization.entity.OrganizationAccountFlow;
import com.waben.stock.datalayer.organization.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountFlowDao;
import com.waben.stock.datalayer.organization.repository.impl.MethodDesc;
import com.waben.stock.interfaces.dto.organization.AgentCapitalManageDto;
import com.waben.stock.interfaces.dto.organization.FuturesCommissionAuditDto;
import com.waben.stock.interfaces.dto.organization.OrganizationAccountFlowWithTradeInfoDto;
import com.waben.stock.interfaces.enums.OrganizationAccountFlowType;
import com.waben.stock.interfaces.pojo.query.organization.AgentCapitalManageQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesCommissionAuditQuery;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationAccountFlowQuery;
import com.waben.stock.interfaces.util.StringUtil;

/**
 * 机构账户流水 Service
 *
 * @author luomengan
 */
@Service
public class OrganizationAccountFlowService {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private OrganizationAccountFlowDao organizationAccountFlowDao;

	@Autowired
	private DynamicQuerySqlDao dynamicQuerySqlDao;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public OrganizationAccountFlow getOrganizationAccountFlowInfo(Long id) {
		return organizationAccountFlowDao.retrieve(id);
	}

	@Transactional
	public OrganizationAccountFlow addOrganizationAccountFlow(OrganizationAccountFlow organizationAccountFlow) {
		return organizationAccountFlowDao.create(organizationAccountFlow);
	}

	@Transactional
	public OrganizationAccountFlow modifyOrganizationAccountFlow(OrganizationAccountFlow organizationAccountFlow) {
		return organizationAccountFlowDao.update(organizationAccountFlow);
	}

	public Page<OrganizationAccountFlowWithTradeInfoDto> pagesWithTradeInfoByQuery(OrganizationAccountFlowQuery query) {
		String queryTypeCondition = " and (t7.level=1 or (t4.id=t7.id or t4.parent_id=t7.id and t7.level>1)) ";
		// if (query.getQueryType() != null && query.getQueryType() == 1) {
		// queryTypeCondition = " and t1.org_id=" + query.getCurrentOrgId() + "
		// ";
		// } else {
		// queryTypeCondition = " and (t7.level=1 or (t4.id=t7.id or
		// t4.parent_id=t7.id and t7.level>1)) ";
		// }
		String tradeNoCondition = "";
		if (!StringUtil.isEmpty(query.getTradeNo())) {
			tradeNoCondition = " and  t1.resource_trade_no like '%" + query.getTradeNo() + "%' ";
		}
		String flowNoCondition = "";
		if (!StringUtil.isEmpty(query.getFlowNo())) {
			flowNoCondition = " and t1.flow_no like '%" + query.getFlowNo() + "%' ";
		}
		String publisherPhoneCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			publisherPhoneCondition = " and ((t2.id is not null and t2.publisher_phone like '%"
					+ query.getPublisherPhone() + "%') or (t3.id is not null and t3.publisher_phone like '%"
					+ query.getPublisherPhone() + "%')) ";
		}
		String publisherNameCondition = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			publisherNameCondition = " and ((t2.id is not null and t5.name like '%" + query.getPublisherName()
					+ "%') or (t3.id is not null and t6.name like '%" + query.getPublisherName() + "%')) ";
		}
		String cycleIdCondition = "";
		if (query.getCycleId() != null && query.getCycleId() > 0) {
			cycleIdCondition = " and t3.id is not null and t3.cycle_id=" + query.getCycleId() + " ";
		}
		String orgCodeOrNameConditon = "";
		if (!StringUtil.isEmpty(query.getOrgCodeOrName())) {
			orgCodeOrNameConditon = " and (t4.code like '%" + query.getOrgCodeOrName() + "%' or t4.name like '%"
					+ query.getOrgCodeOrName() + "%') ";
		}
		String stockCodeOrNameConditon = "";
		if (!StringUtil.isEmpty(query.getStockCodeOrName())) {
			stockCodeOrNameConditon = " and ((t2.id is not null and (t2.stock_code like '%" + query.getStockCodeOrName()
					+ "%' or t2.stock_name like '%" + query.getStockCodeOrName()
					+ "%')) or (t3.id is not null and (t3.stock_code like '%" + query.getStockCodeOrName()
					+ "%' or t3.stock_name like '%" + query.getStockCodeOrName() + "%'))) ";
		}
		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			startTimeCondition = " and t1.occurrence_time>='" + sdf.format(query.getStartTime()) + "' ";
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			endTimeCondition = " and t1.occurrence_time<'" + sdf.format(query.getEndTime()) + "' ";
		}
		String typeCondition = "";
		if (!StringUtil.isEmpty(query.getTypes()) && !"0".equals(query.getTypes())) {
			typeCondition = " and t1.type in(" + query.getTypes() + ") ";
		}

		String sql = String.format(
				"select t1.id, t1.amount, t1.flow_no, t1.occurrence_time, t1.origin_amount, t1.remark, t1.resource_id, t1.resource_trade_no, t1.resource_type, t1.type, t1.org_id, "
						+ "t2.publisher_id as b_publisher_id, t2.publisher_phone as b_publisher_phone, t2.stock_code as b_stock_code, t2.stock_name as b_stock_name, "
						+ "t3.publisher_id as s_publisher_id, t3.publisher_phone as s_publisher_phone, t3.stock_code as s_stock_code, t3.stock_name as s_stock_name, "
						+ "t3.cycle_id, t3.cycle_name, t4.code as org_code, t4.name as org_name, "
						+ "t5.name as b_publisher_name, t6.name as s_publisher_name, t1.available_balance, "
						+ "t8.commodity_symbol, t8.commodity_name, "
						+ "(IF(t1.type = 7,t11.cost_openwind_service_fee,IF(t1.type=8,t11.cost_unwind_service_fee,IF(t1.type=9,t11.cost_deferred_fee,0))) - "
						+ "IF(t1.type = 7,t12.cost_openwind_service_fee,IF(t1.type=8,t12.cost_unwind_service_fee,IF(t1.type=9,t12.cost_deferred_fee,0)))) AS maid_fee, "
						+ "IF(t1.type = 7,t11.sale_openwind_service_fee,IF(t1.type=8,t11.sale_unwind_service_fee,IF(t1.type=9,t11.sale_deferred_fee,0))) AS commission "
						+ "from p_organization_account_flow t1 "
						+ "LEFT JOIN buy_record t2 on t1.resource_type=1 and t1.resource_id=t2.id "
						+ "LEFT JOIN stock_option_trade t3 on t1.resource_type=3 and t1.resource_id=t3.id "
						+ "LEFT JOIN p_organization t4 on t1.org_id=t4.id "
						+ "LEFT JOIN f_futures_order t8 ON t1.resource_type = 6 AND t8.id = t1.resource_id "
						+ "LEFT JOIN f_futures_contract t9 ON t9.id = t8.contract_id "
						+ "LEFT JOIN f_futures_commodity t10 ON t10.id = t9.commodity_id "
						+ "LEFT JOIN p_futures_agent_price t11 ON t11.commodity_id = t10.id AND t11.org_id = t4.id "
						+ "LEFT JOIN p_futures_agent_price t12 ON t12.commodity_id=t10.id AND t12.org_id = t4.parent_id "
						+ "LEFT JOIN real_name t5 on t5.resource_type=2 and t2.publisher_id=t5.resource_id "
						+ "LEFT JOIN real_name t6 on t6.resource_type=2 and t3.publisher_id=t6.resource_id "
						+ "LEFT JOIN p_organization t7 on t7.id=" + query.getCurrentOrgId() + " "
						+ "where 1=1 %s %s %s %s %s %s %s %s %s %s %s and t1.org_id is not null order by t1.occurrence_time desc limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				queryTypeCondition, tradeNoCondition, flowNoCondition, publisherPhoneCondition, publisherNameCondition,
				cycleIdCondition, orgCodeOrNameConditon, stockCodeOrNameConditon, startTimeCondition, endTimeCondition,
				typeCondition);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("limit"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setFlowNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setOccurrenceTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setOriginAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setRemark", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setResourceId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setResourceTradeNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setResourceType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setOrgId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setbPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setbPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setbStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setbStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setsPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setsPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setsStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setsStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setCycleId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(20), new MethodDesc("setCycleName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(21), new MethodDesc("setOrgCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(22), new MethodDesc("setOrgName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(23), new MethodDesc("setbPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(24), new MethodDesc("setsPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(25), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(26), new MethodDesc("setCommoditySymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(27), new MethodDesc("setCommodityName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(28), new MethodDesc("setCommission", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(29), new MethodDesc("setAmountRemaid", new Class<?>[] { BigDecimal.class }));
		List<OrganizationAccountFlowWithTradeInfoDto> content = dynamicQuerySqlDao
				.execute(OrganizationAccountFlowWithTradeInfoDto.class, sql, setMethodMap);
		BigInteger totalElements = dynamicQuerySqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	@Transactional
	public void deleteOrganizationAccountFlow(Long id) {
		organizationAccountFlowDao.delete(id);
	}

	@Transactional
	public void deleteOrganizationAccountFlows(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					organizationAccountFlowDao.delete(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<OrganizationAccountFlow> organizationAccountFlows(int page, int limit) {
		return organizationAccountFlowDao.page(page, limit);
	}

	public List<OrganizationAccountFlow> list() {
		return organizationAccountFlowDao.list();
	}

	public Page<AgentCapitalManageDto> pageAgentCapitalManage(AgentCapitalManageQuery query) {
		String types = "";
		if (!StringUtil.isEmpty(query.getTypes())) {
			types = " and t1.type in(" + query.getTypes() + ")";
		}
		String contractCodeOrName = "";
		if (!StringUtil.isEmpty(query.getContractCodeOrName())) {
			contractCodeOrName = " and t2.contract_symbol like '" + query.getContractCodeOrName()
					+ "' or t2.contract_name like '" + query.getContractCodeOrName() + "'";
		}
		String orgCodeOrName = "";
		if (!StringUtil.isEmpty(query.getOrgCodeOrName())) {
			orgCodeOrName = " and t3.code like '" + query.getOrgCodeOrName() + "' or t3. NAME like '"
					+ query.getOrgCodeOrName() + "'";
		}

		String flowNo = "";
		if (!StringUtil.isEmpty(query.getFlowNo())) {
			flowNo = " and t1.flow_no like '" + query.getFlowNo() + "'";
		}
		String customerName = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			customerName = " and t5.name like '" + query.getPublisherName() + "'";
		}
		String customerPhone = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			customerPhone = " and t6.phone like '" + query.getPublisherPhone() + "'";
		}

		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			startTimeCondition = " and t1.occurrence_time >='" + sdf.format(query.getStartTime()) + "' ";
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			endTimeCondition = " and t1.occurrence_time <'" + sdf.format(query.getEndTime()) + "' ";
		}

		String sql = String.format(
				"SELECT t1.id, t1.flow_no, t5.name AS customer_name, t6.phone, t2.contract_symbol, t2.contract_name, "
						+ "t1.type, IF(t1.type = 7,t7.sale_openwind_service_fee,IF(t1.type=8,t7.sale_unwind_service_fee,IF(t1.type=9,t7.sale_deferred_fee,0))) AS commission, "
						+ "(IF(t1.type = 7,t7.cost_openwind_service_fee,IF(t1.type=8,t7.cost_unwind_service_fee,IF(t1.type=9,t7.cost_deferred_fee,0))) - "
						+ "IF(t1.type = 7,t8.cost_openwind_service_fee,IF(t1.type=8,t8.cost_unwind_service_fee,IF(t1.type=9,t8.cost_deferred_fee,0)))) AS maid_fee, "
						+ "t1.occurrence_time, t1.amount, t1.available_balance, t3.code AS agent_code, t3. NAME AS agent_name "
						+ "from p_organization_account_flow t1 "
						+ "LEFT JOIN f_futures_order t2 ON t2.id = t1.resource_id  AND t1.resource_type=6 "
						+ "LEFT JOIN p_organization t3 ON t3.id = t1.org_id "
						+ "LEFT JOIN real_name t5 ON t5.resource_id = t2.publisher_id and t5.resource_type=2 "
						+ "LEFT JOIN publisher t6 ON t6.id = t2.publisher_id "
						+ "LEFT JOIN p_futures_agent_price t7 ON t7.contract_id = t2.contract_id "
						+ "AND t7.org_id = t3.id "
						+ "LEFT JOIN p_futures_agent_price t8 ON t8.contract_id = t2.contract_id "
						+ "AND t7.org_id = t3.parent_id " + "WHERE t3.tree_code LIKE CONCAT('" + query.getTreeCode()
						+ "', '%%') %s %s %s %s %s %s %s %s ORDER BY t1.occurrence_time DESC limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				types, contractCodeOrName, orgCodeOrName, flowNo, customerName, customerPhone, startTimeCondition,
				endTimeCondition);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("limit"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setFlowNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setCustomerName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setCustomerPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setContractSymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setContractName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6),
				new MethodDesc("setType", new Class<?>[] { OrganizationAccountFlowType.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setCommission", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setAmountRemaid", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setOccurrenceTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setAgentCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setAgentName", new Class<?>[] { String.class }));
		List<AgentCapitalManageDto> content = dynamicQuerySqlDao.execute(AgentCapitalManageDto.class, sql,
				setMethodMap);
		BigInteger totalElements = dynamicQuerySqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<OrganizationAccountFlowWithTradeInfoDto> pageAgentCapitalManageNew(AgentCapitalManageQuery query) {

		String queryTypeCondition = " and (t7.level=1 or (t4.id=t7.id or t4.tree_code LIKE CONCAT(t7.tree_code,'%%') and t7.level>1)) ";

		String types = "";
		if (!StringUtil.isEmpty(query.getTypes())) {
			types = " and t1.type in(" + query.getTypes() + ")";
		}
		String contractCodeOrName = "";
		if (!StringUtil.isEmpty(query.getContractCodeOrName())) {
			contractCodeOrName = " and t8.commodity_symbol like '%" + query.getContractCodeOrName()
					+ "%' or t8.commodity_name like '%" + query.getContractCodeOrName() + "%'";
		}
		String orgCodeOrName = "";
		if (!StringUtil.isEmpty(query.getOrgCodeOrName())) {
			orgCodeOrName = " and t4.code like '%" + query.getOrgCodeOrName() + "%' or t4.name like '%"
					+ query.getOrgCodeOrName() + "%'";
		}
		String flowNo = "";
		if (!StringUtil.isEmpty(query.getFlowNo())) {
			flowNo = " and t1.flow_no like '" + query.getFlowNo() + "'";
		}
		String customerName = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			customerName = " and t14.id is not null and t14.name like '%" + query.getPublisherName() + "%'";
		}
		String customerPhone = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			customerPhone = " and t15.id is not null and t15.phone like '%" + query.getPublisherPhone() + "%'";
		}

		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			startTimeCondition = " and t1.occurrence_time >='" + sdf.format(query.getStartTime()) + "' ";
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			endTimeCondition = " and t1.occurrence_time <'" + sdf.format(query.getEndTime()) + "' ";
		}

		String sql = String
				.format("select t1.id, t1.amount, t1.flow_no, t1.occurrence_time, t1.origin_amount, t1.remark, t1.resource_id, t1.resource_trade_no, t1.resource_type, t1.type, t1.org_id, "
						+ "t2.publisher_id as b_publisher_id, t2.publisher_phone as b_publisher_phone, t2.stock_code as b_stock_code, t2.stock_name as b_stock_name, "
						+ "t3.publisher_id as s_publisher_id, t3.publisher_phone as s_publisher_phone, t3.stock_code as s_stock_code, t3.stock_name as s_stock_name, "
						+ "t3.cycle_id, t3.cycle_name, t4.code as org_code, t4.name as org_name, "
						+ "t5.name as b_publisher_name, t6.name as s_publisher_name, t1.available_balance, "
						+ "t8.commodity_symbol, t8.commodity_name, t8.publisher_id AS o_publisher_id, t14.name AS o_publisher_name ,t15.phone AS o_publisher_phone, "

						+ "t16.real_maid_fee as maid_fee, t1.origin_amount AS commission, t8.trade_no "

						+ "from p_organization_account_flow t1 "
						+ "LEFT JOIN buy_record t2 on t1.resource_type=1 and t1.resource_id=t2.id "
						+ "LEFT JOIN stock_option_trade t3 on t1.resource_type=3 and t1.resource_id=t3.id "
						+ "LEFT JOIN p_organization t4 on t1.org_id=t4.id "
						+ "LEFT JOIN f_futures_order t8 ON t1.resource_type IN(6,7) AND t8.id = t1.resource_id "
						+ "LEFT JOIN f_futures_contract t9 ON t9.id = t8.contract_id "
						+ "LEFT JOIN f_futures_commodity t10 ON t10.id = t9.commodity_id "
						+ "LEFT JOIN p_futures_agent_price t11 ON t11.commodity_id = t10.id AND t11.org_id = t4.id "
						+ "LEFT JOIN p_futures_agent_price t12 ON t12.commodity_id=t10.id AND t12.org_id = t4.parent_id "
						+ "LEFT JOIN real_name t5 on t5.resource_type=2 and t2.publisher_id=t5.resource_id "
						+ "LEFT JOIN real_name t6 on t6.resource_type=2 and t3.publisher_id=t6.resource_id "
						+ "LEFT JOIN real_name t14 ON t14.resource_type = 2 AND t8.publisher_id = t14.resource_id "
						+ "LEFT JOIN p_futures_commission_audit t16 ON t16.flow_id = t1.id "
						+ "LEFT JOIN publisher t15 ON t15.id = t8.publisher_id "
						+ "LEFT JOIN p_organization t7 on t7.id=" + query.getCurrentOrgId() + " "
						+ "where 1=1 %s %s %s %s %s %s %s %s %s and t1.org_id is not null AND (t16.state IS NULL OR t16.state IN(2,3)) order by t1.occurrence_time DESC, t1.available_balance DESC, t1.amount DESC, t1.org_id ASC limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(), queryTypeCondition, types,
						contractCodeOrName, orgCodeOrName, flowNo, customerName, customerPhone, startTimeCondition,
						endTimeCondition);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("limit"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setFlowNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setOccurrenceTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setOriginAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setRemark", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setResourceId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setResourceTradeNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setResourceType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setOrgId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setbPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setbPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setbStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setbStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setsPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setsPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setsStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setsStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setCycleId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(20), new MethodDesc("setCycleName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(21), new MethodDesc("setOrgCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(22), new MethodDesc("setOrgName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(23), new MethodDesc("setbPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(24), new MethodDesc("setsPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(25), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(26), new MethodDesc("setCommoditySymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(27), new MethodDesc("setCommodityName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(28), new MethodDesc("setoPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(29), new MethodDesc("setoPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(30), new MethodDesc("setoPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(31), new MethodDesc("setAmountRemaid", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(32), new MethodDesc("setCommission", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(33), new MethodDesc("setoTradeNo", new Class<?>[] { String.class }));
		List<OrganizationAccountFlowWithTradeInfoDto> content = dynamicQuerySqlDao
				.execute(OrganizationAccountFlowWithTradeInfoDto.class, sql, setMethodMap);
		BigInteger totalElements = dynamicQuerySqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<FuturesCommissionAuditDto> pageAgentCapitalCommissionAudit(FuturesCommissionAuditQuery query) {
		String queryTypeCondition = " and (t7.level=1 or (t4.id=t7.id or t4.tree_code LIKE CONCAT(t7.tree_code,'%%') and t7.level>1)) ";

		String types = "";
		if (!StringUtil.isEmpty(query.getTypes())) {
			types = " and t1.type in(" + query.getTypes() + ")";
		}
		String commoditySymbol = "";
		if (!StringUtil.isEmpty(query.getCommoditySymbol())) {
			commoditySymbol = " and t10.symbol like '" + query.getCommoditySymbol() + "'";
		}
		String commodityName = "";
		if (!StringUtil.isEmpty(query.getCommodityName())) {
			commodityName = " and t10.name like '" + query.getCommodityName() + "'";
		}
		String states = "";
		if (query.getStates() != null) {
			states = "and t16.state in(" + query.getStates() + ")";
		}

		String sql = String
				.format("select t1.id, t1.amount, t1.flow_no, t1.occurrence_time, t1.origin_amount, t1.remark, t1.resource_id, t1.resource_trade_no, t1.resource_type, t1.type, t1.org_id, "
						+ "t2.publisher_id as b_publisher_id, t2.publisher_phone as b_publisher_phone, t2.stock_code as b_stock_code, t2.stock_name as b_stock_name, "
						+ "t3.publisher_id as s_publisher_id, t3.publisher_phone as s_publisher_phone, t3.stock_code as s_stock_code, t3.stock_name as s_stock_name, "
						+ "t3.cycle_id, t3.cycle_name, t4.code as org_code, t4.name as org_name, "
						+ "t5.name as b_publisher_name, t6.name as s_publisher_name, t1.available_balance, "
						+ "t8.commodity_symbol, t8.commodity_name, t8.publisher_id AS o_publisher_id, t14.name AS o_publisher_name ,t15.phone AS o_publisher_phone, "

						+ "t1.amount as maid_fee, t1.origin_amount AS commission, t8.trade_no, "
						+ "t16.id AS audit_id, t16.real_maid_fee, t16.state, t16.audit_remark, t16.examine_time "
						+ "from p_organization_account_flow t1 "
						+ "LEFT JOIN buy_record t2 on t1.resource_type=1 and t1.resource_id=t2.id "
						+ "LEFT JOIN stock_option_trade t3 on t1.resource_type=3 and t1.resource_id=t3.id "
						+ "LEFT JOIN p_organization t4 on t1.org_id=t4.id "
						+ "LEFT JOIN f_futures_order t8 ON t1.resource_type IN(6,7) AND t8.id = t1.resource_id "
						+ "LEFT JOIN f_futures_contract t9 ON t9.id = t8.contract_id "
						+ "LEFT JOIN f_futures_commodity t10 ON t10.id = t9.commodity_id "
						+ "LEFT JOIN p_futures_agent_price t11 ON t11.commodity_id = t10.id AND t11.org_id = t4.id "
						+ "LEFT JOIN p_futures_agent_price t12 ON t12.commodity_id=t10.id AND t12.org_id = t4.parent_id "
						+ "LEFT JOIN real_name t5 on t5.resource_type=2 and t2.publisher_id=t5.resource_id "
						+ "LEFT JOIN real_name t6 on t6.resource_type=2 and t3.publisher_id=t6.resource_id "
						+ "LEFT JOIN real_name t14 ON t14.resource_type = 2 " + "AND t8.publisher_id = t14.resource_id "
						+ "LEFT JOIN publisher t15 ON t15.id = t8.publisher_id "
						+ "LEFT JOIN p_futures_commission_audit t16 ON t16.flow_id = t1.id "
						+ "LEFT JOIN p_organization t7 on t7.id=" + query.getCurrentOrgId() + " "
						+ "where 1=1 %s %s %s %s %s and t4.level !=1 and t1.org_id is not null order by t16.examine_time DESC limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(), queryTypeCondition, types,
						commoditySymbol, commodityName, states);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("limit"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setFlowNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setOccurrenceTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setOriginAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setRemark", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setResourceId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setResourceTradeNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setResourceType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setOrgId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setbPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setbPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setbStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setbStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setsPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setsPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setsStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setsStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setCycleId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(20), new MethodDesc("setCycleName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(21), new MethodDesc("setOrgCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(22), new MethodDesc("setOrgName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(23), new MethodDesc("setbPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(24), new MethodDesc("setsPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(25), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(26), new MethodDesc("setCommoditySymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(27), new MethodDesc("setCommodityName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(28), new MethodDesc("setoPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(29), new MethodDesc("setoPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(30), new MethodDesc("setoPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(31), new MethodDesc("setAmountRemaid", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(32), new MethodDesc("setCommission", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(33), new MethodDesc("setoTradeNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(34), new MethodDesc("setAuditId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(35), new MethodDesc("setRealMaidFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(36), new MethodDesc("setState", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(37), new MethodDesc("setAuditRemark", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(38), new MethodDesc("setExamineTime", new Class<?>[] { Date.class }));
		List<FuturesCommissionAuditDto> content = dynamicQuerySqlDao.execute(FuturesCommissionAuditDto.class, sql,
				setMethodMap);
		BigInteger totalElements = dynamicQuerySqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}
}
