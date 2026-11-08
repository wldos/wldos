/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.service;

import com.example.myapp.entity.Article;
import com.example.myapp.result.ArticleCacheView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.wldos.common.exception.BaseException;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 业务应用自己用 Spring Boot 的 {@link StringRedisTemplate} 实现的文章共享缓存与阅读数分布式锁。
 * 连接配置在 {@code spring.data.redis.*}，键使用应用自己的前缀。
 */
@Service
public class ArticleCacheService {

	private static final String CACHE_KEY_PREFIX = "demo:article:";

	private static final String LOCK_KEY_PREFIX = "demo:lock:article:view:";

	private static final long CACHE_MINUTES = 10L;

	private static final long LOCK_SECONDS = 5L;

	private static final DefaultRedisScript<Long> UNLOCK = new DefaultRedisScript<Long>();

	static {
		UNLOCK.setResultType(Long.class);
		UNLOCK.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
	}

	@Resource
	private ArticleService articleService;

	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Resource
	private ObjectMapper objectMapper;

	public ArticleCacheView read(Long id) {
		String key = CACHE_KEY_PREFIX + id;
		ArticleCacheView view = new ArticleCacheView();
		String json = stringRedisTemplate.opsForValue().get(key);
		if (json != null) {
			view.setArticle(parse(json));
			view.setSource("redis");
			return view;
		}
		Article article = articleService.getById(id);
		view.setArticle(article);
		view.setSource("db");
		if (article != null) {
			stringRedisTemplate.opsForValue().set(key, toJson(article), CACHE_MINUTES, TimeUnit.MINUTES);
		}
		return view;
	}

	public ArticleCacheView recordView(Long id) {
		String lockKey = LOCK_KEY_PREFIX + id;
		String token = UUID.randomUUID().toString();
		Boolean locked = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, token, LOCK_SECONDS, TimeUnit.SECONDS);
		if (!Boolean.TRUE.equals(locked)) {
			throw new BaseException("文章正在更新，请稍后重试");
		}
		try {
			ArticleCacheView view = new ArticleCacheView();
			view.setArticle(articleService.incrementViews(id));
			view.setSource("db");
			stringRedisTemplate.delete(CACHE_KEY_PREFIX + id);
			return view;
		}
		finally {
			stringRedisTemplate.execute(UNLOCK, Collections.singletonList(lockKey), token);
		}
	}

	private Article parse(String json) {
		try {
			return objectMapper.readValue(json, Article.class);
		}
		catch (IOException ex) {
			throw new BaseException("文章缓存内容无法解析", ex);
		}
	}

	private String toJson(Article article) {
		try {
			return objectMapper.writeValueAsString(article);
		}
		catch (JsonProcessingException ex) {
			throw new BaseException("文章无法序列化为缓存内容", ex);
		}
	}
}
