/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.EnumUtils;
import org.json.JSONObject;

/**
 * @Title: ObjectUtil
 * @Package
 * @Description: 类操作工具类。
 * @author 树悉猿
 * @date 2021-04-13
 * @version V1.0
 */
public final class ObjectUtil {
	/**
	 * Object属性值转数组，自动排除null值。
	 * @param entity
	 * @return
	 */
	public static Object[] toArray(Object entity) {
		JSONObject obj = new JSONObject(entity);
		// JSONObject.toMap默认会排除null值
		Map<String, Object> map = obj.toMap();
		Object[] params = map.values().toArray();

		return params;
	}

	/**
	 * 对象转map<String, Object>，自动排除null值属性。
	 * @param entity
	 * @return
	 */
	public static Map<String, Object> toMap(Object entity) {
		// JSONObject.toMap默认会排除null值
		JSONObject obj = new JSONObject(entity);
		return obj.toMap();
	}

	/**
	 * 枚举转map
	 *
	 * @param enumClass
	 * @param <E>
	 * @return
	 */
	public static <E extends Enum<E>> Map<String, E> toEnumMap(final Class<E> enumClass) {
		return EnumUtils.getEnumMap(enumClass);
	}


	public static boolean isBlank(Object obj) {
		if (obj == null || obj.toString().trim().equals("")) {
			return true;
		}

		return false;
	}

	public static boolean isBlank(Collection obj) {
		if (obj == null || obj.isEmpty()) {
			return true;
		}

		return false;
	}

	/**
	 * 判定存在为空的参数
	 *
	 * @param obj
	 * @return
	 */
	public static boolean existsBlank(Object ...obj) {
		if (obj == null)
			return true;

		return Arrays.stream(obj).anyMatch(o -> {
			return isBlank(o);
		});
	}

	public static String string(Object o) {
		if (isBlank(o))
			return "";
		return o.toString();
	}

	/**
	 * 实体对象转成Map
	 *
	 * @param obj 实体对象
	 * @return
	 */
	public static Map object2Map(Object obj) {
		Map map = new HashMap<>();
		if (obj == null) {
			return map;
		}
		@SuppressWarnings("rawtypes")
		Class clazz = obj.getClass();
		Field[] fields = clazz.getDeclaredFields();
		try {
			for (Field field : fields) {
				field.setAccessible(true);
				map.put(field.getName(), field.get(obj));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Map转成实体对象
	 *
	 * @param map   map实体对象包含属性
	 * @param clazz 实体对象类型
	 * @return
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
				field.setAccessible(true);
				field.set(obj, map.get(field.getName()));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
}
