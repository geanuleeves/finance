package com.waben.stock.applayer;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
	
	public static void testMain(String[] args) {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("258369"));
	}
	
	@Test
	public void test() {
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("aaa123"));
		System.out.println(encoder.matches("wangbei", "$2a$10$l18hnSijOQ9pWglwxI2rdORzOftFJS9ecZiuTcZNHlmUyq8Sv38r."));
	}
	
}
