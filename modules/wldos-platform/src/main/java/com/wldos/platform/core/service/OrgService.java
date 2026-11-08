/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.wldos.framework.mvc.service.EntityService;
import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.BoolEnum;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.RedisKeyEnum;
import com.wldos.platform.core.enums.UserStatusEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.utils.TreeUtils;
import com.wldos.platform.core.dao.*;
import com.wldos.platform.core.dao.ComUserDao;
import com.wldos.platform.core.entity.*;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.vo.AuthRes;
import com.wldos.platform.core.vo.Org;
import com.wldos.platform.core.vo.OrgRole;
import com.wldos.platform.core.vo.OrgRoleTree;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织管理service。
 *
 * @author Yuanxi Universe
 * @date 2021/4/28
 * @version 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class OrgService extends EntityService<OrgDao, WoOrg, Long> {

	private final RoleService roleService;

	private final RoleOrgDao roleOrgRepo;

	private final OrgUserDao orgUserRepo;

	private final ComUserDao comUserRepo;

	private final UserService userService;

	private final AdminScopeService adminScopeService;

	private final DomainService domainService;

	@Autowired
	@Lazy
	private ArchitectureDao architectureDao;

	@Autowired
	public OrgService(RoleService roleService, RoleOrgDao roleOrgRepo, OrgUserDao orgUserRepo,
			ComUserDao comUserRepo, @Lazy UserService userService,
			@Lazy AdminScopeService adminScopeService, @Lazy DomainService domainService) {
		this.roleService = roleService;
		this.roleOrgRepo = roleOrgRepo;
		this.orgUserRepo = orgUserRepo;
		this.comUserRepo = comUserRepo;
		this.userService = userService;
		this.adminScopeService = adminScopeService;
		this.domainService = domainService;
	}

	/** 身份链对称清理专用：按（组织 + 人）直删成员，不触发保底守卫（调用方已按绑定关系判定应移除）。 */
	public void removeMemberByOrg(Long userId, Long orgId) {
		if (userId == null || orgId == null) {
			return;
		}
		this.orgUserRepo.removeOrgStaff(Collections.singletonList(userId), orgId);
	}

	/**
	 * 组织的默认体系：归属租户的唯一/首个体系；平台上下文（comId 空或 0）落平台哨兵 {@link Constants#TOP_ARCH_ID}。
	 * 表单隐藏「归属体系」后由服务端按本方法填充（R35）。
	 */
	public Long resolveDefaultArchId(Long comId) {
		if (comId == null || comId <= 0) {
			return Constants.TOP_ARCH_ID;
		}
		WoArchitecture arch = this.architectureDao.findFirstByComIdAndDeleteFlagAndIsValid(comId);
		return arch != null && arch.getId() != null ? arch.getId() : Constants.TOP_ARCH_ID;
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
		return this.queryAllRoleTreeByOrgId(orgId, userId, null);
	}

	/**
	 * 组织可授权角色树：同编码多站实例时按当前站点只展示一条。
	 *
	 * @param requestDomainId 前端目标站点（架构视图工具栏等）；优先于单站自动解析
	 */
	public OrgRoleTree queryAllRoleTreeByOrgId(long orgId, long userId, Long requestDomainId) {
		WoOrg org = this.findById(orgId);
		Long comId = org != null && org.getComId() != null ? org.getComId() : this.getTenantId();
		Long domainId = this.resolveOrgAuthDomainId(org, comId, requestDomainId);
		List<WoRole> roles = this.roleService.findOrgAuthCandidates(comId, domainId);
		if (!this.isAdmin(userId)) {
			roles.removeIf(r -> !this.adminScopeService.canAssignRole(userId, r));
		}
		List<OrgRole> orgRoles = this.queryAuthRole(orgId);
		List<AuthRes> authResList = roles.parallelStream().map(role ->
				AuthRes.of(role.getRoleName(), role.getId().toString(), role.getId(), role.getParentId(), role.getDisplayOrder())
		).collect(Collectors.toList());

		authResList = TreeUtils.build(authResList, Constants.TOP_ROLE_ID);

		OrgRoleTree orgRoleTree = new OrgRoleTree();
		orgRoleTree.setOrgRole(orgRoles);
		orgRoleTree.setAuthRes(authResList);

		return orgRoleTree;
	}

	private Long resolveOrgAuthDomainId(WoOrg org, Long comId, Long requestDomainId) {
		if (org != null && org.getDomainId() != null && org.getDomainId() > 0) {
			return org.getDomainId();
		}
		if (requestDomainId != null && requestDomainId > 0) {
			return requestDomainId;
		}
		if (comId != null && comId > 0 && this.domainService.countValidByComId(comId) == 1) {
			WoDomain domain = this.domainService.findFirstByComId(comId);
			if (domain != null && domain.getId() != null && domain.getId() > 0) {
				return domain.getId();
			}
		}
		return null;
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
		WoOrg orgRow = this.findById(orgId);
		final Long memberComId = this.resolveMemberComIdFromOrg(orgRow, comId);
		final Long memberArchId = orgRow != null && orgRow.getArchId() != null ? orgRow.getArchId() : archId;
		List<Long> ids = userIds.parallelStream().map(Long::parseLong).collect(Collectors.toList());
		List<WoOrgUser> orgUsers = this.orgUserRepo.queryAllByUserIds(orgId, memberArchId, memberComId, ids,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
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
			orgUser.setArchId(memberArchId);
			orgUser.setComId(memberComId);
			orgUser.setUserComId(memberComId); // 默认添加成员的租户即当前租户，只有在超级管理员设置租户管理员或者其他类似身份时以及借调场景，用户归属公司不同于组织公司，涉及场景自行设置

			return orgUser;
		}).filter(Objects::nonNull).collect(Collectors.toList());

		// 验证用户是否关联公司，不存在则创建主企业，存在但不存在当前企业则创建关联，存在当前企业则略过
		List<WoComUser> comUsers = this.comUserRepo.queryAllByUserIds(ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, List<WoComUser>> userComList = comUsers.parallelStream().collect(Collectors.groupingBy(WoComUser::getUserId));
		List<WoComUser> woComUsers = new ArrayList<>();
		List<WoComUser> mainComSync = new ArrayList<>();
		for (Long userId : ids) {
			boolean isCurCom = true;
			if (userComList.containsKey(userId)) {
				if (userComList.get(userId).stream().allMatch(com -> Objects.equals(memberComId, com.getComId()))) {
					continue;
				}
				isCurCom = false;
			}
			WoComUser comUser = new WoComUser();
			comUser.setComId(memberComId);
			comUser.setUserId(userId);
			comUser.setIsMain(isCurCom ? BoolEnum.YES.toString() : BoolEnum.NO.toString());
			woComUsers.add(comUser);
			if (isCurCom) {
				mainComSync.add(comUser);
			}
		}

		if (!orgUserList.isEmpty())
			this.orgUserRepo.saveAll(orgUserList);
		if (!woComUsers.isEmpty())
			this.comUserRepo.saveAll(woComUsers);

		for (WoComUser cu : mainComSync) {
			this.userService.syncUserMainComId(cu.getUserId(), cu.getComId(), curUserId, uip);
		}

		if (orgRow != null && !orgUserList.isEmpty() && !this.isSystemIdentityOrg(orgRow)) {
			Long siteDomainId = orgRow.getDomainId();
			for (WoOrgUser added : orgUserList) {
				Long memberUserId = added.getUserId();
				WoUser member = this.userService.findById(memberUserId);
				if (member == null) {
					continue;
				}
				if (siteDomainId != null && siteDomainId > 0
						&& (member.getDomainId() == null || member.getDomainId() <= 0)) {
					member.setDomainId(siteDomainId);
					this.userService.saveOrUpdate(member);
				}
				if (UserStatusEnum.normal.getValue().equals(member.getStatus())) {
					this.userService.promoteToMemberOrg(memberUserId, siteDomainId);
				}
			}
		}

		WoOrg org = this.findById(orgId);
		this.clearAdminGroupCache(org != null ? org.getOrgCode() : null);

		return mes.toString();
	}

	private Long resolveMemberComIdFromOrg(WoOrg orgRow, Long requestComId) {
		if (requestComId != null && requestComId > 0 && !Objects.equals(requestComId, Constants.TOP_COM_ID)) {
			return requestComId;
		}
		if (orgRow == null) {
			return requestComId;
		}
		Long orgComId = orgRow.getComId();
		if (orgComId != null && orgComId > 0 && !Objects.equals(orgComId, Constants.TOP_COM_ID)) {
			return orgComId;
		}
		Long domainId = orgRow.getDomainId();
		if (domainId != null && domainId > 0) {
			WoDomain domain = this.domainService.findById(domainId);
			if (domain != null && domain.getComId() != null && domain.getComId() > 0
					&& !Objects.equals(domain.getComId(), Constants.TOP_COM_ID)) {
				return domain.getComId();
			}
		}
		return orgComId;
	}

	/**
	 * 系统身份组加人：{@code wo_org_user} 的 {@code com_id}/{@code arch_id} 与组织及 {@code wo_role_org} 对齐，
	 * {@code user_com_id} 与 {@code wo_com_user} 使用受托租户 {@code userComId}（对齐 tadmin 挂接方式）。
	 */
	public String userOrgWithUserComId(List<String> userIds, Long orgId, Long userComId, Long curUserId, String uip) {
		WoOrg org = this.findById(orgId);
		if (org == null || ObjectUtils.isBlank(userIds)) {
			return "";
		}
		Long orgComId = org.getComId() != null ? org.getComId() : Constants.TOP_COM_ID;
		Long orgArchId = org.getArchId() != null ? org.getArchId() : Constants.TOP_COM_ID;
		Long effectiveUserComId = userComId != null ? userComId : orgComId;
		List<Long> ids = userIds.parallelStream().map(Long::parseLong).collect(Collectors.toList());
		StringBuilder mes = new StringBuilder(50);
		List<WoOrgUser> orgUserList = new ArrayList<>();
		for (Long userId : ids) {
			WoOrgUser existing = this.orgUserRepo.queryByUserIdAndOrgId(orgId, userId,
					ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
			if (existing != null) {
				boolean changed = false;
				if (!Objects.equals(existing.getComId(), orgComId)) {
					existing.setComId(orgComId);
					changed = true;
				}
				if (!Objects.equals(existing.getArchId(), orgArchId)) {
					existing.setArchId(orgArchId);
					changed = true;
				}
				if (!Objects.equals(existing.getUserComId(), effectiveUserComId)) {
					existing.setUserComId(effectiveUserComId);
					changed = true;
				}
				if (changed) {
					this.orgUserRepo.saveOrUpdate(existing);
				}
				continue;
			}
			WoOrgUser orgUser = new WoOrgUser();
			orgUser.setOrgId(orgId);
			orgUser.setUserId(userId);
			orgUser.setArchId(orgArchId);
			orgUser.setComId(orgComId);
			orgUser.setUserComId(effectiveUserComId);
			orgUserList.add(orgUser);
		}

		List<WoComUser> comUsers = this.comUserRepo.queryAllByUserIds(ids, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		Map<Long, List<WoComUser>> userComList = comUsers.parallelStream().collect(Collectors.groupingBy(WoComUser::getUserId));
		List<WoComUser> woComUsers = new ArrayList<>();
		List<WoComUser> mainComSync = new ArrayList<>();
		for (Long userId : ids) {
			boolean isCurCom = true;
			if (userComList.containsKey(userId)) {
				if (userComList.get(userId).stream().anyMatch(com -> Objects.equals(effectiveUserComId, com.getComId()))) {
					continue;
				}
				isCurCom = false;
			}
			WoComUser comUser = new WoComUser();
			comUser.setComId(effectiveUserComId);
			comUser.setUserId(userId);
			comUser.setIsMain(isCurCom ? BoolEnum.YES.toString() : BoolEnum.NO.toString());
			woComUsers.add(comUser);
			if (isCurCom) {
				mainComSync.add(comUser);
			}
		}

		if (!orgUserList.isEmpty()) {
			this.orgUserRepo.saveAll(orgUserList);
		}
		if (!woComUsers.isEmpty()) {
			this.comUserRepo.saveAll(woComUsers);
		}
		for (WoComUser cu : mainComSync) {
			this.userService.syncUserMainComId(cu.getUserId(), cu.getComId(), curUserId, uip);
		}
		this.clearAdminGroupCache(org.getOrgCode());
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

	/** 「至少保留一名成员」的系统保留身份组：仅平台超管组，防止系统失管；其余身份组（tadmin/dadmin/can_trust）可为空 */
	private static final List<String> MIN_MEMBER_GUARDED_ORG_CODES = java.util.Collections.singletonList(
			Constants.AdminOrgCode);

	/**
	 * 删除指定组织下的一组人。仅当目标组织是平台超管组（admin）时强制保留至少一名成员，防止系统失管；
	 * 租户管理员/站点管理员/可信用户组允许清空（超管可随时重新指派）。
	 *
	 * @param ids 人员id
	 * @param orgId 组织id
	 */
	public void removeOrgStaff(List<Long> ids, Long orgId) {
		WoOrg org = this.findById(orgId);
		String orgCode = org != null ? org.getOrgCode() : null;
		boolean isGuardedGroup = org != null && org.getComId() != null && org.getComId() == Constants.TOP_COM_ID
				&& MIN_MEMBER_GUARDED_ORG_CODES.contains(orgCode);
		if (isGuardedGroup) {
			List<WoOrgUser> members = this.orgUserRepo.existsAdmin(Constants.TOP_COM_ID, orgCode, orgId);
			long remaining = members.stream().map(WoOrgUser::getUserId).filter(uid -> !ids.contains(uid)).count();
			if (remaining < 1) {
				getLog().error("必须保留至少一名「{}」成员，拒绝清空该身份组，orgId: {}", org.getOrgName(), orgId);
				throw new io.github.wldos.common.exception.BaseException("必须保留至少一名「" + org.getOrgName() + "」成员，不能全部移除");
			}
		}
		this.orgUserRepo.removeOrgStaff(ids, orgId);
		this.clearAdminGroupCache(orgCode);
	}

	private boolean isSystemIdentityOrg(WoOrg org) {
		if (org == null || org.getOrgCode() == null) {
			return false;
		}
		String code = org.getOrgCode();
		return Constants.AdminOrgCode.equals(code)
				|| Constants.TAdminOrgCode.equals(code)
				|| Constants.DomainAdminOrgCode.equals(code)
				|| Constants.CanTrustOrgCode.equals(code);
	}

	private void clearAdminGroupCache(String orgCode) {
		if (Constants.AdminOrgCode.equals(orgCode)) {
			this.cache.remove(RedisKeyEnum.WLDOS_ADMIN.toString());
		}
		else if (Constants.TAdminOrgCode.equals(orgCode)) {
			this.cache.remove(RedisKeyEnum.WLDOS_TENANT_ADMIN.toString());
		}
		else if (Constants.CanTrustOrgCode.equals(orgCode)) {
			this.cache.remove(RedisKeyEnum.WLDOS_TRUST.toString());
		}
	}
}
