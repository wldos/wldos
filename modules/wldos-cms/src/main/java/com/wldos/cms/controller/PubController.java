/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.common.res.Result;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.search.CmsHybridSearchService;
import com.wldos.cms.service.PubService;
import com.wldos.cms.vo.AuditPub;
import com.wldos.cms.vo.SPub;
import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.utils.ObjectUtils;
import com.wldos.platform.core.enums.PubTypeEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
 * @author Yuanxi Universe
 * @date 2021/6/12
 * @version 1.0
 */
@Api(tags = "内容管理")
@RequestMapping("/admin/cms/pub")
@RestController
public class PubController extends EntityController<PubService, KPubs> {

	@Autowired
	private CmsHybridSearchService cmsHybridSearchService;

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
	public PageData<AuditPub> adminPosts(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushFilter("pubType", PubTypeEnum.listSubType());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KPubs.class);

		return this.service.queryAdminPubs(pageQuery);
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
		@ApiImplicitParam(name = "pubStatus", value = "发布状态", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "keyword", value = "标题或正文关键词（管理端列表 OR 匹配 pub_title / pub_content）", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("book")
	public PageData<AuditPub> adminBooks(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("parentId", Constants.TOP_PUB_ID);
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KPubs.class);

		return this.service.queryAdminPubs(pageQuery);
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
	public PageData<AuditPub> adminInfo(@RequestParam Map<String, Object> params) {

		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubType", PubTypeEnum.INFO.getName());
		// 业务对象需要作多租隔离处理：当前域-->所有分类 + 当前租户 => 当前管理员可管对象
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KPubs.class);

		return this.service.queryAdminPubs(pageQuery);
	}

	/**
	 * 业务模块关联 CMS 文档选择器（产品详情、协议等）；与内容管理列表隔离，仅已生效内容。
	 */
	@ApiOperation(value = "关联文档选择器列表", notes = "仅返回 C 端已生效的正式内容；默认 parentId=0 主作品，可传 pubType/keyword 等筛选")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "keyword", value = "标题或正文关键词", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "pubType", value = "发布类型（可选）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "parentId", value = "父作品 ID（默认 0 主类型）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("association-picker")
	public PageData<AuditPub> associationPicker(@RequestParam Map<String, Object> params) {
		PageQuery pageQuery = new PageQuery(params);
		Map<String, Object> condition = pageQuery.getCondition();
		if (condition == null || condition.get("parentId") == null) {
			pageQuery.pushParam("parentId", Constants.TOP_PUB_ID);
		}
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KPubs.class);
		return this.service.queryAssociationPickerPubs(pageQuery);
	}

	/**
	 * 内容发布
	 *
	 * @param pub 待发布审核内容
	 * @return 是否
	 */
	@ApiOperation(value = "内容发布", notes = "发布审核通过的内容")
	@PostMapping("publish")
	@OpLog(action = "内容发布", resourceType = "pub", resourceId = "#pub.id")
	public Boolean publishPost(@ApiParam(value = "待发布审核内容", required = true) @Valid @RequestBody AuditPub pub) {
		this.service.publishOrMergeDraft(pub.getId(), pub.getPubType(), this.getUserId(), this.getUserIp());
		return Boolean.TRUE;
	}

	@ApiOperation(value = "驳回草稿改稿", notes = "待审草稿 in_review 回到 draft")
	@PostMapping("reject-draft")
	@OpLog(action = "驳回草稿", resourceType = "pub", resourceId = "#pub.id")
	public Boolean rejectDraft(@ApiParam(value = "草稿行 ID", required = true) @Valid @RequestBody AuditPub pub) {
		this.service.rejectDraft(pub.getId());
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
	@OpLog(action = "内容下线", resourceType = "pub", resourceId = "#pub.id")
	public Boolean offlinePost(@ApiParam(value = "待下线内容", required = true) @Valid @RequestBody AuditPub pub) {
		this.service.offlinePub(pub);
		return Boolean.TRUE;
	}

	@ApiOperation(value = "更新内容可发现性", notes = "写入 k_pubs.visibility_scope（PUBLIC_LISTED / UNLISTED / INTERNAL_ONLY）")
	@PutMapping("{id}/visibility-scope")
	@OpLog(action = "更新可发现性", resourceType = "pub", resourceId = "#id")
	public Result<Void> updateVisibilityScope(
			@ApiParam(value = "内容ID", required = true) @PathVariable Long id,
			@RequestBody(required = false) Map<String, String> body) {
		String scope = body != null ? body.get("visibilityScope") : null;
		this.service.updateVisibilityScope(id, scope, this.getDomainId(), this.getUserId(), this.getUserIp());
		return Result.ok(null);
	}

	@ApiOperation(value = "混合全文检索", notes = "BM25+向量 RRF；scope=internal 含内部资料")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "wd", value = "搜索关键词", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "scope", value = "public 或 internal", dataTypeClass = String.class, paramType = "query", example = "public"),
		@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10")
	})
	@GetMapping("search-hybrid")
	public PageData<SPub> searchHybrid(@RequestParam Map<String, Object> params) {
		String wd = ObjectUtils.string(params.get("wd"));
		params.remove("wd");
		boolean internal = "internal".equalsIgnoreCase(ObjectUtils.string(params.get("scope")));
		params.remove("scope");
		if (wd.equals("")) {
			return new PageData<>();
		}
		PageQuery pageQuery = new PageQuery(params);
		return cmsHybridSearchService.searchHybrid(this.getDomainId(), pageQuery, wd, internal);
	}

	@ApiOperation(value = "内部资料全文检索", notes = "含 INTERNAL_ONLY；管理端域上下文")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "wd", value = "搜索关键词", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10")
	})
	@GetMapping("search-internal")
	public PageData<SPub> searchInternal(@RequestParam Map<String, Object> params) {
		String wd = ObjectUtils.string(params.get("wd"));
		params.remove("wd");
		if (wd.equals("")) {
			return new PageData<>();
		}
		PageQuery pageQuery = new PageQuery(params);
		return cmsHybridSearchService.searchHybrid(this.getDomainId(), pageQuery, wd, true);
	}
}
