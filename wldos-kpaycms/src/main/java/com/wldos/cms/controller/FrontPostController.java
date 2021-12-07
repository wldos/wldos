/*
 * Copyright (c) 2020 - 2021. Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.service.PostService;
import com.wldos.cms.vo.SPost;
import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端内容controller。
 *
 * @author 树悉猿
 * @date 2021/12/03
 * @version 1.0
 */
@RequestMapping("/cms/post")
@RestController
public class FrontPostController extends RepoController<PostService, KPosts> {

	@RequestMapping("search")
	public PageableResult<SPost> searchPosts(@RequestParam Map<String, Object> params) {

		String wd = ObjectUtil.string(params.get("wd"));
		params.remove("wd");
		if (wd.equals(""))
			return new PageableResult<>();

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postTitle", wd);
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());

		return this.service.searchPosts(this.getDomain(), pageQuery);
	}
}
