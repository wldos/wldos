/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.controller;

import com.wldos.framework.controller.RepoController;
import com.wldos.cms.entity.KStars;
import com.wldos.cms.service.StarService;
import com.wldos.cms.vo.Pub;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞关注controller。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("cms")
@RestController
public class StarController extends RepoController<StarService, KStars> {

	@PostMapping("star")
	public int starObject(@RequestBody Pub pub) {
		return this.service.starObject(pub.getId(), this.getUserId());
	}

	@PostMapping("like")
	public int likeObject(@RequestBody Pub pub) {
		return this.service.likeObject(pub.getId(), this.getUserId());
	}
}
