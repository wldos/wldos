/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

/**
 *
 * 汉字工具类
 *
 * @author 树悉猿
 * @date 2021年12月19日
 * @version V1.0
 */
public class ChineseUtils {
	/***
	 * 将汉字转成拼音(取首字母或全拼)
	 * 
	 * @param hanZi  中文字面值
	 * @param full  是否全拼
	 * @return 拼音
	 */
	public static String hanZi2Pinyin(String hanZi, boolean full) {
		// ^[\u2E80-\u9FFF]+$ 匹配所有东亚区的语言 ^[\u4E00-\u9FFF]+$ 匹配简体和繁体 ^[\u4E00-\u9FA5]+$  匹配简体
		String regExp = "^[\u4E00-\u9FFF]+$";
		StringBuilder sb = new StringBuilder();
		if (hanZi == null || "".equals(hanZi.trim())) {
			return "";
		}
		String pinyin = "";
		int len = hanZi.length();
		for (int i = 0; i < len; i++) {
			char unit = hanZi.charAt(i);
			if (match(String.valueOf(unit), regExp))// 是汉字，则转拼音
			{
				pinyin = singleHanZi2Pinyin(unit);
				if (full) {
					sb.append(pinyin);
				} else {
					sb.append(pinyin.charAt(0));
				}
			} else {
				sb.append(unit);
			}
		}
		return sb.toString();
	}

	/***
	 * 将单个汉字转成拼音
	 * 
	 * @param hanZi 单个中文
	 * @return 拼音
	 */
	private static String singleHanZi2Pinyin(char hanZi) {
		HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
		outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		String[] res;
		StringBuilder sb = new StringBuilder();
		try {
			res = PinyinHelper.toHanyuPinyinStringArray(hanZi, outputFormat);
			sb.append(res[0]); // 对于多音字，只用第一个拼音
		} catch (Exception e) {
			return "";
		}
		return sb.toString();
	}

	/***
	 * @param str   源字符串
	 * @param regex 正则表达式
	 * @return 是否匹配
	 */
	public static boolean match(String str, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		return matcher.find();
	}

	private ChineseUtils() {
		throw new IllegalStateException("Utility class");
	}
}
