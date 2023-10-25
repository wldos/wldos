/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.cms.controller;

import java.util.Map;

import com.wldos.framework.controller.RepoController;
import com.wldos.cms.entity.KPubs;
import com.wldos.cms.service.PubService;
import com.wldos.cms.vo.SPub;
import com.wldos.common.res.PageQuery;
import com.wldos.common.res.PageableResult;
import com.wldos.common.utils.ObjectUtils;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前端内容controller。
 *
 * @author 树悉猿
 * @date 2021/12/03
 * @version 1.0
 */
@RequestMapping("/cms/pub")
@RestController
public class FrontPubController extends RepoController<PubService, KPubs> {
	/**
	 * 全文检索
	 *
	 * @return 内容列表
	 */
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
