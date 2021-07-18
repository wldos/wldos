/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.Map;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.core.entity.WoArchitecture;
import com.wldos.system.core.service.ArchitectureService;
import com.wldos.system.vo.Architecture;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 体系结构相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/arch")
public class ArchitectureController extends RepoController<ArchitectureService, WoArchitecture> {
	/**
	 * 支持查询、排序的分页查询
	 *
	 * @param params
	 * @return
	 */
	@GetMapping("")
	public String listQuery(@RequestParam Map<String, Object> params) {
		//查询列表数据
		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) { // 过滤租户数据
			pageQuery.appendParam(PubConstants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}
		PageableResult<Architecture> res = this.service.execQueryForTree(new Architecture(), new WoArchitecture(), pageQuery, PubConstants.TOP_ARCH_ID);
		return resJson.ok(res);
	}
}
