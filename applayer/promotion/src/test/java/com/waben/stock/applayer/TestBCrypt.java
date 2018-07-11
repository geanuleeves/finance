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
		System.out.println(encoder.encode("wangbei"));
		System.out.println(encoder.matches("aaa123", "$2a$10$FvSi54Ppl9l/IanZ594Y3ew5O5IVC/WjQCuqh83JQgCtZjgcoMI6K"));
	}
	
}
