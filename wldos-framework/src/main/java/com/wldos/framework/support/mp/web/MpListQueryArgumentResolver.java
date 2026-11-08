/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.mp.web;

import io.github.wldos.platform.support.mp.query.PageParam;
import io.github.wldos.platform.support.mp.query.PagedQuery;
import io.github.wldos.platform.support.mp.query.WldosListQuery;
import io.github.wldos.platform.support.mp.query.WldosListRequest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * 第三方 MP 列表 Query Bean 入参解析（{@link PageParam}、{@link PagedQuery} 等）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
public class MpListQueryArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(@NonNull MethodParameter parameter) {
		Class<?> type = parameter.getParameterType();
		if (WldosListRequest.class.isAssignableFrom(type)) {
			return true;
		}
		if (PagedQuery.class.isAssignableFrom(type)) {
			return true;
		}
		if (PageParam.class.isAssignableFrom(type)) {
			return true;
		}
		return parameter.hasParameterAnnotation(WldosListQuery.class);
	}

	@Override
	public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
			@NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
		HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
		Map<String, Object> flatParams = extractFlatParams(request);
		Class<?> paramType = parameter.getParameterType();

		if (PagedQuery.class.isAssignableFrom(paramType)) {
			return bindPagedQuery(parameter, flatParams);
		}
		Object instance = BeanUtils.instantiateClass(paramType);
		bindFlatParams(instance, request);
		return instance;
	}

	private static Map<String, Object> extractFlatParams(HttpServletRequest request) {
		Map<String, Object> params = new HashMap<>();
		if (request == null || request.getParameterMap() == null) {
			return params;
		}
		request.getParameterMap().forEach((key, values) -> {
			if (values != null && values.length > 0) {
				params.put(key, values.length == 1 ? values[0] : java.util.Arrays.asList(values));
			}
		});
		return params;
	}

	private static void bindFlatParams(Object target, HttpServletRequest request) throws Exception {
		if (request == null) {
			return;
		}
		MutablePropertyValues pvs = new ServletRequestParameterPropertyValues(request);
		org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(target);
		binder.setAutoGrowNestedPaths(true);
		binder.bind(pvs);
	}

	@SuppressWarnings("unchecked")
	private static Object bindPagedQuery(MethodParameter parameter, Map<String, Object> flatParams) throws Exception {
		Class<?> rawClass = parameter.getParameterType();
		PagedQuery<Object> query = (PagedQuery<Object>) BeanUtils.instantiateClass(rawClass);
		query.setCurrent(resolvePageNum(flatParams));
		query.setPageSize(parseInt(flatParams.get("pageSize"), 10));
		applySorterFilterFromMap(query, flatParams);

		Class<?> paramsClass = resolvePagedQueryParamsClass(parameter);
		if (paramsClass != null && paramsClass != Object.class) {
			Object paramsBean = BeanUtils.instantiateClass(paramsClass);
			MutablePropertyValues pvs = new MutablePropertyValues();
			for (Map.Entry<String, Object> entry : flatParams.entrySet()) {
				if (isReservedListKey(entry.getKey())) {
					continue;
				}
				pvs.add(entry.getKey(), entry.getValue());
			}
			org.springframework.validation.DataBinder binder = new org.springframework.validation.DataBinder(paramsBean);
			binder.bind(pvs);
			query.setParams(paramsBean);
		}
		return query;
	}

	private static Class<?> resolvePagedQueryParamsClass(MethodParameter parameter) {
		Type generic = parameter.getGenericParameterType();
		if (generic instanceof ParameterizedType) {
			Type arg = ((ParameterizedType) generic).getActualTypeArguments()[0];
			if (arg instanceof Class) {
				return (Class<?>) arg;
			}
		}
		return null;
	}

	private static void applySorterFilterFromMap(PageParam query, Map<String, Object> flatParams) {
		Object sorter = flatParams.get("sorter");
		if (sorter != null) {
			query.setSorterFromJson(sorter);
		}
		Object filter = flatParams.get("filter");
		if (filter != null) {
			query.setFilterFromJson(filter);
		}
	}

	private static boolean isReservedListKey(String key) {
		return "current".equals(key) || "pageNum".equals(key) || "pageNo".equals(key) || "pageSize".equals(key)
				|| "sorter".equals(key) || "filter".equals(key);
	}

	private static int resolvePageNum(Map<String, Object> flatParams) {
		int current = parseInt(flatParams.get("current"), 0);
		if (current > 0) {
			return current;
		}
		int pageNum = parseInt(flatParams.get("pageNum"), 0);
		if (pageNum > 0) {
			return pageNum;
		}
		return parseInt(flatParams.get("pageNo"), 1);
	}

	private static int parseInt(Object value, int defaultValue) {
		if (value == null) {
			return defaultValue;
		}
		int parsed = Integer.parseInt(value.toString());
		return parsed == 0 ? defaultValue : parsed;
	}
}
