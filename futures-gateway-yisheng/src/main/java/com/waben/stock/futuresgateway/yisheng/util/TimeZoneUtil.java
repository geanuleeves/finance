package com.waben.stock.futuresgateway.yisheng.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeZoneUtil {

	public static Date[] retriveBeijingTimeInterval(Date date, String commodityNo) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat fullSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date closeDate = fullSdf.parse(sdf.format(date) + " " + TimeZoneUtil.getCloseTime(commodityNo));
			Date openDate = fullSdf.parse(sdf.format(date) + " " + TimeZoneUtil.getOpenTime(commodityNo));
			if (date.getTime() >= openDate.getTime()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(openDate);
				cal.add(Calendar.SECOND, 30);
				Date startTime = cal.getTime();

				cal.setTime(closeDate);
				cal.add(Calendar.DAY_OF_MONTH, 1);
				cal.add(Calendar.SECOND, 30);
				Date endTime = cal.getTime();
				return new Date[] { startTime, endTime };

			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTime(openDate);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.add(Calendar.SECOND, 30);
				Date startTime = cal.getTime();
				
				cal.setTime(closeDate);
				cal.add(Calendar.SECOND, 30);
				Date endTime = cal.getTime();
				return new Date[] { startTime, endTime };
			}

		} catch (Exception ex) {
			throw new RuntimeException("解析时间错误!");
		}
	}

	/**
	 * 判断纽约时间是否为夏令
	 * 
	 * <p>
	 * 美国的夏令时从3月的第二个周日开始到11月的第一个周日结束。
	 * </p>
	 * 
	 * @return 是否为夏令
	 */
	public static boolean isNYSummerZone() {
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

	/**
	 * 判断瑞士时间是否为夏令
	 * 
	 * <p>
	 * 美国的夏令时从3月的最后一个星期天到10月的最后一个星期天结束。
	 * </p>
	 * 
	 * @return 是否为夏令
	 */
	public static boolean isRSSummerZone() {
		Calendar startCal = Calendar.getInstance();
		startCal.set(Calendar.MONTH, 2);
		startCal.set(Calendar.WEEK_OF_MONTH, 5);
		startCal.set(Calendar.DAY_OF_WEEK, 1);
		startCal.set(Calendar.HOUR_OF_DAY, 13);
		startCal.set(Calendar.MINUTE, 0);
		startCal.set(Calendar.SECOND, 0);
		startCal.set(Calendar.MILLISECOND, 0);

		Calendar endCal = Calendar.getInstance();
		endCal.set(Calendar.MONTH, 9);
		endCal.set(Calendar.WEEK_OF_MONTH, 5);
		endCal.set(Calendar.DAY_OF_WEEK, 1);
		endCal.set(Calendar.HOUR_OF_DAY, 13);
		endCal.set(Calendar.MINUTE, 0);
		endCal.set(Calendar.SECOND, 0);
		endCal.set(Calendar.MILLISECOND, 0);

		Date now = new Date();
		return now.getTime() > startCal.getTime().getTime() && now.getTime() < endCal.getTime().getTime();
	}

	public static String getOpenTime(String commodityNo) {
		// 纽约
		if ("CL".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("GC".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("SL".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("HG".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		}
		// 芝加哥
		else if ("NQ".endsWith(commodityNo)) {
			return isNYSummerZone() ? "07:00:00" : "08:00:00";
		} else if ("BP".endsWith(commodityNo)) {
			return isNYSummerZone() ? "07:00:00" : "08:00:00";
		} else if ("CD".endsWith(commodityNo)) {
			return isNYSummerZone() ? "07:00:00" : "08:00:00";
		} else if ("EC".endsWith(commodityNo)) {
			return isNYSummerZone() ? "07:00:00" : "08:00:00";
		} else if ("AD".endsWith(commodityNo)) {
			return isNYSummerZone() ? "07:00:00" : "08:00:00";
		}
		// 瑞士
		else if ("DAX".endsWith(commodityNo)) {
			return isNYSummerZone() ? "14:00:00" : "15:00:00";
		}
		// 香港
		else if ("HIS".endsWith(commodityNo)) {
			return "09:15:00";
		} else if ("MHI".endsWith(commodityNo)) {
			return "09:15:00";
		}
		// A50指数
		else if ("CN".endsWith(commodityNo)) {
			return "09:00:00";
		}
		// 其他
		else {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		}
	}

	public static String getCloseTime(String commodityNo) {
		// 纽约
		if ("CL".endsWith(commodityNo)) {
			return isNYSummerZone() ? "05:00:00" : "06:00:00";
		} else if ("GC".endsWith(commodityNo)) {
			return isNYSummerZone() ? "05:00:00" : "06:00:00";
		} else if ("SL".endsWith(commodityNo)) {
			return isNYSummerZone() ? "05:00:00" : "06:00:00";
		} else if ("HG".endsWith(commodityNo)) {
			return isNYSummerZone() ? "05:00:00" : "06:00:00";
		}
		// 芝加哥
		else if ("NQ".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("BP".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("CD".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("EC".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		} else if ("AD".endsWith(commodityNo)) {
			return isNYSummerZone() ? "06:00:00" : "07:00:00";
		}
		// 瑞士
		else if ("DAX".endsWith(commodityNo)) {
			return isNYSummerZone() ? "04:00:00" : "05:00:00";
		}
		// 香港
		else if ("HIS".endsWith(commodityNo)) {
			return "00:55:00";
		} else if ("MHI".endsWith(commodityNo)) {
			return "00:55:00";
		}
		// A50指数
		else if ("CN".endsWith(commodityNo)) {
			return "04:40:00";
		}
		// 其他
		else {
			return isNYSummerZone() ? "05:00:00" : "06:00:00";
		}
	}

}
