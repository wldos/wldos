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

import com.wldos.support.controller.EntityAssists;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.system.core.repo.RoleOrgRepo;
import com.wldos.system.enums.RoleTypeEnum;
import com.wldos.system.vo.RoleRes;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.core.entity.WoAuthRole;
import com.wldos.system.core.entity.WoResource;
import com.wldos.system.core.entity.WoRole;
import com.wldos.system.core.entity.WoRoleOrg;
import com.wldos.system.core.repo.AuthRoleRepo;
import com.wldos.system.core.repo.RoleRepo;
import com.wldos.system.vo.AuthRes;
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

	/**
	 * 查询角色授予的权限
	 *
	 * @param roleId
	 * @return
	 */
	public List<RoleRes> queryAuthRes(Long roleId) {
		List<WoResource> resources = this.resourceService.queryResourceByRoleId(roleId);

		List<RoleRes> roleResList = new ArrayList<>();
		List<RoleRes> resSelf = resources.parallelStream().map(item -> {
			RoleRes roleRes = new RoleRes();
			roleRes.setId(item.getId());
			roleRes.setInherit(false);
			return roleRes;
		}).collect(Collectors.toList());

		roleResList.addAll(resSelf);

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

	/**
	 * 按角色组装授权资源树，对已授权资源分别打标：自有、继承(不能编辑),同时返回已授权资源。
	 *
	 * @param roleId
	 * @return
	 */
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
					authRes.setDisabled(role.isInherit()); // @todo 暂不实行颜色标记，先保证继承资源不可编辑
				}
			});
			return authRes;
		}).collect(Collectors.toList());

		authResList = TreeUtil.build(authResList, PubConstants.MENU_ROOT_ID);

		RoleResTree roleResTree = new RoleResTree();
		roleResTree.setRoleRes(roleRes);
		roleResTree.setAuthRes(authResList);

		return roleResTree;
	}

	/**
	 * 给角色授权：排除继承权限、删除自有权限、新增授权
	 *
	 * @param resIds 新权限
	 * @param roleId 角色
	 * @param curUserId
	 * @param uip
	 */
	public void authRole(List<String> resIds, Long roleId, Long curUserId, String uip) {
		List<WoResource> authInherit = this.resourceService.queryResourceByInheritRoleId(roleId);
		// 由于角色配置主要集中在系统初始化阶段，并发不会很大，故此处统一处理
		List<WoAuthRole> authRoleList = resIds.parallelStream().map(item -> {
			Long resId = Long.parseLong(item);
			if (authInherit != null && authInherit.parallelStream().anyMatch(auth ->
					resId.equals(auth.getId()))) // 1.过滤掉继承资源
				return null;

			WoAuthRole authRole = new WoAuthRole();
			authRole.setRoleId(roleId);
			authRole.setResourceId(resId);
			EntityAssists.beforeInsert(authRole, this.nextId(), curUserId, uip, true);
			WoResource resource = this.resourceService.findById(resId);
			authRole.setAppId(resource.getAppId());

			return authRole;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		// 2.delete from wo_auth_role where roleId = ?
		this.authRoleRepo.deleteByRoleId(roleId);

		if (!authRoleList.isEmpty())
			this.authRoleRepo.saveAll(authRoleList);
	}

	/**
	 * 查询资源属性
	 *
	 * @param resId
	 * @return
	 */
	public WoResource findResById(Long resId) {
		return this.resourceService.findById(resId);
	}

	public List<WoRoleOrg> queryRoleByOrgId(Long orgId) {
		return this.roleOrgRepo.queryRoleByOrgId(orgId);
	}

	/**
	 * 查询租户角色（租户可指派的业务角色，来自系统应用创建）
	 *
	 * @return 租户角色列表
	 */
	public List<WoRole> findAllTenant() {
		return this.entityRepo.findAllTenant(RoleTypeEnum.TAL_ROLE.toString());
	}
}
