package com.waben.stock.datalayer.organization.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.organization.business.BindCardBusiness;
import com.waben.stock.datalayer.organization.business.FuturesAgentPriceBusiness;
import com.waben.stock.datalayer.organization.business.FuturesCommodityBusiness;
import com.waben.stock.datalayer.organization.entity.FuturesAgentPrice;
import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationAccount;
import com.waben.stock.datalayer.organization.entity.OrganizationPublisher;
import com.waben.stock.datalayer.organization.entity.SettlementMethod;
import com.waben.stock.datalayer.organization.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.organization.repository.FuturesAgentPriceDao;
import com.waben.stock.datalayer.organization.repository.OrganizationAccountDao;
import com.waben.stock.datalayer.organization.repository.OrganizationDao;
import com.waben.stock.datalayer.organization.repository.OrganizationPublisherDao;
import com.waben.stock.datalayer.organization.repository.SettlementMethodDao;
import com.waben.stock.datalayer.organization.repository.impl.MethodDesc;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.dto.futures.FuturesCommodityDto;
import com.waben.stock.interfaces.dto.organization.FuturesAgentPriceDto;
import com.waben.stock.interfaces.dto.organization.FuturesFowDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDetailDto;
import com.waben.stock.interfaces.dto.organization.OrganizationDto;
import com.waben.stock.interfaces.dto.organization.OrganizationStaDto;
import com.waben.stock.interfaces.dto.organization.TradingFowDto;
import com.waben.stock.interfaces.dto.organization.TreeNode;
import com.waben.stock.interfaces.dto.publisher.BindCardDto;
import com.waben.stock.interfaces.enums.BindCardResourceType;
import com.waben.stock.interfaces.enums.CapitalFlowType;
import com.waben.stock.interfaces.enums.OrganizationState;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.form.organization.OrganizationForm;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.organization.FuturesFowQuery;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationQuery;
import com.waben.stock.interfaces.pojo.query.organization.OrganizationStaQuery;
import com.waben.stock.interfaces.pojo.query.organization.TradingFowQuery;
import com.waben.stock.interfaces.util.CopyBeanUtils;
import com.waben.stock.interfaces.util.StringUtil;

/**
 * 机构 Service
 *
 * @author luomengan
 */
@Service
public class OrganizationService {

	@Autowired
	private OrganizationDao organizationDao;

	@Autowired
	private OrganizationAccountDao accountDao;

	@Autowired
	private BindCardBusiness bindCardBusiness;

	@Autowired
	private DynamicQuerySqlDao sqlDao;

	@Autowired
	public OrganizationPublisherDao orgPublisherDao;

	@Autowired
	public OrganizationService organizationService;

	@Autowired
	private SettlementMethodDao settlementMethodDao;

	@Autowired
	private FuturesAgentPriceDao agentPriceDao;

	@Autowired
	private FuturesAgentPriceBusiness agentPriceBusiness;

	@Autowired
	private FuturesCommodityBusiness commodityBusiness;

	// @Autowired
	// private StockOptionTradeBusiness tradeBusiness;

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public Organization getOrganizationInfo(Long id) {
		return organizationDao.retrieve(id);
	}

	@Transactional
	public Organization addOrganization(Organization organization) {
		return organizationDao.create(organization);
	}

	@Transactional
	public Organization addOrganization(OrganizationForm orgForm) {
		Organization parent = organizationDao.retrieve(orgForm.getParentId());
		int level = 1;
		if (parent == null) {
			throw new ServiceException(ExceptionConstant.ORGANIZATION_NOTEXIST_EXCEPTION);
		} else {
			level = parent.getLevel() + 1;
		}
		// 判断机构名称是否存在
		List<Organization> checkName = organizationDao.retriveByName(orgForm.getName());
		if (checkName != null && checkName.size() > 0) {
			throw new ServiceException(ExceptionConstant.ORGNAME_EXIST_EXCEPTION);
		}
		// 生成树结构代码
		List<Organization> childList = organizationDao.listByParentOrderByCodeDesc(parent);
		String treeCode = parent.getTreeCode();
		if (childList != null && childList.size() > 0) {
			Organization max = childList.get(0);
			String suffix = max.getTreeCode().substring(treeCode.length());
			Long seria = Long.parseLong(suffix) + 1;
			String seriaStr = seria.toString();
			if (seriaStr.length() < suffix.length()) {
				int lack = suffix.length() - seriaStr.length();
				for (int i = 0; i < lack; i++) {
					seriaStr = "0" + seriaStr;
				}
			}
			treeCode += seriaStr;
		} else {
			treeCode += "001";
		}
		// 生成机构代码
		Organization newest = organizationDao.getNewestOrg();
		Integer currentIndex = 0;
		if (newest != null) {
			if (!newest.getCode().startsWith("01")) {
				currentIndex = Integer.parseInt(newest.getCode());
			}
		}
		String code = "";
		while (true) {
			currentIndex += 1;
			code = String.valueOf(currentIndex);
			if (code.length() < 3) {
				int codeLength = code.length();
				for (int i = 0; i < 3 - codeLength; i++) {
					code = "0" + code;
				}
			}
			// 验证该code是否已经存在
			Organization checkCode = organizationDao.retrieveByCode(code);
			if (checkCode == null) {
				break;
			}
		}
		// 保存机构
		Organization org = new Organization();
		org.setCode(code);
		org.setTreeCode(treeCode);
		org.setCreateTime(new Date());
		org.setLevel(level);
		org.setName(orgForm.getName());
		org.setParent(parent);
		org.setRemark(orgForm.getRemark());
		org.setState(OrganizationState.NORMAL);
		org = organizationDao.create(org);
		// 创建机构账户
		OrganizationAccount account = new OrganizationAccount();
		account.setAvailableBalance(BigDecimal.ZERO);
		account.setBalance(BigDecimal.ZERO);
		account.setFrozenCapital(BigDecimal.ZERO);
		account.setOrg(org);
		account.setState(1);
		account.setUpdateTime(new Date());
		accountDao.create(account);
		return org;
	}

	@Transactional
	public Organization modifyOrganization(Organization organization) {
		return organizationDao.update(organization);
	}

	@Transactional
	public void deleteOrganization(Long id) {
		organizationDao.delete(id);
	}

	@Transactional
	public void deleteOrganizations(String ids) {
		if (ids != null) {
			String[] idArr = ids.split(",");
			for (String id : idArr) {
				if (!"".equals(id.trim())) {
					organizationDao.delete(Long.parseLong(id.trim()));
				}
			}
		}
	}

	public Page<Organization> organizations(int page, int limit) {
		return organizationDao.page(page, limit);
	}

	public List<Organization> list() {
		return organizationDao.list();
	}

