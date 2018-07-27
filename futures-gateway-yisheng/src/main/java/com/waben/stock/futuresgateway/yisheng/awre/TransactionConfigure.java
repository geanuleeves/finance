package com.waben.stock.futuresgateway.yisheng.awre;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 配置事务
 * 
 * @author lma
 *
 */
@EnableTransactionManagement
@EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE, basePackages = "com.waben.stock.futuresgateway.yisheng.dao.impl.jpa")
@Configuration
public class TransactionConfigure {

}
