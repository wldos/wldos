/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.RoleOrgDao;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoOrgUser;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.enums.RoleTypeEnum;
import com.wldos.platform.core.vo.RoleIdCode;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.common.IDGen;
import io.github.wldos.framework.support.audit.ISystemLogger;
import io.github.wldos.framework.support.audit.SystemEvent;
import io.github.wldos.platform.support.rbac.GrantVisibilityPolicy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import lombok.extern.slf4j.Slf4j;

/**
 * 系统保留身份锚点（四个内置组织 + 两个内置角色）存在性自愈校验。
 *
 * <p>启动后一次性检查 admin/tadmin/dadmin/can_trust 四个组织定义行、admin/admins/can_trust 三个角色定义行是否存在；
 * 缺失则按最小定义自动重建（只重建"类别定义"本身，不重建成员归属与角色授权），
 * 避免因误删导致对应管理身份整体失效、后台无法进入。
 *
 * <p>依赖 wo_org/wo_role 已具备 is_system 列，
 * 未执行该迁移前若恰好触发重建会因缺列报错，属预期（先迁移、后启用）。
 *
 * <p>开关：{@code wldos.platform.identity-anchor.self-heal-enabled=false} 可关闭。
 */
@Slf4j
@Component
public class IdentityAnchorSelfHealBootstrap implements ApplicationRunner {

	private static final List<String> SYSTEM_ORG_CODES = Arrays.asList(
			Constants.AdminOrgCode, Constants.TAdminOrgCode, Constants.DomainAdminOrgCode, Constants.CanTrustOrgCode);

	private static final List<String> SYSTEM_ROLE_CODES = Arrays.asList(
			Constants.SuperAdminRoleCode, Constants.AdminRootRoleCode, Constants.CanTrustOrgCode);

	@Autowired
	private OrgDao orgDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private AuthRoleDao authRoleDao;

	@Autowired
	private RoleOrgDao roleOrgDao;

	@Autowired
	private OrgUserDao orgUserRepo;

	@Autowired
	private ISystemLogger systemLogger;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Value("${wldos.platform.identity-anchor.self-heal-enabled:true}")
	private boolean enabled;

