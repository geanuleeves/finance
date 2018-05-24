package com.waben.stock.datalayer.futures.warpper;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND, basePackages = "com.waben.stock.datalayer.futures.repository")
@EntityScan(basePackages = "com.waben.stock.datalayer.futures.entity")
@Configuration
public class DataAccessConfigurer {

}