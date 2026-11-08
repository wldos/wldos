/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.bootstrap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.wldos.platform.core.dao.ArchitectureDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.OrgUserDao;
import com.wldos.platform.core.dao.RoleBindingDao;
import com.wldos.platform.core.dao.SlotBindingDao;
import com.wldos.platform.core.entity.WoArchitecture;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoOrgUser;
import com.wldos.platform.core.entity.WoRoleBinding;
import com.wldos.platform.core.entity.WoSlotBinding;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.utils.DateUtils;
import io.github.wldos.framework.common.IDGen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 启动时 RBAC 存量数据审计与可选修复。
 * 默认关闭；dry-run 只打日志，apply=true 时按规则软删或回填错误数据。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Component
@Order(50)
public class RbacEndStateMigrationRunner implements ApplicationRunner {

	private static final List<String> MEMBER_KIND_ORG_CODES = Arrays.asList("user", "freeuser", "vip", "un_active");

	private static final List<String> IDENTITY_ORG_CODES = Arrays.asList(
			Constants.AdminOrgCode,
			Constants.TAdminOrgCode,
			Constants.DomainAdminOrgCode,
			Constants.CanTrustOrgCode);

	private static final List<String> EXCLUDED_STAFF_ORG_CODES;

	static {
		List<String> codes = new ArrayList<>(MEMBER_KIND_ORG_CODES);
		codes.addAll(IDENTITY_ORG_CODES);
		EXCLUDED_STAFF_ORG_CODES = java.util.Collections.unmodifiableList(codes);
	}

	@Autowired
	private DomainDao domainDao;

	@Autowired
	private OrgDao orgDao;

	@Autowired
	private OrgUserDao orgUserDao;

	@Autowired
	private SlotBindingDao slotBindingDao;

	@Autowired
	private RoleBindingDao roleBindingDao;

	@Autowired
	private ArchitectureDao architectureDao;

	@Autowired
	private IDGen idGen;

	@Value("${wldos.rbac.end-state-migration.enabled:false}")
	private boolean enabled;

	@Value("${wldos.rbac.end-state-migration.apply:false}")
	private boolean apply;

	@Override
	public void run(ApplicationArguments args) {
		if (!this.enabled) {
			return;
		}
		EndStateMigrationReport report = this.plan();
		report.logSummary(this.apply);
		if (!this.apply) {
			log.info("[rbac.end-state] dry-run 完成；写入库请设 wldos.rbac.end-state-migration.apply=true");
			return;
		}
		this.applyReport(report);
	}

	EndStateMigrationReport plan() {
		EndStateMigrationReport report = new EndStateMigrationReport();
		Map<Long, WoOrg> catalogMemberOrgs = new LinkedHashMap<>();
		for (WoOrg row : this.orgDao.findValidSegmentsOnCatalogDomains(Constants.ENUM_TYPE_ORG_SEGMENT)) {
			catalogMemberOrgs.put(row.getId(), row);
		}
		for (WoOrg row : this.orgDao.findValidMemberKindsOnCatalogDomains(MEMBER_KIND_ORG_CODES)) {
			catalogMemberOrgs.put(row.getId(), row);
		}
		report.setCatalogSegmentOrgs(new ArrayList<>(catalogMemberOrgs.values()));
		report.setWrongAxisMemberKindDuplicates(
				this.orgDao.findValidWrongAxisMemberKindDuplicates(MEMBER_KIND_ORG_CODES));
		report.setWrongAxisMemberKindsWithoutCanonical(
				this.orgDao.findValidWrongAxisMemberKindsWithoutCanonical(MEMBER_KIND_ORG_CODES));
		report.setWrongAxisMemberKindsOrphanDomain(
				this.orgDao.findValidWrongAxisMemberKindsOrphanDomain(MEMBER_KIND_ORG_CODES));
		List<WoSlotBinding> bindings = this.slotBindingDao.findValidOrgBindingsOnCatalogSegmentTargets();
		report.setCatalogSegmentSlotBindings(bindings);
		report.setKindTemplateOrgUsers(
				this.orgUserDao.findValidMembershipsOnKindTemplateOrgs(MEMBER_KIND_ORG_CODES));
		report.setIdentityOrgUsers(this.orgUserDao.findValidMembershipsOnIdentityOrgs(IDENTITY_ORG_CODES));
		report.setStaffOrgsMissingArch(this.orgDao.findValidTenantStaffOrgsMissingArch(EXCLUDED_STAFF_ORG_CODES));
		report.setDuplicatePlaceholderBindings(this.roleBindingDao.findValidDuplicatePlaceholderBindings());
		report.setOrphanPlaceholderBindings(this.roleBindingDao.findValidOrphanPlaceholderBindings());
		report.setDomainsWithMissingCompany(this.domainDao.findValidDomainsWithMissingCompany());
		report.setCatalogAxisDomains(this.domainDao.findValidCatalogAxisDomains());
		return report;
	}

