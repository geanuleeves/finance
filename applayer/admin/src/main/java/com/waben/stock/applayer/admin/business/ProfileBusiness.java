package com.waben.stock.applayer.admin.business;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ProfileBusiness {

	@Value("${spring.profiles.active}")
	private String activeProfile;

	private boolean isProd = true;

	@PostConstruct
	public void init() {
		if ("prod".equals(activeProfile)) {
			isProd = true;
		} else {
			isProd = false;
		}
	}

	public boolean isProd() {
		return isProd;
	}

	public void setProd(boolean isProd) {
		this.isProd = isProd;
	}

}
