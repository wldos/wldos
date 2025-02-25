/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.framework.support.cache;

/**
 * 缓存执行异常。
 *
 * @author 元悉宇宙
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
