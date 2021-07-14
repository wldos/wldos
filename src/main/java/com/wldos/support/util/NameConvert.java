/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 名称中驼峰与下划线互转。
 *
 * @Title NameConvert
 * @Package com.wldos.support.util
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/9
 * @Version 1.0
 */
public class NameConvert {

	private static Pattern linePattern = Pattern.compile("_([a-z])");

	private static Pattern humpPattern = Pattern.compile("\\B(\\p{Upper})(\\p{Lower}*)");

	/**
	 * 下划线转驼峰
	 *
	 * @param column
	 * @return
	 */
	public static String underlineToHump(String column) {
		Matcher matcher = linePattern.matcher(column);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 驼峰转下划线
	 *
	 * @param name
	 * @return
	 */
	public static String humpToUnderLine(String name) {
		StringBuffer sb = new StringBuffer();
		Matcher matcher = humpPattern.matcher(name);
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);

		return sb.toString();
	}

	public static void main(String[] args) {
		String key = "createIp,updateIp,isValid,updateTime,archId,userId,orgId,orgType,createBy,versions,updateBy,createTime,id,comId";

		System.out.println(humpToUnderLine(key));
	}
}