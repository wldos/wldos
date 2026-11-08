/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.controller;

import com.example.myapp.result.ArticleCacheView;
import com.example.myapp.service.ArticleCacheService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文章共享缓存与分布式锁示例，使用 Spring Boot 的 Redis 能力，需要本机 Redis。
 */
@Api(tags = "文章（Redis 共享缓存与锁）")
@RestController
@RequestMapping("/api/articles")
public class ArticleCacheController {

	@Resource
	private ArticleCacheService articleCacheService;

	@ApiOperation(value = "按主键读取", notes = "先读 Redis（demo:article:{id}），未命中查库并回写")
	@GetMapping("/detail/{id}")
	public ArticleCacheView detail(@PathVariable("id") Long id) {
		return articleCacheService.read(id);
	}

	@ApiOperation(value = "阅读数 +1", notes = "先拿锁 demo:lock:article:view:{id}，更新后删除文章缓存")
	@PostMapping("/detail/{id}/view")
	public ArticleCacheView view(@PathVariable("id") Long id) {
		return articleCacheService.recordView(id);
	}
}
