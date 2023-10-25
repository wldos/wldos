/*
 * Copyright (c) 2020 - 2023 wldos.com. All rights reserved.
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 */

package com.wldos.sys.base.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.framework.service.RepoService;
import com.wldos.common.Constants;
import com.wldos.common.utils.ObjectUtils;
import com.wldos.common.utils.TreeUtils;
import com.wldos.support.resource.entity.WoResource;
import com.wldos.sys.base.entity.WoAuthRole;
import com.wldos.sys.base.entity.WoRole;
import com.wldos.sys.base.entity.WoRoleOrg;
import com.wldos.sys.base.enums.RoleTypeEnum;
import com.wldos.sys.base.repo.AuthRoleRepo;
import com.wldos.sys.base.repo.RoleOrgRepo;
import com.wldos.sys.base.repo.RoleRepo;
import com.wldos.sys.base.vo.AuthRes;
import com.wldos.sys.base.vo.RoleRes;
import com.wldos.sys.base.vo.RoleResTree;

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
public class RoleService extends RepoService<RoleRepo, WoRole, Long> {
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
	 * @param roleId 角色id
	 * @return 权限列表
	 */
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

	/**
	 * 按角色组装授权资源树，对已授权资源分别打标：自有、继承(不能编辑),同时返回已授权资源。
	 *
	 * @param roleId 角色id
	 * @return 权限树
	 */
	public RoleResTree queryAllResTreeByRoleId(Long roleId) {
		List<WoResource> resources = this.resourceService.findAll();

		List<RoleRes> roleRes = this.queryAuthRes(roleId);

		List<AuthRes> authResList = resources.parallelStream().map(res -> {
			AuthRes authRes =
					AuthRes.of(res.getResourceName(), res.getId().toString(), res.getId(), res.getParentId());
			roleRes.parallelStream().forEach(role -> {
				if (authRes.getId().equals(role.getId())) {
					authRes.setDisabled(role.isInherit()); // @todo 暂不实行颜色标记，先保证继承资源不可编辑
				}
			});
			return authRes;
		}).collect(Collectors.toList());

		authResList = TreeUtils.build(authResList, Constants.MENU_ROOT_ID);

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
	 * @param curUserId 用户id
	 * @param uip 用户ip
	 */
	public void authRole(List<String> resIds, Long roleId, Long curUserId, String uip) {
		List<WoResource> authInherit = this.resourceService.queryResourceByInheritRoleId(roleId);
		// 由于角色配置主要集中在系统初始化阶段，并发不会很大，故此处统一处理
		List<WoAuthRole> authRoleList = (ObjectUtils.isBlank(authInherit) ?
				resIds.parallelStream().map(item -> {
					Long resId = Long.parseLong(item);

					WoAuthRole authRole = new WoAuthRole();
					authRole.setRoleId(roleId);
					authRole.setResourceId(resId);
					WoResource resource = this.resourceService.findById(resId);
					authRole.setAppId(resource.getAppId());

					return authRole;
				}) : resIds.parallelStream().map(item -> {
			Long resId = Long.parseLong(item);
			if (authInherit.parallelStream().anyMatch(auth -> resId.equals(auth.getId()))) // 1.过滤掉继承资源
				return null;

			WoAuthRole authRole = new WoAuthRole();
			authRole.setRoleId(roleId);
			authRole.setResourceId(resId);
			WoResource resource = this.resourceService.findById(resId);
			authRole.setAppId(resource.getAppId());

			return authRole;
		}).filter(Objects::nonNull)).collect(Collectors.toList());

		// 2.delete from wo_auth_role where roleId = ? 优化方向：1.库里有、参数没有的delete，2.库里和参数都有的，从参数中过滤掉，3.最后剩下的是需要新增的
		this.authRoleRepo.deleteByRoleId(roleId);

		if (!authRoleList.isEmpty())
			this.authRoleRepo.saveAll(authRoleList);
	}

	/**
	 * 查询资源属性
	 *
	 * @param resId 角色id
	 * @return 资源
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
