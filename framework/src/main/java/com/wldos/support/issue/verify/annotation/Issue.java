/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.verify.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.wldos.support.issue.IssueConstants;

/**
 * 用于标识需要license保护的方法或类。
 *
 * @author 树悉猿
 * @date 2022/1/24
 * @version 1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD,ElementType.TYPE})
// @Conditional(LicenseCondition.class) @todo 待功能清单梳理清晰，版本功能矩阵明确后，在确定各版本的模块屏蔽清单
public @interface Issue {
	/**
	 * license类型：免费版、标准版、旗舰版
	 *
	 * @return String
	 */
	String value() default IssueConstants.DEFAULT_VERSION;
}