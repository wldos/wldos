/*
 * Copyright (c) 2020 - 2025 yuanxiyuzhou. All rights reserved.
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

/**
 * 分类管理controller。
 * 租户共享平台分类、标签设置，不具备管理权限。
 *
 * @author 元悉宇宙
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
public class TermController extends EntityController<TermService, KTerms> {

	/**
	 * 分类列表，支持查询、排序的分页查询
	 *
	 * @param params 分页
	 * @return 分类项树形列表
	 */
	@GetMapping("/admin/cms/category")
	public PageableResult<TermTree> listCategory(@RequestParam Map<String, Object> params) {
		params.put("classType", TermTypeEnum.CATEGORY.toString());
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
	@GetMapping("category/treeSelect")
	public List<TreeSelectOption> categoryLayerTree() {
		return this.service.queryLayerCategoryTree();
	}

	/**
	 * 根据父分类id获取所有子分类（包含自身）
	 * (结构：[{title: '', key: '', slug: '', conType: ''}])
	 *
	 * @return 两级分类树列表
	 */
	@GetMapping("category/fromPid/{parentId:\\d+}")
	public List<Category> categoryFromPid(@PathVariable Long parentId) {
		return this.service.queryCategoriesFromPid(parentId);
	}

	/**
	 * 根据父分类别名获取所有子分类（包含自身）
	 * (结构：[{title: '', key: '', slug: '', conType: ''}])
	 *
	 * @return 两级分类树列表
	 */
	@GetMapping("category/fromPlug/{slugTerm}")
	public List<Category> categoryFromPlug(@PathVariable String slugTerm) {
		return this.service.queryCategoriesFromPlug(slugTerm);
	}

	/**
	 * 获取平顶级分类下拉列表([{label: 'l', value: 'v'},])
	 *
	 * @return 顶级分类下拉列表
	 */
	@GetMapping("category/select")
	public List<SelectOption> queryTopCategories() {
		return this.service.queryTopCategories();
	}

	/**
	 * 获取平台标签下拉列表([{label: 'l', value: 'v'},])
	 *
	 * @return 标签下拉列表
	 */
	@GetMapping("tag/select")
	public List<SelectOption> queryFlatTags() {
		return this.service.queryFlatTags();
	}

	@PostMapping("/admin/cms/category/add")
	public String addCategory(@RequestBody Term term) {
		term.setClassType(TermTypeEnum.CATEGORY.toString());
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
		this.service.refreshTerm();
		return this.resJson.ok(res);
	}

	@PostMapping("/admin/cms/category/update")
	public String updateCategory(@RequestBody Term term) {
		String res = this.service.updateTerm(term, this.getUserId(), this.getUserIp());
		this.service.refreshTerm();
		return this.resJson.ok(res);
	}

	@DeleteMapping("/admin/cms/category/delete")
	public String deleteCategory(@RequestBody Term term) {
		this.service.deleteTerm(term);
		this.service.refreshTerm();
		return this.resJson.ok("ok");
	}

	@SuppressWarnings("unchecked")
	@DeleteMapping("/admin/cms/category/deletes")
	public Boolean removeIds(@RequestBody List<Term> terms) {
		if (!ObjectUtils.isBlank(terms)) {
			this.service.deleteTerms(terms);

			this.service.refreshTerm();
		}

		return Boolean.TRUE;
	}

	@PostMapping("/admin/cms/tag/add")
	public String addTag(@RequestBody Term term) {
		term.setClassType(TermTypeEnum.TAG.toString());
		this.handleDisplayOrder(term);
		String res = this.service.addTerm(term, this.getUserId(), this.getUserIp());
		this.service.appendCacheTag(term); // 新增标签追加缓存
		return this.resJson.ok(res);
	}

	/**
	 * 批量给分类项设置信息发布状态
	 *
	 * @param termIds 分类项ids
	 * @return 设置结果，设置为开的分类项可以在信息发布门户展示和发布该类信息
	 */
	@SuppressWarnings("unchecked")
	@PostMapping("/admin/cms/term/infoFlags")
	public Boolean infoFlag(@RequestBody List<Long> termIds) {
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
