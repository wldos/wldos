/*
 * Copyright (c) 2020 - 2024 wldos.com. All rights reserved.
 * Licensed under the Apache License Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or http://www.wldos.com or 306991142@qq.com
 *
 */

package com.wldos.support.cache;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * 定制化缓存,定义一个线程安全的缓存，专门缓存验证码等运行时交互数据。
 *
 * @author 元悉宇宙
 * @date 2021/5/7
 * @version 1.0
 */
@Slf4j
@Component
public class ConMapCache implements ICache {
	private final ConcurrentHashMap<String, SoftReference<Object>> cache = new ConcurrentHashMap<>();

	private final DelayQueue<CacheObject> delayQueue = new DelayQueue<>();

	public ConMapCache() {
		Thread cleanerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					CacheObject delayedCacheObject = delayQueue.take();
					cache.remove(delayedCacheObject.getKey(), delayedCacheObject.getReference());
				}
				catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		cleanerThread.setDaemon(true);
		cleanerThread.start();
	}

	@Override
	public void set(String key, Object value, long sourceDuration, TimeUnit sourceUnit) {
		try {
			if (key == null) {
				log.warn("缓存写成功，key=null value={}", value);
				return;
			}
			if (value == null) {
				cache.remove(key);
				log.warn("缓存写成功，key={} value=null", key);
				return;
			}

			long multiple;

			if (sourceUnit == TimeUnit.MINUTES) {
				multiple = 1000L * 60L;
			}
			else if (sourceUnit == TimeUnit.MILLISECONDS) {
				multiple = 1L;
			}
			else if (sourceUnit == TimeUnit.HOURS) {
				multiple = 1000L * 60L * 60L;
			}
			else {
				throw new CacheException("暂时不支持的时间单位，请使用分钟、小时或者毫秒");
			}

			// 主动删掉历史缓存，防止高并发场景 或 高时延缓存 导致的内存泄露
			if (this.get(key) != null)
				this.remove(key);

			long expireTime = System.currentTimeMillis() + sourceDuration * multiple;
			SoftReference<Object> reference = new SoftReference<>(value);
			cache.put(key, reference);
			delayQueue.put(new CacheObject(key, reference, expireTime));
			if (log.isDebugEnabled())
				log.debug("缓存写成功，key={} value={}", key, value);
		}
		catch (Exception e) {
			log.error("缓存写异常，key={}", key, e);
		}
	}

	@Override
	public void remove(String key) {
		try {
			cache.remove(key);
			// 主动删掉历史缓存，防止高并发场景 或 高时延缓存 导致的内存泄露
			delayQueue.removeIf((o) -> key.equals(o.getKey()));
			log.debug("缓存删成功，key={}", key);
		}
		catch (Exception e) {
			log.error("缓存删异常，key={} {}", key, e);
		}
	}

	@Override
	public void removeByPrefix(String keyPrefix) {
		try {
			List<String> keys = cache.keySet().parallelStream().filter(key -> key.startsWith(keyPrefix)).collect(Collectors.toList());
			keys.parallelStream().forEach(key -> {
				cache.remove(key);
				log.info("缓存删成功，key={}", key);
			});
		}
		catch (Exception e) {
			log.error("缓存删异常，keyPrefix={} {}", keyPrefix, e);
		}
	}

	@Override
	public Object get(String key) {
		try {
			Object value = Optional.ofNullable(cache.get(key)).map(SoftReference::get).orElse(null);
			log.debug("缓存读成功，key={} value={}", key, value);
			return value;
		}
		catch (Exception e) {
			log.error("缓存读异常，key={} {}", key, e);
		}
		return null;
	}

	@Override
	public List<Object> getByPrefix(String keyPrefix) {
		return cache.entrySet().stream().filter(entry -> entry.getKey().startsWith(keyPrefix)).collect(Collectors.toList());
	}

	@Override
	public void clear() {
		cache.clear();
		log.info("缓存清空成功，the cache is clear.");
	}

	@Override
	public long size() {
		return cache.size();
	}
}