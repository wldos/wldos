/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.base.controller.RepoController;
import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostStatusEnum;
import com.wldos.cms.service.PostService;
import com.wldos.cms.vo.SPost;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.res.PageQuery;

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
	/**
	 * 全文检索
	 *
	 * @return 内容列表
	 */
	@RequestMapping("search")
	public PageableResult<SPost> searchPosts(@RequestParam Map<String, Object> params) {
		// 检索关键字
		String wd = ObjectUtils.string(params.get("wd"));
		params.remove("wd");
		if (wd.equals(""))
			return new PageableResult<>();
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postTitle", wd); // 暂时以标题为查询标的，后期作全文检索实现
		pageQuery.pushParam("postStatus", PostStatusEnum.PUBLISH.toString());

		return this.service.searchPosts(this.getDomain(), pageQuery);
	}
}
