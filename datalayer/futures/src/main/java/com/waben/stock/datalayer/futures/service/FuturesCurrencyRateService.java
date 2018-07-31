package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.waben.stock.datalayer.futures.entity.FuturesCurrencyRate;
import com.waben.stock.datalayer.futures.repository.FuturesCurrencyRateDao;

/**
 * 期货货币汇率 service
 * 
 * @author sunl
 *
 */
@Service
public class FuturesCurrencyRateService {

	@Autowired
	private FuturesCurrencyRateDao currencyRateDao;

	public FuturesCurrencyRate addCurrencyRate(FuturesCurrencyRate rate) {
		return currencyRateDao.create(rate);
	}

	public FuturesCurrencyRate modifyCurrencyRate(FuturesCurrencyRate rate) {
		return currencyRateDao.update(rate);
	}

	public void deleteCurrencyRate(Long id) {
		currencyRateDao.delete(id);

	}

	public Map<String, FuturesCurrencyRate> getRateMap() {
		Map<String, FuturesCurrencyRate> result = new HashMap<>();
		List<FuturesCurrencyRate> rateList = currencyRateDao.list();
		if (rateList != null && rateList.size() > 0) {
			for (FuturesCurrencyRate rate : rateList) {
				result.put(rate.getCurrency(), rate);
			}
		}
		return result;
	}

	public FuturesCurrencyRate queryByName(String currency) {
		List<FuturesCurrencyRate> list = currencyRateDao.retrieveByCurrency(currency);
		// retrieveByCurrencyName(currencyName);
		if (list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	public FuturesCurrencyRate queryRate(final FuturesCurrencyRate rate) {
		Pageable pageable = new PageRequest(0, Integer.MAX_VALUE);
		Page<FuturesCurrencyRate> pages = currencyRateDao.page(new Specification<FuturesCurrencyRate>() {

			@Override
			public Predicate toPredicate(Root<FuturesCurrencyRate> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				// Join<FuturesExchange, FuturesContract> parentJoin =
				// root.join("exchange", JoinType.LEFT);
				if (rate.getCurrencyName() != null && !"".equals(rate.getCurrencyName())) {
					predicateList.add(
							criteriaBuilder.equal(root.get("currencyName").as(String.class), rate.getCurrencyName()));
				}
				if (rate.getCurrency() != null && !"".equals(rate.getCurrency())) {
					predicateList.add(criteriaBuilder.equal(root.get("currency").as(String.class), rate.getCurrency()));
				}
				if (rate.getRate() != null && !"".equals(rate.getRate())) {
					predicateList.add(criteriaBuilder.equal(root.get("rate").as(BigDecimal.class), rate.getRate()));
				}

				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}

				return criteriaQuery.getRestriction();
			}
		}, pageable);
		if (pages.getContent().size() > 0) {
			return pages.getContent().get(0);
		}
		return null;
	}

	public Page<FuturesCurrencyRate> list() {
		return currencyRateDao.page(0, Integer.MAX_VALUE);
	}

	public FuturesCurrencyRate retrieve(Long id) {
		return currencyRateDao.retrieve(id);
	}

	public FuturesCurrencyRate findByCurrency(String currency) {
		List<FuturesCurrencyRate> rateList = currencyRateDao.retrieveByCurrency(currency);
		if (rateList != null && rateList.size() > 0) {
			return rateList.get(0);
		}
		return null;
	}

}
