/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.Objects;
import java.util.Optional;

import com.wldos.platform.core.dao.ArchitectureDao;
import com.wldos.platform.core.dao.CompanyDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.entity.WoArchitecture;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.support.PlatformOperatorConstants;

import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.utils.ObjectUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 幂等创建 platform-operator 公司、绑定主站 domain、provision II 并写入 options 指针。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Service
public class PlatformOperatorBootstrapService {

	private final CompanyDao companyDao;

	private final ArchitectureDao architectureDao;

	private final DomainDao domainDao;

	private final DomainBootstrap domainBootstrap;

	private final DomainService domainService;

	private final PlatformOperatorSettingsService platformOperatorSettingsService;

	@Value("${wldos.platform.operator.primary-site-domain:localhost}")
	private String primarySiteDomain;

	public PlatformOperatorBootstrapService(
			CompanyDao companyDao,
			ArchitectureDao architectureDao,
			DomainDao domainDao,
			DomainBootstrap domainBootstrap,
			DomainService domainService,
			PlatformOperatorSettingsService platformOperatorSettingsService) {
		this.companyDao = companyDao;
		this.architectureDao = architectureDao;
		this.domainDao = domainDao;
		this.domainBootstrap = domainBootstrap;
		this.domainService = domainService;
		this.platformOperatorSettingsService = platformOperatorSettingsService;
	}

	@Transactional(rollbackFor = Exception.class)
	public void ensurePlatformOperatorReady() {
		if (this.platformOperatorSettingsService.buildContext().isReady()) {
			log.debug("[platform.operator] 已配置，跳过 bootstrap");
			return;
		}

		WoCompany company = this.companyDao.findFirstByComCode(PlatformOperatorConstants.COM_CODE);
		if (company == null) {
			company = new WoCompany();
			company.setComCode(PlatformOperatorConstants.COM_CODE);
			company.setComName(PlatformOperatorConstants.DEFAULT_COM_NAME);
			company.setParentId(Constants.TOP_COM_ID);
			company.setDisplayOrder(1L);
			company.setIsValid(ValidStatusEnum.VALID.toString());
			company.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			assignReservedComIdIfAvailable(company);
			this.companyDao.saveOrUpdate(company);
			log.info("[platform.operator] 已创建运营公司 id={}", company.getId());
		}

		WoArchitecture arch = this.architectureDao.findFirstByComIdAndDeleteFlagAndIsValid(company.getId());
		if (arch == null) {
			arch = new WoArchitecture();
			arch.setComId(company.getId());
			arch.setArchCode(PlatformOperatorConstants.COM_CODE + "-arch");
			arch.setArchName(PlatformOperatorConstants.DEFAULT_COM_NAME + "体系");
			arch.setParentId(Constants.TOP_ARCH_ID);
			arch.setDisplayOrder(1L);
			arch.setIsValid(ValidStatusEnum.VALID.toString());
			arch.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
			this.architectureDao.saveOrUpdate(arch);
		}

		String siteDomain = ObjectUtils.isBlank(this.primarySiteDomain)
				? "localhost"
				: this.primarySiteDomain.trim().toLowerCase();
		WoDomain primary = resolvePrimaryDomain(siteDomain, company.getId());
		if (primary == null) {
			log.warn("[platform.operator] 未找到主站 domain（site_domain={}），跳过绑定", siteDomain);
			return;
		}

		if (!Objects.equals(primary.getComId(), company.getId())) {
			primary.setComId(company.getId());
			this.domainDao.saveOrUpdate(primary);
			log.info("[platform.operator] 主站 domain id={} 已绑定 com_id={}", primary.getId(), company.getId());
		}

		this.domainService.provisionSystemKindSeed(primary);
		this.domainBootstrap.ensureSiteOperationalSeed(company.getId(), primary.getId());
		this.platformOperatorSettingsService.upsertOperatorPointers(company.getId(), primary.getId());
		log.info("[platform.operator] bootstrap 完成 comId={} domainId={}", company.getId(), primary.getId());
	}

	private void assignReservedComIdIfAvailable(WoCompany company) {
		long reservedId = PlatformOperatorConstants.RESERVED_COM_ID;
		Optional<WoCompany> atReserved = this.companyDao.findById(reservedId);
		if (!atReserved.isPresent()) {
			company.setId(reservedId);
			return;
		}
		WoCompany occupant = atReserved.get();
		if (PlatformOperatorConstants.COM_CODE.equals(occupant.getComCode())
				&& DeleteFlagEnum.NORMAL.toString().equals(occupant.getDeleteFlag())) {
			return;
		}
		log.warn("[platform.operator] 预留 com id={} 已被其他主体占用，将使用自动分配 id", reservedId);
	}

	private WoDomain resolvePrimaryDomain(String siteDomain, Long operatorComId) {
		Optional<Long> configuredDomainId = this.platformOperatorSettingsService.findPrimaryDomainId();
		if (configuredDomainId.isPresent()) {
			return this.domainDao.findById(configuredDomainId.get()).orElse(null);
		}
		WoDomain bySite = this.domainDao.findBySiteDomainAndIsValidAndDeleteFlag(
				siteDomain, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (bySite != null) {
			return bySite;
		}
		return this.domainDao.findPlatformTemplateDomain();
	}
}
