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

import com.wldos.support.controller.RepoController;
import com.wldos.support.controller.web.PageableResult;
import com.wldos.support.util.PageQuery;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.service.OrgService;
import com.wldos.system.vo.Org;
import com.wldos.system.vo.OrgRoleTree;

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
		PageableResult<Org> res = this.service.execQueryForTree(new Org(), new WoOrg(), pageQuery, PubConstants.TOP_ORG_ID);
		return resJson.ok(res);
	}

	/**
	 * 查询某个组织授予的角色（不继承）
	 *
	 * @param authOrg
	 * @return
	 */
	@GetMapping("role")
	public String queryAuthRole(@RequestParam Map<String, String> authOrg) {

		// 查询当前组织关联的角色列表
		OrgRoleTree orgRoleTree = this.service.queryAllRoleTreeByOrgId(Long.parseLong(authOrg.get("orgId")), this.getCurUserId());

		return this.resJson.ok(orgRoleTree);
	}

	/**
	 * 给组织关联系统角色，对于实组织，关联角色仅针对直属人员生效，对于虚组织(角色组)，虚组织不在组织人员树中展示。
	 *
	 * @param roleOrg
	 * @return
	 */
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

	/**
	 * 给组织添加成员，一般系统级的用户组都是在登录、付费升级等业务应用中自动添加成员，此处为租户给自己的组织新增成员。
	 *
	 * @param orgUser
	 * @return
	 */
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
}
