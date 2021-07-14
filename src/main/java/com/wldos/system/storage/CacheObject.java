/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.storage;

import java.lang.ref.SoftReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 支持过期时间的复合缓存对象。
 *
 * @Title CacheObject
 * @Package com.wldos.system.storage
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/5/7
 * @Version 1.0
 */
public class CacheObject implements Delayed {
	private final String key;

	private final SoftReference<Object> reference;

	private final long expireTime;

	/**
	 * 缓存对象初始化
	 *
	 * @param key
	 * @param reference
	 * @param expireTime 超期时限
	 */
	public CacheObject(String key, SoftReference<Object> reference, long expireTime) {
		this.key = key;
		this.reference = reference;
		this.expireTime = expireTime;
	}

	public String getKey() {
		return this.key;
	}

	public SoftReference<Object> getReference() {
		return this.reference;
	}

	@Override
	public long getDelay(TimeUnit unit) {

		return unit.convert(this.expireTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed o) {
		return Long.compare(expireTime, ((CacheObject) o).expireTime);
	}
}