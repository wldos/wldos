/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.cache;

/**
 * 缓存执行异常。
 *
 * @author 树悉猿
 * @date 2021/7/15
 * @since 1.0
 */
public class CacheException extends RuntimeException {
	@SuppressWarnings("unused")
	public CacheException(String s, Exception e) {
		super(s, e);
	}

	public CacheException(String s) {
		super(s);
	}
}
