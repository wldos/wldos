/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.wldos.common.Constants;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.res.Result;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.support.audit.annotation.OpLog;
import io.github.wldos.common.vo.SelectOption;
import io.github.wldos.common.vo.TreeSelectOption;
import com.wldos.framework.mvc.controller.EntityController;
import io.github.wldos.platform.support.rbac.AdminScopeLevel;
import io.github.wldos.platform.support.rbac.AxisLayerGuard;
import io.github.wldos.platform.support.rbac.DataScopeProfile;
import io.github.wldos.platform.support.rbac.DataScopeQueryHelper;
import io.github.wldos.platform.support.rbac.DataScopeResolver;
import com.wldos.platform.core.entity.KTerms;
import com.wldos.platform.core.service.TermService;
import com.wldos.platform.core.vo.Category;
import com.wldos.platform.core.vo.TermTree;
import io.github.wldos.platform.support.term.dto.Term;
import io.github.wldos.platform.support.term.enums.TermTypeEnum;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.validation.Valid;

/**
 * 分类管理controller。
 * 租户/站点管理员可维护各自范围内的分类、标签（写入时按 {@link io.github.wldos.platform.support.rbac.AdminWriteScopeStamper}
 * 归属租户/站点；未显式指定站点时默认租户内全站共享）；平台共享（com_id=0）分类、标签对所有租户可见。
 *
 * @author Yuanxi Universe
 * @date 2021/5/2
 * @version 1.0
 */
@Api(tags = "分类管理")
@RestController
public class TermController extends EntityController<TermService, KTerms> {

	/**
	 * 分类列表，支持查询、排序的分页查询
	 *
	 * @param params 分页
	 * @return 分类项树形列表
	 */
	@ApiOperation(value = "分类列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式，如：{\"createTime\":\"desc\"}", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "classType", value = "分类类型，默认为category", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "name", value = "分类名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管跨租户查看）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（超管跨站点查看）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("/admin/cms/category")
	public PageData<TermTree> listCategory(
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId,
			@RequestParam Map<String, Object> params) {
		params.remove("comId");
		params.remove("domainId");
		// 如果参数中没有 classType，则默认为 category
		if (!params.containsKey("classType") || ObjectUtils.isBlank(params.get("classType"))) {
			params.put("classType", TermTypeEnum.CATEGORY.toString());
		}
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KTerms.class);
		this.applyAdminTermScope(pageQuery, comId, domainId);
		PageData<TermTree> page = this.service.queryTermForTree(new KTerms(), pageQuery, false);
		this.decorateWritable(page.getRows());
		return page;
	}

