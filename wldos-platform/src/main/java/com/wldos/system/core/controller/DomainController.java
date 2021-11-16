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

import com.wldos.support.Constants;
import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.system.core.entity.WoDomain;
import com.wldos.system.core.entity.WoDomainResource;
import com.wldos.system.core.service.DomainService;
import com.wldos.system.vo.DomainResource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 域相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/domain")
public class DomainController extends RepoController<DomainService, WoDomain> {

	@GetMapping("")
	public PageableResult<WoDomain> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		return this.service.queryDomainList(pageQuery);
	}

	@GetMapping("resList")
	public PageableResult<DomainResource> listDomainRes(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}
		Long domainId = Long.parseLong(params.get("domainId").toString());
		return this.service.queryDomainRes(domainId, pageQuery);
	}

	@PostMapping("app")
	public String domainApp(@RequestBody Map<String, Object> domainApp) {
		Long domainId = Long.parseLong(domainApp.get("domainId").toString());
		Long comId = Long.parseLong(domainApp.get("comId").toString());
		List<String> appIds = (List<String>) domainApp.get("ids");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		String message = this.service.domainApp(appIds, domainId, comId, curUserId, uip);

		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain);

		return this.resJson.ok(message);
	}

	@DeleteMapping("appDel")
	public Boolean removeDomainApp(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<Object>) params.get("ids")).stream().map(id -> Long.parseLong(ObjectUtil.string(id))).collect(Collectors.toList());
		Long domainId = Long.parseLong(ObjectUtil.string(params.get("domainId")));

		this.service.removeDomainApp(ids, domainId);
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain);

		return Boolean.TRUE;
	}

	@PostMapping("res")
	public String domainRes(@RequestBody Map<String, Object> domainRes) {
		Long domainId = Long.parseLong(domainRes.get("domainId").toString());
		List<String> resIds = (List<String>) domainRes.get("ids");

		String message = this.service.domainRes(resIds, domainId, this.getCurUserId(), this.getUserIp());

		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain);

		return this.resJson.ok(message);
	}

	@DeleteMapping("resDel")
	public Boolean removeDomainRes(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<Object>) params.get("ids")).stream().map(id -> Long.parseLong(ObjectUtil.string(id))).collect(Collectors.toList());
		Long domainId = Long.parseLong(ObjectUtil.string(params.get("domainId")));

		this.service.removeDomainRes(ids, domainId);
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain);

		return Boolean.TRUE;
	}

	@PostMapping("resConf")
	public Boolean domainResConf(@RequestBody WoDomainResource dRes) {
		this.service.domainResConf(dRes);
		Long domainId = dRes.getDomainId();
		WoDomain domain = this.service.findById(domainId);
		this.service.refreshDomain(domain);

		return Boolean.TRUE;
	}

	@GetMapping("root")
	public String platformDomain() {
		return this.resJson.ok(Constants.COMMON_KEY_PLATFORM_DOMAIN, this.getPlatDomain());
	}

	@Override
	protected void preAdd(WoDomain entity) {
		entity.setComId(this.getTenantId());
	}

	@Override
	protected void postAdd(WoDomain domain) {
		this.service.refreshDomain(domain);
	}

	@Override
	protected void postUpdate(WoDomain domain) {
		this.service.refreshDomain(domain);
	}

	@Override
	protected void postDelete(WoDomain domain) {
		this.service.refreshDomain(domain);
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		ids.parallelStream().forEach(id ->
				this.service.refreshDomain(this.service.findById((Long) id))
				);
	}
}
