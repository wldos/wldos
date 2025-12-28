/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.web.handler;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import com.wldos.common.exception.BaseException;
import com.wldos.common.res.Result;
import com.wldos.common.res.ResultCode;
import com.wldos.framework.autoconfigure.WldosFrameworkProperties;
import com.wldos.framework.support.auth.TokenForbiddenException;
import com.wldos.framework.support.auth.TokenInvalidException;
import com.wldos.framework.support.auth.UserInvalidException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器（支持可配置的包路径）
 * 
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
@RefreshScope
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@Autowired(required = false)
	private WldosFrameworkProperties properties;
	
	@Value("${wldos_platform_adminEmail:306991142#qq.com}")
	private String adminEmail;

	@ExceptionHandler(TokenInvalidException.class)
	public Result userTokenExceptionHandler(HttpServletResponse response, TokenInvalidException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(TokenForbiddenException.class)
	public Result tokenForbiddenExceptionHandler(HttpServletResponse response, TokenForbiddenException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(UserInvalidException.class)
	public Result userInvalidExceptionHandler(HttpServletResponse response, UserInvalidException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getStatus(), ex.getMessage());
	}

	@ExceptionHandler(BaseException.class)
	public Result baseExceptionHandler(HttpServletResponse response, BaseException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getStatus(), ex.getMessage());
	}

	/**
	 * 处理参数校验异常（@RequestBody参数校验）
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Result handleValidationException(HttpServletResponse response, MethodArgumentNotValidException ex) {
		response.setStatus(200);
		StringBuilder errors = new StringBuilder();
		ex.getBindingResult().getFieldErrors().forEach(error -> {
			if (errors.length() > 0) {
				errors.append("; ");
			}
			errors.append(error.getField()).append(": ").append(error.getDefaultMessage());
		});
		
		log.warn("参数校验失败: {}", errors.toString());
		return Result.error(ResultCode.VALIDATION_ERROR, errors.toString());
	}

	/**
	 * 处理参数校验异常（@RequestParam、@PathVariable参数校验）
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	public Result handleConstraintViolationException(HttpServletResponse response, ConstraintViolationException ex) {
		response.setStatus(200);
		StringBuilder errors = new StringBuilder();
		for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
			if (errors.length() > 0) {
				errors.append("; ");
			}
			errors.append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
		}
		
		log.warn("参数校验失败: {}", errors.toString());
		return Result.error(ResultCode.VALIDATION_ERROR, errors.toString());
	}

	@ExceptionHandler(Exception.class)
	public Result otherExceptionHandler(HttpServletResponse response, Exception ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error("系统异常", ex);
		return Result.error(ResultCode.INTERNAL_ERROR);
	}
}