	void applyReport(EndStateMigrationReport report) {
		for (WoOrg org : report.getCatalogSegmentOrgs()) {
			this.softDeleteOrg(org);
		}
		for (WoOrg org : report.getWrongAxisMemberKindDuplicates()) {
			this.softDeleteOrg(org);
		}
		int realigned = 0;
		for (WoOrg org : report.getWrongAxisMemberKindsWithoutCanonical()) {
			if (this.realignWrongAxisMemberKindComId(org)) {
				realigned++;
			}
		}
		for (WoOrg org : report.getWrongAxisMemberKindsOrphanDomain()) {
			this.softDeleteOrg(org);
		}
		for (WoSlotBinding binding : report.getCatalogSegmentSlotBindings()) {
			binding.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
			binding.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
			this.slotBindingDao.save(binding);
		}
		for (WoOrgUser membership : report.getKindTemplateOrgUsers()) {
			this.softDeleteOrgUser(membership);
		}
		for (WoOrgUser membership : report.getIdentityOrgUsers()) {
			this.softDeleteOrgUser(membership);
		}
		int archFilled = 0;
		for (WoOrg org : report.getStaffOrgsMissingArch()) {
			if (this.backfillStaffOrgArchId(org)) {
				archFilled++;
			}
		}
		for (WoRoleBinding binding : report.getDuplicatePlaceholderBindings()) {
			this.softDeleteRoleBinding(binding);
		}
		int bindingRekeyed = 0;
		for (WoRoleBinding binding : report.getOrphanPlaceholderBindings()) {
			if (this.rekeyPlaceholderBinding(binding)) {
				bindingRekeyed++;
			}
		}
		log.warn("[rbac.end-state] apply 完成：门户分群 {}，重复副本 {}，回填租户 {}，无效站点分群 {}，"
				+ "非法插槽 {}，模板成员 {}，旧身份成员 {}，arch 回填 {}，删占位绑定 {}，换绑定 id {}",
				report.getCatalogSegmentOrgs().size(),
				report.getWrongAxisMemberKindDuplicates().size(),
				realigned,
				report.getWrongAxisMemberKindsOrphanDomain().size(),
				report.getCatalogSegmentSlotBindings().size(),
				report.getKindTemplateOrgUsers().size(),
				report.getIdentityOrgUsers().size(),
				archFilled,
				report.getDuplicatePlaceholderBindings().size(),
				bindingRekeyed);
	}

	private boolean realignWrongAxisMemberKindComId(WoOrg org) {
		if (org == null || org.getDomainId() == null || org.getDomainId() <= 0) {
			return false;
		}
		return this.domainDao.findById(org.getDomainId())
				.filter(d -> d.getComId() != null && d.getComId() > 0)
				.map(d -> {
					org.setComId(d.getComId());
					org.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
					this.orgDao.save(org);
					return true;
				})
				.orElse(false);
	}

	private void softDeleteOrg(WoOrg org) {
		org.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		org.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
		this.orgDao.save(org);
	}

	private void softDeleteOrgUser(WoOrgUser membership) {
		membership.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		membership.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
		this.orgUserDao.save(membership);
	}

	private boolean backfillStaffOrgArchId(WoOrg org) {
		if (org == null || org.getComId() == null || org.getComId() <= 0) {
			return false;
		}
		WoArchitecture arch = this.architectureDao.findFirstByComIdAndDeleteFlagAndIsValid(org.getComId());
		if (arch == null || arch.getId() == null || arch.getId() <= 0) {
			return false;
		}
		org.setArchId(arch.getId());
		org.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
		this.orgDao.save(org);
		return true;
	}

