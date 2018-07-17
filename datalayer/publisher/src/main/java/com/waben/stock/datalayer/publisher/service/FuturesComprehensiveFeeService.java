package com.waben.stock.datalayer.publisher.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.waben.stock.datalayer.publisher.entity.FuturesComprehensiveFee;
import com.waben.stock.datalayer.publisher.entity.Publisher;
import com.waben.stock.datalayer.publisher.repository.FuturesComprehensiveFeeDao;
import com.waben.stock.interfaces.pojo.query.FuturesComprehensiveFeeQuery;

@Service
public class FuturesComprehensiveFeeService {
	
	@Autowired
	private FuturesComprehensiveFeeDao feeDao;
	
	public FuturesComprehensiveFee save(FuturesComprehensiveFee t){
		return feeDao.create(t);
	}
	
	public FuturesComprehensiveFee modify(FuturesComprehensiveFee t){
		return feeDao.update(t);
	}
	
	public FuturesComprehensiveFee retrieve(Long id){
		return feeDao.retrieve(id);
	}
	
	public void delete(Long id){
		feeDao.delete(id);
	}
	
	public Page<FuturesComprehensiveFee> page(final FuturesComprehensiveFeeQuery query){
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesComprehensiveFee> result = feeDao.page(new Specification<FuturesComprehensiveFee>() {
			
			@Override
			public Predicate toPredicate(Root<FuturesComprehensiveFee> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				Join<FuturesComprehensiveFee, Publisher> join = root.join("publisher", JoinType.LEFT);

				if (query.getPublisherId().size() > 0) {
					predicateList.add(criteriaBuilder.in(join.get("id")).value(query.getPublisherId()));
				}
				
				if(query.getState()!=null){
					if(query.getState()==0){
						predicateList.add(criteriaBuilder.equal(root.get("state").as(Integer.class), query.getState()));
					}else{
						predicateList.add(criteriaBuilder.notEqual(root.get("state").as(Integer.class), 0));
					}
				}else{
					predicateList.add(criteriaBuilder.notEqual(root.get("state").as(Integer.class), 0));
				}
				
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime").as(Date.class)));

				return criteriaQuery.getRestriction();
			}
		}, pageable);
		
		return result;
	}

}
