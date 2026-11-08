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
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.search.CmsHybridSearchService;
import com.wldos.cms.service.PubService;
import com.wldos.cms.vo.SPub;
import io.github.wldos.common.res.PageQuery;
import io.github.wldos.common.res.PageData;
import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * 前端内容controller。
 *
 * @author Yuanxi Universe
 * @date 2021/12/03
 * @version 1.0
 */
@Api(tags = "前端内容管理")
@RequestMapping("/cms/pub")
@RestController
public class FrontPubController extends EntityController<PubService, KPubs> {

	@Autowired
	private CmsHybridSearchService cmsHybridSearchService;

	/**
	 * 全文检索
	 *
	 * @return 内容列表
	 */
	@ApiOperation(value = "全文检索", notes = "与 search-hybrid(scope=public) 同路径；无 Hybrid 时降级 instr")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "wd", value = "搜索关键词", dataTypeClass = String.class, paramType = "query", required = true, example = "关键词"),
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("search")
	public PageData<SPub> searchPosts(@RequestParam Map<String, Object> params) {
		// 检索关键字
		String wd = ObjectUtils.string(params.get("wd"));
		params.remove("wd");
		if (wd.equals(""))
			return new PageData<>();
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		return cmsHybridSearchService.searchHybrid(this.getDomainId(), pageQuery, wd, false);
	}

	@ApiOperation(value = "混合全文检索", notes = "BM25+向量 RRF；无 Hybrid 服务时降级 instr")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "wd", value = "搜索关键词", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "scope", value = "public 或 internal", dataTypeClass = String.class, paramType = "query", example = "public"),
		@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10")
	})
	@GetMapping("search-hybrid")
	public PageData<SPub> searchHybridPosts(@RequestParam Map<String, Object> params) {
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

	@ApiOperation(value = "内部资料全文检索", notes = "含 INTERNAL_ONLY；须登录，域由 Host 决定")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "wd", value = "搜索关键词", dataTypeClass = String.class, paramType = "query", required = true),
		@ApiImplicitParam(name = "current", value = "当前页码", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10")
	})
	@GetMapping("search-internal")
	public PageData<SPub> searchInternalPosts(@RequestParam Map<String, Object> params) {
		String wd = ObjectUtils.string(params.get("wd"));
		params.remove("wd");
		if (wd.equals("")) {
			return new PageData<>();
		}
		PageQuery pageQuery = new PageQuery(params);
		return cmsHybridSearchService.searchHybrid(this.getDomainId(), pageQuery, wd, true);
	}
}
