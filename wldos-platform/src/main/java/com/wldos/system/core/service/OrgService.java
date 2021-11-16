/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.enums.BoolEnum;
import com.wldos.support.enums.DeleteFlagEnum;
import com.wldos.support.enums.ValidStatusEnum;
import com.wldos.support.service.BaseService;
import com.wldos.support.Constants;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.core.entity.WoComUser;
import com.wldos.system.core.entity.WoOrg;
import com.wldos.system.core.entity.WoOrgUser;
import com.wldos.system.core.entity.WoRole;
import com.wldos.system.core.entity.WoRoleOrg;
import com.wldos.system.core.repo.ComUserRepo;
import com.wldos.system.core.repo.OrgRepo;
import com.wldos.system.core.repo.OrgUserRepo;
import com.wldos.system.core.repo.RoleOrgRepo;
import com.wldos.system.vo.AuthRes;
import com.wldos.system.vo.Org;
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

	private final ComUserRepo comUserRepo;

	@Autowired
	public OrgService(RoleService roleService, RoleOrgRepo roleOrgRepo, OrgUserRepo orgUserRepo, ComUserRepo comUserRepo) {
		this.roleService = roleService;
		this.roleOrgRepo = roleOrgRepo;
		this.orgUserRepo = orgUserRepo;
		this.comUserRepo = comUserRepo;
	}

	public List<OrgRole> queryAuthRole(Long orgId) {
		List<WoRoleOrg> roleOrgs = this.roleService.queryRoleByOrgId(orgId);

		return roleOrgs.parallelStream().map(item -> {
			OrgRole roleRes = new OrgRole();
			roleRes.setId(item.getRoleId());
			return roleRes;
		}).collect(Collectors.toList());
	}

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

		authResList = TreeUtil.build(authResList, Constants.TOP_ROLE_ID);

		OrgRoleTree orgRoleTree = new OrgRoleTree();
		orgRoleTree.setOrgRole(orgRoles);
		orgRoleTree.setAuthRes(authResList);

		return orgRoleTree;
	}

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

		this.roleOrgRepo.deleteByOrgIdAndArchIdAndComId(orgId, archId, comId);

		if (!roleOrgList.isEmpty())
			this.roleOrgRepo.saveAll(roleOrgList);
	}

	public String userOrg(List<String> userIds, Long orgId, Long archId, Long comId, Long curUserId, String uip) {
		List<Long> ids = userIds.parallelStream().map(Long::parseLong).collect(Collectors.toList());
		List<WoOrgUser> orgUsers = this.orgUserRepo.queryAllByUserIds(orgId, archId, comId, ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, Long> userInOrg = orgUsers.parallelStream().collect(Collectors.toMap(WoOrgUser::getUserId, WoOrgUser::getUserId));
		StringBuilder mes = new StringBuilder(50);
		List<WoOrgUser> orgUserList = ids.parallelStream().map(userId -> {

			if (userInOrg.containsKey(userId)) {
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

		List<WoComUser> comUsers = this.comUserRepo.queryAllByUserIds(ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, List<WoComUser>> userComList = comUsers.parallelStream().collect(Collectors.groupingBy(WoComUser::getUserId));
		List<WoComUser> woComUsers = ids.parallelStream().map(userId -> {
			WoComUser comUser;
			if (userComList.containsKey(userId))
				return null;

			comUser = new WoComUser();
			comUser.setComId(comId);
			comUser.setUserId(userId);
			comUser.setIsMain(userComList.isEmpty() ? BoolEnum.YES.toString() : BoolEnum.NO.toString());
			EntityAssists.beforeInsert(comUser, this.nextId(), curUserId, uip, true);

			return comUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		if (!orgUserList.isEmpty())
			this.orgUserRepo.saveAll(orgUserList);
		if (!woComUsers.isEmpty())
			this.comUserRepo.saveAll(woComUsers);

		return mes.toString();
	}

	public List<Org> queryPlatformRoleOrg(String orgType) {
		return this.entityRepo.findAllByOrgType(DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString(), orgType);
	}

	public void removeOrgStaff(List<Long> ids, Long orgId) {
		this.orgUserRepo.removeOrgStaff(ids, orgId);
	}
}
