/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.controller;

import java.util.List;
import java.util.Map;

import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.res.Result;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.vo.SelectOption;
import com.wldos.common.vo.TreeSelectOption;
import com.wldos.framework.mvc.controller.EntityController;
import com.wldos.platform.core.entity.KTerms;
import com.wldos.platform.core.service.TermService;
import com.wldos.platform.core.vo.Category;
import com.wldos.platform.core.vo.TermTree;
import com.wldos.platform.support.term.dto.Term;
import com.wldos.platform.support.term.enums.TermTypeEnum;

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
 * 租户共享平台分类、标签设置，不具备管理权限。
 *
 * @author 元悉宇宙
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
		@ApiImplicitParam(name = "name", value = "分类名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("/admin/cms/category")
	public PageableResult<TermTree> listCategory(@RequestParam Map<String, Object> params) {
		// 如果参数中没有 classType，则默认为 category
		if (!params.containsKey("classType") || ObjectUtils.isBlank(params.get("classType"))) {
			params.put("classType", TermTypeEnum.CATEGORY.toString());
		}
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.queryTermForTree(new KTerms(), pageQuery, false);
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
		@ApiImplicitParam(name = "name", value = "标签名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("/admin/cms/tag")
	public PageableResult<TermTree> listTag(@RequestParam Map<String, Object> params) {
		params.put("classType", TermTypeEnum.TAG.toString());
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.queryTermForTree(new KTerms(), pageQuery, true);
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
		return this.service.queryFlatCategoryTree();
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
		return this.service.queryFlatCategoryTree();
	}

	/**
	 * 获取分类树状列表
	 * (常规分层, [{title: '', value: '', key: '', children: [{...}...]},])
	 *
	 * @return 分类树状列表
	 */
	@ApiOperation(value = "分类树状列表", notes = "获取分类树状列表")
	@GetMapping("category/treeSelect")
	public List<TreeSelectOption> categoryLayerTree() {
		return this.service.queryLayerCategoryTree();
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
	public List<TreeSelectOption> termCategoryLayerTree(@ApiParam(value = "分类类型", required = true) @RequestParam String classType) {
		return this.service.queryLayerCategoryTreeByType(classType);
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
		return this.service.queryFlatTags();
	}

	@ApiOperation(value = "添加分类", notes = "添加新的分类")
	@PostMapping("/admin/cms/category/add")
	public Result addCategory(@ApiParam(value = "分类信息", required = true) @Valid @RequestBody Term term) {
		term.setClassType(TermTypeEnum.CATEGORY.toString());
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
		this.service.refreshTerm();
		return Result.ok(res);
	}

	@ApiOperation(value = "更新分类", notes = "更新分类信息")
	@PostMapping("/admin/cms/category/update")
	public Result updateCategory(@ApiParam(value = "分类信息", required = true) @Valid @RequestBody Term term) {
		String res = this.service.updateTerm(term, this.getUserId(), this.getUserIp());
		this.service.refreshTerm();
		return Result.ok(res);
	}

	@ApiOperation(value = "删除分类", notes = "删除指定的分类")
	@DeleteMapping("/admin/cms/category/delete")
	public Result deleteCategory(@ApiParam(value = "分类信息", required = true) @Valid @RequestBody Term term) {
		this.service.deleteTerm(term);
		this.service.refreshTerm();
		return Result.ok("ok");
	}

	@ApiOperation(value = "批量删除分类", notes = "批量删除分类")
	@SuppressWarnings("unchecked")
	@DeleteMapping("/admin/cms/category/deletes")
	public Boolean removeIds(@ApiParam(value = "分类列表", required = true) @RequestBody List<Term> terms) {
		if (!ObjectUtils.isBlank(terms)) {
			this.service.deleteTerms(terms);

			this.service.refreshTerm();
		}

		return Boolean.TRUE;
	}

	@ApiOperation(value = "添加标签", notes = "添加新的标签")
	@PostMapping("/admin/cms/tag/add")
	public Result addTag(@ApiParam(value = "标签信息", required = true) @Valid @RequestBody Term term) {
		term.setClassType(TermTypeEnum.TAG.toString());
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
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
		@ApiImplicitParam(name = "name", value = "分类项名称（模糊查询）", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("/admin/term-type/term")
	public PageableResult<TermTree> listTerm(@RequestParam Map<String, Object> params) {
		// classType 是必填参数
		if (!params.containsKey("classType") || ObjectUtils.isBlank(params.get("classType"))) {
			return new PageableResult<>(0L, 1, 15, new java.util.ArrayList<>());
		}
		// 判断是否为扁平结构（tag 类型）
		boolean isFlat = TermTypeEnum.TAG.toString().equals(params.get("classType"));
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		return this.service.queryTermForTree(new KTerms(), pageQuery, isFlat);
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
	public Result addTerm(@ApiParam(value = "分类项信息", required = true) @Valid @RequestBody Term term) {
		// classType 是必填字段
		if (ObjectUtils.isBlank(term.getClassType())) {
			return Result.error(400, "classType 是必填字段");
		}
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
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
	public Result updateTerm(@ApiParam(value = "分类项信息", required = true) @Valid @RequestBody Term term) {
		String res = this.service.updateTerm(term, this.getUserId(), this.getUserIp());
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
	public Result deleteTerm(@ApiParam(value = "分类项信息", required = true) @Valid @RequestBody Term term) {
		this.service.deleteTerm(term);
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
	public Boolean removeTerms(@ApiParam(value = "分类项列表", required = true) @RequestBody List<Term> terms) {
		if (!ObjectUtils.isBlank(terms)) {
			this.service.deleteTerms(terms);
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
	public Boolean infoFlag(@ApiParam(value = "分类项IDs", required = true) @RequestBody List<Long> termIds) {
		if (!ObjectUtils.isBlank(termIds)) {
			this.service.infoFlagByIds(termIds);

			this.service.refreshTerm();
		}
		return Boolean.TRUE;
	}

	private void handleDisplayOrder(Term term) {
		Long parentId = term.getParentId();

		Long order = this.service.queryMaxOrder(parentId);

		term.setDisplayOrder(ObjectUtils.nvlToZero(order) + 1L);
	}
}
