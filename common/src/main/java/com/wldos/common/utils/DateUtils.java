/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类。
 * 
 * @author 树悉猿
 * @date 2021/07/16
 * @version V1.0
 */
public class DateUtils {

	public static final String DATE_PATTER = "yyyy-MM-dd";

	public static final String TIME_PATTER = "yyyy-MM-dd HH:mm:ss";
	
	private DateUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		return sdf.format(date);
	}

	/**
	 * 取日期
	 *
	 * @return 今天日期字符串
	 */
	public static String getDay(Date date) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd");

		return sdf.format(date);
	}
	
	public static String getTime() {

		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTER);

		return sdf.format(new Date());
	}

	public static Date parse(String dateString, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		Date date;
		try {
			date = sdf.parse(dateString);
		}
		catch (ParseException ex) {
			return null;
		}
		return date;
	}

	/**
	 * 转换格式为"yyyy-MM-dd HH:mm:ss"的日期字符串为Date类型
	 * @param dateString 日期字符串
	 * @return 日期类型
	 */
	public static Date parse(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat(TIME_PATTER);
		Date date;
		try {
			date = sdf.parse(dateString);
		}
		catch (ParseException ex) {
			return null;
		}
		return date;
	}

	public static int getValidDaysByYM(String dateString){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTER);
		ParsePosition pos = new ParsePosition(0);
		Date date = sdf.parse(dateString + "-01", pos);

		return getValidDays(date);
	}

	public static int getValidDays(Date date) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(date);
		curCal.add(Calendar.MONTH, 1);
		int dd = curCal.get(Calendar.DATE);
		curCal.add(Calendar.DATE, -dd);
		
		return curCal.get(Calendar.DATE);
	}

	public static Date getLastDayByDate(Date date) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(date);
		int dd = curCal.get(Calendar.DATE);
		int max = getValidDays(date);
		curCal.add(Calendar.DATE, max - dd);
		
		return curCal.getTime();
	}

	public static Date getFirstDayByDate(Date date) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(date);
		curCal.add(Calendar.MONTH, 1);
		int dd = curCal.get(Calendar.DATE) - 1;
		curCal.add(Calendar.DATE, -dd);

		return curCal.getTime();
	}

	public static Date getFirstDayByCurDate() {
		return getFirstDayByDate(new Date());
	}

	public static String getDayOfWeek(Date date) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(date);
		return getWeek(curCal.get(Calendar.WEEK_OF_MONTH));
	}

	public static String getDayOfWeekByCurDate() {
		return getDayOfWeek(new Date());
	}

	private static String getWeek(int week) {
		String weekName;
		switch (week) {
			case 1:
				weekName = "星期日";
				break;
			case 2:
				weekName = "星期一";
				break;
			case 3:
				weekName = "星期二";
				break;
			case 4:
				weekName = "星期三";
				break;
			case 5:
				weekName = "星期四";
				break;
			case 6:
				weekName = "星期五";
				break;
			case 7:
				weekName = "星期六";
				break;
			default:
				throw new IllegalStateException("Unexpected value: " + week);
		}
		return weekName;
	}

	public static int compareDateTime(Date beginDate, Date endDate) {
		Date bDate = parse(format(beginDate, DATE_PATTER), DATE_PATTER);

		Calendar startCal = Calendar.getInstance();
		assert bDate != null;
		startCal.setTime(bDate);
		if ((endDate == null) || (endDate.before(getMinDate()))) {
			endDate = new Date();
		}
		Date eDate = parse(format(endDate, DATE_PATTER), DATE_PATTER);
		Calendar endCal = Calendar.getInstance();
		assert eDate != null;
		endCal.setTime(eDate);
		if (startCal.after(endCal)) {
			int num = 0;
			while (endCal.before(startCal)) {
				endCal.add(Calendar.DATE, 1);
				num++;
			}
			return -num;
		}
		int num = 0;
		while (startCal.before(endCal)) {
			startCal.add(Calendar.DATE, 1);
			num++;
		}
		return num;
	}

	/**
	 * 获取日期差，精确到秒
	 *
	 * @param beginDate 开始日期
	 * @param endDate 结束日期
	 * @return 日期差的秒数
	 */
	public static double getDateDiffInSeconds(Date beginDate, Date endDate) {
		Calendar cldBegin = Calendar.getInstance();
		cldBegin.setTime(beginDate);

		if ((endDate == null) || (endDate.before(getMinDate()))) {
			endDate = new Date();
		}

		Calendar cldEnd = Calendar.getInstance();
		cldEnd.setTime(endDate);

		int hour = cldEnd.get(Calendar.HOUR) - cldBegin.get(Calendar.HOUR);
		int min = cldEnd.get(Calendar.MINUTE) - cldBegin.get(Calendar.MINUTE);
		int sec = cldEnd.get(Calendar.SECOND) - cldBegin.get(Calendar.SECOND);

		int day = compareDateTime(beginDate, endDate);

		return  (double) sec + min * 60 + hour * 60 * 60 + day * 24 * 60 * 60;
	}

	/**
	 * 比对两个日期是否相等
	 *
	 * @param d1 日期1
	 * @param d2 日期2
	 * @return 是否相等
	 */
	public static boolean compareDate(Date d1, Date d2) {
		Calendar curCal = Calendar.getInstance();

		curCal.setTime(d1);
		int y1 = curCal.get(Calendar.YEAR);
		int m1 = curCal.get(Calendar.MONTH);

		curCal.setTime(d2);
		int y2 = curCal.get(Calendar.YEAR);
		int m2 = curCal.get(Calendar.MONTH);

		return (y1 == y2) && (m1 == m2);
	}

	/**
	 * 通过日期获取第几周
	 *
	 * @param date 日期
	 * @return 一年的第几周
	 */
	public static int getWeekByDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	private static Date getMinDate() {
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTER);
		ParsePosition pos = new ParsePosition(0);

		return sdf.parse("1990-1-1", pos);
	}

	public static Timestamp formatTimestamp(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date timeDate;
		try {
			timeDate = sdf.parse(dateString);

			return new Timestamp(
					timeDate.getTime());
		}
		catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 解析字符串为sql date
	 *
	 * @param dateString 日期字符串
	 * @return 转换的sql date
	 */
	public static java.sql.Date parseSQLDate(String dateString) {
		Date d = parse(dateString, DATE_PATTER);

		return d == null ? null : new java.sql.Date(d.getTime());
	}

	/**
	 * 日期转sql date（时间戳）
	 *
	 * @param date 日期实例
	 * @return 时间戳
	 */
	public static Timestamp convSQLDate(Date date) {

		return date == null ? null : new Timestamp(date.getTime());
	}
}