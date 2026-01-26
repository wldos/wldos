/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.impl;

import com.wldos.common.Constants;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.framework.support.auth.JWTTool;
import com.wldos.framework.support.auth.vo.JWT;
import com.wldos.framework.support.auth.vo.Token;
import com.wldos.framework.support.web.EdgeHandler;
import com.wldos.framework.support.web.FilterRequestWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * 默认 EdgeHandler 实现类（开源版本）
 * 当 Agent 模块中的 EdgeHandlerImpl 不存在时使用此实现
 * 使用降级处理：简化实现，提供基础的边缘处理功能
 *
 * @author 元悉宇宙
 * @version 1.0
 * @date 2026/01/10
 */
@ConditionalOnMissingClass("com.wldos.support.web.impl.EdgeHandlerImpl")
@Component
public class DefaultEdgeHandlerImpl implements EdgeHandler {

	@Autowired(required = false)
	private JWTTool jwtTool;

	// 类名常量缓存，避免每次调用 Class.getName() 的性能开销
	private static final String RESULT_CLASS_NAME = "com.wldos.common.res.Result";
	private static final String RESPONSE_ENTITY_CLASS_NAME = ResponseEntity.class.getName();

	@Override
	public Object handleBody(Object body, MethodParameter returnType, MediaType selectedContentType,
	                         Class<? extends HttpMessageConverter<?>> selectedConverterType,
	                         ServerHttpRequest request, ServerHttpResponse response) {
		// 降级处理：刷新 token（如果支持）
		if (jwtTool != null) {
			HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
			Token token = this.jwtTool.refreshToken(req);
			if (token != null) {
				jwtTool.setTokenHeader(response, token);
			}
		}

		// 降级处理：简化类型检查
		if (body == null) {
			return null;
		}

		Class<?> bodyClass = body.getClass();
		String bodyClassName = bodyClass.getName();

		// 快速路径：直接类型匹配
		if (bodyClassName.equals(RESULT_CLASS_NAME) ||
			bodyClassName.equals(RESPONSE_ENTITY_CLASS_NAME) ||
			bodyClass == String.class) {
			return body;
		}

		// 继承关系检查
		if (body instanceof Result ||
			body instanceof ResponseEntity ||
			body instanceof Resource) {
			return body;
		}

		// 类型元数据检查
		if (Resource.class.isAssignableFrom(returnType.getParameterType()) ||
			returnType.getGenericParameterType().equals(String.class)) {
			return body;
		}

		// 降级处理：其他类型直接返回，不进行格式化
		return body;
	}

	@Override
	public void handleResponse(HttpServletResponse response) {
		// 降级处理：设置服务器头
		response.setHeader(HttpHeaders.SERVER, "wldos");
	}

	@Override
	public FilterRequestWrapper handleRequest(HttpServletRequest request, JWT jwt, Long domainId, String proxyPrefix) {
		// 降级处理：创建请求包装器并设置上下文信息
		FilterRequestWrapper headers = new FilterRequestWrapper(request);
		headers.setUriPrefix(proxyPrefix);

		if (jwt != null) {
			headers.add(Constants.CONTEXT_KEY_USER_ID, ObjectUtils.string(jwt.getUserId()));
			headers.add(Constants.CONTEXT_KEY_USER_TENANT, ObjectUtils.string(jwt.getTenantId()));
			headers.add(Constants.CONTEXT_KEY_USER_DOMAIN, ObjectUtils.string(domainId));
			Date d = jwt.getExpireDate();
			headers.add(Constants.CONTEXT_KEY_TOKEN_EXPIRE_TIME, d == null ? "0" : String.valueOf(d.getTime()));
			headers.add(Constants.CONTEXT_KEY_TOKEN_REFRESH, String.valueOf(jwt.getRefresh()));
		}

		return headers;
	}

	@Override
	public String getDomain(HttpServletRequest request, String domainHeader) {
		// 降级处理：简化域名获取逻辑
		String domain = ObjectUtils.string(request.getHeader(domainHeader));
		if (ObjectUtils.isBlank(domain)) {
			// 未设置header, 取真实域名
			try {
				java.net.URL reqUrl = new java.net.URL(request.getRequestURL().toString());
				return reqUrl.getHost();
			} catch (java.net.MalformedURLException e) {
				// 降级处理：返回默认值
				return "localhost";
			}
		}
		return domain;
	}
}
