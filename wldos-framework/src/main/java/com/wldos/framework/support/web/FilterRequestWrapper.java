/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * @author 元悉宇宙
 * @date 2021/9/2
 * @version 1.0
 */
public class FilterRequestWrapper extends HttpServletRequestWrapper {

	public FilterRequestWrapper(HttpServletRequest request) {
		super(request);
	}

	private final Map<String, String> headers = new HashMap<>();

	private String uriPrefix;

	public void setUriPrefix(String uriPrefix) {
		this.uriPrefix = uriPrefix;
	}

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