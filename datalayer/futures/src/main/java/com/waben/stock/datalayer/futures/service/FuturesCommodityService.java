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
import com.waben.stock.datalayer.futures.entity.FuturesExchange;
import com.waben.stock.datalayer.futures.repository.FuturesCommodityDao;
import com.waben.stock.datalayer.futures.repository.FuturesExchangeDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesCommodityAdminQuery;

@Service
public class FuturesCommodityService {

	@Autowired
	private FuturesCommodityDao dao;

	@Autowired
	private FuturesExchangeDao exchangeDao;

	public FuturesCommodity retrieve(Long id) {
		return dao.retrieve(id);
	}

	public FuturesCommodity save(FuturesCommodity dto) {
		return dao.create(dto);
	}

	public FuturesCommodity modify(FuturesCommodity dto) {
		return dao.update(dto);
	}

	public void delete(Long id) {
		dao.delete(id);
	}

	public Page<FuturesCommodity> pages(final FuturesCommodityAdminQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesCommodity> page = dao.page(new Specification<FuturesCommodity>() {

			@Override
			public Predicate toPredicate(Root<FuturesCommodity> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				Join<FuturesCommodity, FuturesExchange> join = root.join("exchange", JoinType.LEFT);

				if (query.getExchangcode() != null && !"".equals(query.getExchangcode())) {
					predicateList.add(criteriaBuilder.or(
							criteriaBuilder.like(join.get("code").as(String.class), query.getExchangcode() + "%"),
							criteriaBuilder.like(join.get("name").as(String.class), query.getExchangcode() + "%")));
				}

				if (query.getSymbol() != null && !"".equals(query.getSymbol())) {
					predicateList.add(criteriaBuilder.equal(root.get("symbol").as(String.class), query.getSymbol()));
				}

				if (query.getName() != null && !"".equals(query.getName())) {
					predicateList.add(criteriaBuilder.equal(root.get("name").as(String.class), query.getName()));
				}

				if (query.getProductType() != null && !"".equals(query.getProductType())) {
					predicateList.add(
							criteriaBuilder.equal(root.get("productType").as(String.class), query.getProductType()));
				}

				if (predicateList.size() > 0) {
					criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
				}

				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createTime").as(Date.class)));

				return criteriaQuery.getRestriction();
			}
		}, pageable);

		return page;
	}

	public List<FuturesCommodity> listByExchangeId(Long exchangeId) {
		FuturesExchange exchange = exchangeDao.retrieve(exchangeId);
		if (exchange != null) {
			return dao.retrieveByExchange(exchange);
		} else {
			return new ArrayList<>();
		}
	}

	public FuturesCommodity setSlipPoint(Long commodityId, Integer buyUpOpenSlipPoint, Integer buyUpCloseSlipPoint,
			Integer buyFallOpenSlipPoint, Integer buyFallCloseSlipPoint) {
		FuturesCommodity commodity = dao.retrieve(commodityId);
		commodity.setBuyUpOpenSlipPoint(buyUpOpenSlipPoint);
		commodity.setBuyUpCloseSlipPoint(buyUpCloseSlipPoint);
		commodity.setBuyFallOpenSlipPoint(buyFallOpenSlipPoint);
		commodity.setBuyFallCloseSlipPoint(buyFallCloseSlipPoint);
		return dao.update(commodity);
	}

}
