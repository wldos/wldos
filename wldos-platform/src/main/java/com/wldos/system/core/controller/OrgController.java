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
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.PageQuery;
import com.wldos.support.Constants;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.service.OrgService;
import com.wldos.system.enums.OrgTypeEnum;
import com.wldos.system.vo.Org;
import com.wldos.system.vo.OrgRoleTree;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公司相关controller。
 *
 * @author 树悉猿
 * @date 2021/5/2
 * @version 1.0
 */
@SuppressWarnings("unchecked")
@RestController
@RequestMapping("admin/sys/org")
public class OrgController extends RepoController<OrgService, WoOrg> {

	@GetMapping("")
	public PageableResult<Org> listQuery(@RequestParam Map<String, Object> params) {

		PageQuery pageQuery = new PageQuery(params);
		if (this.isMultiTenancy() && !this.service.isAdmin(this.getCurUserId())) {
			pageQuery.appendParam(Constants.COMMON_KEY_TENANT_COLUMN, this.getTenantId());
		}

		return this.service.execQueryForTree(new Org(), new WoOrg(), pageQuery, Constants.TOP_ORG_ID);
	}

	@Override
	protected void preAdd(WoOrg entity) {
		entity.setComId(this.getTenantId());
	}

	@GetMapping("role")
	public OrgRoleTree queryAuthRole(@RequestParam Map<String, String> authOrg) {

		return this.service.queryAllRoleTreeByOrgId(Long.parseLong(authOrg.get("orgId")), this.getCurUserId());
	}

	@PostMapping("auth")
	public String authOrg(@RequestBody Map<String, Object> roleOrg) {
		Long orgId = Long.parseLong(roleOrg.get("orgId").toString());
		Long archId = Long.parseLong(roleOrg.get("archId").toString());
		Long comId = Long.parseLong(roleOrg.get("comId").toString());
		List<String> roleIds = (List<String>) roleOrg.get("roleIds");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		this.service.authOrg(roleIds, orgId, archId, comId, curUserId, uip);

		return this.resJson.ok("ok");
	}

	@PostMapping("user")
	public String userOrg(@RequestBody Map<String, Object> orgUser) {
		Long orgId = Long.parseLong(orgUser.get("orgId").toString());
		Long archId = Long.parseLong(orgUser.get("archId").toString());
		Long comId = Long.parseLong(orgUser.get("comId").toString());
		List<String> userIds = (List<String>) orgUser.get("userIds");
		Long curUserId = this.getCurUserId();
		String uip = this.getUserIp();

		String message = this.service.userOrg(userIds, orgId, archId, comId, curUserId, uip);

		return this.resJson.ok(message);
	}

	@GetMapping("platOrg")
	public List<Org> queryPlatformRoleOrg() {

		return this.service.queryPlatformRoleOrg(OrgTypeEnum.PLATFORM.toString());
	}

	@DeleteMapping("staffDel")
	public Boolean removeOrgStaff(@RequestBody Map<String, Object> params) {
		List<Long> ids = ((List<String>) params.get("ids")).stream().map(Long::parseLong).collect(Collectors.toList());
		Long orgId = Long.parseLong(ObjectUtil.string(params.get("orgId")));
		this.service.removeOrgStaff(ids, orgId);

		return Boolean.TRUE;
	}

	@Override
	protected void postAdd(WoOrg org) {
		this.refreshAuth();
	}

	@Override
	protected void postUpdate(WoOrg org) {
		this.refreshAuth();
	}

	@Override
	protected void postDelete(WoOrg org) {
		this.refreshAuth();
	}

	@Override
	protected void postDeletes(List<Object> ids) {
		this.refreshAuth();
	}
}
