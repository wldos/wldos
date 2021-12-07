/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.cms.controller;

import java.util.List;
import java.util.Map;

import com.wldos.cms.entity.KPosts;
import com.wldos.cms.enums.PostTypeEnum;
import com.wldos.cms.service.PostService;
import com.wldos.cms.vo.AuditPost;
import com.wldos.support.Constants;
import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容模型controller。
 *
 * @author 树悉猿
 * @date 2021/6/12
 * @version 1.0
 */
@RequestMapping("/admin/cms/post")
@RestController
public class PostController extends RepoController<PostService, KPosts> {

	@RequestMapping("chapter")
	public PageableResult<AuditPost> adminPosts(@RequestParam Map<String, Object> params) {


		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postType", PostTypeEnum.CHAPTER.toString());

		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
	}

		return this.service.queryAdminPosts(this.getDomain(), pageQuery);
	}

	@RequestMapping("book")
	public PageableResult<AuditPost> adminBooks(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postType", PostTypeEnum.BOOK.toString());

		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
	}

		return this.service.queryAdminPosts(this.getDomain(), pageQuery);
	}

	@PostMapping("publish")
	public Boolean publishPost(@RequestBody  AuditPost post) {
		this.service.publishPost(post);
		return Boolean.TRUE;
	}


	@PostMapping("offline")
	public Boolean offlinePost(@RequestBody  AuditPost post) {
		this.service.offlinePost(post);
		return Boolean.TRUE;
	}
}
