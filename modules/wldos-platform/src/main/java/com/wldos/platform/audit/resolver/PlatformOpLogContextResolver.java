/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by 元悉宇宙 (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.resolver;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.wldos.framework.common.CommonOperation;
import io.github.wldos.framework.support.audit.IOpLogContextResolver;
import io.github.wldos.framework.support.audit.ResourceTypeDict;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 平台模块（{@code wldos-platform-core}）的操作日志名称解析器。
 *
 * <p>覆盖 {@code @OpLog} 中常见的平台资源类型（用户 / 角色 / 组织 / 租户 /
 * 域 / 菜单 / 分类项 / 节假日），每种类型一次 IN 查询批量解析。
 *
 * <p>实现要点：
 * <ul>
 *   <li>所有 SQL 都走 {@link JdbcOperations}，避免引入业务 service 间的循环依赖；</li>
 *   <li>{@code wo_*} 表统一带 {@code is_valid='1' AND delete_flag='normal'} 软删过滤；
 *       已被删除的实体审计时退化为 {@code resourceId} 直显，不强行翻译；</li>
 *   <li>用户名优先取 {@code login_name}（与登录入参一致），老数据缺失时退到 {@code username};</li>
 *   <li>{@code resourceId} 在审计表里是 {@code String}，转 {@code Long} 时遇到非数字静默跳过，
 *       避免一条脏数据让整页解析失败。</li>
 * </ul>
 *
 * @author 元悉宇宙
 * @date 2026/05/05
 * @version 1.0
 */
@Slf4j
@Component
public class PlatformOpLogContextResolver implements IOpLogContextResolver {

	@SuppressWarnings("all")
	@Autowired
	@Lazy // 此工具类在系统启动后才加载
	private CommonOperation commonOperation;

	@Override
	public Set<String> supportedResourceTypes() {
		return PlatformResourceType.supportedCodes();
	}

	@Override
	public List<ResourceTypeDict> resourceTypeLabels() {
		return PlatformResourceType.dict();
	}

	@Override
	public Map<Long, String> resolveUserNames(Set<Long> userIds) {
		if (userIds == null || userIds.isEmpty()) {
			return Collections.emptyMap();
		}
		String sql = "SELECT id, COALESCE(NULLIF(login_name,''), NULLIF(username,''), nickname) AS name"
				+ " FROM wo_user WHERE delete_flag='normal' AND id IN (" + placeholders(userIds.size()) + ")";
		try {
			List<Map<String, Object>> rows = commonOperation.getJdbcOperations()
					.queryForList(sql, userIds.toArray());
			Map<Long, String> out = new HashMap<>(rows.size() * 2);
			for (Map<String, Object> r : rows) {
				Object idObj = r.get("id");
				Object nameObj = r.get("name");
				if (idObj == null || nameObj == null) continue;
				out.put(((Number) idObj).longValue(), String.valueOf(nameObj));
			}
			return out;
		}
		catch (Exception ex) {
			log.warn("resolveUserNames failed: {}", ex.getMessage());
			return Collections.emptyMap();
		}
	}

	@Override
	public Map<String, Map<String, String>> resolveResourceNames(Map<String, Set<String>> typeToIds) {
		if (typeToIds == null || typeToIds.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<String, Map<String, String>> out = new HashMap<>();
		for (Map.Entry<String, Set<String>> entry : typeToIds.entrySet()) {
			String code = entry.getKey();
			Set<String> ids = entry.getValue();
			if (ids == null || ids.isEmpty()) {
				continue;
			}
			PlatformResourceType type = PlatformResourceType.ofCode(code);
			if (type == null) {
				// 不属于本 resolver 声明的类型，忽略不越权解析
				continue;
			}
			Map<String, String> resolved = lookupByType(type, ids);
			if (!resolved.isEmpty()) {
				out.put(code, resolved);
			}
		}
		return out;
	}

	/**
	 * 按枚举类型分发到对应业务表查询；switch on enum 让"漏 case"在编译期暴露，
	 * 避免字符串字面量改名后悄悄退化到 default 拿不到名字。
	 */
	private Map<String, String> lookupByType(PlatformResourceType type, Set<String> rawIds) {
		Set<Long> ids = toLongIds(rawIds);
		if (ids.isEmpty()) {
			return Collections.emptyMap();
		}
		switch (type) {
			case APP:
				return queryNames("SELECT id, app_name AS name FROM wo_app"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case ARCHITECTURE:
				return queryNames("SELECT id, arch_name AS name FROM wo_architecture"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case USER:
			// 个人中心子模块：resourceId 都是当前用户 id，统一回 wo_user 查名字
			case USER_PROFILE:
			case USER_TAGS:
			case USER_SECURITY:
			case USER_BIND:
			case USER_NOTICE:
				return queryNames("SELECT id, COALESCE(NULLIF(login_name,''), NULLIF(username,''), nickname) AS name"
								+ " FROM wo_user WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case ROLE:
				return queryNames("SELECT id, role_name AS name FROM wo_role"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case ORG:
				return queryNames("SELECT id, org_name AS name FROM wo_org"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case COMPANY:
				return queryNames("SELECT id, com_name AS name FROM wo_company"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case DOMAIN:
				return queryNames("SELECT id, COALESCE(NULLIF(site_name,''), site_domain) AS name FROM wo_domain"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case RESOURCE:
				return queryNames("SELECT id, resource_name AS name FROM wo_resource"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case REGION:
				return queryNames("SELECT id, name FROM wo_region"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case CATEGORY:
			case TAG:
			case TERM:
				return queryNames("SELECT id, name FROM k_terms"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case TERM_TYPE:
				// k_term_type 主键 type_code 本身就是英文 code（category/tag/post_format 等），
				// 前端把 resourceId 原样展示即可，不强行查 type_name。
				return Collections.emptyMap();
			case CALENDAR_HOLIDAY:
				return queryNames("SELECT id, name FROM wo_calendar_holiday"
								+ " WHERE delete_flag='normal' AND id IN (" + placeholders(ids.size()) + ")",
						ids);
			case OPTION:
				// 系统配置批量提交（一次改 N 个 key），不存在单数实体 ID → 名称的映射；
				// 资源类型列展示"系统选项"已足够，"资源"列保持空白即可。
				return Collections.emptyMap();
			default:
				return Collections.emptyMap();
		}
	}

	/* ============================================================
	 *  内部小工具
	 * ============================================================ */

	private Map<String, String> queryNames(String sql, Set<Long> ids) {
		try {
			List<Map<String, Object>> rows = commonOperation.getJdbcOperations()
					.queryForList(sql, ids.toArray());
			Map<String, String> out = new HashMap<>(rows.size() * 2);
			for (Map<String, Object> r : rows) {
				Object idObj = r.get("id");
				Object nameObj = r.get("name");
				if (idObj == null || nameObj == null) continue;
				// 审计表里的 resource_id 是字符串，回填映射 key 也用字符串保持一致
				out.put(String.valueOf(idObj), String.valueOf(nameObj));
			}
			return out;
		}
		catch (Exception ex) {
			log.warn("queryNames failed: {} - {}", sql, ex.getMessage());
			return Collections.emptyMap();
		}
	}

	/**
	 * 把 {@code String} id 集合转成 {@code Long}；非数字 id 静默跳过，避免一条脏数据让整批失败。
	 */
	private static Set<Long> toLongIds(Set<String> rawIds) {
		return rawIds.stream()
				.map(s -> {
					try { return Long.parseLong(s); }
					catch (NumberFormatException ex) { return null; }
				})
				.filter(java.util.Objects::nonNull)
				.collect(Collectors.toSet());
	}

	/** 生成 IN 子句的占位符串："?,?,?"。 */
	private static String placeholders(int n) {
		StringBuilder sb = new StringBuilder(n * 2);
		for (int i = 0; i < n; i++) {
			if (i > 0) sb.append(',');
			sb.append('?');
		}
		return sb.toString();
	}
}
