/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.RoleBindingDao;
import com.wldos.platform.core.dao.RoleOrgDao;
import com.wldos.platform.core.dao.SubjectAssociationDao;
import com.wldos.platform.core.entity.WoRoleBinding;
import com.wldos.platform.core.entity.WoRoleOrg;
import com.wldos.platform.core.entity.WoSubjectAssociation;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.framework.common.IDGen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 多态绑定存量迁移：启动时把 wo_role_org（组织成员）、wo_subject_association（主体授权）、
 * 身份组上的 wo_role_org 幂等迁移到 wo_role_binding。
 * 只读旧表写新表，旧表不动；幂等键为 (role_id, target_type, target_id, com_id, arch_id)，已存在则跳过。
 *
 * <p>开关：{@code wldos.rbac.binding-channel.migration-enabled=false} 可关闭。</p>
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
public class RoleBindingMigrationBootstrap implements ApplicationRunner {

	/** 身份组 org_code 集：其上的 wo_role_org 行迁移为 identity 通道绑定 */
	private static final List<String> IDENTITY_ORG_CODES = Arrays.asList(
			Constants.AdminOrgCode, Constants.TAdminOrgCode, Constants.DomainAdminOrgCode, Constants.CanTrustOrgCode);

	@Autowired
	private RoleOrgDao roleOrgDao;

	@Autowired
	private SubjectAssociationDao subjectAssociationDao;

	@Autowired
	private RoleBindingDao roleBindingDao;

	@Autowired
	private OrgDao orgDao;

	@Value("${wldos.rbac.binding-channel.migration-enabled:true}")
	private boolean enabled;

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			log.info("[role.binding] 迁移已关闭（wldos.rbac.binding-channel.migration-enabled=false）");
			return;
		}
		try {
			int[] counts = this.migrate();
			if (counts[0] + counts[1] > 0) {
				log.warn("[role.binding] 绑定迁移完成：org/identity 新增 {} 行，subject_type 新增 {} 行", counts[0], counts[1]);
			}
			else {
				log.info("[role.binding] 旧链路与 wo_role_binding 一致，无需迁移");
			}
		}
		catch (Exception e) {
			log.error("[role.binding] 绑定迁移异常（不影响启动，授权仍走组织/主体旧链路）: {}", e.getMessage(), e);
		}
	}

	/**
	 * 执行幂等迁移（诊断中心一键修复复用）。
	 *
	 * @return [org/identity 通道新增行数, subject_type 通道新增行数]
	 */
	public int[] migrate() {
		Set<Long> identityOrgIds = new HashSet<>(this.orgDao.findIdsByOrgCodes(IDENTITY_ORG_CODES));
		return new int[] { this.migrateRoleOrg(identityOrgIds), this.migrateSubjectAssociation() };
	}

	/** wo_role_org → binding：身份组行落 identity 通道，其余落 org 通道 */
	private int migrateRoleOrg(Set<Long> identityOrgIds) {
		int added = 0;
		for (WoRoleOrg row : this.roleOrgDao.findAllValid()) {
			if (row.getRoleId() == null || row.getOrgId() == null) {
				continue;
			}
			boolean identity = identityOrgIds.contains(row.getOrgId());
			String targetType = identity ? WoRoleBinding.TARGET_IDENTITY : WoRoleBinding.TARGET_ORG;
			if (this.roleBindingDao.findValidOne(row.getRoleId(), targetType, row.getOrgId()) != null) {
				continue;
			}
			WoRoleBinding binding = this.newBinding(row.getRoleId(), targetType, row.getOrgId(), row.getComId());
			binding.setArchId(identity ? 0L : (row.getArchId() == null ? 0L : row.getArchId()));
			this.roleBindingDao.save(binding);
			added++;
		}
		return added;
	}

	/** wo_subject_association → binding(subject_type) */
	private int migrateSubjectAssociation() {
		int added = 0;
		for (WoSubjectAssociation row : this.subjectAssociationDao.findAllValid()) {
			if (row.getRoleId() == null || row.getSubjectTypeId() == null) {
				continue;
			}
			if (this.roleBindingDao.findValidOne(row.getRoleId(), WoRoleBinding.TARGET_SUBJECT_TYPE, row.getSubjectTypeId()) != null) {
				continue;
			}
			WoRoleBinding binding = this.newBinding(row.getRoleId(), WoRoleBinding.TARGET_SUBJECT_TYPE, row.getSubjectTypeId(), row.getComId());
			binding.setDomainId(row.getDomainId());
			this.roleBindingDao.save(binding);
			added++;
		}
		return added;
	}

	private WoRoleBinding newBinding(Long roleId, String targetType, Long targetId, Long comId) {
		WoRoleBinding binding = new WoRoleBinding();
		binding.setId(IDGen.nextId());
		binding.setRoleId(roleId);
		binding.setTargetType(targetType);
		binding.setTargetId(targetId);
		binding.setComId(comId == null ? 0L : comId);
		binding.setArchId(0L);
		binding.setIsValid(ValidStatusEnum.VALID.toString());
		binding.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		binding.setCreateBy(Constants.SYSTEM_USER_ID);
		return binding;
	}
}
