/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.controller.web.handler;

import javax.servlet.http.HttpServletResponse;

import com.wldos.support.controller.web.Result;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.util.exception.AuthException;
import com.wldos.support.util.exception.auth.TokenForbiddenException;
import com.wldos.support.util.exception.auth.UserInvalidAuthException;
import com.wldos.support.util.exception.auth.UserTokenAuthException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice("com.wldos")
public class ControllerExceptionHandler {

	@ExceptionHandler(UserTokenAuthException.class)
	public Result userTokenExceptionHandler(HttpServletResponse response, UserTokenAuthException ex) {
		response.setStatus(401);
		log.error(ex.getMessage());
		return new Result(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(TokenForbiddenException.class)
	public Result tokenForbiddenExceptionHandler(HttpServletResponse response, TokenForbiddenException ex) {
		response.setStatus(403);
		log.error(ex.getMessage());
		return new Result(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(UserInvalidAuthException.class)
	public Result userInvalidExceptionHandler(HttpServletResponse response, UserInvalidAuthException ex) {
		response.setStatus(200);
		log.error(ex.getMessage());
		return new Result(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(AuthException.class)
	public Result baseExceptionHandler(HttpServletResponse response, AuthException ex) {
		log.error(ex.getMessage());
		response.setStatus(200);
		return new Result(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public Result otherExceptionHandler(HttpServletResponse response, Exception ex) {
		response.setStatus(200);
		log.error(ex.getMessage(), ex);
		return new Result(PubConstants.EX_OTHER_CODE, ex.getMessage());
	}

}
