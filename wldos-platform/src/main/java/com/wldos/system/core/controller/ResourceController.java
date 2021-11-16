/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.core.service.ResourceService;
import com.wldos.system.vo.AuthRes;
import com.wldos.system.vo.DomRes;
import com.wldos.system.vo.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 资源相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@RestController
@RequestMapping("admin/sys/res")
public class ResourceController extends RepoController<ResourceService, WoResource> {

	@GetMapping("")
	public PageableResult<Resource> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);

		return this.service.execQueryForTree(new Resource(), new WoResource(), pageQuery, Constants.MENU_ROOT_ID);
	}

	@GetMapping("tree")
	public List<AuthRes> allResTree() {
		List<WoResource> resources = this.service.findAll();

		List<AuthRes> authResList = resources.parallelStream().map(res -> {
			AuthRes authRes = new AuthRes();
			authRes.setId(res.getId());
			authRes.setParentId(res.getParentId());
			authRes.setTitle(res.getResourceName());
			authRes.setKey(res.getId().toString());

			return authRes;
		}).collect(Collectors.toList());

		return TreeUtil.build(authResList, Constants.MENU_ROOT_ID);
	}

	@GetMapping("select")
	public PageableResult<DomRes> listSelect(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);

		Long domainId = Long.parseLong(params.get("domainId").toString());

		return this.service.queryResByDomainId(domainId, this.getTenantId(), pageQuery);
	}

	@Override
	protected void postAdd(WoResource resource) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoResource resource) {
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoResource resource) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}
}
