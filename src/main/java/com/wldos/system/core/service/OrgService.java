/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoOrgUser;
import com.wldos.system.core.entity.WoRole;
import com.wldos.system.core.entity.WoRoleOrg;
import com.wldos.system.core.repo.OrgRepo;
import com.wldos.system.core.repo.OrgUserRepo;
import com.wldos.system.core.repo.RoleOrgRepo;
import com.wldos.system.vo.AuthRes;
import com.wldos.system.vo.OrgRole;
import com.wldos.system.vo.OrgRoleTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrgService extends BaseService<OrgRepo, WoOrg, Long> {

	private final RoleService roleService;

	private final RoleOrgRepo roleOrgRepo;

	private final OrgUserRepo orgUserRepo;

	@Autowired
	public OrgService(RoleService roleService, RoleOrgRepo roleOrgRepo, OrgUserRepo orgUserRepo) {
		this.roleService = roleService;
		this.roleOrgRepo = roleOrgRepo;
		this.orgUserRepo = orgUserRepo;
	}

	/**
	 * 查询组织绑定的系统角色
	 *
	 * @param orgId Organization ID
	 * @return Role of organization binding
	 */
	public List<OrgRole> queryAuthRole(Long orgId) {
		List<WoRoleOrg> roleOrgs = this.roleService.queryRoleByOrgId(orgId);

		return roleOrgs.parallelStream().map(item -> {
			OrgRole roleRes = new OrgRole();
			roleRes.setId(item.getRoleId());
			return roleRes;
		}).collect(Collectors.toList());
	}

	/**
	 * 查询组织绑定的系统角色和可绑定的所有角色树
	 *
	 * @param orgId Organization ID
	 * @param userId The ID of the current login user
	 * @return Organization role tree
	 */
	public OrgRoleTree queryAllRoleTreeByOrgId(long orgId, long userId) {
		List<WoRole> roles = this.isAdmin(userId) ? this.roleService.findAll() : this.roleService.findAllTenant();
		List<OrgRole> orgRoles = this.queryAuthRole(orgId);
		List<AuthRes> authResList = roles.parallelStream().map(role -> {
			AuthRes authRes = new AuthRes();
			authRes.setId(role.getId());
			authRes.setParentId(role.getParentId());
			authRes.setTitle(role.getRoleName());
			authRes.setKey(role.getId().toString());
			return authRes;
		}).collect(Collectors.toList());

		authResList = TreeUtil.build(authResList, PubConstants.TOP_ROLE_ID);

		OrgRoleTree orgRoleTree = new OrgRoleTree();
		orgRoleTree.setOrgRole(orgRoles);
		orgRoleTree.setAuthRes(authResList);

		return orgRoleTree;
	}

	/**
	 * 给组织关联系统角色，对于实组织，关联角色仅针对直属人员生效，对于虚组织(角色组)，虚组织不在组织人员树中展示。
	 *
	 * @param roleIds
	 * @param orgId
	 * @param archId
	 * @param comId
	 * @param curUserId
	 * @param uip
	 */
	public void authOrg(List<String> roleIds, Long orgId, Long archId, Long comId, Long curUserId, String uip) {
		List<WoRoleOrg> roleOrgList = roleIds.parallelStream().map(item -> {
			Long roleId = Long.parseLong(item);

			WoRoleOrg roleOrg = new WoRoleOrg();
			roleOrg.setOrgId(orgId);
			roleOrg.setRoleId(roleId);
			EntityAssists.beforeInsert(roleOrg, this.nextId(), curUserId, uip, true);
			roleOrg.setArchId(archId);
			roleOrg.setComId(comId);

			return roleOrg;
		}).collect(Collectors.toList());

		// 2.delete from wo_role_org where roleId = ?
		this.roleOrgRepo.deleteByOrgIdAndArchIdAndComId(orgId, archId, comId);

		if (!roleOrgList.isEmpty())
			this.roleOrgRepo.saveAll(roleOrgList);
	}

	/**
	 * 给组织添加成员，一般系统级的用户组都是在登录、付费升级等业务应用中自动添加成员，此处为租户给自己的组织新增成员。
	 *
	 * @param userIds
	 * @param orgId
	 * @param archId
	 * @param comId
	 * @param curUserId
	 * @param uip
	 */
	public String userOrg(List<String> userIds, Long orgId, Long archId, Long comId, Long curUserId, String uip) {
		StringBuilder mes = new StringBuilder(50);
		List<WoOrgUser> orgUserList = userIds.parallelStream().map(item -> {
			Long userId = Long.parseLong(item);

			boolean exists = this.orgUserRepo.existsById(userId);
			if (exists) {
				mes.append("用户：").append(userId).append(" 已存在; ");
				return null;
			}

			WoOrgUser orgUser = new WoOrgUser();
			orgUser.setOrgId(orgId);
			orgUser.setUserId(userId);
			EntityAssists.beforeInsert(orgUser, this.nextId(), curUserId, uip, true);
			orgUser.setArchId(archId);
			orgUser.setComId(comId);

			return orgUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!orgUserList.isEmpty())
			this.orgUserRepo.saveAll(orgUserList);

		return mes.toString();
	}
}
