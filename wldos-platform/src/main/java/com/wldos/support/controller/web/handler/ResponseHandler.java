/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.support.controller.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.wldos.support.controller.web.Result;
import com.wldos.support.controller.web.ResultJson;
import com.wldos.system.auth.vo.Token;
import com.wldos.system.core.service.UserService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
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
public class ResponseHandler implements ResponseBodyAdvice<Object> {
	private final UserService userService;
	private final ResultJson resJson;

	public ResponseHandler(UserService userService, ResultJson resJson) {
		this.userService = userService;
		this.resJson = resJson;
	}

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return true;
	}

	@Override
	public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
			Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse res = ((ServletServerHttpResponse) response).getServletResponse();
		Token token = this.userService.refreshToken(req);
		if (token != null) {
			res.setHeader("token", token.getAccessToken());
			res.setHeader("refresh", Integer.toString(token.getRefresh()));
		}

		if (body instanceof Result) {
			return body;
		} else if (returnType.getGenericParameterType().equals(String.class)) {
			return body;
		}
		return this.resJson.format(body);
	}
}
