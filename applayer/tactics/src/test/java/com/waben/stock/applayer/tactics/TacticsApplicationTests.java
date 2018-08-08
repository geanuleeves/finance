package com.waben.stock.applayer.tactics;

import com.waben.stock.interfaces.dto.stockoption.StockOptionTradeDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class TacticsApplicationTests {

	@Test
	public void contextLoads() {
		List<Student> list = new ArrayList<Student>();
		Student s1 = new Student();
		s1.setAge(20);
		s1.setUsable(true);
		Student s2 = new Student();
		s2.setAge(19);
		s2.setUsable(true);
		Student s3 = new Student();
		s3.setAge(21);
		s3.setUsable(false);
		list.add(s1);
		list.add(s2);
		list.add(s3);
		for (Student student : list){
			System.out.println(student.toString());
		}
		descByNominalAmount(list);
		for (Student student : list){
			System.out.println(student.toString());
		}
	}
	private void descByNominalAmount(List<Student> list) {
		Collections.sort(list, new Comparator<Student>() {

			@Override
			public int compare(Student o1, Student o2) {
				// 按照学生的年龄进行升序排列
				if (o1.getAge() > o2.getAge()) {
					return 1;
				}
				if (o1.getAge() == o2.getAge()) {
					return 0;
				}
				return -1;
			}
		});
	}

}
