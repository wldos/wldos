/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.common;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * spring容器工具类。
 *
 * @author 元悉宇宙
 * @date 2021/12/28
 * @version 1.0
 */
@Component
public class GetBeanHelper implements ApplicationContextAware {
	private static GetBeanHelper instance;
	
	private ApplicationContext applicationContext;

	private AutowireCapableBeanFactory beanFactory;

	@Override
	public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		this.beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
		instance = this; // 保存静态引用
	}
	
	/**
	 * 获取 GetBeanHelper 实例（静态方法，用于接口默认方法中获取 Bean）
	 * 
	 * @return GetBeanHelper 实例，如果未初始化则返回 null
	 */
	public static GetBeanHelper getInstance() {
		return instance;
	}

	public AutowireCapableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * 请输入类名称
	 *
	 * @param className 类名称，不含包路径
	 * @return bean实例
	 * @throws BeansException bean异常
	 * @throws IllegalArgumentException 非法参数异常
	 */
	public Object getBean(String className) throws BeansException, IllegalArgumentException {
		if (className == null || className.length() <= 0) {
			throw new IllegalArgumentException("className为空");
		}

		String beanName = null;
		if (className.length() > 1) {
			beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
		}
		else {
			beanName = className.toLowerCase();
		}
		return applicationContext.getBean(beanName);
	}

	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return this.beanFactory.getBean(requiredType);
	}
}