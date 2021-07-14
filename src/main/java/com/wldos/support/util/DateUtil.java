/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Title: DateUtils
 * @Package
 * @Description: 日期工具类。
 * @author 树悉猿
 * @date
 * @version V1.0
 */
public class DateUtil {

	public static final String DATE_PATTER = "yyyy-MM-dd";

	public static final String TIME_PATTER = "yyyy-MM-dd HH:mm:ss";

	public static String format(java.util.Date date, String pattern) {
		String formatDate = "";
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		formatDate = sdf.format(date);
		return formatDate;
	}

	/**
	 * 取日期
	 *
	 * @param date
	 * @return
	 */
	public static String getDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String formatDate = sdf.format(new java.util.Date());
		return formatDate;
	}

	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String formatDate = sdf.format(new java.util.Date());
		return formatDate;
	}

	public static java.util.Date parse(String dateString, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		java.util.Date date = null;
		try {
			date = sdf.parse(dateString);
		}
		catch (ParseException ex) {
			return null;
		}
		return date;
	}

	public static int getLastDay() {
		return 0;
	}

	public static int getValidDaysByYM(String dateString)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		java.util.Date date = sdf.parse(dateString + "-01", pos);
		if (pos.getIndex() <= 0) {
			throw new Exception("指定的日期：" + dateString + "无效");
		}
		return getValidDays(date);
	}

	public static int getValidDays(java.util.Date date) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(date);
		curCal.add(2, 1);
		int dd = curCal.get(5);
		curCal.add(5, -dd);
		dd = curCal.get(5);
		return dd;
	}

	public static java.util.Date getLastDayByDate(java.util.Date dateString) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(dateString);
		int dd = curCal.get(5);
		int max = getValidDays(dateString);
		curCal.add(5, max - dd);
		return curCal.getTime();
	}

	public static java.util.Date getFirstDayByDate(java.util.Date dateString) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(dateString);
		curCal.add(2, 1);
		int dd = curCal.get(5);
		dd--;
		curCal.add(5, -dd);
		return curCal.getTime();
	}

	public static java.util.Date getFirstDayByCurDate() {
		return getFirstDayByDate(new java.util.Date());
	}

	public static String getDayOfWeek(java.util.Date dateString) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(dateString);
		return getWeek(curCal.get(4));
	}

	public static String getDayOfWeekByCurDate() {
		return getDayOfWeek(new java.util.Date());
	}

	private static String getWeek(int week) {
		String weekName = "";
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
		}
		return weekName;
	}

	public static int compareDateTime(java.util.Date beginDate, java.util.Date endDate) {
		java.util.Date _d1 = parse(format(beginDate, "yyyy-MM-dd"), "yyyy-MM-dd");

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(_d1);
		if ((endDate == null) || (endDate.before(getMinDate()))) {
			endDate = new java.util.Date();
		}
		java.util.Date _d2 = parse(format(endDate, "yyyy-MM-dd"), "yyyy-MM-dd");
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(_d2);
		if (startCal.after(endCal)) {
			int num = 0;
			while (endCal.before(startCal)) {
				endCal.add(5, 1);
				num++;
			}
			return -num;
		}
		int num = 0;
		while (startCal.before(endCal)) {
			startCal.add(5, 1);
			num++;
		}
		return num;
	}

	public static double getAbsoluteSec(java.util.Date beginDate, java.util.Date endDate) {
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(beginDate);
		if ((endDate == null) || (endDate.before(getMinDate()))) {
			endDate = new java.util.Date();
		}
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(endDate);

		int hour = cal2.get(10) - cal1.get(10);
		int min = cal2.get(12) - cal1.get(12);
		int sec = cal2.get(13) - cal1.get(13);

		int day = compareDateTime(beginDate, endDate);

		double secs = sec + min * 60 + hour * 60 * 60 + day * 24 * 60 * 60;

		return secs;
	}

	public static boolean compareTwoSec(java.util.Date date1, java.util.Date date2) {
		Calendar curCal = Calendar.getInstance();
		curCal.setTime(date1);
		int yy1 = curCal.get(1);
		int mm1 = curCal.get(2);
		curCal.setTime(date2);
		int yy2 = curCal.get(1);
		int mm2 = curCal.get(2);
		if ((yy1 == yy2) && (mm1 == mm2)) {
			return true;
		}
		return false;
	}

	public static int getWhichWeek(java.util.Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(3);
	}

	private static java.util.Date getMinDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos1 = new ParsePosition(0);
		java.util.Date dd = sdf.parse("1990-1-1", pos1);
		return dd;
	}

	public static Timestamp formatTimestamp(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			java.util.Date timeDate = sdf.parse(dateString);
			return new Timestamp(
					timeDate.getTime());
		}
		catch (Exception localException) {
		}
		return null;
	}

	public static java.sql.Date parseSQLDate(String dateString) {
		java.util.Date d = parse(dateString, "yyyy-MM-dd");
		if (d != null) {
			java.sql.Date sq = new java.sql.Date(d.getTime());
			return sq;
		}
		return null;
	}

	public static Timestamp convSQLDate(Date date) {
		if (date != null) {
			Timestamp sq = new Timestamp(date.getTime());
			return sq;
		}
		return null;
	}
}
