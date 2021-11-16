/*
 * Copyright (c) 2020 - 2021.  Owner of wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see terms.md or https://www.wldos.com/
 *
 */

package com.wldos.system.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.support.Constants;
import com.wldos.support.controller.EntityAssists;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.ObjectUtil;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.core.entity.WoAuthRole;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.core.entity.WoRole;
import com.wldos.system.core.entity.WoRoleOrg;
import com.wldos.system.core.repo.AuthRoleRepo;
import com.wldos.system.core.repo.RoleOrgRepo;
import com.wldos.system.core.repo.RoleRepo;
import com.wldos.system.enums.RoleTypeEnum;
import com.wldos.system.vo.AuthRes;
import com.wldos.system.vo.RoleRes;
import com.wldos.system.vo.RoleResTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 角色管理service。
 *
 * @author 树悉猿
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RoleService extends BaseService<RoleRepo, WoRole, Long> {
	private final AuthRoleRepo authRoleRepo;

	private final ResourceService resourceService;

	private final RoleOrgRepo roleOrgRepo;

	@Autowired
	public RoleService(ResourceService resourceService, AuthRoleRepo authRoleRepo, RoleOrgRepo roleOrgRepo) {
		this.resourceService = resourceService;
		this.authRoleRepo = authRoleRepo;
		this.roleOrgRepo = roleOrgRepo;
	}

	public List<RoleRes> queryAuthRes(Long roleId) {
		List<WoResource> resources = this.resourceService.queryResourceByRoleId(roleId);

		List<RoleRes> roleResList = resources.parallelStream().map(item -> {
			RoleRes roleRes = new RoleRes();
			roleRes.setId(item.getId());
			roleRes.setInherit(false);
			return roleRes;
		}).collect(Collectors.toList());

		resources = this.resourceService.queryResourceByInheritRoleId(roleId);
		List<RoleRes> resInherit = resources.parallelStream().map(item -> {
			RoleRes roleRes = new RoleRes();
			roleRes.setId(item.getId());
			roleRes.setInherit(true);
			return roleRes;
		}).collect(Collectors.toList());
		roleResList.addAll(resInherit);

		return roleResList;
	}

	public RoleResTree queryAllResTreeByRoleId(Long roleId) {
		List<WoResource> resources = this.resourceService.findAll();

		List<RoleRes> roleRes = this.queryAuthRes(roleId);

		List<AuthRes> authResList = resources.parallelStream().map(res -> {
			AuthRes authRes = new AuthRes();
			authRes.setId(res.getId());
			authRes.setParentId(res.getParentId());
			authRes.setTitle(res.getResourceName());
			authRes.setKey(res.getId().toString());
			roleRes.parallelStream().forEach(role -> {
				if (authRes.getId().equals(role.getId())) {
					authRes.setDisabled(role.isInherit());
				}
			});
			return authRes;
		}).collect(Collectors.toList());

		authResList = TreeUtil.build(authResList, Constants.MENU_ROOT_ID);

		RoleResTree roleResTree = new RoleResTree();
		roleResTree.setRoleRes(roleRes);
		roleResTree.setAuthRes(authResList);

		return roleResTree;
	}

	public void authRole(List<String> resIds, Long roleId, Long curUserId, String uip) {
		List<WoResource> authInherit = this.resourceService.queryResourceByInheritRoleId(roleId);

		List<WoAuthRole> authRoleList = (ObjectUtil.isBlank(authInherit) ?
				resIds.parallelStream().map(item -> {
					Long resId = Long.parseLong(item);

					WoAuthRole authRole = new WoAuthRole();
					authRole.setRoleId(roleId);
					authRole.setResourceId(resId);
					EntityAssists.beforeInsert(authRole, this.nextId(), curUserId, uip, true);
					WoResource resource = this.resourceService.findById(resId);
					authRole.setAppId(resource.getAppId());

					return authRole;
				}) : resIds.parallelStream().map(item -> {
			Long resId = Long.parseLong(item);
			if (authInherit.parallelStream().anyMatch(auth -> resId.equals(auth.getId())))
				return null;

			WoAuthRole authRole = new WoAuthRole();
			authRole.setRoleId(roleId);
			authRole.setResourceId(resId);
			EntityAssists.beforeInsert(authRole, this.nextId(), curUserId, uip, true);
			WoResource resource = this.resourceService.findById(resId);
			authRole.setAppId(resource.getAppId());

			return authRole;
		}).filter(Objects::nonNull)).collect(Collectors.toList());

		this.authRoleRepo.deleteByRoleId(roleId);

		if (!authRoleList.isEmpty())
			this.authRoleRepo.saveAll(authRoleList);
	}

	public WoResource findResById(Long resId) {
		return this.resourceService.findById(resId);
	}

	public List<WoRoleOrg> queryRoleByOrgId(Long orgId) {
		return this.roleOrgRepo.queryRoleByOrgId(orgId);
	}

	public List<WoRole> findAllTenant() {
		return this.entityRepo.findAllTenant(RoleTypeEnum.TAL_ROLE.toString());
	}
}
