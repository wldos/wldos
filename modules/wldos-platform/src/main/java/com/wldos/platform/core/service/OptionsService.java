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
}
