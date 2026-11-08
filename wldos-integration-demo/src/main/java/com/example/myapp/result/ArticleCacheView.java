/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.result;

import com.example.myapp.entity.Article;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 文章读取结果，标明数据来自 Redis 还是数据库。
 */
@Data
@ApiModel("文章缓存读取")
public class ArticleCacheView {

	@ApiModelProperty("文章")
	private Article article;

	@ApiModelProperty("redis=命中共享缓存，db=读自数据库")
	private String source;
}
