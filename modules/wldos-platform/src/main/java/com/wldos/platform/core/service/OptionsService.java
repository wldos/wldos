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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wldos.framework.mvc.service.EntityService;
import com.wldos.platform.core.enums.SysOptionEnum;
import com.wldos.platform.core.vo.AccountCenterTabDef;
import com.wldos.platform.core.vo.AdminHeaderHelpConfig;
import com.wldos.platform.core.vo.AdminHomeConfigVO;
import com.wldos.platform.core.vo.PortalFooterBlock;
import com.wldos.platform.core.entity.WoDomain;
import io.github.wldos.common.utils.ObjectUtils;
import com.wldos.platform.core.dao.OptionsDao;
import io.github.wldos.platform.support.system.entity.WoOptions;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统配置项service。
 *
 * @author Yuanxi Universe
 * @date 2021/7/13
 * @since 1.0
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OptionsService extends EntityService<OptionsDao, WoOptions, Long> {

	private static final ObjectMapper JSON = new ObjectMapper();

	@Autowired
	private DomainOptionService domainOptionService;

	@Autowired
	private DomainService domainService;

	/** 管理端首页 source 常量 */
	public static final String ADMIN_HOME_SOURCE_DOMAIN = "domain";
	public static final String ADMIN_HOME_SOURCE_ROOT = "rootDomain";
	public static final String ADMIN_HOME_SOURCE_GLOBAL = "global";
	public static final String ADMIN_HOME_SOURCE_BUILTIN = "builtin";

	/** 个人中心页签 type 白名单（与门户前端约定一致） */
	private static final Set<String> ACCOUNT_CENTER_TAB_TYPES;

	static {
		Set<String> t = new HashSet<>(8);
		t.add("info");
		t.add("book");
		t.add("applications");
		t.add("projects");
		t.add("permission");
		t.add("shortcuts");
		t.add("login_log");
		t.add("op_log");
		ACCOUNT_CENTER_TAB_TYPES = Collections.unmodifiableSet(t);
	}

	/**
	 * 根据配置key获取配置属性
	 *
	 * @param key 配置项键值
	 * @return 配置属性
	 */
	public String findSettingsByKey(String key) {
		// 配置应进缓存 todo 根据key值设置 修改此配置项时删掉对应key的缓存
		List<WoOptions> optionsList = this.entityRepo.findAllByOptionKey(key);
		if (ObjectUtils.isBlank(optionsList))
			return "";
		return optionsList.get(0).getOptionValue();
	}

	/**
	 * 根据key获取系统选项
	 *
	 * @param key 选项key
	 * @return 选项
	 */
	public WoOptions findByKey(String key) {
		List<WoOptions> options = this.entityRepo.findAllByOptionKey(key);
		return ObjectUtils.isBlank(options) ? null : options.get(0);
	}

	/**
	 * 门户顶栏搜索框下拉提示词。配置项见 {@link SysOptionEnum#PORTAL_SEARCH_HINTS}。
	 *
	 * @return 非 null；无配置或解析失败时为空列表（前端可回退到国际化默认文案）
	 */
	public List<String> findPortalSearchHints() {
		String raw = findSettingsByKey(SysOptionEnum.PORTAL_SEARCH_HINTS.getKey());
		if (ObjectUtils.isBlank(raw)) {
			return Collections.emptyList();
		}
		String trimmed = raw.trim();
		if (trimmed.startsWith("[")) {
			try {
				List<String> list = JSON.readValue(trimmed, new TypeReference<List<String>>() {});
				if (list == null) {
					return Collections.emptyList();
				}
				return list.stream().map(s -> s == null ? "" : s.trim()).filter(s -> !s.isEmpty()).collect(Collectors.toList());
			}
			catch (Exception e) {
				log.warn("portal_search_hints 按 JSON 解析失败，将按行拆分: {}", e.getMessage());
			}
		}
		List<String> lines = new ArrayList<>();
		for (String line : trimmed.split("\\r?\\n")) {
			String s = line.trim();
			if (!s.isEmpty()) {
				lines.add(s);
			}
		}
		return lines;
	}

	/**
	 * 门户个人中心页签：优先当前访问域的 {@code wo_domain_option.option_key=portal_account_center_tabs}，
	 * 无有效配置时回退全局 {@code wo_options}。
	 */
	public List<AccountCenterTabDef> findPortalAccountCenterTabs(Long domainId) {
		if (domainId != null) {
			String domainRaw = this.domainOptionService.findOptionValue(domainId, SysOptionEnum.PORTAL_ACCOUNT_CENTER_TABS.getKey());
			if (!ObjectUtils.isBlank(domainRaw)) {
				List<AccountCenterTabDef> parsed = parseAccountCenterTabs(domainRaw.trim());
				if (!parsed.isEmpty()) {
					return parsed;
				}
			}
		}
		return findPortalAccountCenterTabsGlobal();
	}

	/**
	 * 全局 {@code wo_options.portal_account_center_tabs}；无配置或解析失败返回空列表（前端回退 CMS 默认页签）。
	 */
	public List<AccountCenterTabDef> findPortalAccountCenterTabsGlobal() {
		String raw = findSettingsByKey(SysOptionEnum.PORTAL_ACCOUNT_CENTER_TABS.getKey());
		if (ObjectUtils.isBlank(raw)) {
			return Collections.emptyList();
		}
		return parseAccountCenterTabs(raw.trim());
	}

	/**
	 * 管理端顶栏「使用文档」：优先域级 {@code admin_header_help_*}，再回退全局 {@code wo_options}。
	 */
	public AdminHeaderHelpConfig findAdminHeaderHelp(Long domainId) {
		AdminHeaderHelpConfig cfg = new AdminHeaderHelpConfig();
		String enabledRaw = resolveOptionValue(domainId, SysOptionEnum.ADMIN_HEADER_HELP_ENABLED.getKey(), "true");
		cfg.setEnabled(!"false".equalsIgnoreCase(enabledRaw.trim()));
		String url = resolveOptionValue(domainId, SysOptionEnum.ADMIN_HEADER_HELP_URL.getKey(), "").trim();
		cfg.setUrl(url);
		if (!cfg.isEnabled() || url.isEmpty()) {
			cfg.setEnabled(false);
		}
		return cfg;
	}

	/**
	 * 管理端首页：本域 wo_domain 列 → 根域名主站列 → builtin（sys/monitor）。
	 */
	public AdminHomeConfigVO findAdminHomeConfig(Long domainId) {
		AdminHomeConfigVO vo = new AdminHomeConfigVO();
		vo.setRequestDomainId(domainId);

		AdminHomeParsed parsed = parseAdminHomeFromDomainRow(domainId);
		if (parsed != null) {
			fillAdminHomeVo(vo, parsed, ADMIN_HOME_SOURCE_DOMAIN, domainId);
			return vo;
		}

		Long rootDomainId = resolvePlatformRootDomainId();
		if (domainId != null && rootDomainId != null && !domainId.equals(rootDomainId)) {
			parsed = parseAdminHomeFromDomainRow(rootDomainId);
			if (parsed != null) {
				fillAdminHomeVo(vo, parsed, ADMIN_HOME_SOURCE_ROOT, rootDomainId);
				return vo;
			}
		}

		vo.setModule("builtin");
		vo.setUrl("sys/monitor");
		vo.setExtraProps("");
		vo.setTitle("");
		vo.setSource(ADMIN_HOME_SOURCE_BUILTIN);
		vo.setResolvedDomainId(null);
		return vo;
	}

	private Long resolvePlatformRootDomainId() {
		String plat = getPlatDomain();
		if (ObjectUtils.isBlank(plat)) {
			return null;
		}
		WoDomain root = this.domainService.queryDomainByName(plat.trim(), true);
		return root != null ? root.getId() : null;
	}

	private AdminHomeParsed parseAdminHomeFromDomainRow(Long domainId) {
		if (domainId == null) {
			return null;
		}
		WoDomain domain = this.domainService.findById(domainId);
		if (domain == null) {
			return null;
		}
		return parseAdminHomeJson(domain.getAdminHomeConfig());
	}

	private AdminHomeParsed parseAdminHomeJson(String raw) {
		if (ObjectUtils.isBlank(raw)) {
			return null;
		}
		try {
			Map<String, Object> m = JSON.readValue(raw.trim(), new TypeReference<Map<String, Object>>() {});
			if (m == null || m.isEmpty()) {
				return null;
			}
			String module = stringProp(m.get("module"));
			if ("builtin".equalsIgnoreCase(module)) {
				return null;
			}
			AdminHomeParsed p = new AdminHomeParsed();
			p.module = module.isEmpty() ? "component" : module;
			p.url = stringProp(m.get("url"));
			p.extraProps = m.get("extraProps") == null ? "" : m.get("extraProps").toString();
			p.title = stringProp(m.get("title"));
			if ("component".equalsIgnoreCase(p.module) && p.url.isEmpty()) {
				return null;
			}
			if ("redirect".equalsIgnoreCase(p.module) && p.url.isEmpty()) {
				return null;
			}
			return p;
		}
		catch (Exception e) {
			log.warn("admin_home_config JSON 解析失败: {}", e.getMessage());
			return null;
		}
	}

	private static String stringProp(Object o) {
		return o == null ? "" : o.toString().trim();
	}

	private static void fillAdminHomeVo(AdminHomeConfigVO vo, AdminHomeParsed parsed, String source, Long resolvedDomainId) {
		vo.setModule(parsed.module);
		vo.setUrl(parsed.url);
		vo.setExtraProps(parsed.extraProps == null ? "" : parsed.extraProps);
		vo.setTitle(parsed.title);
		vo.setSource(source);
		vo.setResolvedDomainId(resolvedDomainId);
	}

	private static final class AdminHomeParsed {
		String module;
		String url;
		String extraProps;
		String title;
	}

	private String resolveOptionValue(Long domainId, String key, String defaultValue) {
		if (domainId != null) {
			String domainVal = this.domainOptionService.findOptionValue(domainId, key);
			if (!ObjectUtils.isBlank(domainVal)) {
				return domainVal.trim();
			}
		}
		String global = findSettingsByKey(key);
		if (ObjectUtils.isBlank(global)) {
			return defaultValue == null ? "" : defaultValue;
		}
		return global.trim();
	}

	private List<AccountCenterTabDef> parseAccountCenterTabs(String raw) {
		if (ObjectUtils.isBlank(raw)) {
			return Collections.emptyList();
		}
		try {
			List<Map<String, Object>> arr = JSON.readValue(raw, new TypeReference<List<Map<String, Object>>>() {});
			if (arr == null || arr.isEmpty()) {
				return Collections.emptyList();
			}
			List<AccountCenterTabDef> out = new ArrayList<>(arr.size());
			Set<String> usedKeys = new HashSet<>(arr.size());
			int i = 0;
			for (Map<String, Object> m : arr) {
				if (m == null) {
					continue;
				}
				Object typeObj = m.get("type");
				String type = typeObj == null ? "" : typeObj.toString().trim();
				if (type.isEmpty() || !ACCOUNT_CENTER_TAB_TYPES.contains(type)) {
					log.warn("portal_account_center_tabs 跳过非法 type: {}", typeObj);
					continue;
				}
				AccountCenterTabDef def = new AccountCenterTabDef();
				def.setType(type);
				Object keyObj = m.get("key");
				String key = keyObj == null ? "" : keyObj.toString().trim();
				if (key.isEmpty()) {
					key = type + "-" + i;
				}
				String baseKey = key;
				int suf = 1;
				while (usedKeys.contains(key)) {
					key = baseKey + "-" + suf++;
				}
				usedKeys.add(key);
				def.setKey(key);
				Object titleObj = m.get("title");
				if (titleObj != null) {
					String title = titleObj.toString().trim();
					if (!title.isEmpty()) {
						def.setTitle(title);
					}
				}
				// requireManageSide：是否要求具备管理端菜单（权限能力）；个人中心 Tab 可见性主依赖下方鉴权与角色，与「当前访问用户端/管理端页面」无强绑定
				Object rms = m.get("requireManageSide");
				if (rms instanceof Boolean) {
					def.setRequireManageSide((Boolean) rms);
				}
				else if (rms != null) {
					String rs = rms.toString().trim();
					if (!rs.isEmpty()) {
						def.setRequireManageSide(Boolean.parseBoolean(rs));
					}
				}
				if (m.containsKey("resourcePaths")) {
					def.setResourcePaths(readStringListProp(m.get("resourcePaths")));
				}
				if (m.containsKey("authorityNeedAny")) {
					def.setAuthorityNeedAny(readStringListProp(m.get("authorityNeedAny")));
				}
				if (m.containsKey("roleNeedAny")) {
					def.setRoleNeedAny(readStringListProp(m.get("roleNeedAny")));
				}
				out.add(def);
				i++;
			}
			return out;
		}
		catch (Exception e) {
			log.warn("portal_account_center_tabs JSON 解析失败: {}", e.getMessage());
			return Collections.emptyList();
		}
	}

	private static List<String> readStringListProp(Object value) {
		List<String> out = new ArrayList<>();
		if (value instanceof List) {
			for (Object x : (List<?>) value) {
				if (x == null) {
					continue;
				}
				String s = x.toString().trim();
				if (!s.isEmpty()) {
					out.add(s);
				}
			}
		}
		return out;
	}

	/**
	 * 解析门户页脚 i18n 与 QR URL，写入 {@link Domain}（保留 wo_domain 存量 foot/flink/copy 作回退）。
	 */
	public void applyPortalFooter(com.wldos.platform.core.vo.Domain seo, Long domainId) {
		if (seo == null || domainId == null) {
			return;
		}
		WoDomain domain = this.domainService.findById(domainId);
		if (domain == null) {
			return;
		}
		String qr = domain.getPortalQrUrl();
		if (qr != null && !qr.trim().isEmpty()) {
			seo.setPortalQrUrl(qr.trim());
		}
		String i18nRaw = domain.getPortalFooterI18n();
		if (ObjectUtils.isBlank(i18nRaw)) {
			return;
		}
		try {
			Map<String, PortalFooterBlock> map = JSON.readValue(i18nRaw.trim(),
					new TypeReference<Map<String, PortalFooterBlock>>() {});
			if (map == null || map.isEmpty()) {
				return;
			}
			seo.setPortalFooterI18n(i18nRaw.trim());
		}
		catch (Exception e) {
			log.warn("portal_footer_i18n 解析失败: {}", e.getMessage());
		}
	}

	/** 开源默认页脚 i18n 模板（写入 wo_domain_option 时使用） */
	public static String defaultOpenSourceFooterI18nJson() {
		return "{"
				+ "\"zh-CN\":{\"foot\":\"<p>由 WLDOS 社区驱动 · 共建开放站点</p>\","
				+ "\"flink\":\"<a href=\\\"https://gitee.com/wldos/wldos\\\" target=\\\"_blank\\\" rel=\\\"noopener noreferrer\\\">WLDOS 开源仓库</a>\","
				+ "\"copy\":\"<span>© WLDOS Community</span>\"},"
				+ "\"en-US\":{\"foot\":\"<p>Powered by WLDOS Community</p>\","
				+ "\"flink\":\"<a href=\\\"https://gitee.com/wldos/wldos\\\" target=\\\"_blank\\\" rel=\\\"noopener noreferrer\\\">WLDOS on Gitee</a>\","
				+ "\"copy\":\"<span>© WLDOS Community</span>\"}"
				+ "}";
	}
}
