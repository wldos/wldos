/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.issue.verify.annotation;

import java.util.Map;

import com.wldos.support.issue.IssueConstants;
import com.wldos.support.issue.enums.IssueVersionEnum;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * license屏蔽模块条件，被license注解的模块，注解的参数值命中license的系统的版本号才初始化模块，否则屏蔽模块。
 *
 * @author 树悉猿
 * @date 2022/1/28
 * @version 1.0
 */
public class IssueCondition implements Condition {
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		Map<String, Object> meta = metadata.getAnnotationAttributes(Issue.class.getName());
		assert meta != null;
		String edition = (String) meta.get("value");

		// 暂定三种版本，限制级别：Free < Standard < Ultimate @todo 待功能清单梳理清晰，版本功能矩阵明确后，在确定各版本的模块屏蔽清单，配合license注解实现根据license动态管理产品功能显隐
		if (edition.equalsIgnoreCase(IssueVersionEnum.Standard.getValue())) {
			return true; // 标准版标注的模块，旗舰版可用，免费版也可用，都可用
		} else if (edition.equalsIgnoreCase(IssueVersionEnum.Ultimate.getValue())) {
			// 读取通过license动态配置的环境变量：edition，如果是旗舰版注解的模块，则对标准版license屏蔽，但对免费版开放
			String licEdition = context.getEnvironment().getProperty(IssueConstants.Key_PROPERTY_WLDOS_EDITION);
			return edition.equalsIgnoreCase(licEdition) || IssueVersionEnum.Free.getValue().equalsIgnoreCase(licEdition);
		}
		return true;
	}
}
