/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.myapp.entity.Article;
import com.example.myapp.mapper.ArticleMapper;
import com.example.myapp.param.ArticlePageParam;
import com.example.myapp.result.ArticlePageResult;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.platform.support.mp.context.WldosScope;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ArticleService extends ServiceImpl<ArticleMapper, Article> {

	@Resource
	private WldosScope wldosScope;

	@Transactional(readOnly = true)
	public PageData<ArticlePageResult> pageList(ArticlePageParam param) {
		if (param == null) {
			param = new ArticlePageParam();
		}
		PageQuery pageQuery = param.toPageQuery();
		wldosScope.applyTenantFilter(pageQuery);

		long total = baseMapper.countArticlePage(param);
		int offset = (param.getCurrent() - 1) * param.getPageSize();
		List<ArticlePageResult> rows = baseMapper.selectArticlePage(param, offset, param.getPageSize());
		return new PageData<>(total, param.getCurrent(), param.getPageSize(), rows);
	}

	public Article incrementViews(Long id) {
		Article article = getById(id);
		if (article == null) {
			throw new BaseException("文章不存在");
		}
		int views = article.getViews() == null ? 0 : article.getViews().intValue();
		article.setViews(Integer.valueOf(views + 1));
		updateById(article);
		return article;
	}
}
