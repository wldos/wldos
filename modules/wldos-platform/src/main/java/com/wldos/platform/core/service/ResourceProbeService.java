/*
 * Copyright (c) 2020 yuanxiyuzhou. All rights reserved.
 * Created by Yuanxi Universe (306991142@qq.com)
 * Licensed under the Apache License, Version 2.0 or a commercial license.
 * For Apache License Version 2.0 see License in the project root for license information.
 * For commercial licenses see term.md or contact 306991142@qq.com
 */

package com.wldos.platform.core.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import com.wldos.gateway.GateWayHelper;
import com.wldos.platform.core.dao.AppDao;
import com.wldos.platform.core.entity.WoApp;
import io.github.wldos.common.utils.ObjectUtils;
import io.github.wldos.platform.support.resource.enums.MenuNodeKindEnum;
import io.github.wldos.platform.support.resource.enums.ResourceEnum;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 资源（{@code wo_resource}）配置的真值校验。
 *
 * <p>承担两件事：
 * <ol>
 *   <li><b>语法/形态级</b>：与前端 {@code resourcePathValidator.js} 等价的规则
 *     （禁 {@code ${...}}、空格、变量语法、API 类必须 {@code /} 起始等），
 *     防止前端被绕过的非法配置直接落库。</li>
 *   <li><b>真值级（核心）</b>：把 {@code (resourcePath, requestMethod)}
 *     反向比对运行时 Spring 的 {@link RequestMappingHandlerMapping}（含插件子上下文），
 *     若没有任何端点匹配 → 该资源永远 0 命中，鉴权要么放行（strict-mode OFF）
 *     要么 403（strict-mode ON），都属于「配置错误」。</li>
 * </ol>
 *
 * <p>仅对 <b>API 类资源</b>（{@code *_button}）执行真值匹配；
 * 菜单/外链/静态资源（{@code *_menu}、{@code http(s)://...} 等）不参与鉴权路由匹配，
 * 只做语法层校验。
 *
 * @author Yuanxi Universe
 * @since 2026/05/07
 */
@Slf4j
@Service
public class ResourceProbeService {

	private static final Set<String> METHOD_ENUM = new HashSet<>(Arrays.asList(
			"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"));

	/** API 类资源（参与鉴权路由匹配） */
	private static final Set<String> API_TYPES = ResourceEnum.buttonTypes();

	private static final AntPathMatcher MATCHER = new AntPathMatcher();

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Lazy
	private AppDao appDao;

	private volatile RequestMappingHandlerMapping mainHandlerMapping;

	@PostConstruct
	private void init() {
		try {
			this.mainHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
		}
		catch (Exception e) {
			log.warn("[ResourceProbe] 主应用 RequestMappingHandlerMapping 不可用：{}", e.getMessage());
		}
	}

	/**
	 * 校验单条资源配置；通过返回 null，否则返回提示信息。
	 *
	 * <p>调用方可决定 <b>严格模式</b>（直接抛 {@code BaseException} 拒绝保存）
	 * 还是 <b>宽松模式</b>（保存成功 + 警告回写）。
	 *
	 * @param resourceType  资源类型（{@code menu/admin_menu/admin_button/...}）
	 * @param resourcePath  资源路径（AntPath 模板或外链 URL）
	 * @param requestMethod 请求方法（API 类必填）
	 * @return null 表示通过；非 null 为提示文案
	 */
	public String validate(String resourceType, String resourcePath, String requestMethod) {
		return this.validate(resourceType, resourcePath, requestMethod, null);
	}

	public String validate(String resourceType, String resourcePath, String requestMethod, String menuNodeKind) {
		return this.validate(resourceType, resourcePath, requestMethod, menuNodeKind, null);
	}

	/**
	 * 附带 appId 时，额外校验 API 类资源路径首段与其所属 {@code wo_app.app_code} 是否一致
	 * （如 {@code /space/book} 挂在 {@code book} app 下却写成 {@code space} 首段）。
	 * 不一致时返回 {@code "提示："} 前缀的弱提示，不阻断保存——鉴权侧已有
	 * {@code AuthOpener#resolveAuthAppCode} 域挂载反查兜底，但首段与归属 app 一致仍是推荐配置。
	 */
	public String validate(String resourceType, String resourcePath, String requestMethod, String menuNodeKind,
			Long appId) {
		if (MenuNodeKindEnum.isNavDir(menuNodeKind)) {
			return null;
		}
		if ("other".equalsIgnoreCase(resourceType)) {
			if (ObjectUtils.isBlank(resourcePath)) {
				return "模块目录资源路径不能为空";
			}
			return null;
		}
		if (ObjectUtils.isBlank(resourcePath)) {
			return "资源路径不能为空";
		}
		String path = resourcePath.trim();

		// 通用语法（与前端等价）
		if (path.contains("${")) {
			return "资源路径疑似使用了 JS 模板字符串写法 ${...}，Spring AntPath 应使用 {变量名}，例如 /admin/agreement/{id}/active";
		}
		if (path.matches(".*\\s.*")) {
			return "资源路径不能含空格或换行";
		}

		boolean api = isApiType(resourceType);
		boolean abs = isAbsoluteUrl(path);
		boolean proRel = path.startsWith("//");

		if (!api) {
			// 菜单/外链/静态：允许 / 开头 或 绝对/协议相对 URL
			if (path.startsWith("/") || abs || proRel) {
				return null;
			}
			return "请填写以 / 开头的相对路径，或合法的 http(s):// URL";
		}

		// API 类：禁外链 + 必须 / 开头 + 不允许 //
		if (abs || proRel) {
			return "API 资源不可配置为外链 URL，请填写以 / 开头的相对路径";
		}
		if (!path.startsWith("/")) {
			return "API 资源路径必须以 / 开头";
		}
		if (path.contains("//")) {
			return "路径中不能含连续斜杠 //";
		}

		// AntPath 是否可被编译
		try {
			MATCHER.match(path, "/__probe__");
		}
		catch (IllegalArgumentException e) {
			return "AntPath 编译失败：" + e.getMessage();
		}

		// 请求方法
		if (ObjectUtils.isBlank(requestMethod)) {
			return "API 资源必须选择请求方法";
		}
		if (!METHOD_ENUM.contains(requestMethod)) {
			return "请求方法仅允许 GET/POST/PUT/DELETE/PATCH/HEAD/OPTIONS，全大写";
		}

		// 真值匹配：扫描所有 RequestMappingHandlerMapping，看是否存在 (path, method) 对应的 Controller 端点
		String matched = matchAgainstSpringEndpoints(path, requestMethod);
		if (matched == null) {
			return "未在系统中找到任何 Controller 端点匹配 (" + requestMethod + " " + path
					+ ")，该资源永远不会被鉴权命中。请核对：(1) Controller 是否存在并已加载；"
					+ "(2) 插件模块是否启用；(3) 请求方法与后端 @PostMapping/@GetMapping 是否一致。";
		}
		if (!matched.equals(path)) {
			return "提示：与系统端点 '" + matched + "' 等价；建议直接使用该模板，便于后续维护";
		}

		String appCodeHint = this.checkAppCodeConsistency(path, appId);
		if (appCodeHint != null) {
			return appCodeHint;
		}
		return null;
	}

	/**
	 * API 类资源路径首段与所属 {@code wo_app.app_code} 不一致时的弱提示；appId 未提供或首段无法解析时跳过。
	 */
	private String checkAppCodeConsistency(String path, Long appId) {
		if (appId == null || appId <= 0) {
			return null;
		}
		String pathAppCode = GateWayHelper.appCode(path);
		if (ObjectUtils.isBlank(pathAppCode) || "/".equals(pathAppCode)) {
			return null;
		}
		WoApp app = this.appDao.findById(appId).orElse(null);
		if (app == null || ObjectUtils.isBlank(app.getAppCode())) {
			return null;
		}
		if (pathAppCode.equalsIgnoreCase(app.getAppCode())) {
			return null;
		}
		return "提示：路径首段 " + pathAppCode + " 与所属应用编码 " + app.getAppCode() + " 不一致；"
				+ "鉴权依赖运行时按域挂载资源反查兜底，建议首段与所属应用一致或确认该差异符合预期";
	}

	/**
	 * 按 path 解析可命中 Spring 端点的 HTTP 方法；preferred 优先，否则依次尝试常见方法。
	 */
	public String resolveHttpMethod(String path, String preferredMethod) {
		if (ObjectUtils.isBlank(path)) {
			return null;
		}
		String normalizedPath = path.trim();
		if (!ObjectUtils.isBlank(preferredMethod)) {
			String preferred = preferredMethod.trim().toUpperCase();
			if (METHOD_ENUM.contains(preferred) && matchAgainstSpringEndpoints(normalizedPath, preferred) != null) {
				return preferred;
			}
		}
		for (String method : Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH")) {
			if (method.equalsIgnoreCase(preferredMethod)) {
				continue;
			}
			if (matchAgainstSpringEndpoints(normalizedPath, method) != null) {
				return method;
			}
		}
		return null;
	}

	/** 仅做语法校验（不做真值匹配），用于离线脚本/批量校验场景 */
	public String validateSyntax(String resourceType, String resourcePath, String requestMethod) {
		return this.validateSyntax(resourceType, resourcePath, requestMethod, null);
	}

	public String validateSyntax(String resourceType, String resourcePath, String requestMethod, String menuNodeKind) {
		if (MenuNodeKindEnum.isNavDir(menuNodeKind)) {
			return null;
		}
		if (ObjectUtils.isBlank(resourcePath)) return "资源路径不能为空";
		String path = resourcePath.trim();
		if (path.contains("${")) return "禁止 ${...}（JS 模板写法）";
		if (path.matches(".*\\s.*")) return "资源路径不能含空格或换行";

		boolean api = isApiType(resourceType);
		if (!api) {
			return path.startsWith("/") || isAbsoluteUrl(path) || path.startsWith("//")
					? null : "请填写以 / 开头的相对路径，或合法的 http(s):// URL";
		}
		if (isAbsoluteUrl(path) || path.startsWith("//")) return "API 资源不可配置为外链 URL";
		if (!path.startsWith("/")) return "API 资源路径必须以 / 开头";
		if (ObjectUtils.isBlank(requestMethod) || !METHOD_ENUM.contains(requestMethod)) {
			return "API 资源请求方法必填且需大写枚举 GET/POST/PUT/DELETE/PATCH";
		}
		return null;
	}

	/**
	 * 按 path + method 解析 Controller 方法上的 {@link ApiOperation#value()}。
	 */
	public String resolveApiOperationName(String path, String method) {
		if (ObjectUtils.isBlank(path) || ObjectUtils.isBlank(method)) {
			return null;
		}
		EndpointMatch match = this.findEndpointMatch(path.trim(), method.trim().toUpperCase());
		if (match == null || match.handlerMethod == null) {
			return null;
		}
		ApiOperation apiOperation = AnnotationUtils.findAnnotation(match.handlerMethod.getMethod(), ApiOperation.class);
		if (apiOperation != null && !ObjectUtils.isBlank(apiOperation.value())) {
			return apiOperation.value().trim();
		}
		return null;
	}

	/**
	 * 在所有 {@link RequestMappingHandlerMapping}（主应用 + 插件子上下文）里查找匹配。
	 *
	 * @return 命中的 pattern；返回 null 表示无命中
	 */
	private String matchAgainstSpringEndpoints(String path, String method) {
		EndpointMatch match = this.findEndpointMatch(path, method);
		return match != null ? match.pattern : null;
	}

	private EndpointMatch findEndpointMatch(String path, String method) {
		RequestMethod rm;
		try {
			rm = RequestMethod.valueOf(method);
		}
		catch (IllegalArgumentException e) {
			return null;
		}

		Map<String, RequestMappingHandlerMapping> beans =
				applicationContext.getBeansOfType(RequestMappingHandlerMapping.class);

		String probeUri = buildProbeUri(path);
		EndpointMatch fuzzyMatch = null;

		for (RequestMappingHandlerMapping mapping : beans.values()) {
			Map<RequestMappingInfo, HandlerMethod> all;
			try {
				all = mapping.getHandlerMethods();
			}
			catch (Exception e) {
				continue;
			}
			for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : all.entrySet()) {
				RequestMappingInfo info = entry.getKey();
				if (!methodMatches(info, rm)) {
					continue;
				}
				Collection<String> patterns = patternsOf(info);
				if (patterns == null) {
					continue;
				}
				for (String p : patterns) {
					if (p == null) {
						continue;
					}
					if (p.equals(path)) {
						return new EndpointMatch(p, entry.getValue());
					}
					if (fuzzyMatch == null) {
						try {
							if (MATCHER.match(p, probeUri)) {
								fuzzyMatch = new EndpointMatch(p, entry.getValue());
							}
						}
						catch (IllegalArgumentException ignore) {
							// 个别 pattern 编译失败，跳过
						}
					}
				}
			}
		}
		return fuzzyMatch;
	}

	private static final class EndpointMatch {
		private final String pattern;
		private final HandlerMethod handlerMethod;

		private EndpointMatch(String pattern, HandlerMethod handlerMethod) {
			this.pattern = pattern;
			this.handlerMethod = handlerMethod;
		}
	}

	private static boolean methodMatches(RequestMappingInfo info, RequestMethod method) {
		RequestMethodsRequestCondition cond = info.getMethodsCondition();
		if (cond == null || cond.getMethods().isEmpty()) {
			return true; // 未声明方法即接受所有
		}
		return cond.getMethods().contains(method);
	}

	private static Collection<String> patternsOf(RequestMappingInfo info) {
		PatternsRequestCondition pc = info.getPatternsCondition();
		if (pc != null) {
			return pc.getPatterns();
		}
		// Spring 5.3+ 的 PathPatternsRequestCondition 走这里
		try {
			Object ppc = RequestMappingInfo.class.getMethod("getPathPatternsCondition").invoke(info);
			if (ppc != null) {
				Object patterns = ppc.getClass().getMethod("getPatternValues").invoke(ppc);
				if (patterns instanceof Collection) {
					@SuppressWarnings("unchecked")
					Collection<String> c = (Collection<String>) patterns;
					return c;
				}
			}
		}
		catch (ReflectiveOperationException ignore) {
			// 老 Spring 没有该方法，忽略
		}
		return null;
	}

	private static String buildProbeUri(String pattern) {
		// 把 {x} / {x:regex} 都替换为 1；** -> a/b；* -> a
		String probe = pattern.replaceAll("\\{[^/]+}", "1");
		probe = probe.replace("/**", "/a/b");
		probe = probe.replace("/*", "/a");
		return probe;
	}

	private static boolean isApiType(String type) {
		return type != null && API_TYPES.contains(type);
	}

	private static boolean isAbsoluteUrl(String v) {
		return v != null && v.matches("^[a-zA-Z][a-zA-Z\\d+\\-.]*://.*");
	}
}
