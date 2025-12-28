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
import com.wldos.cms.vo.SPub;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 前端内容controller。
 *
 * @author 元悉宇宙
 * @date 2021/12/03
 * @version 1.0
 */
@Api(tags = "前端内容管理")
@RequestMapping("/cms/pub")
@RestController
public class FrontPubController extends EntityController<PubService, KPubs> {
	/**
	 * 全文检索
	 *
	 * @return 内容列表
	 */
	@ApiOperation(value = "全文检索", notes = "搜索内容")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "wd", value = "搜索关键词", dataTypeClass = String.class, paramType = "query", required = true, example = "关键词"),
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@RequestMapping("search")
	public PageableResult<SPub> searchPosts(@RequestParam Map<String, Object> params) {
		// 检索关键字
		String wd = ObjectUtils.string(params.get("wd"));
		params.remove("wd");
		if (wd.equals(""))
			return new PageableResult<>();
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);

		return this.service.searchPubs(this.getDomainId(), pageQuery, wd);
	}
}
