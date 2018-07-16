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

import com.waben.stock.datalayer.futures.entity.FuturesCommodity;
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.repository.FuturesExchangeDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesExchangeAdminQuery;

/**
 * 期货交易所 service
 * 
 * @author pzl
 *
 */
@Service
public class FuturesExchangeService {

	@Autowired
	private FuturesExchangeDao exchangeDao;

	public Page<FuturesExchange> pagesExchange(final FuturesExchangeAdminQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesExchange> pages = exchangeDao.page(new Specification<FuturesExchange>() {
			@Override
			public Predicate toPredicate(Root<FuturesExchange> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				if (query.getCode() != null && !"".equals(query.getCode())) {
					predicateList.add(criteriaBuilder.equal(root.get("code").as(String.class), query.getCode()));
				}
				if (query.getName() != null && !"".equals(query.getName())) {
					predicateList.add(criteriaBuilder.equal(root.get("name").as(String.class), query.getName()));
				}
				if (query.getExchangeType() != null && query.getExchangeType() > 0) {
					predicateList.add(
							criteriaBuilder.equal(root.get("exchangeType").as(String.class), query.getExchangeType()));
				}

				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return pages;
	}

	public FuturesExchange saveExchange(FuturesExchange exchange) {
		exchange.setEnable(true);
		return exchangeDao.create(exchange);
	}

	public FuturesExchange modifyExchange(FuturesExchange exchange) {
		exchange.setEnable(true);
		return exchangeDao.update(exchange);
	}

	public void deleteExchange(Long id) {
		exchangeDao.delete(id);
	}

	public List<FuturesCommodity> findByExchangId(Long exchangeId) {
		return exchangeDao.findByExchangId(exchangeId);
	}

	public FuturesExchange findById(Long id) {
		return exchangeDao.retrieve(id);
	}

	public List<FuturesExchange> list() {
		return exchangeDao.list();
	}
}
