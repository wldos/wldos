/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 反射工具类。
 *
 * @Title ReflectionUtils
 * @Package com.wldos.support.util
 * @author 树悉猿
 * @date 2021-04-14
 * @version V1.0
 */
public class ReflectUtils {

	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static Logger logger = LoggerFactory.getLogger(ReflectUtils.class);

	/**
	 * 调用对象的getter方法，支持获取对象子类的getter。
	 *
	 * @param obj
	 * @param propName
	 * @return Object 返回get结果
	 */
	public static Object invokeGetter(Object obj, String propName) {
		Object object = obj;
		for (String name : StringUtils.split(propName, ".")) {
			String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(name);
			object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
		}
		return object;
	}

	/**
	 * 嵌套调用对象的setter方法。
	 *
	 * @param obj 对象实例
	 * @param propName 对象的属性名称
	 * @param value 要设置的属性值
	 */
	public static void invokeSetter(Object obj, String propName, Object value) {
		Object object = obj;
		String[] names = StringUtils.split(propName, ".");
		for (int i = 0; i < names.length; i++) {
			if (i < names.length - 1) {
				String getterMethodName = GETTER_PREFIX + StringUtils.capitalize(names[i]);
				object = invokeMethod(object, getterMethodName, new Class[] {}, new Object[] {});
			}
			else {
				String setterMethodName = SETTER_PREFIX + StringUtils.capitalize(names[i]);
				invokeMethodByName(object, setterMethodName, new Object[] { value });
			}
		}
	}

	/**
	 * 直接读取对象域值，忽略私有、保护作用域，
	 *
	 * @param obj 要读取的对象实例
	 * @param fieldName 要读取的域名称
	 * @return 读取到的域值
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		try {
			result = field.get(obj);
		}
		catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}

	/**
	 * 临时调用对象的某个方法，忽略私有或保护作用域。支持重载方法。
	 *
	 * @param obj
	 * @param methodName
	 * @param paramTypes
	 * @param args
	 * @return
	 */
	public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] paramTypes,
			final Object[] args) {
		Method method = getAccessibleMethod(obj, methodName, paramTypes);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		}
		catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}


	/**
	 * 临时调用对象的某个方法，忽略私有或保护作用域。只匹配函数名，如果有多个重载函数仅调用首个。
	 *
	 * @param obj
	 * @param methodName
	 * @param args
	 * @return
	 */
	public static Object invokeMethodByName(final Object obj, final String methodName, final Object[] args) {
		Method method = getAccessibleMethodByName(obj, methodName);
		if (method == null) {
			throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
		}

		try {
			return method.invoke(obj, args);
		}
		catch (Exception e) {
			throw convertReflectionExceptionToUnchecked(e);
		}
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问，向上转型到Object仍未找到, 返回null。
	 *
	 * @param obj
	 * @param fieldName
	 * @return
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		Validate.notNull(obj, "object can't be null");
		Validate.notBlank(fieldName, "fieldName can't be blank");
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			}
			catch (NoSuchFieldException e) {
				// Field不在当前类定义,继续向上转型
				continue;// new add
			}
		}
		return null;
	}


	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问，支持重载方法。
	 * 用于频繁访问方法的场景. 先用本函数获得Method,然后调用Method.invoke(Object obj, Object... args)。
	 *
	 * @param obj
	 * @param methodName
	 * @param parameterTypes
	 * @return
	 */
	public static Method getAccessibleMethod(final Object obj, final String methodName,
			final Class<?>... parameterTypes) {
		Validate.notNull(obj, "object can't be null");
		Validate.notBlank(methodName, "methodName can't be blank");

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
			try {
				Method method = searchType.getDeclaredMethod(methodName, parameterTypes);
				makeAccessible(method);
				return method;
			}
			catch (NoSuchMethodException e) {
				// Method不在当前类定义,继续向上转型
				continue;
			}
		}
		return null;
	}

	/**
	 * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问，不支持重载方法。向上转型到Object仍无法找到, 返回null。
	 * 用于频繁访问方法的场景. 先用本函数获得Method,然后调用Method.invoke(Object obj, String methodName)。
	 *
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Method getAccessibleMethodByName(final Object obj, final String methodName) {
		Validate.notNull(obj, "object can't be null");
		Validate.notBlank(methodName, "methodName can't be blank");

		for (Class<?> searchType = obj.getClass(); searchType != Object.class; searchType = searchType.getSuperclass()) {
			Method[] methods = searchType.getDeclaredMethods();
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					makeAccessible(method);
					return method;
				}
			}
		}
		return null;
	}

	/**
	 * 改变private/protected的方法为public，尽量不调用涉及更新的语句，避免JDK的SecurityManager预警。
	 *
	 * @param method
	 */
	public static void makeAccessible(Method method) {
		if ((!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
				&& !method.isAccessible()) {
			method.setAccessible(true);
		}
	}


	/**
	 * 改变private/protected的成员变量为public，尽量不调用涉及更新的语句，避免JDK的SecurityManager预警。
	 *
	 * @param field
	 */
	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
				.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}

	/**
	 * 通过反射获取模糊Class类型实例的类定义中明确声明的泛型参数类型, 注意泛型必须定义在父类处。
	 * 如无法找到, 返回Object.class。
	 *
	 * 实例：
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz 要反射的模糊类型实例
	 * @return 返回第一个泛型类型, 如果无法确定，返回Object类型
	 */
	public static <T> Class<T> getClassGenricType(final Class clazz) {
		return getClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射, 获得Class定义中声明的父类的泛型参数的类型。
	 * 如无法找到, 返回Object.class。
	 *
	 * 如：
	 * public UserDao extends HibernateDao<User,Long>
	 *
	 * @param clazz 要反射的模糊类型实例
	 * @param index 泛型参数的索引，从0开始.
	 * @return 返回第一个泛型类型, 如果无法确定，返回Object类型
	 */
	public static Class getClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			logger.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			logger.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			logger.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	/**
	 * 将反射时的checked exception转换为unchecked exception。
	 *
	 * @param e
	 * @return
	 */
	public static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException) {
			return new IllegalArgumentException(e);
		}
		else if (e instanceof InvocationTargetException) {
			return new RuntimeException(((InvocationTargetException) e).getTargetException());
		}
		else if (e instanceof RuntimeException) {
			return (RuntimeException) e;
		}
		return new RuntimeException("Unexpected Checked Exception.", e);
	}

	/**
	 * 判断某个对象是否拥有某个属性。
	 *
	 * @param obj 对象
	 * @param fieldName 属性名
	 * @return 有属性返回true，无属性返回false
	 */
	public static boolean hasField(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);
		if (field == null) {
			return false;
		}

		return true;
	}
}
