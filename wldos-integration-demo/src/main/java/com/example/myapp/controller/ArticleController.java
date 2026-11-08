/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.controller;

import com.example.myapp.entity.Article;
import com.example.myapp.param.ArticlePageParam;
import com.example.myapp.result.ArticlePageResult;
import com.example.myapp.service.ArticleService;
import io.github.wldos.common.res.PageData;
import io.github.wldos.platform.support.mp.context.WldosContext;
import io.github.wldos.platform.support.mp.context.WldosWriteScope;
import io.github.wldos.platform.support.rbac.WriteScopeTarget;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文章模块（第三方 MyBatis-Plus 金标准，不继承 EntityController）。
 */
@Api(tags = "文章（MyBatis-Plus）")
@Slf4j
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

	@Resource
	private ArticleService articleService;

	@Resource
	private WldosContext wldosContext;

	@Resource
	private WldosWriteScope wldosWriteScope;

	@ApiOperation(value = "分页列表（GET）", notes = "ArticlePageParam；MpListQueryArgumentResolver 绑定扁平 query")
	@GetMapping("/mp-list")
	public PageData<ArticlePageResult> mpList(ArticlePageParam param) {
		log.debug("文章列表，当前用户 id={}", wldosContext.getUserId());
		return articleService.pageList(param);
	}

	@ApiOperation(value = "分页列表（POST JSON）", notes = "Body 可用 pageNum/pageSize 或 current/pageSize")
	@PostMapping("/mp-page")
	public PageData<ArticlePageResult> mpPage(@RequestBody ArticlePageParam param) {
		return articleService.pageList(param);
	}

	@ApiOperation(value = "新增", notes = "WldosWriteScope + MyMetaObjectHandler 审计填充")
	@PostMapping("/mp-add")
	public Article mpAdd(@RequestBody Article article) {
		if (article.getStatus() == null) {
			article.setStatus(0);
		}
		if (article.getViews() == null) {
			article.setViews(0);
		}
		wldosWriteScope.apply(article, WriteScopeTarget.SITE_AXIS);
		articleService.save(article);
		return article;
	}
}
