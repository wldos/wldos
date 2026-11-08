/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.myapp.entity.Article;
import com.example.myapp.param.ArticlePageParam;
import com.example.myapp.result.ArticlePageResult;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章 MyBatis-Plus Mapper（第三方标准 MP，不继承 BaseDao）。
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

	long countArticlePage(@Param("param") ArticlePageParam param);

	List<ArticlePageResult> selectArticlePage(@Param("param") ArticlePageParam param, @Param("offset") int offset,
			@Param("pageSize") int pageSize);
}
