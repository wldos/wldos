/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
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
import com.wldos.framework.support.auth.TokenForbiddenException;
import com.wldos.framework.support.auth.TokenInvalidException;
import com.wldos.framework.support.auth.UserInvalidException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 全局异常处理器（支持可配置的包路径）
 * 
 * <p>支持第三方项目继承并覆盖异常处理逻辑：</p>
 * <pre>{@code
 * @RestControllerAdvice(basePackages = "com.example.myapp")
 * public class MyAppExceptionHandler extends GlobalExceptionHandler {
 *     
 *     // 覆盖父类的异常处理方法
 *     @Override
 *     @ExceptionHandler(BaseException.class)
 *     public Result baseExceptionHandler(HttpServletResponse response, BaseException ex) {
 *         // 自定义异常处理逻辑
 *         return Result.error(ex.getCode(), "自定义错误信息: " + ex.getMessage());
 *     }
 *     
 *     // 添加新的异常处理方法
 *     @ExceptionHandler(MyCustomException.class)
 *     public Result handleMyCustomException(HttpServletResponse response, MyCustomException ex) {
 *         return Result.error(500, ex.getMessage());
 *     }
 * }
 * }</pre>
 * 
 * <p>注意：</p>
 * <ul>
 *   <li>子类需要定义自己的 {@code @RestControllerAdvice} 注解，并指定 {@code basePackages}</li>
 *   <li>子类可以覆盖父类的任何 {@code protected} 方法</li>
 *   <li>如果子类和父类都处理同一个异常类型，Spring 会优先使用子类的处理器（更具体）</li>
 * </ul>
 * 
 * @author 元悉宇宙
 * @date 2025-12-26
 * @version 2.0
 */
@RefreshScope
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	/**
	 * 处理 Token 无效异常
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(TokenInvalidException.class)
	protected Result userTokenExceptionHandler(HttpServletResponse response, TokenInvalidException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getCode(), ex.getMessage());
	}

	/**
	 * 处理 Token 权限不足异常
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(TokenForbiddenException.class)
	protected Result tokenForbiddenExceptionHandler(HttpServletResponse response, TokenForbiddenException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getCode(), ex.getMessage());
	}

	/**
	 * 处理用户无效异常
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(UserInvalidException.class)
	protected Result userInvalidExceptionHandler(HttpServletResponse response, UserInvalidException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getCode(), ex.getMessage());
	}

	/**
	 * 处理基础业务异常
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(BaseException.class)
	protected Result baseExceptionHandler(HttpServletResponse response, BaseException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error(ex.getMessage());
		return Result.error(ex.getCode(), ex.getMessage());
	}

	/**
	 * 处理参数校验异常（@RequestBody参数校验）
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	protected Result handleValidationException(HttpServletResponse response, MethodArgumentNotValidException ex) {
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
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	protected Result handleConstraintViolationException(HttpServletResponse response, ConstraintViolationException ex) {
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

	/**
	 * 处理404错误（接口不存在）
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(NoHandlerFoundException.class)
	protected Result handleNoHandlerFoundException(HttpServletResponse response, NoHandlerFoundException ex) {
		response.setStatus(200); // HTTP状态码始终为200
		String message = "接口不存在: " + ex.getRequestURL();
		log.warn(message);
		return Result.error(ResultCode.NOT_FOUND, message);
	}

	/**
	 * 处理其他未捕获的异常
	 * 子类可以覆盖此方法来自定义处理逻辑
	 */
	@ExceptionHandler(Exception.class)
	protected Result otherExceptionHandler(HttpServletResponse response, Exception ex) {
		response.setStatus(200); // HTTP状态码始终为200
		log.error("系统异常", ex);
		return Result.error(ResultCode.INTERNAL_ERROR);
	}
}