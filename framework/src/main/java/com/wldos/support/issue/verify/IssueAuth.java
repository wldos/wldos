/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.verify;

import java.lang.reflect.Method;

import com.wldos.support.OStart;
import com.wldos.support.issue.WLDOSUtilities;
import com.wldos.support.issue.verify.annotation.Issue;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
@Aspect
@Order(1)
@Component
public class IssueAuth {
	private Verifier verifier;
	public IssueAuth(OStart beanHelper, VerifyEnv verifyEnv) {
		try {
			this.verifier = (Verifier) WLDOSUtilities.getInstance().loadClass("wldos_vri").newInstance();
		}
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException ignored) {
		}
		this.verifier.install(verifyEnv, beanHelper.getBeanFactory());
	}

	@Pointcut("@within(com.wldos.support.issue.verify.annotation.Issue)")
	public void isLicenseTypePointcut() {
	}

	@Pointcut("@annotation(com.wldos.support.issue.verify.annotation.Issue)")
	public void isLicenseMethodPointcut() {
	}

	@Before("isLicenseTypePointcut()")
	public void beforeIsLicenseTypePointcutCheck() {
		this.verifier.check();
	}

	@Before("isLicenseMethodPointcut()")
	public void beforeIsLicenseMethodPointcutCheck() {
		this.verifier.check();
	}

	private Issue getAnnotation(MethodSignature signature) {
		Method method = signature.getMethod();
		Issue issue = method.getAnnotation(Issue.class);

		if (issue == null) {
			issue = AnnotationUtils.findAnnotation(method.getDeclaringClass(), Issue.class);
		}
		return issue;
	}
}