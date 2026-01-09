/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.exception;

/**
 * 异常基类，继承此异常可以得到框架全局异常处理的支撑。
 *
 * @author 元悉宇宙
 * @date 2021/3/15
 * @since 1.0
 */
public class BaseException extends RuntimeException {
	private static final int DEFAULT_CODE = 200;

	/**
	 * 业务状态码：200=成功，非200=失败（如401=未授权，403=禁止访问，500=服务器错误）
	 * 注意：这是业务状态码，不是HTTP状态码。HTTP状态码始终为200。
	 */
	private final int code;

	public BaseException(String message) {
		super(message);
		this.code = DEFAULT_CODE;
	}

	public BaseException(String message, int code) {
		super(message);
		this.code = code;
	}

	public BaseException(String message, Throwable cause) {
		super(message, cause);
		this.code = DEFAULT_CODE;
	}

	public BaseException(Throwable cause) {
		super(cause);
		this.code = DEFAULT_CODE;
	}

	public BaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.code = DEFAULT_CODE;
	}

	/**
	 * 获取业务状态码
	 * @return 业务状态码
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @deprecated 使用 {@link #getCode()} 代替。此方法保留仅为向后兼容。
	 */
	@Deprecated
	public int getStatus() {
		return code;
	}
}