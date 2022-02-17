/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * 类操作工具类。
 *
 * @author 树悉猿
 * @date 2021-04-13
 * @version V1.0
 */
@SuppressWarnings({"rawtypes"})
public final class ObjectUtils {
	/**
	 * Object属性值转数组，自动排除null值。
	 * @param entity 对象
	 * @return 对象数组
	 */
	public static Object[] toArray(Object entity) {
		JSONObject obj = new JSONObject(entity);
		// JSONObject.toMap默认会排除null值
		Map<String, Object> map = obj.toMap();

		return map.values().toArray();
	}

	/**
	 * 对象转map<String, Object>，自动排除null值属性。
	 * @param entity 对象
	 * @return map
	 */
	public static Map<String, Object> toMap(Object entity) {
		// JSONObject.toMap默认会排除null值
		JSONObject obj = new JSONObject(entity);
		return obj.toMap();
	}

	/**
	 * 枚举转map
	 *
	 * @param enumClass 枚举类
	 * @param <E> 枚举类类型
	 * @return map
	 */
	public static <E extends Enum<E>> Map<String, E> toEnumMap(final Class<E> enumClass) {
		return EnumUtils.getEnumMap(enumClass);
	}

	public static boolean isBlank(Object obj) {
		return obj == null || obj.toString().trim().equals("");
	}

	public static boolean isBlank(Collection obj) {
		return obj == null || obj.isEmpty();
	}

	/**
	 * 判定存在为空的参数
	 *
	 * @param obj 对象
	 * @return 是否为空
	 */
	public static boolean existsBlank(Object ...obj) {
		if (obj == null)
			return true;

		return Arrays.stream(obj).anyMatch(ObjectUtils::isBlank);
	}

	public static String string(Object o) {
		if (isBlank(o))
			return "";
		return o.toString();
	}

	public static String nvlToZero(Object o) {
		if (isBlank(o))
			return "0";
		return o.toString();
	}

	/**
	 * 实体对象转成Map,访问私有属性：{@code field.setAccessible(true);}。
	 *
	 * @param obj 实体对象
	 * @return map
	 */
	public static Map<String, Object> object2Map(Object obj) {
		Map<String, Object> map = new HashMap<>();
		if (obj == null) {
			return map;
		}

		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		try {
			for (Field field : fields) {
				if (field.isAccessible())
					map.put(field.getName(), field.get(obj));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Map转成实体对象, 访问私有属性：{@code field.setAccessible(true);}。
	 *
	 * @param map   map实体对象包含属性
	 * @param clazz 实体对象类型
	 * @return object
	 */
	public static Object map2Object(Map map, Class clazz) {
		if (map == null) {
			return null;
		}
		Object obj = null;
		try {
			obj = clazz.newInstance();

			Field[] fields = obj.getClass().getDeclaredFields();
			for (Field field : fields) {
				int mod = field.getModifiers();
				if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
					continue;
				}
				if (field.isAccessible())
					field.set(obj, map.get(field.getName()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 * html转字符串（清除所有html标签）
	 *
	 * @param html html字符串
	 * @return 纯文本
	 */
	public static String htmlToText(String html) {
		return html == null ? "" : html.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>","")
				.replaceAll("<style[^>]*?>[\\s\\S]*?<\\/style>","")
				.replaceAll("<[^>]+>","").trim();
	}

	/**
	 * html转字符串（清除html标签，保留p标签）
	 *
	 * @param html html字符串
	 * @return 带p标签的纯文本
	 */
	public static String htmlToGraph(String html) {
		return html == null ? "" : html.replaceAll("<script[^>]*?>[\\s\\S]*?<\\/script>","")
				.replaceAll("<style[^>]*?>[\\s\\S]*?<\\/style>","")
				.replaceAll("<(?!\\/?[pP])[^>]+>","").trim();
	}

	/**
	 * 手机号码脱敏，隐藏中间4位
	 *
	 * @param phoneNum 手机号码
	 * @return 脱敏字符串
	 */
	public static String hidePhone(String phoneNum) {
		if (ObjectUtils.isBlank(phoneNum))
			return phoneNum;
		return phoneNum.replaceAll("(\\w{3})\\w*(\\w{4})", "$1****$2");
	}

	/**
	 * 姓名脱敏，隐藏名字只留首字
	 *
	 * @param fullName 姓名全称
	 * @return 脱敏字符串
	 */
	public static String hideName(String fullName) {
		if (ObjectUtils.isBlank(fullName))
			return fullName;
		String name = StringUtils.left(fullName, 1);
		return StringUtils.rightPad(name, StringUtils.length(fullName), "*");
	}

	/**
	 * 身份证号脱敏，前6后3，中间隐藏
	 *
	 * @param idNum 身份证号
	 * @return 脱敏身份证
	 */
	public static String hideID(String idNum) {
		if (ObjectUtils.isBlank(idNum))
			return idNum;
		return StringUtils.left(idNum,
				6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(idNum, 3),
				StringUtils.length(idNum), "*"), "******"));
	}

	/**
	 * 地址脱敏，第4位起隐藏8位
	 * @param address 地址
	 * @return 脱敏地址
	 */
	public static String hideAddress(String address) {
		if (ObjectUtils.isBlank(address))
			return address;

		String left = StringUtils.left(address, 3);
		String leftPad = StringUtils.leftPad(StringUtils.right(address,
				address.length()-11), StringUtils.length(address), "*");
		String removeStart = StringUtils.removeStart(leftPad, "***");

		return left.concat(removeStart);
	}

	/**
	 * 判断字符串(滤掉html标签)字符数是否超过指定长度
	 *
	 * @param content 字符串
	 * @param maxLength 指定最大长度
	 * @return 是否
	 */
	public static boolean isOutBoundsClearHtml(String content, int maxLength) {
		return ObjectUtils.htmlToText(content).getBytes(StandardCharsets.UTF_8).length > maxLength;
	}

	/**
	 * 判断字符串字符数是否超过指定长度
	 *
	 * @param content 字符串
	 * @param maxLength 指定最大长度
	 * @return 是否
	 */
	public static boolean isOutBounds(String content, int maxLength) {
		return content.getBytes(StandardCharsets.UTF_8).length > maxLength;
	}

	private ObjectUtils() {
		throw new IllegalStateException("Utility class");
	}
}