	private void softDeleteRoleBinding(WoRoleBinding binding) {
		binding.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		binding.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
		this.roleBindingDao.save(binding);
	}

	private boolean rekeyPlaceholderBinding(WoRoleBinding old) {
		if (old == null) {
			return false;
		}
		WoRoleBinding neu = new WoRoleBinding();
		neu.setId(this.idGen.nextId());
		neu.setRoleId(old.getRoleId());
		neu.setTargetType(old.getTargetType());
		neu.setTargetId(old.getTargetId());
		neu.setComId(old.getComId());
		neu.setArchId(old.getArchId());
		neu.setDomainId(old.getDomainId());
		neu.setIsValid(ValidStatusEnum.VALID.toString());
		neu.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		neu.setCreateBy(old.getCreateBy() != null ? old.getCreateBy() : Constants.SYSTEM_USER_ID);
		this.roleBindingDao.save(neu);
		old.setDeleteFlag(DeleteFlagEnum.DELETED.toString());
		old.setUpdateTime(DateUtils.convSQLDate(new java.util.Date()));
		this.roleBindingDao.save(old);
		return true;
	}

	static final class EndStateMigrationReport {

		private List<WoOrg> catalogSegmentOrgs = java.util.Collections.emptyList();

		private List<WoOrg> wrongAxisMemberKindDuplicates = java.util.Collections.emptyList();

		private List<WoOrg> wrongAxisMemberKindsWithoutCanonical = java.util.Collections.emptyList();

		private List<WoOrg> wrongAxisMemberKindsOrphanDomain = java.util.Collections.emptyList();

		private List<WoSlotBinding> catalogSegmentSlotBindings = java.util.Collections.emptyList();

		private List<WoOrgUser> kindTemplateOrgUsers = java.util.Collections.emptyList();

		private List<WoOrgUser> identityOrgUsers = java.util.Collections.emptyList();

		private List<WoOrg> staffOrgsMissingArch = java.util.Collections.emptyList();

		private List<WoRoleBinding> duplicatePlaceholderBindings = java.util.Collections.emptyList();

		private List<WoRoleBinding> orphanPlaceholderBindings = java.util.Collections.emptyList();

		private List<WoDomain> domainsWithMissingCompany = java.util.Collections.emptyList();

		private List<WoDomain> catalogAxisDomains = java.util.Collections.emptyList();

		void setCatalogSegmentOrgs(List<WoOrg> catalogSegmentOrgs) {
			this.catalogSegmentOrgs = catalogSegmentOrgs == null ? java.util.Collections.emptyList() : catalogSegmentOrgs;
		}

		void setWrongAxisMemberKindDuplicates(List<WoOrg> wrongAxisMemberKindDuplicates) {
			this.wrongAxisMemberKindDuplicates = wrongAxisMemberKindDuplicates == null
					? java.util.Collections.emptyList() : wrongAxisMemberKindDuplicates;
		}

		void setWrongAxisMemberKindsWithoutCanonical(List<WoOrg> wrongAxisMemberKindsWithoutCanonical) {
			this.wrongAxisMemberKindsWithoutCanonical = wrongAxisMemberKindsWithoutCanonical == null
					? java.util.Collections.emptyList() : wrongAxisMemberKindsWithoutCanonical;
		}

		void setWrongAxisMemberKindsOrphanDomain(List<WoOrg> wrongAxisMemberKindsOrphanDomain) {
			this.wrongAxisMemberKindsOrphanDomain = wrongAxisMemberKindsOrphanDomain == null
					? java.util.Collections.emptyList() : wrongAxisMemberKindsOrphanDomain;
		}

		void setCatalogSegmentSlotBindings(List<WoSlotBinding> catalogSegmentSlotBindings) {
			this.catalogSegmentSlotBindings = catalogSegmentSlotBindings == null
					? java.util.Collections.emptyList() : catalogSegmentSlotBindings;
		}

		void setKindTemplateOrgUsers(List<WoOrgUser> kindTemplateOrgUsers) {
			this.kindTemplateOrgUsers = kindTemplateOrgUsers == null
					? java.util.Collections.emptyList() : kindTemplateOrgUsers;
		}

