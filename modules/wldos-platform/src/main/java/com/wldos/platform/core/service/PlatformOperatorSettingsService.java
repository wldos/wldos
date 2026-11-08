/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.core.dao.CompanyDao;
import com.wldos.platform.core.dao.DomainAdminDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoDomainAdmin;
import com.wldos.platform.core.enums.SysOptionEnum;
import com.wldos.platform.core.support.PlatformOperatorConstants;
import com.wldos.platform.core.vo.Company;
import com.wldos.platform.core.vo.PlatformOperatorContextVO;

import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.system.entity.WoOptions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 解析 {@code wo_options} 中平台运营指针，并校验 com / 主站 domain 绑定。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
@Transactional(readOnly = true)
public class PlatformOperatorSettingsService extends NonEntityService {

	private final OptionsService optionsService;

	private final CompanyDao companyDao;

	private final DomainDao domainDao;

	private final DomainAdminDao domainAdminDao;

	public PlatformOperatorSettingsService(
			OptionsService optionsService,
			CompanyDao companyDao,
			DomainDao domainDao,
			DomainAdminDao domainAdminDao) {
		this.optionsService = optionsService;
		this.companyDao = companyDao;
		this.domainDao = domainDao;
		this.domainAdminDao = domainAdminDao;
	}

	public Optional<Long> findOperatorComId() {
		Long fromOption = parsePositiveLong(this.optionsService.findSettingsByKey(
				SysOptionEnum.PLATFORM_OPERATOR_COM_ID.getKey()));
		if (fromOption != null) {
			return Optional.of(fromOption);
		}
		WoCompany byCode = this.companyDao.findFirstByComCode(PlatformOperatorConstants.COM_CODE);
		if (byCode == null || byCode.getId() == null || byCode.getId() <= 0) {
			return Optional.empty();
		}
		return Optional.of(byCode.getId());
	}

	public Long resolveOperatorComId() {
		return findOperatorComId().orElseThrow(() -> new BaseException(
				"平台运营主体未配置，请先执行平台运营初始化", ResultCode.OPERATION_FAILED.getCode()));
	}

	public Optional<Long> findPrimaryDomainId() {
		Long fromOption = parsePositiveLong(this.optionsService.findSettingsByKey(
				SysOptionEnum.PLATFORM_OPERATOR_PRIMARY_DOMAIN_ID.getKey()));
		if (fromOption != null) {
			return Optional.of(fromOption);
		}
		return Optional.empty();
	}

	public boolean isPlatformOperatorCom(Long comId) {
		if (comId == null || comId <= 0) {
			return false;
		}
		Optional<Long> operator = findOperatorComId();
		return operator.isPresent() && operator.get().equals(comId);
	}

	/**
	 * 是否参与平台运营数据面（可见运营主体名、平台运营 scope），与客户租户代管列表分离。
	 * 超管、受托/主企业为 operator com 的操作者、operator com 下任一有效站点管理员。
	 */
	public boolean canParticipateInPlatformOperatorDataPlane(Long userId) {
		if (userId == null) {
			return false;
		}
		if (this.commonOperate.isAdmin(userId)) {
			return true;
		}
		Optional<Long> operatorComId = findOperatorComId();
		if (!operatorComId.isPresent()) {
			return false;
		}
		Long opCom = operatorComId.get();
		Long sessionOperatorCom = this.commonOperate.getOperatorComId(userId);
		if (sessionOperatorCom != null && sessionOperatorCom.equals(opCom)) {
			return true;
		}
		if (!this.commonOperate.isDomainAdmin(userId)) {
			return false;
		}
		return hasActiveDomainAdminOnOperatorCom(userId, opCom);
	}

	/**
	 * 租户候选扁平列表：非平台运营数据面参与者不展示 platform-operator com。
	 */
	public List<WoCompany> applyTenantPickerVisibility(List<WoCompany> rows, Long viewerUserId) {
		if (rows == null || canParticipateInPlatformOperatorDataPlane(viewerUserId)) {
			return rows;
		}
		return CompanyService.excludePlatformOperatorCompany(rows, findOperatorComId().orElse(null));
	}

