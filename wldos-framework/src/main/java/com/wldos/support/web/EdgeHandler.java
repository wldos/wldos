/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.auth.vo.JWT;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

/**
 * 边缘通信处理。
 *
 * @author 元悉宇宙
 * @date 2023/1/29
 * @version 1.0
 */
public interface EdgeHandler {
	/**
	 * 响应客户端之前预处理body
	 *
	 * @param body 响应body
	 * @param returnType 返回类型
	 * @param selectedContentType 选择内容类型
	 * @param selectedConverterType 选择转换器类型
	 * @param request 请求
	 * @param response 响应
	 * @return 预处理body
	 */
	Object handleBody(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response);

	/**
	 * 对响应的处理
	 *
	 * @param response 响应
	 */
	void handleResponse(HttpServletResponse response);

	/**
	 * 预处理请求
	 *
	 * @param request 请求
	 * @param jwt java web token对象
	 * @param domainId 请求的域id
	 * @return 包装过的请求对象
	 */
	FilterRequestWrapper handleRequest(HttpServletRequest request, JWT jwt, Long domainId, String proxyPrefix);

	/**
	 * 获取用户当前访问的顶级域名
	 *
	 * @param request 请求
	 * @param domainHeader 域参数在request中保存的header
	 * @return 所用域名的顶级域名，类似： xxx.com...
	 */
	String getDomain(HttpServletRequest request, String domainHeader);
}
