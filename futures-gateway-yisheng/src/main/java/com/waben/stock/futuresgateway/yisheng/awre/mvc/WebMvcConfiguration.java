package com.waben.stock.futuresgateway.yisheng.awre.mvc;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClientURI;

/**
 * web mvc配置
 * 
 * @author lma
 *
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

	@Value("${spring.data.mongodb.uri}")
	private String mongoUri;

	@Bean
	public MongoTemplate mongoTemplate() {
		try {
			MongoDbFactory fac = new SimpleMongoDbFactory(new MongoClientURI(mongoUri));
			return new MongoTemplate(fac);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer() {
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				ErrorPage error401Page = new ErrorPage(HttpStatus.UNAUTHORIZED, "/401.html");
				ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/403.html");
				container.addErrorPages(error401Page, error403Page);
			}
		};
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
		return objectMapper;
	}

}
