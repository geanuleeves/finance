package com.waben.stock.datalayer.manage.service;

import com.waben.stock.datalayer.manage.entity.OperationLog;
import com.waben.stock.datalayer.manage.entity.Permission;
import com.waben.stock.datalayer.manage.repository.OperationLogDao;
import com.waben.stock.interfaces.pojo.query.OperationLogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author: zengzhiwei
 * @date: 2018/7/27 15:47
 * @desc：
 */
@Service
public class OperationLogService {

    @Autowired
    private OperationLogDao dao;


    public OperationLog save(OperationLog operationLog) {
        return dao.create(operationLog);
    }

    public Page<OperationLog> pages(OperationLogQuery query) {

        Pageable pageable = new PageRequest(query.getPage(), query.getSize());
        Page<OperationLog> pages = dao.page(new Specification<OperationLog>() {
            @Override
            public Predicate toPredicate(Root<OperationLog> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                    criteriaBuilder) {
                List<Predicate> predicatesList = new ArrayList();
                if (query.getStaffId()!=null) {
                    Predicate staffIdQuery = criteriaBuilder.equal(root.get("staffId").as(Long.class), query
                            .getStaffId());
                    predicatesList.add(staffIdQuery);
                }
                if (query.getType()!=null) {
                    Predicate typeQuery = criteriaBuilder.equal(root.get("type").as(Integer.class), query
                            .getType());
                    predicatesList.add(typeQuery);
                }
                criteriaQuery.where(predicatesList.toArray(new Predicate[predicatesList.size()]));
                criteriaQuery.orderBy(criteriaBuilder.desc(root.<Date>get("createTime").as(Date.class)));
                return criteriaQuery.getRestriction();
            }
        }, pageable);
        return pages;
    }
}
