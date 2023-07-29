/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.base.NoRepoController;
import com.wldos.cms.enums.PubStatusEnum;
import com.wldos.cms.service.DocService;
import com.wldos.cms.service.KCMSService;
import com.wldos.cms.vo.Article;
import com.wldos.cms.vo.Doc;
import com.wldos.cms.vo.DocItem;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.sys.base.enums.PubTypeEnum;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档库controller。
 *
 * @author 树悉猿
 * @date 2023/5/10
 * @version 1.0
 */
@RequestMapping("/doc")
@RestController
public class DocController extends NoRepoController<DocService> {
	private final KCMSService kcmsService;

	public DocController(KCMSService kcmsService) {
		this.kcmsService = kcmsService;
	}

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

	@GetMapping("book/{bookId:\\d+}.html")
	public Doc curDocById(@PathVariable Long bookId) {
		return this.service.queryDoc(bookId, this.getDomainId());
	}

	@GetMapping("book/{bookId:\\d+}/chapter/{chapterId:\\d+}.html")
	public Article curChapterByChapId(@PathVariable Long bookId, @PathVariable Long chapterId) {
		return this.kcmsService.queryArticle(chapterId, false, this.getDomainId());
	}
}