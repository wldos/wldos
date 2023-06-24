/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.base;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring容器工具类。
 *
 * @author 树悉猿
 * @date 2021/12/28
 * @version 1.0
 */
@Component
public class GetBeanHelper implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	private AutowireCapableBeanFactory beanFactory;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		this.beanFactory = this.applicationContext.getAutowireCapableBeanFactory();
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
}