		void setIdentityOrgUsers(List<WoOrgUser> identityOrgUsers) {
			this.identityOrgUsers = identityOrgUsers == null ? java.util.Collections.emptyList() : identityOrgUsers;
		}

		void setStaffOrgsMissingArch(List<WoOrg> staffOrgsMissingArch) {
			this.staffOrgsMissingArch = staffOrgsMissingArch == null
					? java.util.Collections.emptyList() : staffOrgsMissingArch;
		}

		void setDuplicatePlaceholderBindings(List<WoRoleBinding> duplicatePlaceholderBindings) {
			this.duplicatePlaceholderBindings = duplicatePlaceholderBindings == null
					? java.util.Collections.emptyList() : duplicatePlaceholderBindings;
		}

		void setOrphanPlaceholderBindings(List<WoRoleBinding> orphanPlaceholderBindings) {
			this.orphanPlaceholderBindings = orphanPlaceholderBindings == null
					? java.util.Collections.emptyList() : orphanPlaceholderBindings;
		}

		void setDomainsWithMissingCompany(List<WoDomain> domainsWithMissingCompany) {
			this.domainsWithMissingCompany = domainsWithMissingCompany == null
					? java.util.Collections.emptyList() : domainsWithMissingCompany;
		}

		void setCatalogAxisDomains(List<WoDomain> catalogAxisDomains) {
			this.catalogAxisDomains = catalogAxisDomains == null
					? java.util.Collections.emptyList() : catalogAxisDomains;
		}

		List<WoOrg> getCatalogSegmentOrgs() {
			return this.catalogSegmentOrgs;
		}

		List<WoOrg> getWrongAxisMemberKindDuplicates() {
			return this.wrongAxisMemberKindDuplicates;
		}

		List<WoOrg> getWrongAxisMemberKindsWithoutCanonical() {
			return this.wrongAxisMemberKindsWithoutCanonical;
		}

		List<WoOrg> getWrongAxisMemberKindsOrphanDomain() {
			return this.wrongAxisMemberKindsOrphanDomain;
		}

		List<WoSlotBinding> getCatalogSegmentSlotBindings() {
			return this.catalogSegmentSlotBindings;
		}

		List<WoOrgUser> getKindTemplateOrgUsers() {
			return this.kindTemplateOrgUsers;
		}

		List<WoOrgUser> getIdentityOrgUsers() {
			return this.identityOrgUsers;
		}

		List<WoOrg> getStaffOrgsMissingArch() {
			return this.staffOrgsMissingArch;
		}

		List<WoRoleBinding> getDuplicatePlaceholderBindings() {
			return this.duplicatePlaceholderBindings;
		}

		List<WoRoleBinding> getOrphanPlaceholderBindings() {
			return this.orphanPlaceholderBindings;
		}

		List<WoDomain> getDomainsWithMissingCompany() {
			return this.domainsWithMissingCompany;
		}

		List<WoDomain> getCatalogAxisDomains() {
			return this.catalogAxisDomains;
		}

