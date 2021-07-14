/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.support.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分页场景下的原子计数操作。
 *
 * @Title AtomicUtil
 * @Package com.wldos.support.util
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/6/22
 * @Version 1.0
 */
public final class AtomicUtil {

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
