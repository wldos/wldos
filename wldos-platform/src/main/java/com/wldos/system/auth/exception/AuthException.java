/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 *Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see https://www.wldos.com/
 *
 */

package com.wldos.system.auth.exception;

/**
 * 认证异常。
 *
 * @author 树悉猿
 * @date 2021/7/15
 * @since 1.0
 */
public class AuthException extends RuntimeException {
	private static final int DEFAULT_STATUS = 200;
	private final int status;

	public AuthException(String message) {
		super(message);
		this.status = DEFAULT_STATUS;
	}

	public AuthException(String message, int status) {
		super(message);
		this.status = status;
	}

	public AuthException(String message, Throwable cause) {
		super(message, cause);
		this.status = DEFAULT_STATUS;
	}

	public AuthException(Throwable cause) {
		super(cause);
		this.status = DEFAULT_STATUS;
	}

	public AuthException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.status = DEFAULT_STATUS;
	}

	public int getStatus() {
		return status;
	}
}