	/**
	 * 标签列表，支持查询、排序的分页查询
	 *
	 * @param params 请求参数
	 * @return 标签列表
	 */
	@ApiOperation(value = "标签列表", notes = "支持查询、排序的分页查询")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "name", value = "标签名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管跨租户查看）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（超管跨站点查看）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("/admin/cms/tag")
	public PageData<TermTree> listTag(
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId,
			@RequestParam Map<String, Object> params) {
		params.remove("comId");
		params.remove("domainId");
		params.put("classType", TermTypeEnum.TAG.toString());
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KTerms.class);
		this.applyAdminTermScope(pageQuery, comId, domainId);
		PageData<TermTree> page = this.service.queryTermForTree(new KTerms(), pageQuery, true);
		this.decorateWritable(page.getRows());
		return page;
	}

	/**
	 * 获取分类树，用于信息发布
	 * (大类、小类两级矮树, [{title: '', key: '', slug: '', conType: ''}])
	 *
	 * @return 两级分类树列表
	 */
	@ApiOperation(value = "分类扁平树", notes = "获取分类树，用于信息发布")
	@GetMapping("category/flatTree")
	public List<Category> categoryFlatTree() {
		return this.service.queryPortalFlatCategoryTree(this.getDomainId());
	}

	/**
	 * 信息发布获取分类树，用于方便权限控制，后期追加路由权限管理
	 * (大类、小类两级矮树, [{title: '', key: '', slug: '', conType: ''}])
	 *
	 * @return 两级分类树列表
	 */
	@ApiOperation(value = "信息分类扁平树", notes = "信息发布获取分类树")
	@GetMapping("info/flatTree")
	public List<Category> infoFlatTree() {
		return this.service.queryPortalInfoFlatCategoryTree(this.getDomainId());
	}

	/**
	 * 获取分类树状列表
	 * (常规分层, [{title: '', value: '', key: '', children: [{...}...]},])
	 *
	 * @return 分类树状列表
	 */
	@ApiOperation(value = "分类树状列表", notes = "获取分类树状列表")
	@GetMapping("category/treeSelect")
	public List<TreeSelectOption> categoryLayerTree(
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId) {
		if (this.useScopedTermTree(comId, domainId)) {
			return this.queryScopedTreeSelect(TermTypeEnum.CATEGORY.toString(), comId, domainId);
		}
		return this.service.queryPortalLayerCategoryTree(TermTypeEnum.CATEGORY.toString(), this.getDomainId());
	}

	/**
	 * 获取插件分类树状列表
	 * (常规分层, [{title: '', value: '', key: '', children: [{...}...]},])
	 *
	 * @return 插件分类树状列表
	 */
	@ApiOperation(value = "插件分类树状列表", notes = "获取插件分类树状列表")
	@GetMapping("plugin/category/treeSelect")
	public List<TreeSelectOption> pluginCategoryLayerTree() {
		return this.service.queryLayerCategoryTreeByType(TermTypeEnum.PLUGIN.toString());
	}

	/**
	 * 通用分类树查询接口，支持所有类型
	 * (常规分层, [{title: '', value: '', key: '', children: [{...}...]},])
	 *
	 * @param classType 分类类型（如：category, plugin, nav_menu 等）
	 * @return 分类树状列表
	 */
	@ApiOperation(value = "通用分类树", notes = "通用分类树查询接口，支持所有类型")
	@GetMapping("term/category/treeSelect")
	public List<TreeSelectOption> termCategoryLayerTree(
			@ApiParam(value = "分类类型", required = true) @RequestParam String classType,
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId) {
		if (this.useScopedTermTree(comId, domainId)) {
			return this.queryScopedTreeSelect(classType, comId, domainId);
		}
		return this.service.queryPortalLayerCategoryTree(classType, this.getDomainId());
	}

	/**
	 * 根据父分类id获取所有子分类（包含自身）
	 * (结构：[{title: '', key: '', slug: '', conType: ''}])
	 *
	 * @return 两级分类树列表
	 */
	@ApiOperation(value = "根据父ID获取分类", notes = "根据父分类id获取所有子分类（包含自身）")
	@GetMapping("category/fromPid/{parentId:\\d+}")
	public List<Category> categoryFromPid(@ApiParam(value = "父分类ID", required = true) @PathVariable Long parentId) {
		return this.service.queryCategoriesFromPid(parentId);
	}

	/**
	 * 根据父分类别名获取所有子分类（包含自身）
	 * (结构：[{title: '', key: '', slug: '', conType: ''}])
	 *
	 * @return 两级分类树列表
	 */
	@ApiOperation(value = "根据别名获取分类", notes = "根据父分类别名获取所有子分类（包含自身）")
	@GetMapping("category/fromPlug/{slugTerm}")
	public List<Category> categoryFromPlug(@ApiParam(value = "分类别名", required = true) @PathVariable String slugTerm) {
		return this.service.queryCategoriesFromPlug(slugTerm);
	}

	/**
	 * 获取平顶级分类下拉列表([{label: 'l', value: 'v'},])
	 *
	 * @return 顶级分类下拉列表
	 */
	@ApiOperation(value = "顶级分类下拉列表", notes = "获取平顶级分类下拉列表")
	@GetMapping("category/select")
	public List<SelectOption> queryTopCategories() {
		return this.service.queryTopCategories();
	}

	/**
	 * 获取平台标签下拉列表([{label: 'l', value: 'v'},])
	 *
	 * @return 标签下拉列表
	 */
	@ApiOperation(value = "标签下拉列表", notes = "获取平台标签下拉列表")
	@GetMapping("tag/select")
	public List<SelectOption> queryFlatTags() {
		return this.service.queryPortalFlatTags(this.getDomainId());
	}

	@ApiOperation(value = "添加分类", notes = "添加新的分类")
	@PostMapping("/admin/cms/category/add")
	@OpLog(action = "新增分类", resourceType = "category", resourceId = "#term.id")
	public Result addCategory(@ApiParam(value = "分类信息", required = true) @Valid @RequestBody Term term) {
		term.setClassType(TermTypeEnum.CATEGORY.toString());
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
		if (!"ok".equals(res)) {
			return Result.error(ResultCode.VALIDATION_ERROR.getCode(), res);
		}
		this.service.refreshTerm();
		return Result.ok(res);
	}

	@ApiOperation(value = "更新分类", notes = "更新分类信息")
	@PostMapping("/admin/cms/category/update")
	@OpLog(action = "更新分类", resourceType = "category", resourceId = "#term.id")
	public Result updateCategory(@ApiParam(value = "分类信息", required = true) @Valid @RequestBody Term term) {
		String res = this.service.updateTerm(term, this.getUserId(), this.getUserIp());
		if (!"ok".equals(res)) {
			return Result.error(ResultCode.VALIDATION_ERROR.getCode(), res);
		}
		this.service.refreshTerm();
		return Result.ok(res);
	}

	@ApiOperation(value = "删除分类", notes = "删除指定的分类")
	@DeleteMapping("/admin/cms/category/delete")
	@OpLog(action = "删除分类", resourceType = "category", resourceId = "#term.id", recordParams = false)
	public Result deleteCategory(@ApiParam(value = "分类信息", required = true) @Valid @RequestBody Term term) {
		this.service.deleteTerm(term, this.getUserId());
		this.service.refreshTerm();
		return Result.ok("ok");
	}

	@ApiOperation(value = "批量删除分类", notes = "批量删除分类")
	@SuppressWarnings("unchecked")
	@DeleteMapping("/admin/cms/category/deletes")
	@OpLog(action = "批量删除分类", resourceType = "category")
	public Boolean removeIds(@ApiParam(value = "分类列表", required = true) @RequestBody List<Term> terms) {
		if (!ObjectUtils.isBlank(terms)) {
			this.service.deleteTerms(terms, this.getUserId());

			this.service.refreshTerm();
		}

		return Boolean.TRUE;
	}

	@ApiOperation(value = "添加标签", notes = "添加新的标签")
	@PostMapping("/admin/cms/tag/add")
	@OpLog(action = "新增标签", resourceType = "tag", resourceId = "#term.id")
	public Result addTag(@ApiParam(value = "标签信息", required = true) @Valid @RequestBody Term term) {
		term.setClassType(TermTypeEnum.TAG.toString());
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
		if (!"ok".equals(res)) {
			return Result.error(ResultCode.VALIDATION_ERROR.getCode(), res);
		}
		this.service.appendCacheTag(term); // 新增标签追加缓存
		return Result.ok(res);
	}

	/**
	 * 通用分类项列表，支持所有类型的查询、排序的分页查询
	 * 通过 classType 参数指定类型（category, tag, plugin 等）
	 *
	 * @param params 分页参数，必须包含 classType
	 * @return 分类项树形列表
	 */
	@ApiOperation(value = "通用分类项列表", notes = "通用分类项列表，支持所有类型的查询、排序的分页查询，必须包含 classType 参数")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "classType", value = "分类类型（必填），如：category, tag, plugin, nav_menu 等", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "name", value = "分类项名称（模糊查询）", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "comId", value = "目标租户 ID（超管跨租户查看）", dataTypeClass = Long.class, paramType = "query"),
		@ApiImplicitParam(name = "domainId", value = "目标站点 ID（超管跨站点查看）", dataTypeClass = Long.class, paramType = "query")
	})
	@GetMapping("/admin/term-type/term")
	public PageData<TermTree> listTerm(
			@ApiParam(value = "目标租户 ID") @RequestParam(required = false) Long comId,
			@ApiParam(value = "目标站点 ID") @RequestParam(required = false) Long domainId,
			@RequestParam Map<String, Object> params) {
		params.remove("comId");
		params.remove("domainId");
		// classType 是必填参数
		if (!params.containsKey("classType") || ObjectUtils.isBlank(params.get("classType"))) {
			return new PageData<>(0L, 1, 15, new java.util.ArrayList<>());
		}
		// 判断是否为扁平结构（tag 类型）
		boolean isFlat = TermTypeEnum.TAG.toString().equals(params.get("classType"));
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KTerms.class);
		this.applyAdminTermScope(pageQuery, comId, domainId);
		PageData<TermTree> page = this.service.queryTermForTree(new KTerms(), pageQuery, isFlat);
		this.decorateWritable(page.getRows());
		return page;
	}

	/**
	 * 超管代管视图：在传入目标租户/站点时按「平台共享 ∪ 租户共享 ∪ 目标站」并集读，与域管 {@code domainSharedRead} 一致。
	 * 非超管由 {@code applyScope(ADMIN_DOMAIN)} 决定，忽略请求中的 comId/domainId。
	 */
	private void applyAdminTermScope(PageQuery pageQuery, Long comId, Long domainId) {
		if (!this.commonOperate.isAdmin(this.getUserId())) {
			return;
		}
		Long tenantForDomain = null;
		if (domainId != null && domainId > 0 && (comId == null || comId <= 0)) {
			tenantForDomain = this.service.resolveComIdForDomain(domainId);
		}
		DataScopeQueryHelper.applyAdminOperationalSharedRead(pageQuery, comId, domainId, tenantForDomain);
	}

	private boolean useScopedTermTree(Long comId, Long domainId) {
		if (!this.commonOperate.isAdmin(this.getUserId())) {
			return domainId != null && domainId > 0;
		}
		if (domainId != null && domainId > 0) {
			return true;
		}
		return comId != null;
	}

	private List<TreeSelectOption> queryScopedTreeSelect(String classType, Long comId, Long domainId) {
		PageQuery pageQuery = new PageQuery(new HashMap<>());
		pageQuery.pushParam("classType", classType);
		this.applyScope(pageQuery, DataScopeProfile.ADMIN_DOMAIN, KTerms.class);
		this.applyAdminTermScope(pageQuery, comId, domainId);
		return this.service.queryTreeSelectForClassType(classType, pageQuery);
	}

	/**
	 * 按行的归属层级（平台共享/租户共享/站点专属，见 {@link AxisLayerGuard#resolveAxisLayer}）标记当前操作者是否可改写，
	 * 与 {@link com.wldos.platform.core.service.TermService#assertTermMutable} 的写保护规则一致，
	 * 前端据此对不可改的共享行灰显「配置/删除」，避免点了才被 403 拒绝。递归处理分类树的子节点。
	 */
	private void decorateWritable(List<TermTree> rows) {
		if (rows == null || rows.isEmpty()) {
			return;
		}
		Long userId = this.getUserId();
		boolean admin = this.isAdmin(userId);
		AdminScopeLevel operatorLevel = admin ? AdminScopeLevel.PLATFORM : DataScopeResolver.resolveOperatorLevel(
				false,
				this.commonOperate.isTenantAdmin(userId),
				this.commonOperate.isDomainAdmin(userId),
				this.commonOperate.getPrimaryOrgId(userId) != null);
		for (TermTree row : rows) {
			if (row == null) {
				continue;
			}
			AdminScopeLevel rowLayer = AxisLayerGuard.resolveAxisLayer(row.getComId(), row.getDomainId());
			row.setWritable(admin || operatorLevel.getRank() >= rowLayer.getRank());
			if (row.getChildren() != null && !row.getChildren().isEmpty()) {
				this.decorateWritable(row.getChildren());
			}
		}
	}

	/**
	 * 通用分类项新增，支持所有类型
	 * 通过请求体中的 classType 字段指定类型
	 *
	 * @param term 分类项信息，必须包含 classType
	 * @return 操作结果
	 */
	@ApiOperation(value = "通用分类项新增", notes = "通用分类项新增，支持所有类型")
	@PostMapping("/admin/term-type/term/add")
	@OpLog(action = "新增分类项", resourceType = "term", resourceId = "#term.id")
	public Result addTerm(@ApiParam(value = "分类项信息", required = true) @Valid @RequestBody Term term) {
		// classType 是必填字段
		if (ObjectUtils.isBlank(term.getClassType())) {
			return Result.error(400, "classType 是必填字段");
		}
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
		if (!"ok".equals(res)) {
			return Result.error(ResultCode.VALIDATION_ERROR.getCode(), res);
		}
		// 如果是标签类型，追加缓存
		if (TermTypeEnum.TAG.toString().equals(term.getClassType())) {
			this.service.appendCacheTag(term);
		}
		this.service.refreshTerm();
		return Result.ok(res);
	}

	/**
	 * 通用分类项更新，支持所有类型
	 *
	 * @param term 分类项信息
	 * @return 操作结果
	 */
	@ApiOperation(value = "通用分类项更新", notes = "通用分类项更新，支持所有类型")
	@PostMapping("/admin/term-type/term/update")
	@OpLog(action = "更新分类项", resourceType = "term", resourceId = "#term.id")
	public Result updateTerm(@ApiParam(value = "分类项信息", required = true) @Valid @RequestBody Term term) {
		String res = this.service.updateTerm(term, this.getUserId(), this.getUserIp());
		if (!"ok".equals(res)) {
			return Result.error(ResultCode.VALIDATION_ERROR.getCode(), res);
		}
		this.service.refreshTerm();
		return Result.ok(res);
	}

	/**
	 * 通用分类项删除，支持所有类型
	 *
	 * @param term 分类项信息
	 * @return 操作结果
	 */
	@ApiOperation(value = "通用分类项删除", notes = "通用分类项删除，支持所有类型")
	@DeleteMapping("/admin/term-type/term/delete")
	@OpLog(action = "删除分类项", resourceType = "term", resourceId = "#term.id", recordParams = false)
	public Result deleteTerm(@ApiParam(value = "分类项信息", required = true) @Valid @RequestBody Term term) {
		this.service.deleteTerm(term, this.getUserId());
		this.service.refreshTerm();
		return Result.ok("ok");
	}

	/**
	 * 通用分类项批量删除，支持所有类型
	 *
	 * @param terms 分类项列表
	 * @return 操作结果
	 */
	@ApiOperation(value = "通用分类项批量删除", notes = "通用分类项批量删除，支持所有类型")
	@SuppressWarnings("unchecked")
	@DeleteMapping("/admin/term-type/term/deletes")
	@OpLog(action = "批量删除分类项", resourceType = "term")
	public Boolean removeTerms(@ApiParam(value = "分类项列表", required = true) @RequestBody List<Term> terms) {
		if (!ObjectUtils.isBlank(terms)) {
			this.service.deleteTerms(terms, this.getUserId());
			this.service.refreshTerm();
		}
		return Boolean.TRUE;
	}

	/**
	 * 批量给分类项设置信息发布状态
	 *
	 * @param termIds 分类项ids
	 * @return 设置结果，设置为开的分类项可以在信息发布门户展示和发布该类信息
	 */
	@ApiOperation(value = "设置信息发布状态", notes = "批量给分类项设置信息发布状态")
	@SuppressWarnings("unchecked")
	@PostMapping("/admin/cms/term/infoFlags")
	@OpLog(action = "切换信息发布状态", resourceType = "term", resourceId = "#termIds")
	public Boolean infoFlag(@ApiParam(value = "分类项IDs", required = true) @RequestBody List<Long> termIds) {
		if (!ObjectUtils.isBlank(termIds)) {
			this.service.infoFlagByIds(termIds);

			this.service.refreshTerm();
		}
		return Boolean.TRUE;
	}

	/** 展示顺序上限，见 {@link io.github.wldos.common.Constants#DISPLAY_ORDER_MAX} */
	private static final long DISPLAY_ORDER_MAX = Constants.DISPLAY_ORDER_MAX;

	private void handleDisplayOrder(Term term) {
		Long parentId = term.getParentId();

		Long order = this.service.queryMaxOrder(parentId);

		long nextOrder = ObjectUtils.nvlToZero(order) + 1L;
		term.setDisplayOrder(Math.min(nextOrder, DISPLAY_ORDER_MAX));
	}
}
