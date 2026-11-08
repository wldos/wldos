/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.resolver;

import io.github.wldos.common.res.PageQuery;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link PageQuery} 方法参数解析器（现网 / JDBC / CO，行为不变）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class PageQueryMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(@NonNull MethodParameter parameter) {
		return PageQuery.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			@NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		if (request == null) {
			return new PageQuery(null);
		}
		Map<String, Object> params = new HashMap<>();
		Map<String, String[]> paramMap = request.getParameterMap();
		if (paramMap != null) {
			paramMap.forEach((key, values) -> {
				if (values != null && values.length > 0) {
					params.put(key, values.length == 1 ? values[0] : Arrays.asList(values));
				}
			});
		}
		return new PageQuery(params);
	}
}
