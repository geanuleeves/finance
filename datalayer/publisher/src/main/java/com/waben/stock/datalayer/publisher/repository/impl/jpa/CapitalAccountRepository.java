package com.waben.stock.datalayer.publisher.repository.impl.jpa;

import com.waben.stock.datalayer.publisher.entity.CapitalAccount;
import com.waben.stock.datalayer.publisher.entity.Publisher;

/**
 * 资金账号 Jpa
 * 
 * @author lma
 *
 */
public interface CapitalAccountRepository extends CustomJpaRepository<CapitalAccount, Long> {

	CapitalAccount findByPublisherSerialCode(String serialCode);

	CapitalAccount findByPublisher(Publisher publisher);

}
