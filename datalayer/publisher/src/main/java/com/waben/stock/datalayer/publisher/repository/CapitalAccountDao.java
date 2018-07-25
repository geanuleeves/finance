package com.waben.stock.datalayer.publisher.repository;

import com.waben.stock.datalayer.publisher.entity.CapitalAccount;

/**
 * 资金账号 Dao
 * 
 * @author lma
 *
 */
public interface CapitalAccountDao extends BaseDao<CapitalAccount, Long> {

	CapitalAccount retriveByPublisherSerialCode(String publisherSerialCode);
	
	CapitalAccount retriveByPublisherId(Long publisherId);


}
