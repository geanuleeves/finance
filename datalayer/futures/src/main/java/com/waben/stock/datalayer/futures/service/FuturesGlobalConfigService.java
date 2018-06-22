package com.waben.stock.datalayer.futures.service;

import java.util.ArrayList;
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

import com.waben.stock.datalayer.futures.entity.FuturesGlobalConfig;
import com.waben.stock.datalayer.futures.repository.FuturesGlobalConfigDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesGlobalConfigQuery;

@Service
public class FuturesGlobalConfigService {

	@Autowired
	private FuturesGlobalConfigDao globalConfigDao;
	
	public FuturesGlobalConfig save(FuturesGlobalConfig global){
		return globalConfigDao.create(global);
	}
	
	public FuturesGlobalConfig modify(FuturesGlobalConfig global){
		return globalConfigDao.update(global);
	}
	
	public void delete(Long id){
		globalConfigDao.delete(id);
	}
	
	public FuturesGlobalConfig findById(Long id){
		return globalConfigDao.retrieve(id);
	}
	
	public List<FuturesGlobalConfig> findAll(){
		return globalConfigDao.list();
	}
	
	public FuturesGlobalConfig saveAndModif(FuturesGlobalConfig global){
		return globalConfigDao.create(global);
	}
	
	public Page<FuturesGlobalConfig> pagesGlobal(final FuturesGlobalConfigQuery query){
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesGlobalConfig> pages = globalConfigDao.page(new Specification<FuturesGlobalConfig>() {
			
			@Override
			public Predicate toPredicate(Root<FuturesGlobalConfig> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				
				if(query.getType()!=null && !"".equals(query.getType())){
					predicateList.add(
							criteriaBuilder.equal(root.get("type").as(String.class), query.getType()));
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
