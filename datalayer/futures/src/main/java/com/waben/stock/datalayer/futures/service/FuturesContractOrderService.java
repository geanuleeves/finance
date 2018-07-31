package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesContract;
import com.waben.stock.datalayer.futures.entity.FuturesContractOrder;
import com.waben.stock.datalayer.futures.repository.FuturesContractDao;
import com.waben.stock.datalayer.futures.repository.FuturesContractOrderDao;
import com.waben.stock.interfaces.pojo.query.admin.futures.FuturesTradeAdminQuery;
import com.waben.stock.interfaces.pojo.query.futures.FuturesContractOrderQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 合约订单
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesContractOrderService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesContractOrderDao futuresContractOrderDao;
    
    @Autowired
    private FuturesContractDao contractDao;

    public FuturesContractOrder findById(Long id) {
        return futuresContractOrderDao.retrieve(id);
    }

    public FuturesContractOrder save(FuturesContractOrder futuresContractOrder) {
        return futuresContractOrderDao.create(futuresContractOrder);
    }

    public FuturesContractOrder modify(FuturesContractOrder futuresContractOrder) {
        return futuresContractOrderDao.update(futuresContractOrder);
    }

    public void delete(Long id) {
        futuresContractOrderDao.delete(id);
    }

	public FuturesContractOrder findByContractIdAndPublisherId(Long contractId, Long publisherId) {
		FuturesContract contract = contractDao.retrieve(contractId);
		if(contract != null) {
			return futuresContractOrderDao.retrieveByContractAndPublisherId(contract, publisherId);
		}
		return null;
	}

    public Page<FuturesContractOrder> pages(final FuturesContractOrderQuery query) {
        Pageable pageable = new PageRequest(query.getPage(), query.getSize());
        Page<FuturesContractOrder> page = futuresContractOrderDao.page(new Specification<FuturesContractOrder>() {
            @Override
            public Predicate toPredicate(Root<FuturesContractOrder> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                //用户ID
                if (query.getPublisherId() != null && query.getPublisherId() != 0) {
                    predicateList.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
                }
                //品种编号
                if (!StringUtils.isEmpty(query.getCommodityNo())) {
                    predicateList.add(criteriaBuilder.equal(root.get("commodityNo").as(String.class), query.getCommodityNo()));
                }
                //合约编号
                if (!StringUtils.isEmpty(query.getContractNo())) {
                    predicateList.add(criteriaBuilder.equal(root.get("contractNo").as(String.class), query.getContractNo()));
                }
                if (predicateList.size() > 0) {
                    criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
                }
                //以更新时间排序
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        }, pageable);
        return page;
    }
    
    public Page<FuturesContractOrder> pages(final FuturesTradeAdminQuery query) {
		Pageable pageable = new PageRequest(query.getPage(), query.getSize());
		Page<FuturesContractOrder> page = futuresContractOrderDao.page(new Specification<FuturesContractOrder>() {
			@Override
			public Predicate toPredicate(Root<FuturesContractOrder> root, CriteriaQuery<?> criteriaQuery,
					CriteriaBuilder criteriaBuilder) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
				
				// 以更新时间排序
				criteriaQuery.orderBy(criteriaBuilder.desc(root.get("updateTime").as(Date.class)));
				return criteriaQuery.getRestriction();
			}
		}, pageable);
		return page;
	}


}
