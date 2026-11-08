/*
 * Copyright (c) 2020 Yuanxi Universe. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.core.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.platform.core.dao.TermDao;
import com.wldos.platform.core.enums.ProvisionScenarioCodeEnum;
import com.wldos.platform.core.dao.DomainAppDao;
import com.wldos.platform.core.dao.DomainDao;
import com.wldos.platform.core.dao.DomainResourceDao;
import com.wldos.platform.core.entity.WoDomain;
import com.wldos.platform.core.enums.SysOptionEnum;
import io.github.wldos.common.util.HandoffLandingPathSupport;
import com.wldos.platform.core.vo.ProvisionScenarioDef;
import com.wldos.platform.core.vo.ProvisionScenarioView;
import io.github.wldos.common.Constants;
import io.github.wldos.common.enums.ValidStatusEnum;
import io.github.wldos.common.exception.BaseException;
import io.github.wldos.common.res.ResultCode;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.common.vo.SelectOption;
import io.github.wldos.platform.support.domain.IProvisionScenarioCloneOpener;
import io.github.wldos.platform.support.domain.ProvisionCloneOptions;
import io.github.wldos.platform.support.system.entity.WoOptions;

import io.github.wldos.platform.support.term.dto.Term;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 租户入驻场景登记与解析。
 *
 * @author Yuanxi Universe
 * @version 1.0
 * @since 3.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class ProvisionScenarioService {

	private static final ObjectMapper JSON = new ObjectMapper();

	private final OptionsService optionsService;

	private final DomainDao domainDao;

	private final DomainResourceDao domainResourceDao;

	private final DomainAppDao domainAppDao;

	private final DomainBootstrap domainBootstrap;

	private final IProvisionScenarioCloneOpener scenarioCloneOpener;

	private static final String SCENARIO_CODE_CLASS = "provision_scenario";

	private static final String SCENARIO_CODE_PATTERN = "^[a-z][a-z0-9_-]{0,47}$";

	@Value("${wldos.platform.template-domain-id:0}")
	private long configuredTemplateDomainId;

	@Autowired
	@Lazy
	private TermDao termDao;

	@Autowired
	public ProvisionScenarioService(
			OptionsService optionsService,
			DomainDao domainDao,
			DomainResourceDao domainResourceDao,
			DomainAppDao domainAppDao,
			DomainBootstrap domainBootstrap,
			@Lazy IProvisionScenarioCloneOpener scenarioCloneOpener) {
		this.optionsService = optionsService;
		this.domainDao = domainDao;
		this.domainResourceDao = domainResourceDao;
		this.domainAppDao = domainAppDao;
		this.domainBootstrap = domainBootstrap;
		this.scenarioCloneOpener = scenarioCloneOpener;
	}

	@Transactional(readOnly = true)
	public List<ProvisionScenarioView> listScenariosWithStatus() {
		List<ProvisionScenarioDef> defs = loadScenarioDefs();
		List<ProvisionScenarioView> views = new ArrayList<>(defs.size());
		for (ProvisionScenarioDef def : defs) {
			views.add(toView(def));
		}
		return views;
	}

	@Transactional(readOnly = true)
	public List<SelectOption> listEnabledScenarioOptions() {
		return loadScenarioDefs().stream()
				.filter(ProvisionScenarioDef::isEnabled)
				.map(def -> SelectOption.of(def.getLabel(), def.getCode()))
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public String resolveHandoffLandingPath(String scenarioCode) {
		ProvisionScenarioDef def = resolveScenario(scenarioCode);
		return HandoffLandingPathSupport.normalize(def.getHandoffLandingPath());
	}

	@Transactional(readOnly = true)
	public ProvisionScenarioDef resolveScenario(String scenarioCode) {
		List<ProvisionScenarioDef> defs = loadScenarioDefs();
		if (!ObjectUtils.isBlank(scenarioCode)) {
			for (ProvisionScenarioDef def : defs) {
				if (scenarioCode.trim().equals(def.getCode())) {
					return def;
				}
			}
			throw new BaseException("入驻场景不存在: " + scenarioCode.trim(), ResultCode.VALIDATION_ERROR.getCode());
		}
		for (ProvisionScenarioDef def : defs) {
		 if (def.isDefaultScenario()) {
			 return def;
		 }
		}
		if (!defs.isEmpty()) {
			return defs.get(0);
		}
		return buildFallbackScenario();
	}

	@Transactional(readOnly = true)
	public Set<Long> listTemplateDomainIds() {
		Set<Long> ids = new HashSet<>();
		for (ProvisionScenarioDef def : loadScenarioDefs()) {
			if (def.getTemplateDomainId() != null && def.getTemplateDomainId() > 0) {
				ids.add(def.getTemplateDomainId());
			}
		}
		if (this.configuredTemplateDomainId > 0) {
			ids.add(this.configuredTemplateDomainId);
		}
		return ids;
	}

	@Transactional(readOnly = true)
	public boolean isRegisteredTemplateDomain(Long domainId) {
		if (domainId == null || domainId <= 0) {
			return false;
		}
		return listTemplateDomainIds().contains(domainId);
	}

	public void saveScenarios(List<ProvisionScenarioDef> scenarios) {
		validateScenarioDefs(scenarios);
		String json;
		try {
			json = JSON.writeValueAsString(scenarios);
		}
		catch (Exception ex) {
			throw new BaseException("场景配置序列化失败", ResultCode.OPERATION_FAILED.getCode());
		}
		WoOptions row = this.optionsService.findByKey(SysOptionEnum.PLATFORM_PROVISION_SCENARIOS.getKey());
		if (row == null) {
			row = new WoOptions();
			row.setOptionKey(SysOptionEnum.PLATFORM_PROVISION_SCENARIOS.getKey());
			row.setOptionName(SysOptionEnum.PLATFORM_PROVISION_SCENARIOS.getTitle());
			row.setOptionType("normal");
			row.setAppCode("sys_option");
		}
		row.setOptionValue(json);
		this.optionsService.saveOrUpdate(row);
	}

	@Transactional(readOnly = true)
	public void validateTemplateReady(ProvisionScenarioDef scenario) {
		if (scenario == null || scenario.getTemplateDomainId() == null) {
			throw new BaseException("入驻场景未配置模板站", ResultCode.VALIDATION_ERROR.getCode());
		}
		WoDomain template = this.domainDao.findById(scenario.getTemplateDomainId()).orElse(null);
		if (template == null || !ValidStatusEnum.VALID.toString().equals(template.getIsValid())) {
			throw new BaseException("入驻场景模板站不存在或已停用: " + scenario.getTemplateDomainId(),
					ResultCode.VALIDATION_ERROR.getCode());
		}
		if (template.getComId() == null || template.getComId() != Constants.TOP_COM_ID) {
			throw new BaseException("入驻模板站须为平台站（com_id=0）: " + template.getSiteDomain(),
					ResultCode.VALIDATION_ERROR.getCode());
		}
		long resourceCount = this.domainResourceDao.countByDomainId(template.getId());
		long appCount = this.domainAppDao.countByDomainId(template.getId());
		if (resourceCount <= 0 || appCount <= 0) {
			StringBuilder msg = new StringBuilder("入驻场景「");
			msg.append(scenario.getLabel()).append("」模板站 ");
			msg.append(template.getSiteDomain()).append("(id=").append(template.getId()).append(") ");
			msg.append("挂载未就绪：域资源 ").append(resourceCount).append("、应用 ").append(appCount);
			msg.append("；请在站点管理为模板站配置应用与资源");
			throw new BaseException(msg.toString(), ResultCode.VALIDATION_ERROR.getCode());
		}
		ProvisionCloneOptions opts = normalizeCloneOptions(scenario.getCloneOptions());
		if (requiresCmsClone(opts) && !this.scenarioCloneOpener.supportsCmsClone()) {
			throw new BaseException("入驻场景「" + scenario.getLabel() + "」含 CMS 复制项，当前环境未加载 CMS 模块",
					ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	public void applyScenarioClone(long templateDomainId, long targetDomainId, long targetComId, ProvisionCloneOptions options) {
		ProvisionCloneOptions opts = normalizeCloneOptions(options);
		if (this.scenarioCloneOpener.supportsCmsClone()) {
			if (opts.isCopyDomainResource() || opts.isCopyMenu() || requiresCmsClone(opts)) {
				this.scenarioCloneOpener.cloneFromTemplate(templateDomainId, targetDomainId, opts);
			}
		}
		else if (requiresCmsClone(opts)) {
			throw new BaseException("入驻场景含 CMS 复制项，当前环境未加载 CMS 模块", ResultCode.VALIDATION_ERROR.getCode());
		}
		else {
			this.domainBootstrap.ensureDefaultDomainResourceMount(targetDomainId, templateDomainId);
		}
		this.domainBootstrap.ensureDefaultDomainAppMount(targetComId, targetDomainId, templateDomainId);
	}

	private List<ProvisionScenarioDef> loadScenarioDefs() {
		String raw = this.optionsService.findSettingsByKey(SysOptionEnum.PLATFORM_PROVISION_SCENARIOS.getKey());
		if (ObjectUtils.isBlank(raw)) {
			return defaultScenarioDefs();
		}
		try {
			List<ProvisionScenarioDef> list = JSON.readValue(raw.trim(), new TypeReference<List<ProvisionScenarioDef>>() {});
			if (list == null || list.isEmpty()) {
				return defaultScenarioDefs();
			}
			return list;
		}
		catch (Exception ex) {
			log.warn("[provision.scenario] parse failed, use defaults: {}", ex.getMessage());
			return defaultScenarioDefs();
		}
	}

	private List<ProvisionScenarioDef> defaultScenarioDefs() {
		Long templateId = this.domainBootstrap.resolveFallbackTemplateDomainId();
		if (templateId == null) {
			return Collections.emptyList();
		}
		List<ProvisionScenarioDef> list = new ArrayList<>(3);
		list.add(buildScenario("standard", "标准 SaaS", templateId, standardOptions(), true));
		list.add(buildScenario("cms-portal", "内容门户", templateId, cmsPortalOptions(), false));
		list.add(buildScenario("full-mirror", "完整镜像", templateId, fullMirrorOptions(), false));
		return list;
	}

	private ProvisionScenarioDef buildFallbackScenario() {
		Long templateId = this.domainBootstrap.resolveFallbackTemplateDomainId();
		if (templateId == null) {
			throw new BaseException("未配置入驻场景且无法解析平台模板站", ResultCode.VALIDATION_ERROR.getCode());
		}
		return buildScenario("standard", "标准 SaaS", templateId, standardOptions(), true);
	}

	private static ProvisionScenarioDef buildScenario(
			String code, String label, Long templateDomainId, ProvisionCloneOptions options, boolean defaultScenario) {
		ProvisionScenarioDef def = new ProvisionScenarioDef();
		def.setCode(code);
		def.setLabel(label);
		def.setTemplateDomainId(templateDomainId);
		def.setCloneOptions(options);
		def.setDefaultScenario(defaultScenario);
		def.setEnabled(true);
		return def;
	}

	private static ProvisionCloneOptions standardOptions() {
		ProvisionCloneOptions opts = new ProvisionCloneOptions();
		opts.setCopyMenu(true);
		opts.setCopyDomainResource(true);
		return opts;
	}

	private static ProvisionCloneOptions cmsPortalOptions() {
		ProvisionCloneOptions opts = standardOptions();
		opts.setCopyPages(true);
		opts.setCopyDocs(true);
		opts.setCopyTermBindings(true);
		return opts;
	}

	private static ProvisionCloneOptions fullMirrorOptions() {
		ProvisionCloneOptions opts = cmsPortalOptions();
		opts.setCopyMenu(true);
		opts.setCopyDomainResource(true);
		return opts;
	}

	private void validateScenarioDefs(List<ProvisionScenarioDef> scenarios) {
		if (scenarios == null || scenarios.isEmpty()) {
			throw new BaseException("至少保留一个入驻场景", ResultCode.VALIDATION_ERROR.getCode());
		}
		Set<String> codes = new HashSet<>();
		int defaultCount = 0;
		for (ProvisionScenarioDef def : scenarios) {
			if (def == null || ObjectUtils.isBlank(def.getCode()) || ObjectUtils.isBlank(def.getLabel())) {
				throw new BaseException("场景编码与名称不能为空", ResultCode.VALIDATION_ERROR.getCode());
			}
			String code = def.getCode().trim();
			assertValidScenarioCode(code);
			if (!codes.add(code)) {
				throw new BaseException("场景编码重复: " + code, ResultCode.VALIDATION_ERROR.getCode());
			}
			if (def.getTemplateDomainId() == null || def.getTemplateDomainId() <= 0) {
				throw new BaseException("场景 " + code + " 须指定模板站", ResultCode.VALIDATION_ERROR.getCode());
			}
			WoDomain template = this.domainDao.findById(def.getTemplateDomainId()).orElse(null);
			if (template == null || template.getComId() == null || template.getComId() != Constants.TOP_COM_ID) {
				throw new BaseException("场景 " + code + " 的模板站须为有效平台站", ResultCode.VALIDATION_ERROR.getCode());
			}
			if (def.isDefaultScenario()) {
				defaultCount++;
			}
			def.setCode(code);
			def.setLabel(def.getLabel().trim());
			def.setCloneOptions(normalizeCloneOptions(def.getCloneOptions()));
			def.setHandoffLandingPath(HandoffLandingPathSupport.normalize(def.getHandoffLandingPath()));
		}
		if (defaultCount != 1) {
			throw new BaseException("须且仅能指定一个默认入驻场景", ResultCode.VALIDATION_ERROR.getCode());
		}
	}

	private void assertValidScenarioCode(String code) {
		if (ObjectUtils.isBlank(code)) {
			throw new BaseException("场景编码不能为空", ResultCode.VALIDATION_ERROR.getCode());
		}
		String slug = code.trim();
		if (!slug.matches(SCENARIO_CODE_PATTERN)) {
			throw new BaseException("场景编码须为小写字母、数字、下划线或连字符，且以字母开头", ResultCode.VALIDATION_ERROR.getCode());
		}
		if (this.termDao != null) {
			List<Term> terms = this.termDao.findAllByClassType(SCENARIO_CODE_CLASS);
			if (terms != null && !terms.isEmpty()) {
				boolean ok = terms.stream().anyMatch(t -> slug.equals(t.getSlug()));
				if (!ok) {
					throw new BaseException("场景编码未在类型管理（provision_scenario）中维护: " + slug,
							ResultCode.VALIDATION_ERROR.getCode());
				}
				return;
			}
		}
		for (ProvisionScenarioCodeEnum item : ProvisionScenarioCodeEnum.values()) {
			if (item.getValue().equals(slug)) {
				return;
			}
		}
		throw new BaseException("场景编码无效: " + slug, ResultCode.VALIDATION_ERROR.getCode());
	}

	private ProvisionScenarioView toView(ProvisionScenarioDef def) {
		ProvisionScenarioView view = new ProvisionScenarioView();
		view.setCode(def.getCode());
		view.setLabel(def.getLabel());
		view.setTemplateDomainId(def.getTemplateDomainId());
		view.setCloneOptions(normalizeCloneOptions(def.getCloneOptions()));
		view.setDefaultScenario(def.isDefaultScenario());
		view.setEnabled(def.isEnabled());
		view.setHandoffLandingPath(def.getHandoffLandingPath());
		view.setCloneSummary(buildCloneSummary(view.getCloneOptions()));
		if (def.getTemplateDomainId() != null) {
			WoDomain template = this.domainDao.findById(def.getTemplateDomainId()).orElse(null);
			if (template != null) {
				view.setTemplateSiteDomain(template.getSiteDomain());
				view.setTemplateSiteName(template.getSiteName());
			}
			long resourceCount = this.domainResourceDao.countByDomainId(def.getTemplateDomainId());
			long appCount = this.domainAppDao.countByDomainId(def.getTemplateDomainId());
			view.setResourceCount(resourceCount);
			view.setAppCount(appCount);
			view.setReady(resourceCount > 0 && appCount > 0);
		}
		return view;
	}

	private static ProvisionCloneOptions normalizeCloneOptions(ProvisionCloneOptions options) {
		return options != null ? options : new ProvisionCloneOptions();
	}

	private static boolean requiresCmsClone(ProvisionCloneOptions options) {
		return options.isCopyPages()
				|| options.isCopyDocs()
				|| options.isCopyTermBindings()
				|| options.isCopySiteConfig()
				|| options.isCopyDomainOptions();
	}

	private static String buildCloneSummary(ProvisionCloneOptions opts) {
		List<String> parts = new ArrayList<>();
		if (opts.isCopyDomainResource()) {
			parts.add("域资源");
		}
		if (opts.isCopyMenu()) {
			parts.add("菜单");
		}
		if (opts.isCopyPages()) {
			parts.add("页面");
		}
		if (opts.isCopyDocs()) {
			parts.add("文档");
		}
		if (opts.isCopyTermBindings()) {
			parts.add("分类标签");
		}
		if (opts.isCopySiteConfig()) {
			parts.add("站点配置");
		}
		if (opts.isCopyDomainOptions()) {
			parts.add("域参数");
		}
		return parts.isEmpty() ? "仅应用挂载" : String.join("、", parts);
	}
}
