package com.waben.stock.datalayer.promotion.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.waben.stock.datalayer.promotion.entity.Organization;
import com.waben.stock.datalayer.promotion.entity.OrganizationCategory;
import com.waben.stock.datalayer.promotion.pojo.query.OrganizationForm;
import com.waben.stock.datalayer.promotion.pojo.query.OrganizationQuery;
import com.waben.stock.datalayer.promotion.repository.OrganizationCategoryDao;
import com.waben.stock.datalayer.promotion.repository.OrganizationDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.enums.OrganizationState;

/**
 * 机构 Service
 * 
 * @author luomengan
 *
 */
@Service
public class OrganizationService {

	@Autowired
	private OrganizationDao organizationDao;
	
	@Autowired
	private OrganizationCategoryDao orgCategoryDao;

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
		if(parent == null) {
			throw new ServiceException(ExceptionConstant.ORGANIZATION_NOTEXIST_EXCEPTION);
		}
		OrganizationCategory category = orgCategoryDao.retrieve(orgForm.getCategoryId());
		if(category == null) {
			throw new ServiceException(ExceptionConstant.ORGANIZATIONCATEGORY_NOTEXIST_EXCEPTION);
		}
		// 生成机构代码
		List<Organization> childList = organizationDao.listByParentOrderByCodeDesc(parent);
		String code = parent.getCode();
		if(childList != null && childList.size() > 0) {
			Organization max = childList.get(0);
			String suffix = max.getCode().substring(code.length());
			Long seria = Long.parseLong(suffix) + 1;
			String seriaStr = seria.toString();
			if(seriaStr.length() < suffix.length()) {
				int lack = suffix.length() - seriaStr.length();
				for(int i = 0; i < lack; i++) {
					seriaStr = "0" + seriaStr;
				}
			}
			code += seriaStr;
		} else {
			if(category.getLevel() == 2) {
				code += "001";
			} else if(category.getLevel() == 3) {
				code += "00001";
			} else {
				code += "001";
			}
		}
		// 保存机构
		Organization org = new Organization();
		org.setCategory(category);
		org.setCode(code);
		org.setCreateTime(new Date());
		org.setLevel(category.getLevel());
		org.setName(orgForm.getName());
		org.setParent(parent);
		org.setRemark(orgForm.getRemark());
		org.setState(OrganizationState.NORMAL);
		return organizationDao.create(org);
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
		if(ids != null) {
			String[] idArr= ids.split(",");
			for(String id : idArr) {
				if(!"".equals(id.trim())) {
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

	public Page<Organization> pagesByQuery(final OrganizationQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<Organization> pages = organizationDao.page(new Specification<Organization>() {
			@Override
			public Predicate toPredicate(Root<Organization> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				Join<Organization, OrganizationCategory> join = root.join("category", JoinType.LEFT);
				if (query.getCode() != null && !"".equals(query.getCode().trim())) {
					predicateList.add(root.get("code").in(query.getCode()));
				}
				if(query.getState() != null && !"".equals(query.getState().trim()) && !"0".equals(query.getState().trim())) {
					predicateList.add(criteriaBuilder.equal(root.get("state").as(OrganizationState.class), OrganizationState.getByIndex(query.getState().trim())));
				}
				if(query.getCategoryId() != null && !"".equals(query.getCategoryId().trim()) && !"0".equals(query.getCategoryId().trim())) {
					predicateList.add(criteriaBuilder.equal(join.get("id").as(Long.class), Long.parseLong(query.getCategoryId())));
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
		if(parentId.longValue() > 0) {
			parent = organizationDao.retrieve(parentId);
			if(parent == null) {
				return new ArrayList<Organization>();
			}
		}
		return organizationDao.listByParent(parent);
	}

}
