/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 动态代理。
 *
 * @author 树悉猿
 * @date 2021/6/16
 * @version 1.0
 */
public class InvokeProxy {
	private Object execute;

	private Method method;

	private Object[] args;

	/**
	 * 这是用反射机制获取相关实例
	 *
	 * @param classFullName full name
	 * @param methodName method name
	 * @param args args
	 * @param parameterTypes parameter type
	 */
	public InvokeProxy(String classFullName, String methodName, Object[] args, Class<?>[] parameterTypes)
			throws ClassNotFoundException, SecurityException, NoSuchMethodException, InstantiationException, IllegalAccessException {
		if (this.getClass().getSimpleName().equalsIgnoreCase(classFullName)) {
			throw new RuntimeException("不能自己调用自己！");
		}
		Class<?> clazz = Class.forName(classFullName);
		this.method = clazz.getMethod(methodName, parameterTypes);
		this.execute = clazz.newInstance(); // 此方法对应构造器，调用目标时必须实现对应的构造方法，无参数时对应无参构造，代理目标必须实现无参构造。
		// clazz.getClass()返回的是静态区的class对象仅能访问其静态元素(如static方法)，而非静态资源只能实例化以后在实例区通过实例对象引用拿到。
		this.args = args;
	}

	/**
	 * 用反射机制调用执行方法
	 *
	 * @return 如果方法返回void，返回null，否则返回方法执行结果。
	 * @throws IllegalArgumentException illegal arg
	 * @throws IllegalAccessException  illegal access
	 * @throws InvocationTargetException invocation
	 */
	public Object invoke() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (null != this.execute && null != this.method) {
			return this.method.invoke(this.execute, this.args);
		}
		else {
			throw new RuntimeException("请先正确初始化……");
		}
	}

	/**
	 * 实现动态代理
	 */
	public <T> InvokeProxy(T target, String methodName, Object[] args, Class<?>[] parameterTypes) {
		// Proxy.newProxyInstance();
	}
}