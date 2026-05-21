/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.handler;

import com.wldos.framework.autoconfigure.WldosFrameworkProperties;
import com.wldos.framework.support.web.annotation.NotResponseBody;
import io.github.wldos.framework.support.web.EdgeHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * api响应处理（支持可配置的包路径）
 *
 * @author Yuanxi Universe
 * @date 2025-12-26
 * @version 2.0
 */
@RestControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {
	
	@Autowired
	private EdgeHandler edgeHandler;
	
	@Autowired
	private WldosFrameworkProperties properties;

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		if (returnType.hasMethodAnnotation(NotResponseBody.class)
				|| AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), NotResponseBody.class)) {
			return false;
		}
		// void：无返回值可包装，多用于自行写 response（如 downloadFile）；免逐接口 @NotResponseBody
		if (void.class == returnType.getParameterType()) {
			return false;
		}
		// 检查Controller是否在配置的包路径下
		String controllerPackage = returnType.getContainingClass().getPackage().getName();
		String basePackage = properties.getBasePackage();
		
		// 始终处理 com.wldos 与 io.github.wldos 包下的 Controller（框架、platform、sdk）
		if (controllerPackage.startsWith("com.wldos") || controllerPackage.startsWith("io.github.wldos")) {
			return true;
		}
		
		// 如果配置了basePackage，同时处理第三方应用的包路径
		if (basePackage != null && !basePackage.isEmpty() && !basePackage.equals("com.wldos")) {
			return controllerPackage.startsWith(basePackage);
		}
		
		// 默认只处理com.wldos包下的Controller
		return false;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		// 使用EdgeHandler处理响应（EdgeHandlerImpl已实现全部转换，包括自动包装为Result）
		return this.edgeHandler.handleBody(body, returnType, selectedContentType, selectedConverterType, request, response);
	}
}