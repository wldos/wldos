/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.wldos.platform.core.dao.AuthRoleDao;
import com.wldos.platform.core.dao.DomainAppDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.dao.OrgDao;
import com.wldos.platform.core.dao.RoleDao;
import com.wldos.platform.core.dao.RoleOrgDao;
import com.wldos.platform.core.entity.WoAuthRole;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainApp;
import com.wldos.platform.core.entity.WoDomainResource;
import com.wldos.platform.core.entity.WoOrg;
import com.wldos.platform.core.entity.WoRole;
import com.wldos.platform.core.entity.WoRoleOrg;
import com.wldos.platform.core.enums.RoleTypeEnum;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.framework.common.IDGen;
import io.github.wldos.platform.support.rbac.kind.SystemOrgKind;
import io.github.wldos.platform.support.rbac.kind.SystemRoleKind;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 站点创建时复制平台模板，生成内置会员分群/角色及默认关联。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Service
public class DomainBootstrap {

	private final OrgDao orgDao;

	private final RoleDao roleDao;

	private final RoleOrgDao roleOrgDao;

	private final AuthRoleDao authRoleDao;

	private final DomainResourceDao domainResourceDao;

	private final DomainAppDao domainAppDao;

	private final DomainDao domainDao;

	private final KindInstanceResolverImpl kindInstanceResolver;

	@Value("${wldos.platform.template-domain-id:0}")
	private long configuredTemplateDomainId;

