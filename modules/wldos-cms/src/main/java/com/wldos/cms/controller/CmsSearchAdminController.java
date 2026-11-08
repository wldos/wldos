/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.cms.controller;

import com.wldos.cms.service.CmsSearchReindexService;
import com.wldos.cms.vo.CmsSearchOverviewVO;
import com.wldos.cms.vo.CmsSearchRebuildSummary;
import com.wldos.framework.mvc.controller.NonEntityController;

import io.github.wldos.common.res.Result;
import io.github.wldos.framework.support.audit.annotation.OpLog;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * CMS 检索运维（全量重建、配置概览）。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Api(tags = "CMS检索运维")
@RestController
@RequestMapping("/admin/cms/search")
public class CmsSearchAdminController extends NonEntityController<CmsSearchReindexService> {

	@ApiOperation(value = "检索配置概览", notes = "只读 wldos.search.* 与 CMS namespace")
	@GetMapping("overview")
	public Result<CmsSearchOverviewVO> overview() {
		return Result.ok(service.overview());
	}

	@ApiOperation(value = "全量重建 CMS 检索索引", notes = "Lucene rebuild + 可选向量 upsert；须超管或 org admin")
	@PostMapping("rebuild")
	@OpLog(action = "CMS检索全量重建", resourceType = "cms_search")
	public Result<CmsSearchRebuildSummary> rebuild() {
		return Result.ok(service.rebuildAll(this.getUserId()));
	}
}
