package com.waben.stock.datalayer.futures.service;

import com.waben.stock.datalayer.futures.entity.FuturesOrder;
import com.waben.stock.datalayer.futures.entity.FuturesTradeAction;
import com.waben.stock.datalayer.futures.entity.FuturesTradeEntrust;
import com.waben.stock.datalayer.futures.repository.FuturesTradeActionDao;
import com.waben.stock.interfaces.pojo.query.futures.FuturesTradeActionQuery;
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
 * 订单交易开平仓记录
 *
 * @author chenk 2018/7/26
 */
@Service
public class FuturesTradeActionService {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FuturesTradeActionDao futuresTradeActionDao;

    public FuturesTradeAction findById(Long id) {
        return futuresTradeActionDao.retrieve(id);
    }

    public FuturesTradeAction save(FuturesTradeAction futuresTradeAction) {
        return futuresTradeActionDao.create(futuresTradeAction);
    }

    public FuturesTradeAction modify(FuturesTradeAction futuresTradeAction) {
        return futuresTradeActionDao.update(futuresTradeAction);
    }

    public void delete(Long id) {
        futuresTradeActionDao.delete(id);
    }

    public Page<FuturesTradeAction> pages(final FuturesTradeActionQuery query) {
        Pageable pageable = new PageRequest(query.getPage(), query.getSize());
        Page<FuturesTradeAction> page = futuresTradeActionDao.page(new Specification<FuturesTradeAction>() {
            @Override
            public Predicate toPredicate(Root<FuturesTradeAction> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicateList = new ArrayList<Predicate>();
                //用户ID
                if (query.getPublisherId() != null && query.getPublisherId() != 0) {
                    predicateList.add(criteriaBuilder.equal(root.get("publisherId").as(Long.class), query.getPublisherId()));
                }
                //交易订单实体
                Join<FuturesTradeAction, FuturesOrder> orderJoin = root.join("order", JoinType.LEFT);
                if (query.getOrderId() != null && query.getOrderId() != 0) {
                    Predicate orderId = criteriaBuilder.equal(orderJoin.get("id").as(Long.class),
                            query.getOrderId());
                    predicateList.add(criteriaBuilder.and(orderId));
                }
                //交易委托
                Join<FuturesTradeAction, FuturesTradeEntrust> tradeEntrustJoin = root.join("tradeEntrust", JoinType.LEFT);
                if (query.getTradeEntrustId() != null && query.getTradeEntrustId() != 0) {
                    Predicate tradeEntrustIdId = criteriaBuilder.equal(tradeEntrustJoin.get("id").as(Long.class),
                            query.getTradeEntrustId());
                    predicateList.add(criteriaBuilder.and(tradeEntrustIdId));
                }
                //委托时间
                if (query.getEntrustTime() != null) {
                    Predicate entrustTime = criteriaBuilder.greaterThanOrEqualTo(root.get("entrustTime").as(Date.class),
                            query.getEntrustTime());
                    predicateList.add(criteriaBuilder.and(entrustTime));
                }
                //交易开平仓 类型
                if (!StringUtils.isEmpty(query.getTradeActionType())) {
                    predicateList.add(criteriaBuilder.equal(root.get("tradeActionType").as(String.class), query.getTradeActionType()));
                }
                //风控类型
                if (!StringUtils.isEmpty(query.getWindControlType())) {
                    predicateList.add(criteriaBuilder.equal(root.get("windControlType").as(String.class), query.getWindControlType()));
                }
                //委托状态
                if (!StringUtils.isEmpty(query.getState())) {
                    predicateList.add(criteriaBuilder.equal(root.get("state").as(String.class), query.getState()));
                }
                //交易成功时间
                if (query.getTradeTime() != null) {
                    Predicate tradeTime = criteriaBuilder.greaterThanOrEqualTo(root.get("tradeTime").as(Date.class),
                            query.getTradeTime());
                    predicateList.add(criteriaBuilder.and(tradeTime));
                }
                //结算时间
                if (query.getSettlementTime() != null) {
                    Predicate settlementTime = criteriaBuilder.greaterThanOrEqualTo(root.get("settlementTime").as(Date.class),
                            query.getSettlementTime());
                    predicateList.add(criteriaBuilder.and(settlementTime));
                }

                if (predicateList.size() > 0) {
                    criteriaQuery.where(predicateList.toArray(new Predicate[predicateList.size()]));
                }
                //以委托时间排序
                criteriaQuery.orderBy(criteriaBuilder.desc(root.get("entrustTime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        }, pageable);
        return page;
    }

    /**
     * 查询今持仓
     * @param publisherId 用户ID
     * @param commodityNo 品种编号
     * @param contractNo 合约编号
     * @param tradeActionType 交易开平仓类型
     * @return
     */
     public Integer findFilledNow(Long publisherId, String commodityNo, String contractNo, String tradeActionType) {
        return futuresTradeActionDao.findFilledNow(publisherId, commodityNo, contractNo, tradeActionType);
    }

}
