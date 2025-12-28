/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.service.PubService;
import com.wldos.cms.vo.AuditPub;
import com.wldos.common.Constants;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.platform.core.enums.PubTypeEnum;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 内容管理controller。
 *
 * @author 元悉宇宙
 * @date 2021/6/12
 * @version 1.0
 */
@Api(tags = "内容管理")
@RequestMapping("/admin/cms/pub")
@RestController
public class PubController extends EntityController<PubService, KPubs> {

	/**
	 * 作品元素列表，作品内的章节、剧集或附件等
	 *
	 * @return 内容列表
	 */
	@ApiOperation(value = "作品元素列表", notes = "查询作品内的章节、剧集或附件等")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubType", value = "发布类型", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubStatus", value = "发布状态", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("chapter")
	public PageableResult<AuditPub> adminPosts(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushFilter("pubType", PubTypeEnum.listSubType());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyTenantFilter(pageQuery);

		return this.service.queryAdminPubs(this.getDomainId(), pageQuery);
	}

	/**
	 * 作品管理列表
	 *
	 * @return 作品列表
	 */
	@ApiOperation(value = "作品管理列表", notes = "查询作品列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubType", value = "发布类型", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubStatus", value = "发布状态", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("book")
	public PageableResult<AuditPub> adminBooks(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("parentId", Constants.TOP_PUB_ID);
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyTenantFilter(pageQuery);

		return this.service.queryAdminPubs(this.getDomainId(), pageQuery);
	}

	/**
	 * 信息管理列表
	 *
	 * @return 信息列表
	 */
	@ApiOperation(value = "信息管理列表", notes = "查询信息列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubStatus", value = "发布状态", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("info")
	public PageableResult<AuditPub> adminInfo(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubType", PubTypeEnum.INFO.getName());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyTenantFilter(pageQuery);

		return this.service.queryAdminPubs(this.getDomainId(), pageQuery);
	}

	/**
	 * 内容发布
	 *
	 * @param pub 待发布审核内容
	 * @return 是否
	 */
	@ApiOperation(value = "内容发布", notes = "发布审核通过的内容")
	@PostMapping("publish")
	public Boolean publishPost(@ApiParam(value = "待发布审核内容", required = true) @Valid @RequestBody AuditPub pub) {
		this.service.publishPub(pub.getId(), pub.getPubType());
		return Boolean.TRUE;
	}

	/**
	 * 内容下线
	 *
	 * @param pub 待发布审核内容
	 * @return 是否
	 */
	@ApiOperation(value = "内容下线", notes = "将已发布的内容下线")
	@PostMapping("offline")
	public Boolean offlinePost(@ApiParam(value = "待下线内容", required = true) @Valid @RequestBody AuditPub pub) {
		this.service.offlinePub(pub);
		return Boolean.TRUE;
	}
}
