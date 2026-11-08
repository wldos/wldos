/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import com.wldos.framework.mvc.service.NonEntityService;
import com.wldos.platform.auth.service.LoginAuthService;
import com.wldos.platform.auth.vo.Login;
import com.wldos.platform.auth.vo.Register;
import com.wldos.platform.core.dao.ArchitectureDao;
import com.wldos.platform.core.dao.CompanyDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.UserDao;
import com.wldos.platform.core.entity.WoArchitecture;
import com.wldos.platform.core.entity.WoCompany;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.entity.WoUser;
import com.wldos.platform.core.vo.ProvisionScenarioDef;
import com.wldos.platform.core.vo.TenantAdminAccountBody;
import com.wldos.platform.core.vo.TenantProvisionBody;
import com.wldos.platform.core.vo.TenantProvisionResult;
import io.github.wldos.common.Constants;
import io.github.wldos.common.util.HandoffLandingPathSupport;
import io.github.wldos.common.enums.DeleteFlagEnum;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.rbac.slot.BusinessSlot;
import io.github.wldos.platform.support.rbac.slot.SlotResolver;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 租户入驻单事务编排：公司 + 体系 + 主站 + bootstrap + 租户管。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TenantProvisionService extends NonEntityService {

	private static final Pattern SECOND_DOMAIN_PREFIX = Pattern.compile("^[a-z]{1,10}$");

	private final CompanyDao companyDao;

	private final ArchitectureDao architectureDao;

	private final DomainDao domainDao;

	private final DomainService domainService;

	private final CompanyService companyService;

	private final LoginAuthService loginAuthService;

	private final SlotResolver slotResolver;

	private final ProvisionScenarioService provisionScenarioService;

	private final UserDao userDao;

	public TenantProvisionService(
			CompanyDao companyDao,
			ArchitectureDao architectureDao,
			DomainDao domainDao,
			DomainService domainService,
			CompanyService companyService,
			LoginAuthService loginAuthService,
			SlotResolver slotResolver,
			ProvisionScenarioService provisionScenarioService,
			UserDao userDao) {
		this.companyDao = companyDao;
		this.architectureDao = architectureDao;
		this.domainDao = domainDao;
		this.domainService = domainService;
		this.companyService = companyService;
		this.loginAuthService = loginAuthService;
		this.slotResolver = slotResolver;
		this.provisionScenarioService = provisionScenarioService;
		this.userDao = userDao;
	}

	public TenantProvisionResult provision(TenantProvisionBody body, Long operatorId, String operatorIp) {
		validate(body);
		ProvisionScenarioDef scenario = this.provisionScenarioService.resolveScenario(body.getProvisionScenarioCode());
		this.provisionScenarioService.validateTemplateReady(scenario);
		WoDomain templateDomain = this.domainDao.findById(scenario.getTemplateDomainId()).orElse(null);
		String templateSiteDomain = templateDomain != null ? templateDomain.getSiteDomain() : null;

		WoCompany company = new WoCompany();
		company.setComCode(body.getComCode().trim());
		company.setComName(body.getComName().trim());
		company.setComDesc(body.getComDesc());
		company.setParentId(Constants.TOP_COM_ID);
		company.setDisplayOrder(1L);
		company.setIsValid(ValidStatusEnum.VALID.toString());
		this.companyDao.saveOrUpdate(company);
		if (company.getId() == null) {
			throw new BaseException("创建公司失败", ResultCode.OPERATION_FAILED.getCode());
		}

		WoArchitecture arch = new WoArchitecture();
		arch.setComId(company.getId());
		arch.setArchCode(ObjectUtils.isBlank(body.getArchCode()) ? body.getComCode().trim() + "-arch" : body.getArchCode().trim());
		arch.setArchName(ObjectUtils.isBlank(body.getArchName()) ? body.getComName().trim() + "体系" : body.getArchName().trim());
		arch.setParentId(Constants.TOP_ARCH_ID);
		arch.setDisplayOrder(1L);
		arch.setIsValid(ValidStatusEnum.VALID.toString());
		arch.setDeleteFlag(DeleteFlagEnum.NORMAL.toString());
		this.architectureDao.saveOrUpdate(arch);

		String siteDomain = body.getSiteDomain().trim().toLowerCase();
		WoDomain existing = this.domainDao.findBySiteDomainAndIsValidAndDeleteFlag(
				siteDomain, ValidStatusEnum.VALID.toString(), DeleteFlagEnum.NORMAL.toString());
		if (existing != null) {
			throw new BaseException("主站域名已被占用: " + siteDomain, ResultCode.VALIDATION_ERROR.getCode());
		}

		String siteName = ObjectUtils.isBlank(body.getSiteName()) ? body.getComName().trim() : body.getSiteName().trim();
		WoDomain domain = new WoDomain();
		domain.setComId(company.getId());
		domain.setSiteDomain(siteDomain);
		domain.setSiteName(siteName);
		domain.setSecondDomain(resolveSecondDomain(body.getComCode(), siteDomain, getPlatDomain()));
		applySeoDefaults(domain, body.getComName().trim(), siteName);
		String siteUrl = resolveSiteUrl(body, siteDomain);
		domain.setSiteUrl(siteUrl);
		domain.setDisplayOrder(1L);
		domain.setIsValid(ValidStatusEnum.VALID.toString());
		this.domainDao.saveOrUpdate(domain);
		this.domainService.provisionSystemKindSeed(domain);
		this.provisionScenarioService.applyScenarioClone(
				scenario.getTemplateDomainId(),
				domain.getId(),
				company.getId(),
				scenario.getCloneOptions());
		this.domainService.assertSiteMountsReady(domain.getId(), scenario.getTemplateDomainId(), templateSiteDomain);
		this.domainService.refreshDomain(domain);

		String adminMessage = null;
		Long tenantAdminUserId = null;
		String tenantAdminLoginName = null;
		if (body.getTenantAdminAccount() != null && hasAnyAccountField(body.getTenantAdminAccount())) {
			TenantAdminAccountBody account = body.getTenantAdminAccount();
			tenantAdminLoginName = account.getLoginName() != null ? account.getLoginName().trim() : null;
			tenantAdminUserId = createTenantAdminAccount(
					account, company.getId(), arch.getId(), domain, operatorId, operatorIp);
			adminMessage = this.companyService.addTenantAdmin(
					Collections.singletonList(String.valueOf(tenantAdminUserId)),
					company.getId(), operatorId, operatorIp);
		}
		else {
			List<String> tenantAdminUserIds = body.getTenantAdminUserIds();
			if (tenantAdminUserIds != null && !tenantAdminUserIds.isEmpty()) {
				adminMessage = this.companyService.addTenantAdmin(tenantAdminUserIds, company.getId(), operatorId, operatorIp);
				tenantAdminUserId = Long.parseLong(tenantAdminUserIds.get(0));
				tenantAdminLoginName = resolveLoginName(tenantAdminUserId);
			}
		}
		if (body.getDomainAdminUserId() != null && body.getDomainAdminUserId() > 0) {
			this.domainService.bindDomainAdmin(domain.getId(), body.getDomainAdminUserId(), company.getId(), operatorId, operatorIp);
		}

		TenantProvisionResult result = new TenantProvisionResult();
		result.setComId(company.getId());
		result.setArchId(arch.getId());
		result.setDomainId(domain.getId());
		result.setAdminUrl(buildAdminUrl(siteUrl));
		result.setTenantAdminUserId(tenantAdminUserId);
		result.setTenantAdminLoginName(tenantAdminLoginName);
		result.setMessage(adminMessage);
		return result;
	}

	private String resolveLoginName(Long userId) {
		if (userId == null || userId <= 0) {
			return null;
		}
		WoUser user = this.userDao.findById(userId).orElse(null);
		if (user == null || ObjectUtils.isBlank(user.getLoginName())) {
			return null;
		}
		return user.getLoginName().trim();
	}

	private long createTenantAdminAccount(
			TenantAdminAccountBody account,
			Long comId,
			Long archId,
			WoDomain domain,
			Long operatorId,
			String operatorIp) {
		validateTenantAdminAccount(account);
		Long memberOrgId = this.slotResolver.resolveOrg(BusinessSlot.TENANT_FIRST_ADMIN_ORG.getSlotCode(),
				comId, domain.getId());
		if (memberOrgId == null) {
			throw new BaseException("站点会员组织未就绪，无法创建首管", ResultCode.OPERATION_FAILED.getCode());
		}

		Register register = new Register();
		long userId = this.nextId();
		register.setId(userId);
		register.setRegisterIp(operatorIp);
		register.setLoginName(account.getLoginName().trim());
		register.setEmail(account.getEmail().trim());
		register.setNickname(account.getNickname().trim());
		register.setPasswd(account.getPasswd());
		register.setConfirm(account.getConfirm());
		register.setComId(comId);
		register.setArchId(archId);
		register.setOrgId(memberOrgId);

		Login login = this.loginAuthService.addUser4admin(domain.getId(), comId, operatorId, register, false);
		if (login == null || !"ok".equals(login.getStatus())) {
			String reason = login != null && !ObjectUtils.isBlank(login.getNews()) ? login.getNews() : "创建首管账号失败";
			throw new BaseException(reason, ResultCode.VALIDATION_ERROR.getCode());
		}
		return userId;
	}

	private void validate(TenantProvisionBody body) {
		if (body == null || ObjectUtils.isBlank(body.getComCode()) || ObjectUtils.isBlank(body.getComName())
				|| ObjectUtils.isBlank(body.getSiteDomain())) {
			throw new BaseException("公司编码、名称与主站域名为必填项", ResultCode.VALIDATION_ERROR.getCode());
		}
		boolean hasAccount = body.getTenantAdminAccount() != null && hasAnyAccountField(body.getTenantAdminAccount());
		boolean hasExisting = body.getTenantAdminUserIds() != null && !body.getTenantAdminUserIds().isEmpty();
		if (hasAccount && hasExisting) {
			throw new BaseException("创建首管与指定已有用户不能同时填写", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (hasAccount) {
			validateTenantAdminAccount(body.getTenantAdminAccount());
		}
		validateSiteAccess(body);
	}

	private void validateSiteAccess(TenantProvisionBody body) {
		if (body.getSitePort() != null && (body.getSitePort() < 1 || body.getSitePort() > 65535)) {
			throw new BaseException("访问端口须在 1-65535 之间", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!ObjectUtils.isBlank(body.getSiteScheme())) {
			String scheme = body.getSiteScheme().trim().toLowerCase();
			if (!"http".equals(scheme) && !"https".equals(scheme)) {
				throw new BaseException("访问协议仅支持 http 或 https", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
	}

	private String resolveSiteUrl(TenantProvisionBody body, String siteDomain) {
		if (!ObjectUtils.isBlank(body.getSiteUrl())) {
			return body.getSiteUrl().trim();
		}
		String scheme = normalizeSiteScheme(body.getSiteScheme(), body.getSitePort());
		Integer port = body.getSitePort();
		if (port == null || port == 80) {
			return scheme + "://" + siteDomain;
		}
		if (port == 443) {
			return "https://" + siteDomain;
		}
		return scheme + "://" + siteDomain + ":" + port;
	}

	private String normalizeSiteScheme(String siteScheme, Integer sitePort) {
		if (sitePort != null && sitePort == 443) {
			return "https";
		}
		if (ObjectUtils.isBlank(siteScheme)) {
			return "http";
		}
		String scheme = siteScheme.trim().toLowerCase();
		return "https".equals(scheme) ? "https" : "http";
	}

	private boolean hasAnyAccountField(TenantAdminAccountBody account) {
		return !ObjectUtils.isBlank(account.getLoginName())
				|| !ObjectUtils.isBlank(account.getEmail())
				|| !ObjectUtils.isBlank(account.getNickname())
				|| !ObjectUtils.isBlank(account.getPasswd())
				|| !ObjectUtils.isBlank(account.getConfirm());
	}

	private void validateTenantAdminAccount(TenantAdminAccountBody account) {
		if (account == null) {
			return;
		}
		if (ObjectUtils.isBlank(account.getLoginName()) || ObjectUtils.isBlank(account.getEmail())
				|| ObjectUtils.isBlank(account.getNickname())
				|| ObjectUtils.isBlank(account.getPasswd()) || ObjectUtils.isBlank(account.getConfirm())) {
			throw new BaseException("创建首管需填写登录账号、邮箱、昵称、密码与确认密码", ResultCode.VALIDATION_ERROR.getCode());
		}
		int loginLen = account.getLoginName().trim().length();
		boolean emailAsLogin = account.getLoginName().trim().contains("@");
		if (emailAsLogin) {
			if (loginLen > 240) {
				throw new BaseException("登录账号（邮箱）长度不能超过 240", ResultCode.VALIDATION_ERROR.getCode());
			}
		}
		else if (loginLen > 30) {
			throw new BaseException("登录账号长度不能超过 30", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!account.getEmail().trim().contains("@")) {
			throw new BaseException("邮箱格式不正确", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (!Objects.equals(account.getPasswd(), account.getConfirm())) {
			throw new BaseException("两次输入的密码不一致", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (account.getPasswd().length() < 6 || account.getPasswd().length() > 100) {
			throw new BaseException("密码长度须在 6-100 之间", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (account.getNickname().length() > 50) {
			throw new BaseException("昵称长度不能超过 50", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private String buildAdminUrl(String siteUrl) {
		if (ObjectUtils.isBlank(siteUrl)) {
			return null;
		}
		return HandoffLandingPathSupport.buildSiteEntryUrl(siteUrl, null, null);
	}

	private String resolveSecondDomain(String comCode, String siteDomain, String platDomain) {
		if (ObjectUtils.isBlank(platDomain) || siteDomain.equals(platDomain)) {
			return null;
		}
		String platSuffix = "." + platDomain;
		if (siteDomain.endsWith(platSuffix) && siteDomain.length() > platSuffix.length()) {
			String prefix = siteDomain.substring(0, siteDomain.length() - platSuffix.length());
			if (isValidSecondDomainPrefix(prefix)) {
				return allocateUniqueSecondDomain(prefix);
			}
			return null;
		}
		String fromComCode = sanitizeSecondDomainPrefix(comCode);
		if (fromComCode == null) {
			return null;
		}
		return allocateUniqueSecondDomain(fromComCode);
	}

	private void applySeoDefaults(WoDomain domain, String comName, String siteName) {
		domain.setSiteTitle(siteName);
		domain.setSiteKeyword(comName);
		domain.setSiteDescription(comName);
	}

	private boolean isValidSecondDomainPrefix(String prefix) {
		return prefix != null && SECOND_DOMAIN_PREFIX.matcher(prefix).matches();
	}

	private String sanitizeSecondDomainPrefix(String comCode) {
		if (ObjectUtils.isBlank(comCode)) {
			return null;
		}
		StringBuilder letters = new StringBuilder();
		for (char c : comCode.toLowerCase().toCharArray()) {
			if (c >= 'a' && c <= 'z') {
				letters.append(c);
				if (letters.length() >= 10) {
					break;
				}
			}
		}
		return letters.length() >= 1 ? letters.toString() : null;
	}

	private String allocateUniqueSecondDomain(String prefix) {
		if (!isValidSecondDomainPrefix(prefix)) {
			return null;
		}
		if (isSecondDomainAvailable(prefix)) {
			return prefix;
		}
		for (int i = 1; i <= 9; i++) {
			String suffix = String.valueOf(i);
			String candidate;
			if (prefix.length() + suffix.length() > 10) {
				candidate = prefix.substring(0, 10 - suffix.length()) + suffix;
			}
			else {
				candidate = prefix + suffix;
			}
			if (isValidSecondDomainPrefix(candidate) && isSecondDomainAvailable(candidate)) {
				return candidate;
			}
		}
		return null;
	}

	private boolean isSecondDomainAvailable(String secondDomain) {
		WoDomain occupied = this.domainDao.findBySecondDomain(secondDomain);
		return occupied == null;
	}
}
