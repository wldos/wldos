/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名称中驼峰与下划线互转。
 *
 * @author 树悉猿
 * @date 2021/5/9
 * @version 1.0
 */
public class NameConvert {

	private static final Pattern linePattern = Pattern.compile("_([a-z])");

	private static final Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

	/**
	 * 下划线转驼峰
	 *
	 * @param column 列名
	 * @return 属性名
	 */
	public static String underlineToHump(String column) {
		Matcher matcher = linePattern.matcher(column);
		StringBuffer sb = new StringBuffer(); // 10万次以内的字符串连接，优于StringBuilder，另外需要线程安全，Builder不支持
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 驼峰转下划线
	 *
	 * @param name 属性名
	 * @return 字段名
	 */
	public static String humpToUnderLine(String name) {
		StringBuffer sb = new StringBuffer(); // 10万次以内的字符串连接，优于StringBuilder，另外需要线程安全，Builder不支持
		Matcher matcher = humpPattern.matcher(name);
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	private NameConvert() {
		throw new IllegalStateException("Utility class");
	}
}