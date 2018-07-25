package com.waben.stock.datalayer.publisher.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.publisher.entity.RealName;
import com.waben.stock.datalayer.publisher.repository.RealNameDao;
import com.waben.stock.interfaces.commonapi.juhe.RealNameInfoVerifier;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.enums.ResourceType;
import com.waben.stock.interfaces.exception.ServiceException;

/**
 * 实名认证 Service
 * 
 * @author lma
 *
 */
@Service
public class RealNameService {

	@Autowired
	private RealNameDao realNameDao;

	public synchronized RealName save(RealName realName) {
		RealName check = realNameDao.retriveByResourceTypeAndResourceId(realName.getResourceType(),
				realName.getResourceId());
		if (check != null) {
			throw new ServiceException(ExceptionConstant.REALNAME_EXIST_EXCEPTION);
		}
		List<RealName> checkList = realNameDao.retrieveByNameAndIdCard(realName.getName(), realName.getIdCard());
		if (checkList != null && checkList.size() > 0) {
			throw new ServiceException(ExceptionConstant.REALNAME_ALREADY_USERED_EXCEPTION);
		}
		// 验证实名信息
		boolean isValid = RealNameInfoVerifier.verify(realName.getName(), realName.getIdCard());
		if (!isValid) {
			throw new ServiceException(ExceptionConstant.REALNAME_WRONG_EXCEPTION);
		}
		realName.setCreateTime(new Date());
		return realNameDao.create(realName);
	}

	public RealName findByResourceTypeAndResourceId(ResourceType resourceType, Long resourceId) {
		return realNameDao.retriveByResourceTypeAndResourceId(resourceType, resourceId);
	}
	
	public List<RealName> findByName(final String name){
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		Page<RealName> page = realNameDao.page(new Specification<RealName>() {
			
			@Override
			public Predicate toPredicate(Root<RealName> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				if(name != null && !"".equals(name)){
					predicateList.add(criteriaBuilder.like(root.get("name").as(String.class), "%"+name+"%"));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page.getContent();
	}
	
	public RealName findByResourceId(Long resourceId){
		return realNameDao.findByResourceId(resourceId);
	}

}
