/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.common.exception;

/**
 * 异常基类，继承此异常可以得到框架全局异常处理的支撑。
 *
 * @author 树悉猿
 * @date 2021/3/15
 * @since 1.0
 */
public class BaseException extends RuntimeException {
	private static final int DEFAULT_STATUS = 200;

	private final int status;

	public BaseException(String message) {
		super(message);
		this.status = DEFAULT_STATUS;
	}

	public BaseException(String message, int status) {
		super(message);
		this.status = status;
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
		this.status = DEFAULT_STATUS;
	}

	public BaseException(Throwable cause) {
		super(cause);
		this.status = DEFAULT_STATUS;
	}

	public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.status = DEFAULT_STATUS;
	}

	public int getStatus() {
		return status;
	}
}