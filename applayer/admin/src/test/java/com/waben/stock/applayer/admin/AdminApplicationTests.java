package com.waben.stock.applayer.admin;

import com.waben.stock.applayer.admin.util.PhoneUtil;
import org.junit.Test;

public class AdminApplicationTests {

	@Test
	public void contextLoads() {
//		String phone = "1,2,3,4,6,5,7,8,9,7,8";
//		String[] strings = phone.split(",");
//		System.out.println(PhoneUtil.encodedPhone(phone));
		AdminApplicationTests adminApplicationTests = new AdminApplicationTests();
		System.out.println(adminApplicationTests.getClass().getName());
		System.out.println(adminApplicationTests.getClass().getSuperclass().getName());

	}

}
