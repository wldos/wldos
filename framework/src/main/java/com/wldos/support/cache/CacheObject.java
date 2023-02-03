/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.support.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * 支持过期时间的复合缓存对象。
 *
 * @author 树悉猿
 * @date 2021/5/7
 * @version 1.0
 */
public class CacheObject implements Delayed {
	private final String key;

	private final SoftReference<Object> reference;

	private final long expireTime;

	/**
	 * @param key 缓存key
	 * @param reference 缓存实体引用
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