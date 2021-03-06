/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.support.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分页场景下的原子计数操作。
 *
 * @author 树悉猿
 * @date 2021/6/22
 * @version 1.0
 */
public final class AtomicUtil {

	private AtomicUtil() {
		throw new IllegalStateException("Utility class");
	}

	/**
	 * 分页场景下的原子计数操作，保证每次计数都从正确的记录数开始
	 *
	 * @param currentPage
	 * @param pageSize
	 * @return
	 */
	public static AtomicInteger count(int currentPage, int pageSize) {

		int num = (currentPage - 1) * pageSize;

		return new AtomicInteger(num);
	}

	/**
	 * 分页场景下的原子计数操作，保证每次计数都从正确的记录数开始
	 *
	 * @param pageQuery
	 * @return
	 */
	public static AtomicInteger count(PageQuery pageQuery) {
		int currentPage = pageQuery.getCurrent();
		int pageSize = pageQuery.getPageSize();

		int num = (currentPage - 1) * pageSize;

		return new AtomicInteger(num);
	}
}
