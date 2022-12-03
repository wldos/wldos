/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.gateway;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 过滤器专用请求包装类。增加请求头添加能力。
 *
 * @author 树悉猿
 * @date 2021/9/2
 * @version 1.0
 */
public class FilterRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * Constructs a request object wrapping the given request.
	 *
	 * @param request The request to wrap
	 *
	 * @throws IllegalArgumentException
	 *             if the request is null
	 */
	public FilterRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	private final Map<String, String> headers = new HashMap<>();

	private String uriPrefix;

	/**
	 * 设置前缀后，继承的get方法自动从uri中去掉前缀，方便后端api的前缀设置，controller无需关心
	 *
	 * @param uriPrefix 路由uri前缀
	 */
	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

	/**
	 * 新增header
	 *
	 * @param name 名称
	 * @param value 值
	 */
	public void add(String name, String value) {
		this.headers.put(name, value);
	}

	@Override
	public String getHeader(String name) {
		if (this.headers.containsKey(name)) {
			return this.headers.get(name);
		}

		return super.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		List<String> names = Collections.list(super.getHeaderNames());
		names.addAll(this.headers.keySet());
		return Collections.enumeration(names);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		List<String> values = Collections.list(super.getHeaders(name));
		if (this.headers.containsKey(name)) {
			values.add(this.headers.get(name));
		}
		return Collections.enumeration(values);
	}

	@Override
	public String getRequestURI() {
		String uri = super.getRequestURI();
		if (this.uriPrefix != null && uri.startsWith(this.uriPrefix))
			return uri.substring(this.uriPrefix.length());

		return super.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		StringBuffer url = super.getRequestURL();
		if (this.uriPrefix != null)
			return new StringBuffer(url.toString().replace(super.getRequestURI(), "")).append(this.getRequestURI());

		return super.getRequestURL();
	}
}
