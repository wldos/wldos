/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.base.controller.RepoController;
import com.wldos.cms.entity.KPosts;
import com.wldos.sys.base.enums.ContModelTypeEnum;
import com.wldos.cms.service.PostService;
import com.wldos.cms.vo.AuditPost;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.PageQuery;

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
	/**
	 * 内容管理列表，作品内的篇章，视为一个帖子
	 *
	 * @return 内容列表
	 */
	@RequestMapping("chapter")
	public PageableResult<AuditPost> adminPosts(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postType", ContModelTypeEnum.CHAPTER.toString());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyTenantFilter(pageQuery);

		return this.service.queryAdminPosts(this.getDomain(), pageQuery);
	}

	/**
	 * 作品管理列表，为什么不叫文集，因为还可以是视频、谱籍等其他体裁
	 *
	 * @return 作品列表
	 */
	@RequestMapping("book")
	public PageableResult<AuditPost> adminBooks(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("postType", ContModelTypeEnum.BOOK.toString());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyTenantFilter(pageQuery);

		return this.service.queryAdminPosts(this.getDomain(), pageQuery);
	}

	/**
	 * 内容发布
	 *
	 * @param post 待发布审核内容
	 * @return 是否
	 */
	@PostMapping("publish")
	public Boolean publishPost(@RequestBody AuditPost post) {
		this.service.publishPost(post);
		return Boolean.TRUE;
	}

	/**
	 * 内容下线
	 *
	 * @param post 待发布审核内容
	 * @return 是否
	 */
	@PostMapping("offline")
	public Boolean offlinePost(@RequestBody AuditPost post) {
		this.service.offlinePost(post);
		return Boolean.TRUE;
	}
}
