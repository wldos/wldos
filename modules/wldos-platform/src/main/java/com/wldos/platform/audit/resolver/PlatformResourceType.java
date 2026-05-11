/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */
package com.wldos.platform.audit.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.wldos.framework.support.audit.ResourceTypeDict;

/**
 * 平台模块（{@code wldos-platform-core}）的操作日志资源类型枚举。
 *
 * <p>**为什么用枚举而不是 {@code Map<String,String>} 字面量**：
 * <ul>
 *   <li>WLDOS 硬约定：除了 controller API 入参，其他入参/返回值/变量"尽量不用 Map"，
 *       密集 {@code m.put(...)} 对维护者不友好；</li>
 *   <li>枚举天然类型安全 + 可读：每个值带 Javadoc 说明语义；
 *       新增/淘汰 type 只需修改本枚举，{@link #supportedCodes()} / {@link #dict()}
 *       自动同步无需改 resolver；</li>
 *   <li>{@code @OpLog(resourceType = PlatformResourceType.DOMAIN.getCode())}
 *       这种写法在跨模块调用时也比裸字符串更安全。</li>
 * </ul>
 *
 * <p>命名约定（与 02-OpLog-API覆盖清单 一致）：{@code code} 全小写英文 + 单数 + 下划线，
 * 与审计表 {@code wo_op_log.resource_type} 列严格一致。
 *
 * @author Yuanxi Universe
 * @date 2026/05/05
 * @version 1.0
 */
public enum PlatformResourceType {

	/* ----------------------------------------------------------------------
	 * 命名规则（与 OpLogAspect#resolveResourceTypeFromGeneric 严格对齐）：
	 *   1) code = 实体类去 Wo/K 前缀后的 snake_case 单数形式；
	 *   2) 例：WoUser → user, WoCompany → company, WoCalendarHoliday → calendar_holiday；
	 *   3) 这样**任何继承 EntityController<S, E> 的子类**触发基类 CRUD 的 @OpLog 时，
	 *      自动推断的 resourceType 一定能在枚举里找到，**永远不会出现灰色英文 fallback**。
	 *   4) 同一资源的"手写 @OpLog(resourceType="xxx")" 必须用相同 code，
	 *      不能再出现既有 com 又有 company 这种"同一资源两个 type code"的审计完整性事故。
	 * ---------------------------------------------------------------------- */

	/** 应用实体（{@code wo_app}），租户已订阅的功能集合。 */
	APP("app", "应用"),

	/** 架构实体（{@code wo_architecture}），SaaS 架构层级元数据。 */
	ARCHITECTURE("architecture", "架构"),

	/** 用户实体（{@code wo_user}），CRUD/授权/重置密码等。 */
	USER("user", "用户"),

	/** 角色实体（{@code wo_role}），含角色与组织/资源的授权关系变更。 */
	ROLE("role", "角色"),

	/** 组织实体（{@code wo_org}），单位/部门维度。 */
	ORG("org", "组织"),

	/** 租户实体（{@code wo_company}），多租户体系顶层；code 跟随实体类名而非业务习用的 com_id。 */
	COMPANY("company", "租户"),

	/** 站点/域名实体（{@code wo_domain}），多域多端口部署单位。 */
	DOMAIN("domain", "域名"),

	/** API 资源/菜单实体（{@code wo_resource}），权限挂载点。 */
	RESOURCE("resource", "API资源"),

	/** 区域/地区实体（{@code wo_region}），收件地址等场景使用。 */
	REGION("region", "区域"),

	/**
	 * 分类（{@code k_terms} 的子集，{@code term_type=category}）。
	 *
	 * <p>与 {@link #TAG} / {@link #TERM} 同源一表，按业务语义区分注解时择一使用。
	 */
	CATEGORY("category", "分类"),

	/** 标签（{@code k_terms} 的子集，{@code term_type=tag}）。 */
	TAG("tag", "标签"),

