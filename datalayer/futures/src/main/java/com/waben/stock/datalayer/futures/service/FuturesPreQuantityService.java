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
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesPreQuantity;
import com.waben.stock.datalayer.futures.repository.FuturesPreQuantityDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesPreQuantityQuery;

@Service
public class FuturesPreQuantityService {

	@Autowired
	private FuturesPreQuantityDao preQuantityDao;
	
	public FuturesPreQuantity save(FuturesPreQuantity pre){
		return preQuantityDao.create(pre);
	}
	
	public FuturesPreQuantity modify(FuturesPreQuantity pre){
		return preQuantityDao.update(pre);
	}
	
	public void delete(Long id){
		preQuantityDao.delete(id);
	}
	
	public int deleteByCommodityId(Long commodityId){
		return preQuantityDao.deleteByCommodityId(commodityId);
	}
	
	public List<FuturesPreQuantity> findByCommodityId(Long commodityId){
		return preQuantityDao.findByCommodityId(commodityId);
	}
	
	public Page<FuturesPreQuantity> pagePre(final FuturesPreQuantityQuery query){
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesPreQuantity> pages = preQuantityDao.page(new Specification<FuturesPreQuantity>() {
			
			@Override
			public Predicate toPredicate(Root<FuturesPreQuantity> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				Join<FuturesPreQuantity,FuturesCommodity> join = root.join("commodity", JoinType.LEFT);
				if(query.getCommodityId() !=null ){
					predicateList.add(criteriaBuilder.equal(join.get("id").as(Long.class), query.getCommodityId()));
				}
				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}
}
