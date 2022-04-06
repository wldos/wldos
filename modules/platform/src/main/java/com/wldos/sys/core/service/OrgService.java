/*
 * Copyright (c) 2020 - 2022 wldos.com. All rights reserved.
 * Licensed under the AGPL or a commercial license.
 * For AGPL see License in the project root for license information.
 * For commercial licenses see term.md or https://www.wldos.com
 *
 */

package com.wldos.sys.core.service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.base.entity.EntityAssists;
import com.wldos.base.service.BaseService;
import com.wldos.common.Constants;
import com.wldos.common.enums.BoolEnum;
import com.wldos.common.enums.DeleteFlagEnum;
import com.wldos.common.enums.ValidStatusEnum;
import com.wldos.common.utils.TreeUtils;
import com.wldos.sys.base.entity.WoComUser;
import com.wldos.sys.base.service.RoleService;
import com.wldos.sys.core.entity.WoOrg;
import com.wldos.sys.core.entity.WoOrgUser;
import com.wldos.sys.base.entity.WoRole;
import com.wldos.sys.base.entity.WoRoleOrg;
import com.wldos.sys.base.repo.RoleOrgRepo;
import com.wldos.sys.base.repo.ComUserRepo;
import com.wldos.sys.core.repo.OrgRepo;
import com.wldos.sys.core.repo.OrgUserRepo;
import com.wldos.sys.base.vo.AuthRes;
import com.wldos.sys.core.vo.Org;
import com.wldos.sys.core.vo.OrgRole;
import com.wldos.sys.core.vo.OrgRoleTree;
import com.wldos.common.enums.RedisKeyEnum;

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

		authResList = TreeUtils.build(authResList, Constants.TOP_ROLE_ID);

		OrgRoleTree orgRoleTree = new OrgRoleTree();
		orgRoleTree.setOrgRole(orgRoles);
		orgRoleTree.setAuthRes(authResList);

		return orgRoleTree;
	}

	/**
	 * 给组织关联系统角色，对于实组织，关联角色仅针对直属人员生效，对于虚组织(角色组)，虚组织不在组织人员树中展示。
	 *
	 * @param roleIds 角色id
	 * @param orgId 组织id
	 * @param archId 体系id
	 * @param comId 公司id
	 * @param curUserId 用户id
	 * @param uip 用户ip
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
	 * @param userIds 用户id
	 * @param orgId 组织id
	 * @param archId 体系id
	 * @param comId 公司id
	 * @param curUserId 当前用户id
	 * @param uip 用户ip
	 */
	public String userOrg(List<String> userIds, Long orgId, Long archId, Long comId, Long curUserId, String uip) {
		// @todo 人员的添加应该经过本人同意，本人同意后执行关联操作，如果是首次添加设为主企业(不存在主企业)。
		// @todo 同时应该实现人员加入组织的功能，申请后需要租户管理员的同意，如果是首次添加设为主企业(不存在主企业)。
		// @todo 人员可以选择退出组织或退出企业，退出主企业时默认最后一个企业为主企业，租户管理员可以设置是否允许人员自行退出。
		// @todo 本操作应该加入消息队列支持。
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
			orgUser.setUserComId(comId); // 默认添加成员的租户即当前租户，只有在超级管理员设置租户管理员或者其他类似身份时以及借调场景，用户归属公司不同于组织公司，涉及场景自行设置

			return orgUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		// 验证用户是否关联公司，不存在则创建主企业，存在但不存在当前企业则创建关联，存在当前企业则略过
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

		this.cache.remove(RedisKeyEnum.WLDOS_ADMIN.toString());

		return mes.toString();
	}

	/**
	 * 根据组织类型查询有效组织列表
	 *
	 * @param orgType 组织类型
	 * @return 组织列表
	 */
	public List<Org> queryPlatformRoleOrg(String orgType) {
		return this.entityRepo.findAllByOrgType(DeleteFlagEnum.NORMAL.toString(), ValidStatusEnum.VALID.toString(), orgType);
	}

	/**
	 * 删除指定组织下的一组人
	 *
	 * @param ids 人员id
	 * @param orgId 组织id
	 */
	public void removeOrgStaff(List<Long> ids, Long orgId) {
		// 返回空，说明不是超级管理组，直接删除; 返回非空，是超级管理组，必须保留一个超级管理员
		List<WoOrgUser> adminUsers = this.orgUserRepo.existsAdmin(Constants.TOP_COM_ID, Constants.AdminOrgCode, orgId);
		if (!adminUsers.isEmpty() && adminUsers.size() - ids.size() >= 1) {
			getLog().error("必须保留一个超级管理员，删除失败管理用户组id: {}", orgId);
			return;
		}
		this.orgUserRepo.removeOrgStaff(ids, orgId);
		this.cache.remove(RedisKeyEnum.WLDOS_ADMIN.toString());
	}
}