	public String queryChildOrgId(Long orgId) {
		return organizationDao.queryChlidOrgId(orgId);
	}

	public Page<Organization> pagesByQuery(final OrganizationQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<Organization> pages = organizationDao.page(new Specification<Organization>() {
			@Override
			public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (query.isOnlyLoginOrg()) {
					predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), query.getLoginOrgId()));
				} else {
					if (query.getCode() != null && !"".equals(query.getCode().trim())) {
						predicateList.add(
								criteriaBuilder.like(root.get("code").as(String.class), "%" + query.getCode() + "%"));
					}
					if (query.getState() != null && !"".equals(query.getState().trim())
							&& !"0".equals(query.getState().trim())) {
						predicateList.add(criteriaBuilder.equal(root.get("state").as(OrganizationState.class),
								OrganizationState.getByIndex(query.getState().trim())));
					}
					if (query.getParentId() != null && query.getParentId() != 0) {
						Join<Organization, Organization> parentJoin = root.join("parent", JoinType.LEFT);
						predicateList
								.add(criteriaBuilder.equal(parentJoin.get("id").as(Long.class), query.getParentId()));
					}
					if (query.getTreeCode() != null && !"".equals(query.getTreeCode().trim())) {
						predicateList.add(criteriaBuilder.like(root.get("treeCode").as(String.class),
								"%" + query.getTreeCode() + "%"));
					}
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("level").as(Integer.class)),
						criteriaBuilder.asc(root.get("createTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public Page<Organization> pagesByOrgQuery(final OrganizationQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<Organization> pages = organizationDao.page(new Specification<Organization>() {
			@Override
			public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (query.isOnlyLoginOrg()) {
					predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), query.getLoginOrgId()));
				}
				if (query.getCode() != null && !"".equals(query.getCode().trim())) {
					predicateList
							.add(criteriaBuilder.like(root.get("code").as(String.class), "%" + query.getCode() + "%"));
				}
				if (!StringUtil.isEmpty(query.getOrganizationName())) {
					predicateList.add(criteriaBuilder.like(root.get("name").as(String.class),
							"%" + query.getOrganizationName() + "%"));
				}
				if (query.getState() != null && !"".equals(query.getState().trim())
						&& !"0".equals(query.getState().trim())) {
					predicateList.add(criteriaBuilder.equal(root.get("state").as(OrganizationState.class),
							OrganizationState.getByIndex(query.getState().trim())));
				}
				if (query.getParentId() != null && query.getParentId() != 0) {
					Join<Organization, Organization> parentJoin = root.join("parent", JoinType.LEFT);
					predicateList.add(criteriaBuilder.equal(parentJoin.get("id").as(Long.class), query.getParentId()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("level").as(Integer.class)),
						criteriaBuilder.asc(root.get("createTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public List<Organization> listByParentId(Long parentId) {
		Organization parent = null;
		if (parentId.longValue() > 0) {
			parent = organizationDao.retrieve(parentId);
			if (parent == null) {
				return new ArrayList<Organization>();
			}
		}
		return organizationDao.listByParent(parent);
	}

	public List<TreeNode> adminTree(Long orgId) {
		List<TreeNode> result = new ArrayList<>();
		// // 根节点添加
		// TreeNode root = new TreeNode();
		// root.setId(0L);
		// root.setLevel(0);
		// root.setName("所有机构");
		// root.setOpen(true);
		// root.setPid(-1L);
		// result.add(root);
		// 添加所有数据栏目
		Organization org = null;
		if (orgId != null) {
			// org = organizationDao.retrieve(orgId);
			// result.add(new TreeNode(org.getId(), org.getName(),
			// org.getLevel(), 0L, true));
			// result.addAll(loopTree(org));
			org = organizationDao.retrieve(orgId);
			TreeNode node = new TreeNode(org.getId(), org.getName(), org.getLevel(), 0L, true);
			wrapTreeNode(node);
			result.add(node);
		}
		return result;
	}

	private void wrapTreeNode(TreeNode node) {
		List<TreeNode> children = childrenTreeNode(node.getId());
		node.setChildren(children);
		if (children != null && children.size() > 0) {
			for (TreeNode innerNode : children) {
				wrapTreeNode(innerNode);
			}
		}
	}

	private List<TreeNode> childrenTreeNode(Long orgId) {
		List<TreeNode> result = new ArrayList<>();
		Organization parent = organizationDao.retrieve(orgId);
		List<Organization> childList = organizationDao.listByParent(parent);
		if (childList != null && childList.size() > 0) {
			for (Organization org : childList) {
				result.add(new TreeNode(org.getId(), org.getName(), org.getLevel(),
						org.getParent() != null ? org.getParent().getId() : 0, false));
			}
		}
		return result;
	}

	public OrganizationDetailDto detail(Long orgId) {

		OrganizationDetailDto result = null;
		Organization org = organizationDao.retrieve(orgId);
		if (org != null) {
			result = CopyBeanUtils.copyBeanProperties(OrganizationDetailDto.class, org, false);
		}

		return result;
	}

	@Transactional
	public Organization modifyName(Long id, String name, BigDecimal billCharge, Integer settlementType) {
		Organization org = organizationDao.retrieve(id);
		org.setName(name);
		if (billCharge != null) {
			org.setBillCharge(billCharge);
		}
		Organization otion = organizationDao.update(org);
		if (settlementType != null) {
			SettlementMethod smethod = new SettlementMethod();
			List<SettlementMethod> menthod = settlementMethodDao.list();
			// if (menthod.get(0).getSettlementType() != settlementType) {
			// Integer count = tradeBusiness.countStockOptionTradeState();
			// if (count > 0) {
			// throw new
			// ServiceException(ExceptionConstant.SETTLEMENT_METHOD_EXCEPITON);
			// }
			// }
			if (menthod != null && menthod.size() > 0) {
				smethod = menthod.get(0);
			}
			smethod.setSettlementType(settlementType);
			settlementMethodDao.create(smethod);
		}
		return otion;
	}

	public BindCardDto getBindCard(Long orgId) {
		List<BindCardDto> bindCardList = bindCardBusiness.listsByOrgId(orgId);
		if (bindCardList != null && bindCardList.size() > 0) {
			return bindCardList.get(bindCardList.size() - 1);
		}
		return null;
	}

	public BindCardDto bindCard(Long orgId, BindCardDto bindCardDto) {
		if (bindCardDto.getId() != null && bindCardDto.getId() > 0) {
			if (bindCardDto.getResourceType() == BindCardResourceType.ORGANIZATION
					&& bindCardDto.getResourceId().longValue() == orgId.longValue()) {
				return bindCardBusiness.revision(bindCardDto);
			} else {
				throw new ServiceException(ExceptionConstant.UNKNOW_EXCEPTION, "错误的绑卡信息!");
			}
		} else {
			bindCardDto.setResourceType(BindCardResourceType.ORGANIZATION);
			bindCardDto.setResourceId(orgId);
			return bindCardBusiness.save(bindCardDto);
		}
	}

	public Organization findByCode(String code) {
		return organizationDao.retrieveByCode(code);
	}

	public Page<Organization> pagesAgentByQuery(final OrganizationQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<Organization> pages = organizationDao.page(new Specification<Organization>() {
			@Override
			public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (query.isOnlyLoginOrg()) {
					predicateList.add(criteriaBuilder.equal(root.get("id").as(Long.class), query.getLoginOrgId()));
				} else {
					if (query.getCode() != null && !"".equals(query.getCode().trim())) {
						predicateList.add(
								criteriaBuilder.like(root.get("code").as(String.class), "%" + query.getCode() + "%"));
					}
					if (query.getState() != null && !"".equals(query.getState().trim())
							&& !"0".equals(query.getState().trim())) {
						predicateList.add(criteriaBuilder.equal(root.get("state").as(OrganizationState.class),
								OrganizationState.getByIndex(query.getState().trim())));
					}
					if (query.getParentId() != null && query.getParentId() != 0) {
						Join<Organization, Organization> parentJoin = root.join("parent", JoinType.LEFT);
						predicateList
								.add(criteriaBuilder.equal(parentJoin.get("id").as(Long.class), query.getParentId()));
					}
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.asc(root.get("level").as(Integer.class)),
						criteriaBuilder.asc(root.get("createTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public Page<OrganizationDetailDto> adminPagesByQuery(OrganizationQuery query) {
		Organization org = organizationDao.retrieve(query.getOrgId());
		String levelName = "";
		if (org != null) {
			if (org.getLevel() != 1) {
				levelName = " and p.`level` = " + org.getLevel() + ""; // 根据登录用户的代理商id获取该代理商的level然后获取该用户的直属代理商
			} else {
				levelName = " and p.id NOT IN(1)";
			}
		}
		String codeName = "";
		if (!StringUtil.isEmpty(query.getCode())) {
			codeName = " and p.`code` = " + query.getCode() + "";
		}
		String organizationName = "";
		if (!StringUtil.isEmpty(query.getOrganizationName())) {
			organizationName = " and p.`name` LIKE '%" + query.getOrganizationName() + "%'";
		}

		String sql = String.format(
				"SELECT" + " p.id," + " p.`code`," + " p.`name`," + " p.`level`," + " p.create_time,"
						+ " p2.`name` AS agnetName," + " pc.balance," + " p2.phone," + " p.state" + " FROM"
						+ " p_organization p" + " LEFT JOIN p_organization_publisher p1 ON p1.org_code = p.`code`"
						+ " LEFT JOIN p_organization_account pc ON pc.org_id = p.id"
						+ " LEFT JOIN bind_card p2 ON p2.resource_type = 2" + " AND p2.resource_id = p.id"
						+ " where 1=1 %s %s %s " + " GROUP BY" + " p.id" + " ORDER BY p.create_time DESC limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				levelName, codeName, organizationName);
		String countSql = "select count(*) FROM ( " + sql.substring(sql.indexOf("SELECT"), sql.indexOf("limit"))
				+ ") s";
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setLevel", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setCreateTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setAgnetName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setState", new Class<?>[] { OrganizationState.class }));
		List<OrganizationDetailDto> contentList = new ArrayList<OrganizationDetailDto>();
		List<OrganizationDetailDto> content = sqlDao.execute(OrganizationDetailDto.class, sql, setMethodMap);
		for (OrganizationDetailDto organizationDetailDto : content) {
			String publisherSql = "select count(*) from publisher t1"
					+ " INNER JOIN p_organization_publisher t2 on t1.id=t2.publisher_id"
					+ " LEFT JOIN p_organization t3 on t2.org_code=t3.code" + " LEFT JOIN p_organization t4 on t4.id= "
					+ organizationDetailDto.getId() + "" + " where (t4.level=1 or (t3.id=t4.id or t3.parent_id=t4.id))";
			BigInteger publisher = sqlDao.executeComputeSql(publisherSql);
			organizationDetailDto.setPublisherCount(publisher != null ? publisher.intValue() : 0);

			String childSql = "select count(*) from p_organization t1" + " LEFT JOIN p_organization t2 on t2.id="
					+ organizationDetailDto.getId() + "" + " where (t2.level=1 or (t2.level>1 and t1.parent_id=t2.id))";

			BigInteger child = sqlDao.executeComputeSql(childSql);
			organizationDetailDto.setChildOrgCount(child != null ? child.intValue() : 0);
			contentList.add(organizationDetailDto);
		}
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(contentList, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<OrganizationStaDto> adminStaPagesByQuery(OrganizationStaQuery query) {
		String queryTypeCondition = "";
		if (query.getQueryType() != null && query.getQueryType() == 1) {
			queryTypeCondition = " and t4.id=" + query.getCurrentOrgId() + " ";
		} else {
			queryTypeCondition = " and t4.parent_id=" + query.getCurrentOrgId() + " ";
		}
		String orgCodeCondition = "";
		if (!StringUtil.isEmpty(query.getOrgCode())) {
			orgCodeCondition = " and t4.code like '%" + query.getOrgCode() + "%' ";
		}
		String orgNameCondition = "";
		if (!StringUtil.isEmpty(query.getOrgName())) {
			orgNameCondition = " and t4.name like '%" + query.getOrgName() + "%' ";
		}
		String sql = String.format(
				"select t4.id, t4.parent_id, t4.code, t4.name, t4.level, t4.state, t4.create_time, t5.promotion_count,  "
						+ "IF(t6.pid is null, 0, t6.children_count) as children_count, IFNULL(t7.available_balance, 0) as available_balance, t8.name as bind_name, t8.phone as bing_phone "
						+ "from p_organization t4 LEFT JOIN "
						+ "(select t1.id, IF(t2.id is null, 0, count(t1.id)) as promotion_count from p_organization t1 LEFT JOIN p_organization_publisher t2 on t2.tree_code LIKE CONCAT(t1.tree_code, '%%') group by t1.id) as t5 on t4.id=t5.id  "
						+ "LEFT JOIN "
						+ "(select t1.id AS pid, (count(t2.id)-1) as children_count from p_organization t1 LEFT JOIN p_organization t2 on t2.tree_code like CONCAT(t1.tree_code, '%%') GROUP BY t1.id) as t6 on t4.id=t6.pid "
						+ "LEFT JOIN p_organization_account t7 on t4.id=t7.org_id  "
						+ "LEFT JOIN bind_card t8 on t8.resource_type=2 and t8.resource_id=t4.id "
						+ "where 1=1 %s %s %s order by t4.level desc, t4.create_time asc limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				queryTypeCondition, orgCodeCondition, orgNameCondition);
		String countSql = "select count(*) " + sql.substring(sql.indexOf("from"), sql.indexOf("limit"));
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setParentId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setLevel", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setState", new Class<?>[] { OrganizationState.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setCreateTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setPromotionCount", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setChildrenCount", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setBindName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setBingPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setWsType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setBillCharge", new Class<?>[] { BigDecimal.class }));
		List<OrganizationStaDto> content = sqlDao.execute(OrganizationStaDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<FuturesFowDto> futuresFowPageByQuery(FuturesFowQuery query) {
		String customerNameQuery = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			customerNameQuery = " and t4.name like '%" + query.getPublisherName() + "%'";
		}

		String tradingNumberQuery = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			tradingNumberQuery = " and t5.phone like '%" + query.getPublisherPhone() + "%'";
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
		if (query.getFlowTypes() != null && !"".equals(query.getFlowTypes()) && !"0".equals(query.getFlowTypes())) {
			typeCondition = " and t1.type in(" + query.getFlowTypes() + ") ";
		}
		String contractCodeQuery = "";
		if (!StringUtil.isEmpty(query.getContractCodeOrName())) {
			contractCodeQuery = " and (t3.symbol like '%" + query.getContractCodeOrName() + "%' or t3.name like '%"
					+ query.getContractCodeOrName() + "%')";
		}

		String agentCodeNameQuery = "";
		if (!StringUtil.isEmpty(query.getOrgCodeOrName())) {
			agentCodeNameQuery = " and (t10.code like '%" + query.getOrgCodeOrName() + "%' or t10.name like '%"
					+ query.getOrgCodeOrName() + "%')";
		}

		String treeCodeQuery = "";
		if (!StringUtil.isEmpty(query.getTreeCode())) {
			treeCodeQuery = " and t10.tree_code like '" + query.getTreeCode() + "%'";
		}

		String commoditySymbolCondition = "";
		if (!StringUtil.isEmpty(query.getSymbol())) {
			commoditySymbolCondition = " and t11.commodity_symbol like '%" + query.getSymbol() + "%' ";
		}
		String commodityNameCondition = "";
		if (!StringUtil.isEmpty(query.getCommodityName())) {
			commodityNameCondition = " and t11.commodity_name like '%" + query.getCommodityName() + "%' ";
		}

		String sql = String.format(
				"select t1.id, t1.amount, t1.flow_no, t1.occurrence_time, t1.publisher_id, t5.phone, t1.remark, t1.type, t4.name, "
						+ "t2.stock_code as b_stock_code, t2.stock_name as b_stock_name, t11.commodity_symbol as commodity_symbol, t11.commodity_name as commodity_name, t11.contract_no as contract_no, "
						+ "t3.stock_code as s_stock_code, t3.stock_name as s_stock_name, t6.type as payment_type, t7.bank_card, t8.bank_name, t1.available_balance, t5.is_test, "
						+ " t10.code AS agentCode,t10.name AS agentName   from capital_flow t1 "
						+ " LEFT JOIN buy_record t2 on t1.extend_type=1 and t1.extend_id=t2.id "
						+ " LEFT JOIN stock_option_trade t3 on t1.extend_type=3 and t1.extend_id=t3.id "
						+ " LEFT JOIN real_name t4 on t4.resource_type=2 and t1.publisher_id=t4.resource_id "
						+ " LEFT JOIN publisher t5 on t5.id=t1.publisher_id "
						+ " LEFT JOIN payment_order t6 on t1.extend_type=4 and t1.extend_id=t6.id"
						+ " LEFT JOIN withdrawals_order t7 on t1.extend_type=5 and t1.extend_id=t7.id "
						+ " LEFT JOIN bind_card t8 on t7.bank_card=t8.bank_card"
						+ " LEFT JOIN p_organization_publisher t9 ON t9.publisher_id = t5.id"
						+ " LEFT JOIN p_organization t10 ON t10.code = t9.org_code"
						+ " LEFT JOIN f_futures_overnight_record t12 on t1.extend_type=7 and t1.extend_id=t12.id "
						+ " LEFT JOIN f_futures_order t11 on (t1.extend_type=6 and t1.extend_id=t11.id ) or (t1.extend_type=7 and t11.id=t12.order_id) "
						+ " WHERE 1=1 and t10.id is not null  %s %s %s %s %s %s %s %s %s %s order by t1.occurrence_time desc limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				customerNameQuery, tradingNumberQuery, startTimeCondition, endTimeCondition, typeCondition,
				contractCodeQuery, agentCodeNameQuery, treeCodeQuery, commodityNameCondition, commoditySymbolCondition);
		String countSql = "select count(*) from (" + sql.substring(0, sql.indexOf("limit")) + ") c";
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setFlowNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setOccurrenceTime", new Class<?>[] { Date.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setPublisherId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(5), new MethodDesc("setPublisherPhone", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(6), new MethodDesc("setRemark", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setType", new Class<?>[] { CapitalFlowType.class }));
		setMethodMap.put(new Integer(8), new MethodDesc("setPublisherName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(9), new MethodDesc("setbStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setbStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(11), new MethodDesc("setCommoditySymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(12), new MethodDesc("setCommodityName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(13), new MethodDesc("setContractNo", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(14), new MethodDesc("setsStockCode", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(15), new MethodDesc("setsStockName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(16), new MethodDesc("setPaymentType", new Class<?>[] { Integer.class }));
		setMethodMap.put(new Integer(17), new MethodDesc("setBankCard", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(18), new MethodDesc("setBankName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(19), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(20), new MethodDesc("setIsTest", new Class<?>[] { Boolean.class }));
		setMethodMap.put(new Integer(21), new MethodDesc("setAgentCode", new Class<?>[] { String.class })); // 代理商代码
		setMethodMap.put(new Integer(22), new MethodDesc("setAgentCodeName", new Class<?>[] { String.class }));// 代理商名称
		List<FuturesFowDto> content = sqlDao.execute(FuturesFowDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public Page<TradingFowDto> tradingFowPageByQuery(TradingFowQuery query) {

		String customerNameQuery = "";
		if (!StringUtil.isEmpty(query.getPublisherName())) {
			customerNameQuery = " and t4.name like '%" + query.getPublisherName() + "%'";
		}

		String tradingNumberQuery = "";
		if (!StringUtil.isEmpty(query.getPublisherPhone())) {
			tradingNumberQuery = " and t5.phone like '%" + query.getPublisherPhone() + "%'";
		}
		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			startTimeCondition = " and t1.occurrence_time>='" + sdf.format(query.getStartTime()) + "' ";
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			endTimeCondition = " and t1.occurrence_time<'" + sdf.format(query.getEndTime()) + "' ";
		}
		String typeQuery = "";
		if (!StringUtil.isEmpty(query.getTypes())) {
			typeQuery = " and t1.type in(" + query.getTypes() + ") ";
		}
		String stockCodeQuery = "";
		if (!StringUtil.isEmpty(query.getStockCodeOrName())) {
			stockCodeQuery = " and (t3.stock_code like '%" + query.getStockCodeOrName() + "%' or t3.stock_name like '%"
					+ query.getStockCodeOrName() + "%')";
		}

		String agentCodeNameQuery = "";
		if (!StringUtil.isEmpty(query.getOrgCodeOrName())) {
			agentCodeNameQuery = " and (t10.code like '%" + query.getOrgCodeOrName() + "%' or t10.name like '%"
					+ query.getOrgCodeOrName() + "%')";
		}

		String sql = String.format(
				"select t1.id, t4.name as publisher_name, t5.phone, t1.flow_no, t1.occurrence_time, t1.type, t1.amount, t1.available_balance, "
						+ " IF(t3.stock_code IS NULL,t2.stock_code,t3.stock_code) AS stock_code,  IF(t3.stock_name IS NULL,t2.stock_name,t3.stock_name) AS stcode_name,"
						+ " t10.code,t10.name AS agentName " + " from capital_flow t1 "
						+ " LEFT JOIN buy_record t2 on t1.extend_type=1 and t1.extend_id=t2.id "
						+ " LEFT JOIN stock_option_trade t3 on t1.extend_type=3 and t1.extend_id=t3.id "
						+ " LEFT JOIN real_name t4 on t4.resource_type=2 and t1.publisher_id=t4.resource_id "
						+ " LEFT JOIN publisher t5 on t5.id=t1.publisher_id "
						+ " LEFT JOIN payment_order t6 on t1.extend_type=4 and t1.extend_id=t6.id"
						+ " LEFT JOIN withdrawals_order t7 on t1.extend_type=5 and t1.extend_id=t7.id "
						+ " LEFT JOIN bind_card t8 on t7.bank_card=t8.bank_card"
						+ " LEFT JOIN p_organization_publisher t9 ON t9.publisher_id = t5.id"
						+ " LEFT JOIN p_organization t10 ON t10.code = t9.org_code"
						+ " LEFT JOIN p_organization t11 on t11.id=" + query.getCurrentOrgId() + ""
						+ " WHERE 1=1 and t10.id is not null and (t11.level=1 or (t11.level>1 and (t11.id=t10.id or t11.id=t10.parent_id))) %s %s %s %s %s %s %s order by t1.occurrence_time desc limit "
						+ query.getPage() * query.getSize() + "," + query.getSize(),
				customerNameQuery, tradingNumberQuery, startTimeCondition, endTimeCondition, typeQuery, stockCodeQuery,
				agentCodeNameQuery);
		String countSql = "select count(*) from (" + sql.substring(0, sql.indexOf("limit")) + ") c";
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setId", new Class<?>[] { Long.class })); // ID
		setMethodMap.put(new Integer(1), new MethodDesc("setCustomerName", new Class<?>[] { String.class })); // 客户名称
		setMethodMap.put(new Integer(2), new MethodDesc("setTradingNumber", new Class<?>[] { String.class })); // 银行卡号
		setMethodMap.put(new Integer(3), new MethodDesc("setFlowNo", new Class<?>[] { String.class })); // 流水号
		setMethodMap.put(new Integer(4), new MethodDesc("setOccurrenceTime", new Class<?>[] { Date.class })); // 交易时间
		setMethodMap.put(new Integer(5), new MethodDesc("setType", new Class<?>[] { CapitalFlowType.class })); // 交易类型
		setMethodMap.put(new Integer(6), new MethodDesc("setAmount", new Class<?>[] { BigDecimal.class }));// 金额
		setMethodMap.put(new Integer(7), new MethodDesc("setAvailableBalance", new Class<?>[] { BigDecimal.class })); // 当前可用余额
		setMethodMap.put(new Integer(8), new MethodDesc("setStockCode", new Class<?>[] { String.class })); // 股票代码
		setMethodMap.put(new Integer(9), new MethodDesc("setMarkedStock", new Class<?>[] { String.class }));// 股票名称
		setMethodMap.put(new Integer(10), new MethodDesc("setAgentCode", new Class<?>[] { String.class })); // 代理商代码
		setMethodMap.put(new Integer(11), new MethodDesc("setAgentCodeName", new Class<?>[] { String.class }));// 代理商名称
		List<TradingFowDto> content = sqlDao.execute(TradingFowDto.class, sql, setMethodMap);
		BigInteger totalElements = sqlDao.executeComputeSql(countSql);
		return new PageImpl<>(content, new PageRequest(query.getPage(), query.getSize()),
				totalElements != null ? totalElements.longValue() : 0);
	}

	public List<Organization> findAll() {
		return organizationDao.list();
	}

	public List<FuturesAgentPriceDto> getListByFuturesAgentPrice(Long orgId) {
		String sql = String
				.format("SELECT c1.id AS commodity_id ,c1.symbol,c1.`name`,c2.id , c2.cost_reserve_fund, c2.cost_openwind_service_fee,c2.cost_unwind_service_fee,c2.cost_deferred_fee,c2.sale_openwind_service_fee,c2.sale_unwind_service_fee,c2.sale_deferred_fee FROM f_futures_commodity c1 LEFT JOIN p_futures_agent_price c2 ON c2.commodity_id = c1.id AND c2.org_id="
						+ orgId);
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setCommodityId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setCommoditySymbol", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setCommodityName", new Class<?>[] { String.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setId", new Class<?>[] { Long.class }));
		setMethodMap.put(new Integer(4), new MethodDesc("setCostReserveFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(5),
				new MethodDesc("setCostOpenwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(6),
				new MethodDesc("setCostUnwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(7), new MethodDesc("setCostDeferredFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(8),
				new MethodDesc("setSaleOpenwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(9),
				new MethodDesc("setSaleUnwindServiceFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(10), new MethodDesc("setSaleDeferredFee", new Class<?>[] { BigDecimal.class }));
		List<FuturesAgentPriceDto> content = sqlDao.execute(FuturesAgentPriceDto.class, sql, setMethodMap);
		return content;
	}

	@Transactional
	public Integer saveFuturesAgentPrice(List<FuturesAgentPriceDto> futuresAgentPricedto) {
		List<FuturesAgentPrice> futuresAgentPrice = CopyBeanUtils.copyListBeanPropertiesToList(futuresAgentPricedto,
				FuturesAgentPrice.class);
		if (futuresAgentPrice != null && futuresAgentPrice.size() > 0) {
			Long orgId = futuresAgentPrice.get(0).getOrgId();
			OrganizationDto organization = agentPriceBusiness.fetchByOrgId(orgId);
			for (int i = 0; i < futuresAgentPrice.size(); i++) {
				// 获取当前期货代理价格数据
				FuturesAgentPrice currentPrice = futuresAgentPrice.get(i);
				// 获取上级期货代理价格数据
				FuturesAgentPrice agentPrice = agentPriceDao.findByCommodityIdAndOrgId(currentPrice.getCommodityId(),
						organization.getParentId());
				// 获取期货品种信息
				FuturesCommodityDto contractDto = agentPriceBusiness
						.getFuturesByCommodityId(currentPrice.getCommodityId());
				// 判断是否期货代理价格是否合法
				currentPrice = saveAgent(currentPrice, agentPrice, contractDto, organization);
				currentPrice.setCreateTime(new Date());
				agentPriceDao.create(currentPrice);
			}
			return 1;
		}
		return null;
	}

	/**
	 * 判断是否期货代理价格是否合法
	 * 
	 * @param currentPrice
	 *            当前期货代理价格数据
	 * @param agentPrice
	 *            上级期货代理价格数据
	 * @param contractDto
	 *            合约数据
	 * @param organization
	 *            代理商数据
	 */
	public FuturesAgentPrice saveAgent(FuturesAgentPrice currentPrice, FuturesAgentPrice agentPrice,
			FuturesCommodityDto contractDto, OrganizationDto organization) {
		if (contractDto == null) {
			// 该合约不存在
			throw new ServiceException(ExceptionConstant.CONTRACT_DOESNOT_EXIST_EXCEPTION);
		}
		if (organization.getLevel() <= 2) {
			if (contractDto.getPerUnitReserveFund() == null || contractDto.getOpenwindServiceFee() == null
					|| contractDto.getUnwindServiceFee() == null
					|| contractDto.getOvernightPerUnitDeferredFee() == null) {
				// 没有设置全局成本价
				throw new ServiceException(ExceptionConstant.NOT_GLOBAL_COST_PRICE_ISSET_EXCEPTION);
			}

			if (currentPrice.getCostReserveFund() == null) {
				currentPrice.setCostReserveFund(contractDto.getPerUnitReserveFund());
			} else if (currentPrice.getCostReserveFund() == BigDecimal.ZERO) {
				currentPrice.setCostReserveFund(null);
			} /*
				 * else if
				 * (currentPrice.getCostReserveFund().compareTo(contractDto.
				 * getPerUnitReserveFund()) < 0) { // 成本保证金不能比全局设置的低 throw new
				 * ServiceException(ExceptionConstant.
				 * COST_MARGIN_CANNOT_LOWER_THAN_GLOBAL_SETTING_EXCEPTION, new
				 * Object[] { contractDto.getName(),
				 * contractDto.getPerUnitReserveFund() }); }
				 */
			if (currentPrice.getCostOpenwindServiceFee() == null) {
				currentPrice.setCostOpenwindServiceFee(contractDto.getOpenwindServiceFee());
			} else if (currentPrice.getCostOpenwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setCostOpenwindServiceFee(null);
			} /*
				 * else if (currentPrice.getCostOpenwindServiceFee().compareTo(
				 * contractDto.getOpenwindServiceFee()) < 0) { //
				 * 成本开仓手续费不能比全局设置的低 throw new ServiceException(
				 * ExceptionConstant.
				 * COST_OPENING_WAREHOUSE_SHOULDNOT_LOWER_OVERALL_SETTING_EXCEPTION,
				 * new Object[] { contractDto.getName(),
				 * contractDto.getOpenwindServiceFee() }); }
				 */
			if (currentPrice.getSaleOpenwindServiceFee() == null) {
				currentPrice.setSaleOpenwindServiceFee(currentPrice.getCostOpenwindServiceFee());
			} else if (currentPrice.getSaleOpenwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setSaleOpenwindServiceFee(null);
			} else if (currentPrice.getCostOpenwindServiceFee()
					.compareTo(currentPrice.getSaleOpenwindServiceFee()) > 0) {
				// 销售开仓手续费不能比成本开仓手续费的低
				throw new ServiceException(ExceptionConstant.SALES_OPENINGFEE_CANNOT_LOWER_COST_OPENINGFEE_EXCEPTION,
						new Object[] { contractDto.getName() });
			}
			if (currentPrice.getCostUnwindServiceFee() == null) {
				currentPrice.setCostUnwindServiceFee(contractDto.getUnwindServiceFee());
			} else if (currentPrice.getCostUnwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setCostUnwindServiceFee(null);
			} /*
				 * else if
				 * (currentPrice.getCostUnwindServiceFee().compareTo(contractDto
				 * .getUnwindServiceFee()) < 0) { // 成本平仓手续费不能比全局设置的低 throw new
				 * ServiceException(ExceptionConstant.
				 * COST_ISNOT_LOWER_OVERALL_SETTING_EXCEPTION, new Object[] {
				 * contractDto.getName(), contractDto.getUnwindServiceFee() });
				 * }
				 */
			if (currentPrice.getSaleUnwindServiceFee() == null) {
				currentPrice.setSaleUnwindServiceFee(currentPrice.getCostUnwindServiceFee());
			} else if (currentPrice.getSaleUnwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setSaleUnwindServiceFee(null);
			} else if (currentPrice.getCostUnwindServiceFee().compareTo(currentPrice.getSaleUnwindServiceFee()) > 0) {
				// 销售平仓手续费不能比成本开仓手续费的低
				throw new ServiceException(ExceptionConstant.SALES_CLOSINGFEE_CANNOT_LOWER_COST_OPENINGFEE_EXCEPTION,
						new Object[] { contractDto.getName() });
			}
			if (currentPrice.getCostDeferredFee() == null) {
				currentPrice.setCostDeferredFee(contractDto.getOvernightPerUnitDeferredFee());
			} else if (currentPrice.getCostDeferredFee() == BigDecimal.ZERO) {
				currentPrice.setCostDeferredFee(null);
			} /*
				 * else if
				 * (currentPrice.getCostDeferredFee().compareTo(contractDto.
				 * getOvernightPerUnitDeferredFee()) < 0) { // 成本递延费不能比全局设置的低
				 * throw new ServiceException( ExceptionConstant.
				 * COST_DEFERREDFEE_SHOULD_NOTBE_LOWER_GLOBAL_SETTING_EXCEPTION,
				 * new Object[] { contractDto.getName(),
				 * contractDto.getOvernightPerUnitDeferredFee() }); }
				 */
			if (currentPrice.getSaleDeferredFee() == null) {
				currentPrice.setSaleDeferredFee(currentPrice.getCostDeferredFee());
			} else if (currentPrice.getSaleDeferredFee() == BigDecimal.ZERO) {
				currentPrice.setSaleDeferredFee(null);
			} else if (currentPrice.getCostDeferredFee().compareTo(currentPrice.getSaleDeferredFee()) > 0) {
				// 销售递延费不能比成本递延费的低
				throw new ServiceException(
						ExceptionConstant.SALES_DEFERRED_CHARGES_CANNOT_LOWER_COST_DEFERRED_CHARGES_EXCEPTION,
						new Object[] { contractDto.getName() });
			}
		} else {
			if (agentPrice == null || (agentPrice.getCostReserveFund() == null
					|| agentPrice.getCostOpenwindServiceFee() == null || agentPrice.getCostUnwindServiceFee() == null
					|| agentPrice.getCostDeferredFee() == null)) {
				// 上级成本价格不能为空
				throw new ServiceException(ExceptionConstant.COST_MARGIN_CANNOT_LOWER_GLOBAL_SETTING_EXCEPTION,
						new Object[] { contractDto.getName() });
			}

			if (currentPrice.getCostReserveFund() == null) {
				currentPrice.setCostReserveFund(agentPrice.getCostReserveFund());
			} else if (currentPrice.getCostReserveFund() == BigDecimal.ZERO) {
				currentPrice.setCostReserveFund(null);
			} /*
				 * else if
				 * (currentPrice.getCostReserveFund().compareTo(agentPrice.
				 * getCostReserveFund()) < 0) { // 成本保证金不能比上级设置的低 throw new
				 * ServiceException(ExceptionConstant.
				 * COST_MARGIN_CANNOT_LOWER_GLOBAL_SETTING_EXCEPTION, new
				 * Object[] { contractDto.getName(),
				 * agentPrice.getCostReserveFund() }); }
				 */
			if (currentPrice.getCostOpenwindServiceFee() == null) {
				currentPrice.setCostOpenwindServiceFee(agentPrice.getCostOpenwindServiceFee());
			} else if (currentPrice.getCostOpenwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setCostOpenwindServiceFee(null);
			} else if (currentPrice.getCostOpenwindServiceFee().compareTo(agentPrice.getCostOpenwindServiceFee()) < 0) {
				// 成本开仓手续费不能比上级设置的低
				throw new ServiceException(ExceptionConstant.COST_OPENINGCHARGES_CANNOT_LOWER_GLOBAL_SETTING_EXCEPTION,
						new Object[] { contractDto.getName(), agentPrice.getCostOpenwindServiceFee() });
			}

			if (currentPrice.getSaleOpenwindServiceFee() == null) {
				currentPrice.setSaleOpenwindServiceFee(currentPrice.getCostOpenwindServiceFee());
			} else if (currentPrice.getSaleOpenwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setSaleOpenwindServiceFee(null);
			} else if (currentPrice.getSaleOpenwindServiceFee()
					.compareTo(currentPrice.getCostOpenwindServiceFee()) < 0) {
				// 销售开仓手续费不能比成本开仓手续费的低
				throw new ServiceException(ExceptionConstant.SALES_OPENINGFEE_CANNOT_LOWER_COST_OPENINGFEE_EXCEPTION,
						new Object[] { contractDto.getName() });
			}

			if (currentPrice.getCostUnwindServiceFee() == null) {
				currentPrice.setCostUnwindServiceFee(agentPrice.getCostUnwindServiceFee());
			} else if (currentPrice.getCostUnwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setCostUnwindServiceFee(null);
			} else if (currentPrice.getCostUnwindServiceFee().compareTo(agentPrice.getCostUnwindServiceFee()) < 0) {
				// 成本平仓手续费不能比上级设置的低
				throw new ServiceException(ExceptionConstant.COST_NOT_LOWER_OVERALL_SETTING_EXCEPTION,
						new Object[] { contractDto.getName(), agentPrice.getCostUnwindServiceFee() });
			}

			if (currentPrice.getSaleUnwindServiceFee() == null) {
				currentPrice.setSaleUnwindServiceFee(currentPrice.getSaleUnwindServiceFee());
			} else if (currentPrice.getSaleUnwindServiceFee() == BigDecimal.ZERO) {
				currentPrice.setSaleUnwindServiceFee(null);
			} else if (currentPrice.getSaleUnwindServiceFee().compareTo(currentPrice.getCostUnwindServiceFee()) < 0) {
				// 销售平仓手续费不能比成本平仓手续费的低
				throw new ServiceException(ExceptionConstant.SALES_CLOSINGFEE_CANNOT_LOWER_COST_OPENINGFEE_EXCEPTION,
						new Object[] { contractDto.getName() });
			}

			if (currentPrice.getCostDeferredFee() == null) {
				currentPrice.setCostDeferredFee(agentPrice.getCostDeferredFee());
			} else if (currentPrice.getCostDeferredFee() == BigDecimal.ZERO) {
				currentPrice.setCostDeferredFee(null);
			} else if (currentPrice.getCostDeferredFee().compareTo(agentPrice.getCostDeferredFee()) < 0) {
				// 成本递延费不能比上级设置的低
				throw new ServiceException(ExceptionConstant.COST_DEFERREDFEE_SHOULD_NOT_LOWER_GLOBAL_SETTING_EXCEPTION,
						new Object[] { contractDto.getName(), agentPrice.getCostDeferredFee() });
			}

			if (currentPrice.getSaleDeferredFee() == null) {
				currentPrice.setSaleDeferredFee(currentPrice.getCostDeferredFee());
			} else if (currentPrice.getSaleDeferredFee() == BigDecimal.ZERO) {
				currentPrice.setSaleDeferredFee(null);
			} else if (currentPrice.getSaleDeferredFee().compareTo(currentPrice.getCostDeferredFee()) < 0) {
				// 销售递延费不能比成本递延费的低
				throw new ServiceException(
						ExceptionConstant.SALES_DEFERRED_CHARGES_CANNOT_LOWER_COST_DEFERRED_CHARGES_EXCEPTION,
						new Object[] { contractDto.getName() });
			}
		}
		return currentPrice;
	}

	/**
	 * 获取当前期货代理价格数据
	 * 
	 * @param orgId
	 *            代理商ID
	 * @param commodityId
	 *            品种ID
	 * @return 货代理价格数据
	 */
	public FuturesAgentPrice currentAgentPrice(Long publisherId, Long commodityId) {
		OrganizationPublisher orgPublisher = orgPublisherDao.retrieveByPublisherId(publisherId);
		if (orgPublisher == null) {
			return null;
		}
		// return this.cycleAgentPrice(orgPublisher.getOrgId(), commodityId,
		// null, true);
		return this.platformAgentPrice(orgPublisher.getOrgId(), commodityId);
	}

	/**
	 * 获取代理商设置价格，如果该代理商未设置价格 默认为平台价格
	 * 
	 * @param orgId
	 *            代理商ID
	 * @param commodityId
	 *            品种ID
	 * @return 期货代理价格
	 */
	public FuturesAgentPrice platformAgentPrice(Long orgId, Long commodityId) {
		FuturesAgentPrice agentPrice = agentPriceDao.findByCommodityIdAndOrgId(commodityId, orgId);
		if (agentPrice == null) {
			FuturesCommodityDto commodity = commodityBusiness.getFuturesByCommodityId(commodityId);
			if (commodity == null) {
				return null;
			}
			agentPrice = new FuturesAgentPrice();
			agentPrice.setOrgId(orgId);
			agentPrice.setCommodityId(commodityId);
			agentPrice.setCostReserveFund(commodity.getPerUnitReserveFund());
			agentPrice.setCostOpenwindServiceFee(commodity.getOpenwindServiceFee());
			agentPrice.setCostUnwindServiceFee(commodity.getUnwindServiceFee());
			agentPrice.setCostDeferredFee(commodity.getOvernightPerUnitDeferredFee());
			agentPrice.setSaleOpenwindServiceFee(commodity.getOpenwindServiceFee());
			agentPrice.setSaleUnwindServiceFee(commodity.getUnwindServiceFee());
			agentPrice.setSaleDeferredFee(commodity.getOvernightPerUnitDeferredFee());
		}
		return agentPrice;
	}

	/**
	 * 循环获取代理价格
	 * <p>
	 * 销售价使用最后一级设置的销售价，成本价如果当前层级未设置则逐级往上查找
	 * </p>
	 * 
	 * @return 代理价格
	 */
	public FuturesAgentPrice cycleAgentPrice(Long orgId, Long commodityId, FuturesAgentPrice beforePrice,
			boolean isLast) {
		FuturesAgentPrice agentPrice = agentPriceDao.findByCommodityIdAndOrgId(commodityId, orgId);
		if (beforePrice == null) {
			beforePrice = agentPrice;
			// 如果进入到这个判断中，并且不是最后一级，说明最后一级没有agentPrice记录，把销售价置空
			if (!isLast && agentPrice != null) {
				beforePrice.setSaleDeferredFee(null);
				beforePrice.setSaleOpenwindServiceFee(null);
				beforePrice.setSaleUnwindServiceFee(null);
			}
		} else if (agentPrice != null) {
			if (beforePrice.getCostDeferredFee() == null) {
				beforePrice.setCostDeferredFee(agentPrice.getCostDeferredFee());
			}
			if (beforePrice.getCostOpenwindServiceFee() == null) {
				beforePrice.setCostOpenwindServiceFee(agentPrice.getCostOpenwindServiceFee());
			}
			if (beforePrice.getCostReserveFund() == null) {
				beforePrice.setCostReserveFund(agentPrice.getCostReserveFund());
			}
			if (beforePrice.getCostUnwindServiceFee() == null) {
				beforePrice.setCostUnwindServiceFee(agentPrice.getCostUnwindServiceFee());
			}
		}
		Organization org = organizationDao.retrieve(orgId);
		if (org.getLevel() <= 2 || (beforePrice != null && beforePrice.getCostDeferredFee() != null
				&& beforePrice.getCostOpenwindServiceFee() != null && beforePrice.getCostReserveFund() != null
				&& beforePrice.getCostUnwindServiceFee() != null)) {
			return beforePrice;
		} else {
			return cycleAgentPrice(org.getParentId(), commodityId, beforePrice, false);
		}
	}

	/**
	 * 获取上级期货代理商数据
	 * 
	 * @param orgId
	 *            代理商ID
	 * @param commodityId
	 *            品种ID
	 * @return 货代理商数据
	 */
	public FuturesAgentPrice superiorAgentPrice(Long orgId, Long commodityId) {
		OrganizationDto organization = agentPriceBusiness.fetchByOrgId(orgId);
		return agentPriceDao.findByCommodityIdAndOrgId(commodityId, organization.getParentId());
	}

	public FuturesOrderCountDto getSUMOrder(FuturesTradeAdminQuery query) {
		String orderStateCondition = "";
		if (!StringUtil.isEmpty(query.getOrderState())) {
			orderStateCondition = " and t1.state in (" + query.getOrderState() + ")";
		}

		String publisherId = "";
		if (query.getPublisherIds().size() > 0) {
			String pu = "";
			for (int i = 0; i < query.getPublisherIds().size(); i++) {
				if (i == query.getPublisherIds().size() - 1) {
					pu = pu + query.getPublisherIds().get(i);
				} else {
					pu = pu + query.getPublisherIds().get(i) + ", ";
				}
			}
			publisherId = " and t1.publisher_id in (" + pu + ")";
		}

		String commoditySymbolCondition = "";
		if (!StringUtil.isEmpty(query.getSymbol())) {
			commoditySymbolCondition = " and t1.commodity_symbol like '%" + query.getSymbol() + "%' ";
		}
		String commodityNameCondition = "";
		if (!StringUtil.isEmpty(query.getName())) {
			commodityNameCondition = " and t1.commodity_name like '%" + query.getName() + "%' ";
		}

		String orderTypeCondition = "";
		if (!StringUtil.isEmpty(query.getOrderType())) {
			orderTypeCondition = " and t1.order_type = " + query.getOrderType() + "";
		}

		String windControlTypeCondition = "";
		if (!StringUtil.isEmpty(query.getWindControlType())) {
			windControlTypeCondition = " and t1.wind_control_type in (" + query.getWindControlType() + ")";
		}
		String sql = String.format(
				" SELECT SUM(t1.total_quantity) AS quantity, SUM(t1.reserve_fund) AS reserve_fund, SUM( (t1.openwind_service_fee + t1.unwind_service_fee ) * t1.total_quantity ) AS zhf, "
						+ " SUM( t4.overnight_deferred_fee )AS deferred_record "
						+ " FROM f_futures_order t1 LEFT JOIN f_futures_contract t2 ON t2.id = t1.contract_id LEFT JOIN f_futures_commodity t3 ON t3.id = t2.commodity_id LEFT JOIN f_futures_overnight_record t4 ON t4.order_id = t1.id"
						+ " where 1=1 %s %s %s %s %s %s",
				orderStateCondition, publisherId, commodityNameCondition, commoditySymbolCondition, orderTypeCondition,
				windControlTypeCondition);
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setQuantity", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setDeferred", new Class<?>[] { BigDecimal.class }));
		List<FuturesOrderCountDto> content = sqlDao.execute(FuturesOrderCountDto.class, sql, setMethodMap);
		if (content != null && content.size() > 0) {
			return content.get(0);
		}
		return null;
	}

}