		void logSummary(boolean apply) {
			log.warn("[rbac.end-state] {} 个站点所属租户在 company 表中不存在（仅报告，不写库）",
					this.domainsWithMissingCompany.size());
			for (WoDomain d : this.domainsWithMissingCompany) {
				log.warn("[rbac.end-state]   domain id={} siteDomain={} comId={}",
						d.getId(), d.getSiteDomain(), d.getComId());
			}
			log.warn("[rbac.end-state] {} 个平台门户站点（com_id=0）", this.catalogAxisDomains.size());
			for (WoDomain d : this.catalogAxisDomains) {
				Long comId = d.getComId();
				boolean ok = comId != null && comId == Constants.TOP_COM_ID;
				log.warn("[rbac.end-state]   domain id={} siteDomain={} com_id={} {}",
						d.getId(), d.getSiteDomain(), comId, ok ? "OK" : "异常");
			}
			if (this.catalogAxisDomains.isEmpty()) {
				log.warn("[rbac.end-state] 未找到有效的平台门户站点（com_id=0）");
			}
			log.warn("[rbac.end-state] {} 条门户站上的会员分群（apply 时将软删）", this.catalogSegmentOrgs.size());
			for (WoOrg o : this.catalogSegmentOrgs) {
				log.warn("[rbac.end-state]   org id={} code={} domainId={} comId={}",
						o.getId(), o.getOrgCode(), o.getDomainId(), o.getComId());
			}
			log.warn("[rbac.end-state] {} 条租户站会员分群重复副本（已有正确租户轴数据，apply 时将软删）",
					this.wrongAxisMemberKindDuplicates.size());
			for (WoOrg o : this.wrongAxisMemberKindDuplicates) {
				log.warn("[rbac.end-state]   org id={} code={} domainId={} comId={}",
						o.getId(), o.getOrgCode(), o.getDomainId(), o.getComId());
			}
			log.warn("[rbac.end-state] {} 条租户站会员分群仍挂在平台租户轴（apply 时将 com_id 改为站点所属租户）",
					this.wrongAxisMemberKindsWithoutCanonical.size());
			for (WoOrg o : this.wrongAxisMemberKindsWithoutCanonical) {
				log.warn("[rbac.end-state]   org id={} code={} domainId={} comId={}",
						o.getId(), o.getOrgCode(), o.getDomainId(), o.getComId());
			}
			log.warn("[rbac.end-state] {} 条会员分群指向无效站点（apply 时将软删）",
					this.wrongAxisMemberKindsOrphanDomain.size());
			for (WoOrg o : this.wrongAxisMemberKindsOrphanDomain) {
				log.warn("[rbac.end-state]   org id={} code={} domainId={} comId={}",
						o.getId(), o.getOrgCode(), o.getDomainId(), o.getComId());
			}
			log.warn("[rbac.end-state] {} 条门户站上的非法插槽绑定（apply 时将软删）", this.catalogSegmentSlotBindings.size());
			for (WoSlotBinding b : this.catalogSegmentSlotBindings) {
				log.warn("[rbac.end-state]   binding id={} slot={} targetId={} domainId={}",
						b.getId(), b.getSlotCode(), b.getTargetId(), b.getDomainId());
			}
			log.warn("[rbac.end-state] {} 条平台模板组织上的成员关系（apply 时将软删）", this.kindTemplateOrgUsers.size());
			for (WoOrgUser u : this.kindTemplateOrgUsers) {
				log.warn("[rbac.end-state]   org_user id={} orgId={} userId={}",
						u.getId(), u.getOrgId(), u.getUserId());
			}
			log.warn("[rbac.end-state] {} 条应迁移到身份登记册的旧组织成员（apply 时将软删）", this.identityOrgUsers.size());
			for (WoOrgUser u : this.identityOrgUsers) {
				log.warn("[rbac.end-state]   org_user id={} orgId={} userId={}",
						u.getId(), u.getOrgId(), u.getUserId());
			}
			log.warn("[rbac.end-state] {} 条租户编制组织缺少 arch_id（apply 时将回填）", this.staffOrgsMissingArch.size());
			for (WoOrg o : this.staffOrgsMissingArch) {
				log.warn("[rbac.end-state]   org id={} code={} comId={} archId={}",
						o.getId(), o.getOrgCode(), o.getComId(), o.getArchId());
			}
			log.warn("[rbac.end-state] {} 条占位 id 的角色绑定重复（apply 时将软删占位行）",
					this.duplicatePlaceholderBindings.size());
			for (WoRoleBinding b : this.duplicatePlaceholderBindings) {
				log.warn("[rbac.end-state]   binding id={} roleId={} targetType={} targetId={}",
						b.getId(), b.getRoleId(), b.getTargetType(), b.getTargetId());
			}
			log.warn("[rbac.end-state] {} 条占位 id 的角色绑定无对应正式行（apply 时将换发真实 id）",
					this.orphanPlaceholderBindings.size());
			for (WoRoleBinding b : this.orphanPlaceholderBindings) {
				log.warn("[rbac.end-state]   binding id={} roleId={} targetType={} targetId={}",
						b.getId(), b.getRoleId(), b.getTargetType(), b.getTargetId());
			}
			if (!apply) {
				log.warn("[rbac.end-state] 以上为计划动作，未写库");
			}
		}
	}
}
