package com.waben.stock.datalayer.futures.service;

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

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.repository.FuturesTradeLimitDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeLimitQuery;
import com.waben.stock.interfaces.util.StringUtil;

@Service
public class FuturesTradeLimitService {

	@Autowired
	private FuturesTradeLimitDao limitDao;

	public FuturesTradeLimit save(FuturesTradeLimit limit) {
		limit.setUpdateTime(new Date());
		return limitDao.create(limit);
	}

	public FuturesTradeLimit modify(FuturesTradeLimit limit) {
		limit.setUpdateTime(new Date());
		return limitDao.update(limit);
	}

	public void delete(Long id) {
		limitDao.delete(id);
		;
	}

	public void deleteByContractId(Long contractId) {
		limitDao.deleteByContractId(contractId);
	}

	public List<FuturesTradeLimit> findByContractId(Long contractId) {
		return limitDao.findByContractId(contractId);
	}

	public Page<FuturesTradeLimit> pagesTradeLimit(final FuturesTradeLimitQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesTradeLimit> pages = limitDao.page(new Specification<FuturesTradeLimit>() {

			@Override
			public Predicate toPredicate(Root<FuturesTradeLimit> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<>();
				if (!StringUtil.isEmpty(query.getName())) {
					Join<FuturesTradeLimit, FuturesContract> parentJoin = root.join("contract", JoinType.LEFT)
							.join("commodity", JoinType.LEFT);
					// Join<FuturesContract, FuturesCommodity> join =
					// root.join("commodity", JoinType.LEFT);
					Predicate contractName = criteriaBuilder.like(parentJoin.get("name").as(String.class),
							"%" + query.getName() + "%");
					predicateList.add(contractName);
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
