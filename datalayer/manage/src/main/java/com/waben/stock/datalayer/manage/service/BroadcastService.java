package com.waben.stock.datalayer.manage.service;

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

import com.waben.stock.datalayer.manage.entity.Broadcast;
import com.waben.stock.datalayer.manage.repository.BroadcastDao;
import com.waben.stock.interfaces.pojo.query.admin.BroadcastQuery;
import com.waben.stock.interfaces.util.StringUtil;

@Service
public class BroadcastService {
	
	@Autowired
	private BroadcastDao broadDao;
	
	public Broadcast save(Broadcast t){
		return broadDao.create(t);
	}
	
	public Broadcast modify(Broadcast t){
		return broadDao.update(t);
	}
	
	public void delete(Long id){
		broadDao.delete(id);
	}
	
	public Broadcast findById(Long id){
		return broadDao.retrieve(id);
	}
	
	public List<Broadcast> findByType(String type, boolean enable){
		return broadDao.findBytype(type, enable);
	}
	
	public Page<Broadcast> page(final BroadcastQuery query){
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<Broadcast> result = broadDao.page(new Specification<Broadcast>() {
			
			@Override
			public Predicate toPredicate(Root<Broadcast> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicatesList = new ArrayList<>();
				
				if(!StringUtil.isEmpty(query.getName())){
					Predicate userNameQuery = criteriaBuilder.equal(root.get("name").as(String.class), query.getName());
                    predicatesList.add(userNameQuery);
				}
				
				if(!StringUtil.isEmpty(query.getType())){
					Predicate userNameQuery = criteriaBuilder.equal(root.get("type").as(String.class), query.getType());
                    predicatesList.add(userNameQuery);
				}
				
				
				
				criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.<Date>get("createTime").as(Date.class)));
                return criteriaQuery.getRestriction();
			}
		}, pageable);
		
		
		return result;
	}

}