	/**
	 * 应急恢复通道（Break-glass）：仅本机可用，启动时传入即无条件把指定用户写回超级管理员组。
	 * 默认不开启，触发时会写 CRITICAL 系统日志，仅用于「应用层判定全灭、后台已进不去」场景下的最后恢复手段。
	 * 用法：{@code -Dwldos.recovery.grant-admin=<userId>}，恢复后应立即移除该参数并重启。
	 */
	@Value("${wldos.recovery.grant-admin:}")
	private String recoveryGrantAdminUserId;

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			log.info("[identity.self-heal] disabled by config");
		}
		else {
			try {
				this.healOrgs();
				this.healRoles();
				this.purgeIllegalIdentityReplicas();
			}
			catch (Exception e) {
				log.error("[identity.self-heal] 自愈校验异常，请人工排查系统保留身份锚点（admin/tadmin/dadmin/can_trust 组织，admin/admins/can_trust 角色）是否完整: {}", e.getMessage(), e);
			}
		}
		try {
			this.grantEmergencyAdminIfRequested();
		}
		catch (Exception e) {
			log.error("[identity.break-glass] 应急授权执行异常: {}", e.getMessage(), e);
		}
	}

	/**
	 * 检测到 {@code wldos.recovery.grant-admin} 启动参数时，无条件把指定用户写回超级管理员组
	 * （前提：超级管理员组定义已存在，可能刚经过上面的自愈重建）。
	 */
	private void grantEmergencyAdminIfRequested() {
		if (ObjectUtils.isBlank(this.recoveryGrantAdminUserId)) {
			return;
		}
		Long userId;
		try {
			userId = Long.parseLong(this.recoveryGrantAdminUserId.trim());
		}
		catch (NumberFormatException e) {
			log.error("[identity.break-glass] wldos.recovery.grant-admin 参数非法，必须是数字用户ID: {}", this.recoveryGrantAdminUserId);
			return;
		}
		WoOrg adminOrg = this.orgDao.findByOrgCodeAndIsValidAndDeleteFlag(Constants.AdminOrgCode,
				ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (adminOrg == null) {
			log.error("[identity.break-glass] 超级管理员组织定义仍不存在，无法执行应急授权，userId={}", userId);
			return;
		}
		List<WoOrgUser> members = this.orgUserRepo.existsAdmin(Constants.TOP_COM_ID, Constants.AdminOrgCode, adminOrg.getId());
		if (members.stream().anyMatch(u -> userId.equals(u.getUserId()))) {
			log.warn("[identity.break-glass] userId={} 已是超级管理员，无需重复授权", userId);
			return;
		}
		WoOrgUser orgUser = new WoOrgUser();
		orgUser.setId(IDGen.nextId());
		orgUser.setOrgId(adminOrg.getId());
		orgUser.setUserId(userId);
		orgUser.setArchId(adminOrg.getArchId());
		orgUser.setComId(adminOrg.getComId());
		orgUser.setUserComId(Constants.TOP_COM_ID);
		orgUser.setIsValid(ValidStatusEnum.VALID.toString());
		orgUser.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		this.orgUserRepo.saveOrUpdate(orgUser);

		String message = "应急恢复通道：已将 userId=" + userId + " 写入超级管理员组（wldos.recovery.grant-admin 启动参数触发，请事后复核并移除该参数）";
		log.error("[identity.break-glass] {}", message);
		this.systemLogger.recordAsync(SystemEvent.critical("platform.identity", "BREAK_GLASS_GRANT_ADMIN", message)
				.withOperator(userId)
				.withMetadata("{\"userId\":" + userId + ",\"trigger\":\"wldos.recovery.grant-admin\"}"));
	}

	private void healOrgs() {
		List<String> existing = this.orgDao.findExistingSystemOrgCodes(Constants.TOP_COM_ID, SYSTEM_ORG_CODES);
		List<String> missing = SYSTEM_ORG_CODES.stream().filter(code -> !existing.contains(code)).collect(Collectors.toList());
		if (missing.isEmpty()) {
			log.info("[identity.self-heal] 系统组织定义完整：{}", SYSTEM_ORG_CODES);
			return;
		}
		for (String code : missing) {
			WoOrg org = new WoOrg();
			org.setId(IDGen.nextId());
			org.setOrgCode(code);
			org.setOrgName(this.systemOrgName(code));
			org.setOrgType(this.systemOrgType(code));
			org.setArchId(Constants.TOP_COM_ID);
			org.setComId(Constants.TOP_COM_ID);
			org.setParentId(Constants.TOP_COM_ID);
			org.setDisplayOrder(99L);
			org.setIsValid(ValidStatusEnum.VALID.toString());
			org.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			org.markSystemReserved();
			this.orgDao.saveOrUpdate(org);
			log.warn("[identity.self-heal] 系统组织定义缺失，已自动重建：org_code={} id={}（仅重建定义，成员归属需人工核实）", code, org.getId());
		}
	}

	private void purgeIllegalIdentityReplicas() {
		this.transactionTemplate.executeWithoutResult(status -> {
			List<WoRole> replicas = this.roleDao.findNonTemplateIdentityRoles(GrantVisibilityPolicy.identityRoleCodes());
			for (WoRole replica : replicas) {
				if (replica == null || replica.getId() == null) {
					continue;
				}
				this.authRoleDao.deleteAllByRoleId(replica.getId());
				this.roleOrgDao.deleteByRoleId(replica.getId());
				this.roleDao.deleteById(replica.getId());
				String message = "I 类身份角色非法副本已物理清除：role_code=" + replica.getRoleCode()
						+ " id=" + replica.getId() + " com_id=" + replica.getComId() + " domain_id=" + replica.getDomainId();
				log.error("[identity.self-heal] {}", message);
				this.systemLogger.recordAsync(SystemEvent.critical("platform.identity", "IDENTITY_ROLE_REPLICA_PURGE", message));
			}
			for (String roleCode : GrantVisibilityPolicy.identityRoleCodes()) {
				WoRole template = this.roleDao.findPlatformTemplateByRoleCode(roleCode);
				if (template != null && template.getId() != null) {
					this.authRoleDao.deleteDomainOverlaysByRoleId(template.getId());
				}
			}
		});
	}

	private void healRoles() {
		List<RoleIdCode> existing = this.roleDao.findAllByRoleCode(SYSTEM_ROLE_CODES);
		List<String> existingCodes = existing.stream().map(RoleIdCode::getRoleCode).collect(Collectors.toList());
		List<String> missing = SYSTEM_ROLE_CODES.stream().filter(code -> !existingCodes.contains(code)).collect(Collectors.toList());
		if (missing.isEmpty()) {
			log.info("[identity.self-heal] 系统角色定义完整：{}", SYSTEM_ROLE_CODES);
			return;
		}
		// admins（角色树根）需先于 admin 重建，admin 的 parentId 才能正确挂靠到 admins
		Long adminsRoleId = existing.stream()
				.filter(r -> Constants.AdminRootRoleCode.equals(r.getRoleCode()))
				.map(RoleIdCode::getId).findFirst().orElse(null);
		if (missing.contains(Constants.AdminRootRoleCode)) {
			adminsRoleId = IDGen.nextId();
			this.saveSystemRole(Constants.AdminRootRoleCode, "管理员", Constants.TOP_COM_ID, adminsRoleId);
			log.warn("[identity.self-heal] 系统角色定义缺失，已自动重建：role_code={} id={}", Constants.AdminRootRoleCode, adminsRoleId);
		}
		if (missing.contains(Constants.SuperAdminRoleCode)) {
			Long adminRoleId = IDGen.nextId();
			Long parentId = adminsRoleId != null ? adminsRoleId : Constants.TOP_COM_ID;
			this.saveSystemRole(Constants.SuperAdminRoleCode, "超级管理员", parentId, adminRoleId);
			log.warn("[identity.self-heal] 系统角色定义缺失，已自动重建：role_code={} id={}（仅重建定义，授权与成员归属需人工核实）", Constants.SuperAdminRoleCode, adminRoleId);
		}
		// can_trust 角色与 admins/admin 无父子关系，独立顶级角色；仅作 wo_role_org 挂接的权限容器
		if (missing.contains(Constants.CanTrustOrgCode)) {
			Long trustRoleId = IDGen.nextId();
			this.saveSystemRole(Constants.CanTrustOrgCode, "可信会员", Constants.TOP_COM_ID, trustRoleId);
			log.warn("[identity.self-heal] 系统角色定义缺失，已自动重建：role_code={} id={}（仅重建定义，授权与成员归属需人工核实）", Constants.CanTrustOrgCode, trustRoleId);
		}
	}

	private void saveSystemRole(String roleCode, String roleName, Long parentId, Long id) {
		WoRole role = new WoRole();
		role.setId(id);
		role.setRoleCode(roleCode);
		role.setRoleName(roleName);
		role.setRoleType(RoleTypeEnum.SYS_ROLE.toString());
		role.setParentId(parentId);
		role.setDisplayOrder(99L);
		role.setIsValid(ValidStatusEnum.VALID.toString());
		role.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		role.markSystemReserved();
		role.setComId(Constants.TOP_COM_ID);
		this.roleDao.saveOrUpdate(role);
	}

	private String systemOrgName(String code) {
		if (Constants.AdminOrgCode.equals(code)) {
			return "超级管理员";
		}
		if (Constants.TAdminOrgCode.equals(code)) {
			return "租户管理员";
		}
		if (Constants.DomainAdminOrgCode.equals(code)) {
			return "站点运营管理员";
		}
		if (Constants.CanTrustOrgCode.equals(code)) {
			return "可信会员";
		}
		return code;
	}

	private String systemOrgType(String code) {
		if (Constants.AdminOrgCode.equals(code)) {
			return "platform";
		}
		return "role_org";
	}
}
