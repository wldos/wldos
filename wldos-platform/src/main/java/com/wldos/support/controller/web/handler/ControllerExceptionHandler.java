/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.controller.web.handler;

import javax.servlet.http.HttpServletResponse;

import com.wldos.support.controller.web.Result;
import com.wldos.support.Constants;
import com.wldos.system.auth.exception.AuthException;
import com.wldos.system.auth.exception.TokenForbiddenException;
import com.wldos.system.auth.exception.UserInvalidAuthException;
import com.wldos.system.auth.exception.UserTokenAuthException;
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
		return new Result(Constants.EX_OTHER_CODE, "Sorry, the server is abnormal, please try again, or contact support: support#zhiletu.com");
	}

}