	/** 通用分类项（{@code k_terms} 的全集兜底）。 */
	TERM("term", "分类项"),

	/** 分类类型元数据（{@code k_term_type}），定义有哪些 category/tag 维度。 */
	TERM_TYPE("term_type", "分类类型"),

	/** 节假日实体（{@code wo_calendar_holiday}）；code 跟随实体类名 calendar_holiday 而非业务昵称 holiday。 */
	CALENDAR_HOLIDAY("calendar_holiday", "节假日"),

	/**
	 * 系统配置项（{@code wo_options}）——批量配置语义。
	 *
	 * <p>注意：本类型在审计中**没有 ID → 名称的映射**——一次 {@code POST /system/options/config}
	 * 通常一并改 N 个 key（key 列表已写到 {@code wo_op_log.params_summary} / {@code wo_sys_log.metadata}），
	 * 不存在"被改的那条 wo_options 行"这个单数实体。所以
	 * {@link PlatformOpLogContextResolver#lookupByType} 对它返回空 map 即可，
	 * 前端列表里"资源"列正常显示为 {@code -}，但"资源类型"列展示中文 {@code 系统选项}。
	 */
	OPTION("option", "系统选项"),

	/* ----------------------------- 个人中心子模块 -----------------------------
	 * 这一组 resourceId 都是当前用户 id，物理上回 wo_user 同一行，
	 * 但 resourceType 区分不同的"配置切面"，便于审计按子模块筛选。
	 * --------------------------------------------------------------------- */

	/** 个人资料（昵称/头像/简介），对应 {@code POST /user/conf}。 */
	USER_PROFILE("user_profile", "个人资料"),

	/** 个人标签 JSON，对应 {@code POST /user/conf/tags}。 */
	USER_TAGS("user_tags", "个人标签"),

	/** 账户安全（含密码、二次验证），对应 {@code POST /user/conf/sec}。 */
	USER_SECURITY("user_security", "账户安全"),

	/** 账户绑定（手机/邮箱/第三方），对应 {@code POST /user/conf/bind}。 */
	USER_BIND("user_bind", "账户绑定"),

	/** 通知设置，对应 {@code POST /user/conf/notice}。 */
	USER_NOTICE("user_notice", "通知设置");

	private final String code;

	private final String label;

	PlatformResourceType(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

	/** 所有 code 集合（不可变），供 {@code IOpLogContextResolver#supportedResourceTypes()} 直接返回。 */
	public static Set<String> supportedCodes() {
		return SUPPORTED_CODES;
	}

	/** 字典项列表（不可变），供 {@code IOpLogContextResolver#resourceTypeLabels()} 直接返回。 */
	public static List<ResourceTypeDict> dict() {
		return DICT;
	}

	/**
	 * 按 {@code code} 反查枚举，未登记返回 {@code null}（resolver 会忽略不属于本模块的类型）。
	 *
	 * <p>{@code O(1)}：内部用 {@code HashMap} 索引，避免 {@code valueOf} 抛异常做流控的性能损耗。
	 */
	public static PlatformResourceType ofCode(String code) {
		return code == null ? null : CODE_INDEX.get(code);
	}

	private static final Set<String> SUPPORTED_CODES;
	private static final List<ResourceTypeDict> DICT;
	private static final Map<String, PlatformResourceType> CODE_INDEX;
	static {
		PlatformResourceType[] all = values();
		Set<String> codes = new HashSet<>(all.length * 2);
		List<ResourceTypeDict> dict = new ArrayList<>(all.length);
		Map<String, PlatformResourceType> idx = new HashMap<>(all.length * 2);
		for (PlatformResourceType t : all) {
			codes.add(t.code);
			dict.add(new ResourceTypeDict(t.code, t.label));
			idx.put(t.code, t);
		}
		SUPPORTED_CODES = Collections.unmodifiableSet(codes);
		DICT = Collections.unmodifiableList(dict);
		CODE_INDEX = Collections.unmodifiableMap(idx);
	}
}
