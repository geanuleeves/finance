package com.waben.stock.datalayer.manage.service;

import com.waben.stock.datalayer.manage.entity.Permission;
import com.waben.stock.datalayer.manage.entity.Role;
import com.waben.stock.datalayer.manage.repository.PermissionDao;
import com.waben.stock.datalayer.manage.repository.RoleDao;
import com.waben.stock.interfaces.constants.ExceptionConstant;
import com.waben.stock.interfaces.exception.ServiceException;
import com.waben.stock.interfaces.pojo.query.PermissionQuery;
import com.waben.stock.interfaces.pojo.query.RoleQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * @author Created by yuyidi on 2017/12/11.
 * @desc
 */
public class PermissionService {


    @Autowired
    private PermissionDao permissionDao;

    public Permission findById(Long id) {
        Permission permission = permissionDao.retrieve(id);
        if (permission == null) {
            throw new ServiceException(ExceptionConstant.ROLE_NOT_FOUND_EXCEPTION);
        }
        return permission;
    }

    public Page<Permission> pagesByQuery(final PermissionQuery query) {
        Pageable pageable = new PageRequest(query.getPage(), query.getSize());
        Page<Permission> pages = permissionDao.page(new Specification<Permission>() {
            @Override
            public Predicate toPredicate(Root<Permission> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                    criteriaBuilder) {

                return criteriaQuery.getRestriction();
            }
        }, pageable);
        return pages;
    }

}
