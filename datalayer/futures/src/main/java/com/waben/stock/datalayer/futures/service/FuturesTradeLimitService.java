package com.waben.stock.datalayer.futures.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeLimitQuery;
import com.waben.stock.interfaces.util.StringUtil;

@Service
public class FuturesTradeLimitService {

	@Autowired
	private FuturesTradeLimitDao limitDao;

	@Autowired
	private DynamicQuerySqlDao sqlDao;
	
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

	public FuturesOrderCountDto getSUMOrder(FuturesTradeAdminQuery query) {
		String orderStateCondition = "";
		if(!StringUtil.isEmpty(query.getOrderState())){
			orderStateCondition = " and t1.state in ("+ query.getOrderState() +")";
		}
		
		String publisherId = "";
		if(query.getPublisherIds().size()>0){
			String pu = "";
			for(int i=0;i<query.getPublisherIds().size();i++){
				if(i==query.getPublisherIds().size()-1){
					pu = pu + query.getPublisherIds().get(i);
				}else{
					pu = pu + query.getPublisherIds().get(i)+", ";
				}
			}
			publisherId = " and t1.publisher_id in ("+ pu +")";
		}
		
		String commoditySymbolCondition = "";
		if (!StringUtil.isEmpty(query.getSymbol())) {
			commoditySymbolCondition = " and t1.commodity_symbol like '%"
					+ query.getSymbol() + "%' ";
		}
		String commodityNameCondition = "";
		if(!StringUtil.isEmpty(query.getName())){
			commodityNameCondition = " and t1.commodity_name like '%" + query.getName() +"%' ";
		}
		
		String orderTypeCondition = "";
		if(!StringUtil.isEmpty(query.getOrderType())){
			orderTypeCondition = " and t1.order_type = "+ query.getOrderType() +"";
		}
		
		String startTimeCondition = "";
		if (query.getStartTime() != null) {
			if(query.getQueryType()==0 || query.getQueryType()==1){
				startTimeCondition = " and t1.buying_time>='" + sdf.format(query.getStartTime()) + "' ";
			}else if(query.getQueryType()==2){
				startTimeCondition = " and t1.selling_time>='" + sdf.format(query.getStartTime()) + "' ";
			}
		}
		String endTimeCondition = "";
		if (query.getEndTime() != null) {
			if(query.getQueryType()==0 || query.getQueryType()==1){
				endTimeCondition = " and t1.buying_time<'" + sdf.format(query.getEndTime()) + "' ";
			}else if(query.getQueryType()==2){
				endTimeCondition = " and t1.selling_time<'" + sdf.format(query.getEndTime()) + "' ";
			}
		}
		
		String buyingPriceTypeCodition = "";
		if(!StringUtil.isEmpty(query.getPriceType())){
			buyingPriceTypeCodition = " and t1.buying_price_type = "+ query.getPriceType() +"";
		}
		
		String windControlTypeCondition = "";
		if(!StringUtil.isEmpty(query.getWindControlType())){
			windControlTypeCondition = " and t1.wind_control_type in ("+ query.getWindControlType() +")";
		}
		String sql =String.format(" SELECT SUM(t1.total_quantity) AS quantity, SUM(t1.reserve_fund) AS reserve_fund, SUM( (t1.openwind_service_fee + t1.unwind_service_fee ) * t1.total_quantity ) AS zhf, "
						+ " SUM( t4.overnight_deferred_fee )AS deferred_record "
						+ " FROM f_futures_order t1 LEFT JOIN f_futures_contract t2 ON t2.id = t1.contract_id LEFT JOIN f_futures_commodity t3 ON t3.id = t2.commodity_id LEFT JOIN f_futures_overnight_record t4 ON t4.order_id = t1.id"
						+ " where 1=1 %s %s %s %s %s %s %s %s %s", orderStateCondition, publisherId, commodityNameCondition, commoditySymbolCondition,
						orderTypeCondition, windControlTypeCondition, buyingPriceTypeCodition, startTimeCondition, endTimeCondition);
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
