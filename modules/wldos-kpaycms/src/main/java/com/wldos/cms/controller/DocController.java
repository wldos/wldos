/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.framework.mvc.controller.NonEntityController;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.DocService;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.Doc;
import com.wldos.cms.vo.DocItem;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.platform.core.enums.PubTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * 文档库controller。
 *
 * @author 元悉宇宙
 * @date 2023/5/10
 * @version 1.0
 */
@Api(tags = "文档库管理")
@RequestMapping("/doc")
@RestController
public class DocController extends NonEntityController<DocService> {
	private final KCMSService kcmsService;

	public DocController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

	@ApiOperation(value = "文档列表", notes = "查询文档列表")
	@ApiImplicitParams({
		@ApiImplicitParam(name = "current", value = "当前页码，从1开始", dataTypeClass = Integer.class, paramType = "query", example = "1"),
		@ApiImplicitParam(name = "pageSize", value = "每页条数", dataTypeClass = Integer.class, paramType = "query", example = "10"),
		@ApiImplicitParam(name = "sorter", value = "排序规则，JSON格式", dataTypeClass = String.class, paramType = "query"),
		@ApiImplicitParam(name = "filter", value = "过滤条件，JSON格式", dataTypeClass = String.class, paramType = "query")
	})
	@GetMapping("")
	public PageableResult<DocItem> listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		pageQuery.pushParam("pubStatus", PubStatusEnum.PUBLISH.toString());
		pageQuery.pushParam("deleteFlag", DeleteFlagEnum.NORMAL.toString());
		pageQuery.pushParam("pubType", PubTypeEnum.DOC.getName());
		this.applyDomainFilter(pageQuery);

		return this.service.queryDocList(pageQuery);
	}

	@ApiOperation(value = "文档详情", notes = "根据ID查询文档详情")
	@GetMapping("book/{bookId:\\d+}.html")
	public Doc curDocById(@ApiParam(value = "文档ID", required = true) @PathVariable Long bookId) {
		return this.service.queryDoc(bookId, this.getDomainId());
	}

	@ApiOperation(value = "章节详情", notes = "根据ID查询章节详情")
	@GetMapping("book/{bookId:\\d+}/chapter/{chapterId:\\d+}.html")
	public Article curChapterByChapId(@ApiParam(value = "文档ID", required = true) @PathVariable Long bookId, 
			@ApiParam(value = "章节ID", required = true) @PathVariable Long chapterId) {
		return this.kcmsService.queryArticle(chapterId, false, this.getDomainId());
	}
}