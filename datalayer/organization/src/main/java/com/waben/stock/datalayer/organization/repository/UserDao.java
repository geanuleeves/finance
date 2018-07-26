package com.waben.stock.datalayer.organization.repository;

import com.waben.stock.datalayer.organization.entity.User;

/**
 * 机构管理用户 Dao
 * 
 * @author lma
 *
 */
public interface UserDao extends BaseDao<User, Long> {

    User retrieveByUserName(String userName);
}
