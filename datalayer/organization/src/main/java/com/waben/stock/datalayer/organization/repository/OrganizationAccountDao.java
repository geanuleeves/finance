package com.waben.stock.datalayer.organization.repository;

import com.waben.stock.datalayer.organization.entity.Organization;
import com.waben.stock.datalayer.organization.entity.OrganizationAccount;

/**
 * 机构账户 Dao
 * 
 * @author lma
 *
 */
public interface OrganizationAccountDao extends BaseDao<OrganizationAccount, Long> {

	OrganizationAccount retrieveByOrg(Organization org);

}
