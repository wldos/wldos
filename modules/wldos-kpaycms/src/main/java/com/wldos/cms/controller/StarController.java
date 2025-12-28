/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.cms.entity.KStars;
import com.wldos.cms.service.StarService;
import com.wldos.cms.vo.Pub;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 点赞关注controller。
 *
 * @author 元悉宇宙
 * @date 2021/6/12
 * @version 1.0
 */
@Api(tags = "点赞关注管理")
@RequestMapping("cms")
@RestController
public class StarController extends EntityController<StarService, KStars> {

	@ApiOperation(value = "关注对象", notes = "关注指定的内容对象")
	@PostMapping("star")
	public int starObject(@ApiParam(value = "内容对象", required = true) @Valid @RequestBody Pub pub) {
		return this.service.starObject(pub.getId(), this.getUserId());
	}

	@ApiOperation(value = "点赞对象", notes = "点赞指定的内容对象")
	@PostMapping("like")
	public int likeObject(@ApiParam(value = "内容对象", required = true) @Valid @RequestBody Pub pub) {
		return this.service.likeObject(pub.getId(), this.getUserId());
	}
}
