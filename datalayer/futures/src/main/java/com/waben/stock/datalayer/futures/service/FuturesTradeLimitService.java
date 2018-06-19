package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesTradeLimit;
import com.waben.stock.datalayer.futures.repository.DynamicQuerySqlDao;
import com.waben.stock.datalayer.futures.repository.FuturesTradeLimitDao;
import com.waben.stock.datalayer.futures.repository.impl.MethodDesc;
import com.waben.stock.interfaces.dto.admin.futures.FuturesOrderCountDto;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeLimitQuery;
import com.waben.stock.interfaces.util.StringUtil;

@Service
public class FuturesTradeLimitService {

	@Autowired
	private FuturesTradeLimitDao limitDao;

	@Autowired
	private DynamicQuerySqlDao sqlDao;

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

	public FuturesOrderCountDto getSUMOrder(String state) {
		String sql = String
				.format("SELECT SUM(t1.total_quantity) AS quantity, SUM(t1.reserve_fund* t1.total_quantity) AS reserve_fund, SUM( (t1.openwind_service_fee + t1.unwind_service_fee ) * t1.total_quantity ) AS zhf, "
						+ "SUM( IF(DATE_FORMAT(CURTIME(),'%T') >= t3.overnight_time || t1.state=9, t1.overnight_per_unit_deferred_fee * t1.total_quantity ,0) )AS deferred_record "
						+ "FROM f_futures_order t1 LEFT JOIN f_futures_contract t2 ON t2.id = t1.contract_id LEFT JOIN f_futures_commodity t3 ON t3.id = t2.commodity_id "
						+ "where t1.state in(" + state + ")");
		Map<Integer, MethodDesc> setMethodMap = new HashMap<>();
		setMethodMap.put(new Integer(0), new MethodDesc("setQuantity", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(1), new MethodDesc("setFund", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(2), new MethodDesc("setFee", new Class<?>[] { BigDecimal.class }));
		setMethodMap.put(new Integer(3), new MethodDesc("setDeferred", new Class<?>[] { BigDecimal.class }));
		List<FuturesOrderCountDto> content = sqlDao.execute(FuturesOrderCountDto.class, sql, setMethodMap);
		if (content != null && content.size() > 0) {
			return content.get(0);
		}
		return null;
	}
}
