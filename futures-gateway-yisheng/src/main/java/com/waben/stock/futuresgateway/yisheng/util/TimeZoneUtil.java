package com.waben.stock.futuresgateway.yisheng.util;

import java.util.Calendar;
import java.util.Date;

public class TimeZoneUtil {

	public static Date[] retriveBeijingTimeInterval(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, getCloseTimeHour());
		cal.add(Calendar.SECOND, 30);
		Date endTime = cal.getTime();

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(date);
		startCal.add(Calendar.HOUR_OF_DAY, -18);
		startCal.add(Calendar.MINUTE, 30);
		Date startTime = startCal.getTime();

		return new Date[] { startTime, endTime };
	}

	/**
	 * 判断纽约时间是否为夏令
	 * 
	 * <p>
	 * 美国的夏令时从三月的第二个周日开始到十一月的第一个周日结束。
	 * </p>
	 * 
	 * @return 是否为夏令
	 */
	public static boolean isSummerZone() {
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.MONTH, 2);
		startCal.set(Calendar.WEEK_OF_MONTH, 3);
		startCal.set(Calendar.DAY_OF_WEEK, 1);
		startCal.set(Calendar.HOUR_OF_DAY, 13);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);

		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.MONTH, 10);
		endCal.set(Calendar.WEEK_OF_MONTH, 2);
		endCal.set(Calendar.DAY_OF_WEEK, 1);
		endCal.set(Calendar.HOUR_OF_DAY, 13);
		endCal.set(Calendar.MINUTE, 0);
		endCal.set(Calendar.SECOND, 0);
		endCal.set(Calendar.MILLISECOND, 0);

		Date now = new Date();
		return now.getTime() > startCal.getTime().getTime() && now.getTime() < endCal.getTime().getTime();
	}

	public static int getOpenTimeHour() {
		return isSummerZone() ? 6 : 7;
	}

	public static int getCloseTimeHour() {
		return isSummerZone() ? 5 : 6;
	}

}
