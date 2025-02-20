/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.common.utils;

import java.util.concurrent.atomic.AtomicInteger;

import com.wldos.common.res.PageQuery;

/**
 * 分页场景下的原子计数操作。
 *
 * @author 元悉宇宙
 * @date 2021/6/22
 * @version 1.0
 */
public final class AtomicUtils {

	private AtomicUtils() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 分页场景下的原子计数操作，保证每次计数都从正确记录数开始
	 */
	public static AtomicInteger count(int currentPage, int pageSize) {

		int num = (currentPage - 1) * pageSize;

		return new AtomicInteger(num);
	}

	/**
	 * 分页场景下的原子计数操作，保证每次计数都从正确的记录数开始
	 */
	public static AtomicInteger count(PageQuery pageQuery) {
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();

		int num = (currentPage - 1) * pageSize;

		return new AtomicInteger(num);
	}
}