	/** 租户架构树：与 {@link #applyTenantPickerVisibility(List, Long)} 同一可见性规则。 */
	public void applyTenantPickerVisibilityOnTree(List<Company> treeRoots, Long viewerUserId) {
		if (treeRoots == null || canParticipateInPlatformOperatorDataPlane(viewerUserId)) {
			return;
		}
		CompanyService.excludePlatformOperatorFromCompanyTree(treeRoots, findOperatorComId().orElse(null));
	}

	public PlatformOperatorContextVO buildContext() {
		PlatformOperatorContextVO vo = new PlatformOperatorContextVO();
		Optional<Long> comIdOpt = findOperatorComId();
		if (!comIdOpt.isPresent()) {
			vo.setReady(false);
			return vo;
		}
		Long comId = comIdOpt.get();
		WoCompany company = this.companyDao.findById(comId).orElse(null);
		if (company == null || !ValidStatusEnum.VALID.toString().equals(company.getIsValid())
				|| !DeleteFlagEnum.NORMAL.toString().equals(company.getDeleteFlag())) {
			vo.setReady(false);
			return vo;
		}
		vo.setOperatorComId(comId);
		vo.setComName(company.getComName());

		Optional<Long> domainIdOpt = findPrimaryDomainId();
		if (!domainIdOpt.isPresent()) {
			vo.setReady(false);
			return vo;
		}
		WoDomain domain = this.domainDao.findById(domainIdOpt.get()).orElse(null);
		if (domain == null || !Objects.equals(domain.getComId(), comId)) {
			vo.setReady(false);
			return vo;
		}
		vo.setPrimaryDomainId(domain.getId());
		vo.setSiteDomain(domain.getSiteDomain());
		vo.setSiteName(domain.getSiteName());
		vo.setReady(true);
		return vo;
	}

	@Transactional(rollbackFor = Exception.class)
	public void upsertOperatorPointers(Long comId, Long domainId) {
		if (comId == null || comId <= 0 || domainId == null || domainId <= 0) {
			throw new BaseException("平台运营指针参数无效", ResultCode.VALIDATION_ERROR.getCode());
		}
		saveOption(SysOptionEnum.PLATFORM_OPERATOR_COM_ID, String.valueOf(comId));
		saveOption(SysOptionEnum.PLATFORM_OPERATOR_PRIMARY_DOMAIN_ID, String.valueOf(domainId));
	}

	private void saveOption(SysOptionEnum optionEnum, String value) {
		WoOptions row = this.optionsService.findByKey(optionEnum.getKey());
		if (row == null) {
			row = new WoOptions();
			row.setOptionKey(optionEnum.getKey());
			row.setOptionName(optionEnum.getTitle());
			row.setOptionType("normal");
			row.setAppCode("sys_option");
		}
		row.setOptionValue(value);
		this.optionsService.saveOrUpdate(row);
	}

	private boolean hasActiveDomainAdminOnOperatorCom(Long userId, Long operatorComId) {
		Optional<Long> primaryDomainId = findPrimaryDomainId();
		for (WoDomainAdmin row : this.domainAdminDao.findByUserId(userId)) {
			if (row == null || !ValidStatusEnum.VALID.toString().equals(row.getIsValid())
					|| !DeleteFlagEnum.NORMAL.toString().equals(row.getDeleteFlag())) {
				continue;
			}
			if (Objects.equals(row.getComId(), operatorComId)) {
				return true;
			}
			if (primaryDomainId.isPresent() && Objects.equals(row.getDomainId(), primaryDomainId.get())) {
				return true;
			}
		}
		return false;
	}

	private static Long parsePositiveLong(String raw) {
		if (ObjectUtils.isBlank(raw)) {
			return null;
		}
		try {
			long v = Long.parseLong(raw.trim());
			return v > 0 ? v : null;
		}
		catch (NumberFormatException ex) {
			return null;
		}
	}
}
