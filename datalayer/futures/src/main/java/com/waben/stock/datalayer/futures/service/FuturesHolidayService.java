package com.waben.stock.datalayer.futures.service;

import java.util.ArrayList;
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

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesHoliday;
import com.waben.stock.datalayer.futures.repository.FuturesHolidayDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesHolidayQuery;
import com.waben.stock.interfaces.util.StringUtil;

@Service
public class FuturesHolidayService {
	
	@Autowired
	private FuturesHolidayDao holidayDao;
	
	public FuturesHoliday saveAndModify(FuturesHoliday t){
		return holidayDao.create(t);
	}
	
	public void delete(Long id){
		holidayDao.delete(id);
	}
	
	public FuturesHoliday findById(Long id){
		return holidayDao.retrieve(id);
	}
	
	public Page<FuturesHoliday> page(final FuturesHolidayQuery query){
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesHoliday> page = holidayDao.page(new Specification<FuturesHoliday>() {
			
			@Override
			public Predicate toPredicate(Root<FuturesHoliday> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				Join<FuturesHoliday,FuturesCommodity> join = root.join("commodity", JoinType.LEFT);
				
				if(!StringUtil.isEmpty(query.getCommoditySymbol())){
					predicateList.add(criteriaBuilder.like(join.get("symbol").as(String.class), "%"+query.getCommoditySymbol()+"%"));
				}
				
				if(!StringUtil.isEmpty(query.getCommodityName())){
					predicateList.add(criteriaBuilder.like(join.get("name").as(String.class), "%"+query.getCommodityName()+"%"));
				}

				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}

				return criteriaQuery.getRestriction();
			}
		}, pageable);
		
		return page;
	}

}
