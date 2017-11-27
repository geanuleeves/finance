package com.waben.stock.applayer.tactics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
// 服务发现客户端
@EnableDiscoveryClient
// 服务调用
@EnableFeignClients
// 断路器
@EnableHystrix
// 扫描包
@ComponentScan(basePackages = { "com.waben.stock" })
public class TacticsApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacticsApplication.class, args);
	}

}
