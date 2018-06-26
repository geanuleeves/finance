package com.waben.stock.futuresgateway.yisheng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;

@SpringBootApplication(exclude = { MongoAutoConfiguration.class, MongoDataAutoConfiguration.class })
public class FuturesGatewayApplication {

	public static void testMain(String[] args) {
		SpringApplication.run(FuturesGatewayApplication.class, args);
	}

}
