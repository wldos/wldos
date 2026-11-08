/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.example.myapp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.wldos.framework.mvc.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章实体；MyBatis-Plus 映射表 {@code demo_article}。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("demo_article")
public class Article extends BaseEntity {

	private String title;
	private String content;
	private String author;
	private Integer views;
	private Integer status;
}
