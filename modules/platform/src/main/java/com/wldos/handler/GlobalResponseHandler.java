/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.handler;

import javax.servlet.http.HttpServletRequest;

import com.wldos.common.res.Result;
import com.wldos.common.res.ResultJson;
import com.wldos.support.auth.JWTTool;
import com.wldos.support.auth.vo.Token;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * api响应处理。
 *
 * @author 树悉猿
 * @date 2021/9/16
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice("com.wldos")
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {
	private final JWTTool jwtTool;

	private final ResultJson resJson;

	public GlobalResponseHandler(JWTTool jwtTool, ResultJson resJson) {
		this.jwtTool = jwtTool;
		this.resJson = resJson;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true; // 保证每一调都能命中token续签处理
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		Token token = this.jwtTool.refreshToken(req);
		if (token != null) {
			this.jwtTool.setTokenHeader(response, token);
		}

		if (body instanceof Result) {
			return body;
		}

		if (returnType.getGenericParameterType().equals(String.class)) { // 返回纯字符串必须是json串，普通字符串不允许
			return body;
		}

		return this.resJson.format(body);
	}
}