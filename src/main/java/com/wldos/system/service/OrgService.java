/*
 * Copyright (c) 2020 - 2021. zhiletu.com and/or its affiliates. All rights reserved.
 * zhiletu.com PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * http://www.zhiletu.com
 */

package com.wldos.system.service;

import java.util.List;
import java.util.stream.Collectors;

import com.wldos.support.controller.EntityTools;
import com.wldos.support.service.BaseService;
import com.wldos.support.util.constant.PubConstants;
import com.wldos.support.util.TreeUtil;
import com.wldos.system.entity.WoOrg;
import com.wldos.system.entity.WoOrgUser;
import com.wldos.system.entity.WoRole;
import com.wldos.system.entity.WoRoleOrg;
import com.wldos.system.repo.OrgRepo;
import com.wldos.system.repo.OrgUserRepo;
import com.wldos.system.repo.RoleOrgRepo;
import com.wldos.system.vo.AuthRes;
import com.wldos.system.vo.OrgRole;
import com.wldos.system.vo.OrgRoleTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织管理service。
 *
 * @Title OrgService
 * @Package com.wldos.system.service
 * @Project wldos
 * @Author 树悉猿、wldos
 * @Date 2021/4/28
 * @Version 1.0
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
	 * @param orgId
	 * @return
	 */
	public List<OrgRole> queryAuthRole(Long orgId) {
		List<WoRoleOrg> roleOrgs = this.roleService.queryRoleByOrgId(orgId);

		List<OrgRole> resSelf = roleOrgs.parallelStream().map(item -> {
			OrgRole roleRes = new OrgRole();
			roleRes.setId(item.getRoleId());
			return roleRes;
		}).collect(Collectors.toList());

		return resSelf;
	}

	/**
	 * 查询组织绑定的系统角色和可绑定的所有角色树
	 *
	 * @param orgId
	 * @param userId 登陆用户id
	 * @return
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
			Long resId = Long.parseLong(item);

			WoRoleOrg roleOrg = new WoRoleOrg();
			roleOrg.setOrgId(orgId);
			roleOrg.setRoleId(resId);
			EntityTools.setInsertInfo(roleOrg, this.nextId(), curUserId, uip, true);
			WoRole role = this.roleService.findById(resId);
			roleOrg.setArchId(archId);
			roleOrg.setComId(comId);

			return roleOrg;
		}).collect(Collectors.toList());

		// 2.delete from wo_role_org where roleId = ?
		this.roleOrgRepo.deleteByOrgIdAndArchIdAndComId(orgId, archId, comId);

		if (roleOrgList != null && !roleOrgList.isEmpty())
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
		StringBuffer mes = new StringBuffer(50);
		List<WoOrgUser> orgUserList = userIds.parallelStream().map(item -> {
			Long userId = Long.parseLong(item);

			boolean exists = this.orgUserRepo.existsById(userId);
			if (exists) {
				mes.append("用户："+userId+" 已存在; ");
				return null;
			}

			WoOrgUser orgUser = new WoOrgUser();
			orgUser.setOrgId(orgId);
			orgUser.setUserId(userId);
			EntityTools.setInsertInfo(orgUser, this.nextId(), curUserId, uip, true);
			WoOrg org = this.findById(orgId);
			orgUser.setArchId(archId);
			orgUser.setComId(comId);

			return orgUser;
		}).filter(u -> u != null).collect(Collectors.toList());

		if (orgUserList != null && !orgUserList.isEmpty())
			this.orgUserRepo.saveAll(orgUserList);

		return mes.toString();
	}
}