	public DomainBootstrap(
			OrgDao orgDao,
			RoleDao roleDao,
			RoleOrgDao roleOrgDao,
			AuthRoleDao authRoleDao,
			DomainResourceDao domainResourceDao,
			DomainAppDao domainAppDao,
			DomainDao domainDao,
			KindInstanceResolverImpl kindInstanceResolver) {
		this.orgDao = orgDao;
		this.roleDao = roleDao;
		this.roleOrgDao = roleOrgDao;
		this.authRoleDao = authRoleDao;
		this.domainResourceDao = domainResourceDao;
		this.domainAppDao = domainAppDao;
		this.domainDao = domainDao;
		this.kindInstanceResolver = kindInstanceResolver;
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureSiteOperationalSeed(Long comId, Long domainId) {
		ensureSiteKindSeed(comId, domainId);
		this.ensureSiteMounts(comId, domainId);
	}

	/** 站点默认资源/应用挂载，与会员分群复制独立配置 */
	@Transactional(rollbackFor = Exception.class)
	public void ensureSiteMounts(Long comId, Long domainId) {
		this.ensureDefaultDomainResourceMount(domainId);
		this.ensureDefaultDomainAppMount(comId, domainId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureSiteKindSeed(Long comId, Long domainId) {
		if (comId == null || domainId == null || domainId <= 0) {
			return;
		}
		if (Objects.equals(comId, Constants.TOP_COM_ID)) {
			log.debug("[domain.bootstrap] 门户站跳过会员分群复制 domainId={}", domainId);
			return;
		}
		this.provisionSystemKinds(comId, domainId);
		this.ensureInitialAuthFromTemplate(comId, domainId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void provisionSystemKinds(Long comId, Long domainId) {
		if (comId == null || domainId == null || domainId <= 0) {
			return;
		}
		if (Objects.equals(comId, Constants.TOP_COM_ID)) {
			return;
		}
		if (this.kindInstanceResolver.hasSiteKindInstances(comId, domainId)) {
			log.debug("[domain.bootstrap] skip existing kinds comId={} domainId={}", comId, domainId);
			return;
		}

		Map<SystemOrgKind, Long> orgIds = this.provisionOrgTree(comId, domainId);
		Map<SystemRoleKind, Long> roleIds = this.provisionRoleTree(comId, domainId);
		this.provisionDefaultRoleOrg(comId, roleIds, orgIds);

		log.info("[domain.bootstrap] provisioned II class kinds comId={} domainId={}", comId, domainId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureInitialAuthFromTemplate(Long comId, Long domainId) {
		if (comId == null || domainId == null || domainId <= 0) {
			return;
		}
		for (SystemRoleKind kind : SystemRoleKind.siteBootstrapKinds()) {
			Long siteRoleId = this.kindInstanceResolver.resolveRoleKind(comId, domainId, kind);
			if (siteRoleId == null) {
				continue;
			}
			if (this.authRoleDao.countByRoleId(siteRoleId) > 0) {
				continue;
			}
			WoRole template = this.roleDao.findPlatformTemplateByRoleCode(kind.getRoleCode());
			if (template == null || template.getId() == null) {
				log.warn("[domain.bootstrap] skip auth clone, no template for kind={} domainId={}", kind.getRoleCode(), domainId);
				continue;
			}
			List<WoAuthRole> templateGrants = this.authRoleDao.findAllByRoleId(template.getId());
			if (templateGrants == null || templateGrants.isEmpty()) {
				continue;
			}
			for (WoAuthRole source : templateGrants) {
				WoAuthRole grant = new WoAuthRole();
				grant.setId(IDGen.nextId());
				grant.setRoleId(siteRoleId);
				grant.setResourceId(source.getResourceId());
				grant.setAppId(source.getAppId());
				grant.setComId(null);
				grant.setDomainId(null);
				grant.setIsValid(ValidStatusEnum.VALID.toString());
				grant.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
				this.authRoleDao.save(grant);
			}
			log.info("[domain.bootstrap] cloned {} auth rows kind={} domainId={}", templateGrants.size(), kind.getRoleCode(), domainId);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureDefaultDomainResourceMount(Long domainId) {
		ensureDefaultDomainResourceMount(domainId, resolveFallbackTemplateDomainId());
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureDefaultDomainResourceMount(Long domainId, Long templateDomainId) {
		if (domainId == null || domainId <= 0) {
			return;
		}
		if (this.domainResourceDao.countByDomainId(domainId) > 0) {
			return;
		}
		if (templateDomainId == null || templateDomainId <= 0 || templateDomainId.equals(domainId)) {
			log.warn("[domain.bootstrap] skip mount clone, no template domain for domainId={}", domainId);
			return;
		}
		List<WoDomainResource> templateMounts = this.domainResourceDao.queryDomainRes(templateDomainId);
		if (templateMounts == null || templateMounts.isEmpty()) {
			log.warn("[domain.bootstrap] template domainId={} has no wo_domain_resource rows, target domainId={}",
					templateDomainId, domainId);
			return;
		}
		for (WoDomainResource source : templateMounts) {
			WoDomainResource mount = new WoDomainResource();
			mount.setId(IDGen.nextId());
			mount.setModuleName(source.getModuleName());
			mount.setResourceId(source.getResourceId());
			mount.setAppId(source.getAppId());
			mount.setTermTypeId(source.getTermTypeId());
			mount.setUrl(source.getUrl());
			mount.setExtraProps(source.getExtraProps());
			mount.setDomainId(domainId);
			mount.setIsValid(ValidStatusEnum.VALID.toString());
			mount.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			this.domainResourceDao.save(mount);
		}
		log.info("[domain.bootstrap] cloned {} domain_resource rows domainId={} from templateDomainId={}",
				templateMounts.size(), domainId, templateDomainId);
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureDefaultDomainAppMount(Long comId, Long domainId) {
		ensureDefaultDomainAppMount(comId, domainId, resolveFallbackTemplateDomainId());
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensureDefaultDomainAppMount(Long comId, Long domainId, Long templateDomainId) {
		if (comId == null || domainId == null || domainId <= 0) {
			return;
		}
		if (this.domainAppDao.countByDomainId(domainId) > 0) {
			return;
		}
		if (templateDomainId == null || templateDomainId <= 0 || templateDomainId.equals(domainId)) {
			log.warn("[domain.bootstrap] skip app clone, no template domain for domainId={}", domainId);
			return;
		}
		List<WoDomainApp> templateApps = this.domainAppDao.queryAllByDomainIdAndIsValidAndDeleteFlag(
				templateDomainId, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (templateApps == null || templateApps.isEmpty()) {
			log.warn("[domain.bootstrap] template domainId={} has no wo_domain_app rows, target domainId={}",
					templateDomainId, domainId);
			return;
		}
		for (WoDomainApp source : templateApps) {
			if (source.getAppId() == null) {
				continue;
			}
			if (this.domainAppDao.existsByAppIdAndDomainId(source.getAppId(), domainId)) {
				continue;
			}
			WoDomainApp mount = new WoDomainApp();
			mount.setId(IDGen.nextId());
			mount.setAppId(source.getAppId());
			mount.setComId(comId);
			mount.setDomainId(domainId);
			mount.setIsValid(ValidStatusEnum.VALID.toString());
			mount.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			this.domainAppDao.save(mount);
		}
		log.info("[domain.bootstrap] cloned {} domain_app rows domainId={} from templateDomainId={}",
				templateApps.size(), domainId, templateDomainId);
	}

	/**
	 * 入驻/建站后校验域级资源与应用挂载是否就绪。
	 */
	public void assertSiteMountsReady(Long domainId) {
		assertSiteMountsReady(domainId, null, null);
	}

	public void assertSiteMountsReady(Long domainId, Long templateDomainId, String templateSiteDomain) {
		if (domainId == null || domainId <= 0) {
			return;
		}
		long resourceCount = this.domainResourceDao.countByDomainId(domainId);
		long appCount = this.domainAppDao.countByDomainId(domainId);
		if (resourceCount <= 0 || appCount <= 0) {
			StringBuilder msg = new StringBuilder("主站资源或应用挂载未完成");
			if (!ObjectUtils.isBlank(templateSiteDomain) && templateDomainId != null) {
				msg.append("（模板站 ").append(templateSiteDomain).append(", id=").append(templateDomainId).append("）");
			}
			msg.append("：域资源 ").append(resourceCount).append("、应用 ").append(appCount);
			msg.append("；请在站点管理为入驻模板站配置应用与资源");
			throw new BaseException(msg.toString(), ResultCode.OPERATION_FAILED.getCode());
		}
	}

	/**
	 * yml 或 SQL 回退解析平台模板站 id（无场景登记时使用）。
	 */
	public Long resolveFallbackTemplateDomainId() {
		if (this.configuredTemplateDomainId > 0) {
			return this.configuredTemplateDomainId;
		}
		WoDomain template = this.domainDao.findPlatformTemplateDomain();
		return template != null ? template.getId() : null;
	}

	private Long resolveTemplateDomainId() {
		return resolveFallbackTemplateDomainId();
	}

	private Map<SystemOrgKind, Long> provisionOrgTree(Long comId, Long domainId) {
		Map<SystemOrgKind, Long> ids = new HashMap<>();
		for (SystemOrgKind kind : SystemOrgKind.siteBootstrapKinds()) {
			Long parentId = Constants.TOP_COM_ID;
			if (kind.getParentKind() != null) {
				parentId = ids.get(kind.getParentKind());
			}
			WoOrg org = new WoOrg();
			org.setId(IDGen.nextId());
			org.setOrgCode(kind.getOrgCode());
			org.setOrgName(kind.getDefaultName());
			org.setOrgType(kind.getDefaultOrgType());
			org.setArchId(Constants.TOP_ARCH_ID);
			org.setComId(comId);
			org.setDomainId(domainId);
			org.setParentId(parentId != null ? parentId : Constants.TOP_COM_ID);
			org.setDisplayOrder(kind.getDefaultDisplayOrder());
			org.setIsValid(ValidStatusEnum.VALID.toString());
			org.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			org.markSystemReserved();
			this.orgDao.saveOrUpdate(org);
			ids.put(kind, org.getId());
		}
		return ids;
	}

	private Map<SystemRoleKind, Long> provisionRoleTree(Long comId, Long domainId) {
		Map<SystemRoleKind, Long> ids = new HashMap<>();
		for (SystemRoleKind kind : SystemRoleKind.siteBootstrapKinds()) {
			Long parentId = Constants.TOP_ROLE_ID;
			if (kind.getParentKind() != null) {
				parentId = ids.get(kind.getParentKind());
			}
			WoRole role = new WoRole();
			role.setId(IDGen.nextId());
			role.setRoleCode(kind.getRoleCode());
			role.setRoleName(kind.getDefaultName());
			role.setRoleType(RoleTypeEnum.SYS_ROLE.toString());
			role.setParentId(parentId != null ? parentId : Constants.TOP_ROLE_ID);
			role.setDisplayOrder(kind.getDefaultDisplayOrder());
			role.setIsValid(ValidStatusEnum.VALID.toString());
			role.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			role.markSystemReserved();
			role.setComId(comId);
			role.setDomainId(domainId);
			this.roleDao.saveOrUpdate(role);
			ids.put(kind, role.getId());
		}
		return ids;
	}

	private void provisionDefaultRoleOrg(Long comId, Map<SystemRoleKind, Long> roleIds, Map<SystemOrgKind, Long> orgIds) {
		bindRoleOrg(comId, roleIds.get(SystemRoleKind.GUEST), orgIds.get(SystemOrgKind.UN_ACTIVE));
		bindRoleOrg(comId, roleIds.get(SystemRoleKind.USER), orgIds.get(SystemOrgKind.FREEUSER));
		bindRoleOrg(comId, roleIds.get(SystemRoleKind.GVIP), orgIds.get(SystemOrgKind.VIP));
	}

	private void bindRoleOrg(Long comId, Long roleId, Long orgId) {
		if (roleId == null || orgId == null) {
			return;
		}
		WoRoleOrg bind = new WoRoleOrg();
		bind.setId(IDGen.nextId());
		bind.setRoleId(roleId);
		bind.setOrgId(orgId);
		bind.setArchId(Constants.TOP_ARCH_ID);
		bind.setComId(comId);
		bind.setIsValid(ValidStatusEnum.VALID.toString());
		bind.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		this.roleOrgDao.saveOrUpdate(bind);
	}
}
